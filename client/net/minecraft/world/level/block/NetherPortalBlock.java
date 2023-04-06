/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NetherPortalBlock
extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public NetherPortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AXIS, Direction.Axis.X));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        switch (blockState.getValue(AXIS)) {
            case Z: {
                return Z_AXIS_AABB;
            }
        }
        return X_AXIS_AABB;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        if (serverLevel.dimensionType().natural() && serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && random.nextInt(2000) < serverLevel.getDifficulty().getId()) {
            ZombifiedPiglin zombifiedPiglin;
            while (serverLevel.getBlockState(blockPos).is(this)) {
                blockPos = blockPos.below();
            }
            if (serverLevel.getBlockState(blockPos).isValidSpawn(serverLevel, blockPos, EntityType.ZOMBIFIED_PIGLIN) && (zombifiedPiglin = EntityType.ZOMBIFIED_PIGLIN.spawn(serverLevel, null, null, null, blockPos.above(), MobSpawnType.STRUCTURE, false, false)) != null) {
                zombifiedPiglin.setPortalCooldown();
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        boolean bl;
        Direction.Axis axis = direction.getAxis();
        Direction.Axis axis2 = blockState.getValue(AXIS);
        boolean bl2 = bl = axis2 != axis && axis.isHorizontal();
        if (bl || blockState2.is(this) || new PortalShape(levelAccessor, blockPos, axis2).isComplete()) {
            return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (!entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions()) {
            entity.handleInsidePortal(blockPos);
        }
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        if (random.nextInt(100) == 0) {
            level.playLocalSound((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5f, random.nextFloat() * 0.4f + 0.8f, false);
        }
        for (int i = 0; i < 4; ++i) {
            double d = (double)blockPos.getX() + random.nextDouble();
            double d2 = (double)blockPos.getY() + random.nextDouble();
            double d3 = (double)blockPos.getZ() + random.nextDouble();
            double d4 = ((double)random.nextFloat() - 0.5) * 0.5;
            double d5 = ((double)random.nextFloat() - 0.5) * 0.5;
            double d6 = ((double)random.nextFloat() - 0.5) * 0.5;
            int n = random.nextInt(2) * 2 - 1;
            if (level.getBlockState(blockPos.west()).is(this) || level.getBlockState(blockPos.east()).is(this)) {
                d3 = (double)blockPos.getZ() + 0.5 + 0.25 * (double)n;
                d6 = random.nextFloat() * 2.0f * (float)n;
            } else {
                d = (double)blockPos.getX() + 0.5 + 0.25 * (double)n;
                d4 = random.nextFloat() * 2.0f * (float)n;
            }
            level.addParticle(ParticleTypes.PORTAL, d, d2, d3, d4, d5, d6);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                switch (blockState.getValue(AXIS)) {
                    case X: {
                        return (BlockState)blockState.setValue(AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return (BlockState)blockState.setValue(AXIS, Direction.Axis.X);
                    }
                }
                return blockState;
            }
        }
        return blockState;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

}

