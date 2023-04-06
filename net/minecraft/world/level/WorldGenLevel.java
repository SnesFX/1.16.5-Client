/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level;

import java.util.stream.Stream;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public interface WorldGenLevel
extends ServerLevelAccessor {
    public long getSeed();

    public Stream<? extends StructureStart<?>> startsForFeature(SectionPos var1, StructureFeature<?> var2);
}

