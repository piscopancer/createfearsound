package dev.piscopancer.createfearsound.common.registries;

import net.neoforged.bus.api.IEventBus;

public class ModRegistries {
  public static void register(IEventBus modEventBus) {
    ItemsRegistry.REGISTRY.register(modEventBus);
    BlocksRegistry.REGISTRY.register(modEventBus);
    CreativeModTabsRegistry.REGISTRY.register(modEventBus);
    MenuTypesRegistry.REGISTRY.register(modEventBus);
    DataComponentsRegistry.REGISTRY.register(modEventBus);
    CreateSerializersRegistry.REGISTRY.register(modEventBus);
  }
}
