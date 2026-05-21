package dev.piscopancer.createfearsound.server;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.data.TrackData;
import dev.piscopancer.createfearsound.common.registries.DataComponentsRegistry;
import dev.piscopancer.createfearsound.common.registries.ItemsRegistry;
import dev.piscopancer.createfearsound.server.audio.AudioStorage;
import dev.piscopancer.createfearsound.server.audio.YtDlp;
import dev.piscopancer.createfearsound.server.payloads.TapeFindPayload;
import dev.piscopancer.createfearsound.server.payloads.TapeFindResultPayload;
import dev.piscopancer.createfearsound.server.payloads.TapeRecordPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class Payloads {
  @SubscribeEvent
  static void register(final RegisterPayloadHandlersEvent event) {
    final PayloadRegistrar r = event.registrar("1");

    r.playToServer(TapeFindPayload.TYPE, TapeFindPayload.STREAM_CODEC, (payload, context) ->
        context.enqueueWork(() -> {
          ServerPlayer player = (ServerPlayer) context.player();
          MinecraftServer server = player.getServer();
          String url = payload.url();

          AudioStorage.findByUrl(url).ifPresentOrElse(
              existing -> {
                int dur = existing.oggInfo() != null ? existing.oggInfo().durationSeconds() : 0;
                String autoTitle = existing.meta().autoTitle() != null ? existing.meta().autoTitle() : "";
                PacketDistributor.sendToPlayer(player, new TapeFindResultPayload(url, true, autoTitle, dur, ""));
              },
              () -> {
                if (!YtDlp.isAvailable()) {
                  PacketDistributor.sendToPlayer(player, new TapeFindResultPayload(url, false, "", 0, "yt-dlp not found in PATH"));
                  return;
                }
                AudioStorage.download(url, player.getGameProfile().getName()).whenComplete((entry, err) ->
                    server.execute(() -> {
                      if (err != null) {
                        CFS.LOGGER.error("[CFS] Find/download failed for {}: {}", url, err.getMessage());
                        PacketDistributor.sendToPlayer(player, new TapeFindResultPayload(url, false, "", 0, err.getMessage()));
                      } else {
                        int dur = entry.oggInfo() != null ? entry.oggInfo().durationSeconds() : 0;
                        String autoTitle = entry.meta().autoTitle() != null ? entry.meta().autoTitle() : "";
                        PacketDistributor.sendToPlayer(player, new TapeFindResultPayload(url, true, autoTitle, dur, ""));
                      }
                    }));
              });
        }));

    r.playToServer(TapeRecordPayload.TYPE, TapeRecordPayload.STREAM_CODEC, (payload, context) ->
        context.enqueueWork(() -> {
          ServerPlayer player = (ServerPlayer) context.player();
          String url = payload.url();

          ItemStack tape = findTapeInHands(player);
          if (tape == null) return;

          int duration = AudioStorage.findByUrl(url)
              .map(e -> e.oggInfo() != null ? e.oggInfo().durationSeconds() : 0)
              .orElse(0);
          tape.set(DataComponentsRegistry.TAPE_PIECE.get(), new TrackData(url, payload.title(), payload.author(), duration));

          for (InteractionHand hand : InteractionHand.values()) {
            ItemStack handItem = player.getItemInHand(hand);
            if (handItem.is(Items.FLINT)) {
              handItem.shrink(1);
              break;
            }
          }

          player.inventoryMenu.broadcastChanges();
        }));
  }

  private static ItemStack findTapeInHands(ServerPlayer player) {
    for (ItemStack s : new ItemStack[]{player.getMainHandItem(), player.getOffhandItem()}) {
      if (s.is(ItemsRegistry.TAPE_PIECE.get())) return s;
    }
    return null;
  }
}
