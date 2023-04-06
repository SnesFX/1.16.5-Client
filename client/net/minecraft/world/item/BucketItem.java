/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BucketItem
extends Item {
    private final Fluid content;

    public BucketItem(Fluid fluid, Item.Properties properties) {
        super(properties);
        this.content = fluid;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        BlockHitResult blockHitResult = BucketItem.getPlayerPOVHitResult(level, player, this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        if (((HitResult)blockHitResult).getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemStack);
        }
        if (((HitResult)blockHitResult).getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos;
            BlockHitResult blockHitResult2 = blockHitResult;
            BlockPos blockPos2 = blockHitResult2.getBlockPos();
            Direction direction = blockHitResult2.getDirection();
            BlockPos blockPos3 = blockPos2.relative(direction);
            if (!level.mayInteract(player, blockPos2) || !player.mayUseItemAt(blockPos3, direction, itemStack)) {
                return InteractionResultHolder.fail(itemStack);
            }
            if (this.content == Fluids.EMPTY) {
                Fluid fluid;
                BlockState blockState = level.getBlockState(blockPos2);
                if (blockState.getBlock() instanceof BucketPickup && (fluid = ((BucketPickup)((Object)blockState.getBlock())).takeLiquid(level, blockPos2, blockState)) != Fluids.EMPTY) {
                    player.awardStat(Stats.ITEM_USED.get(this));
                    player.playSound(fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL, 1.0f, 1.0f);
                    ItemStack itemStack2 = ItemUtils.createFilledResult(itemStack, player, new ItemStack(fluid.getBucket()));
                    if (!level.isClientSide) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, new ItemStack(fluid.getBucket()));
                    }
                    return InteractionResultHolder.sidedSuccess(itemStack2, level.isClientSide());
                }
                return InteractionResultHolder.fail(itemStack);
            }
            BlockState blockState = level.getBlockState(blockPos2);
            BlockPos blockPos4 = blockPos = blockState.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER ? blockPos2 : blockPos3;
            if (this.emptyBucket(player, level, blockPos, blockHitResult2)) {
                this.checkExtraContent(level, itemStack, blockPos);
                if (player instanceof ServerPlayer) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockPos, itemStack);
                }
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.sidedSuccess(this.getEmptySuccessItem(itemStack, player), level.isClientSide());
            }
            return InteractionResultHolder.fail(itemStack);
        }
        return InteractionResultHolder.pass(itemStack);
    }

    protected ItemStack getEmptySuccessItem(ItemStack itemStack, Player player) {
        if (!player.abilities.instabuild) {
            return new ItemStack(Items.BUCKET);
        }
        return itemStack;
    }

    public void checkExtraContent(Level level, ItemStack itemStack, BlockPos blockPos) {
    }

    public boolean emptyBucket(@Nullable Player player, Level level, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
        boolean bl;
        if (!(this.content instanceof FlowingFluid)) {
            return false;
        }
        BlockState blockState = level.getBlockState(blockPos);
        Block block = blockState.getBlock();
        Material material = blockState.getMaterial();
        boolean bl2 = blockState.canBeReplaced(this.content);
        boolean bl3 = bl = blockState.isAir() || bl2 || block instanceof LiquidBlockContainer && ((LiquidBlockContainer)((Object)block)).canPlaceLiquid(level, blockPos, blockState, this.content);
        if (!bl) {
            return blockHitResult != null && this.emptyBucket(player, level, blockHitResult.getBlockPos().relative(blockHitResult.getDirection()), null);
        }
        if (level.dimensionType().ultraWarm() && this.content.is(FluidTags.WATER)) {
            int n = blockPos.getX();
            int n2 = blockPos.getY();
            int n3 = blockPos.getZ();
            level.playSound(player, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (level.random.nextFloat() - level.random.nextFloat()) * 0.8f);
            for (int i = 0; i < 8; ++i) {
                level.addParticle(ParticleTypes.LARGE_SMOKE, (double)n + Math.random(), (double)n2 + Math.random(), (double)n3 + Math.random(), 0.0, 0.0, 0.0);
            }
            return true;
        }
        if (block instanceof LiquidBlockContainer && this.content == Fluids.WATER) {
            ((LiquidBlockContainer)((Object)block)).placeLiquid(level, blockPos, blockState, ((FlowingFluid)this.content).getSource(false));
            this.playEmptySound(player, level, blockPos);
            return true;
        }
        if (!level.isClientSide && bl2 && !material.isLiquid()) {
            level.destroyBlock(blockPos, true);
        }
        if (level.setBlock(blockPos, this.content.defaultFluidState().createLegacyBlock(), 11) || blockState.getFluidState().isSource()) {
            this.playEmptySound(player, level, blockPos);
            return true;
        }
        return false;
    }

    protected void playEmptySound(@Nullable Player player, LevelAccessor levelAccessor, BlockPos blockPos) {
        SoundEvent soundEvent = this.content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        levelAccessor.playSound(player, blockPos, soundEvent, SoundSource.BLOCKS, 1.0f, 1.0f);
    }
}

