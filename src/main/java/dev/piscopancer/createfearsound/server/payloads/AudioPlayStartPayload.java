package dev.piscopancer.createfearsound.server.payloads;

import dev.piscopancer.createfearsound.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record AudioPlayStartPayload(UUID trackId, String name, int totalChunks) implements CustomPacketPayload {
  public static final Type<AudioPlayStartPayload> TYPE = new Type<>(Util.modResLoc("audio_play_start"));

  private static final StreamCodec<ByteBuf, UUID> UUID_CODEC = StreamCodec.of(
      (buf, uuid) -> { buf.writeLong(uuid.getMostSignificantBits()); buf.writeLong(uuid.getLeastSignificantBits()); },
      buf -> new UUID(buf.readLong(), buf.readLong()));

  public static final StreamCodec<ByteBuf, AudioPlayStartPayload> STREAM_CODEC = StreamCodec.composite(
      UUID_CODEC, AudioPlayStartPayload::trackId,
      ByteBufCodecs.STRING_UTF8, AudioPlayStartPayload::name,
      ByteBufCodecs.VAR_INT, AudioPlayStartPayload::totalChunks,
      AudioPlayStartPayload::new);

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
