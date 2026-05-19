package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.common.data.TapePlayerLink;
import net.minecraft.world.level.block.Block;

public class ControlBlock extends Block {
  public final TapePlayerLink.LinkType type;

  public ControlBlock(Properties properties, TapePlayerLink.LinkType type) {
    super(properties);
    this.type = type;
  }
}
