package dev.piscopancer.createfearsound.common.recipes;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import dev.piscopancer.createfearsound.common.data.CassetteData;
import dev.piscopancer.createfearsound.common.registries.DataComponentsRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class CassetteMixingRecipe extends MixingRecipe {
  public CassetteMixingRecipe(ProcessingRecipeParams params) {
    super(params);
  }

  @Override
  public boolean matches(RecipeInput input, @NotNull Level worldIn) {
    for (int i = 0; i < input.size(); i++) {
      var stack = input.getItem(i);
      if (stack.is(Items.PAPER) && stack.has(DataComponents.CUSTOM_NAME))
        return true;
    }
    return false;
  }

  @Override
  public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
    ItemStack result = super.assemble(input, registries);
    String note = "";
    CassetteData cassetteData = null;
    for (int i = 0; i < input.size(); i++) {
      ItemStack stack = input.getItem(i);
      if (stack.is(Items.PAPER)) {
        Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
          note = customName.getString();
        }
      } else if (stack.has(DataComponentsRegistry.CASSETE.get())) {
        cassetteData = stack.get(DataComponentsRegistry.CASSETE.get());
      }
    }
    var finalData = CassetteData.builder()
        .label(cassetteData != null ? cassetteData.label() : "")
        .tracks(cassetteData != null ? cassetteData.tracks() : List.of())
        .note(note.isEmpty() ? (cassetteData != null ? cassetteData.note() : "")
            : note)
        .build();
    result.set(DataComponentsRegistry.CASSETE.get(), finalData);
    return result;
  }

  @Override
  public RecipeType<?> getType() {
    return AllRecipeTypes.MIXING.getType();
  }
}