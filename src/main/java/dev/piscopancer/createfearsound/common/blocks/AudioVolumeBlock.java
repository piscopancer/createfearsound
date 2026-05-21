package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.common.data.AudioLink;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AudioVolumeBlock extends AudioPeripheralBlock implements EntityBlock {
  public AudioVolumeBlock(Properties properties) {
    super(properties);
  }

  @Override
  public AudioLink.LinkType getLinkType() {
    return AudioLink.LinkType.Volume;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new AudioVolumeBlockEntity(pos, state);
  }

  @Override
  public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
    super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    if (!level.isClientSide) pushToController(level, pos);
  }

  @Override
  public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
    super.onPlace(state, level, pos, oldState, isMoving);
    if (!level.isClientSide) pushToController(level, pos);
  }

  private void pushToController(Level level, BlockPos pos) {
    if (!(level.getBlockEntity(pos) instanceof AudioVolumeBlockEntity vbe)) return;
    BlockPos cpos = vbe.getControllerPos();
    if (cpos == null) return;
    if (!(level.getBlockEntity(cpos) instanceof AudioControllerBlockEntity cbe)) return;
    cbe.setVolume(level.getBestNeighborSignal(pos));
  }
}
