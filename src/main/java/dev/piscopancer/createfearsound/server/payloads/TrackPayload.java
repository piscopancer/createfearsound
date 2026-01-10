package dev.piscopancer.createfearsound.server.payloads;

import dev.piscopancer.createfearsound.Util;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Builder
public record TrackPayload(String title, String author, String url, Integer duration) implements CustomPacketPayload {
  public static final Type<TrackPayload> TYPE = new Type<>(Util.modResLoc("track_data_payload"));

  public static final StreamCodec<ByteBuf, TrackPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, TrackPayload::title,
      ByteBufCodecs.STRING_UTF8, TrackPayload::author,
      ByteBufCodecs.STRING_UTF8, TrackPayload::url,
      ByteBufCodecs.VAR_INT, TrackPayload::duration,
      TrackPayload::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
