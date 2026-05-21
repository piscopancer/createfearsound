package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.data.AudioLink;
import dev.piscopancer.createfearsound.common.registries.DataComponentsRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AudioPeripheralBlock extends Block {
  public AudioPeripheralBlock(Properties properties) {
    super(properties);
  }

  public abstract AudioLink.LinkType getLinkType();

  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(level, pos, state, placer, stack);
    if (level.isClientSide)
      return;
    BlockPos controllerPos = stack.get(DataComponentsRegistry.LINKED_AUDIO_CONTROLLER.get());
    if (controllerPos == null)
      return;
    if (!(level.getBlockEntity(controllerPos) instanceof AudioControllerBlockEntity be))
      return;
    boolean added = be.addLink(new AudioLink(pos.immutable(), getLinkType()));
    if (added && placer instanceof Player p) {
      p.displayClientMessage(
          Component.translatable("createfearsound.audio_controller.attached",
              getLinkType().name(), controllerPos.getX(), controllerPos.getY(), controllerPos.getZ()),
          true);
    }
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
