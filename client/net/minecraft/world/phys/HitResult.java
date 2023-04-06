/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public abstract class HitResult {
    protected final Vec3 location;

    protected HitResult(Vec3 vec3) {
        this.location = vec3;
    }

    public double distanceTo(Entity entity) {
        double d = this.location.x - entity.getX();
        double d2 = this.location.y - entity.getY();
        double d3 = this.location.z - entity.getZ();
        return d * d + d2 * d2 + d3 * d3;
    }

    public abstract Type getType();

    public Vec3 getLocation() {
        return this.location;
    }

    public static enum Type {
        MISS,
        BLOCK,
        ENTITY;
        
    }

}

