package dev.piscopancer.createfearsound.common.registries;

import com.mojang.serialization.Codec;
import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.data.CassetteData;
import dev.piscopancer.createfearsound.common.data.TrackData;
import dev.piscopancer.createfearsound.common.items.Cassette;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DataComponentsRegistry {
  static final DeferredRegister.DataComponents REGISTRY = DeferredRegister
      .createDataComponents(Registries.DATA_COMPONENT_TYPE, CFS.MODID);

  public static final DeferredHolder<DataComponentType<?>, DataComponentType<TrackData>> TAPE_PIECE = REGISTRY
      .registerComponentType("tape_piece", b -> b
          .persistent(TrackData.CODEC)
          .networkSynchronized(TrackData.STREAM_CODEC));

  public static final DeferredHolder<DataComponentType<?>, DataComponentType<CassetteData>> CASSETE = REGISTRY
      .registerComponentType("cassette", b -> b
          .persistent(CassetteData.CODEC)
          .networkSynchronized(CassetteData.STREAM_CODEC));

  public static final DeferredHolder<DataComponentType<?>, DataComponentType<Cassette.Color>> COLOR_DATA_COMPONENT = REGISTRY
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
