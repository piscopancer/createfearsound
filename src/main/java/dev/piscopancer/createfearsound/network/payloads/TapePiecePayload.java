package dev.piscopancer.createfearsound.network.payloads;

import dev.piscopancer.createfearsound.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record TapePiecePayload(String title, String author, String url) implements CustomPacketPayload {
  public static final Type<TapePiecePayload> TYPE = new Type<>(Util.modResLoc("save_tape_piece"));

  public static final StreamCodec<ByteBuf, TapePiecePayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, TapePiecePayload::title,
      ByteBufCodecs.STRING_UTF8, TapePiecePayload::author,
      ByteBufCodecs.STRING_UTF8, TapePiecePayload::url,
      TapePiecePayload::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
