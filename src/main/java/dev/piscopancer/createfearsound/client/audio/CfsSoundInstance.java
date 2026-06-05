package dev.piscopancer.createfearsound.client.audio;

import dev.piscopancer.createfearsound.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.CompletableFuture;

public class CfsSoundInstance extends AbstractTickableSoundInstance {
  private static final Sound DUMMY_SOUND = new Sound(
      Util.modResLoc("custom_audio"),
      ConstantFloat.of(1f), ConstantFloat.of(1f),
      1, Sound.Type.FILE, true, false, 16);

  // SoundEngine skips play() when volume == 0; start with a non-zero value so the channel gets created
  private static final float MIN_INITIAL_VOLUME = 0.0001f;

  private static final float REFERENCE_DISTANCE = 4.0f;
  private static final float AIR_ABSORPTION_HALF_DISTANCE = 32.0f;
  private static final float AIR_ABSORPTION_FACTOR = 0.9f;

  private final AudioStream stream;
  private final boolean followsPlayer;
  private final Vec3 sourcePos;
  private boolean stopped = false;

  public CfsSoundInstance(AudioStream stream, Vec3 pos, boolean followsPlayer) {
    super(SoundEvent.createVariableRangeEvent(Util.modResLoc("custom_audio")), SoundSource.RECORDS, SoundInstance.createUnseededRandom());
    this.stream = stream;
    this.followsPlayer = followsPlayer;
    this.sourcePos = pos;
    this.x = pos.x;
    this.y = pos.y;
    this.z = pos.z;
    this.volume = MIN_INITIAL_VOLUME;
    this.attenuation = Attenuation.NONE;
  }

  @Override
  public Sound getSound() { return DUMMY_SOUND; }

  @Override
  public WeighedSoundEvents resolve(net.minecraft.client.sounds.SoundManager soundManager) {
    this.sound = DUMMY_SOUND;
    return new WeighedSoundEvents(Util.modResLoc("custom_audio"), null);
  }

  @Override
  public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
    return CompletableFuture.completedFuture(stream);
  }

  @Override
  public void tick() {
    if (stopped) {
      stop();
      return;
    }
    var player = Minecraft.getInstance().player;
    if (player == null) {
      stop();
      return;
    }
    if (followsPlayer) {
      this.x = player.getX();
      this.y = player.getY();
      this.z = player.getZ();
      this.volume = 1.0f;
    } else {
      double dist = player.getEyePosition().distanceTo(sourcePos);
      float geometric = (float) Math.min(1.0, REFERENCE_DISTANCE / Math.max(dist, 0.1));
      float airAbsorption = (float) Math.pow(AIR_ABSORPTION_FACTOR, dist / AIR_ABSORPTION_HALF_DISTANCE);
      this.volume = geometric * airAbsorption;
    }
  }

  public void requestStop() {
    stopped = true;
  }
}
