/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.core.dispenser;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;

public class ShearsDispenseItemBehavior
extends OptionalDispenseItemBehavior {
    @Override
    protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
        ServerLevel serverLevel = blockSource.getLevel();
        if (!serverLevel.isClientSide()) {
            BlockPos blockPos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
            this.setSuccess(ShearsDispenseItemBehavior.tryShearBeehive(serverLevel, blockPos) || ShearsDispenseItemBehavior.tryShearLivingEntity(serverLevel, blockPos));
            if (this.isSuccess() && itemStack.hurt(1, serverLevel.getRandom(), null)) {
                itemStack.setCount(0);
            }
        }
        return itemStack;
    }

    private static boolean tryShearBeehive(ServerLevel serverLevel, BlockPos blockPos) {
        int n;
        BlockState blockState = serverLevel.getBlockState(blockPos);
        if (blockState.is(BlockTags.BEEHIVES) && (n = blockState.getValue(BeehiveBlock.HONEY_LEVEL).intValue()) >= 5) {
            serverLevel.playSound(null, blockPos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0f, 1.0f);
            BeehiveBlock.dropHoneycomb(serverLevel, blockPos);
            ((BeehiveBlock)blockState.getBlock()).releaseBeesAndResetHoneyLevel(serverLevel, blockState, blockPos, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
            return true;
        }
        return false;
    }

    private static boolean tryShearLivingEntity(ServerLevel serverLevel, BlockPos blockPos) {
        List<Entity> list = serverLevel.getEntitiesOfClass(LivingEntity.class, new AABB(blockPos), EntitySelector.NO_SPECTATORS);
        for (LivingEntity livingEntity : list) {
            Shearable shearable;
            if (!(livingEntity instanceof Shearable) || !(shearable = (Shearable)((Object)livingEntity)).readyForShearing()) continue;
            shearable.shear(SoundSource.BLOCKS);
            return true;
        }
        return false;
    }
}

