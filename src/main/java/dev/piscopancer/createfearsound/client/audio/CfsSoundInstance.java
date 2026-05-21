package dev.piscopancer.createfearsound.client.audio;

import dev.piscopancer.createfearsound.Util;
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
      1, Sound.Type.FILE, true, false, 0);

  private final AudioStream stream;
  private boolean stopped = false;

  public CfsSoundInstance(AudioStream stream, Vec3 pos) {
    super(SoundEvent.createVariableRangeEvent(Util.modResLoc("custom_audio")), SoundSource.RECORDS, SoundInstance.createUnseededRandom());
    this.stream = stream;
    this.x = pos.x;
    this.y = pos.y;
    this.z = pos.z;
    this.volume = 1.0f;
    this.attenuation = Attenuation.LINEAR;
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
    if (stopped) stop();
  }

  public void requestStop() {
    stopped = true;
  }
}
