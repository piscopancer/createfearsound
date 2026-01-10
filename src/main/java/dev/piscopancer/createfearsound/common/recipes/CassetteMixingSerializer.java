package dev.piscopancer.createfearsound.common.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CassetteMixingSerializer implements RecipeSerializer<CassetteMixingRecipe> {
  public static final MapCodec<CassetteMixingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
      ProcessingRecipeParams.CODEC.fieldOf("params").forGetter(ProcessingRecipe::getParams))
      .apply(instance, CassetteMixingRecipe::new));
  public static final StreamCodec<RegistryFriendlyByteBuf, CassetteMixingRecipe> STREAM_CODEC = StreamCodec.of(
      (buf, recipe) -> {
        ProcessingRecipeParams.STREAM_CODEC.encode(buf, recipe.getParams());
      },
      buf -> new CassetteMixingRecipe(ProcessingRecipeParams.STREAM_CODEC.decode(buf)));

  @Override
  public MapCodec<CassetteMixingRecipe> codec() {
    return CODEC;
  }

  @Override
  public StreamCodec<RegistryFriendlyByteBuf, CassetteMixingRecipe> streamCodec() {
    return STREAM_CODEC;
  }
}