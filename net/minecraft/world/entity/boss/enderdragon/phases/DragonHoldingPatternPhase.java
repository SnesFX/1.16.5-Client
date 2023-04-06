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
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonLandingApproachPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonStrafePlayerPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class DragonHoldingPatternPhase
extends AbstractDragonPhaseInstance {
    private static final TargetingConditions NEW_TARGET_TARGETING = new TargetingConditions().range(64.0);
    private Path currentPath;
    private Vec3 targetLocation;
    private boolean clockwise;

    public DragonHoldingPatternPhase(EnderDragon enderDragon) {
        super(enderDragon);
    }

    public EnderDragonPhase<DragonHoldingPatternPhase> getPhase() {
        return EnderDragonPhase.HOLDING_PATTERN;
    }

    @Override
    public void doServerTick() {
        double d;
        double d2 = d = this.targetLocation == null ? 0.0 : this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (d < 100.0 || d > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.findNewTarget();
        }
    }

    @Override
    public void begin() {
        this.currentPath = null;
        this.targetLocation = null;
    }

    @Nullable
    @Override
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    private void findNewTarget() {
        int n;
        if (this.currentPath != null && this.currentPath.isDone()) {
            BlockPos blockPos = this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION));
            int n2 = n = this.dragon.getDragonFight() == null ? 0 : this.dragon.getDragonFight().getCrystalsAlive();
            if (this.dragon.getRandom().nextInt(n + 3) == 0) {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.LANDING_APPROACH);
                return;
            }
            double d = 64.0;
            Player player = this.dragon.level.getNearestPlayer(NEW_TARGET_TARGETING, blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (player != null) {
                d = blockPos.distSqr(player.position(), true) / 512.0;
            }
            if (!(player == null || player.abilities.invulnerable || this.dragon.getRandom().nextInt(Mth.abs((int)d) + 2) != 0 && this.dragon.getRandom().nextInt(n + 2) != 0)) {
                this.strafePlayer(player);
                return;
            }
        }
        if (this.currentPath == null || this.currentPath.isDone()) {
            int n3;
            n = n3 = this.dragon.findClosestNode();
            if (this.dragon.getRandom().nextInt(8) == 0) {
                this.clockwise = !this.clockwise;
                n += 6;
            }
            n = this.clockwise ? ++n : --n;
            if (this.dragon.getDragonFight() == null || this.dragon.getDragonFight().getCrystalsAlive() < 0) {
                n -= 12;
                n &= 7;
                n += 12;
            } else if ((n %= 12) < 0) {
                n += 12;
            }
            this.currentPath = this.dragon.findPath(n3, n, null);
            if (this.currentPath != null) {
                this.currentPath.advance();
            }
        }
        this.navigateToNextPathNode();
    }

    private void strafePlayer(Player player) {
        this.dragon.getPhaseManager().setPhase(EnderDragonPhase.STRAFE_PLAYER);
        this.dragon.getPhaseManager().getPhase(EnderDragonPhase.STRAFE_PLAYER).setTarget(player);
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
    public void onCrystalDestroyed(EndCrystal endCrystal, BlockPos blockPos, DamageSource damageSource, @Nullable Player player) {
        if (player != null && !player.abilities.invulnerable) {
            this.strafePlayer(player);
        }
    }
}

