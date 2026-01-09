package dev.piscopancer.createfearsound.client.gui;

import dev.piscopancer.createfearsound.registries.MenuTypesRegistry;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class TapePieceMenu extends AbstractContainerMenu {
  public static final int SLOT_COUNT = 20;
  // private final ItemStackHandler inventory = new ItemStackHandler(20); // 20
  // слотов

  public TapePieceMenu(int containerId, Inventory playerInv) {
    super(MenuTypesRegistry.TAPE_PIECE_MENU.get(), containerId);

    // Инвентарь самого предмета (20 слотов)
    SimpleContainer container = new SimpleContainer(20);

    // Добавляем слоты кассеты
    for (int row = 0; row < 2; row++) {
      for (int col = 0; col < 10; col++) {
        this.addSlot(new Slot(container, col + row * 10, 8 + col * 18, 18 + row * 18));
      }
    }

    // ОБЯЗАТЕЛЬНО: Инвентарь игрока (Main)
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
      }
    }

    // ОБЯЗАТЕЛЬНО: Хотбар игрока
    for (int col = 0; col < 9; col++) {
      this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
    }
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean stillValid(Player player) {
    return true;
  }
}
