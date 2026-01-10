package dev.piscopancer.createfearsound.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import lombok.Builder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@Builder
public record CassetteData(String label, String note, List<TrackData> tracks) {
  public static final Codec<CassetteData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.STRING.fieldOf("label").forGetter(CassetteData::label),
      Codec.STRING.fieldOf("note").forGetter(CassetteData::note),
      TrackData.CODEC.listOf().fieldOf("tracks").forGetter(CassetteData::tracks))
      .apply(instance, CassetteData::new));
  public static final StreamCodec<ByteBuf, CassetteData> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, CassetteData::label,
      ByteBufCodecs.STRING_UTF8, CassetteData::note,
      TrackData.STREAM_CODEC.apply(ByteBufCodecs.list()), CassetteData::tracks,
      CassetteData::new);
}