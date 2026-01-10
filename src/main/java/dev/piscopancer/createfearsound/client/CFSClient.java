package dev.piscopancer.createfearsound.client;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.client.gui.CassetteScreen;
import dev.piscopancer.createfearsound.client.gui.TapePieceScreen;
import dev.piscopancer.createfearsound.registries.DataComponentsRegistry;
import dev.piscopancer.createfearsound.registries.ItemsRegistry;
import dev.piscopancer.createfearsound.registries.MenuTypesRegistry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CFS.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods
// in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CFS.MODID, value = Dist.CLIENT)
public class CFSClient {
  public CFSClient(ModContainer container) {
    // Allows NeoForge to create a config screen for this mod's configs.
    // The config screen is accessed by going to the Mods screen > clicking on your
    // mod > clicking on config.
    // Do not forget to add translations for your config options to the en_us.json
    // file.
    container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
  }

  @SubscribeEvent
  static void onClientSetup(FMLClientSetupEvent event) {
    // CreateFearSound.LOGGER.info("HELLO FROM CLIENT SETUP");
    // CreateFearSound.LOGGER.info("MINECRAFT NAME >> {}",
    // Minecraft.getInstance().getUser().getName());
    event.enqueueWork(() -> {
      ItemProperties.register(
          ItemsRegistry.CASSETTE.get(),
          ResourceLocation.fromNamespaceAndPath(CFS.MODID, "color"),
          (stack, level, player, seed) -> {
            var color = stack.get(DataComponentsRegistry.COLOR_DATA_COMPONENT.get());
            return color == null ? 0 : switch (color) {
              case None -> 0;
              case Red -> 1;
              case Green -> 2;
            };
          });
    });
  }

  @SubscribeEvent
  public static void registerScreens(RegisterMenuScreensEvent event) {
    event.register(MenuTypesRegistry.CASSETTE_MENU.get(), CassetteScreen::new);
  }
}
