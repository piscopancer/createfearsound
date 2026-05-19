package dev.piscopancer.createfearsound.common.items;

import dev.piscopancer.createfearsound.common.blocks.ControlBlock;
import dev.piscopancer.createfearsound.common.data.TapePlayerLink;
import dev.piscopancer.createfearsound.common.registries.DataComponentsRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

public class TapePlayerItem extends BlockItem {
  public TapePlayerItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    var player = context.getPlayer();
    var level = context.getLevel();
    var clickedPos = context.getClickedPos();
    var state = level.getBlockState(clickedPos);

    if (player != null && player.isShiftKeyDown() && state.getBlock() instanceof ControlBlock control) {
      if (!level.isClientSide) {
        var stack = context.getItemInHand();
        List<TapePlayerLink> current = stack.get(DataComponentsRegistry.TAPE_PLAYER_LINKS.get());
        List<TapePlayerLink> updated = current == null ? new ArrayList<>() : new ArrayList<>(current);
        boolean removed = updated.removeIf(l -> l.pos().equals(clickedPos));
        if (!removed) {
          updated.add(new TapePlayerLink(clickedPos.immutable(), control.type));
        }
        stack.set(DataComponentsRegistry.TAPE_PLAYER_LINKS.get(), List.copyOf(updated));
        var msg = removed
            ? Component.translatable("createfearsound.tape_player.unlinked",
                control.type.name(), clickedPos.getX(), clickedPos.getY(), clickedPos.getZ())
            : Component.translatable("createfearsound.tape_player.linked",
                control.type.name(), clickedPos.getX(), clickedPos.getY(), clickedPos.getZ());
        player.displayClientMessage(msg, true);
      }
      return InteractionResult.sidedSuccess(level.isClientSide);
    }

    return super.useOn(context);
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, context, tooltip, flag);
    List<TapePlayerLink> links = stack.get(DataComponentsRegistry.TAPE_PLAYER_LINKS.get());
    if (links == null || links.isEmpty()) {
      tooltip.add(Component.translatable("createfearsound.tape_player.no_links")
          .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
      return;
    }
    tooltip.add(Component.translatable("createfearsound.tape_player.links_header", links.size())
        .withStyle(ChatFormatting.GOLD));
    for (TapePlayerLink link : links) {
      tooltip.add(Component.literal("• ")
          .withStyle(ChatFormatting.DARK_GRAY)
          .append(Component.literal(link.type().name()).withStyle(ChatFormatting.AQUA))
          .append(Component.literal(" "))
          .append(Component.literal(link.pos().getX() + ", " + link.pos().getY() + ", " + link.pos().getZ())
              .withStyle(ChatFormatting.GRAY)));
    }
  }
}
