package dev.piscopancer.createfearsound.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TrackData(String url, String title, String author, int duration) {
  public static final Codec<TrackData> CODEC = RecordCodecBuilder.create(i -> i.group(
      Codec.STRING.fieldOf("url").forGetter(TrackData::url),
      Codec.STRING.optionalFieldOf("title", "").forGetter(TrackData::title),
      Codec.STRING.optionalFieldOf("author", "").forGetter(TrackData::author),
      Codec.INT.optionalFieldOf("duration", 0).forGetter(TrackData::duration)
  ).apply(i, TrackData::new));

  public static final StreamCodec<ByteBuf, TrackData> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, TrackData::url,
      ByteBufCodecs.STRING_UTF8, TrackData::title,
      ByteBufCodecs.STRING_UTF8, TrackData::author,
      ByteBufCodecs.VAR_INT, TrackData::duration,
      TrackData::new);
}
