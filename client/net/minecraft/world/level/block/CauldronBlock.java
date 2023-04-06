/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CauldronBlock
extends Block {
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;
    private static final VoxelShape INSIDE = CauldronBlock.box(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape SHAPE = Shapes.join(Shapes.block(), Shapes.or(CauldronBlock.box(0.0, 0.0, 4.0, 16.0, 3.0, 12.0), CauldronBlock.box(4.0, 0.0, 0.0, 12.0, 3.0, 16.0), CauldronBlock.box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), INSIDE), BooleanOp.ONLY_FIRST);

    public CauldronBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 0));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return INSIDE;
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        int n = blockState.getValue(LEVEL);
        float f = (float)blockPos.getY() + (6.0f + (float)(3 * n)) / 16.0f;
        if (!level.isClientSide && entity.isOnFire() && n > 0 && entity.getY() <= (double)f) {
            entity.clearFire();
            this.setWaterLevel(level, blockPos, blockState, n - 1);
        }
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        Object object;
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.isEmpty()) {
            return InteractionResult.PASS;
        }
        int n = blockState.getValue(LEVEL);
        Item item = itemStack.getItem();
        if (item == Items.WATER_BUCKET) {
            if (n < 3 && !level.isClientSide) {
                if (!player.abilities.instabuild) {
                    player.setItemInHand(interactionHand, new ItemStack(Items.BUCKET));
                }
                player.awardStat(Stats.FILL_CAULDRON);
                this.setWaterLevel(level, blockPos, blockState, 3);
                level.playSound(null, blockPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (item == Items.BUCKET) {
            if (n == 3 && !level.isClientSide) {
                if (!player.abilities.instabuild) {
                    itemStack.shrink(1);
                    if (itemStack.isEmpty()) {
                        player.setItemInHand(interactionHand, new ItemStack(Items.WATER_BUCKET));
                    } else if (!player.inventory.add(new ItemStack(Items.WATER_BUCKET))) {
                        player.drop(new ItemStack(Items.WATER_BUCKET), false);
                    }
                }
                player.awardStat(Stats.USE_CAULDRON);
                this.setWaterLevel(level, blockPos, blockState, 0);
                level.playSound(null, blockPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (item == Items.GLASS_BOTTLE) {
            if (n > 0 && !level.isClientSide) {
                if (!player.abilities.instabuild) {
                    ItemStack itemStack2 = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
                    player.awardStat(Stats.USE_CAULDRON);
                    itemStack.shrink(1);
                    if (itemStack.isEmpty()) {
                        player.setItemInHand(interactionHand, itemStack2);
                    } else if (!player.inventory.add(itemStack2)) {
                        player.drop(itemStack2, false);
                    } else if (player instanceof ServerPlayer) {
                        ((ServerPlayer)player).refreshContainer(player.inventoryMenu);
                    }
                }
                level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                this.setWaterLevel(level, blockPos, blockState, n - 1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (item == Items.POTION && PotionUtils.getPotion(itemStack) == Potions.WATER) {
            if (n < 3 && !level.isClientSide) {
                if (!player.abilities.instabuild) {
                    ItemStack itemStack3 = new ItemStack(Items.GLASS_BOTTLE);
                    player.awardStat(Stats.USE_CAULDRON);
                    player.setItemInHand(interactionHand, itemStack3);
                    if (player instanceof ServerPlayer) {
                        ((ServerPlayer)player).refreshContainer(player.inventoryMenu);
                    }
                }
                level.playSound(null, blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                this.setWaterLevel(level, blockPos, blockState, n + 1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (n > 0 && item instanceof DyeableLeatherItem && (object = (DyeableLeatherItem)((Object)item)).hasCustomColor(itemStack) && !level.isClientSide) {
            object.clearColor(itemStack);
            this.setWaterLevel(level, blockPos, blockState, n - 1);
            player.awardStat(Stats.CLEAN_ARMOR);
            return InteractionResult.SUCCESS;
        }
        if (n > 0 && item instanceof BannerItem) {
            if (BannerBlockEntity.getPatternCount(itemStack) > 0 && !level.isClientSide) {
                object = itemStack.copy();
                ((ItemStack)object).setCount(1);
                BannerBlockEntity.removeLastPattern((ItemStack)object);
                player.awardStat(Stats.CLEAN_BANNER);
                if (!player.abilities.instabuild) {
                    itemStack.shrink(1);
                    this.setWaterLevel(level, blockPos, blockState, n - 1);
                }
                if (itemStack.isEmpty()) {
                    player.setItemInHand(interactionHand, (ItemStack)object);
                } else if (!player.inventory.add((ItemStack)object)) {
                    player.drop((ItemStack)object, false);
                } else if (player instanceof ServerPlayer) {
                    ((ServerPlayer)player).refreshContainer(player.inventoryMenu);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (n > 0 && item instanceof BlockItem) {
            object = ((BlockItem)item).getBlock();
            if (object instanceof ShulkerBoxBlock && !level.isClientSide()) {
                ItemStack itemStack4 = new ItemStack(Blocks.SHULKER_BOX, 1);
                if (itemStack.hasTag()) {
                    itemStack4.setTag(itemStack.getTag().copy());
                }
                player.setItemInHand(interactionHand, itemStack4);
                this.setWaterLevel(level, blockPos, blockState, n - 1);
                player.awardStat(Stats.CLEAN_SHULKER_BOX);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    public void setWaterLevel(Level level, BlockPos blockPos, BlockState blockState, int n) {
        level.setBlock(blockPos, (BlockState)blockState.setValue(LEVEL, Mth.clamp(n, 0, 3)), 2);
        level.updateNeighbourForOutputSignal(blockPos, this);
    }

    @Override
    public void handleRain(Level level, BlockPos blockPos) {
        if (level.random.nextInt(20) != 1) {
            return;
        }
        float f = level.getBiome(blockPos).getTemperature(blockPos);
        if (f < 0.15f) {
            return;
        }
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.getValue(LEVEL) < 3) {
            level.setBlock(blockPos, (BlockState)blockState.cycle(LEVEL), 2);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return blockState.getValue(LEVEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }
}

