package dev.piscopancer.createfearsound.common.blocks;

import com.mojang.serialization.Codec;
import dev.piscopancer.createfearsound.common.data.AudioLink;
import dev.piscopancer.createfearsound.common.registries.BlockEntityTypesRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AudioControllerBlockEntity extends BlockEntity {
  private static final String LINKS_KEY = "links";
  private static final Codec<List<AudioLink>> LINKS_CODEC = AudioLink.CODEC.listOf();

  private List<AudioLink> links = new ArrayList<>();
  private int volume = 0;

  public AudioControllerBlockEntity(BlockPos pos, BlockState blockState) {
    super(BlockEntityTypesRegistry.AUDIO_CONTROLLER.get(), pos, blockState);
  }

  public List<AudioLink> getLinks() {
    return Collections.unmodifiableList(links);
  }

  public int getVolume() {
    return volume;
  }

  public void setVolume(int volume) {
    this.volume = Math.clamp(volume, 0, 15);
    syncLinksChange();
  }

  public boolean addLink(AudioLink link) {
    if (this.links.stream().anyMatch(l -> l.pos().equals(link.pos())))
      return false;
    this.links.add(link);
    syncLinksChange();
    return true;
  }

  public boolean removeLink(BlockPos pos) {
    boolean removed = this.links.removeIf(l -> l.pos().equals(pos));
    if (removed)
      syncLinksChange();
    return removed;
  }

  private void syncLinksChange() {
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putInt("volume", volume);
    LINKS_CODEC.encodeStart(NbtOps.INSTANCE, links)
        .resultOrPartial(err -> {
        })
        .ifPresent(t -> tag.put(LINKS_KEY, t));
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    volume = tag.contains("volume") ? Math.clamp(tag.getInt("volume"), 0, 15) : 0;
    if (tag.contains(LINKS_KEY)) {
      Tag linksTag = tag.get(LINKS_KEY);
      LINKS_CODEC.parse(NbtOps.INSTANCE, linksTag)
          .resultOrPartial(err -> {
          })
          .ifPresent(parsed -> this.links = new ArrayList<>(parsed));
    }
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
