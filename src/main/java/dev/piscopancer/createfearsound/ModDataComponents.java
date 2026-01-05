package dev.piscopancer.createfearsound;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;
import dev.piscopancer.createfearsound.items.Cassette;

public class ModDataComponents {
  public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister
      .createDataComponents(Registries.DATA_COMPONENT_TYPE, CreateFearSound.MODID);

  public static final Supplier<DataComponentType<Cassette.Color>> COLOR_DATA_COMPONENT = ModDataComponents.DATA_COMPONENTS
      .registerComponentType(
          "color",
          builder -> builder
              .persistent(Codec.STRING.xmap(
                  k -> {
                    try {
                      return Cassette.Color.valueOf(k);
                    } catch (IllegalArgumentException e) {
                      return Cassette.Color.None;
                    }
                  },
                  v -> v.name()))
              .networkSynchronized(StreamCodec.of(
                  (buf, v) -> buf.writeUtf(v.name()),
                  buf -> {
                    String name = buf.readUtf();
                    try {
                      return Cassette.Color.valueOf(name);
                    } catch (IllegalArgumentException e) {
                      return Cassette.Color.None;
                    }
                  })));

}
