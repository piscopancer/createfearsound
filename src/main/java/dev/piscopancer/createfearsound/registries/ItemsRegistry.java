package dev.piscopancer.createfearsound.registries;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.items.Cassette;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ItemsRegistry {
  private ItemsRegistry() {
  }

  static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(CFS.MODID);
  public static final DeferredItem<Item> CASSETTE = REGISTRY.registerItem("cassette", Cassette::new);
}