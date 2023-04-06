/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;

public class CarvedPumpkinBlock
extends HorizontalDirectionalBlock
implements Wearable {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    @Nullable
    private BlockPattern snowGolemBase;
    @Nullable
    private BlockPattern snowGolemFull;
    @Nullable
    private BlockPattern ironGolemBase;
    @Nullable
    private BlockPattern ironGolemFull;
    private static final Predicate<BlockState> PUMPKINS_PREDICATE = blockState -> blockState != null && (blockState.is(Blocks.CARVED_PUMPKIN) || blockState.is(Blocks.JACK_O_LANTERN));

    protected CarvedPumpkinBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState2.is(blockState.getBlock())) {
            return;
        }
        this.trySpawnGolem(level, blockPos);
    }

    public boolean canSpawnGolem(LevelReader levelReader, BlockPos blockPos) {
        return this.getOrCreateSnowGolemBase().find(levelReader, blockPos) != null || this.getOrCreateIronGolemBase().find(levelReader, blockPos) != null;
    }

    private void trySpawnGolem(Level level, BlockPos blockPos) {
        block9 : {
            BlockPattern.BlockPatternMatch blockPatternMatch;
            block8 : {
                Object object;
                blockPatternMatch = this.getOrCreateSnowGolemFull().find(level, blockPos);
                if (blockPatternMatch == null) break block8;
                for (int i = 0; i < this.getOrCreateSnowGolemFull().getHeight(); ++i) {
                    object = blockPatternMatch.getBlock(0, i, 0);
                    level.setBlock(((BlockInWorld)object).getPos(), Blocks.AIR.defaultBlockState(), 2);
                    level.levelEvent(2001, ((BlockInWorld)object).getPos(), Block.getId(((BlockInWorld)object).getState()));
                }
                SnowGolem snowGolem = EntityType.SNOW_GOLEM.create(level);
                object = blockPatternMatch.getBlock(0, 2, 0).getPos();
                snowGolem.moveTo((double)((Vec3i)object).getX() + 0.5, (double)((Vec3i)object).getY() + 0.05, (double)((Vec3i)object).getZ() + 0.5, 0.0f, 0.0f);
                level.addFreshEntity(snowGolem);
                for (ServerPlayer object2 : level.getEntitiesOfClass(ServerPlayer.class, snowGolem.getBoundingBox().inflate(5.0))) {
                    CriteriaTriggers.SUMMONED_ENTITY.trigger(object2, snowGolem);
                }
                for (int i = 0; i < this.getOrCreateSnowGolemFull().getHeight(); ++i) {
                    BlockInWorld j = blockPatternMatch.getBlock(0, i, 0);
                    level.blockUpdated(j.getPos(), Blocks.AIR);
                }
                break block9;
            }
            blockPatternMatch = this.getOrCreateIronGolemFull().find(level, blockPos);
            if (blockPatternMatch == null) break block9;
            for (int i = 0; i < this.getOrCreateIronGolemFull().getWidth(); ++i) {
                for (int j = 0; j < this.getOrCreateIronGolemFull().getHeight(); ++j) {
                    BlockInWorld blockInWorld = blockPatternMatch.getBlock(i, j, 0);
                    level.setBlock(blockInWorld.getPos(), Blocks.AIR.defaultBlockState(), 2);
                    level.levelEvent(2001, blockInWorld.getPos(), Block.getId(blockInWorld.getState()));
                }
            }
            BlockPos blockPos2 = blockPatternMatch.getBlock(1, 2, 0).getPos();
            IronGolem ironGolem = EntityType.IRON_GOLEM.create(level);
            ironGolem.setPlayerCreated(true);
            ironGolem.moveTo((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.05, (double)blockPos2.getZ() + 0.5, 0.0f, 0.0f);
            level.addFreshEntity(ironGolem);
            for (ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, ironGolem.getBoundingBox().inflate(5.0))) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, ironGolem);
            }
            for (int i = 0; i < this.getOrCreateIronGolemFull().getWidth(); ++i) {
                for (int j = 0; j < this.getOrCreateIronGolemFull().getHeight(); ++j) {
                    BlockInWorld blockInWorld = blockPatternMatch.getBlock(i, j, 0);
                    level.blockUpdated(blockInWorld.getPos(), Blocks.AIR);
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    private BlockPattern getOrCreateSnowGolemBase() {
        if (this.snowGolemBase == null) {
            this.snowGolemBase = BlockPatternBuilder.start().aisle(" ", "#", "#").where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }
        return this.snowGolemBase;
    }

    private BlockPattern getOrCreateSnowGolemFull() {
        if (this.snowGolemFull == null) {
            this.snowGolemFull = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }
        return this.snowGolemFull;
    }

    private BlockPattern getOrCreateIronGolemBase() {
        if (this.ironGolemBase == null) {
            this.ironGolemBase = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~").where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
        }
        return this.ironGolemBase;
    }

    private BlockPattern getOrCreateIronGolemFull() {
        if (this.ironGolemFull == null) {
            this.ironGolemFull = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
        }
        return this.ironGolemFull;
    }
}

