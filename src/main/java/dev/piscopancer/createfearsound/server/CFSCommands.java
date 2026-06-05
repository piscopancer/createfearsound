package dev.piscopancer.createfearsound.server;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.server.audio.AudioStorage;
import dev.piscopancer.createfearsound.server.audio.YtDlp;
import dev.piscopancer.createfearsound.server.payloads.AudioChunkPayload;
import dev.piscopancer.createfearsound.server.payloads.AudioPlayStartPayload;
import dev.piscopancer.createfearsound.server.payloads.StopAudioPayload;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = CFS.MODID)
public class CFSCommands {

  @SubscribeEvent
  static void onRegisterCommands(RegisterCommandsEvent event) {
    event.getDispatcher().register(
        Commands.literal("cfs")
            .requires(src -> src.hasPermission(2))
            .then(Commands.literal("audio")
                .then(Commands.literal("upload")
                    .then(Commands.argument("url", StringArgumentType.greedyString())
                        .executes(ctx -> upload(ctx, StringArgumentType.getString(ctx, "url")))))
                .then(Commands.literal("play")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .suggests(CFSCommands::suggestAudioIds)
                        .executes(ctx -> play(ctx, StringArgumentType.getString(ctx, "id"), false))
                        .then(Commands.literal("--block")
                            .executes(ctx -> play(ctx, StringArgumentType.getString(ctx, "id"), true)))))
                .then(Commands.literal("stop")
                    .executes(CFSCommands::stop))
                .then(Commands.literal("delete")
                    .then(Commands.argument("id", StringArgumentType.string())
                        .suggests(CFSCommands::suggestAudioIds)
                        .executes(ctx -> delete(ctx, StringArgumentType.getString(ctx, "id")))))
                .then(Commands.literal("list")
                    .executes(CFSCommands::list))
                .then(Commands.literal("check")
                    .executes(CFSCommands::check))));
  }

  private static CompletableFuture<Suggestions> suggestAudioIds(
      CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
    AudioStorage.all().forEach(e -> builder.suggest(e.name()));
    return builder.buildFuture();
  }

  // --- handlers ---

  private static int upload(CommandContext<CommandSourceStack> ctx, String url) {
    CommandSourceStack src = ctx.getSource();
    MinecraftServer server = src.getServer();

    if (!YtDlp.isAvailable()) {
      src.sendFailure(Component.literal("yt-dlp not found in PATH").withStyle(ChatFormatting.RED));
      return 0;
    }

    src.sendSuccess(() -> Component.literal("Downloading: ")
        .withStyle(ChatFormatting.GRAY)
        .append(Component.literal(url).withStyle(ChatFormatting.WHITE)), false);

    AudioStorage.download(url, src.getTextName()).whenComplete((entry, err) ->
        server.execute(() -> {
          if (err != null) {
            src.sendFailure(Component.literal("Download failed: " + err.getMessage())
                .withStyle(ChatFormatting.RED));
          } else {
            src.sendSuccess(() -> Component.literal("Saved ")
                .withStyle(ChatFormatting.GREEN)
                .append(Component.literal(entry.name()).withStyle(ChatFormatting.WHITE))
                .append(Component.literal("  [" + entry.id() + "]").withStyle(ChatFormatting.DARK_GRAY)), false);
          }
        }));

    return 1;
  }

  private static final int CHUNK_SIZE = 32768;

  private static int play(CommandContext<CommandSourceStack> ctx, String id, boolean block) {
    CommandSourceStack src = ctx.getSource();
    ServerPlayer player = src.getPlayer();
    if (player == null) {
      src.sendFailure(Component.literal("Must be run by a player").withStyle(ChatFormatting.RED));
      return 0;
    }
    var found = AudioStorage.find(id);
    if (found.isEmpty()) {
      src.sendFailure(Component.literal("Not found: " + id).withStyle(ChatFormatting.RED));
      return 0;
    }
    var entry = found.get();
    if (!entry.isCached()) {
      MinecraftServer server = src.getServer();
      src.sendSuccess(() -> Component.literal("Downloading ").withStyle(ChatFormatting.GRAY)
          .append(Component.literal(entry.name()).withStyle(ChatFormatting.WHITE))
          .append(Component.literal("...").withStyle(ChatFormatting.GRAY)), false);
      AudioStorage.redownload(entry.id()).whenComplete((redownloaded, err) ->
          server.execute(() -> {
            if (err != null) {
              src.sendFailure(Component.literal("Download failed: " + err.getMessage()).withStyle(ChatFormatting.RED));
              return;
            }
            sendAudio(src, player, redownloaded, !block);
          }));
      return 1;
    }
    return sendAudio(src, player, entry, !block);
  }

  private static int sendAudio(CommandSourceStack src, ServerPlayer player, AudioStorage.AudioEntry entry, boolean followsPlayer) {
    byte[] data;
    try {
      data = Files.readAllBytes(entry.audioFile());
    } catch (IOException e) {
      src.sendFailure(Component.literal("Failed to read audio: " + e.getMessage()).withStyle(ChatFormatting.RED));
      return 0;
    }
    int totalChunks = (int) Math.ceil((double) data.length / CHUNK_SIZE);
    var pos = src.getPosition();
    PacketDistributor.sendToPlayer(player, new AudioPlayStartPayload(entry.id(), entry.name(), totalChunks, followsPlayer, pos.x, pos.y, pos.z));
    for (int i = 0; i < totalChunks; i++) {
      int from = i * CHUNK_SIZE;
      byte[] chunk = Arrays.copyOfRange(data, from, Math.min(from + CHUNK_SIZE, data.length));
      PacketDistributor.sendToPlayer(player, new AudioChunkPayload(entry.id(), i, chunk));
    }
    AudioStorage.recordListen(entry.id());
    src.sendSuccess(() -> Component.literal("Playing ").withStyle(ChatFormatting.GREEN)
        .append(Component.literal(entry.name()).withStyle(ChatFormatting.WHITE)), false);
    return 1;
  }

  private static int stop(CommandContext<CommandSourceStack> ctx) {
    ServerPlayer player = ctx.getSource().getPlayer();
    if (player == null) return 0;
    PacketDistributor.sendToPlayer(player, new StopAudioPayload());
    ctx.getSource().sendSuccess(() -> Component.literal("Stopped").withStyle(ChatFormatting.GRAY), false);
    return 1;
  }

  private static int delete(CommandContext<CommandSourceStack> ctx, String id) {
    AudioStorage.find(id).ifPresentOrElse(
        entry -> {
          AudioStorage.delete(entry.id());
          ctx.getSource().sendSuccess(
              () -> Component.literal("Deleted ")
                  .withStyle(ChatFormatting.GREEN)
                  .append(Component.literal(entry.name()).withStyle(ChatFormatting.WHITE))
                  .append(Component.literal("  [" + entry.id() + "]").withStyle(ChatFormatting.DARK_GRAY)),
              false);
        },
        () -> ctx.getSource().sendFailure(
            Component.literal("Not found: " + id).withStyle(ChatFormatting.RED)));
    return 1;
  }

  private static int check(CommandContext<CommandSourceStack> ctx) {
    var bad = AudioStorage.allInvalid();
    if (bad.isEmpty()) {
      ctx.getSource().sendSuccess(
          () -> Component.literal("No invalid entries.").withStyle(ChatFormatting.GREEN), false);
      return 1;
    }
    ctx.getSource().sendSuccess(
        () -> Component.literal("=== CFS Invalid Entries (" + bad.size() + ") ===")
            .withStyle(ChatFormatting.RED), false);
    bad.forEach(e -> ctx.getSource().sendSuccess(
        () -> Component.literal("  [" + e.id() + "]").withStyle(ChatFormatting.DARK_GRAY)
            .append(Component.literal("  " + e.reason()).withStyle(ChatFormatting.YELLOW)),
        false));
    return 1;
  }

  private static int list(CommandContext<CommandSourceStack> ctx) {
    var all = AudioStorage.all();
    CommandSourceStack src = ctx.getSource();
    if (all.isEmpty()) {
      src.sendSuccess(() -> Component.literal("No audio uploaded yet.").withStyle(ChatFormatting.GRAY), false);
      return 1;
    }
    src.sendSuccess(() -> Component.literal("=== CFS Audio (" + all.size() + ") ===").withStyle(ChatFormatting.GOLD), false);
    int[] i = {1};
    all.forEach(e -> {
      int n = i[0]++;
      boolean cached = e.isCached();
      src.sendSuccess(() -> Component.literal(n + ". ")
          .withStyle(ChatFormatting.GRAY)
          .append(Component.literal(e.name()).withStyle(ChatFormatting.WHITE))
          .append(Component.literal(cached ? "  [cached]" : "  [evicted]")
              .withStyle(cached ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY))
          .append(Component.literal("  [" + e.id() + "]").withStyle(ChatFormatting.DARK_GRAY)), false);
      src.sendSuccess(() -> field("Source").append(Component.literal(e.meta().source()).withStyle(ChatFormatting.WHITE)), false);
      src.sendSuccess(() -> field("Uploaded") .append(Component.literal(e.meta().uploadedAt().substring(0, 10)).withStyle(ChatFormatting.WHITE))
          .append(Component.literal(" by ").withStyle(ChatFormatting.DARK_GRAY))
          .append(Component.literal(e.meta().uploadedBy()).withStyle(ChatFormatting.WHITE)), false);
      if (e.oggInfo() != null) {
        src.sendSuccess(() -> field("Length") .append(Component.literal(e.oggInfo().formatDuration()).withStyle(ChatFormatting.WHITE)), false);
        src.sendSuccess(() -> field("Quality").append(Component.literal(e.oggInfo().formatBitrate()).withStyle(ChatFormatting.WHITE)), false);
      }
      src.sendSuccess(() -> field("Listened").append(Component.literal(e.meta().timesListened() + "x").withStyle(ChatFormatting.WHITE)), false);
      if (e.meta().lastListenedAt() != null) {
        src.sendSuccess(() -> field("Last").append(Component.literal(e.meta().lastListenedAt().substring(0, 10)).withStyle(ChatFormatting.WHITE)), false);
      }
    });
    return 1;
  }

  private static MutableComponent field(String key) {
    return Component.literal("  " + key + ": ").withStyle(ChatFormatting.DARK_GRAY);
  }
}
