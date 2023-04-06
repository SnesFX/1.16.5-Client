/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 */
package net.minecraft.world.level.newbiome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.function.LongFunction;
import net.minecraft.Util;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.area.LazyArea;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.LazyAreaContext;
import net.minecraft.world.level.newbiome.layer.AddDeepOceanLayer;
import net.minecraft.world.level.newbiome.layer.AddEdgeLayer;
import net.minecraft.world.level.newbiome.layer.AddIslandLayer;
import net.minecraft.world.level.newbiome.layer.AddMushroomIslandLayer;
import net.minecraft.world.level.newbiome.layer.AddSnowLayer;
import net.minecraft.world.level.newbiome.layer.BiomeEdgeLayer;
import net.minecraft.world.level.newbiome.layer.BiomeInitLayer;
import net.minecraft.world.level.newbiome.layer.IslandLayer;
import net.minecraft.world.level.newbiome.layer.Layer;
import net.minecraft.world.level.newbiome.layer.OceanLayer;
import net.minecraft.world.level.newbiome.layer.OceanMixerLayer;
import net.minecraft.world.level.newbiome.layer.RareBiomeLargeLayer;
import net.minecraft.world.level.newbiome.layer.RareBiomeSpotLayer;
import net.minecraft.world.level.newbiome.layer.RegionHillsLayer;
import net.minecraft.world.level.newbiome.layer.RemoveTooMuchOceanLayer;
import net.minecraft.world.level.newbiome.layer.RiverInitLayer;
import net.minecraft.world.level.newbiome.layer.RiverLayer;
import net.minecraft.world.level.newbiome.layer.RiverMixerLayer;
import net.minecraft.world.level.newbiome.layer.ShoreLayer;
import net.minecraft.world.level.newbiome.layer.SmoothLayer;
import net.minecraft.world.level.newbiome.layer.ZoomLayer;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;

public class Layers {
    private static final Int2IntMap CATEGORIES = (Int2IntMap)Util.make(new Int2IntOpenHashMap(), int2IntOpenHashMap -> {
        Layers.register(int2IntOpenHashMap, Category.BEACH, 16);
        Layers.register(int2IntOpenHashMap, Category.BEACH, 26);
        Layers.register(int2IntOpenHashMap, Category.DESERT, 2);
        Layers.register(int2IntOpenHashMap, Category.DESERT, 17);
        Layers.register(int2IntOpenHashMap, Category.DESERT, 130);
        Layers.register(int2IntOpenHashMap, Category.EXTREME_HILLS, 131);
        Layers.register(int2IntOpenHashMap, Category.EXTREME_HILLS, 162);
        Layers.register(int2IntOpenHashMap, Category.EXTREME_HILLS, 20);
        Layers.register(int2IntOpenHashMap, Category.EXTREME_HILLS, 3);
        Layers.register(int2IntOpenHashMap, Category.EXTREME_HILLS, 34);
        Layers.register(int2IntOpenHashMap, Category.FOREST, 27);
        Layers.register(int2IntOpenHashMap, Category.FOREST, 28);
        Layers.register(int2IntOpenHashMap, Category.FOREST, 29);
        Layers.register(int2IntOpenHashMap, Category.FOREST, 157);
        Layers.register(int2IntOpenHashMap, Category.FOREST, 132);
        Layers.register(int2IntOpenHashMap, Category.FOREST, 4);
        Layers.register(int2IntOpenHashMap, Category.FOREST, 155);
        Layers.register(int2IntOpenHashMap, Category.FOREST, 156);
        Layers.register(int2IntOpenHashMap, Category.FOREST, 18);
        Layers.register(int2IntOpenHashMap, Category.ICY, 140);
        Layers.register(int2IntOpenHashMap, Category.ICY, 13);
        Layers.register(int2IntOpenHashMap, Category.ICY, 12);
        Layers.register(int2IntOpenHashMap, Category.JUNGLE, 168);
        Layers.register(int2IntOpenHashMap, Category.JUNGLE, 169);
        Layers.register(int2IntOpenHashMap, Category.JUNGLE, 21);
        Layers.register(int2IntOpenHashMap, Category.JUNGLE, 23);
        Layers.register(int2IntOpenHashMap, Category.JUNGLE, 22);
        Layers.register(int2IntOpenHashMap, Category.JUNGLE, 149);
        Layers.register(int2IntOpenHashMap, Category.JUNGLE, 151);
        Layers.register(int2IntOpenHashMap, Category.MESA, 37);
        Layers.register(int2IntOpenHashMap, Category.MESA, 165);
        Layers.register(int2IntOpenHashMap, Category.MESA, 167);
        Layers.register(int2IntOpenHashMap, Category.MESA, 166);
        Layers.register(int2IntOpenHashMap, Category.BADLANDS_PLATEAU, 39);
        Layers.register(int2IntOpenHashMap, Category.BADLANDS_PLATEAU, 38);
        Layers.register(int2IntOpenHashMap, Category.MUSHROOM, 14);
        Layers.register(int2IntOpenHashMap, Category.MUSHROOM, 15);
        Layers.register(int2IntOpenHashMap, Category.NONE, 25);
        Layers.register(int2IntOpenHashMap, Category.OCEAN, 46);
        Layers.register(int2IntOpenHashMap, Category.OCEAN, 49);
        Layers.register(int2IntOpenHashMap, Category.OCEAN, 50);
        Layers.register(int2IntOpenHashMap, Category.OCEAN, 48);
        Layers.register(int2IntOpenHashMap, Category.OCEAN, 24);
        Layers.register(int2IntOpenHashMap, Category.OCEAN, 47);
        Layers.register(int2IntOpenHashMap, Category.OCEAN, 10);
        Layers.register(int2IntOpenHashMap, Category.OCEAN, 45);
        Layers.register(int2IntOpenHashMap, Category.OCEAN, 0);
        Layers.register(int2IntOpenHashMap, Category.OCEAN, 44);
        Layers.register(int2IntOpenHashMap, Category.PLAINS, 1);
        Layers.register(int2IntOpenHashMap, Category.PLAINS, 129);
        Layers.register(int2IntOpenHashMap, Category.RIVER, 11);
        Layers.register(int2IntOpenHashMap, Category.RIVER, 7);
        Layers.register(int2IntOpenHashMap, Category.SAVANNA, 35);
        Layers.register(int2IntOpenHashMap, Category.SAVANNA, 36);
        Layers.register(int2IntOpenHashMap, Category.SAVANNA, 163);
        Layers.register(int2IntOpenHashMap, Category.SAVANNA, 164);
        Layers.register(int2IntOpenHashMap, Category.SWAMP, 6);
        Layers.register(int2IntOpenHashMap, Category.SWAMP, 134);
        Layers.register(int2IntOpenHashMap, Category.TAIGA, 160);
        Layers.register(int2IntOpenHashMap, Category.TAIGA, 161);
        Layers.register(int2IntOpenHashMap, Category.TAIGA, 32);
        Layers.register(int2IntOpenHashMap, Category.TAIGA, 33);
        Layers.register(int2IntOpenHashMap, Category.TAIGA, 30);
        Layers.register(int2IntOpenHashMap, Category.TAIGA, 31);
        Layers.register(int2IntOpenHashMap, Category.TAIGA, 158);
        Layers.register(int2IntOpenHashMap, Category.TAIGA, 5);
        Layers.register(int2IntOpenHashMap, Category.TAIGA, 19);
        Layers.register(int2IntOpenHashMap, Category.TAIGA, 133);
    });

