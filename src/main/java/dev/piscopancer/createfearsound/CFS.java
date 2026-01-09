package dev.piscopancer.createfearsound;

import com.mojang.logging.LogUtils;
import dev.piscopancer.createfearsound.registries.ModRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(CFS.MODID)
public class CFS {
  public static final String MODID = "createfearsound";
  public static final Logger LOGGER = LogUtils.getLogger();

  public CFS(IEventBus modEventBus, ModContainer modContainer) {
    ModRegistries.register(modEventBus);
    modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
  }
}
