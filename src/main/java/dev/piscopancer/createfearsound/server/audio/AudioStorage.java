package dev.piscopancer.createfearsound.server.audio;

import dev.piscopancer.createfearsound.CFS;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@EventBusSubscriber(modid = CFS.MODID, bus = EventBusSubscriber.Bus.GAME)
public class AudioStorage {

  /** audioFile is null when the OGG has been evicted to save disk space. */
  public record AudioEntry(UUID id, String name, @Nullable Path audioFile, AudioMeta meta, @Nullable OggInfo oggInfo) {
    public boolean isCached() { return audioFile != null && Files.exists(audioFile); }
  }

  public record InvalidEntry(UUID id, String reason) {}

  private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
    Thread t = new Thread(r, "cfs-audio-download");
    t.setDaemon(true);
    return t;
  });

  private static Path storageDir;
  private static final Map<UUID, AudioEntry> entries = new ConcurrentHashMap<>();
  private static final Map<UUID, String> invalid = new ConcurrentHashMap<>();

  @SubscribeEvent
  static void onServerStarting(ServerStartingEvent event) {
    storageDir = event.getServer().getWorldPath(LevelResource.ROOT).resolve("cfs-audio");
    entries.clear();
    invalid.clear();
    try {
      Files.createDirectories(storageDir);
      scanExisting();
      pruneStale();
    } catch (IOException e) {
      CFS.LOGGER.error("[CFS] Failed to init audio storage", e);
    }
  }

  private static void scanExisting() throws IOException {
    try (Stream<Path> dirs = Files.list(storageDir)) {
      dirs.filter(Files::isDirectory).forEach(dir -> {
        UUID id;
        try {
          id = UUID.fromString(dir.getFileName().toString());
        } catch (IllegalArgumentException ignored) {
          return;
        }

        Path metaFile = dir.resolve("meta.json");
        if (!Files.exists(metaFile)) {
          invalid.put(id, "meta.json missing");
          return;
        }

        AudioMeta meta = switch (AudioMeta.read(metaFile)) {
          case AudioMeta.Either.Ok ok -> ok.meta();
          case AudioMeta.Either.Err err -> {
            invalid.put(id, "meta.json invalid: " + err.reason());
            yield null;
          }
        };
        if (meta == null) return;

        String name = meta.autoTitle() != null && !meta.autoTitle().isBlank() ? meta.autoTitle() : "unknown";
        Path audioFile = dir.resolve("audio.ogg");
        boolean hasAudio = Files.exists(audioFile);
        OggInfo oggInfo = hasAudio ? OggInfo.scan(audioFile).orElse(null) : null;
        entries.put(id, new AudioEntry(id, name, hasAudio ? audioFile : null, meta, oggInfo));
        CFS.LOGGER.info("[CFS] Loaded: {} ({}) {}", name, id, hasAudio ? "" : "[evicted]");
      });
    }
    CFS.LOGGER.info("[CFS] Audio storage ready: {} track(s), {} invalid", entries.size(), invalid.size());
  }

  public static CompletableFuture<AudioEntry> download(String url, String playerName) {
    return CompletableFuture.supplyAsync(() -> {
      UUID id = UUID.randomUUID();
      Path entryDir = storageDir.resolve(id.toString());
      try {
        Files.createDirectories(entryDir);

        // 1. download — yt-dlp saves as "%(title)s.ogg"
        Path tempFile = YtDlp.download(url, entryDir);
        String name = stripExt(tempFile.getFileName().toString());

        // 2. rename to stable filename
        Path audioFile = entryDir.resolve("audio.ogg");
        Files.move(tempFile, audioFile, StandardCopyOption.REPLACE_EXISTING);

        if (Files.size(audioFile) == 0)
          throw new RuntimeException("downloaded file is empty");

        // 3. write meta with title
        AudioMeta meta = new AudioMeta(
            Instant.now().toString(), playerName,
            URLDecoder.decode(url, StandardCharsets.UTF_8),
            0, null, name);
        meta.write(entryDir.resolve("meta.json"));

        OggInfo oggInfo = OggInfo.scan(audioFile).orElse(null);
        AudioEntry entry = new AudioEntry(id, name, audioFile, meta, oggInfo);
        entries.put(id, entry);
        CFS.LOGGER.info("[CFS] Downloaded: {} ({})", name, id);
        return entry;
      } catch (Exception e) {
        try { deleteDir(entryDir); } catch (IOException ignored) {}
        throw new RuntimeException(e.getMessage(), e);
      }
    }, EXECUTOR);
  }

  public static CompletableFuture<AudioEntry> redownload(UUID id) {
    return CompletableFuture.supplyAsync(() -> {
      AudioEntry entry = entries.get(id);
      if (entry == null) throw new RuntimeException("Entry not found: " + id);
      Path entryDir = storageDir.resolve(id.toString());
      try {
        Path tempFile = YtDlp.download(entry.meta().source(), entryDir);
        String downloadedName = stripExt(tempFile.getFileName().toString());
        Path audioFile = entryDir.resolve("audio.ogg");
        Files.move(tempFile, audioFile, StandardCopyOption.REPLACE_EXISTING);

        // Update meta title if it was missing (old entries without title)
        AudioMeta meta = entry.meta();
        if (meta.autoTitle() == null || meta.autoTitle().isBlank()) {
          meta = new AudioMeta(meta.uploadedAt(), meta.uploadedBy(), meta.source(),
              meta.timesListened(), meta.lastListenedAt(), downloadedName);
          meta.write(entryDir.resolve("meta.json"));
        }

        String name = meta.autoTitle() != null && !meta.autoTitle().isBlank() ? meta.autoTitle() : downloadedName;
        OggInfo oggInfo = OggInfo.scan(audioFile).orElse(null);
        AudioEntry updated = new AudioEntry(id, name, audioFile, meta, oggInfo);
        entries.put(id, updated);
        CFS.LOGGER.info("[CFS] Re-downloaded: {} ({})", name, id);
        return updated;
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }, EXECUTOR);
  }

  public static Collection<AudioEntry> all() {
    return Collections.unmodifiableCollection(entries.values());
  }

  public static Collection<InvalidEntry> allInvalid() {
    return invalid.entrySet().stream()
        .map(e -> new InvalidEntry(e.getKey(), e.getValue()))
        .toList();
  }

  public static Optional<AudioEntry> findByUrl(String url) {
    return entries.values().stream()
        .filter(e -> e.meta().source().equals(url))
        .findFirst();
  }

  public static Optional<AudioEntry> find(String idOrName) {
    try {
      return Optional.ofNullable(entries.get(UUID.fromString(idOrName)));
    } catch (IllegalArgumentException ignored) {}
    return entries.values().stream()
        .filter(e -> e.name().equalsIgnoreCase(idOrName))
        .findFirst();
  }

  public static void recordListen(UUID id) {
    AudioEntry entry = entries.get(id);
    if (entry == null) return;
    AudioMeta updated = entry.meta().withListen();
    entries.put(id, new AudioEntry(entry.id(), entry.name(), entry.audioFile(), updated, entry.oggInfo()));
    try {
      updated.write(storageDir.resolve(id.toString()).resolve("meta.json"));
    } catch (IOException e) {
      CFS.LOGGER.warn("[CFS] Failed to persist listen stats for {}", id, e);
    }
  }

  private static void pruneStale() {
    Instant cutoff = Instant.now().minus(24, ChronoUnit.HOURS);
    List<UUID> toEvict = entries.values().stream()
        .filter(AudioEntry::isCached)
        .filter(e -> {
          String ref = e.meta().lastListenedAt() != null ? e.meta().lastListenedAt() : e.meta().uploadedAt();
          try {
            return Instant.parse(ref).isBefore(cutoff);
          } catch (DateTimeParseException ignored) {
            return false;
          }
        })
        .map(AudioEntry::id)
        .toList();
    toEvict.forEach(id -> {
      AudioEntry e = entries.get(id);
      CFS.LOGGER.info("[CFS] Evicting stale audio: {} ({})", e != null ? e.name() : "?", id);
      evictAudio(id);
    });
    if (!toEvict.isEmpty())
      CFS.LOGGER.info("[CFS] Evicted {} stale track(s)", toEvict.size());
  }

  private static void evictAudio(UUID id) {
    AudioEntry entry = entries.get(id);
    if (entry == null) return;
    if (entry.audioFile() != null) {
      try {
        Files.deleteIfExists(entry.audioFile());
      } catch (IOException e) {
        CFS.LOGGER.warn("[CFS] Failed to evict audio for {}: {}", id, e.getMessage());
      }
    }
    entries.put(id, new AudioEntry(entry.id(), entry.name(), null, entry.meta(), null));
  }

  public static boolean delete(UUID id) {
    entries.remove(id);
    invalid.remove(id);
    try {
      deleteDir(storageDir.resolve(id.toString()));
      return true;
    } catch (IOException e) {
      CFS.LOGGER.error("[CFS] Failed to delete {}", id, e);
      return false;
    }
  }

  private static String stripExt(String filename) {
    int dot = filename.lastIndexOf('.');
    return dot > 0 ? filename.substring(0, dot) : filename;
  }

  private static void deleteDir(Path dir) throws IOException {
    if (!Files.exists(dir)) return;
    try (Stream<Path> files = Files.walk(dir)) {
      files.sorted(Comparator.reverseOrder()).forEach(p -> {
        try { Files.delete(p); } catch (IOException e) {
          CFS.LOGGER.warn("[CFS] delete {}: {}", p, e.getMessage());
        }
      });
    }
  }
}
