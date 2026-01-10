package dev.piscopancer.createfearsound.server;

import dev.piscopancer.createfearsound.common.data.TrackData;
import dev.piscopancer.createfearsound.common.registries.DataComponentsRegistry;
import dev.piscopancer.createfearsound.common.registries.ItemsRegistry;
import dev.piscopancer.createfearsound.server.payloads.TrackPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class Payloads {
  @SubscribeEvent
  static void register(final RegisterPayloadHandlersEvent event) {
    final PayloadRegistrar registrar = event.registrar("1");
    registrar.playToServer(
        TrackPayload.TYPE,
        TrackPayload.STREAM_CODEC,
        (payload, context) -> {
          context.enqueueWork(() -> {
            var player = (ServerPlayer) context.player();
            var stack = player.getItemInHand(player.getUsedItemHand());
            if (stack.is(ItemsRegistry.TAPE_PIECE.get())) {
              var data = TrackData.builder()
                  .title(payload.title())
                  .author(payload.author())
                  .url(payload.url())
                  .duration(payload.duration())
                  .build();
              stack.set(DataComponentsRegistry.TAPE_PIECE.get(), data);
              player.displayClientMessage(Component.literal("Данные кассеты обновлены!"), true);
            }
          });
        });
  }
}
