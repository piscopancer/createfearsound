package dev.piscopancer.createfearsound.registries;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.gui.CassetteMenu;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MenuTypesRegistry {
  private MenuTypesRegistry() {
  }

  static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, CFS.MODID);
  public static final Supplier<MenuType<CassetteMenu>> CASSETTE_MENU = REGISTRY.register("cassette_menu",
      () -> new MenuType<>(CassetteMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
