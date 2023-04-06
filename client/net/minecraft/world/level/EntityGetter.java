/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface EntityGetter {
    public List<Entity> getEntities(@Nullable Entity var1, AABB var2, @Nullable Predicate<? super Entity> var3);

    public <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> var1, AABB var2, @Nullable Predicate<? super T> var3);

    default public <T extends Entity> List<T> getLoadedEntitiesOfClass(Class<? extends T> class_, AABB aABB, @Nullable Predicate<? super T> predicate) {
        return this.getEntitiesOfClass(class_, aABB, predicate);
    }

    public List<? extends Player> players();

    default public List<Entity> getEntities(@Nullable Entity entity, AABB aABB) {
        return this.getEntities(entity, aABB, EntitySelector.NO_SPECTATORS);
    }

    default public boolean isUnobstructed(@Nullable Entity entity, VoxelShape voxelShape) {
        if (voxelShape.isEmpty()) {
            return true;
        }
        for (Entity entity2 : this.getEntities(entity, voxelShape.bounds())) {
            if (entity2.removed || !entity2.blocksBuilding || entity != null && entity2.isPassengerOfSameVehicle(entity) || !Shapes.joinIsNotEmpty(voxelShape, Shapes.create(entity2.getBoundingBox()), BooleanOp.AND)) continue;
            return false;
        }
        return true;
    }

    default public <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> class_, AABB aABB) {
        return this.getEntitiesOfClass(class_, aABB, EntitySelector.NO_SPECTATORS);
    }

    default public <T extends Entity> List<T> getLoadedEntitiesOfClass(Class<? extends T> class_, AABB aABB) {
        return this.getLoadedEntitiesOfClass(class_, aABB, EntitySelector.NO_SPECTATORS);
    }

    default public Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, AABB aABB, Predicate<Entity> predicate) {
        if (aABB.getSize() < 1.0E-7) {
            return Stream.empty();
        }
        AABB aABB2 = aABB.inflate(1.0E-7);
        return this.getEntities(entity, aABB2, predicate.and(entity2 -> entity2.getBoundingBox().intersects(aABB2) && (entity == null ? entity2.canBeCollidedWith() : entity.canCollideWith((Entity)entity2)))).stream().map(Entity::getBoundingBox).map(Shapes::create);
    }

    @Nullable
    default public Player getNearestPlayer(double d, double d2, double d3, double d4, @Nullable Predicate<Entity> predicate) {
        double d5 = -1.0;
        Player player = null;
        for (Player player2 : this.players()) {
            if (predicate != null && !predicate.test(player2)) continue;
            double d6 = player2.distanceToSqr(d, d2, d3);
            if (!(d4 < 0.0) && !(d6 < d4 * d4) || d5 != -1.0 && !(d6 < d5)) continue;
            d5 = d6;
            player = player2;
        }
        return player;
    }

    @Nullable
    default public Player getNearestPlayer(Entity entity, double d) {
        return this.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), d, false);
    }

    @Nullable
    default public Player getNearestPlayer(double d, double d2, double d3, double d4, boolean bl) {
        Predicate<Entity> predicate = bl ? EntitySelector.NO_CREATIVE_OR_SPECTATOR : EntitySelector.NO_SPECTATORS;
        return this.getNearestPlayer(d, d2, d3, d4, predicate);
    }

    default public boolean hasNearbyAlivePlayer(double d, double d2, double d3, double d4) {
        for (Player player : this.players()) {
            if (!EntitySelector.NO_SPECTATORS.test(player) || !EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(player)) continue;
            double d5 = player.distanceToSqr(d, d2, d3);
            if (!(d4 < 0.0) && !(d5 < d4 * d4)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    default public Player getNearestPlayer(TargetingConditions targetingConditions, LivingEntity livingEntity) {
        return this.getNearestEntity(this.players(), targetingConditions, livingEntity, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
    }

    @Nullable
    default public Player getNearestPlayer(TargetingConditions targetingConditions, LivingEntity livingEntity, double d, double d2, double d3) {
        return this.getNearestEntity(this.players(), targetingConditions, livingEntity, d, d2, d3);
    }

    @Nullable
    default public Player getNearestPlayer(TargetingConditions targetingConditions, double d, double d2, double d3) {
        return this.getNearestEntity(this.players(), targetingConditions, null, d, d2, d3);
    }

    @Nullable
    default public <T extends LivingEntity> T getNearestEntity(Class<? extends T> class_, TargetingConditions targetingConditions, @Nullable LivingEntity livingEntity, double d, double d2, double d3, AABB aABB) {
        return this.getNearestEntity(this.getEntitiesOfClass(class_, aABB, null), targetingConditions, livingEntity, d, d2, d3);
    }

    @Nullable
    default public <T extends LivingEntity> T getNearestLoadedEntity(Class<? extends T> class_, TargetingConditions targetingConditions, @Nullable LivingEntity livingEntity, double d, double d2, double d3, AABB aABB) {
        return this.getNearestEntity(this.getLoadedEntitiesOfClass(class_, aABB, null), targetingConditions, livingEntity, d, d2, d3);
    }

    @Nullable
    default public <T extends LivingEntity> T getNearestEntity(List<? extends T> list, TargetingConditions targetingConditions, @Nullable LivingEntity livingEntity, double d, double d2, double d3) {
        double d4 = -1.0;
        LivingEntity livingEntity2 = null;
        for (LivingEntity livingEntity3 : list) {
            if (!targetingConditions.test(livingEntity, livingEntity3)) continue;
            double d5 = livingEntity3.distanceToSqr(d, d2, d3);
            if (d4 != -1.0 && !(d5 < d4)) continue;
            d4 = d5;
            livingEntity2 = livingEntity3;
        }
        return (T)livingEntity2;
    }

    default public List<Player> getNearbyPlayers(TargetingConditions targetingConditions, LivingEntity livingEntity, AABB aABB) {
        ArrayList arrayList = Lists.newArrayList();
        for (Player player : this.players()) {
            if (!aABB.contains(player.getX(), player.getY(), player.getZ()) || !targetingConditions.test(livingEntity, player)) continue;
            arrayList.add(player);
        }
        return arrayList;
    }

    default public <T extends LivingEntity> List<T> getNearbyEntities(Class<? extends T> class_, TargetingConditions targetingConditions, LivingEntity livingEntity, AABB aABB) {
        List<T> list = this.getEntitiesOfClass(class_, aABB, null);
        ArrayList arrayList = Lists.newArrayList();
        for (LivingEntity livingEntity2 : list) {
            if (!targetingConditions.test(livingEntity, livingEntity2)) continue;
            arrayList.add(livingEntity2);
        }
        return arrayList;
    }

    @Nullable
    default public Player getPlayerByUUID(UUID uUID) {
        for (int i = 0; i < this.players().size(); ++i) {
            Player player = this.players().get(i);
            if (!uUID.equals(player.getUUID())) continue;
            return player;
        }
        return null;
    }
}

