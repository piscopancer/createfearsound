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
        add(BlocksRegistry.TAPE_PLAYER.get(), "Tape Player");
        add(BlocksRegistry.PLAY_BLOCK.get(), "Play Control");
        add(BlocksRegistry.PAUSE_BLOCK.get(), "Pause Control");
        add(BlocksRegistry.VOLUME_BLOCK.get(), "Volume Control");
        add("createfearsound.tape_player.no_links", "No controls linked");
        add("createfearsound.tape_player.links_header", "Linked controls (%s):");
        add("createfearsound.tape_player.linked", "Linked %s at %s, %s, %s");
        add("createfearsound.tape_player.unlinked", "Unlinked %s at %s, %s, %s");
      }
      case "ru_ru" -> {
        add(ItemsRegistry.TAPE_PIECE.get(), "Кусочек плёнки");
        add(ItemsRegistry.CASSETTE.get(), "Кассета");
        add(BlocksRegistry.TAPE_PLAYER.get(), "Проигрыватель");
        add(BlocksRegistry.PLAY_BLOCK.get(), "Блок воспроизведения");
        add(BlocksRegistry.PAUSE_BLOCK.get(), "Блок паузы");
        add(BlocksRegistry.VOLUME_BLOCK.get(), "Блок громкости");
        add("createfearsound.tape_player.no_links", "Привязок нет");
        add("createfearsound.tape_player.links_header", "Привязанные блоки (%s):");
        add("createfearsound.tape_player.linked", "Привязан %s по координатам %s, %s, %s");
        add("createfearsound.tape_player.unlinked", "Отвязан %s по координатам %s, %s, %s");
      }
    }
  }
}
