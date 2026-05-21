package dev.piscopancer.createfearsound.server.payloads;

import dev.piscopancer.createfearsound.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record TapeFindPayload(String url) implements CustomPacketPayload {
  public static final Type<TapeFindPayload> TYPE = new Type<>(Util.modResLoc("tape_find"));

  public static final StreamCodec<ByteBuf, TapeFindPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, TapeFindPayload::url,
      TapeFindPayload::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
