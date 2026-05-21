package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.common.registries.BlockEntityTypesRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AudioPauseBlockEntity extends AudioPeripheralBlockEntity {
  public AudioPauseBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntityTypesRegistry.AUDIO_PAUSE.get(), pos, state);
  }

  @Override
  protected String getGoggleHeaderKey() {
    return "gui.goggles.audio_pause";
  }
}
