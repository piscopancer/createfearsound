package dev.piscopancer.createfearsound.client.gui;

import dev.piscopancer.createfearsound.common.data.TapePlayerLink;
import dev.piscopancer.createfearsound.common.registries.MenuTypesRegistry;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class TapePlayerMenu extends AbstractContainerMenu {
  private final BlockPos pos;
  private final List<TapePlayerLink> links;

  public TapePlayerMenu(int containerId, Inventory inv, BlockPos pos, List<TapePlayerLink> links) {
    super(MenuTypesRegistry.TAPE_PLAYER_MENU.get(), containerId);
    this.pos = pos;
    this.links = List.copyOf(links);
  }

  public TapePlayerMenu(int containerId, Inventory inv, RegistryFriendlyByteBuf buf) {
    this(containerId, inv, buf.readBlockPos(),
        TapePlayerLink.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf));
  }

  public BlockPos getPos() {
    return pos;
  }

  public List<TapePlayerLink> getLinks() {
    return links;
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean stillValid(Player player) {
    return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64.0D;
  }
}
