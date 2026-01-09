package dev.piscopancer.createfearsound.datagen;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.registries.ItemsRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

class CFSItemModelProvider extends ItemModelProvider {
  public CFSItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, CFS.MODID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    basicItem(ItemsRegistry.TAPE_PIECE.get());

    final var CASSETTE_PATH = ItemsRegistry.CASSETTE.getId().getPath();

    var cassetteBuilder = getBuilder(CASSETTE_PATH)
        .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
        .texture("layer0", modLoc("item/" + CASSETTE_PATH));

    getBuilder(CASSETTE_PATH + "_red")
        .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
        .texture("layer0", modLoc("item/" + CASSETTE_PATH))
        .texture("layer1", modLoc("item/" + CASSETTE_PATH + "_red"));
    cassetteBuilder.override()
        .predicate(ResourceLocation.fromNamespaceAndPath(CFS.MODID, "color"), 1)
        .model(new ModelFile.UncheckedModelFile(modLoc("item/" + CASSETTE_PATH + "_red")))
        .end();

    getBuilder(CASSETTE_PATH + "_green")
        .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
        .texture("layer0", modLoc("item/" + CASSETTE_PATH))
        .texture("layer1", modLoc("item/" + CASSETTE_PATH + "_green"));
    cassetteBuilder.override()
        .predicate(ResourceLocation.fromNamespaceAndPath(CFS.MODID, "color"), 2)
        .model(new ModelFile.UncheckedModelFile(modLoc("item/" + CASSETTE_PATH + "_green")))
        .end();

    //

    // Ингредиент: Кассета БЕЗ компонента player_id
    // Ingredient mixtapeWithoutOwner = Ingredient.of(
    // DataComponentPredicate.builder()
    // .expect(MyComponents.PLAYER_ID.get(), null) // null означает отсутствие
    // компонента
    // .build(),
    // ItemsRegistry.CASSETTE.get());

    // // Ингредиент: Идентификатор песни (обычный предмет)
    // Ingredient songId = Ingredient.of(MyItems.SONG_IDENTIFIER.get());

    // new ProcessingRecipeBuilder<>(MixingRecipe::new,
    // ResourceLocation.fromNamespaceAndPath("mymod", "mixtape_mixing"))
    // .withItemIngredients(
    // mixtapeWithoutOwner, // Наша кассета без ID
    // songId // Идентификатор песни
    // )
    // .output(MyItems.RECORDED_MIXTAPE.get()) // Результат
    // .requiresHeat(HeatCondition.NONE) // Условие нагрева (если нужно)
    // .build(output);
  }

}
