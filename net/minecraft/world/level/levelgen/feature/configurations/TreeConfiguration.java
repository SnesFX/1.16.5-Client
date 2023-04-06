/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P9
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function9
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function9;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

public class TreeConfiguration
implements FeatureConfiguration {
    public static final Codec<TreeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter(treeConfiguration -> treeConfiguration.trunkProvider), (App)BlockStateProvider.CODEC.fieldOf("leaves_provider").forGetter(treeConfiguration -> treeConfiguration.leavesProvider), (App)FoliagePlacer.CODEC.fieldOf("foliage_placer").forGetter(treeConfiguration -> treeConfiguration.foliagePlacer), (App)TrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter(treeConfiguration -> treeConfiguration.trunkPlacer), (App)FeatureSize.CODEC.fieldOf("minimum_size").forGetter(treeConfiguration -> treeConfiguration.minimumSize), (App)TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter(treeConfiguration -> treeConfiguration.decorators), (App)Codec.INT.fieldOf("max_water_depth").orElse((Object)0).forGetter(treeConfiguration -> treeConfiguration.maxWaterDepth), (App)Codec.BOOL.fieldOf("ignore_vines").orElse((Object)false).forGetter(treeConfiguration -> treeConfiguration.ignoreVines), (App)Heightmap.Types.CODEC.fieldOf("heightmap").forGetter(treeConfiguration -> treeConfiguration.heightmap)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6, arg_7, arg_8) -> TreeConfiguration.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6, arg_7, arg_8)));
    public final BlockStateProvider trunkProvider;
    public final BlockStateProvider leavesProvider;
    public final List<TreeDecorator> decorators;
    public transient boolean fromSapling;
    public final FoliagePlacer foliagePlacer;
    public final TrunkPlacer trunkPlacer;
    public final FeatureSize minimumSize;
    public final int maxWaterDepth;
    public final boolean ignoreVines;
    public final Heightmap.Types heightmap;

    protected TreeConfiguration(BlockStateProvider blockStateProvider, BlockStateProvider blockStateProvider2, FoliagePlacer foliagePlacer, TrunkPlacer trunkPlacer, FeatureSize featureSize, List<TreeDecorator> list, int n, boolean bl, Heightmap.Types types) {
        this.trunkProvider = blockStateProvider;
        this.leavesProvider = blockStateProvider2;
        this.decorators = list;
        this.foliagePlacer = foliagePlacer;
        this.minimumSize = featureSize;
        this.trunkPlacer = trunkPlacer;
        this.maxWaterDepth = n;
        this.ignoreVines = bl;
        this.heightmap = types;
    }

    public void setFromSapling() {
        this.fromSapling = true;
    }

    public TreeConfiguration withDecorators(List<TreeDecorator> list) {
        return new TreeConfiguration(this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.trunkPlacer, this.minimumSize, list, this.maxWaterDepth, this.ignoreVines, this.heightmap);
    }

    public static class TreeConfigurationBuilder {
        public final BlockStateProvider trunkProvider;
        public final BlockStateProvider leavesProvider;
        private final FoliagePlacer foliagePlacer;
        private final TrunkPlacer trunkPlacer;
        private final FeatureSize minimumSize;
        private List<TreeDecorator> decorators = ImmutableList.of();
        private int maxWaterDepth;
        private boolean ignoreVines;
        private Heightmap.Types heightmap = Heightmap.Types.OCEAN_FLOOR;

        public TreeConfigurationBuilder(BlockStateProvider blockStateProvider, BlockStateProvider blockStateProvider2, FoliagePlacer foliagePlacer, TrunkPlacer trunkPlacer, FeatureSize featureSize) {
            this.trunkProvider = blockStateProvider;
            this.leavesProvider = blockStateProvider2;
            this.foliagePlacer = foliagePlacer;
            this.trunkPlacer = trunkPlacer;
            this.minimumSize = featureSize;
        }

        public TreeConfigurationBuilder decorators(List<TreeDecorator> list) {
            this.decorators = list;
            return this;
        }

        public TreeConfigurationBuilder maxWaterDepth(int n) {
            this.maxWaterDepth = n;
            return this;
        }

        public TreeConfigurationBuilder ignoreVines() {
            this.ignoreVines = true;
            return this;
        }

        public TreeConfigurationBuilder heightmap(Heightmap.Types types) {
            this.heightmap = types;
            return this;
        }

        public TreeConfiguration build() {
            return new TreeConfiguration(this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.trunkPlacer, this.minimumSize, this.decorators, this.maxWaterDepth, this.ignoreVines, this.heightmap);
        }
    }

}

