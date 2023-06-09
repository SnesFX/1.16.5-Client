/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonHoverPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragonPhaseManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final EnderDragon dragon;
    private final DragonPhaseInstance[] phases = new DragonPhaseInstance[EnderDragonPhase.getCount()];
    private DragonPhaseInstance currentPhase;

    public EnderDragonPhaseManager(EnderDragon enderDragon) {
        this.dragon = enderDragon;
        this.setPhase(EnderDragonPhase.HOVERING);
    }

    public void setPhase(EnderDragonPhase<?> enderDragonPhase) {
        if (this.currentPhase != null && enderDragonPhase == this.currentPhase.getPhase()) {
            return;
        }
        if (this.currentPhase != null) {
            this.currentPhase.end();
        }
        this.currentPhase = this.getPhase(enderDragonPhase);
        if (!this.dragon.level.isClientSide) {
            this.dragon.getEntityData().set(EnderDragon.DATA_PHASE, enderDragonPhase.getId());
        }
        LOGGER.debug("Dragon is now in phase {} on the {}", enderDragonPhase, (Object)(this.dragon.level.isClientSide ? "client" : "server"));
        this.currentPhase.begin();
    }

    public DragonPhaseInstance getCurrentPhase() {
        return this.currentPhase;
    }

    public <T extends DragonPhaseInstance> T getPhase(EnderDragonPhase<T> enderDragonPhase) {
        int n = enderDragonPhase.getId();
        if (this.phases[n] == null) {
            this.phases[n] = enderDragonPhase.createInstance(this.dragon);
        }
        return (T)this.phases[n];
    }
}

