/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TurtleEggBlock
extends Block {
    private static final VoxelShape ONE_EGG_AABB = Block.box(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
    private static final VoxelShape MULTIPLE_EGGS_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
    public static final IntegerProperty EGGS = BlockStateProperties.EGGS;

    public TurtleEggBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HATCH, 0)).setValue(EGGS, 1));
    }

    @Override
    public void stepOn(Level level, BlockPos blockPos, Entity entity) {
        this.destroyEgg(level, blockPos, entity, 100);
        super.stepOn(level, blockPos, entity);
    }

    @Override
    public void fallOn(Level level, BlockPos blockPos, Entity entity, float f) {
        if (!(entity instanceof Zombie)) {
            this.destroyEgg(level, blockPos, entity, 3);
        }
        super.fallOn(level, blockPos, entity, f);
    }

    private void destroyEgg(Level level, BlockPos blockPos, Entity entity, int n) {
        BlockState blockState;
        if (!this.canDestroyEgg(level, entity)) {
            return;
        }
        if (!level.isClientSide && level.random.nextInt(n) == 0 && (blockState = level.getBlockState(blockPos)).is(Blocks.TURTLE_EGG)) {
            this.decreaseEggs(level, blockPos, blockState);
        }
    }

    private void decreaseEggs(Level level, BlockPos blockPos, BlockState blockState) {
        level.playSound(null, blockPos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7f, 0.9f + level.random.nextFloat() * 0.2f);
        int n = blockState.getValue(EGGS);
        if (n <= 1) {
            level.destroyBlock(blockPos, false);
        } else {
            level.setBlock(blockPos, (BlockState)blockState.setValue(EGGS, n - 1), 2);
            level.levelEvent(2001, blockPos, Block.getId(blockState));
        }
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        if (this.shouldUpdateHatchLevel(serverLevel) && TurtleEggBlock.onSand(serverLevel, blockPos)) {
            int n = blockState.getValue(HATCH);
            if (n < 2) {
                serverLevel.playSound(null, blockPos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                serverLevel.setBlock(blockPos, (BlockState)blockState.setValue(HATCH, n + 1), 2);
            } else {
                serverLevel.playSound(null, blockPos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7f, 0.9f + random.nextFloat() * 0.2f);
                serverLevel.removeBlock(blockPos, false);
                for (int i = 0; i < blockState.getValue(EGGS); ++i) {
                    serverLevel.levelEvent(2001, blockPos, Block.getId(blockState));
                    Turtle turtle = EntityType.TURTLE.create(serverLevel);
                    turtle.setAge(-24000);
                    turtle.setHomePos(blockPos);
                    turtle.moveTo((double)blockPos.getX() + 0.3 + (double)i * 0.2, blockPos.getY(), (double)blockPos.getZ() + 0.3, 0.0f, 0.0f);
                    serverLevel.addFreshEntity(turtle);
                }
            }
        }
    }

    public static boolean onSand(BlockGetter blockGetter, BlockPos blockPos) {
        return TurtleEggBlock.isSand(blockGetter, blockPos.below());
    }

    public static boolean isSand(BlockGetter blockGetter, BlockPos blockPos) {
        return blockGetter.getBlockState(blockPos).is(BlockTags.SAND);
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (TurtleEggBlock.onSand(level, blockPos) && !level.isClientSide) {
            level.levelEvent(2005, blockPos, 0);
        }
    }

    private boolean shouldUpdateHatchLevel(Level level) {
        float f = level.getTimeOfDay(1.0f);
        if ((double)f < 0.69 && (double)f > 0.65) {
            return true;
        }
        return level.random.nextInt(500) == 0;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
        this.decreaseEggs(level, blockPos, blockState);
    }

    @Override
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        if (blockPlaceContext.getItemInHand().getItem() == this.asItem() && blockState.getValue(EGGS) < 4) {
            return true;
        }
        return super.canBeReplaced(blockState, blockPlaceContext);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
        if (blockState.is(this)) {
            return (BlockState)blockState.setValue(EGGS, Math.min(4, blockState.getValue(EGGS) + 1));
        }
        return super.getStateForPlacement(blockPlaceContext);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        if (blockState.getValue(EGGS) > 1) {
            return MULTIPLE_EGGS_AABB;
        }
        return ONE_EGG_AABB;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HATCH, EGGS);
    }

    private boolean canDestroyEgg(Level level, Entity entity) {
        if (entity instanceof Turtle || entity instanceof Bat) {
            return false;
        }
        if (entity instanceof LivingEntity) {
            return entity instanceof Player || level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        }
        return false;
    }
}

