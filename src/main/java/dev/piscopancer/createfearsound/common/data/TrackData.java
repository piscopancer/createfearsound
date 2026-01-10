package dev.piscopancer.createfearsound.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@Builder
public record TrackData(String title, String author, String url, Integer duration) {
  public static final Codec<TrackData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.STRING.fieldOf("title").forGetter(TrackData::title),
      Codec.STRING.fieldOf("author").forGetter(TrackData::author),
      Codec.STRING.fieldOf("url").forGetter(TrackData::url),
      Codec.INT.fieldOf("duration").forGetter(TrackData::duration)).apply(instance, TrackData::new));
  public static final StreamCodec<ByteBuf, TrackData> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, TrackData::title,
      ByteBufCodecs.STRING_UTF8, TrackData::author,
      ByteBufCodecs.STRING_UTF8, TrackData::url,
      ByteBufCodecs.VAR_INT, TrackData::duration,
      TrackData::new);
}