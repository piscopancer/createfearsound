package dev.piscopancer.createfearsound.datagen;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.registries.BlocksRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

class CFSBlockStateProvider extends BlockStateProvider {
  private static final ResourceLocation PLACEHOLDER = ResourceLocation.withDefaultNamespace("block/stone");

  public CFSBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, CFS.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    placeholderCube(BlocksRegistry.TAPE_PLAYER);
    placeholderCube(BlocksRegistry.PLAY_BLOCK);
    placeholderCube(BlocksRegistry.PAUSE_BLOCK);
    placeholderCube(BlocksRegistry.VOLUME_BLOCK);
  }

  private void placeholderCube(DeferredBlock<? extends Block> deferred) {
    String name = deferred.getId().getPath();
    ModelFile model = models()
        .cubeAll(name, PLACEHOLDER);
    simpleBlock(deferred.get(), model);
  }
}
