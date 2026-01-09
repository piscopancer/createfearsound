package dev.piscopancer.createfearsound.gui;

import dev.piscopancer.createfearsound.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CassetteScreen extends AbstractContainerScreen<CassetteMenu> {
  // private static final ResourceLocation TEXTURE =
  // ResourceLocation.fromNamespaceAndPath("modid", "textures/gui/cassette.png");
  private static final ResourceLocation TEXTURE = Util.modResLoc(Util.texturePath("gui", "cassette.png"));

  public CassetteScreen(CassetteMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    this.imageWidth = 194; // Ширина текстуры (чуть больше стандартной для 10 слотов)
    this.imageHeight = 166; // Высота текстуры
    this.inventoryLabelY = this.imageHeight - 94; // Смещение надписи "Инвентарь"
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    int x = (this.width - this.imageWidth) / 2;
    int y = (this.height - this.imageHeight) / 2;
    guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    super.render(guiGraphics, mouseX, mouseY, partialTick);
    this.renderTooltip(guiGraphics, mouseX, mouseY); // Подсказки при наведении на предметы
  }
}
