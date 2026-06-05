package dev.piscopancer.createfearsound.client.audio;

import dev.piscopancer.createfearsound.CFS;
import net.minecraft.client.sounds.AudioStream;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CfsAudioStream implements AudioStream {

    private static final int INITIAL_WORK_BUF = 65536;
    private static final long POLL_MS = 50;

    // shared: main thread writes, audio thread reads
    private final LinkedBlockingQueue<byte[]> incoming = new LinkedBlockingQueue<>();
    private volatile boolean eof = false;
    private volatile boolean closed = false;

    // audio thread only — OGG accumulation (heap, compacted on demand)
    private byte[] ogg   = new byte[65536];
    private int oggWrite = 0;
    private int oggRead  = 0;

    // reusable native work buffer for vorbis I/O (grows, never shrinks)
    private ByteBuffer workBuf = MemoryUtil.memAlloc(INITIAL_WORK_BUF);

    // vorbis decoder state
    private long vorbis   = 0;
    private int  channels = 0;
    private AudioFormat format;

    // decoded PCM ready to serve (native buffers freed after consumed)
    private final ArrayDeque<ByteBuffer> pcmQueue = new ArrayDeque<>();
    private int pcmTotal = 0;

    // ── main-thread API ───────────────────────────────────────────────────

    public void pushChunk(byte[] data) {
        incoming.offer(data);
    }

    public void signalEof() {
        eof = true;
    }

    // ── AudioStream ───────────────────────────────────────────────────────

    @Override
    public AudioFormat getFormat() {
        ensureOpen();
        if (format == null) CFS.LOGGER.error("[CFS] getFormat() called but vorbis failed to open");
        return format;
    }

    @Override
    public ByteBuffer read(int size) throws IOException {
        if (closed || vorbis == 0) return MemoryUtil.memAlloc(0);
        fillPcm(size);
        return drainPcm(size);
    }

    @Override
    public void close() {
        closed = true;
        if (vorbis != 0) {
            STBVorbis.stb_vorbis_close(vorbis);
            vorbis = 0;
        }
        MemoryUtil.memFree(workBuf);
        for (ByteBuffer buf : pcmQueue) MemoryUtil.memFree(buf);
        pcmQueue.clear();
        pcmTotal = 0;
    }

    // ── vorbis init ───────────────────────────────────────────────────────

    private void ensureOpen() {
        while (!closed && vorbis == 0) {
            drainIncoming();
            tryOpen();
            if (vorbis != 0) break;
            if (eof && incoming.isEmpty()) break;
            try {
                byte[] chunk = incoming.poll(POLL_MS, TimeUnit.MILLISECONDS);
                if (chunk != null) appendOgg(chunk);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void tryOpen() {
        int available = oggWrite - oggRead;
        if (available <= 0) return;

        ensureWorkBuf(available);
        workBuf.clear();
        workBuf.put(ogg, oggRead, available).flip();

        IntBuffer consumed = MemoryUtil.memAllocInt(1);
        IntBuffer error    = MemoryUtil.memAllocInt(1);
        try {
            long h = STBVorbis.stb_vorbis_open_pushdata(workBuf, consumed, error, null);
            if (h == 0) {
                int err = error.get(0);
                if (err != STBVorbis.VORBIS_need_more_data)
                    CFS.LOGGER.error("[CFS] stb_vorbis_open_pushdata error: {}", err);
                return;
            }
            vorbis = h;
            oggRead += consumed.get(0);
            try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
                STBVorbis.stb_vorbis_get_info(h, info);
                channels = info.channels();
                format = new AudioFormat(info.sample_rate(), 16, channels, true, false);
                CFS.LOGGER.info("[CFS] Vorbis opened: {}ch {}Hz", channels, info.sample_rate());
            }
        } finally {
            MemoryUtil.memFree(consumed);
            MemoryUtil.memFree(error);
        }
    }

    // ── decode ────────────────────────────────────────────────────────────

    private void fillPcm(int needed) {
        while (!closed && pcmTotal < needed) {
            if (decodeFrame()) continue;
            // vorbis needs more OGG data
            byte[] chunk = incoming.poll();
            if (chunk != null) { appendOgg(chunk); continue; }
            if (eof && incoming.isEmpty()) break;
            try {
                chunk = incoming.poll(POLL_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if (chunk != null) appendOgg(chunk);
        }
    }

    private boolean decodeFrame() {
        int available = oggWrite - oggRead;
        if (available <= 0) return false;

        // Try with a capped window first (frames are small), grow only if needed
        int toPass = Math.min(available, INITIAL_WORK_BUF);
        ensureWorkBuf(toPass);
        workBuf.clear();
        workBuf.put(ogg, oggRead, toPass).flip();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer outCh     = stack.mallocInt(1);
            IntBuffer outNs     = stack.mallocInt(1);
            // stb_vorbis writes float** into this — one level of indirection
            var       outputPtr = stack.mallocPointer(1);

            int consumed = STBVorbis.stb_vorbis_decode_frame_pushdata(vorbis, workBuf, outCh, outputPtr, outNs);

            if (consumed == 0 && toPass < available) {
                // frame spans beyond the window — retry with full available data
                ensureWorkBuf(available);
                workBuf.clear();
                workBuf.put(ogg, oggRead, available).flip();
                consumed = STBVorbis.stb_vorbis_decode_frame_pushdata(vorbis, workBuf, outCh, outputPtr, outNs);
            }

            if (consumed == 0) return false; // truly need more incoming data

            oggRead += consumed;
            int nc = outCh.get(0);
            int ns = outNs.get(0);

            if (ns > 0 && nc > 0) {
                // outputPtr.get(0) is float** — pointer to the per-channel float* array
                long channelArrayAddr = outputPtr.get(0);
                if (channelArrayAddr == 0) return true; // consumed but no sample data (header packet)

                var channelPtrs = MemoryUtil.memPointerBuffer(channelArrayAddr, nc);
                FloatBuffer[] chBufs = new FloatBuffer[nc];
                for (int ch = 0; ch < nc; ch++) {
                    chBufs[ch] = MemoryUtil.memFloatBuffer(channelPtrs.get(ch), ns);
                }

                ByteBuffer pcm = MemoryUtil.memAlloc(ns * nc * 2);
                for (int i = 0; i < ns; i++) {
                    for (int ch = 0; ch < nc; ch++) {
                        float s = chBufs[ch].get();
                        pcm.putShort((short) Math.max(-32768, Math.min(32767, (int) (s * 32767f))));
                    }
                }
                pcm.flip();
                pcmQueue.add(pcm);
                pcmTotal += ns * nc * 2;
            }
            return true;
        }
    }

    private ByteBuffer drainPcm(int size) {
        if (pcmTotal == 0) return MemoryUtil.memAlloc(0);
        int toDrain = Math.min(size, pcmTotal);
        ByteBuffer out = MemoryUtil.memAlloc(toDrain);
        int rem = toDrain;
        while (rem > 0 && !pcmQueue.isEmpty()) {
            ByteBuffer head = pcmQueue.peek();
            int take = Math.min(rem, head.remaining());
            int savedLim = head.limit();
            head.limit(head.position() + take);
            out.put(head);
            head.limit(savedLim);
            rem -= take;
            if (!head.hasRemaining()) {
                pcmQueue.poll();
                MemoryUtil.memFree(head);
            }
        }
        out.flip();
        pcmTotal -= toDrain;
        return out;
    }

    // ── OGG accumulation ─────────────────────────────────────────────────

    private void drainIncoming() {
        byte[] chunk;
        while ((chunk = incoming.poll()) != null) appendOgg(chunk);
    }

    private void appendOgg(byte[] data) {
        int unconsumed = oggWrite - oggRead;
        // compact if we can't fit new data without growing
        if (oggRead > 0 && unconsumed + data.length > ogg.length - oggRead) {
            if (unconsumed > 0) System.arraycopy(ogg, oggRead, ogg, 0, unconsumed);
            oggWrite = unconsumed;
            oggRead  = 0;
        }
        if (oggWrite + data.length > ogg.length) {
            ogg = Arrays.copyOf(ogg, Math.max(ogg.length * 2, oggWrite + data.length));
        }
        System.arraycopy(data, 0, ogg, oggWrite, data.length);
        oggWrite += data.length;
    }

    private void ensureWorkBuf(int size) {
        if (workBuf.capacity() >= size) return;
        MemoryUtil.memFree(workBuf);
        workBuf = MemoryUtil.memAlloc(size);
    }
}
