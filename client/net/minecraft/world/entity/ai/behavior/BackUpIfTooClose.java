/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class BackUpIfTooClose<E extends Mob>
extends Behavior<E> {
    private final int tooCloseDistance;
    private final float strafeSpeed;

    public BackUpIfTooClose(int n, float f) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.VISIBLE_LIVING_ENTITIES, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.tooCloseDistance = n;
        this.strafeSpeed = f;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        return this.isTargetVisible(e) && this.isTargetTooClose(e);
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        ((LivingEntity)e).getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(this.getTarget(e), true));
        ((Mob)e).getMoveControl().strafe(-this.strafeSpeed, 0.0f);
        ((Mob)e).yRot = Mth.rotateIfNecessary(((Mob)e).yRot, ((Mob)e).yHeadRot, 0.0f);
    }

    private boolean isTargetVisible(E e) {
        return ((LivingEntity)e).getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).get().contains(this.getTarget(e));
    }

    private boolean isTargetTooClose(E e) {
        return this.getTarget(e).closerThan((Entity)e, this.tooCloseDistance);
    }

    private LivingEntity getTarget(E e) {
        return ((LivingEntity)e).getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}

