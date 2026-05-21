package dev.piscopancer.createfearsound.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.piscopancer.createfearsound.common.data.TrackData;
import dev.piscopancer.createfearsound.server.payloads.TapeFindPayload;
import dev.piscopancer.createfearsound.server.payloads.TapeFindResultPayload;
import dev.piscopancer.createfearsound.server.payloads.TapeRecordPayload;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;

public class TapePieceScreen extends Screen {
  private enum State { IDLE, SEARCHING, FOUND, ERROR }

  private State state = State.IDLE;
  private String lastSearchedUrl = "";
  private String errorMessage = "";

  @Nullable private final TrackData existing;

  private EditBox urlField;
  private Button findButton;
  private EditBox titleField;
  private EditBox authorField;
  private Button recordButton;

  public TapePieceScreen(@Nullable TrackData existing) {
    super(Component.literal("Запись на плёнку"));
    this.existing = existing;
  }

  @Override
  protected void init() {
    int cx = width / 2;
    int top = height / 2 - 60;

    urlField = new EditBox(font, cx - 100, top, 200, 20, Component.literal("URL"));
    urlField.setMaxLength(512);
    addRenderableWidget(urlField);

    findButton = Button.builder(Component.literal("Найти"), btn -> onFind())
        .bounds(cx - 50, top + 26, 100, 20).build();
    addRenderableWidget(findButton);

    titleField = new EditBox(font, cx - 100, top + 54, 200, 20, Component.literal("Название"));
    titleField.setMaxLength(128);
    titleField.setHint(Component.literal("Название").withStyle(ChatFormatting.DARK_GRAY));
    addRenderableWidget(titleField);

    authorField = new EditBox(font, cx - 100, top + 80, 200, 20, Component.literal("Автор"));
    authorField.setMaxLength(128);
    authorField.setHint(Component.literal("Автор").withStyle(ChatFormatting.DARK_GRAY));
    addRenderableWidget(authorField);

    recordButton = Button.builder(Component.literal("Записать"), btn -> onRecord())
        .bounds(cx - 50, top + 108, 100, 20).build();
    addRenderableWidget(recordButton);

    // Pre-fill from existing data BEFORE wiring responders (no callbacks fire)
    if (existing != null) {
      urlField.setValue(existing.url());
      titleField.setValue(existing.title());
      authorField.setValue(existing.author());
      lastSearchedUrl = existing.url();
      state = State.FOUND;
    }

    urlField.setResponder(text -> {
      if (state == State.FOUND && !text.equals(lastSearchedUrl)) {
        state = State.IDLE;
        titleField.setValue("");
        authorField.setValue("");
      }
      refreshWidgets();
    });
    titleField.setResponder(text -> refreshWidgets());
    authorField.setResponder(text -> refreshWidgets());

    setInitialFocus(urlField);
    refreshWidgets();
  }

  private void refreshWidgets() {
    if (urlField == null) return;
    String url = urlField.getValue();
    boolean urlUnchanged = url.equals(lastSearchedUrl);

    findButton.active = isWebUrl(url) && state != State.SEARCHING && !urlUnchanged;

    boolean metaEditable = state == State.FOUND && urlUnchanged;
    titleField.setEditable(metaEditable);
    authorField.setEditable(metaEditable);

    recordButton.active = metaEditable
        && !titleField.getValue().isBlank()
        && !authorField.getValue().isBlank();
  }

  private void onFind() {
    String url = urlField.getValue();
    lastSearchedUrl = url;
    state = State.SEARCHING;
    errorMessage = "";
    refreshWidgets();
    PacketDistributor.sendToServer(new TapeFindPayload(url));
  }

  public void onFindResult(TapeFindResultPayload payload) {
    if (!payload.url().equals(urlField.getValue())) return;
    if (payload.success()) {
      state = State.FOUND;
      if (!payload.autoTitle().isBlank() && titleField.getValue().isBlank()) {
        titleField.setValue(payload.autoTitle());
      }
    } else {
      state = State.ERROR;
      errorMessage = payload.error();
    }
    refreshWidgets();
  }

  private void onRecord() {
    Minecraft mc = Minecraft.getInstance();
    if (mc.player != null && mc.level != null) {
      mc.player.playSound(SoundEvents.ITEM_BREAK, 1.0f, 0.8f + mc.level.random.nextFloat() * 0.4f);
    }
    PacketDistributor.sendToServer(new TapeRecordPayload(
        urlField.getValue(), titleField.getValue(), authorField.getValue()));
    onClose();
  }

  @Override
  public void render(GuiGraphics g, int mx, int my, float pt) {
    renderBackground(g, mx, my, pt);
    super.render(g, mx, my, pt);

    int cx = width / 2;
    int top = height / 2 - 60;

    g.drawCenteredString(font, title, cx, top - 20, 0xFFFFFF);
    g.drawString(font, "Ссылка", cx - 100, top - 10, ChatFormatting.GRAY.getColor());
    g.drawString(font, "Название", cx - 100, top + 44, ChatFormatting.GRAY.getColor());
    g.drawString(font, "Автор", cx - 100, top + 70, ChatFormatting.GRAY.getColor());

    if (state == State.SEARCHING) {
      g.drawCenteredString(font, Component.literal("Загрузка...").withStyle(ChatFormatting.YELLOW),
          cx, top + 36, 0xFFFFFF);
    } else if (state == State.ERROR && !errorMessage.isEmpty()) {
      String msg = errorMessage.length() > 40 ? errorMessage.substring(0, 37) + "..." : errorMessage;
      g.drawCenteredString(font, Component.literal(msg).withStyle(ChatFormatting.RED),
          cx, top + 36, 0xFFFFFF);
    }
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == InputConstants.KEY_ESCAPE) {
      onClose();
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  private boolean isWebUrl(String text) {
    return !text.isEmpty() && (text.startsWith("http://") || text.startsWith("https://")) && text.contains(".");
  }
}
