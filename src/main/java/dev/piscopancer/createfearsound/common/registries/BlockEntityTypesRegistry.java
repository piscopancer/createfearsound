package dev.piscopancer.createfearsound.common.registries;

import dev.piscopancer.createfearsound.CFS;
import dev.piscopancer.createfearsound.common.blocks.TapePlayerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BlockEntityTypesRegistry {
  static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,
      CFS.MODID);

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TapePlayerBlockEntity>> TAPE_PLAYER = REGISTRY
      .register("tape_player", () -> BlockEntityType.Builder
          .of(TapePlayerBlockEntity::new, BlocksRegistry.TAPE_PLAYER.get())
          .build(null));
}
