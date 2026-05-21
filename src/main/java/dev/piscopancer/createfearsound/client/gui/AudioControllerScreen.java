package dev.piscopancer.createfearsound.client.gui;

import dev.piscopancer.createfearsound.common.blocks.AudioPauseBlock;
import dev.piscopancer.createfearsound.common.blocks.AudioPlayBlock;
import dev.piscopancer.createfearsound.common.blocks.AudioVolumeBlock;
import dev.piscopancer.createfearsound.common.data.AudioLink;
import dev.piscopancer.createfearsound.common.registries.ItemsRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class AudioControllerScreen extends AbstractContainerScreen<AudioControllerMenu> {
  private static final ResourceLocation TEXTURE =
      ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
  private static final int HEADER_H = 17;
  private static final int SLOT_SIZE = 18;
  private static final int BOTTOM_BORDER_H = 7;
  private static final int MIN_SLOTS = 3;
  private static final int SLOTS_X = 8;

  public AudioControllerScreen(AudioControllerMenu menu, Inventory inv, Component title) {
    super(menu, inv, title);
    int cols = Math.max(MIN_SLOTS, menu.getLinks().size());
    this.imageWidth = SLOTS_X + cols * SLOT_SIZE + SLOTS_X;
    this.imageHeight = HEADER_H + SLOT_SIZE + BOTTOM_BORDER_H;
  }

  @Override
  protected void init() {
    super.init();
    this.titleLabelX = 8;
    this.titleLabelY = 6;
    this.inventoryLabelY = this.imageHeight + 100;
  }

  @Override
  protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
    int x = this.leftPos, y = this.topPos;
    g.blit(TEXTURE, x, y, 0, 0, this.imageWidth, HEADER_H + SLOT_SIZE);
    g.blit(TEXTURE, x, y + HEADER_H + SLOT_SIZE, 0, 126, this.imageWidth, BOTTOM_BORDER_H);

    var links = this.menu.getLinks();
    for (int i = 0; i < links.size(); i++) {
      int slotX = x + SLOTS_X + i * SLOT_SIZE;
      int slotY = y + HEADER_H;
      boolean hovered = mouseX >= slotX && mouseX < slotX + SLOT_SIZE
          && mouseY >= slotY && mouseY < slotY + SLOT_SIZE;
      if (hovered)
        g.fill(slotX, slotY, slotX + SLOT_SIZE, slotY + SLOT_SIZE, 0x40FFFFFF);
      g.renderItem(itemForType(links.get(i).type()), slotX + 1, slotY + 1);
    }
  }

  @Override
  protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
    g.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);

    if (this.menu.getLinks().isEmpty()) {
      var msg = Component.translatable("createfearsound.audio_controller.no_links")
          .withStyle(ChatFormatting.ITALIC);
      int cx = (this.imageWidth - this.font.width(msg)) / 2;
      int cy = HEADER_H + (SLOT_SIZE - 8) / 2;
      g.drawString(this.font, msg, cx, cy, 0x888888, false);
    }
  }

  @Override
  public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
    this.renderBackground(g, mouseX, mouseY, partialTick);
    super.render(g, mouseX, mouseY, partialTick);

    var links = this.menu.getLinks();
    for (int i = 0; i < links.size(); i++) {
      var link = links.get(i);
      int slotX = this.leftPos + SLOTS_X + i * SLOT_SIZE;
      int slotY = this.topPos + HEADER_H;
      boolean hovered = mouseX >= slotX && mouseX < slotX + SLOT_SIZE
          && mouseY >= slotY && mouseY < slotY + SLOT_SIZE;
      if (!hovered) continue;

      boolean active = isBlockPresent(link);
      List<Component> tip = new ArrayList<>();
      tip.add(itemForType(link.type()).getHoverName().copy().withStyle(ChatFormatting.WHITE));
      tip.add(Component.literal(link.pos().getX() + ", " + link.pos().getY() + ", " + link.pos().getZ())
          .withStyle(ChatFormatting.GRAY));
      tip.add(Component.translatable(active
          ? "createfearsound.audio_controller.status.active"
          : "createfearsound.audio_controller.status.missing")
          .withStyle(active ? ChatFormatting.GREEN : ChatFormatting.RED));
      g.renderTooltip(this.font, tip, Optional.empty(), mouseX, mouseY);
    }
  }

  private static ItemStack itemForType(AudioLink.LinkType type) {
    return switch (type) {
      case Volume -> new ItemStack(ItemsRegistry.AUDIO_VOLUME.get());
      case Play -> new ItemStack(ItemsRegistry.AUDIO_PLAY.get());
      case Pause -> new ItemStack(ItemsRegistry.AUDIO_PAUSE.get());
    };
  }

  private boolean isBlockPresent(AudioLink link) {
    if (this.minecraft == null || this.minecraft.level == null) return false;
    var block = this.minecraft.level.getBlockState(link.pos()).getBlock();
    return switch (link.type()) {
      case Volume -> block instanceof AudioVolumeBlock;
      case Play -> block instanceof AudioPlayBlock;
      case Pause -> block instanceof AudioPauseBlock;
    };
  }
}
