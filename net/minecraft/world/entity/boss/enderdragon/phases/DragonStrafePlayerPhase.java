/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonHoldingPatternPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DragonStrafePlayerPhase
extends AbstractDragonPhaseInstance {
    private static final Logger LOGGER = LogManager.getLogger();
    private int fireballCharge;
    private Path currentPath;
    private Vec3 targetLocation;
    private LivingEntity attackTarget;
    private boolean holdingPatternClockwise;

    public DragonStrafePlayerPhase(EnderDragon enderDragon) {
        super(enderDragon);
    }

    @Override
    public void doServerTick() {
        double d;
        double d2;
        double d3;
        if (this.attackTarget == null) {
            LOGGER.warn("Skipping player strafe phase because no player was found");
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            return;
        }
        if (this.currentPath != null && this.currentPath.isDone()) {
            d3 = this.attackTarget.getX();
            d2 = this.attackTarget.getZ();
            double d4 = d3 - this.dragon.getX();
            double d5 = d2 - this.dragon.getZ();
            d = Mth.sqrt(d4 * d4 + d5 * d5);
            double d6 = Math.min(0.4000000059604645 + d / 80.0 - 1.0, 10.0);
            this.targetLocation = new Vec3(d3, this.attackTarget.getY() + d6, d2);
        }
        double d7 = d3 = this.targetLocation == null ? 0.0 : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (d3 < 100.0 || d3 > 22500.0) {
            this.findNewTarget();
        }
        d2 = 64.0;
        if (this.attackTarget.distanceToSqr(this.dragon) < 4096.0) {
            if (this.dragon.canSee(this.attackTarget)) {
                ++this.fireballCharge;
                Vec3 vec3 = new Vec3(this.attackTarget.getX() - this.dragon.getX(), 0.0, this.attackTarget.getZ() - this.dragon.getZ()).normalize();
                Vec3 vec32 = new Vec3(Mth.sin(this.dragon.yRot * 0.017453292f), 0.0, -Mth.cos(this.dragon.yRot * 0.017453292f)).normalize();
                float f = (float)vec32.dot(vec3);
                float f2 = (float)(Math.acos(f) * 57.2957763671875);
                f2 += 0.5f;
                if (this.fireballCharge >= 5 && f2 >= 0.0f && f2 < 10.0f) {
                    d = 1.0;
                    Vec3 vec33 = this.dragon.getViewVector(1.0f);
                    double d8 = this.dragon.head.getX() - vec33.x * 1.0;
                    double d9 = this.dragon.head.getY(0.5) + 0.5;
                    double d10 = this.dragon.head.getZ() - vec33.z * 1.0;
                    double d11 = this.attackTarget.getX() - d8;
                    double d12 = this.attackTarget.getY(0.5) - d9;
                    double d13 = this.attackTarget.getZ() - d10;
                    if (!this.dragon.isSilent()) {
                        this.dragon.level.levelEvent(null, 1017, this.dragon.blockPosition(), 0);
                    }
                    DragonFireball dragonFireball = new DragonFireball(this.dragon.level, this.dragon, d11, d12, d13);
                    dragonFireball.moveTo(d8, d9, d10, 0.0f, 0.0f);
                    this.dragon.level.addFreshEntity(dragonFireball);
                    this.fireballCharge = 0;
                    if (this.currentPath != null) {
                        while (!this.currentPath.isDone()) {
                            this.currentPath.advance();
                        }
                    }
                    this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
                }
            } else if (this.fireballCharge > 0) {
                --this.fireballCharge;
            }
        } else if (this.fireballCharge > 0) {
            --this.fireballCharge;
        }
    }

    private void findNewTarget() {
        if (this.currentPath == null || this.currentPath.isDone()) {
            int n;
            int n2 = n = this.dragon.findClosestNode();
            if (this.dragon.getRandom().nextInt(8) == 0) {
                this.holdingPatternClockwise = !this.holdingPatternClockwise;
                n2 += 6;
            }
            n2 = this.holdingPatternClockwise ? ++n2 : --n2;
            if (this.dragon.getDragonFight() == null || this.dragon.getDragonFight().getCrystalsAlive() <= 0) {
                n2 -= 12;
                n2 &= 7;
                n2 += 12;
            } else if ((n2 %= 12) < 0) {
                n2 += 12;
            }
            this.currentPath = this.dragon.findPath(n, n2, null);
            if (this.currentPath != null) {
                this.currentPath.advance();
            }
        }
        this.navigateToNextPathNode();
    }

    private void navigateToNextPathNode() {
        if (this.currentPath != null && !this.currentPath.isDone()) {
            double d;
            BlockPos blockPos = this.currentPath.getNextNodePos();
            this.currentPath.advance();
            double d2 = blockPos.getX();
            double d3 = blockPos.getZ();
            while ((d = (double)((float)blockPos.getY() + this.dragon.getRandom().nextFloat() * 20.0f)) < (double)blockPos.getY()) {
            }
            this.targetLocation = new Vec3(d2, d, d3);
        }
    }

    @Override
    public void begin() {
        this.fireballCharge = 0;
        this.targetLocation = null;
        this.currentPath = null;
        this.attackTarget = null;
    }

    public void setTarget(LivingEntity livingEntity) {
        this.attackTarget = livingEntity;
        int n = this.dragon.findClosestNode();
        int n2 = this.dragon.findClosestNode(this.attackTarget.getX(), this.attackTarget.getY(), this.attackTarget.getZ());
        int n3 = Mth.floor(this.attackTarget.getX());
        int n4 = Mth.floor(this.attackTarget.getZ());
        double d = (double)n3 - this.dragon.getX();
        double d2 = (double)n4 - this.dragon.getZ();
        double d3 = Mth.sqrt(d * d + d2 * d2);
        double d4 = Math.min(0.4000000059604645 + d3 / 80.0 - 1.0, 10.0);
        int n5 = Mth.floor(this.attackTarget.getY() + d4);
        Node node = new Node(n3, n5, n4);
        this.currentPath = this.dragon.findPath(n, n2, node);
        if (this.currentPath != null) {
            this.currentPath.advance();
            this.navigateToNextPathNode();
        }
    }

    @Nullable
    @Override
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    public EnderDragonPhase<DragonStrafePlayerPhase> getPhase() {
        return EnderDragonPhase.STRAFE_PLAYER;
    }
}

