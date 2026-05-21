package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.common.registries.BlockEntityTypesRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AudioPlayBlockEntity extends AudioPeripheralBlockEntity {
  public AudioPlayBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntityTypesRegistry.AUDIO_PLAY.get(), pos, state);
  }

  @Override
  protected String getGoggleHeaderKey() {
    return "gui.goggles.audio_play";
  }
}
