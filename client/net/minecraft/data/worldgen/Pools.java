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
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.BastionPieces;
import net.minecraft.data.worldgen.PillagerOutpostPools;
import net.minecraft.data.worldgen.VillagePools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;

public class Pools {
    public static final ResourceKey<StructureTemplatePool> EMPTY = ResourceKey.create(Registry.TEMPLATE_POOL_REGISTRY, new ResourceLocation("empty"));
    private static final StructureTemplatePool BUILTIN_EMPTY = Pools.register(new StructureTemplatePool(EMPTY.location(), EMPTY.location(), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of(), StructureTemplatePool.Projection.RIGID));

    public static StructureTemplatePool register(StructureTemplatePool structureTemplatePool) {
        return BuiltinRegistries.register(BuiltinRegistries.TEMPLATE_POOL, structureTemplatePool.getName(), structureTemplatePool);
    }

    public static StructureTemplatePool bootstrap() {
        BastionPieces.bootstrap();
        PillagerOutpostPools.bootstrap();
        VillagePools.bootstrap();
        return BUILTIN_EMPTY;
    }

    static {
        Pools.bootstrap();
    }
}

