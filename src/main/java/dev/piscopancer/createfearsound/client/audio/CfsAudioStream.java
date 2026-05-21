package dev.piscopancer.createfearsound.client.audio;

import net.minecraft.client.sounds.AudioStream;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class CfsAudioStream implements AudioStream {
  private final ByteBuffer pcm;
  private final AudioFormat format;

  public CfsAudioStream(byte[] oggData) throws IOException {
    ByteBuffer raw = MemoryUtil.memAlloc(oggData.length);
    raw.put(oggData).flip();

    IntBuffer channelsBuf = MemoryUtil.memAllocInt(1);
    IntBuffer sampleRateBuf = MemoryUtil.memAllocInt(1);
    ShortBuffer decoded = STBVorbis.stb_vorbis_decode_memory(raw, channelsBuf, sampleRateBuf);
    MemoryUtil.memFree(raw);

    if (decoded == null) {
      MemoryUtil.memFree(channelsBuf);
      MemoryUtil.memFree(sampleRateBuf);
      throw new IOException("STBVorbis: failed to decode OGG");
    }

    int channels = channelsBuf.get(0);
    int sampleRate = sampleRateBuf.get(0);
    MemoryUtil.memFree(channelsBuf);
    MemoryUtil.memFree(sampleRateBuf);

    this.format = new AudioFormat(sampleRate, 16, channels, true, false);

    int byteCount = decoded.remaining() * 2;
    this.pcm = MemoryUtil.memAlloc(byteCount);
    this.pcm.asShortBuffer().put(decoded);
    this.pcm.limit(byteCount);
    MemoryUtil.memFree(decoded);
  }

  @Override
  public AudioFormat getFormat() {
    return format;
  }

  @Override
  public ByteBuffer read(int size) throws IOException {
    if (!pcm.hasRemaining()) return MemoryUtil.memAlloc(0);
    int toRead = Math.min(size, pcm.remaining());
    int savedLimit = pcm.limit();
    pcm.limit(pcm.position() + toRead);
    ByteBuffer chunk = MemoryUtil.memAlloc(toRead);
    chunk.put(pcm);
    chunk.flip();
    pcm.limit(savedLimit);
    return chunk;
  }

  @Override
  public void close() {
    MemoryUtil.memFree(pcm);
  }
}
