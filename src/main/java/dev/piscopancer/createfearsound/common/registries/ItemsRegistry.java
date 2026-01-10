package dev.piscopancer.createfearsound.common.registries;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.items.Cassette;
import dev.piscopancer.createfearsound.common.items.TapePiece;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ItemsRegistry {
  static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(CFS.MODID);
  public static final DeferredItem<Item> CASSETTE = REGISTRY.registerItem("cassette", Cassette::new);
  public static final DeferredItem<Item> TAPE_PIECE = REGISTRY.registerItem("tape_piece", TapePiece::new);
}