package dev.piscopancer.createfearsound.common.blocks;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import dev.piscopancer.createfearsound.CFS;
import java.util.List;
import javax.annotation.Nullable;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AudioPeripheralBlockEntity extends BlockEntity implements IHaveGoggleInformation {
  @Nullable
  private BlockPos controllerPos = null;

  public AudioPeripheralBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  protected abstract String getGoggleHeaderKey();

  protected void addMidGoggleLines(List<Component> tooltip, boolean isPlayerSneaking) {}

  @Override
  public final boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
    lang().translate(getGoggleHeaderKey()).style(ChatFormatting.GRAY).forGoggles(tooltip);
    addMidGoggleLines(tooltip, isPlayerSneaking);
    if (controllerPos != null) {
      lang().translate("gui.goggles.audio_peripheral.connected")
          .style(ChatFormatting.GREEN)
          .forGoggles(tooltip, 1);
      lang().text(ChatFormatting.DARK_GRAY,
              controllerPos.getX() + ", " + controllerPos.getY() + ", " + controllerPos.getZ())
          .forGoggles(tooltip, 2);
    } else {
      lang().translate("gui.goggles.audio_peripheral.disconnected")
          .style(ChatFormatting.RED)
          .forGoggles(tooltip, 1);
    }
    return true;
  }

  protected static LangBuilder lang() {
    return new LangBuilder(CFS.MODID);
  }

  @Nullable
  public BlockPos getControllerPos() {
    return controllerPos;
  }

  public void setControllerPos(@Nullable BlockPos pos) {
    this.controllerPos = pos == null ? null : pos.immutable();
    setChanged();
    if (level != null && !level.isClientSide)
      level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    if (controllerPos != null)
      tag.put("controller", NbtUtils.writeBlockPos(controllerPos));
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    controllerPos = tag.contains("controller")
        ? NbtUtils.readBlockPos(tag, "controller").orElse(null)
        : null;
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = super.getUpdateTag(registries);
    saveAdditional(tag, registries);
    return tag;
  }

  @Override
  public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
    loadAdditional(tag, registries);
  }

  @Override
  @Nullable
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }
}
