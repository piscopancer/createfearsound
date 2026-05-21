package dev.piscopancer.createfearsound.common.registries;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.blocks.AudioControllerBlock;
import dev.piscopancer.createfearsound.common.blocks.AudioPauseBlock;
import dev.piscopancer.createfearsound.common.blocks.AudioPlayBlock;
import dev.piscopancer.createfearsound.common.blocks.AudioSpeakerBlock;
import dev.piscopancer.createfearsound.common.blocks.AudioVolumeBlock;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BlocksRegistry {
  static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(CFS.MODID);

  public static final DeferredBlock<AudioControllerBlock> AUDIO_CONTROLLER = REGISTRY.registerBlock(
      "audio_controller",
      AudioControllerBlock::new,
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.METAL)
          .strength(2.0F)
          .sound(SoundType.METAL));

  public static final DeferredBlock<AudioVolumeBlock> AUDIO_VOLUME = REGISTRY.registerBlock(
      "audio_volume",
      AudioVolumeBlock::new,
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.METAL)
          .strength(1.5F)
          .sound(SoundType.METAL));

  public static final DeferredBlock<AudioPlayBlock> AUDIO_PLAY = REGISTRY.registerBlock(
      "audio_play",
      AudioPlayBlock::new,
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.METAL)
          .strength(1.5F)
          .sound(SoundType.METAL));

  public static final DeferredBlock<AudioPauseBlock> AUDIO_PAUSE = REGISTRY.registerBlock(
      "audio_pause",
      AudioPauseBlock::new,
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.METAL)
          .strength(1.5F)
          .sound(SoundType.METAL));

  public static final DeferredBlock<AudioSpeakerBlock> AUDIO_SPEAKER = REGISTRY.registerBlock(
      "audio_speaker",
      AudioSpeakerBlock::new,
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.METAL)
          .strength(1.5F)
          .sound(SoundType.METAL));

  public static final List<DeferredBlock<? extends Block>> ALL = List.of(
      AUDIO_CONTROLLER, AUDIO_VOLUME, AUDIO_PLAY, AUDIO_PAUSE, AUDIO_SPEAKER);
}
