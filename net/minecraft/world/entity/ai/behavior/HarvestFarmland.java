/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HarvestFarmland
extends Behavior<Villager> {
    @Nullable
    private BlockPos aboveFarmlandPos;
    private long nextOkStartTime;
    private int timeWorkedSoFar;
    private final List<BlockPos> validFarmlandAroundVillager = Lists.newArrayList();

    public HarvestFarmland() {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.SECONDARY_JOB_SITE, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        }
        if (villager.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return false;
        }
        BlockPos.MutableBlockPos mutableBlockPos = villager.blockPosition().mutable();
        this.validFarmlandAroundVillager.clear();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    mutableBlockPos.set(villager.getX() + (double)i, villager.getY() + (double)j, villager.getZ() + (double)k);
                    if (!this.validPos(mutableBlockPos, serverLevel)) continue;
                    this.validFarmlandAroundVillager.add(new BlockPos(mutableBlockPos));
                }
            }
        }
        this.aboveFarmlandPos = this.getValidFarmland(serverLevel);
        return this.aboveFarmlandPos != null;
    }

    @Nullable
    private BlockPos getValidFarmland(ServerLevel serverLevel) {
        return this.validFarmlandAroundVillager.isEmpty() ? null : this.validFarmlandAroundVillager.get(serverLevel.getRandom().nextInt(this.validFarmlandAroundVillager.size()));
    }

    private boolean validPos(BlockPos blockPos, ServerLevel serverLevel) {
        BlockState blockState = serverLevel.getBlockState(blockPos);
        Block block = blockState.getBlock();
        Block block2 = serverLevel.getBlockState(blockPos.below()).getBlock();
        return block instanceof CropBlock && ((CropBlock)block).isMaxAge(blockState) || blockState.isAir() && block2 instanceof FarmBlock;
    }

    @Override
    protected void start(ServerLevel serverLevel, Villager villager, long l) {
        if (l > this.nextOkStartTime && this.aboveFarmlandPos != null) {
            villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.aboveFarmlandPos));
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.aboveFarmlandPos), 0.5f, 1));
        }
    }

    @Override
    protected void stop(ServerLevel serverLevel, Villager villager, long l) {
        villager.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        this.timeWorkedSoFar = 0;
        this.nextOkStartTime = l + 40L;
    }

    @Override
    protected void tick(ServerLevel serverLevel, Villager villager, long l) {
        if (this.aboveFarmlandPos != null && !this.aboveFarmlandPos.closerThan(villager.position(), 1.0)) {
            return;
        }
        if (this.aboveFarmlandPos != null && l > this.nextOkStartTime) {
            BlockState blockState = serverLevel.getBlockState(this.aboveFarmlandPos);
            Block block = blockState.getBlock();
            Block block2 = serverLevel.getBlockState(this.aboveFarmlandPos.below()).getBlock();
            if (block instanceof CropBlock && ((CropBlock)block).isMaxAge(blockState)) {
                serverLevel.destroyBlock(this.aboveFarmlandPos, true, villager);
            }
            if (blockState.isAir() && block2 instanceof FarmBlock && villager.hasFarmSeeds()) {
                SimpleContainer simpleContainer = villager.getInventory();
                for (int i = 0; i < simpleContainer.getContainerSize(); ++i) {
                    ItemStack itemStack = simpleContainer.getItem(i);
                    boolean bl = false;
                    if (!itemStack.isEmpty()) {
                        if (itemStack.getItem() == Items.WHEAT_SEEDS) {
                            serverLevel.setBlock(this.aboveFarmlandPos, Blocks.WHEAT.defaultBlockState(), 3);
                            bl = true;
                        } else if (itemStack.getItem() == Items.POTATO) {
                            serverLevel.setBlock(this.aboveFarmlandPos, Blocks.POTATOES.defaultBlockState(), 3);
                            bl = true;
                        } else if (itemStack.getItem() == Items.CARROT) {
                            serverLevel.setBlock(this.aboveFarmlandPos, Blocks.CARROTS.defaultBlockState(), 3);
                            bl = true;
                        } else if (itemStack.getItem() == Items.BEETROOT_SEEDS) {
                            serverLevel.setBlock(this.aboveFarmlandPos, Blocks.BEETROOTS.defaultBlockState(), 3);
                            bl = true;
                        }
                    }
                    if (!bl) continue;
                    serverLevel.playSound(null, this.aboveFarmlandPos.getX(), this.aboveFarmlandPos.getY(), this.aboveFarmlandPos.getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0f, 1.0f);
                    itemStack.shrink(1);
                    if (!itemStack.isEmpty()) break;
                    simpleContainer.setItem(i, ItemStack.EMPTY);
                    break;
                }
            }
            if (block instanceof CropBlock && !((CropBlock)block).isMaxAge(blockState)) {
                this.validFarmlandAroundVillager.remove(this.aboveFarmlandPos);
                this.aboveFarmlandPos = this.getValidFarmland(serverLevel);
                if (this.aboveFarmlandPos != null) {
                    this.nextOkStartTime = l + 20L;
                    villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.aboveFarmlandPos), 0.5f, 1));
                    villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.aboveFarmlandPos));
                }
            }
        }
        ++this.timeWorkedSoFar;
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, Villager villager, long l) {
        return this.timeWorkedSoFar < 200;
    }

    @Override
    protected /* synthetic */ boolean canStillUse(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        return this.canStillUse(serverLevel, (Villager)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void stop(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        this.stop(serverLevel, (Villager)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void tick(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        this.tick(serverLevel, (Villager)livingEntity, l);
    }
}

