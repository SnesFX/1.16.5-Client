/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import java.util.Objects;
import java.util.Spliterators;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CollisionSpliterator
extends Spliterators.AbstractSpliterator<VoxelShape> {
    @Nullable
    private final Entity source;
    private final AABB box;
    private final CollisionContext context;
    private final Cursor3D cursor;
    private final BlockPos.MutableBlockPos pos;
    private final VoxelShape entityShape;
    private final CollisionGetter collisionGetter;
    private boolean needsBorderCheck;
    private final BiPredicate<BlockState, BlockPos> predicate;

    public CollisionSpliterator(CollisionGetter collisionGetter, @Nullable Entity entity, AABB aABB) {
        this(collisionGetter, entity, aABB, (blockState, blockPos) -> true);
    }

    public CollisionSpliterator(CollisionGetter collisionGetter, @Nullable Entity entity, AABB aABB, BiPredicate<BlockState, BlockPos> biPredicate) {
        super(Long.MAX_VALUE, 1280);
        this.context = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
        this.pos = new BlockPos.MutableBlockPos();
        this.entityShape = Shapes.create(aABB);
        this.collisionGetter = collisionGetter;
        this.needsBorderCheck = entity != null;
        this.source = entity;
        this.box = aABB;
        this.predicate = biPredicate;
        int n = Mth.floor(aABB.minX - 1.0E-7) - 1;
        int n2 = Mth.floor(aABB.maxX + 1.0E-7) + 1;
        int n3 = Mth.floor(aABB.minY - 1.0E-7) - 1;
        int n4 = Mth.floor(aABB.maxY + 1.0E-7) + 1;
        int n5 = Mth.floor(aABB.minZ - 1.0E-7) - 1;
        int n6 = Mth.floor(aABB.maxZ + 1.0E-7) + 1;
        this.cursor = new Cursor3D(n, n3, n5, n2, n4, n6);
    }

    @Override
    public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
        return this.needsBorderCheck && this.worldBorderCheck(consumer) || this.collisionCheck(consumer);
    }

    boolean collisionCheck(Consumer<? super VoxelShape> consumer) {
        while (this.cursor.advance()) {
            BlockGetter blockGetter;
            int n = this.cursor.nextX();
            int n2 = this.cursor.nextY();
            int n3 = this.cursor.nextZ();
            int n4 = this.cursor.getNextType();
            if (n4 == 3 || (blockGetter = this.getChunk(n, n3)) == null) continue;
            this.pos.set(n, n2, n3);
            BlockState blockState = blockGetter.getBlockState(this.pos);
            if (!this.predicate.test(blockState, this.pos) || n4 == 1 && !blockState.hasLargeCollisionShape() || n4 == 2 && !blockState.is(Blocks.MOVING_PISTON)) continue;
            VoxelShape voxelShape = blockState.getCollisionShape(this.collisionGetter, this.pos, this.context);
            if (voxelShape == Shapes.block()) {
                if (!this.box.intersects(n, n2, n3, (double)n + 1.0, (double)n2 + 1.0, (double)n3 + 1.0)) continue;
                consumer.accept(voxelShape.move(n, n2, n3));
                return true;
            }
            VoxelShape voxelShape2 = voxelShape.move(n, n2, n3);
            if (!Shapes.joinIsNotEmpty(voxelShape2, this.entityShape, BooleanOp.AND)) continue;
            consumer.accept(voxelShape2);
            return true;
        }
        return false;
    }

    @Nullable
    private BlockGetter getChunk(int n, int n2) {
        int n3 = n >> 4;
        int n4 = n2 >> 4;
        return this.collisionGetter.getChunkForCollisions(n3, n4);
    }

    boolean worldBorderCheck(Consumer<? super VoxelShape> consumer) {
        VoxelShape voxelShape;
        Objects.requireNonNull(this.source);
        this.needsBorderCheck = false;
        WorldBorder worldBorder = this.collisionGetter.getWorldBorder();
        AABB aABB = this.source.getBoundingBox();
        if (!CollisionSpliterator.isBoxFullyWithinWorldBorder(worldBorder, aABB) && !CollisionSpliterator.isOutsideBorder(voxelShape = worldBorder.getCollisionShape(), aABB) && CollisionSpliterator.isCloseToBorder(voxelShape, aABB)) {
            consumer.accept(voxelShape);
            return true;
        }
        return false;
    }

    private static boolean isCloseToBorder(VoxelShape voxelShape, AABB aABB) {
        return Shapes.joinIsNotEmpty(voxelShape, Shapes.create(aABB.inflate(1.0E-7)), BooleanOp.AND);
    }

    private static boolean isOutsideBorder(VoxelShape voxelShape, AABB aABB) {
        return Shapes.joinIsNotEmpty(voxelShape, Shapes.create(aABB.deflate(1.0E-7)), BooleanOp.AND);
    }

    public static boolean isBoxFullyWithinWorldBorder(WorldBorder worldBorder, AABB aABB) {
        double d = Mth.floor(worldBorder.getMinX());
        double d2 = Mth.floor(worldBorder.getMinZ());
        double d3 = Mth.ceil(worldBorder.getMaxX());
        double d4 = Mth.ceil(worldBorder.getMaxZ());
        return aABB.minX > d && aABB.minX < d3 && aABB.minZ > d2 && aABB.minZ < d4 && aABB.maxX > d && aABB.maxX < d3 && aABB.maxZ > d2 && aABB.maxZ < d4;
    }
}

