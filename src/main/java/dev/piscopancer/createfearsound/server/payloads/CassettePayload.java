package dev.piscopancer.createfearsound.server.payloads;

import dev.piscopancer.createfearsound.Util;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Builder
public record CassettePayload(String title, String author, String url, Integer duration)
    implements CustomPacketPayload {
  public static final Type<CassettePayload> TYPE = new Type<>(Util.modResLoc("cassette_data_payload"));

  public static final StreamCodec<ByteBuf, CassettePayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8, CassettePayload::title,
      ByteBufCodecs.STRING_UTF8, CassettePayload::author,
      ByteBufCodecs.STRING_UTF8, CassettePayload::url,
      ByteBufCodecs.VAR_INT, CassettePayload::duration,
      CassettePayload::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
