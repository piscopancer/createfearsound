package dev.piscopancer.createfearsound.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AudioLink(BlockPos pos, LinkType type) {
  public enum LinkType {
    Volume,
    Play,
    Pause;

    public static final Codec<LinkType> CODEC = Codec.STRING.xmap(s -> {
      try {
        return LinkType.valueOf(s);
      } catch (IllegalArgumentException e) {
        return LinkType.Volume;
      }
    }, LinkType::name);

    public static final StreamCodec<ByteBuf, LinkType> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(
        s -> {
          try {
            return LinkType.valueOf(s);
          } catch (IllegalArgumentException e) {
            return LinkType.Volume;
          }
        },
        LinkType::name);
  }

  public static final Codec<AudioLink> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      BlockPos.CODEC.fieldOf("pos").forGetter(AudioLink::pos),
      LinkType.CODEC.fieldOf("type").forGetter(AudioLink::type))
      .apply(instance, AudioLink::new));

  public static final StreamCodec<ByteBuf, AudioLink> STREAM_CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      AudioLink::pos,
      LinkType.STREAM_CODEC,
      AudioLink::type,
      AudioLink::new);
}
