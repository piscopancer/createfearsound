package dev.piscopancer.createfearsound.common.registries;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.recipes.CassetteMixingRecipe;
import dev.piscopancer.createfearsound.common.recipes.CassetteMixingSerializer;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class CreateSerializersRegistry {
  static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister
      .create(Registries.RECIPE_SERIALIZER, CFS.MODID);

  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CassetteMixingRecipe>> CASSETE_MIXING = REGISTRY
      .register("cassette_mixing", CassetteMixingSerializer::new);
}
