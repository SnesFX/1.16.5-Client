/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.control;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MoveControl {
    protected final Mob mob;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected double speedModifier;
    protected float strafeForwards;
    protected float strafeRight;
    protected Operation operation = Operation.WAIT;

    public MoveControl(Mob mob) {
        this.mob = mob;
    }

    public boolean hasWanted() {
        return this.operation == Operation.MOVE_TO;
    }

    public double getSpeedModifier() {
        return this.speedModifier;
    }

    public void setWantedPosition(double d, double d2, double d3, double d4) {
        this.wantedX = d;
        this.wantedY = d2;
        this.wantedZ = d3;
        this.speedModifier = d4;
        if (this.operation != Operation.JUMPING) {
            this.operation = Operation.MOVE_TO;
        }
    }

    public void strafe(float f, float f2) {
        this.operation = Operation.STRAFE;
        this.strafeForwards = f;
        this.strafeRight = f2;
        this.speedModifier = 0.25;
    }

    public void tick() {
        if (this.operation == Operation.STRAFE) {
            float f;
            float f2 = (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
            float f3 = (float)this.speedModifier * f2;
            float f4 = this.strafeForwards;
            float f5 = this.strafeRight;
            float f6 = Mth.sqrt(f4 * f4 + f5 * f5);
            if (f6 < 1.0f) {
                f6 = 1.0f;
            }
            f6 = f3 / f6;
            float f7 = Mth.sin(this.mob.yRot * 0.017453292f);
            float f8 = Mth.cos(this.mob.yRot * 0.017453292f);
            float f9 = (f4 *= f6) * f8 - (f5 *= f6) * f7;
            if (!this.isWalkable(f9, f = f5 * f8 + f4 * f7)) {
                this.strafeForwards = 1.0f;
                this.strafeRight = 0.0f;
            }
            this.mob.setSpeed(f3);
            this.mob.setZza(this.strafeForwards);
            this.mob.setXxa(this.strafeRight);
            this.operation = Operation.WAIT;
        } else if (this.operation == Operation.MOVE_TO) {
            this.operation = Operation.WAIT;
            double d = this.wantedX - this.mob.getX();
            double d2 = this.wantedZ - this.mob.getZ();
            double d3 = this.wantedY - this.mob.getY();
            double d4 = d * d + d3 * d3 + d2 * d2;
            if (d4 < 2.500000277905201E-7) {
                this.mob.setZza(0.0f);
                return;
            }
            float f = (float)(Mth.atan2(d2, d) * 57.2957763671875) - 90.0f;
            this.mob.yRot = this.rotlerp(this.mob.yRot, f, 90.0f);
            this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            BlockPos blockPos = this.mob.blockPosition();
            BlockState blockState = this.mob.level.getBlockState(blockPos);
            Block block = blockState.getBlock();
            VoxelShape voxelShape = blockState.getCollisionShape(this.mob.level, blockPos);
            if (d3 > (double)this.mob.maxUpStep && d * d + d2 * d2 < (double)Math.max(1.0f, this.mob.getBbWidth()) || !voxelShape.isEmpty() && this.mob.getY() < voxelShape.max(Direction.Axis.Y) + (double)blockPos.getY() && !block.is(BlockTags.DOORS) && !block.is(BlockTags.FENCES)) {
                this.mob.getJumpControl().jump();
                this.operation = Operation.JUMPING;
            }
        } else if (this.operation == Operation.JUMPING) {
            this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            if (this.mob.isOnGround()) {
                this.operation = Operation.WAIT;
            }
        } else {
            this.mob.setZza(0.0f);
        }
    }

    private boolean isWalkable(float f, float f2) {
        NodeEvaluator nodeEvaluator;
        PathNavigation pathNavigation = this.mob.getNavigation();
        return pathNavigation == null || (nodeEvaluator = pathNavigation.getNodeEvaluator()) == null || nodeEvaluator.getBlockPathType(this.mob.level, Mth.floor(this.mob.getX() + (double)f), Mth.floor(this.mob.getY()), Mth.floor(this.mob.getZ() + (double)f2)) == BlockPathTypes.WALKABLE;
    }

    protected float rotlerp(float f, float f2, float f3) {
        float f4;
        float f5 = Mth.wrapDegrees(f2 - f);
        if (f5 > f3) {
            f5 = f3;
        }
        if (f5 < -f3) {
            f5 = -f3;
        }
        if ((f4 = f + f5) < 0.0f) {
            f4 += 360.0f;
        } else if (f4 > 360.0f) {
            f4 -= 360.0f;
        }
        return f4;
    }

    public double getWantedX() {
        return this.wantedX;
    }

    public double getWantedY() {
        return this.wantedY;
    }

    public double getWantedZ() {
        return this.wantedZ;
    }

    public static enum Operation {
        WAIT,
        MOVE_TO,
        STRAFE,
        JUMPING;
        
    }

}

