package dev.piscopancer.createfearsound.items;

import dev.piscopancer.createfearsound.client.gui.CassetteMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Cassette extends Item {
  public Cassette(Properties properties) {
    super(properties);
  }

  public static enum Color {
    None,
    Red,
    Green
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    var itemstack = player.getItemInHand(hand);
    if (player.isCrouching()) {
      if (!level.isClientSide) {
        player.openMenu(new SimpleMenuProvider(
            (id, inv, p) -> new CassetteMenu(id, inv),
            Component.literal("Cassette")));
      }
      return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
    return InteractionResultHolder.pass(itemstack);
  }

}
