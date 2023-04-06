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
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class ChorusFlowerBlock
extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_5;
    private final ChorusPlantBlock plant;

    protected ChorusFlowerBlock(ChorusPlantBlock chorusPlantBlock, BlockBehaviour.Properties properties) {
        super(properties);
        this.plant = chorusPlantBlock;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        if (!blockState.canSurvive(serverLevel, blockPos)) {
            serverLevel.destroyBlock(blockPos, true);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return blockState.getValue(AGE) < 5;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        int n;
        int n2;
        BlockPos blockPos2 = blockPos.above();
        if (!serverLevel.isEmptyBlock(blockPos2) || blockPos2.getY() >= 256) {
            return;
        }
        int n3 = blockState.getValue(AGE);
        if (n3 >= 5) {
            return;
        }
        boolean bl = false;
        boolean bl2 = false;
        BlockState blockState2 = serverLevel.getBlockState(blockPos.below());
        Block block = blockState2.getBlock();
        if (block == Blocks.END_STONE) {
            bl = true;
        } else if (block == this.plant) {
            n = 1;
            for (n2 = 0; n2 < 4; ++n2) {
                Block block2 = serverLevel.getBlockState(blockPos.below(n + 1)).getBlock();
                if (block2 == this.plant) {
                    ++n;
                    continue;
                }
                if (block2 != Blocks.END_STONE) break;
                bl2 = true;
                break;
            }
            if (n < 2 || n <= random.nextInt(bl2 ? 5 : 4)) {
                bl = true;
            }
        } else if (blockState2.isAir()) {
            bl = true;
        }
        if (bl && ChorusFlowerBlock.allNeighborsEmpty(serverLevel, blockPos2, null) && serverLevel.isEmptyBlock(blockPos.above(2))) {
            serverLevel.setBlock(blockPos, this.plant.getStateForPlacement(serverLevel, blockPos), 2);
            this.placeGrownFlower(serverLevel, blockPos2, n3);
        } else if (n3 < 4) {
            n = random.nextInt(4);
            if (bl2) {
                ++n;
            }
            n2 = 0;
            for (int i = 0; i < n; ++i) {
                Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                BlockPos blockPos3 = blockPos.relative(direction);
                if (!serverLevel.isEmptyBlock(blockPos3) || !serverLevel.isEmptyBlock(blockPos3.below()) || !ChorusFlowerBlock.allNeighborsEmpty(serverLevel, blockPos3, direction.getOpposite())) continue;
                this.placeGrownFlower(serverLevel, blockPos3, n3 + 1);
                n2 = 1;
            }
            if (n2 != 0) {
                serverLevel.setBlock(blockPos, this.plant.getStateForPlacement(serverLevel, blockPos), 2);
            } else {
                this.placeDeadFlower(serverLevel, blockPos);
            }
        } else {
            this.placeDeadFlower(serverLevel, blockPos);
        }
    }

    private void placeGrownFlower(Level level, BlockPos blockPos, int n) {
        level.setBlock(blockPos, (BlockState)this.defaultBlockState().setValue(AGE, n), 2);
        level.levelEvent(1033, blockPos, 0);
    }

    private void placeDeadFlower(Level level, BlockPos blockPos) {
        level.setBlock(blockPos, (BlockState)this.defaultBlockState().setValue(AGE, 5), 2);
        level.levelEvent(1034, blockPos, 0);
    }

    private static boolean allNeighborsEmpty(LevelReader levelReader, BlockPos blockPos, @Nullable Direction direction) {
        for (Direction direction2 : Direction.Plane.HORIZONTAL) {
            if (direction2 == direction || levelReader.isEmptyBlock(blockPos.relative(direction2))) continue;
            return false;
        }
        return true;
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (direction != Direction.UP && !blockState.canSurvive(levelAccessor, blockPos)) {
            levelAccessor.getBlockTicks().scheduleTick(blockPos, this, 1);
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockState blockState2 = levelReader.getBlockState(blockPos.below());
        if (blockState2.getBlock() == this.plant || blockState2.is(Blocks.END_STONE)) {
            return true;
        }
        if (!blockState2.isAir()) {
            return false;
        }
        boolean bl = false;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockState3 = levelReader.getBlockState(blockPos.relative(direction));
            if (blockState3.is(this.plant)) {
                if (bl) {
                    return false;
                }
                bl = true;
                continue;
            }
            if (blockState3.isAir()) continue;
            return false;
        }
        return bl;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public static void generatePlant(LevelAccessor levelAccessor, BlockPos blockPos, Random random, int n) {
        levelAccessor.setBlock(blockPos, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).getStateForPlacement(levelAccessor, blockPos), 2);
        ChorusFlowerBlock.growTreeRecursive(levelAccessor, blockPos, random, blockPos, n, 0);
    }

    private static void growTreeRecursive(LevelAccessor levelAccessor, BlockPos blockPos, Random random, BlockPos blockPos2, int n, int n2) {
        int n3;
        ChorusPlantBlock chorusPlantBlock = (ChorusPlantBlock)Blocks.CHORUS_PLANT;
        int n4 = random.nextInt(4) + 1;
        if (n2 == 0) {
            ++n4;
        }
        for (n3 = 0; n3 < n4; ++n3) {
            BlockPos blockPos3 = blockPos.above(n3 + 1);
            if (!ChorusFlowerBlock.allNeighborsEmpty(levelAccessor, blockPos3, null)) {
                return;
            }
            levelAccessor.setBlock(blockPos3, chorusPlantBlock.getStateForPlacement(levelAccessor, blockPos3), 2);
            levelAccessor.setBlock(blockPos3.below(), chorusPlantBlock.getStateForPlacement(levelAccessor, blockPos3.below()), 2);
        }
        n3 = 0;
        if (n2 < 4) {
            int n5 = random.nextInt(4);
            if (n2 == 0) {
                ++n5;
            }
            for (int i = 0; i < n5; ++i) {
                Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                BlockPos blockPos4 = blockPos.above(n4).relative(direction);
                if (Math.abs(blockPos4.getX() - blockPos2.getX()) >= n || Math.abs(blockPos4.getZ() - blockPos2.getZ()) >= n || !levelAccessor.isEmptyBlock(blockPos4) || !levelAccessor.isEmptyBlock(blockPos4.below()) || !ChorusFlowerBlock.allNeighborsEmpty(levelAccessor, blockPos4, direction.getOpposite())) continue;
                n3 = 1;
                levelAccessor.setBlock(blockPos4, chorusPlantBlock.getStateForPlacement(levelAccessor, blockPos4), 2);
                levelAccessor.setBlock(blockPos4.relative(direction.getOpposite()), chorusPlantBlock.getStateForPlacement(levelAccessor, blockPos4.relative(direction.getOpposite())), 2);
                ChorusFlowerBlock.growTreeRecursive(levelAccessor, blockPos4, random, blockPos2, n, n2 + 1);
            }
        }
        if (n3 == 0) {
            levelAccessor.setBlock(blockPos.above(n4), (BlockState)Blocks.CHORUS_FLOWER.defaultBlockState().setValue(AGE, 5), 2);
        }
    }

    @Override
    public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
        if (projectile.getType().is(EntityTypeTags.IMPACT_PROJECTILES)) {
            BlockPos blockPos = blockHitResult.getBlockPos();
            level.destroyBlock(blockPos, true, projectile);
        }
    }
}

