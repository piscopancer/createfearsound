package dev.piscopancer.createfearsound.common.registries;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.client.gui.AudioControllerMenu;
import dev.piscopancer.createfearsound.client.gui.CassetteMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MenuTypesRegistry {
  static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, CFS.MODID);
  public static final DeferredHolder<MenuType<?>, MenuType<CassetteMenu>> CASSETTE_MENU = REGISTRY.register(
      "cassette_menu",
      () -> new MenuType<>(CassetteMenu::new, FeatureFlags.DEFAULT_FLAGS));

  public static final DeferredHolder<MenuType<?>, MenuType<AudioControllerMenu>> AUDIO_CONTROLLER_MENU = REGISTRY
      .register(
          "audio_controller_menu",
          () -> IMenuTypeExtension.create(AudioControllerMenu::new));
}
