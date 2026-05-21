package dev.piscopancer.createfearsound.common.items;

import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import dev.piscopancer.createfearsound.client.gui.TapePieceScreen;
import dev.piscopancer.createfearsound.common.data.TrackData;
import dev.piscopancer.createfearsound.common.registries.DataComponentsRegistry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbility;

public class TapePiece extends SandPaperItem {
  public TapePiece(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack tape = player.getItemInHand(hand);
    InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    if (!player.getItemInHand(otherHand).is(Items.FLINT))
      return InteractionResultHolder.fail(tape);
    player.startUsingItem(hand);
    return InteractionResultHolder.success(tape);
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
    if (level.isClientSide) {
      TrackData existing = stack.get(DataComponentsRegistry.TAPE_PIECE.get());
      Minecraft.getInstance().setScreen(new TapePieceScreen(existing));
    }
    return stack;
  }

  @Override
  public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
    // flint stays in hand — nothing to return
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    return InteractionResult.PASS;
  }

  @Override
  public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
    return false;
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    TrackData data = stack.get(DataComponentsRegistry.TAPE_PIECE.get());
    if (data != null) {
      tooltip.add(Component.literal(data.url()).withStyle(ChatFormatting.GRAY));
      if (!data.title().isEmpty()) {
        tooltip.add(Component.literal(data.title()).withStyle(ChatFormatting.WHITE));
        if (!data.author().isEmpty())
          tooltip.add(Component.literal(data.author()).withStyle(ChatFormatting.GRAY));
        if (data.duration() > 0)
          tooltip.add(Component.literal(formatDuration(data.duration())).withStyle(ChatFormatting.GRAY));
      }
    } else {
      tooltip.add(Component.literal("Пусто").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
    }
  }

  private static String formatDuration(int sec) {
    int m = sec / 60;
    int s = sec % 60;
    return String.format("%d:%02d", m, s);
  }
}
