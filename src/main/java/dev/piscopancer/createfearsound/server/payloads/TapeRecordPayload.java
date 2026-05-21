package dev.piscopancer.createfearsound.server.payloads;

import dev.piscopancer.createfearsound.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record TapeRecordPayload(String url, String title, String author) implements CustomPacketPayload {
  public static final Type<TapeRecordPayload> TYPE = new Type<>(Util.modResLoc("tape_record"));

  public static final StreamCodec<ByteBuf, TapeRecordPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, TapeRecordPayload::url,
      ByteBufCodecs.STRING_UTF8, TapeRecordPayload::title,
      ByteBufCodecs.STRING_UTF8, TapeRecordPayload::author,
      TapeRecordPayload::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
