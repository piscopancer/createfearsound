package dev.piscopancer.createfearsound.client;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.blocks.AudioPeripheralBlock;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = CFS.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class CFSClientEvents {

  public static BlockPos pendingControllerPos = null;

  @SubscribeEvent
  static void onClientTick(ClientTickEvent.Pre event) {
    Minecraft mc = Minecraft.getInstance();
    if (mc.player == null || mc.level == null)
      return;
    boolean holdingPeripheral = false;
    for (InteractionHand hand : InteractionHand.values()) {
      ItemStack stack = mc.player.getItemInHand(hand);
      if (!(stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof AudioPeripheralBlock))
        continue;
      holdingPeripheral = true;
      if (pendingControllerPos == null)
        break;
      Outliner.getInstance()
          .showAABB("cfs_controller_link_" + hand.name(), new AABB(pendingControllerPos))
          .colored(0xFFD580)
          .lineWidth(1 / 16f);
      break;
    }
    if (!holdingPeripheral) {
      pendingControllerPos = null;
    }
  }
}
