package dev.piscopancer.createfearsound.common.registries;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.items.Cassette;
import dev.piscopancer.createfearsound.common.items.TapePiece;
import dev.piscopancer.createfearsound.common.items.TapePlayerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ItemsRegistry {
  static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(CFS.MODID);
  public static final DeferredItem<Item> CASSETTE = REGISTRY.registerItem("cassette", Cassette::new);
  public static final DeferredItem<Item> TAPE_PIECE = REGISTRY.registerItem("tape_piece", TapePiece::new);

  public static final DeferredItem<TapePlayerItem> TAPE_PLAYER = REGISTRY.registerItem(
      "tape_player",
      props -> new TapePlayerItem(BlocksRegistry.TAPE_PLAYER.get(), props));

  public static final DeferredItem<BlockItem> PLAY_BLOCK = REGISTRY.registerItem(
      "play_block",
      props -> new BlockItem(BlocksRegistry.PLAY_BLOCK.get(), props));

  public static final DeferredItem<BlockItem> PAUSE_BLOCK = REGISTRY.registerItem(
      "pause_block",
      props -> new BlockItem(BlocksRegistry.PAUSE_BLOCK.get(), props));

  public static final DeferredItem<BlockItem> VOLUME_BLOCK = REGISTRY.registerItem(
      "volume_block",
      props -> new BlockItem(BlocksRegistry.VOLUME_BLOCK.get(), props));
}
