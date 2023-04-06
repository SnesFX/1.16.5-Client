/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.animal;

import java.util.Random;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public abstract class WaterAnimal
extends PathfinderMob {
    protected WaterAnimal(EntityType<? extends WaterAnimal> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    protected int getExperienceReward(Player player) {
        return 1 + this.level.random.nextInt(3);
    }

    protected void handleAirSupply(int n) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(n - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(DamageSource.DROWN, 2.0f);
            }
        } else {
            this.setAirSupply(300);
        }
    }

    @Override
    public void baseTick() {
        int n = this.getAirSupply();
        super.baseTick();
        this.handleAirSupply(n);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }
}

