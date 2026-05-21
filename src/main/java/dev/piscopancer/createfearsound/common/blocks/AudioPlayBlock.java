package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.common.data.AudioLink;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AudioPlayBlock extends AudioPeripheralBlock implements EntityBlock {
  public AudioPlayBlock(Properties properties) {
    super(properties);
  }

  @Override
  public AudioLink.LinkType getLinkType() {
    return AudioLink.LinkType.Play;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new AudioPlayBlockEntity(pos, state);
  }
}
