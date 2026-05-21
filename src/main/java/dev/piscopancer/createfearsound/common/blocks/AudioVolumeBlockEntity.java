package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.common.registries.BlockEntityTypesRegistry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class AudioVolumeBlockEntity extends AudioPeripheralBlockEntity {
  private float volume = 1.0f;

  public AudioVolumeBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntityTypesRegistry.AUDIO_VOLUME.get(), pos, state);
  }

  public float getVolume() {
    return volume;
  }

  @Override
  protected String getGoggleHeaderKey() {
    return "gui.goggles.audio_volume";
  }

  @Override
  protected void addMidGoggleLines(List<Component> tooltip, boolean isPlayerSneaking) {
    lang().translate("gui.goggles.audio_volume.volume")
        .style(ChatFormatting.GRAY)
        .add(lang().text(ChatFormatting.GOLD, " " + Math.round(volume * 100) + "%"))
        .forGoggles(tooltip, 1);
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putFloat("volume", volume);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    volume = tag.contains("volume") ? tag.getFloat("volume") : 1.0f;
  }
}
