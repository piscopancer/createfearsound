package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.common.registries.BlockEntityTypesRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AudioSpeakerBlockEntity extends AudioPeripheralBlockEntity {
  public AudioSpeakerBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntityTypesRegistry.AUDIO_SPEAKER.get(), pos, state);
  }

  @Override
  protected String getGoggleHeaderKey() {
    return "gui.goggles.audio_speaker";
  }
}
