package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.common.data.AudioLink;

public class AudioPlayBlock extends AudioPeripheralBlock {
  public AudioPlayBlock(Properties properties) {
    super(properties);
  }

  @Override
  public AudioLink.LinkType getLinkType() {
    return AudioLink.LinkType.Play;
  }
}
