package dev.piscopancer.createfearsound.server.audio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import javax.annotation.Nullable;

public record AudioMeta(
    String uploadedAt,
    String uploadedBy,
    String source,
    int timesListened,
    @Nullable String lastListenedAt,
    @Nullable String autoTitle
) {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public static Either read(Path file) {
    String json;
    try {
      json = Files.readString(file, StandardCharsets.UTF_8);
    } catch (IOException e) {
      return Either.err("could not read file: " + e.getMessage());
    }

    JsonObject obj;
    try {
      obj = GSON.fromJson(json, JsonObject.class);
    } catch (JsonSyntaxException e) {
      return Either.err("malformed JSON: " + e.getMessage());
    }

    if (obj == null) return Either.err("file is empty or null");

    String uploadedAt = str(obj, "uploadedAt");
    String uploadedBy = str(obj, "uploadedBy");
    String source = str(obj, "source");
    int timesListened = obj.has("timesListened") ? obj.get("timesListened").getAsInt() : 0;
    String lastListenedAt = str(obj, "lastListenedAt");
    String autoTitle = str(obj, "__title");

    AudioMeta meta = new AudioMeta(uploadedAt, uploadedBy, source, timesListened, lastListenedAt, autoTitle);
    Optional<String> err = meta.validate();
    if (err.isPresent()) return Either.err(err.get());
    return Either.ok(meta);
  }

  private static @Nullable String str(JsonObject obj, String key) {
    return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : null;
  }

  private Optional<String> validate() {
    if (uploadedAt == null || uploadedAt.isBlank())
      return Optional.of("missing field: uploadedAt");
    if (uploadedBy == null || uploadedBy.isBlank())
      return Optional.of("missing field: uploadedBy");
    if (source == null || source.isBlank())
      return Optional.of("missing field: source");
    try {
      Instant.parse(uploadedAt);
    } catch (DateTimeParseException e) {
      return Optional.of("uploadedAt is not a valid ISO date: \"" + uploadedAt + "\"");
    }
    if (lastListenedAt != null) {
      try {
        Instant.parse(lastListenedAt);
      } catch (DateTimeParseException e) {
        return Optional.of("lastListenedAt is not a valid ISO date: \"" + lastListenedAt + "\"");
      }
    }
    return Optional.empty();
  }

  public AudioMeta withListen() {
    return new AudioMeta(uploadedAt, uploadedBy, source, timesListened + 1, Instant.now().toString(), autoTitle);
  }

  public void write(Path file) throws IOException {
    JsonObject obj = new JsonObject();
    obj.addProperty("uploadedAt", uploadedAt);
    obj.addProperty("uploadedBy", uploadedBy);
    obj.addProperty("source", source);
    obj.addProperty("timesListened", timesListened);
    if (lastListenedAt != null) obj.addProperty("lastListenedAt", lastListenedAt);
    if (autoTitle != null) obj.addProperty("__title", autoTitle);
    Files.writeString(file, GSON.toJson(obj), StandardCharsets.UTF_8);
  }

  /** Simple Either<AudioMeta, String> — ok = valid meta, err = reason. */
  public sealed interface Either {
    record Ok(AudioMeta meta) implements Either {}
    record Err(String reason) implements Either {}

    static Either ok(AudioMeta meta) { return new Ok(meta); }
    static Either err(String reason) { return new Err(reason); }
  }
}
