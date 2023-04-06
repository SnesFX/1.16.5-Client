/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.level.Level;

public class Donkey
extends AbstractChestedHorse {
    public Donkey(EntityType<? extends Donkey> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.DONKEY_AMBIENT;
    }

    @Override
    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.DONKEY_ANGRY;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.DONKEY_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getEatingSound() {
        return SoundEvents.DONKEY_EAT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        super.getHurtSound(damageSource);
        return SoundEvents.DONKEY_HURT;
    }

    @Override
    public boolean canMate(Animal animal) {
        if (animal == this) {
            return false;
        }
        if (animal instanceof Donkey || animal instanceof Horse) {
            return this.canParent() && ((AbstractHorse)animal).canParent();
        }
        return false;
    }

    @Override
    public AgableMob getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        EntityType<AbstractChestedHorse> entityType = agableMob instanceof Horse ? EntityType.MULE : EntityType.DONKEY;
        AbstractHorse abstractHorse = entityType.create(serverLevel);
        this.setOffspringAttributes(agableMob, abstractHorse);
        return abstractHorse;
    }
}

