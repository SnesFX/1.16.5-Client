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
import net.minecraft.data.worldgen.BastionBridgePools;
import net.minecraft.data.worldgen.BastionHoglinStablePools;
import net.minecraft.data.worldgen.BastionHousingUnitsPools;
import net.minecraft.data.worldgen.BastionSharedPools;
import net.minecraft.data.worldgen.BastionTreasureRoomPools;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class BastionPieces {
    public static final StructureTemplatePool START = Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/starts"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.single("bastion/units/air_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.single("bastion/hoglin_stable/air_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.single("bastion/treasure/big_air_full", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.single("bastion/bridge/starting_pieces/entrance_base", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1)), StructureTemplatePool.Projection.RIGID));

    public static void bootstrap() {
        BastionHousingUnitsPools.bootstrap();
        BastionHoglinStablePools.bootstrap();
        BastionTreasureRoomPools.bootstrap();
        BastionBridgePools.bootstrap();
        BastionSharedPools.bootstrap();
    }
}

