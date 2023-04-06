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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;

public class BastionSharedPools {
    public static void bootstrap() {
    }

    static {
        Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/mobs/piglin"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.single("bastion/mobs/melee_piglin"), (Object)1), (Object)Pair.of(StructurePoolElement.single("bastion/mobs/sword_piglin"), (Object)4), (Object)Pair.of(StructurePoolElement.single("bastion/mobs/crossbow_piglin"), (Object)4), (Object)Pair.of(StructurePoolElement.single("bastion/mobs/empty"), (Object)1)), StructureTemplatePool.Projection.RIGID));
        Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/mobs/hoglin"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.single("bastion/mobs/hoglin"), (Object)2), (Object)Pair.of(StructurePoolElement.single("bastion/mobs/empty"), (Object)1)), StructureTemplatePool.Projection.RIGID));
        Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/blocks/gold"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.single("bastion/blocks/air"), (Object)3), (Object)Pair.of(StructurePoolElement.single("bastion/blocks/gold"), (Object)1)), StructureTemplatePool.Projection.RIGID));
        Pools.register(new StructureTemplatePool(new ResourceLocation("bastion/mobs/piglin_melee"), new ResourceLocation("empty"), (List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>)ImmutableList.of((Object)Pair.of(StructurePoolElement.single("bastion/mobs/melee_piglin_always"), (Object)1), (Object)Pair.of(StructurePoolElement.single("bastion/mobs/melee_piglin"), (Object)5), (Object)Pair.of(StructurePoolElement.single("bastion/mobs/sword_piglin"), (Object)1)), StructureTemplatePool.Projection.RIGID));
    }
}

