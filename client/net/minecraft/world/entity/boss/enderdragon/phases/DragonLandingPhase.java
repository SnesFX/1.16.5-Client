/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingFlamingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingScanningPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.Vec3;

public class DragonLandingPhase
extends AbstractDragonPhaseInstance {
    private Vec3 targetLocation;

    public DragonLandingPhase(EnderDragon enderDragon) {
        super(enderDragon);
    }

    @Override
    public void doClientTick() {
        Vec3 vec3 = this.dragon.getHeadLookVector(1.0f).normalize();
        vec3.yRot(-0.7853982f);
        double d = this.dragon.head.getX();
        double d2 = this.dragon.head.getY(0.5);
        double d3 = this.dragon.head.getZ();
        for (int i = 0; i < 8; ++i) {
            Random random = this.dragon.getRandom();
            double d4 = d + random.nextGaussian() / 2.0;
            double d5 = d2 + random.nextGaussian() / 2.0;
            double d6 = d3 + random.nextGaussian() / 2.0;
            Vec3 vec32 = this.dragon.getDeltaMovement();
            this.dragon.level.addParticle(ParticleTypes.DRAGON_BREATH, d4, d5, d6, -vec3.x * 0.07999999821186066 + vec32.x, -vec3.y * 0.30000001192092896 + vec32.y, -vec3.z * 0.07999999821186066 + vec32.z);
            vec3.yRot(0.19634955f);
        }
    }

    @Override
    public void doServerTick() {
        if (this.targetLocation == null) {
            this.targetLocation = Vec3.atBottomCenterOf(this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION));
        }
        if (this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ()) < 1.0) {
            this.dragon.getPhaseManager().getPhase(EnderDragonPhase.SITTING_FLAMING).resetFlameCount();
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_SCANNING);
        }
    }

    @Override
    public float getFlySpeed() {
        return 1.5f;
    }

    @Override
    public float getTurnSpeed() {
        float f = Mth.sqrt(Entity.getHorizontalDistanceSqr(this.dragon.getDeltaMovement())) + 1.0f;
        float f2 = Math.min(f, 40.0f);
        return f2 / f;
    }

    @Override
    public void begin() {
        this.targetLocation = null;
    }

    @Nullable
    @Override
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    public EnderDragonPhase<DragonLandingPhase> getPhase() {
        return EnderDragonPhase.LANDING;
    }
}

