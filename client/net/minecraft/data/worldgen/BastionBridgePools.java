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

public class BastionBridgePools {
    public static void bootstrap() {
    }

    static {
        Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/bridge/starting_pieces"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.single("bastion/bridge/starting_pieces/entrance", ProcessorLists.ENTRANCE_REPLACEMENT), (Object)1), (Object)Pair.of(StructurePoolElement.single("bastion/bridge/starting_pieces/entrance_face", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1)), StructureTemplatePool.Projection.RIGID));
        Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/bridge/bridge_pieces"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.single("bastion/bridge/bridge_pieces/bridge", ProcessorLists.BRIDGE), (Object)1)), StructureTemplatePool.Projection.RIGID));
        Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/bridge/legs"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.single("bastion/bridge/legs/leg_0", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.single("bastion/bridge/legs/leg_1", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1)), StructureTemplatePool.Projection.RIGID));
        Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/bridge/walls"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.single("bastion/bridge/walls/wall_base_0", ProcessorLists.RAMPART_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.single("bastion/bridge/walls/wall_base_1", ProcessorLists.RAMPART_DEGRADATION), (Object)1)), StructureTemplatePool.Projection.RIGID));
        Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/bridge/ramparts"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.single("bastion/bridge/ramparts/rampart_0", ProcessorLists.RAMPART_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.single("bastion/bridge/ramparts/rampart_1", ProcessorLists.RAMPART_DEGRADATION), (Object)1)), StructureTemplatePool.Projection.RIGID));
        Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/bridge/rampart_plates"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.single("bastion/bridge/rampart_plates/plate_0", ProcessorLists.RAMPART_DEGRADATION), (Object)1)), StructureTemplatePool.Projection.RIGID));
        Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/bridge/connectors"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.single("bastion/bridge/connectors/back_bridge_top", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1), (Object)Pair.of(StructurePoolElement.single("bastion/bridge/connectors/back_bridge_bottom", ProcessorLists.BASTION_GENERIC_DEGRADATION), (Object)1)), StructureTemplatePool.Projection.RIGID));
    }
}

