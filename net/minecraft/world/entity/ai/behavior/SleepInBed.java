/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class SleepInBed
extends Behavior<LivingEntity> {
    private long nextOkStartTime;

    public SleepInBed() {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.HOME, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.LAST_WOKEN, (Object)((Object)MemoryStatus.REGISTERED)));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
        long l;
        if (livingEntity.isPassenger()) {
            return false;
        }
        Brain<?> brain = livingEntity.getBrain();
        GlobalPos globalPos = brain.getMemory(MemoryModuleType.HOME).get();
        if (serverLevel.dimension() != globalPos.dimension()) {
            return false;
        }
        Optional<Long> optional = brain.getMemory(MemoryModuleType.LAST_WOKEN);
        if (optional.isPresent() && (l = serverLevel.getGameTime() - optional.get()) > 0L && l < 100L) {
            return false;
        }
        BlockState blockState = serverLevel.getBlockState(globalPos.pos());
        return globalPos.pos().closerThan(livingEntity.position(), 2.0) && blockState.getBlock().is(BlockTags.BEDS) && blockState.getValue(BedBlock.OCCUPIED) == false;
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        Optional<GlobalPos> optional = livingEntity.getBrain().getMemory(MemoryModuleType.HOME);
        if (!optional.isPresent()) {
            return false;
        }
        BlockPos blockPos = optional.get().pos();
        return livingEntity.getBrain().isActive(Activity.REST) && livingEntity.getY() > (double)blockPos.getY() + 0.4 && blockPos.closerThan(livingEntity.position(), 1.14);
    }

    @Override
    protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        if (l > this.nextOkStartTime) {
            InteractWithDoor.closeDoorsThatIHaveOpenedOrPassedThrough(serverLevel, livingEntity, null, null);
            livingEntity.startSleeping(livingEntity.getBrain().getMemory(MemoryModuleType.HOME).get().pos());
        }
    }

    @Override
    protected boolean timedOut(long l) {
        return false;
    }

    @Override
    protected void stop(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        if (livingEntity.isSleeping()) {
            livingEntity.stopSleeping();
            this.nextOkStartTime = l + 40L;
        }
    }
}

