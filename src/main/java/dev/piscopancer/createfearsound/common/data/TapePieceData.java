package dev.piscopancer.createfearsound.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TapePieceData(String title, String author, String url) {
  public static final Codec<TapePieceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.STRING.fieldOf("title").forGetter(TapePieceData::title),
      Codec.STRING.fieldOf("author").forGetter(TapePieceData::author),
      Codec.STRING.fieldOf("url").forGetter(TapePieceData::url))
      .apply(instance, TapePieceData::new));
  public static final StreamCodec<ByteBuf, TapePieceData> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, TapePieceData::title,
      ByteBufCodecs.STRING_UTF8, TapePieceData::author,
      ByteBufCodecs.STRING_UTF8, TapePieceData::url,
      TapePieceData::new);
}