package dev.piscopancer.createfearsound.items;

import dev.piscopancer.createfearsound.CreateFearSound;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
  public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, CreateFearSound.MODID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    final var CASSETTE_PATH = CreateFearSound.CASSETTE.getId().getPath();

    var cassetteBuilder = getBuilder(CASSETTE_PATH)
        .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
        .texture("layer0", modLoc("item/" + CASSETTE_PATH));

    getBuilder(CASSETTE_PATH + "_red")
        .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
        .texture("layer0", modLoc("item/" + CASSETTE_PATH))
        .texture("layer1", modLoc("item/" + CASSETTE_PATH + "_red"));
    cassetteBuilder.override()
        .predicate(ResourceLocation.fromNamespaceAndPath(CreateFearSound.MODID, "color"), 1)
        .model(new ModelFile.UncheckedModelFile(modLoc("item/" + CASSETTE_PATH + "_red")))
        .end();

    getBuilder(CASSETTE_PATH + "_green")
        .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
        .texture("layer0", modLoc("item/" + CASSETTE_PATH))
        .texture("layer1", modLoc("item/" + CASSETTE_PATH + "_green"));
    cassetteBuilder.override()
        .predicate(ResourceLocation.fromNamespaceAndPath(CreateFearSound.MODID, "color"), 2)
        .model(new ModelFile.UncheckedModelFile(modLoc("item/" + CASSETTE_PATH + "_green")))
        .end();
  }

}
