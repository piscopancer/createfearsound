package dev.piscopancer.createfearsound.server.payloads;

import dev.piscopancer.createfearsound.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record AudioChunkPayload(UUID trackId, int chunkIndex, byte[] data) implements CustomPacketPayload {
  public static final Type<AudioChunkPayload> TYPE = new Type<>(Util.modResLoc("audio_chunk"));

  private static final StreamCodec<ByteBuf, UUID> UUID_CODEC = StreamCodec.of(
      (buf, uuid) -> { buf.writeLong(uuid.getMostSignificantBits()); buf.writeLong(uuid.getLeastSignificantBits()); },
      buf -> new UUID(buf.readLong(), buf.readLong()));

  private static final StreamCodec<ByteBuf, byte[]> BYTES_CODEC = StreamCodec.of(
      (buf, data) -> { ByteBufCodecs.VAR_INT.encode(buf, data.length); buf.writeBytes(data); },
      buf -> { byte[] data = new byte[ByteBufCodecs.VAR_INT.decode(buf)]; buf.readBytes(data); return data; });

  public static final StreamCodec<ByteBuf, AudioChunkPayload> STREAM_CODEC = StreamCodec.composite(
      UUID_CODEC, AudioChunkPayload::trackId,
      ByteBufCodecs.VAR_INT, AudioChunkPayload::chunkIndex,
      BYTES_CODEC, AudioChunkPayload::data,
      AudioChunkPayload::new);

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
