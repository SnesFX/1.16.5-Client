/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class MoveThroughVillageGoal
extends Goal {
    protected final PathfinderMob mob;
    private final double speedModifier;
    private Path path;
    private BlockPos poiPos;
    private final boolean onlyAtNight;
    private final List<BlockPos> visited = Lists.newArrayList();
    private final int distanceToPoi;
    private final BooleanSupplier canDealWithDoors;

    public MoveThroughVillageGoal(PathfinderMob pathfinderMob, double d, boolean bl, int n, BooleanSupplier booleanSupplier) {
        this.mob = pathfinderMob;
        this.speedModifier = d;
        this.onlyAtNight = bl;
        this.distanceToPoi = n;
        this.canDealWithDoors = booleanSupplier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        if (!GoalUtils.hasGroundPathNavigation(pathfinderMob)) {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }

    @Override
    public boolean canUse() {
        if (!GoalUtils.hasGroundPathNavigation(this.mob)) {
            return false;
        }
        this.updateVisited();
        if (this.onlyAtNight && this.mob.level.isDay()) {
            return false;
        }
        ServerLevel serverLevel = (ServerLevel)this.mob.level;
        BlockPos blockPos = this.mob.blockPosition();
        if (!serverLevel.isCloseToVillage(blockPos, 6)) {
            return false;
        }
        Vec3 vec3 = RandomPos.getLandPos(this.mob, 15, 7, blockPos2 -> {
            if (!serverLevel.isVillage((BlockPos)blockPos2)) {
                return Double.NEGATIVE_INFINITY;
            }
            Optional<BlockPos> optional = serverLevel.getPoiManager().find(PoiType.ALL, this::hasNotVisited, (BlockPos)blockPos2, 10, PoiManager.Occupancy.IS_OCCUPIED);
            if (!optional.isPresent()) {
                return Double.NEGATIVE_INFINITY;
            }
            return -optional.get().distSqr(blockPos);
        });
        if (vec3 == null) {
            return false;
        }
        Optional<BlockPos> optional = serverLevel.getPoiManager().find(PoiType.ALL, this::hasNotVisited, new BlockPos(vec3), 10, PoiManager.Occupancy.IS_OCCUPIED);
        if (!optional.isPresent()) {
            return false;
        }
        this.poiPos = optional.get().immutable();
        GroundPathNavigation groundPathNavigation = (GroundPathNavigation)this.mob.getNavigation();
        boolean bl = groundPathNavigation.canOpenDoors();
        groundPathNavigation.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
        this.path = groundPathNavigation.createPath(this.poiPos, 0);
        groundPathNavigation.setCanOpenDoors(bl);
        if (this.path == null) {
            Vec3 vec32 = RandomPos.getPosTowards(this.mob, 10, 7, Vec3.atBottomCenterOf(this.poiPos));
            if (vec32 == null) {
                return false;
            }
            groundPathNavigation.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
            this.path = this.mob.getNavigation().createPath(vec32.x, vec32.y, vec32.z, 0);
            groundPathNavigation.setCanOpenDoors(bl);
            if (this.path == null) {
                return false;
            }
        }
        for (int i = 0; i < this.path.getNodeCount(); ++i) {
            Node node = this.path.getNode(i);
            BlockPos blockPos3 = new BlockPos(node.x, node.y + 1, node.z);
            if (!DoorBlock.isWoodenDoor(this.mob.level, blockPos3)) continue;
            this.path = this.mob.getNavigation().createPath(node.x, (double)node.y, node.z, 0);
            break;
        }
        return this.path != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob.getNavigation().isDone()) {
            return false;
        }
        return !this.poiPos.closerThan(this.mob.position(), (double)(this.mob.getBbWidth() + (float)this.distanceToPoi));
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
    }

    @Override
    public void stop() {
        if (this.mob.getNavigation().isDone() || this.poiPos.closerThan(this.mob.position(), (double)this.distanceToPoi)) {
            this.visited.add(this.poiPos);
        }
    }

    private boolean hasNotVisited(BlockPos blockPos) {
        for (BlockPos blockPos2 : this.visited) {
            if (!Objects.equals(blockPos, blockPos2)) continue;
            return false;
        }
        return true;
    }

    private void updateVisited() {
        if (this.visited.size() > 15) {
            this.visited.remove(0);
        }
    }
}

