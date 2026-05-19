package dev.piscopancer.createfearsound.common.blocks;

import com.mojang.serialization.Codec;
import dev.piscopancer.createfearsound.common.data.TapePlayerLink;
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

public class TapePlayerBlockEntity extends BlockEntity {
  private static final String LINKS_KEY = "links";
  private static final Codec<List<TapePlayerLink>> LINKS_CODEC = TapePlayerLink.CODEC.listOf();

  private List<TapePlayerLink> links = new ArrayList<>();

  public TapePlayerBlockEntity(BlockPos pos, BlockState blockState) {
    super(BlockEntityTypesRegistry.TAPE_PLAYER.get(), pos, blockState);
  }

  public List<TapePlayerLink> getLinks() {
    return Collections.unmodifiableList(links);
  }

  public void setLinks(List<TapePlayerLink> newLinks) {
    this.links = new ArrayList<>(newLinks);
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    LINKS_CODEC.encodeStart(NbtOps.INSTANCE, links)
        .resultOrPartial(err -> {
        })
        .ifPresent(t -> tag.put(LINKS_KEY, t));
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
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
