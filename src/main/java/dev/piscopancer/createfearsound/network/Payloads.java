package dev.piscopancer.createfearsound.network;

import dev.piscopancer.createfearsound.common.data.TapePieceData;
import dev.piscopancer.createfearsound.network.payloads.TapePiecePayload;
import dev.piscopancer.createfearsound.registries.DataComponentsRegistry;
import dev.piscopancer.createfearsound.registries.ItemsRegistry;
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
        TapePiecePayload.TYPE,
        TapePiecePayload.STREAM_CODEC,
        (payload, context) -> {
          context.enqueueWork(() -> {
            var player = (ServerPlayer) context.player();
            var stack = player.getItemInHand(player.getUsedItemHand());
            if (stack.is(ItemsRegistry.TAPE_PIECE.get())) {
              var data = new TapePieceData(payload.title(), payload.author(), payload.url());
              stack.set(DataComponentsRegistry.TAPE_PIECE.get(), data);
              player.displayClientMessage(Component.literal("Данные кассеты обновлены!"), true);
            }
          });
        });
  }
}
