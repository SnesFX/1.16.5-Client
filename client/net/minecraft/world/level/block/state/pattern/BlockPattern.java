/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.MoreObjects$ToStringHelper
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.state.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class BlockPattern {
    private final Predicate<BlockInWorld>[][][] pattern;
    private final int depth;
    private final int height;
    private final int width;

    public BlockPattern(Predicate<BlockInWorld>[][][] arrpredicate) {
        this.pattern = arrpredicate;
        this.depth = arrpredicate.length;
        if (this.depth > 0) {
            this.height = arrpredicate[0].length;
            this.width = this.height > 0 ? arrpredicate[0][0].length : 0;
        } else {
            this.height = 0;
            this.width = 0;
        }
    }

    public int getDepth() {
        return this.depth;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @Nullable
    private BlockPatternMatch matches(BlockPos blockPos, Direction direction, Direction direction2, LoadingCache<BlockPos, BlockInWorld> loadingCache) {
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.height; ++j) {
                for (int k = 0; k < this.depth; ++k) {
                    if (this.pattern[k][j][i].test((BlockInWorld)loadingCache.getUnchecked((Object)BlockPattern.translateAndRotate(blockPos, direction, direction2, i, j, k)))) continue;
                    return null;
                }
            }
        }
        return new BlockPatternMatch(blockPos, direction, direction2, loadingCache, this.width, this.height, this.depth);
    }

    @Nullable
    public BlockPatternMatch find(LevelReader levelReader, BlockPos blockPos) {
        LoadingCache<BlockPos, BlockInWorld> loadingCache = BlockPattern.createLevelCache(levelReader, false);
        int n = Math.max(Math.max(this.width, this.height), this.depth);
        for (BlockPos blockPos2 : BlockPos.betweenClosed(blockPos, blockPos.offset(n - 1, n - 1, n - 1))) {
            for (Direction direction : Direction.values()) {
                for (Direction direction2 : Direction.values()) {
                    BlockPatternMatch blockPatternMatch;
                    if (direction2 == direction || direction2 == direction.getOpposite() || (blockPatternMatch = this.matches(blockPos2, direction, direction2, loadingCache)) == null) continue;
                    return blockPatternMatch;
                }
            }
        }
        return null;
    }

    public static LoadingCache<BlockPos, BlockInWorld> createLevelCache(LevelReader levelReader, boolean bl) {
        return CacheBuilder.newBuilder().build((CacheLoader)new BlockCacheLoader(levelReader, bl));
    }

    protected static BlockPos translateAndRotate(BlockPos blockPos, Direction direction, Direction direction2, int n, int n2, int n3) {
        if (direction == direction2 || direction == direction2.getOpposite()) {
            throw new IllegalArgumentException("Invalid forwards & up combination");
        }
        Vec3i vec3i = new Vec3i(direction.getStepX(), direction.getStepY(), direction.getStepZ());
        Vec3i vec3i2 = new Vec3i(direction2.getStepX(), direction2.getStepY(), direction2.getStepZ());
        Vec3i vec3i3 = vec3i.cross(vec3i2);
        return blockPos.offset(vec3i2.getX() * -n2 + vec3i3.getX() * n + vec3i.getX() * n3, vec3i2.getY() * -n2 + vec3i3.getY() * n + vec3i.getY() * n3, vec3i2.getZ() * -n2 + vec3i3.getZ() * n + vec3i.getZ() * n3);
    }

    public static class BlockPatternMatch {
        private final BlockPos frontTopLeft;
        private final Direction forwards;
        private final Direction up;
        private final LoadingCache<BlockPos, BlockInWorld> cache;
        private final int width;
        private final int height;
        private final int depth;

        public BlockPatternMatch(BlockPos blockPos, Direction direction, Direction direction2, LoadingCache<BlockPos, BlockInWorld> loadingCache, int n, int n2, int n3) {
            this.frontTopLeft = blockPos;
            this.forwards = direction;
            this.up = direction2;
            this.cache = loadingCache;
            this.width = n;
            this.height = n2;
            this.depth = n3;
        }

        public BlockPos getFrontTopLeft() {
            return this.frontTopLeft;
        }

        public Direction getForwards() {
            return this.forwards;
        }

        public Direction getUp() {
            return this.up;
        }

        public BlockInWorld getBlock(int n, int n2, int n3) {
            return (BlockInWorld)this.cache.getUnchecked((Object)BlockPattern.translateAndRotate(this.frontTopLeft, this.getForwards(), this.getUp(), n, n2, n3));
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("up", (Object)this.up).add("forwards", (Object)this.forwards).add("frontTopLeft", (Object)this.frontTopLeft).toString();
        }
    }

    static class BlockCacheLoader
    extends CacheLoader<BlockPos, BlockInWorld> {
        private final LevelReader level;
        private final boolean loadChunks;

        public BlockCacheLoader(LevelReader levelReader, boolean bl) {
            this.level = levelReader;
            this.loadChunks = bl;
        }

        public BlockInWorld load(BlockPos blockPos) throws Exception {
            return new BlockInWorld(this.level, blockPos, this.loadChunks);
        }

        public /* synthetic */ Object load(Object object) throws Exception {
            return this.load((BlockPos)object);
        }
    }

}

