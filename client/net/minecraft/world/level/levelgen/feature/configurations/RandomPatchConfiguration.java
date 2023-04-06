/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P11
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function11
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function11;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacer;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class RandomPatchConfiguration
implements FeatureConfiguration {
    public static final Codec<RandomPatchConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(randomPatchConfiguration -> randomPatchConfiguration.stateProvider), (App)BlockPlacer.CODEC.fieldOf("block_placer").forGetter(randomPatchConfiguration -> randomPatchConfiguration.blockPlacer), (App)BlockState.CODEC.listOf().fieldOf("whitelist").forGetter(randomPatchConfiguration -> randomPatchConfiguration.whitelist.stream().map(Block::defaultBlockState).collect(Collectors.toList())), (App)BlockState.CODEC.listOf().fieldOf("blacklist").forGetter(randomPatchConfiguration -> ImmutableList.copyOf(randomPatchConfiguration.blacklist)), (App)Codec.INT.fieldOf("tries").orElse((Object)128).forGetter(randomPatchConfiguration -> randomPatchConfiguration.tries), (App)Codec.INT.fieldOf("xspread").orElse((Object)7).forGetter(randomPatchConfiguration -> randomPatchConfiguration.xspread), (App)Codec.INT.fieldOf("yspread").orElse((Object)3).forGetter(randomPatchConfiguration -> randomPatchConfiguration.yspread), (App)Codec.INT.fieldOf("zspread").orElse((Object)7).forGetter(randomPatchConfiguration -> randomPatchConfiguration.zspread), (App)Codec.BOOL.fieldOf("can_replace").orElse((Object)false).forGetter(randomPatchConfiguration -> randomPatchConfiguration.canReplace), (App)Codec.BOOL.fieldOf("project").orElse((Object)true).forGetter(randomPatchConfiguration -> randomPatchConfiguration.project), (App)Codec.BOOL.fieldOf("need_water").orElse((Object)false).forGetter(randomPatchConfiguration -> randomPatchConfiguration.needWater)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6, arg_7, arg_8, arg_9, arg_10) -> RandomPatchConfiguration.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6, arg_7, arg_8, arg_9, arg_10)));
    public final BlockStateProvider stateProvider;
    public final BlockPlacer blockPlacer;
    public final Set<Block> whitelist;
    public final Set<BlockState> blacklist;
    public final int tries;
    public final int xspread;
    public final int yspread;
    public final int zspread;
    public final boolean canReplace;
    public final boolean project;
    public final boolean needWater;

    private RandomPatchConfiguration(BlockStateProvider blockStateProvider, BlockPlacer blockPlacer, List<BlockState> list, List<BlockState> list2, int n, int n2, int n3, int n4, boolean bl, boolean bl2, boolean bl3) {
        this(blockStateProvider, blockPlacer, list.stream().map(BlockBehaviour.BlockStateBase::getBlock).collect(Collectors.toSet()), (Set<BlockState>)ImmutableSet.copyOf(list2), n, n2, n3, n4, bl, bl2, bl3);
    }

    private RandomPatchConfiguration(BlockStateProvider blockStateProvider, BlockPlacer blockPlacer, Set<Block> set, Set<BlockState> set2, int n, int n2, int n3, int n4, boolean bl, boolean bl2, boolean bl3) {
        this.stateProvider = blockStateProvider;
        this.blockPlacer = blockPlacer;
        this.whitelist = set;
        this.blacklist = set2;
        this.tries = n;
        this.xspread = n2;
        this.yspread = n3;
        this.zspread = n4;
        this.canReplace = bl;
        this.project = bl2;
        this.needWater = bl3;
    }

    public static class GrassConfigurationBuilder {
        private final BlockStateProvider stateProvider;
        private final BlockPlacer blockPlacer;
        private Set<Block> whitelist = ImmutableSet.of();
        private Set<BlockState> blacklist = ImmutableSet.of();
        private int tries = 64;
        private int xspread = 7;
        private int yspread = 3;
        private int zspread = 7;
        private boolean canReplace;
        private boolean project = true;
        private boolean needWater = false;

        public GrassConfigurationBuilder(BlockStateProvider blockStateProvider, BlockPlacer blockPlacer) {
            this.stateProvider = blockStateProvider;
            this.blockPlacer = blockPlacer;
        }

        public GrassConfigurationBuilder whitelist(Set<Block> set) {
            this.whitelist = set;
            return this;
        }

        public GrassConfigurationBuilder blacklist(Set<BlockState> set) {
            this.blacklist = set;
            return this;
        }

        public GrassConfigurationBuilder tries(int n) {
            this.tries = n;
            return this;
        }

        public GrassConfigurationBuilder xspread(int n) {
            this.xspread = n;
            return this;
        }

        public GrassConfigurationBuilder yspread(int n) {
            this.yspread = n;
            return this;
        }

        public GrassConfigurationBuilder zspread(int n) {
            this.zspread = n;
            return this;
        }

        public GrassConfigurationBuilder canReplace() {
            this.canReplace = true;
            return this;
        }

        public GrassConfigurationBuilder noProjection() {
            this.project = false;
            return this;
        }

        public GrassConfigurationBuilder needWater() {
            this.needWater = true;
            return this;
        }

        public RandomPatchConfiguration build() {
            return new RandomPatchConfiguration(this.stateProvider, this.blockPlacer, this.whitelist, this.blacklist, this.tries, this.xspread, this.yspread, this.zspread, this.canReplace, this.project, this.needWater);
        }
    }

}

