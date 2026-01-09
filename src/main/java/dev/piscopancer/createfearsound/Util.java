package dev.piscopancer.createfearsound;

import java.nio.file.Path;
import net.minecraft.resources.ResourceLocation;

public class Util {
  public static Path texturePath(Path subpath) {
    return Path.of("textures", subpath.toString());
  }

  public static Path texturePath(String... subpath) {
    return Path.of("textures", Path.of("", subpath).toString());
  }

  public static ResourceLocation modResLoc(Path subpath) {
    return ResourceLocation.fromNamespaceAndPath(CFS.MODID, subpath.toString());
  }

  public static ResourceLocation modResLoc(String... subpath) {
    return ResourceLocation.fromNamespaceAndPath(CFS.MODID, Path.of("", subpath).toString());
  }
}
