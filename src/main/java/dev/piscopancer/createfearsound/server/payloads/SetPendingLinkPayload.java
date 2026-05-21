package dev.piscopancer.createfearsound.server.payloads;

import dev.piscopancer.createfearsound.Util;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SetPendingLinkPayload(Optional<BlockPos> pos) implements CustomPacketPayload {
  public static final Type<SetPendingLinkPayload> TYPE = new Type<>(Util.modResLoc("set_pending_link"));

  public static final StreamCodec<ByteBuf, SetPendingLinkPayload> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
      SetPendingLinkPayload::pos,
      SetPendingLinkPayload::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
