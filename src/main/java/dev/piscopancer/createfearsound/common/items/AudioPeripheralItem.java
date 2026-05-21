package dev.piscopancer.createfearsound.common.items;

import dev.piscopancer.createfearsound.common.blocks.AudioControllerBlock;
import dev.piscopancer.createfearsound.common.blocks.AudioPeripheralBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;

public class AudioPeripheralItem extends BlockItem {
  public AudioPeripheralItem(AudioPeripheralBlock block, Properties properties) {
    super(block, properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    var level = context.getLevel();
    var clickedPos = context.getClickedPos();
    var clickedState = level.getBlockState(clickedPos);
    var player = context.getPlayer();
    if (player != null && clickedState.getBlock() instanceof AudioControllerBlock) {
      AudioControllerBlock.captureLink(clickedPos, player, level);
      return InteractionResult.sidedSuccess(level.isClientSide);
    }
    return super.useOn(context);
  }
}
