package dev.piscopancer.createfearsound.common.blocks;

import dev.piscopancer.createfearsound.client.gui.AudioControllerMenu;
import dev.piscopancer.createfearsound.common.data.AudioLink;
import dev.piscopancer.createfearsound.server.payloads.SetPendingLinkPayload;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

public class AudioControllerBlock extends Block implements EntityBlock {

  public static final Map<UUID, BlockPos> PENDING_LINKS = new HashMap<>();

  public AudioControllerBlock(Properties properties) {
    super(properties);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new AudioControllerBlockEntity(pos, state);
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

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof AudioControllerBlockEntity be) {
      for (var link : be.getLinks()) {
        if (level.getBlockEntity(link.pos()) instanceof AudioPeripheralBlockEntity pbe)
          pbe.setControllerPos(null);
      }
    }
    super.onRemove(state, level, pos, newState, movedByPiston);
  }

  public static void captureLink(BlockPos pos, Player player, Level level) {
    if (level.isClientSide)
      return;
    PENDING_LINKS.put(player.getUUID(), pos.immutable());
    player.displayClientMessage(
        Component.translatable("createfearsound.audio_controller.captured", pos.getX(), pos.getY(), pos.getZ()),
        true);
    if (player instanceof ServerPlayer sp) {
      PacketDistributor.sendToPlayer(sp, new SetPendingLinkPayload(Optional.of(pos.immutable())));
    }
  }
}
