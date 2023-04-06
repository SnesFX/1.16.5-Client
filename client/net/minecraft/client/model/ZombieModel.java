/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieModel<T extends Zombie>
extends AbstractZombieModel<T> {
    public ZombieModel(float f, boolean bl) {
        this(f, 0.0f, 64, bl ? 32 : 64);
    }

    protected ZombieModel(float f, float f2, int n, int n2) {
        super(f, f2, n, n2);
    }

    @Override
    public boolean isAggressive(T t) {
        return ((Mob)t).isAggressive();
    }
}

