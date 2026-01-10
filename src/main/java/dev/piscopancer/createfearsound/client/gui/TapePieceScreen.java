package dev.piscopancer.createfearsound.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.piscopancer.createfearsound.network.payloads.TapePiecePayload;
import java.net.URI;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class TapePieceScreen extends Screen {
  // Убираем Menu и Inventory, они больше не нужны в конструкторе
  public TapePieceScreen(ItemStack stack) {
    super(stack.getHoverName());
  }

  private EditBox titleTextField;
  private EditBox authorTextField;
  private EditBox urlTextField;
  private Button saveButton;

  @Override
  protected void init() {
    int centerX = width / 2;

    titleTextField = new EditBox(font, centerX - 100, 50, 200, 20, Component.literal("Название"));
    addRenderableWidget(titleTextField);

    // Поле для автора
    authorTextField = new EditBox(font, centerX - 100, 90, 200, 20, Component.literal("Автор"));
    addRenderableWidget(authorTextField);

    // Поле для URL
    urlTextField = new EditBox(font, centerX - 100, 130, 200, 20, Component.literal("Ссылка на аудиофайл"));
    urlTextField.setMaxLength(128);
    urlTextField.setResponder(text -> {
      var valid = isWebUrl(text);
      saveButton.active = valid;
      saveButton.setTooltip(valid ? null : Tooltip.create(Component.literal("Введите прямую ссылку (http/https)")));
    });
    addRenderableWidget(urlTextField);

    // Кнопка сохранения
    saveButton = Button.builder(Component.literal("Сохранить"), (btn) -> {
      PacketDistributor.sendToServer(new TapePiecePayload(
          titleTextField.getValue(),
          authorTextField.getValue(),
          urlTextField.getValue()));
      onClose();
    }).bounds(centerX - 50, 170, 100, 20).build();

    saveButton.active = isWebUrl(urlTextField.getValue());
    addRenderableWidget(saveButton);
    setInitialFocus(titleTextField);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    super.render(guiGraphics, mouseX, mouseY, partialTick);
    int centerX = width / 2;
    guiGraphics.drawCenteredString(font, title, centerX, 15, 0xFFFFFF);
    var labelColor = ChatFormatting.GRAY.getColor();
    guiGraphics.drawString(font, "Название трека", centerX - 100, 40, labelColor);
    guiGraphics.drawString(font, "Исполнитель", centerX - 100, 80, labelColor);
    guiGraphics.drawString(font, "Ссылка на аудио", centerX - 100, 120, labelColor);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == InputConstants.KEY_ESCAPE) {
      this.onClose();
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char codePoint, int modifiers) {
    return super.charTyped(codePoint, modifiers);
  }

  private boolean isWebUrl(String text) {
    if (text.isEmpty())
      return false;
    try {
      URI uri = new URI(text);
      return (text.startsWith("http://") || text.startsWith("https://")) && text.contains(".");
    } catch (Exception e) {
      return false;
    }
  }
}
