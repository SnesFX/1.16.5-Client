/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P5
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function5
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import net.minecraft.world.phys.AABB;

public class SpikeFeature
extends Feature<SpikeConfiguration> {
    private static final LoadingCache<Long, List<EndSpike>> SPIKE_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build((CacheLoader)new SpikeCacheLoader());

    public SpikeFeature(Codec<SpikeConfiguration> codec) {
        super(codec);
    }

    public static List<EndSpike> getSpikesForLevel(WorldGenLevel worldGenLevel) {
        Random random = new Random(worldGenLevel.getSeed());
        long l = random.nextLong() & 0xFFFFL;
        return (List)SPIKE_CACHE.getUnchecked((Object)l);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SpikeConfiguration spikeConfiguration) {
        List<EndSpike> list = spikeConfiguration.getSpikes();
        if (list.isEmpty()) {
            list = SpikeFeature.getSpikesForLevel(worldGenLevel);
        }
        for (EndSpike endSpike : list) {
            if (!endSpike.isCenterWithinChunk(blockPos)) continue;
            this.placeSpike(worldGenLevel, random, spikeConfiguration, endSpike);
        }
        return true;
    }

    private void placeSpike(ServerLevelAccessor serverLevelAccessor, Random random, SpikeConfiguration spikeConfiguration, EndSpike endSpike) {
        int n = endSpike.getRadius();
        for (BlockPos blockPos : BlockPos.betweenClosed(new BlockPos(endSpike.getCenterX() - n, 0, endSpike.getCenterZ() - n), new BlockPos(endSpike.getCenterX() + n, endSpike.getHeight() + 10, endSpike.getCenterZ() + n))) {
            if (blockPos.distSqr(endSpike.getCenterX(), blockPos.getY(), endSpike.getCenterZ(), false) <= (double)(n * n + 1) && blockPos.getY() < endSpike.getHeight()) {
                this.setBlock(serverLevelAccessor, blockPos, Blocks.OBSIDIAN.defaultBlockState());
                continue;
            }
            if (blockPos.getY() <= 65) continue;
            this.setBlock(serverLevelAccessor, blockPos, Blocks.AIR.defaultBlockState());
        }
        if (endSpike.isGuarded()) {
            int n2 = -2;
            int n3 = 2;
            int n4 = 3;
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            for (int i = -2; i <= 2; ++i) {
                for (int j = -2; j <= 2; ++j) {
                    for (int k = 0; k <= 3; ++k) {
                        boolean bl;
                        boolean bl2 = Mth.abs(i) == 2;
                        boolean bl3 = Mth.abs(j) == 2;
                        boolean bl4 = bl = k == 3;
                        if (!bl2 && !bl3 && !bl) continue;
                        boolean bl5 = i == -2 || i == 2 || bl;
                        boolean bl6 = j == -2 || j == 2 || bl;
                        BlockState blockState = (BlockState)((BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, bl5 && j != -2)).setValue(IronBarsBlock.SOUTH, bl5 && j != 2)).setValue(IronBarsBlock.WEST, bl6 && i != -2)).setValue(IronBarsBlock.EAST, bl6 && i != 2);
                        this.setBlock(serverLevelAccessor, mutableBlockPos.set(endSpike.getCenterX() + i, endSpike.getHeight() + k, endSpike.getCenterZ() + j), blockState);
                    }
                }
            }
        }
        EndCrystal endCrystal = EntityType.END_CRYSTAL.create(serverLevelAccessor.getLevel());
        endCrystal.setBeamTarget(spikeConfiguration.getCrystalBeamTarget());
        endCrystal.setInvulnerable(spikeConfiguration.isCrystalInvulnerable());
        endCrystal.moveTo((double)endSpike.getCenterX() + 0.5, endSpike.getHeight() + 1, (double)endSpike.getCenterZ() + 0.5, random.nextFloat() * 360.0f, 0.0f);
        serverLevelAccessor.addFreshEntity(endCrystal);
        this.setBlock(serverLevelAccessor, new BlockPos(endSpike.getCenterX(), endSpike.getHeight(), endSpike.getCenterZ()), Blocks.BEDROCK.defaultBlockState());
    }

    static class SpikeCacheLoader
    extends CacheLoader<Long, List<EndSpike>> {
        private SpikeCacheLoader() {
        }

        public List<EndSpike> load(Long l) {
            List list = IntStream.range(0, 10).boxed().collect(Collectors.toList());
            Collections.shuffle(list, new Random(l));
            ArrayList arrayList = Lists.newArrayList();
            for (int i = 0; i < 10; ++i) {
                int n = Mth.floor(42.0 * Math.cos(2.0 * (-3.141592653589793 + 0.3141592653589793 * (double)i)));
                int n2 = Mth.floor(42.0 * Math.sin(2.0 * (-3.141592653589793 + 0.3141592653589793 * (double)i)));
                int n3 = (Integer)list.get(i);
                int n4 = 2 + n3 / 3;
                int n5 = 76 + n3 * 3;
                boolean bl = n3 == 1 || n3 == 2;
                arrayList.add(new EndSpike(n, n2, n4, n5, bl));
            }
            return arrayList;
        }

        public /* synthetic */ Object load(Object object) throws Exception {
            return this.load((Long)object);
        }
    }

    public static class EndSpike {
        public static final Codec<EndSpike> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("centerX").orElse((Object)0).forGetter(endSpike -> endSpike.centerX), (App)Codec.INT.fieldOf("centerZ").orElse((Object)0).forGetter(endSpike -> endSpike.centerZ), (App)Codec.INT.fieldOf("radius").orElse((Object)0).forGetter(endSpike -> endSpike.radius), (App)Codec.INT.fieldOf("height").orElse((Object)0).forGetter(endSpike -> endSpike.height), (App)Codec.BOOL.fieldOf("guarded").orElse((Object)false).forGetter(endSpike -> endSpike.guarded)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4) -> EndSpike.new(arg_0, arg_1, arg_2, arg_3, arg_4)));
        private final int centerX;
        private final int centerZ;
        private final int radius;
        private final int height;
        private final boolean guarded;
        private final AABB topBoundingBox;

        public EndSpike(int n, int n2, int n3, int n4, boolean bl) {
            this.centerX = n;
            this.centerZ = n2;
            this.radius = n3;
            this.height = n4;
            this.guarded = bl;
            this.topBoundingBox = new AABB(n - n3, 0.0, n2 - n3, n + n3, 256.0, n2 + n3);
        }

        public boolean isCenterWithinChunk(BlockPos blockPos) {
            return blockPos.getX() >> 4 == this.centerX >> 4 && blockPos.getZ() >> 4 == this.centerZ >> 4;
        }

        public int getCenterX() {
            return this.centerX;
        }

        public int getCenterZ() {
            return this.centerZ;
        }

        public int getRadius() {
            return this.radius;
        }

        public int getHeight() {
            return this.height;
        }

        public boolean isGuarded() {
            return this.guarded;
        }

        public AABB getTopBoundingBox() {
            return this.topBoundingBox;
        }
    }

}

