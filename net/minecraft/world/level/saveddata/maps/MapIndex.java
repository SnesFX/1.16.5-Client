/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 */
package net.minecraft.world.level.saveddata.maps;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class MapIndex
extends SavedData {
    private final Object2IntMap<String> usedAuxIds = new Object2IntOpenHashMap();

    public MapIndex() {
        super("idcounts");
        this.usedAuxIds.defaultReturnValue(-1);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        this.usedAuxIds.clear();
        for (String string : compoundTag.getAllKeys()) {
            if (!compoundTag.contains(string, 99)) continue;
            this.usedAuxIds.put((Object)string, compoundTag.getInt(string));
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        for (Object2IntMap.Entry entry : this.usedAuxIds.object2IntEntrySet()) {
            compoundTag.putInt((String)entry.getKey(), entry.getIntValue());
        }
        return compoundTag;
    }

    public int getFreeAuxValueForMap() {
        int n = this.usedAuxIds.getInt((Object)"map") + 1;
        this.usedAuxIds.put((Object)"map", n);
        this.setDirty();
        return n;
    }
}

