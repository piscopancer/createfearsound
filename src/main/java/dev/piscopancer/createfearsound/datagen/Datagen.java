package dev.piscopancer.createfearsound.datagen;

import dev.piscopancer.createfearsound.CFS;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = CFS.MODID)
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

    if (event.includeServer()) {
      generator.addProvider(true, new CFSPressingRecipeProvider(output, event.getLookupProvider()));
    }
  }
}
