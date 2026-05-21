package dev.piscopancer.createfearsound.server.payloads;

import dev.piscopancer.createfearsound.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record TapeFindResultPayload(String url, boolean success, String autoTitle, int duration, String error) implements CustomPacketPayload {
  public static final Type<TapeFindResultPayload> TYPE = new Type<>(Util.modResLoc("tape_find_result"));

  public static final StreamCodec<ByteBuf, TapeFindResultPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, TapeFindResultPayload::url,
      ByteBufCodecs.BOOL, TapeFindResultPayload::success,
      ByteBufCodecs.STRING_UTF8, TapeFindResultPayload::autoTitle,
      ByteBufCodecs.VAR_INT, TapeFindResultPayload::duration,
      ByteBufCodecs.STRING_UTF8, TapeFindResultPayload::error,
      TapeFindResultPayload::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
