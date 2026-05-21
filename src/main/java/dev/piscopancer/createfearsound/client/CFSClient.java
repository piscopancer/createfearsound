package dev.piscopancer.createfearsound.client;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.client.gui.AudioControllerScreen;
import dev.piscopancer.createfearsound.client.gui.CassetteScreen;
import dev.piscopancer.createfearsound.client.gui.TapePieceScreen;
import dev.piscopancer.createfearsound.common.registries.DataComponentsRegistry;
import dev.piscopancer.createfearsound.common.registries.ItemsRegistry;
import dev.piscopancer.createfearsound.common.registries.MenuTypesRegistry;
import dev.piscopancer.createfearsound.client.audio.ClientAudioReceiver;
import dev.piscopancer.createfearsound.server.payloads.AudioChunkPayload;
import dev.piscopancer.createfearsound.server.payloads.AudioPlayStartPayload;
import dev.piscopancer.createfearsound.server.payloads.SetPendingLinkPayload;
import dev.piscopancer.createfearsound.server.payloads.StopAudioPayload;
import dev.piscopancer.createfearsound.server.payloads.TapeFindResultPayload;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CFS.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods
// in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CFS.MODID, value = Dist.CLIENT)
public class CFSClient {
  public CFSClient(ModContainer container) {
    // Allows NeoForge to create a config screen for this mod's configs.
    // The config screen is accessed by going to the Mods screen > clicking on your
    // mod > clicking on config.
    // Do not forget to add translations for your config options to the en_us.json
    // file.
    container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
  }

  @SubscribeEvent
  static void onClientSetup(FMLClientSetupEvent event) {
    // CreateFearSound.LOGGER.info("HELLO FROM CLIENT SETUP");
    // CreateFearSound.LOGGER.info("MINECRAFT NAME >> {}",
    // Minecraft.getInstance().getUser().getName());
    event.enqueueWork(() -> {
      ItemProperties.register(
          ItemsRegistry.CASSETTE.get(),
          ResourceLocation.fromNamespaceAndPath(CFS.MODID, "color"),
          (stack, level, player, seed) -> {
            var color = stack.get(DataComponentsRegistry.COLOR_DATA_COMPONENT.get());
            return color == null ? 0 : switch (color) {
              case None -> 0;
              case Red -> 1;
              case Green -> 2;
            };
          });
    });
  }

  @SubscribeEvent
  public static void registerScreens(RegisterMenuScreensEvent event) {
    event.register(MenuTypesRegistry.CASSETTE_MENU.get(), CassetteScreen::new);
    event.register(MenuTypesRegistry.AUDIO_CONTROLLER_MENU.get(), AudioControllerScreen::new);
  }

  @SubscribeEvent
  public static void registerPayloads(RegisterPayloadHandlersEvent event) {
    var r = event.registrar("1");
    r.playToClient(SetPendingLinkPayload.TYPE, SetPendingLinkPayload.STREAM_CODEC,
        (payload, context) -> context.enqueueWork(
            () -> CFSClientEvents.pendingControllerPos = payload.pos().orElse(null)));
    r.playToClient(AudioPlayStartPayload.TYPE, AudioPlayStartPayload.STREAM_CODEC,
        (payload, context) -> context.enqueueWork(
            () -> ClientAudioReceiver.onPlayStart(payload.trackId(), payload.name(), payload.totalChunks())));
    r.playToClient(AudioChunkPayload.TYPE, AudioChunkPayload.STREAM_CODEC,
        (payload, context) -> context.enqueueWork(
            () -> ClientAudioReceiver.onChunk(payload.trackId(), payload.chunkIndex(), payload.data())));
    r.playToClient(StopAudioPayload.TYPE, StopAudioPayload.STREAM_CODEC,
        (payload, context) -> context.enqueueWork(ClientAudioReceiver::stop));
    r.playToClient(TapeFindResultPayload.TYPE, TapeFindResultPayload.STREAM_CODEC,
        (payload, context) -> context.enqueueWork(() -> {
          var mc = net.minecraft.client.Minecraft.getInstance();
          if (mc.screen instanceof TapePieceScreen s) s.onFindResult(payload);
        }));
  }
}
