/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CropBlock
extends BushBlock
implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};

    protected CropBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(this.getAgeProperty(), 0));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE_BY_AGE[blockState.getValue(this.getAgeProperty())];
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.is(Blocks.FARMLAND);
    }

    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 7;
    }

    protected int getAge(BlockState blockState) {
        return blockState.getValue(this.getAgeProperty());
    }

    public BlockState getStateForAge(int n) {
        return (BlockState)this.defaultBlockState().setValue(this.getAgeProperty(), n);
    }

    public boolean isMaxAge(BlockState blockState) {
        return blockState.getValue(this.getAgeProperty()) >= this.getMaxAge();
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return !this.isMaxAge(blockState);
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        int n;
        float f;
        if (serverLevel.getRawBrightness(blockPos, 0) >= 9 && (n = this.getAge(blockState)) < this.getMaxAge() && random.nextInt((int)(25.0f / (f = CropBlock.getGrowthSpeed(this, serverLevel, blockPos))) + 1) == 0) {
            serverLevel.setBlock(blockPos, this.getStateForAge(n + 1), 2);
        }
    }

    public void growCrops(Level level, BlockPos blockPos, BlockState blockState) {
        int n;
        int n2 = this.getAge(blockState) + this.getBonemealAgeIncrease(level);
        if (n2 > (n = this.getMaxAge())) {
            n2 = n;
        }
        level.setBlock(blockPos, this.getStateForAge(n2), 2);
    }

    protected int getBonemealAgeIncrease(Level level) {
        return Mth.nextInt(level.random, 2, 5);
    }

    protected static float getGrowthSpeed(Block block, BlockGetter blockGetter, BlockPos blockPos) {
        boolean bl;
        Object object;
        float f = 1.0f;
        BlockPos blockPos2 = blockPos.below();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float f2 = 0.0f;
                object = blockGetter.getBlockState(blockPos2.offset(i, 0, j));
                if (((BlockBehaviour.BlockStateBase)object).is(Blocks.FARMLAND)) {
                    f2 = 1.0f;
                    if (((StateHolder)object).getValue(FarmBlock.MOISTURE) > 0) {
                        f2 = 3.0f;
                    }
                }
                if (i != 0 || j != 0) {
                    f2 /= 4.0f;
                }
                f += f2;
            }
        }
        BlockPos blockPos3 = blockPos.north();
        BlockPos blockPos4 = blockPos.south();
        BlockPos blockPos5 = blockPos.west();
        object = blockPos.east();
        boolean bl2 = block == blockGetter.getBlockState(blockPos5).getBlock() || block == blockGetter.getBlockState((BlockPos)object).getBlock();
        boolean bl3 = bl = block == blockGetter.getBlockState(blockPos3).getBlock() || block == blockGetter.getBlockState(blockPos4).getBlock();
        if (bl2 && bl) {
            f /= 2.0f;
        } else {
            boolean bl4;
            boolean bl5 = bl4 = block == blockGetter.getBlockState(blockPos5.north()).getBlock() || block == blockGetter.getBlockState(((BlockPos)object).north()).getBlock() || block == blockGetter.getBlockState(((BlockPos)object).south()).getBlock() || block == blockGetter.getBlockState(blockPos5.south()).getBlock();
            if (bl4) {
                f /= 2.0f;
            }
        }
        return f;
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return (levelReader.getRawBrightness(blockPos, 0) >= 8 || levelReader.canSeeSky(blockPos)) && super.canSurvive(blockState, levelReader, blockPos);
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (entity instanceof Ravager && level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            level.destroyBlock(blockPos, true, entity);
        }
        super.entityInside(blockState, level, blockPos, entity);
    }

    protected ItemLike getBaseSeedId() {
        return Items.WHEAT_SEEDS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(this.getBaseSeedId());
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean bl) {
        return !this.isMaxAge(blockState);
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, Random random, BlockPos blockPos, BlockState blockState) {
        this.growCrops(serverLevel, blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}

