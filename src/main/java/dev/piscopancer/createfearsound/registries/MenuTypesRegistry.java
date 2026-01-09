package dev.piscopancer.createfearsound.registries;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.gui.TapePieceMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MenuTypesRegistry {
  static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, CFS.MODID);
  public static final DeferredHolder<MenuType<?>, MenuType<TapePieceMenu>> TAPE_PIECE_MENU = REGISTRY.register(
      "tape_piece_menu",
      () -> new MenuType<>(TapePieceMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
