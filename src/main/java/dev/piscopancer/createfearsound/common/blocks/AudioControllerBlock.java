package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.client.gui.AudioControllerMenu;
import dev.piscopancer.createfearsound.common.data.AudioLink;
import dev.piscopancer.createfearsound.common.registries.DataComponentsRegistry;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AudioControllerBlock extends Block implements EntityBlock {
  public AudioControllerBlock(Properties properties) {
    super(properties);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new AudioControllerBlockEntity(pos, state);
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
      InteractionHand hand, BlockHitResult hit) {
    if (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof AudioPeripheralBlock) {
      captureLink(stack, pos, player, level);
      return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
      BlockHitResult hit) {
    if (level.isClientSide)
      return InteractionResult.SUCCESS;
    if (!(player instanceof ServerPlayer sp))
      return InteractionResult.PASS;
    if (!(level.getBlockEntity(pos) instanceof AudioControllerBlockEntity be))
      return InteractionResult.PASS;
    List<AudioLink> links = be.getLinks();
    sp.openMenu(new MenuProvider() {
      @Override
      public Component getDisplayName() {
        return Component.translatable("block.createfearsound.audio_controller");
      }

      @Override
      public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
        return new AudioControllerMenu(id, inv, pos, links);
      }
    }, buf -> {
      buf.writeBlockPos(pos);
      AudioLink.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, links);
    });
    return InteractionResult.CONSUME;
  }

  public static void captureLink(ItemStack stack, BlockPos pos, Player player, Level level) {
    if (level.isClientSide)
      return;
    stack.set(DataComponentsRegistry.LINKED_AUDIO_CONTROLLER.get(), pos.immutable());
    player.displayClientMessage(
        Component.translatable("createfearsound.audio_controller.captured", pos.getX(), pos.getY(), pos.getZ()),
        true);
  }
}
