package dev.piscopancer.createfearsound.datagen;

import com.simibubi.create.api.data.recipe.PressingRecipeGen;
import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.registries.ItemsRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

final class CFSPressingRecipeProvider extends PressingRecipeGen {
  public CFSPressingRecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
    super(output, registries, CFS.MODID);
  }

  GeneratedRecipe

  tapePiece = create(() -> Items.KELP, b -> b.output(ItemsRegistry.TAPE_PIECE.get()));
}
