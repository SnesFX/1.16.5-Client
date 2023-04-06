/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Camera {
    private boolean initialized;
    private BlockGetter level;
    private Entity entity;
    private Vec3 position = Vec3.ZERO;
    private final BlockPos.MutableBlockPos blockPosition = new BlockPos.MutableBlockPos();
    private final Vector3f forwards = new Vector3f(0.0f, 0.0f, 1.0f);
    private final Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
    private final Vector3f left = new Vector3f(1.0f, 0.0f, 0.0f);
    private float xRot;
    private float yRot;
    private final Quaternion rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
    private boolean detached;
    private boolean mirror;
    private float eyeHeight;
    private float eyeHeightOld;

    public void setup(BlockGetter blockGetter, Entity entity, boolean bl, boolean bl2, float f) {
        this.initialized = true;
        this.level = blockGetter;
        this.entity = entity;
        this.detached = bl;
        this.mirror = bl2;
        this.setRotation(entity.getViewYRot(f), entity.getViewXRot(f));
        this.setPosition(Mth.lerp((double)f, entity.xo, entity.getX()), Mth.lerp((double)f, entity.yo, entity.getY()) + (double)Mth.lerp(f, this.eyeHeightOld, this.eyeHeight), Mth.lerp((double)f, entity.zo, entity.getZ()));
        if (bl) {
            if (bl2) {
                this.setRotation(this.yRot + 180.0f, -this.xRot);
            }
            this.move(-this.getMaxZoom(4.0), 0.0, 0.0);
        } else if (entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
            Direction direction = ((LivingEntity)entity).getBedOrientation();
            this.setRotation(direction != null ? direction.toYRot() - 180.0f : 0.0f, 0.0f);
            this.move(0.0, 0.3, 0.0);
        }
    }

    public void tick() {
        if (this.entity != null) {
            this.eyeHeightOld = this.eyeHeight;
            this.eyeHeight += (this.entity.getEyeHeight() - this.eyeHeight) * 0.5f;
        }
    }

    private double getMaxZoom(double d) {
        for (int i = 0; i < 8; ++i) {
            BlockHitResult blockHitResult;
            double d2;
            Vec3 vec3;
            float f = (i & 1) * 2 - 1;
            float f2 = (i >> 1 & 1) * 2 - 1;
            float f3 = (i >> 2 & 1) * 2 - 1;
            Vec3 vec32 = this.position.add(f *= 0.1f, f2 *= 0.1f, f3 *= 0.1f);
            if (((HitResult)(blockHitResult = this.level.clip(new ClipContext(vec32, vec3 = new Vec3(this.position.x - (double)this.forwards.x() * d + (double)f + (double)f3, this.position.y - (double)this.forwards.y() * d + (double)f2, this.position.z - (double)this.forwards.z() * d + (double)f3), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this.entity)))).getType() == HitResult.Type.MISS || !((d2 = blockHitResult.getLocation().distanceTo(this.position)) < d)) continue;
            d = d2;
        }
        return d;
    }

    protected void move(double d, double d2, double d3) {
        double d4 = (double)this.forwards.x() * d + (double)this.up.x() * d2 + (double)this.left.x() * d3;
        double d5 = (double)this.forwards.y() * d + (double)this.up.y() * d2 + (double)this.left.y() * d3;
        double d6 = (double)this.forwards.z() * d + (double)this.up.z() * d2 + (double)this.left.z() * d3;
        this.setPosition(new Vec3(this.position.x + d4, this.position.y + d5, this.position.z + d6));
    }

    protected void setRotation(float f, float f2) {
        this.xRot = f2;
        this.yRot = f;
        this.rotation.set(0.0f, 0.0f, 0.0f, 1.0f);
        this.rotation.mul(Vector3f.YP.rotationDegrees(-f));
        this.rotation.mul(Vector3f.XP.rotationDegrees(f2));
        this.forwards.set(0.0f, 0.0f, 1.0f);
        this.forwards.transform(this.rotation);
        this.up.set(0.0f, 1.0f, 0.0f);
        this.up.transform(this.rotation);
        this.left.set(1.0f, 0.0f, 0.0f);
        this.left.transform(this.rotation);
    }

    protected void setPosition(double d, double d2, double d3) {
        this.setPosition(new Vec3(d, d2, d3));
    }

    protected void setPosition(Vec3 vec3) {
        this.position = vec3;
        this.blockPosition.set(vec3.x, vec3.y, vec3.z);
    }

    public Vec3 getPosition() {
        return this.position;
    }

    public BlockPos getBlockPosition() {
        return this.blockPosition;
    }

    public float getXRot() {
        return this.xRot;
    }

    public float getYRot() {
        return this.yRot;
    }

    public Quaternion rotation() {
        return this.rotation;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public boolean isDetached() {
        return this.detached;
    }

    public FluidState getFluidInCamera() {
        if (!this.initialized) {
            return Fluids.EMPTY.defaultFluidState();
        }
        FluidState fluidState = this.level.getFluidState(this.blockPosition);
        if (!fluidState.isEmpty() && this.position.y >= (double)((float)this.blockPosition.getY() + fluidState.getHeight(this.level, this.blockPosition))) {
            return Fluids.EMPTY.defaultFluidState();
        }
        return fluidState;
    }

    public final Vector3f getLookVector() {
        return this.forwards;
    }

    public final Vector3f getUpVector() {
        return this.up;
    }

    public void reset() {
        this.level = null;
        this.entity = null;
        this.initialized = false;
    }
}

