package dev.piscopancer.createfearsound.client.audio;

import dev.piscopancer.createfearsound.CFS;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public final class ClientAudioReceiver {
    @Nullable private static UUID activeTrackId;
    @Nullable private static CfsAudioStream currentStream;
    @Nullable private static CfsSoundInstance currentInstance;
    @Nullable private static Vec3 pendingPos;
    private static boolean pendingFollowsPlayer;
    private static int totalChunks;
    private static int receivedCount;

    public static void onPlayStart(UUID trackId, String name, int numChunks, boolean followsPlayer, double x, double y, double z) {
        stop();
        activeTrackId        = trackId;
        totalChunks          = numChunks;
        receivedCount        = 0;
        pendingPos           = new Vec3(x, y, z);
        pendingFollowsPlayer = followsPlayer;
        currentStream        = new CfsAudioStream();
    }

    public static void onChunk(UUID trackId, int chunkIndex, byte[] data) {
        if (currentStream == null || !trackId.equals(activeTrackId)) return;
        currentStream.pushChunk(data);
        if (++receivedCount == totalChunks) currentStream.signalEof();

        // Start playback on first chunk so getFormat() finds data immediately
        if (receivedCount == 1 && currentInstance == null && pendingPos != null) {
            Minecraft mc = Minecraft.getInstance();
            currentInstance = new CfsSoundInstance(currentStream, pendingPos, pendingFollowsPlayer);
            mc.getSoundManager().play(currentInstance);
            CFS.LOGGER.info("[CFS] Playback started (first chunk received)");
        }
    }

    public static void stop() {
        if (currentInstance != null) {
            currentInstance.requestStop();
            currentInstance = null;
        }
        // stream.close() is called by SoundEngine after the instance stops
        currentStream = null;
        activeTrackId = null;
        pendingPos    = null;
    }
}
