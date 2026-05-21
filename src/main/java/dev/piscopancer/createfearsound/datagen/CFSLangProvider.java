package dev.piscopancer.createfearsound.datagen;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.registries.BlocksRegistry;
import dev.piscopancer.createfearsound.common.registries.ItemsRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

final class CFSLangProvider extends LanguageProvider {
  private String locale;

  public CFSLangProvider(PackOutput output, String locale) {
    super(output, CFS.MODID, locale);
    this.locale = locale;
  }

  @Override
  protected void addTranslations() {
    switch (locale) {
      case "en_us" -> {
        add(ItemsRegistry.TAPE_PIECE.get(), "Tape piece");
        add(ItemsRegistry.CASSETTE.get(), "Cassette");
        add(BlocksRegistry.AUDIO_CONTROLLER.get(), "Audio Controller");
        add(BlocksRegistry.AUDIO_VOLUME.get(), "Audio Volume");
        add(BlocksRegistry.AUDIO_PLAY.get(), "Audio Play");
        add(BlocksRegistry.AUDIO_PAUSE.get(), "Audio Pause");
        add("createfearsound.audio_controller.no_links", "No peripherals linked");
        add("createfearsound.audio_controller.links_header", "Linked peripherals (%s):");
        add("createfearsound.audio_controller.captured", "Audio Controller at %s, %s, %s selected");
        add("createfearsound.audio_controller.attached", "%s linked to Audio Controller at %s, %s, %s");
        add("createfearsound.audio_peripheral.unlinked", "Not linked. Right-click an Audio Controller.");
        add("createfearsound.audio_peripheral.linked_to", "Will link to Audio Controller at %s, %s, %s");
        add("createfearsound.audio_peripheral.clicked", "[%s] click at %s, %s, %s");
        add("createfearsound.audio_controller.status.active", "Active");
        add("createfearsound.audio_controller.status.missing", "Block missing");
        add("createfearsound.gui.goggles.audio_volume", "Audio Volume");
        add("createfearsound.gui.goggles.audio_volume.volume", "Volume:");
        add("createfearsound.gui.goggles.audio_play", "Audio Play");
        add("createfearsound.gui.goggles.audio_pause", "Audio Pause");
        add("createfearsound.gui.goggles.audio_peripheral.connected", "Connected to controller");
        add("createfearsound.gui.goggles.audio_peripheral.disconnected", "Not connected");
      }
      case "ru_ru" -> {
        add(ItemsRegistry.TAPE_PIECE.get(), "Кусочек плёнки");
        add(ItemsRegistry.CASSETTE.get(), "Кассета");
        add(BlocksRegistry.AUDIO_CONTROLLER.get(), "Аудио-контроллер");
        add(BlocksRegistry.AUDIO_VOLUME.get(), "Блок громкости");
        add(BlocksRegistry.AUDIO_PLAY.get(), "Блок воспроизведения");
        add(BlocksRegistry.AUDIO_PAUSE.get(), "Блок паузы");
        add("createfearsound.audio_controller.no_links", "Периферии нет");
        add("createfearsound.audio_controller.links_header", "Привязанная периферия (%s):");
        add("createfearsound.audio_controller.captured", "Аудио-контроллер %s, %s, %s выбран");
        add("createfearsound.audio_controller.attached", "%s привязан к аудио-контроллеру %s, %s, %s");
        add("createfearsound.audio_peripheral.unlinked", "Без привязки. Кликни по аудио-контроллеру.");
        add("createfearsound.audio_peripheral.linked_to", "Привяжется к аудио-контроллеру %s, %s, %s");
        add("createfearsound.audio_peripheral.clicked", "[%s] клик %s, %s, %s");
        add("createfearsound.audio_controller.status.active", "Активен");
        add("createfearsound.audio_controller.status.missing", "Блок отсутствует");
        add("createfearsound.gui.goggles.audio_volume", "Блок громкости");
        add("createfearsound.gui.goggles.audio_volume.volume", "Громкость:");
        add("createfearsound.gui.goggles.audio_play", "Блок воспроизведения");
        add("createfearsound.gui.goggles.audio_pause", "Блок паузы");
        add("createfearsound.gui.goggles.audio_peripheral.connected", "Подключён к контроллеру");
        add("createfearsound.gui.goggles.audio_peripheral.disconnected", "Не подключён");
      }
    }
  }
}
