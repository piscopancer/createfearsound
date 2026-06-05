package dev.piscopancer.createfearsound.server.payloads;

import dev.piscopancer.createfearsound.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record AudioPlayStartPayload(UUID trackId, String name, int totalChunks, boolean followsPlayer, double x, double y, double z) implements CustomPacketPayload {
  public static final Type<AudioPlayStartPayload> TYPE = new Type<>(Util.modResLoc("audio_play_start"));

  private static final StreamCodec<ByteBuf, UUID> UUID_CODEC = StreamCodec.of(
      (buf, uuid) -> { buf.writeLong(uuid.getMostSignificantBits()); buf.writeLong(uuid.getLeastSignificantBits()); },
      buf -> new UUID(buf.readLong(), buf.readLong()));

  public static final StreamCodec<ByteBuf, AudioPlayStartPayload> STREAM_CODEC = StreamCodec.of(
      (buf, p) -> {
        UUID_CODEC.encode(buf, p.trackId);
        ByteBufCodecs.STRING_UTF8.encode(buf, p.name);
        ByteBufCodecs.VAR_INT.encode(buf, p.totalChunks);
        ByteBufCodecs.BOOL.encode(buf, p.followsPlayer);
        ByteBufCodecs.DOUBLE.encode(buf, p.x);
        ByteBufCodecs.DOUBLE.encode(buf, p.y);
        ByteBufCodecs.DOUBLE.encode(buf, p.z);
      },
      buf -> new AudioPlayStartPayload(
          UUID_CODEC.decode(buf),
          ByteBufCodecs.STRING_UTF8.decode(buf),
          ByteBufCodecs.VAR_INT.decode(buf),
          ByteBufCodecs.BOOL.decode(buf),
          ByteBufCodecs.DOUBLE.decode(buf),
          ByteBufCodecs.DOUBLE.decode(buf),
          ByteBufCodecs.DOUBLE.decode(buf)
      ));

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
