package dev.piscopancer.createfearsound.common.registries;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.blocks.ControlBlock;
import dev.piscopancer.createfearsound.common.blocks.TapePlayerBlock;
import dev.piscopancer.createfearsound.common.data.TapePlayerLink;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BlocksRegistry {
  static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(CFS.MODID);

  public static final DeferredBlock<TapePlayerBlock> TAPE_PLAYER = REGISTRY.registerBlock(
      "tape_player",
      TapePlayerBlock::new,
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.METAL)
          .strength(2.0F)
          .sound(SoundType.METAL));

  public static final DeferredBlock<ControlBlock> PLAY_BLOCK = REGISTRY.registerBlock(
      "play_block",
      props -> new ControlBlock(props, TapePlayerLink.LinkType.Play),
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.METAL)
          .strength(1.5F)
          .sound(SoundType.METAL));

  public static final DeferredBlock<ControlBlock> PAUSE_BLOCK = REGISTRY.registerBlock(
      "pause_block",
      props -> new ControlBlock(props, TapePlayerLink.LinkType.Pause),
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.METAL)
          .strength(1.5F)
          .sound(SoundType.METAL));

  public static final DeferredBlock<ControlBlock> VOLUME_BLOCK = REGISTRY.registerBlock(
      "volume_block",
      props -> new ControlBlock(props, TapePlayerLink.LinkType.Volume),
      BlockBehaviour.Properties.of()
          .mapColor(MapColor.METAL)
          .strength(1.5F)
          .sound(SoundType.METAL));

  public static final List<DeferredBlock<? extends Block>> ALL = List.of(
      TAPE_PLAYER, PLAY_BLOCK, PAUSE_BLOCK, VOLUME_BLOCK);
}
