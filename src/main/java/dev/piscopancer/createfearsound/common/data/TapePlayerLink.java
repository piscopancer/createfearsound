package dev.piscopancer.createfearsound.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TapePlayerLink(BlockPos pos, LinkType type) {
  public enum LinkType {
    Play,
    Pause,
    Volume;

    public static final Codec<LinkType> CODEC = Codec.STRING.xmap(s -> {
      try {
        return LinkType.valueOf(s);
      } catch (IllegalArgumentException e) {
        return LinkType.Play;
      }
    }, LinkType::name);

    public static final StreamCodec<ByteBuf, LinkType> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(
        s -> {
          try {
            return LinkType.valueOf(s);
          } catch (IllegalArgumentException e) {
            return LinkType.Play;
          }
        },
        LinkType::name);
  }

  public static final Codec<TapePlayerLink> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      BlockPos.CODEC.fieldOf("pos").forGetter(TapePlayerLink::pos),
      LinkType.CODEC.fieldOf("type").forGetter(TapePlayerLink::type))
      .apply(instance, TapePlayerLink::new));

  public static final StreamCodec<ByteBuf, TapePlayerLink> STREAM_CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      TapePlayerLink::pos,
      LinkType.STREAM_CODEC,
      TapePlayerLink::type,
      TapePlayerLink::new);
}
