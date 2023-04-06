/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Monster;

public class GiantZombieModel
extends AbstractZombieModel<Giant> {
    public GiantZombieModel() {
        this(0.0f, false);
    }

    public GiantZombieModel(float f, boolean bl) {
        super(f, 0.0f, 64, bl ? 32 : 64);
    }

    @Override
    public boolean isAggressive(Giant giant) {
        return false;
    }
}

