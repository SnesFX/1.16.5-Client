/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.newbiome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset1Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum RegionHillsLayer implements AreaTransformer2,
DimensionOffset1Transformer
{
    INSTANCE;
    
    private static final Logger LOGGER;
    private static final Int2IntMap MUTATIONS;

    @Override
    public int applyPixel(Context context, Area area, Area area2, int n, int n2) {
        int n3 = area.get(this.getParentX(n + 1), this.getParentY(n2 + 1));
        int n4 = area2.get(this.getParentX(n + 1), this.getParentY(n2 + 1));
        if (n3 > 255) {
            LOGGER.debug("old! {}", (Object)n3);
        }
        int n5 = (n4 - 2) % 29;
        if (!Layers.isShallowOcean(n3) && n4 >= 2 && n5 == 1) {
            return MUTATIONS.getOrDefault(n3, n3);
        }
        if (context.nextRandom(3) == 0 || n5 == 0) {
            int n6 = n3;
            if (n3 == 2) {
                n6 = 17;
            } else if (n3 == 4) {
                n6 = 18;
            } else if (n3 == 27) {
                n6 = 28;
            } else if (n3 == 29) {
                n6 = 1;
            } else if (n3 == 5) {
                n6 = 19;
            } else if (n3 == 32) {
                n6 = 33;
            } else if (n3 == 30) {
                n6 = 31;
            } else if (n3 == 1) {
                n6 = context.nextRandom(3) == 0 ? 18 : 4;
            } else if (n3 == 12) {
                n6 = 13;
            } else if (n3 == 21) {
                n6 = 22;
            } else if (n3 == 168) {
                n6 = 169;
            } else if (n3 == 0) {
                n6 = 24;
            } else if (n3 == 45) {
                n6 = 48;
            } else if (n3 == 46) {
                n6 = 49;
            } else if (n3 == 10) {
                n6 = 50;
            } else if (n3 == 3) {
                n6 = 34;
            } else if (n3 == 35) {
                n6 = 36;
            } else if (Layers.isSame(n3, 38)) {
                n6 = 37;
            } else if ((n3 == 24 || n3 == 48 || n3 == 49 || n3 == 50) && context.nextRandom(3) == 0) {
                int n7 = n6 = context.nextRandom(2) == 0 ? 1 : 4;
            }
            if (n5 == 0 && n6 != n3) {
                n6 = MUTATIONS.getOrDefault(n6, n3);
            }
            if (n6 != n3) {
                int n8 = 0;
                if (Layers.isSame(area.get(this.getParentX(n + 1), this.getParentY(n2 + 0)), n3)) {
                    ++n8;
                }
                if (Layers.isSame(area.get(this.getParentX(n + 2), this.getParentY(n2 + 1)), n3)) {
                    ++n8;
                }
                if (Layers.isSame(area.get(this.getParentX(n + 0), this.getParentY(n2 + 1)), n3)) {
                    ++n8;
                }
                if (Layers.isSame(area.get(this.getParentX(n + 1), this.getParentY(n2 + 2)), n3)) {
                    ++n8;
                }
                if (n8 >= 3) {
                    return n6;
                }
            }
        }
        return n3;
    }

    static {
        LOGGER = LogManager.getLogger();
        MUTATIONS = (Int2IntMap)Util.make(new Int2IntOpenHashMap(), int2IntOpenHashMap -> {
            int2IntOpenHashMap.put(1, 129);
            int2IntOpenHashMap.put(2, 130);
            int2IntOpenHashMap.put(3, 131);
            int2IntOpenHashMap.put(4, 132);
            int2IntOpenHashMap.put(5, 133);
            int2IntOpenHashMap.put(6, 134);
            int2IntOpenHashMap.put(12, 140);
            int2IntOpenHashMap.put(21, 149);
            int2IntOpenHashMap.put(23, 151);
            int2IntOpenHashMap.put(27, 155);
            int2IntOpenHashMap.put(28, 156);
            int2IntOpenHashMap.put(29, 157);
            int2IntOpenHashMap.put(30, 158);
            int2IntOpenHashMap.put(32, 160);
            int2IntOpenHashMap.put(33, 161);
            int2IntOpenHashMap.put(34, 162);
            int2IntOpenHashMap.put(35, 163);
            int2IntOpenHashMap.put(36, 164);
            int2IntOpenHashMap.put(37, 165);
            int2IntOpenHashMap.put(38, 166);
            int2IntOpenHashMap.put(39, 167);
        });
    }
}

