/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseFireBlock
extends Block {
    private final float fireDamage;
    protected static final VoxelShape DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    public BaseFireBlock(BlockBehaviour.Properties properties, float f) {
        super(properties);
        this.fireDamage = f;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return BaseFireBlock.getState(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos());
    }

    public static BlockState getState(BlockGetter blockGetter, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.below();
        BlockState blockState = blockGetter.getBlockState(blockPos2);
        if (SoulFireBlock.canSurviveOnBlock(blockState.getBlock())) {
            return Blocks.SOUL_FIRE.defaultBlockState();
        }
        return ((FireBlock)Blocks.FIRE).getStateForPlacement(blockGetter, blockPos);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return DOWN_AABB;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        block12 : {
            double d;
            double d2;
            double d3;
            int n;
            block11 : {
                BlockPos blockPos2;
                BlockState blockState2;
                if (random.nextInt(24) == 0) {
                    level.playLocalSound((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f, false);
                }
                if (!this.canBurn(blockState2 = level.getBlockState(blockPos2 = blockPos.below())) && !blockState2.isFaceSturdy(level, blockPos2, Direction.UP)) break block11;
                for (int i = 0; i < 3; ++i) {
                    double d4 = (double)blockPos.getX() + random.nextDouble();
                    double d5 = (double)blockPos.getY() + random.nextDouble() * 0.5 + 0.5;
                    double d6 = (double)blockPos.getZ() + random.nextDouble();
                    level.addParticle(ParticleTypes.LARGE_SMOKE, d4, d5, d6, 0.0, 0.0, 0.0);
                }
                break block12;
            }
            if (this.canBurn(level.getBlockState(blockPos.west()))) {
                for (n = 0; n < 2; ++n) {
                    d3 = (double)blockPos.getX() + random.nextDouble() * 0.10000000149011612;
                    d2 = (double)blockPos.getY() + random.nextDouble();
                    d = (double)blockPos.getZ() + random.nextDouble();
                    level.addParticle(ParticleTypes.LARGE_SMOKE, d3, d2, d, 0.0, 0.0, 0.0);
                }
            }
            if (this.canBurn(level.getBlockState(blockPos.east()))) {
                for (n = 0; n < 2; ++n) {
                    d3 = (double)(blockPos.getX() + 1) - random.nextDouble() * 0.10000000149011612;
                    d2 = (double)blockPos.getY() + random.nextDouble();
                    d = (double)blockPos.getZ() + random.nextDouble();
                    level.addParticle(ParticleTypes.LARGE_SMOKE, d3, d2, d, 0.0, 0.0, 0.0);
                }
            }
            if (this.canBurn(level.getBlockState(blockPos.north()))) {
                for (n = 0; n < 2; ++n) {
                    d3 = (double)blockPos.getX() + random.nextDouble();
                    d2 = (double)blockPos.getY() + random.nextDouble();
                    d = (double)blockPos.getZ() + random.nextDouble() * 0.10000000149011612;
                    level.addParticle(ParticleTypes.LARGE_SMOKE, d3, d2, d, 0.0, 0.0, 0.0);
                }
            }
            if (this.canBurn(level.getBlockState(blockPos.south()))) {
                for (n = 0; n < 2; ++n) {
                    d3 = (double)blockPos.getX() + random.nextDouble();
                    d2 = (double)blockPos.getY() + random.nextDouble();
                    d = (double)(blockPos.getZ() + 1) - random.nextDouble() * 0.10000000149011612;
                    level.addParticle(ParticleTypes.LARGE_SMOKE, d3, d2, d, 0.0, 0.0, 0.0);
                }
            }
            if (!this.canBurn(level.getBlockState(blockPos.above()))) break block12;
            for (n = 0; n < 2; ++n) {
                d3 = (double)blockPos.getX() + random.nextDouble();
                d2 = (double)(blockPos.getY() + 1) - random.nextDouble() * 0.10000000149011612;
                d = (double)blockPos.getZ() + random.nextDouble();
                level.addParticle(ParticleTypes.LARGE_SMOKE, d3, d2, d, 0.0, 0.0, 0.0);
            }
        }
    }

    protected abstract boolean canBurn(BlockState var1);

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (!entity.fireImmune()) {
            entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
            if (entity.getRemainingFireTicks() == 0) {
                entity.setSecondsOnFire(8);
            }
            entity.hurt(DamageSource.IN_FIRE, this.fireDamage);
        }
        super.entityInside(blockState, level, blockPos, entity);
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        Optional<PortalShape> optional;
        if (blockState2.is(blockState.getBlock())) {
            return;
        }
        if (BaseFireBlock.inPortalDimension(level) && (optional = PortalShape.findEmptyPortalShape(level, blockPos, Direction.Axis.X)).isPresent()) {
            optional.get().createPortalBlocks();
            return;
        }
        if (!blockState.canSurvive(level, blockPos)) {
            level.removeBlock(blockPos, false);
        }
    }

    private static boolean inPortalDimension(Level level) {
        return level.dimension() == Level.OVERWORLD || level.dimension() == Level.NETHER;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        if (!level.isClientSide()) {
            level.levelEvent(null, 1009, blockPos, 0);
        }
    }

    public static boolean canBePlacedAt(Level level, BlockPos blockPos, Direction direction) {
        BlockState blockState = level.getBlockState(blockPos);
        if (!blockState.isAir()) {
            return false;
        }
        return BaseFireBlock.getState(level, blockPos).canSurvive(level, blockPos) || BaseFireBlock.isPortal(level, blockPos, direction);
    }

    private static boolean isPortal(Level level, BlockPos blockPos, Direction direction) {
        if (!BaseFireBlock.inPortalDimension(level)) {
            return false;
        }
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
        boolean bl = false;
        for (Direction direction2 : Direction.values()) {
            if (!level.getBlockState(mutableBlockPos.set(blockPos).move(direction2)).is(Blocks.OBSIDIAN)) continue;
            bl = true;
            break;
        }
        if (!bl) {
            return false;
        }
        Direction.Axis axis = direction.getAxis().isHorizontal() ? direction.getCounterClockWise().getAxis() : Direction.Plane.HORIZONTAL.getRandomAxis(level.random);
        return PortalShape.findEmptyPortalShape(level, blockPos, axis).isPresent();
    }
}

