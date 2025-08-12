package com.ddbrother.shootingstars.mixin;

import com.ddbrother.shootingstars.items.CustomItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {

    @Shadow
    private BlockState blockState;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onAnvilLandTick(CallbackInfo ci) {
        FallingBlockEntity thisEntity = (FallingBlockEntity) (Object) this;
        World world = thisEntity.getWorld();

        if (world.isClient || !this.blockState.isOf(Blocks.ANVIL)) {
            return;
        }

        BlockPos landingPos = thisEntity.getBlockPos();

        if (world.getBlockState(landingPos.down()).isOf(Blocks.OBSIDIAN)) {
            System.out.println("Obsidian!");

            Box detectionBox = new Box(landingPos.down()).expand(0, 1.0, 0).offset(0, 0.5, 0);
            List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, detectionBox, itemEntity -> true);

            for (ItemEntity itemEntity : items) {
                ItemStack stack = itemEntity.getStack();

                if (stack.isOf(CustomItems.INSTANCE.getSTAR())) {
                    itemEntity.remove(Entity.RemovalReason.DISCARDED);

                    ItemStack shards = new ItemStack(CustomItems.INSTANCE.getSTAR_SHARD(), 6);
                    ItemEntity shardsToSpawn = new ItemEntity(world,
                            landingPos.getX() + 0.5,
                            landingPos.getY(),
                            landingPos.getZ() + 0.5,
                            shards);
                    world.spawnEntity(shardsToSpawn);
                }
            }
        }
    }
}
