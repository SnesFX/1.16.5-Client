/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityDimensions {
    public final float width;
    public final float height;
    public final boolean fixed;

    public EntityDimensions(float f, float f2, boolean bl) {
        this.width = f;
        this.height = f2;
        this.fixed = bl;
    }

    public AABB makeBoundingBox(Vec3 vec3) {
        return this.makeBoundingBox(vec3.x, vec3.y, vec3.z);
    }

    public AABB makeBoundingBox(double d, double d2, double d3) {
        float f = this.width / 2.0f;
        float f2 = this.height;
        return new AABB(d - (double)f, d2, d3 - (double)f, d + (double)f, d2 + (double)f2, d3 + (double)f);
    }

    public EntityDimensions scale(float f) {
        return this.scale(f, f);
    }

    public EntityDimensions scale(float f, float f2) {
        if (this.fixed || f == 1.0f && f2 == 1.0f) {
            return this;
        }
        return EntityDimensions.scalable(this.width * f, this.height * f2);
    }

    public static EntityDimensions scalable(float f, float f2) {
        return new EntityDimensions(f, f2, false);
    }

    public static EntityDimensions fixed(float f, float f2) {
        return new EntityDimensions(f, f2, true);
    }

    public String toString() {
        return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.fixed;
    }
}

