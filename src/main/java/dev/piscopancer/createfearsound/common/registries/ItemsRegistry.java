package dev.piscopancer.createfearsound.common.registries;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.items.AudioPeripheralItem;
import dev.piscopancer.createfearsound.common.items.Cassette;
import dev.piscopancer.createfearsound.common.items.TapePiece;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ItemsRegistry {
  static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(CFS.MODID);
  public static final DeferredItem<Item> CASSETTE = REGISTRY.registerItem("cassette", Cassette::new);
  public static final DeferredItem<Item> TAPE_PIECE = REGISTRY.registerItem("tape_piece", TapePiece::new);

  public static final DeferredItem<BlockItem> AUDIO_CONTROLLER = REGISTRY.registerItem(
      "audio_controller",
      props -> new BlockItem(BlocksRegistry.AUDIO_CONTROLLER.get(), props));

  public static final DeferredItem<AudioPeripheralItem> AUDIO_VOLUME = REGISTRY.registerItem(
      "audio_volume",
      props -> new AudioPeripheralItem(BlocksRegistry.AUDIO_VOLUME.get(), props));

  public static final DeferredItem<AudioPeripheralItem> AUDIO_PLAY = REGISTRY.registerItem(
      "audio_play",
      props -> new AudioPeripheralItem(BlocksRegistry.AUDIO_PLAY.get(), props));

  public static final DeferredItem<AudioPeripheralItem> AUDIO_PAUSE = REGISTRY.registerItem(
      "audio_pause",
      props -> new AudioPeripheralItem(BlocksRegistry.AUDIO_PAUSE.get(), props));

  public static final DeferredItem<AudioPeripheralItem> AUDIO_SPEAKER = REGISTRY.registerItem(
      "audio_speaker",
      props -> new AudioPeripheralItem(BlocksRegistry.AUDIO_SPEAKER.get(), props));
}
