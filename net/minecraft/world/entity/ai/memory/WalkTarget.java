/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.memory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.phys.Vec3;

public class WalkTarget {
    private final PositionTracker target;
    private final float speedModifier;
    private final int closeEnoughDist;

    public WalkTarget(BlockPos blockPos, float f, int n) {
        this(new BlockPosTracker(blockPos), f, n);
    }

    public WalkTarget(Vec3 vec3, float f, int n) {
        this(new BlockPosTracker(new BlockPos(vec3)), f, n);
    }

    public WalkTarget(PositionTracker positionTracker, float f, int n) {
        this.target = positionTracker;
        this.speedModifier = f;
        this.closeEnoughDist = n;
    }

    public PositionTracker getTarget() {
        return this.target;
    }

    public float getSpeedModifier() {
        return this.speedModifier;
    }

    public int getCloseEnoughDist() {
        return this.closeEnoughDist;
    }
}

