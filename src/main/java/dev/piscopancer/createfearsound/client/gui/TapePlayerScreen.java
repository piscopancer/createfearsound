package dev.piscopancer.createfearsound.client.gui;

import dev.piscopancer.createfearsound.common.data.TapePlayerLink;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TapePlayerScreen extends AbstractContainerScreen<TapePlayerMenu> {
  private static final int BG_WIDTH = 220;
  private static final int BG_HEIGHT = 180;
  private static final int LINE_HEIGHT = 12;
  private static final int LIST_TOP = 28;

  public TapePlayerScreen(TapePlayerMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    this.imageWidth = BG_WIDTH;
    this.imageHeight = BG_HEIGHT;
  }

  @Override
  protected void init() {
    super.init();
    this.inventoryLabelY = this.imageHeight + 10;
    this.titleLabelX = 8;
    this.titleLabelY = 8;
  }

  @Override
  protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
    int x = (this.width - this.imageWidth) / 2;
    int y = (this.height - this.imageHeight) / 2;
    g.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xCC101010);
    g.fill(x, y, x + this.imageWidth, y + 20, 0xFF202020);
    g.fill(x, y + this.imageHeight - 1, x + this.imageWidth, y + this.imageHeight, 0xFF202020);
  }

  @Override
  protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
    g.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFF, false);

    var links = this.menu.getLinks();
    if (links.isEmpty()) {
      g.drawString(this.font,
          Component.translatable("createfearsound.tape_player.no_links")
              .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC),
          8, LIST_TOP, 0xFFFFFF, false);
      return;
    }

    g.drawString(this.font,
        Component.translatable("createfearsound.tape_player.links_header", links.size())
            .withStyle(ChatFormatting.GOLD),
        8, LIST_TOP - 12, 0xFFFFFF, false);

    int y = LIST_TOP;
    for (TapePlayerLink link : links) {
      var line = Component.literal(link.type().name())
          .withStyle(ChatFormatting.AQUA)
          .append(Component.literal("  "))
          .append(Component.literal(link.pos().getX() + ", " + link.pos().getY() + ", " + link.pos().getZ())
              .withStyle(ChatFormatting.GRAY));
      g.drawString(this.font, line, 8, y, 0xFFFFFF, false);
      y += LINE_HEIGHT;
    }
  }

  @Override
  public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
    this.renderBackground(g, mouseX, mouseY, partialTick);
    super.render(g, mouseX, mouseY, partialTick);
  }
}
