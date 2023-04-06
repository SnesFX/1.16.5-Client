/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public interface FeatureAccess {
    @Nullable
    public StructureStart<?> getStartForFeature(StructureFeature<?> var1);

    public void setStartForFeature(StructureFeature<?> var1, StructureStart<?> var2);

    public LongSet getReferencesForFeature(StructureFeature<?> var1);

    public void addReferenceForFeature(StructureFeature<?> var1, long var2);

    public Map<StructureFeature<?>, LongSet> getAllReferences();

    public void setAllReferences(Map<StructureFeature<?>, LongSet> var1);
}

