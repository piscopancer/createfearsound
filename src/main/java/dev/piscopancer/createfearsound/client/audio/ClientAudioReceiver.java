package dev.piscopancer.createfearsound.client.audio;

import dev.piscopancer.createfearsound.CFS;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.UUID;

public final class ClientAudioReceiver {
  @Nullable private static UUID activeTrackId;
  @Nullable private static byte[][] chunks;
  private static int totalChunks;
  private static int receivedCount;
  @Nullable private static CfsSoundInstance currentInstance;

  public static void onPlayStart(UUID trackId, String name, int numChunks) {
    stop();
    activeTrackId = trackId;
    totalChunks = numChunks;
    chunks = new byte[numChunks][];
    receivedCount = 0;
  }

  public static void onChunk(UUID trackId, int chunkIndex, byte[] data) {
    if (!trackId.equals(activeTrackId) || chunks == null) return;
    if (chunkIndex < 0 || chunkIndex >= chunks.length || chunks[chunkIndex] != null) return;
    chunks[chunkIndex] = data;
    if (++receivedCount == totalChunks) assemble();
  }

  public static void stop() {
    if (currentInstance != null) {
      currentInstance.requestStop();
      currentInstance = null;
    }
    activeTrackId = null;
    chunks = null;
  }

  private static void assemble() {
    byte[][] captured = chunks;
    chunks = null;

    int totalSize = 0;
    for (byte[] c : captured) totalSize += c.length;
    byte[] oggData = new byte[totalSize];
    int offset = 0;
    for (byte[] c : captured) { System.arraycopy(c, 0, oggData, offset, c.length); offset += c.length; }

    Minecraft mc = Minecraft.getInstance();
    mc.execute(() -> {
      try {
        CfsAudioStream stream = new CfsAudioStream(oggData);
        currentInstance = new CfsSoundInstance(stream, mc.player.position());
        mc.getSoundManager().play(currentInstance);
      } catch (IOException e) {
        CFS.LOGGER.error("[CFS] Failed to play audio: {}", e.getMessage());
      }
    });
  }
}
