package dev.piscopancer.createfearsound.gui;

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
  private final ItemStackHandler inventory = new ItemStackHandler(20); // 20 слотов

  public TapePieceMenu(int containerId, Inventory playerInv) {
    super(MenuTypesRegistry.TAPE_PIECE_MENU.get(), containerId);
    // 1. Создаем временный инвентарь (на 20 слотов)
    SimpleContainer inventory = new SimpleContainer(SLOT_COUNT);

    // 2. Добавляем слоты кассеты (например, 2 ряда по 10)
    for (int row = 0; row < 2; row++) {
      for (int col = 0; col < 10; col++) {
        this.addSlot(new Slot(inventory, col + row * 10, 8 + col * 18, 18 + row * 18));
      }
    }

    // 3. Добавляем стандартный инвентарь игрока (36 слотов)
    // layoutPlayerInventory(playerInv, 8, 84);
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
