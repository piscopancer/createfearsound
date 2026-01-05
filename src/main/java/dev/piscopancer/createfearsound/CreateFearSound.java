package dev.piscopancer.createfearsound;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import dev.piscopancer.createfearsound.items.Cassette;
import dev.piscopancer.createfearsound.items.ModItemModelProvider;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(CreateFearSound.MODID)
public class CreateFearSound {
  public static final String MODID = "createfearsound";
  public static final Logger LOGGER = LogUtils.getLogger();
  public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
      .create(Registries.CREATIVE_MODE_TAB, MODID);
  // public static final DeferredBlock<Block> EXAMPLE_BLOCK =
  // BLOCKS.registerSimpleBlock("example_block",
  // BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
  // public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM =
  // ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);
  public static final DeferredItem<Item> CASSETTE = ITEMS.registerItem("cassette", Cassette::new);

  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_TAB = CREATIVE_MODE_TABS
      .register(MODID + "_tab", () -> CreativeModeTab.builder()
          .title(Component.translatable("itemGroup." + MODID)) // The language key for the title of your
          .withTabsBefore(CreativeModeTabs.COMBAT)
          .icon(() -> CASSETTE.get().getDefaultInstance())
          .displayItems((parameters, output) -> {
            output.accept(CASSETTE.get());
          }).build());

  // The constructor for the mod class is the first code that is run when your mod
  // is loaded.
  // FML will recognize some parameter types like IEventBus or ModContainer and
  // pass them in automatically.
  public CreateFearSound(IEventBus modEventBus, ModContainer modContainer) {
    CREATIVE_MODE_TABS.register(modEventBus);
    BLOCKS.register(modEventBus);
    ITEMS.register(modEventBus);
    ModDataComponents.DATA_COMPONENTS.register(modEventBus);
    // Register ourselves for server and other game events we are interested in.
    // Note that this is necessary if and only if we want *this* class
    // (CreateFearSound) to respond directly to events.
    // Do not add this line if there are no @SubscribeEvent-annotated functions in
    // this class, like onServerStarting() below.
    NeoForge.EVENT_BUS.register(this);
    modEventBus.addListener(this::gatherData);
    // modEventBus.addListener(this::addCreative);

    // Register our mod's ModConfigSpec so that FML can create and load the config
    // file for us
    modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
  }

  // Add the example block item to the building blocks tab
  // private void addCreative(BuildCreativeModeTabContentsEvent event) {
  // if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
  // }
  // }

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
