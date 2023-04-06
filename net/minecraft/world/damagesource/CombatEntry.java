/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CombatEntry {
    private final DamageSource source;
    private final int time;
    private final float damage;
    private final float health;
    private final String location;
    private final float fallDistance;

    public CombatEntry(DamageSource damageSource, int n, float f, float f2, String string, float f3) {
        this.source = damageSource;
        this.time = n;
        this.damage = f2;
        this.health = f;
        this.location = string;
        this.fallDistance = f3;
    }

    public DamageSource getSource() {
        return this.source;
    }

    public float getDamage() {
        return this.damage;
    }

    public boolean isCombatRelated() {
        return this.source.getEntity() instanceof LivingEntity;
    }

    @Nullable
    public String getLocation() {
        return this.location;
    }

    @Nullable
    public Component getAttackerName() {
        return this.getSource().getEntity() == null ? null : this.getSource().getEntity().getDisplayName();
    }

    public float getFallDistance() {
        if (this.source == DamageSource.OUT_OF_WORLD) {
            return Float.MAX_VALUE;
        }
        return this.fallDistance;
    }
}

