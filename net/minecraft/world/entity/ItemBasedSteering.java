/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity;

import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;

public class ItemBasedSteering {
    private final SynchedEntityData entityData;
    private final EntityDataAccessor<Integer> boostTimeAccessor;
    private final EntityDataAccessor<Boolean> hasSaddleAccessor;
    public boolean boosting;
    public int boostTime;
    public int boostTimeTotal;

    public ItemBasedSteering(SynchedEntityData synchedEntityData, EntityDataAccessor<Integer> entityDataAccessor, EntityDataAccessor<Boolean> entityDataAccessor2) {
        this.entityData = synchedEntityData;
        this.boostTimeAccessor = entityDataAccessor;
        this.hasSaddleAccessor = entityDataAccessor2;
    }

    public void onSynced() {
        this.boosting = true;
        this.boostTime = 0;
        this.boostTimeTotal = this.entityData.get(this.boostTimeAccessor);
    }

    public boolean boost(Random random) {
        if (this.boosting) {
            return false;
        }
        this.boosting = true;
        this.boostTime = 0;
        this.boostTimeTotal = random.nextInt(841) + 140;
        this.entityData.set(this.boostTimeAccessor, this.boostTimeTotal);
        return true;
    }

    public void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("Saddle", this.hasSaddle());
    }

    public void readAdditionalSaveData(CompoundTag compoundTag) {
        this.setSaddle(compoundTag.getBoolean("Saddle"));
    }

    public void setSaddle(boolean bl) {
        this.entityData.set(this.hasSaddleAccessor, bl);
    }

    public boolean hasSaddle() {
        return this.entityData.get(this.hasSaddleAccessor);
    }
}

