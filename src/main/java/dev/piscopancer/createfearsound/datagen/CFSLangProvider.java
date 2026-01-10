package dev.piscopancer.createfearsound.datagen;

import dev.piscopancer.createfearsound.CFS;
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
      }
      case "ru_ru" -> {
        add(ItemsRegistry.TAPE_PIECE.get(), "Кусочек плёнки");
        add(ItemsRegistry.CASSETTE.get(), "Кассета");
      }
    }
    // Для кастомных строк (интерфейс, подсказки)
    // add("tooltip.mymod.power", "Энергия: %s FE");
  }
}