    private static <T extends Area, C extends BigContext<T>> AreaFactory<T> zoom(long l, AreaTransformer1 areaTransformer1, AreaFactory<T> areaFactory, int n, LongFunction<C> longFunction) {
        AreaFactory<T> areaFactory2 = areaFactory;
        for (int i = 0; i < n; ++i) {
            areaFactory2 = areaTransformer1.run((BigContext)longFunction.apply(l + (long)i), areaFactory2);
        }
        return areaFactory2;
    }

    private static <T extends Area, C extends BigContext<T>> AreaFactory<T> getDefaultLayer(boolean bl, int n, int n2, LongFunction<C> longFunction) {
        AreaFactory areaFactory = IslandLayer.INSTANCE.run((BigContext)longFunction.apply(1L));
        areaFactory = ZoomLayer.FUZZY.run((BigContext)longFunction.apply(2000L), areaFactory);
        areaFactory = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(1L), areaFactory);
        areaFactory = ZoomLayer.NORMAL.run((BigContext)longFunction.apply(2001L), areaFactory);
        areaFactory = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(2L), areaFactory);
        areaFactory = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(50L), areaFactory);
        areaFactory = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(70L), areaFactory);
        areaFactory = RemoveTooMuchOceanLayer.INSTANCE.run((BigContext)longFunction.apply(2L), areaFactory);
        AreaFactory areaFactory2 = OceanLayer.INSTANCE.run((BigContext)longFunction.apply(2L));
        areaFactory2 = Layers.zoom(2001L, ZoomLayer.NORMAL, areaFactory2, 6, longFunction);
        areaFactory = AddSnowLayer.INSTANCE.run((BigContext)longFunction.apply(2L), areaFactory);
        areaFactory = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(3L), areaFactory);
        areaFactory = AddEdgeLayer.CoolWarm.INSTANCE.run((BigContext)longFunction.apply(2L), areaFactory);
        areaFactory = AddEdgeLayer.HeatIce.INSTANCE.run((BigContext)longFunction.apply(2L), areaFactory);
        areaFactory = AddEdgeLayer.IntroduceSpecial.INSTANCE.run((BigContext)longFunction.apply(3L), areaFactory);
        areaFactory = ZoomLayer.NORMAL.run((BigContext)longFunction.apply(2002L), areaFactory);
        areaFactory = ZoomLayer.NORMAL.run((BigContext)longFunction.apply(2003L), areaFactory);
        areaFactory = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(4L), areaFactory);
        areaFactory = AddMushroomIslandLayer.INSTANCE.run((BigContext)longFunction.apply(5L), areaFactory);
        areaFactory = AddDeepOceanLayer.INSTANCE.run((BigContext)longFunction.apply(4L), areaFactory);
        AreaFactory areaFactory3 = areaFactory = Layers.zoom(1000L, ZoomLayer.NORMAL, areaFactory, 0, longFunction);
        areaFactory3 = Layers.zoom(1000L, ZoomLayer.NORMAL, areaFactory3, 0, longFunction);
        areaFactory3 = RiverInitLayer.INSTANCE.run((BigContext)longFunction.apply(100L), areaFactory3);
        AreaFactory areaFactory4 = areaFactory;
        areaFactory4 = new BiomeInitLayer(bl).run((BigContext)longFunction.apply(200L), areaFactory4);
        areaFactory4 = RareBiomeLargeLayer.INSTANCE.run((BigContext)longFunction.apply(1001L), areaFactory4);
        areaFactory4 = Layers.zoom(1000L, ZoomLayer.NORMAL, areaFactory4, 2, longFunction);
        areaFactory4 = BiomeEdgeLayer.INSTANCE.run((BigContext)longFunction.apply(1000L), areaFactory4);
        AreaFactory areaFactory5 = areaFactory3;
        areaFactory5 = Layers.zoom(1000L, ZoomLayer.NORMAL, areaFactory5, 2, longFunction);
        areaFactory4 = RegionHillsLayer.INSTANCE.run((BigContext)longFunction.apply(1000L), areaFactory4, areaFactory5);
        areaFactory3 = Layers.zoom(1000L, ZoomLayer.NORMAL, areaFactory3, 2, longFunction);
        areaFactory3 = Layers.zoom(1000L, ZoomLayer.NORMAL, areaFactory3, n2, longFunction);
        areaFactory3 = RiverLayer.INSTANCE.run((BigContext)longFunction.apply(1L), areaFactory3);
        areaFactory3 = SmoothLayer.INSTANCE.run((BigContext)longFunction.apply(1000L), areaFactory3);
        areaFactory4 = RareBiomeSpotLayer.INSTANCE.run((BigContext)longFunction.apply(1001L), areaFactory4);
        for (int i = 0; i < n; ++i) {
            areaFactory4 = ZoomLayer.NORMAL.run((BigContext)longFunction.apply(1000 + i), areaFactory4);
            if (i == 0) {
                areaFactory4 = AddIslandLayer.INSTANCE.run((BigContext)longFunction.apply(3L), areaFactory4);
            }
            if (i != 1 && n != 1) continue;
            areaFactory4 = ShoreLayer.INSTANCE.run((BigContext)longFunction.apply(1000L), areaFactory4);
        }
        areaFactory4 = SmoothLayer.INSTANCE.run((BigContext)longFunction.apply(1000L), areaFactory4);
        areaFactory4 = RiverMixerLayer.INSTANCE.run((BigContext)longFunction.apply(100L), areaFactory4, areaFactory3);
        areaFactory4 = OceanMixerLayer.INSTANCE.run((BigContext)longFunction.apply(100L), areaFactory4, areaFactory2);
        return areaFactory4;
    }

    public static Layer getDefaultLayer(long l, boolean bl, int n, int n2) {
        int n3 = 25;
        AreaFactory<LazyArea> areaFactory = Layers.getDefaultLayer(bl, n, n2, l2 -> new LazyAreaContext(25, l, l2));
        return new Layer(areaFactory);
    }

    public static boolean isSame(int n, int n2) {
        if (n == n2) {
            return true;
        }
        return CATEGORIES.get(n) == CATEGORIES.get(n2);
    }

    private static void register(Int2IntOpenHashMap int2IntOpenHashMap, Category category, int n) {
        int2IntOpenHashMap.put(n, category.ordinal());
    }

    protected static boolean isOcean(int n) {
        return n == 44 || n == 45 || n == 0 || n == 46 || n == 10 || n == 47 || n == 48 || n == 24 || n == 49 || n == 50;
    }

    protected static boolean isShallowOcean(int n) {
        return n == 44 || n == 45 || n == 0 || n == 46 || n == 10;
    }

    static enum Category {
        NONE,
        TAIGA,
        EXTREME_HILLS,
        JUNGLE,
        MESA,
        BADLANDS_PLATEAU,
        PLAINS,
        SAVANNA,
        ICY,
        BEACH,
        FOREST,
        OCEAN,
        DESERT,
        RIVER,
        SWAMP,
        MUSHROOM;
        
    }

}

