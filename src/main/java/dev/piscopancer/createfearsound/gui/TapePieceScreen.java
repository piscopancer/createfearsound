package dev.piscopancer.createfearsound.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TapePieceScreen extends AbstractContainerScreen<TapePieceMenu> {
  public TapePieceScreen(TapePieceMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
  }

  private EditBox titleEdit;
  private EditBox authorEdit;
  private EditBox urlEdit;

  @Override
  protected void init() {
    // Поле для названия
    this.titleEdit = new EditBox(this.font, this.width / 2 - 100, 50, 200, 20, Component.literal("Название"));
    this.addRenderableWidget(titleEdit);

    // Поле для автора
    this.authorEdit = new EditBox(this.font, this.width / 2 - 100, 90, 200, 20, Component.literal("Автор"));
    this.addRenderableWidget(authorEdit);

    // Поле для URL
    this.urlEdit = new EditBox(this.font, this.width / 2 - 100, 130, 200, 20, Component.literal("URL ссылки"));
    this.addRenderableWidget(urlEdit);

    // Кнопка сохранения
    this.addRenderableWidget(Button.builder(Component.literal("Записать"), (btn) -> {
      // Здесь отправка пакета на сервер с данными из полей
      // sendDataToServer(titleEdit.getValue(), authorEdit.getValue(),
      // urlEdit.getValue());
      this.onClose();
    }).bounds(this.width / 2 - 50, 170, 100, 20).build());
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'renderBg'");
  }
}
