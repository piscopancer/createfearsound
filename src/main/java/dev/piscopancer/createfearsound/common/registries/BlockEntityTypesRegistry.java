package dev.piscopancer.createfearsound.common.registries;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.blocks.AudioControllerBlockEntity;
import dev.piscopancer.createfearsound.common.blocks.AudioPauseBlockEntity;
import dev.piscopancer.createfearsound.common.blocks.AudioPlayBlockEntity;
import dev.piscopancer.createfearsound.common.blocks.AudioSpeakerBlockEntity;
import dev.piscopancer.createfearsound.common.blocks.AudioVolumeBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BlockEntityTypesRegistry {
  static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,
      CFS.MODID);

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AudioControllerBlockEntity>> AUDIO_CONTROLLER = REGISTRY
      .register("audio_controller", () -> BlockEntityType.Builder
          .of(AudioControllerBlockEntity::new, BlocksRegistry.AUDIO_CONTROLLER.get())
          .build(null));

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AudioVolumeBlockEntity>> AUDIO_VOLUME = REGISTRY
      .register("audio_volume", () -> BlockEntityType.Builder
          .of(AudioVolumeBlockEntity::new, BlocksRegistry.AUDIO_VOLUME.get())
          .build(null));

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AudioPlayBlockEntity>> AUDIO_PLAY = REGISTRY
      .register("audio_play", () -> BlockEntityType.Builder
          .of(AudioPlayBlockEntity::new, BlocksRegistry.AUDIO_PLAY.get())
          .build(null));

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AudioPauseBlockEntity>> AUDIO_PAUSE = REGISTRY
      .register("audio_pause", () -> BlockEntityType.Builder
          .of(AudioPauseBlockEntity::new, BlocksRegistry.AUDIO_PAUSE.get())
          .build(null));

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AudioSpeakerBlockEntity>> AUDIO_SPEAKER = REGISTRY
      .register("audio_speaker", () -> BlockEntityType.Builder
          .of(AudioSpeakerBlockEntity::new, BlocksRegistry.AUDIO_SPEAKER.get())
          .build(null));
}
