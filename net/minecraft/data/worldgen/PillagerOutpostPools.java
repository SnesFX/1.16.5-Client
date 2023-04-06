/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.data.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Function;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class PillagerOutpostPools {
    public static final StructureTemplatePool START = Pools.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/base_plates"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.legacy("pillager_outpost/base_plate"), (Object)1)), StructureTemplatePool.Projection.RIGID));

    public static void bootstrap() {
    }

    static {
        Pools.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/towers"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.list((List<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>>)ImmutableList.of(StructurePoolElement.legacy("pillager_outpost/watchtower"), StructurePoolElement.legacy("pillager_outpost/watchtower_overgrown", ProcessorLists.OUTPOST_ROT))), (Object)1)), StructureTemplatePool.Projection.RIGID));
        Pools.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/feature_plates"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_plate"), (Object)1)), StructureTemplatePool.Projection.TERRAIN_MATCHING));
        Pools.register(new StructureTemplatePool(new ResourceLocation("pillager_outpost/features"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_cage1"), (Object)1), (Object)Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_cage2"), (Object)1), (Object)Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_logs"), (Object)1), (Object)Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_tent1"), (Object)1), (Object)Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_tent2"), (Object)1), (Object)Pair.of(StructurePoolElement.legacy("pillager_outpost/feature_targets"), (Object)1), (Object)Pair.of(StructurePoolElement.empty(), (Object)6)), StructureTemplatePool.Projection.RIGID));
    }
}

