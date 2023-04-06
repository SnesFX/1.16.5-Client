/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionSpliterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CollisionGetter
extends BlockGetter {
    public WorldBorder getWorldBorder();

    @Nullable
    public BlockGetter getChunkForCollisions(int var1, int var2);

    default public boolean isUnobstructed(@Nullable Entity entity, VoxelShape voxelShape) {
        return true;
    }

    default public boolean isUnobstructed(BlockState blockState, BlockPos blockPos, CollisionContext collisionContext) {
        VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos, collisionContext);
        return voxelShape.isEmpty() || this.isUnobstructed(null, voxelShape.move(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
    }

    default public boolean isUnobstructed(Entity entity) {
        return this.isUnobstructed(entity, Shapes.create(entity.getBoundingBox()));
    }

    default public boolean noCollision(AABB aABB) {
        return this.noCollision(null, aABB, entity -> true);
    }

    default public boolean noCollision(Entity entity2) {
        return this.noCollision(entity2, entity2.getBoundingBox(), entity -> true);
    }

    default public boolean noCollision(Entity entity2, AABB aABB) {
        return this.noCollision(entity2, aABB, entity -> true);
    }

    default public boolean noCollision(@Nullable Entity entity, AABB aABB, Predicate<Entity> predicate) {
        return this.getCollisions(entity, aABB, predicate).allMatch(VoxelShape::isEmpty);
    }

    public Stream<VoxelShape> getEntityCollisions(@Nullable Entity var1, AABB var2, Predicate<Entity> var3);

    default public Stream<VoxelShape> getCollisions(@Nullable Entity entity, AABB aABB, Predicate<Entity> predicate) {
        return Stream.concat(this.getBlockCollisions(entity, aABB), this.getEntityCollisions(entity, aABB, predicate));
    }

    default public Stream<VoxelShape> getBlockCollisions(@Nullable Entity entity, AABB aABB) {
        return StreamSupport.stream(new CollisionSpliterator(this, entity, aABB), false);
    }

    default public boolean noBlockCollision(@Nullable Entity entity, AABB aABB, BiPredicate<BlockState, BlockPos> biPredicate) {
        return this.getBlockCollisions(entity, aABB, biPredicate).allMatch(VoxelShape::isEmpty);
    }

    default public Stream<VoxelShape> getBlockCollisions(@Nullable Entity entity, AABB aABB, BiPredicate<BlockState, BlockPos> biPredicate) {
        return StreamSupport.stream(new CollisionSpliterator(this, entity, aABB, biPredicate), false);
    }
}

