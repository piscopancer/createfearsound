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
  public CFSBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, CFS.MODID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
    vanillaCube(BlocksRegistry.AUDIO_CONTROLLER, "chiseled_stone_bricks");
    vanillaCube(BlocksRegistry.AUDIO_VOLUME, "polished_andesite");
    vanillaCube(BlocksRegistry.AUDIO_PLAY, "emerald_block");
    vanillaCube(BlocksRegistry.AUDIO_PAUSE, "gold_block");
    vanillaCube(BlocksRegistry.AUDIO_SPEAKER, "note_block");
  }

  private void vanillaCube(DeferredBlock<? extends Block> deferred, String vanillaTexture) {
    String name = deferred.getId().getPath();
    ResourceLocation texture = ResourceLocation.withDefaultNamespace("block/" + vanillaTexture);
    ModelFile model = models().cubeAll(name, texture);
    simpleBlock(deferred.get(), model);
  }

}
