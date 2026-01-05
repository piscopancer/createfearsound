package dev.piscopancer.createfearsound;

import javax.annotation.Nullable;

import dev.piscopancer.createfearsound.items.Cassette;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CreateFearSound.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods
// in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CreateFearSound.MODID, value = Dist.CLIENT)
public class CreateFearSoundClient {
  public CreateFearSoundClient(ModContainer container) {
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
          CreateFearSound.CASSETTE.get(),
          ResourceLocation.fromNamespaceAndPath(CreateFearSound.MODID, "color"),
          (stack, level, player, seed) -> {
            @Nullable
            var color = stack.get(ModDataComponents.COLOR_DATA_COMPONENT.get());
            return color == null ? 0 : switch (color) {
              case None -> 0;
              case Red -> 1;
              case Green -> 2;
            };
          });
    });
  }
}
