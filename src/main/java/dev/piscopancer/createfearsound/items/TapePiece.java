package dev.piscopancer.createfearsound.items;

import dev.piscopancer.createfearsound.client.gui.TapePieceMenu;
import dev.piscopancer.createfearsound.common.data.TapePieceData;
import dev.piscopancer.createfearsound.registries.DataComponentsRegistry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
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
    ItemStack stack = player.getItemInHand(hand);
    if (!level.isClientSide) {
      player.openMenu(
          new SimpleMenuProvider((containerId, playerInventory, p) -> new TapePieceMenu(containerId, playerInventory),
              Component.literal("Tape Piece")));
    }
    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    TapePieceData data = stack.get(DataComponentsRegistry.TAPE_PIECE.get());
    if (data != null) {
      tooltip.add(Component.literal("Название: " + data.title()).withStyle(ChatFormatting.GOLD));
      tooltip.add(Component.literal("Автор: " + data.author()).withStyle(ChatFormatting.AQUA));
    } else {
      tooltip.add(Component.literal("Пустая кассета").withStyle(ChatFormatting.GRAY));
    }
  }
}
