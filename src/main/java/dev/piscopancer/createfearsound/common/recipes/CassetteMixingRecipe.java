package dev.piscopancer.createfearsound.common.recipes;

import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import dev.piscopancer.createfearsound.common.data.CassetteData;
import dev.piscopancer.createfearsound.common.data.TrackData;
import dev.piscopancer.createfearsound.common.registries.CreateSerializersRegistry;
import dev.piscopancer.createfearsound.common.registries.DataComponentsRegistry;
import dev.piscopancer.createfearsound.common.registries.ItemsRegistry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class CassetteMixingRecipe extends MixingRecipe {
  public CassetteMixingRecipe(ProcessingRecipeParams params) {
    super(params);
  }

  /** Called by the basin to determine actual item output — we build it here from input data. */
  @Override
  public NonNullList<ItemStack> getRemainingItems(RecipeInput input) {
    TrackData track = null;
    ItemStack cassetteIn = ItemStack.EMPTY;

    for (int i = 0; i < input.size(); i++) {
      ItemStack stack = input.getItem(i);
      if (stack.is(ItemsRegistry.TAPE_PIECE.get())) {
        TrackData t = stack.get(DataComponentsRegistry.TAPE_PIECE.get());
        if (t != null) track = t;
      } else if (stack.is(ItemsRegistry.CASSETTE.get())) {
        cassetteIn = stack;
      }
    }

    NonNullList<ItemStack> out = NonNullList.create();

    if (track == null) {
      if (!cassetteIn.isEmpty()) out.add(cassetteIn.copy());
      return out;
    }

    CassetteData existing = cassetteIn.isEmpty() ? null : cassetteIn.get(DataComponentsRegistry.CASSETE.get());
    List<TrackData> tracks = new ArrayList<>(existing != null ? existing.tracks() : List.of());
    tracks.add(track);

    String label = existing != null && !existing.label().isEmpty() ? existing.label() : "mixtape";
    String note = existing != null ? existing.note() : "";

    ItemStack result = new ItemStack(ItemsRegistry.CASSETTE.get());
    result.set(DataComponentsRegistry.CASSETE.get(), CassetteData.builder()
        .label(label).note(note).tracks(tracks).build());
    out.add(result);
    return out;
  }

  /** Prevent the JSON result item from being output directly — output comes from getRemainingItems. */
  @Override
  public List<ItemStack> rollResults(RandomSource random) {
    return Collections.emptyList();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return CreateSerializersRegistry.CASSETE_MIXING.get();
  }
}
