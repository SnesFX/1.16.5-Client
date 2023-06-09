/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.material;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;

public abstract class LavaFluid
extends FlowingFluid {
    @Override
    public Fluid getFlowing() {
        return Fluids.FLOWING_LAVA;
    }

    @Override
    public Fluid getSource() {
        return Fluids.LAVA;
    }

    @Override
    public Item getBucket() {
        return Items.LAVA_BUCKET;
    }

    @Override
    public void animateTick(Level level, BlockPos blockPos, FluidState fluidState, Random random) {
        BlockPos blockPos2 = blockPos.above();
        if (level.getBlockState(blockPos2).isAir() && !level.getBlockState(blockPos2).isSolidRender(level, blockPos2)) {
            if (random.nextInt(100) == 0) {
                double d = (double)blockPos.getX() + random.nextDouble();
                double d2 = (double)blockPos.getY() + 1.0;
                double d3 = (double)blockPos.getZ() + random.nextDouble();
                level.addParticle(ParticleTypes.LAVA, d, d2, d3, 0.0, 0.0, 0.0);
                level.playLocalSound(d, d2, d3, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
            if (random.nextInt(200) == 0) {
                level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        }
    }

    @Override
    public void randomTick(Level level, BlockPos blockPos, FluidState fluidState, Random random) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            return;
        }
        int n = random.nextInt(3);
        if (n > 0) {
            BlockPos blockPos2 = blockPos;
            for (int i = 0; i < n; ++i) {
                if (!level.isLoaded(blockPos2 = blockPos2.offset(random.nextInt(3) - 1, 1, random.nextInt(3) - 1))) {
                    return;
                }
                BlockState blockState = level.getBlockState(blockPos2);
                if (blockState.isAir()) {
                    if (!this.hasFlammableNeighbours(level, blockPos2)) continue;
                    level.setBlockAndUpdate(blockPos2, BaseFireBlock.getState(level, blockPos2));
                    return;
                }
                if (!blockState.getMaterial().blocksMotion()) continue;
                return;
            }
        } else {
            for (int i = 0; i < 3; ++i) {
                BlockPos blockPos3 = blockPos.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                if (!level.isLoaded(blockPos3)) {
                    return;
                }
                if (!level.isEmptyBlock(blockPos3.above()) || !this.isFlammable(level, blockPos3)) continue;
                level.setBlockAndUpdate(blockPos3.above(), BaseFireBlock.getState(level, blockPos3));
            }
        }
    }

    private boolean hasFlammableNeighbours(LevelReader levelReader, BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            if (!this.isFlammable(levelReader, blockPos.relative(direction))) continue;
            return true;
        }
        return false;
    }

    private boolean isFlammable(LevelReader levelReader, BlockPos blockPos) {
        if (blockPos.getY() >= 0 && blockPos.getY() < 256 && !levelReader.hasChunkAt(blockPos)) {
            return false;
        }
        return levelReader.getBlockState(blockPos).getMaterial().isFlammable();
    }

    @Nullable
    @Override
    public ParticleOptions getDripParticle() {
        return ParticleTypes.DRIPPING_LAVA;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        this.fizz(levelAccessor, blockPos);
    }

    @Override
    public int getSlopeFindDistance(LevelReader levelReader) {
        return levelReader.dimensionType().ultraWarm() ? 4 : 2;
    }

    @Override
    public BlockState createLegacyBlock(FluidState fluidState) {
        return (BlockState)Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL, LavaFluid.getLegacyLevel(fluidState));
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
    }

    @Override
    public int getDropOff(LevelReader levelReader) {
        return levelReader.dimensionType().ultraWarm() ? 1 : 2;
    }

    @Override
    public boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
        return fluidState.getHeight(blockGetter, blockPos) >= 0.44444445f && fluid.is(FluidTags.WATER);
    }

    @Override
    public int getTickDelay(LevelReader levelReader) {
        return levelReader.dimensionType().ultraWarm() ? 10 : 30;
    }

    @Override
    public int getSpreadDelay(Level level, BlockPos blockPos, FluidState fluidState, FluidState fluidState2) {
        int n = this.getTickDelay(level);
        if (!(fluidState.isEmpty() || fluidState2.isEmpty() || fluidState.getValue(FALLING).booleanValue() || fluidState2.getValue(FALLING).booleanValue() || !(fluidState2.getHeight(level, blockPos) > fluidState.getHeight(level, blockPos)) || level.getRandom().nextInt(4) == 0)) {
            n *= 4;
        }
        return n;
    }

    private void fizz(LevelAccessor levelAccessor, BlockPos blockPos) {
        levelAccessor.levelEvent(1501, blockPos, 0);
    }

    @Override
    protected boolean canConvertToSource() {
        return false;
    }

    @Override
    protected void spreadTo(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
        if (direction == Direction.DOWN) {
            FluidState fluidState2 = levelAccessor.getFluidState(blockPos);
            if (this.is(FluidTags.LAVA) && fluidState2.is(FluidTags.WATER)) {
                if (blockState.getBlock() instanceof LiquidBlock) {
                    levelAccessor.setBlock(blockPos, Blocks.STONE.defaultBlockState(), 3);
                }
                this.fizz(levelAccessor, blockPos);
                return;
            }
        }
        super.spreadTo(levelAccessor, blockPos, blockState, direction, fluidState);
    }

    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0f;
    }

    public static class Flowing
    extends LavaFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState fluidState) {
            return fluidState.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return false;
        }
    }

    public static class Source
    extends LavaFluid {
        @Override
        public int getAmount(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return true;
        }
    }

}

