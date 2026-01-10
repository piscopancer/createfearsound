package dev.piscopancer.createfearsound.common.registries;

import dev.piscopancer.createfearsound.CFS;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CreativeModTabsRegistry {
  static final ArrayList<DeferredItem<Item>> CREATIVE_TAB_ITEMS = new ArrayList<>(
      List.of(
          ItemsRegistry.CASSETTE,
          ItemsRegistry.TAPE_PIECE));

  static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister
      .create(Registries.CREATIVE_MODE_TAB, CFS.MODID);
  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_TAB = REGISTRY
      .register(CFS.MODID + "_tab", () -> CreativeModeTab.builder()
          .title(Component.translatable("itemGroup." + CFS.MODID))
          .withTabsBefore(CreativeModeTabs.COMBAT)
          .icon(() -> ItemsRegistry.CASSETTE.get().getDefaultInstance())
          .displayItems((parameters, output) -> {
            CREATIVE_TAB_ITEMS.forEach(item -> output.accept(item.get()));
          }).build());
}
