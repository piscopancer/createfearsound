package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.common.registries.BlockEntityTypesRegistry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class AudioVolumeBlockEntity extends AudioPeripheralBlockEntity {
  public AudioVolumeBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntityTypesRegistry.AUDIO_VOLUME.get(), pos, state);
  }

  @Override
  protected String getGoggleHeaderKey() {
    return "gui.goggles.audio_volume";
  }

  @Override
  protected void addMidGoggleLines(List<Component> tooltip, boolean isPlayerSneaking) {
    BlockPos cpos = getControllerPos();
    if (cpos == null || level == null) return;
    if (!(level.getBlockEntity(cpos) instanceof AudioControllerBlockEntity cbe)) return;
    lang().translate("gui.goggles.audio_volume.volume")
        .style(ChatFormatting.GRAY)
        .add(lang().text(ChatFormatting.GOLD, " " + cbe.getVolume() + "/15"))
        .forGoggles(tooltip, 1);
  }
}
