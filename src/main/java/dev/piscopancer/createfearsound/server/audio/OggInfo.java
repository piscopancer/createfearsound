package dev.piscopancer.createfearsound.server.audio;

import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Duration and bitrate extracted directly from an OGG Vorbis file.
 * Reads only the first 4KB (identification header) and last 64KB (final granule).
 */
public record OggInfo(int durationSeconds, int bitrateKbps, boolean bitrateEstimated) {

  public String formatDuration() {
    int h = durationSeconds / 3600;
    int m = (durationSeconds % 3600) / 60;
    int s = durationSeconds % 60;
    if (h > 0) return h + ":" + pad(m) + ":" + pad(s);
    return m + ":" + pad(s);
  }

  public String formatBitrate() {
    return (bitrateEstimated ? "~" : "") + bitrateKbps + " kb/s";
  }

  public static Optional<OggInfo> scan(Path oggFile) {
    try (RandomAccessFile raf = new RandomAccessFile(oggFile.toFile(), "r")) {
      long fileSize = raf.length();
      if (fileSize == 0) return Optional.empty();

      // Read first 4KB — enough to find the Vorbis identification header
      byte[] head = new byte[(int) Math.min(4096, fileSize)];
      raf.seek(0);
      raf.readFully(head);

      int sampleRate = extractSampleRate(head);
      int nominalBitrate = extractNominalBitrate(head);
      if (sampleRate == 0) return Optional.empty();

      // Read last 64KB — find the last OGG page with a valid granule position
      long tailStart = Math.max(0, fileSize - 65536);
      byte[] tail = new byte[(int) (fileSize - tailStart)];
      raf.seek(tailStart);
      raf.readFully(tail);

      long lastGranule = extractLastGranule(tail);
      int durationSeconds = (int) (lastGranule / sampleRate);

      int bitrateKbps;
      boolean estimated;
      if (nominalBitrate > 0) {
        bitrateKbps = nominalBitrate / 1000;
        estimated = false;
      } else {
        bitrateKbps = durationSeconds > 0 ? (int) (fileSize * 8 / durationSeconds / 1000) : 0;
        estimated = true;
      }

      return Optional.of(new OggInfo(durationSeconds, bitrateKbps, estimated));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  // --- OGG parsing helpers ---

  private static int extractSampleRate(byte[] data) {
    int pos = findVorbisIdHeader(data);
    return pos >= 0 && pos + 16 <= data.length ? readIntLE(data, pos + 12) : 0;
  }

  private static int extractNominalBitrate(byte[] data) {
    int pos = findVorbisIdHeader(data);
    return pos >= 0 && pos + 24 <= data.length ? readIntLE(data, pos + 20) : 0;
  }

  private static int findVorbisIdHeader(byte[] data) {
    for (int i = 0; i < data.length - 7; i++) {
      if (data[i] == 0x01
          && data[i + 1] == 'v' && data[i + 2] == 'o' && data[i + 3] == 'r'
          && data[i + 4] == 'b' && data[i + 5] == 'i' && data[i + 6] == 's') {
        return i;
      }
    }
    return -1;
  }

  private static long extractLastGranule(byte[] data) {
    long last = 0;
    int i = 0;
    while (i <= data.length - 27) {
      if (data[i] != 'O' || data[i + 1] != 'g' || data[i + 2] != 'g' || data[i + 3] != 'S') {
        i++;
        continue;
      }
      long granule = readLongLE(data, i + 6);
      if (granule > 0 && granule != -1L) last = granule;

      int segments = data[i + 26] & 0xFF;
      if (i + 27 + segments > data.length) break;
      int bodySize = 0;
      for (int s = 0; s < segments; s++) bodySize += data[i + 27 + s] & 0xFF;
      i += 27 + segments + bodySize;
    }
    return last;
  }

  private static int readIntLE(byte[] d, int o) {
    return (d[o] & 0xFF) | ((d[o+1] & 0xFF) << 8) | ((d[o+2] & 0xFF) << 16) | ((d[o+3] & 0xFF) << 24);
  }

  private static long readLongLE(byte[] d, int o) {
    long r = 0;
    for (int i = 0; i < 8; i++) r |= ((long) (d[o + i] & 0xFF)) << (i * 8);
    return r;
  }

  private static String pad(int n) {
    return n < 10 ? "0" + n : String.valueOf(n);
  }
}
