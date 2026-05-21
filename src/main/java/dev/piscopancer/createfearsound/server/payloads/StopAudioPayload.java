package dev.piscopancer.createfearsound.server.payloads;

import dev.piscopancer.createfearsound.Util;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record StopAudioPayload() implements CustomPacketPayload {
  public static final Type<StopAudioPayload> TYPE = new Type<>(Util.modResLoc("stop_audio"));
  public static final StreamCodec<ByteBuf, StopAudioPayload> STREAM_CODEC = StreamCodec.unit(new StopAudioPayload());

  @Override
  public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
