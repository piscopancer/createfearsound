package dev.piscopancer.createfearsound.registries;

import dev.piscopancer.createfearsound.CFS;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CreativeModTabsRegistry {
  private CreativeModTabsRegistry() {
  }

  static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister
      .create(Registries.CREATIVE_MODE_TAB, CFS.MODID);
  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_TAB = REGISTRY
      .register(CFS.MODID + "_tab", () -> CreativeModeTab.builder()
          .title(Component.translatable("itemGroup." + CFS.MODID))
          .withTabsBefore(CreativeModeTabs.COMBAT)
          .icon(() -> ItemsRegistry.CASSETTE.get().getDefaultInstance())
          .displayItems((parameters, output) -> {
            output.accept(ItemsRegistry.CASSETTE.get());
          }).build());
}
