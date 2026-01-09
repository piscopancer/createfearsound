package dev.piscopancer.createfearsound.datagen;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.PressingRecipeGen;
import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.registries.ItemsRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

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
    // ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE,
    // ItemsRegistry.CASSETTE.get(), 1);
  }
}

final class CFSPressingRecipeProvider extends PressingRecipeGen {
  public CFSPressingRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
    super(output, registries, CFS.MODID);
  }

  GeneratedRecipe

  tapePiece = create(() -> Items.KELP, b -> b.output(ItemsRegistry.TAPE_PIECE.get()));
}
