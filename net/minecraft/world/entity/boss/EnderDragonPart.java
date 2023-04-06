/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;

public class EnderDragonPart
extends Entity {
    public final EnderDragon parentMob;
    public final String name;
    private final EntityDimensions size;

    public EnderDragonPart(EnderDragon enderDragon, String string, float f, float f2) {
        super(enderDragon.getType(), enderDragon.level);
        this.size = EntityDimensions.scalable(f, f2);
        this.refreshDimensions();
        this.parentMob = enderDragon;
        this.name = string;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        return this.parentMob.hurt(this, damageSource, f);
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.parentMob == entity;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }
}

