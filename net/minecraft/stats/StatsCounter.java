/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.stats;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.world.entity.player.Player;

public class StatsCounter {
    protected final Object2IntMap<Stat<?>> stats = Object2IntMaps.synchronize((Object2IntMap)new Object2IntOpenHashMap());

    public StatsCounter() {
        this.stats.defaultReturnValue(0);
    }

    public void increment(Player player, Stat<?> stat, int n) {
        int n2 = (int)Math.min((long)this.getValue(stat) + (long)n, Integer.MAX_VALUE);
        this.setValue(player, stat, n2);
    }

    public void setValue(Player player, Stat<?> stat, int n) {
        this.stats.put(stat, n);
    }

    public <T> int getValue(StatType<T> statType, T t) {
        return statType.contains(t) ? this.getValue(statType.get(t)) : 0;
    }

    public int getValue(Stat<?> stat) {
        return this.stats.getInt(stat);
    }
}

