package dev.piscopancer.createfearsound.datagen;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.api.data.recipe.PressingRecipeGen;
import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.recipes.CassetteMixingRecipe;
import dev.piscopancer.createfearsound.common.registries.ItemsRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

final class CFSRecipeProvider extends RecipeProvider {
  public CFSRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
    super(output, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput output) {
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ItemsRegistry.CASSETTE.get(), 1)
        .pattern("CCC")
        .pattern("ARA")
        .define('C', AllItems.CARDBOARD.get())
        .define('A', AllItems.ANDESITE_ALLOY.get())
        .define('R', Items.REDSTONE)
        .unlockedBy("has_cardboard", has(AllItems.CARDBOARD.get()))
        .save(output);
  }
}

final class CFSPressingRecipeProvider extends PressingRecipeGen {
  public CFSPressingRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
    super(output, registries, CFS.MODID);
  }

  GeneratedRecipe

  tapePiece = create(() -> Items.KELP, b -> b.output(ItemsRegistry.TAPE_PIECE.get()));
}

final class CFSMixingRecipeProvider extends MixingRecipeGen {
  public CFSMixingRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
    super(output, registries, CFS.MODID);
  }
}

final class CassetteMixingRecipeGen extends StandardProcessingRecipeGen<CassetteMixingRecipe> {
  CassetteMixingRecipeGen(PackOutput output, CompletableFuture<Provider> registries) {
    super(output, registries, CFS.MODID);
  }

  @Override
  protected IRecipeTypeInfo getRecipeType() {
    return AllRecipeTypes.MIXING;
  }

  @Override
  protected StandardProcessingRecipe.Builder<CassetteMixingRecipe> getBuilder(ResourceLocation id) {
    return new StandardProcessingRecipe.Builder<>(CassetteMixingRecipe::new, id);
  }

  GeneratedRecipe mixtape = create("mixtape", b -> b
      .withItemIngredients(
          Ingredient.of(ItemsRegistry.TAPE_PIECE.get()),
          Ingredient.of(ItemsRegistry.CASSETTE.get()))
      .output(ItemsRegistry.CASSETTE.get())
      .duration(200));
}
