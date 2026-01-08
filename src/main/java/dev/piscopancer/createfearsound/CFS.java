package dev.piscopancer.createfearsound;

import com.mojang.logging.LogUtils;
import dev.piscopancer.createfearsound.items.ModItemModelProvider;
import dev.piscopancer.createfearsound.registries.ModRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(CFS.MODID)
public class CFS {
  public static final String MODID = "createfearsound";
  public static final Logger LOGGER = LogUtils.getLogger();

  public CFS(IEventBus modEventBus, ModContainer modContainer) {
    ModRegistries.register(modEventBus);
    ModDataComponents.DATA_COMPONENTS.register(modEventBus);
    NeoForge.EVENT_BUS.register(this);
    modEventBus.addListener(this::gatherData);
    modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
  }

  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
    LOGGER.info("HELLO from server starting");
  }

  public void gatherData(GatherDataEvent event) {
    var generator = event.getGenerator();
    var output = generator.getPackOutput();
    var existingFileHelper = event.getExistingFileHelper();

    generator.addProvider(
        event.includeClient(),
        new ModItemModelProvider(output, existingFileHelper));
  }
}
