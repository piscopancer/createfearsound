package dev.piscopancer.createfearsound.datagen;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber
public class Datagen {
  /*
   * Server Side (Рецепты, теги, лут-таблицы).
   * Client Side (Модели предметов, модели блоков, переводы).
   */
  @SubscribeEvent
  static void generateData(GatherDataEvent event) {
    var generator = event.getGenerator();
    var output = generator.getPackOutput();
    var helper = event.getExistingFileHelper();

    generator.addProvider(
        event.includeClient(),
        new CFSItemModelProvider(output, helper));

    generator.addProvider(event.includeClient(), new CFSLangProvider(output, "en_us"));
    generator.addProvider(event.includeClient(), new CFSLangProvider(output,
        "ru_ru"));

    if (event.includeServer()) {
      generator.addProvider(true, new CFSPressingRecipeProvider(output, event.getLookupProvider()));
      generator.addProvider(true, new CFSMixingRecipeProvider(output,
          event.getLookupProvider()));
    }
  }
}
