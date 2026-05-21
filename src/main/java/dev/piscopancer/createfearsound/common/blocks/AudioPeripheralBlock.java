package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.data.AudioLink;
import dev.piscopancer.createfearsound.server.payloads.SetPendingLinkPayload;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class AudioPeripheralBlock extends Block {
  public AudioPeripheralBlock(Properties properties) {
    super(properties);
  }

  public abstract AudioLink.LinkType getLinkType();

  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(level, pos, state, placer, stack);
    if (level.isClientSide || !(placer instanceof Player player))
      return;
    BlockPos controllerPos = AudioControllerBlock.PENDING_LINKS.remove(player.getUUID());
    if (controllerPos == null)
      return;
    if (!(level.getBlockEntity(controllerPos) instanceof AudioControllerBlockEntity be))
      return;
    boolean added = be.addLink(new AudioLink(pos.immutable(), getLinkType()));
    if (level.getBlockEntity(pos) instanceof AudioPeripheralBlockEntity pbe)
      pbe.setControllerPos(controllerPos);
    if (player instanceof ServerPlayer sp) {
      PacketDistributor.sendToPlayer(sp, new SetPendingLinkPayload(Optional.empty()));
      if (added) {
        sp.displayClientMessage(
            Component.translatable("createfearsound.audio_controller.attached",
                getLinkType().name(), controllerPos.getX(), controllerPos.getY(), controllerPos.getZ()),
            true);
      }
    }
  }

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof AudioPeripheralBlockEntity pbe) {
      BlockPos controllerPos = pbe.getControllerPos();
      if (controllerPos != null && level.getBlockEntity(controllerPos) instanceof AudioControllerBlockEntity cbe)
        cbe.removeLink(pos);
    }
    super.onRemove(state, level, pos, newState, movedByPiston);
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
      BlockHitResult hit) {
    if (!level.isClientSide) {
      CFS.LOGGER.info("[{}] right-clicked at {} by {}", getLinkType().name(), pos, player.getName().getString());
      player.displayClientMessage(
          Component.translatable("createfearsound.audio_peripheral.clicked",
              getLinkType().name(), pos.getX(), pos.getY(), pos.getZ()),
          true);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }
}
