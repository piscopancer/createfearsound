package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.client.gui.TapePlayerMenu;
import dev.piscopancer.createfearsound.common.data.TapePlayerLink;
import dev.piscopancer.createfearsound.common.registries.DataComponentsRegistry;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TapePlayerBlock extends Block implements EntityBlock {
  public TapePlayerBlock(Properties properties) {
    super(properties);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TapePlayerBlockEntity(pos, state);
  }

  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(level, pos, state, placer, stack);
    if (level.isClientSide)
      return;
    List<TapePlayerLink> links = stack.get(DataComponentsRegistry.TAPE_PLAYER_LINKS.get());
    if (links == null || links.isEmpty())
      return;
    if (level.getBlockEntity(pos) instanceof TapePlayerBlockEntity be) {
      be.setLinks(links);
    }
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
      BlockHitResult hit) {
    if (level.isClientSide)
      return InteractionResult.SUCCESS;
    if (!(player instanceof ServerPlayer sp))
      return InteractionResult.PASS;
    if (!(level.getBlockEntity(pos) instanceof TapePlayerBlockEntity be))
      return InteractionResult.PASS;
    List<TapePlayerLink> links = be.getLinks();
    sp.openMenu(new MenuProvider() {
      @Override
      public Component getDisplayName() {
        return Component.translatable("block.createfearsound.tape_player");
      }

      @Override
      public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
        return new TapePlayerMenu(id, inv, pos, links);
      }
    }, buf -> {
      buf.writeBlockPos(pos);
      TapePlayerLink.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, links);
    });
    return InteractionResult.CONSUME;
  }
}
