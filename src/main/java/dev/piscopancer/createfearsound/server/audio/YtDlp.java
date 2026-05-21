package dev.piscopancer.createfearsound.server.audio;

import dev.piscopancer.createfearsound.CFS;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public final class YtDlp {

  public static boolean isAvailable() {
    try {
      return new ProcessBuilder("yt-dlp", "--version")
          .redirectErrorStream(true)
          .start()
          .waitFor() == 0;
    } catch (Exception e) {
      return false;
    }
  }

  public static Path download(String url, Path outputDir) throws Exception {
    List<String> cmd = List.of(
        "yt-dlp",
        "--extract-audio",
        "--audio-format", "vorbis",
        "--audio-quality", "0",
        "--no-playlist",
        "--output", outputDir.resolve("%(title)s.%(ext)s").toString(),
        url
    );

    Process process = new ProcessBuilder(cmd)
        .redirectErrorStream(true)
        .start();

    String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    int exitCode = process.waitFor();

    CFS.LOGGER.info("[yt-dlp] {}", output.trim());

    if (exitCode != 0)
      throw new RuntimeException("yt-dlp exited with code " + exitCode + "\n" + output);

    try (Stream<Path> files = Files.list(outputDir)) {
      return files
          .filter(f -> f.getFileName().toString().endsWith(".ogg"))
          .findFirst()
          .orElseThrow(() -> new RuntimeException("yt-dlp succeeded but no .ogg found in " + outputDir));
    }
  }
}
