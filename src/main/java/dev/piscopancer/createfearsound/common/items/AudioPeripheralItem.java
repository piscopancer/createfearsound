package dev.piscopancer.createfearsound.common.items;

import dev.piscopancer.createfearsound.common.blocks.AudioControllerBlock;
import dev.piscopancer.createfearsound.common.blocks.AudioPeripheralBlock;
import dev.piscopancer.createfearsound.common.registries.DataComponentsRegistry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
      AudioControllerBlock.captureLink(context.getItemInHand(), clickedPos, player, level);
      return InteractionResult.sidedSuccess(level.isClientSide);
    }
    return super.useOn(context);
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, context, tooltip, flag);
    BlockPos linked = stack.get(DataComponentsRegistry.LINKED_AUDIO_CONTROLLER.get());
    if (linked == null) {
      tooltip.add(Component.translatable("createfearsound.audio_peripheral.unlinked")
          .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    } else {
      tooltip.add(Component.translatable("createfearsound.audio_peripheral.linked_to",
          linked.getX(), linked.getY(), linked.getZ())
          .withStyle(ChatFormatting.AQUA));
    }
  }
}
