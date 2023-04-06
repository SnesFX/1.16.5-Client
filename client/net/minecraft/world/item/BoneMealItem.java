/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BoneMealItem
extends Item {
    public BoneMealItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockPos blockPos2 = blockPos.relative(useOnContext.getClickedFace());
        if (BoneMealItem.growCrop(useOnContext.getItemInHand(), level, blockPos)) {
            if (!level.isClientSide) {
                level.levelEvent(2005, blockPos, 0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        BlockState blockState = level.getBlockState(blockPos);
        boolean bl = blockState.isFaceSturdy(level, blockPos, useOnContext.getClickedFace());
        if (bl && BoneMealItem.growWaterPlant(useOnContext.getItemInHand(), level, blockPos2, useOnContext.getClickedFace())) {
            if (!level.isClientSide) {
                level.levelEvent(2005, blockPos2, 0);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static boolean growCrop(ItemStack itemStack, Level level, BlockPos blockPos) {
        BonemealableBlock bonemealableBlock;
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.getBlock() instanceof BonemealableBlock && (bonemealableBlock = (BonemealableBlock)((Object)blockState.getBlock())).isValidBonemealTarget(level, blockPos, blockState, level.isClientSide)) {
            if (level instanceof ServerLevel) {
                if (bonemealableBlock.isBonemealSuccess(level, level.random, blockPos, blockState)) {
                    bonemealableBlock.performBonemeal((ServerLevel)level, level.random, blockPos, blockState);
                }
                itemStack.shrink(1);
            }
            return true;
        }
        return false;
    }

    public static boolean growWaterPlant(ItemStack itemStack, Level level, BlockPos blockPos, @Nullable Direction direction) {
        if (!level.getBlockState(blockPos).is(Blocks.WATER) || level.getFluidState(blockPos).getAmount() != 8) {
            return false;
        }
        if (!(level instanceof ServerLevel)) {
            return true;
        }
        block0 : for (int i = 0; i < 128; ++i) {
            BlockPos blockPos2 = blockPos;
            BlockState blockState = Blocks.SEAGRASS.defaultBlockState();
            for (int j = 0; j < i / 16; ++j) {
                if (level.getBlockState(blockPos2 = blockPos2.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1)).isCollisionShapeFullBlock(level, blockPos2)) continue block0;
            }
            Optional<ResourceKey<Biome>> optional = level.getBiomeName(blockPos2);
            if (Objects.equals(optional, Optional.of(Biomes.WARM_OCEAN)) || Objects.equals(optional, Optional.of(Biomes.DEEP_WARM_OCEAN))) {
                if (i == 0 && direction != null && direction.getAxis().isHorizontal()) {
                    blockState = (BlockState)((Block)BlockTags.WALL_CORALS.getRandomElement(level.random)).defaultBlockState().setValue(BaseCoralWallFanBlock.FACING, direction);
                } else if (random.nextInt(4) == 0) {
                    blockState = ((Block)BlockTags.UNDERWATER_BONEMEALS.getRandomElement(random)).defaultBlockState();
                }
            }
            if (blockState.getBlock().is(BlockTags.WALL_CORALS)) {
                for (int j = 0; !blockState.canSurvive(level, blockPos2) && j < 4; ++j) {
                    blockState = (BlockState)blockState.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
                }
            }
            if (!blockState.canSurvive(level, blockPos2)) continue;
            BlockState blockState2 = level.getBlockState(blockPos2);
            if (blockState2.is(Blocks.WATER) && level.getFluidState(blockPos2).getAmount() == 8) {
                level.setBlock(blockPos2, blockState, 3);
                continue;
            }
            if (!blockState2.is(Blocks.SEAGRASS) || random.nextInt(10) != 0) continue;
            ((BonemealableBlock)((Object)Blocks.SEAGRASS)).performBonemeal((ServerLevel)level, random, blockPos2, blockState2);
        }
        itemStack.shrink(1);
        return true;
    }

    public static void addGrowthParticles(LevelAccessor levelAccessor, BlockPos blockPos, int n) {
        double d;
        BlockState blockState;
        if (n == 0) {
            n = 15;
        }
        if ((blockState = levelAccessor.getBlockState(blockPos)).isAir()) {
            return;
        }
        double d2 = 0.5;
        if (blockState.is(Blocks.WATER)) {
            n *= 3;
            d = 1.0;
            d2 = 3.0;
        } else if (blockState.isSolidRender(levelAccessor, blockPos)) {
            blockPos = blockPos.above();
            n *= 3;
            d2 = 3.0;
            d = 1.0;
        } else {
            d = blockState.getShape(levelAccessor, blockPos).max(Direction.Axis.Y);
        }
        levelAccessor.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 0.0, 0.0, 0.0);
        for (int i = 0; i < n; ++i) {
            double d3;
            double d4;
            double d5 = random.nextGaussian() * 0.02;
            double d6 = random.nextGaussian() * 0.02;
            double d7 = random.nextGaussian() * 0.02;
            double d8 = 0.5 - d2;
            double d9 = (double)blockPos.getX() + d8 + random.nextDouble() * d2 * 2.0;
            if (levelAccessor.getBlockState(new BlockPos(d9, d4 = (double)blockPos.getY() + random.nextDouble() * d, d3 = (double)blockPos.getZ() + d8 + random.nextDouble() * d2 * 2.0).below()).isAir()) continue;
            levelAccessor.addParticle(ParticleTypes.HAPPY_VILLAGER, d9, d4, d3, d5, d6, d7);
        }
    }
}

