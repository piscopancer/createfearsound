package dev.piscopancer.createfearsound.common.recipes;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;

public class CassetteMixingSerializer extends StandardProcessingRecipe.Serializer<CassetteMixingRecipe> {
  public CassetteMixingSerializer() {
    super(CassetteMixingRecipe::new);
  }
}
