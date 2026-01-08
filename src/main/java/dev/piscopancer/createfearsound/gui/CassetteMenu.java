package dev.piscopancer.createfearsound.gui;

import dev.piscopancer.createfearsound.registries.MenuTypesRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class CassetteMenu extends AbstractContainerMenu {
  public CassetteMenu(int containerId, Inventory playerInv) {
    super(MenuTypesRegistry.CASSETTE_MENU.get(), containerId);
    // ...
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    throw new UnsupportedOperationException("Unimplemented method 'quickMoveStack'");
  }

  @Override
  public boolean stillValid(Player player) {
    throw new UnsupportedOperationException("Unimplemented method 'stillValid'");
  }
}
