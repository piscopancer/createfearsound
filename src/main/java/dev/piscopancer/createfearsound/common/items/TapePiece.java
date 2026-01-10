package dev.piscopancer.createfearsound.common.items;

import dev.piscopancer.createfearsound.client.gui.TapePieceScreen;
import dev.piscopancer.createfearsound.common.data.TrackData;
import dev.piscopancer.createfearsound.common.registries.DataComponentsRegistry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class TapePiece extends Item {
  public TapePiece(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    var stack = player.getItemInHand(hand);
    if (level.isClientSide) {
      Minecraft.getInstance().setScreen(new TapePieceScreen(stack));
    }
    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    TrackData data = stack.get(DataComponentsRegistry.TAPE_PIECE.get());
    if (data != null) {
      tooltip.add(Component.literal("Recorded song").withStyle(ChatFormatting.GOLD));
      tooltip.add(
          Component.literal("╔").withStyle(ChatFormatting.DARK_GRAY)
              .append(Component.literal(data.title()).withStyle(ChatFormatting.WHITE)));
      tooltip.add(
          Component.literal("╚").withStyle(ChatFormatting.DARK_GRAY)
              .append(Component.literal(data.author()).withStyle(ChatFormatting.GRAY)));
      tooltip.add(
          Component.literal(formatDuration(data.duration()) + "").withStyle(ChatFormatting.GRAY));
    } else {
      tooltip.add(Component.literal("Пусто").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
    }
  }

  static String formatDuration(int sec) {
    int m = sec / 60;
    int s = sec % 60;
    return String.format("%d:%02d", m, s);
  }
}
