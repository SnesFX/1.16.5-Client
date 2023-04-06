/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  it.unimi.dsi.fastutil.objects.ObjectArrays
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.GravityProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureTemplatePool {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<StructureTemplatePool> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ResourceLocation.CODEC.fieldOf("name").forGetter(StructureTemplatePool::getName), (App)ResourceLocation.CODEC.fieldOf("fallback").forGetter(StructureTemplatePool::getFallback), (App)Codec.mapPair((MapCodec)StructurePoolElement.CODEC.fieldOf("element"), (MapCodec)Codec.INT.fieldOf("weight")).codec().listOf().promotePartial(Util.prefix("Pool element: ", ((Logger)LOGGER)::error)).fieldOf("elements").forGetter(structureTemplatePool -> structureTemplatePool.rawTemplates)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> StructureTemplatePool.new(arg_0, arg_1, arg_2)));
    public static final Codec<Supplier<StructureTemplatePool>> CODEC = RegistryFileCodec.create(Registry.TEMPLATE_POOL_REGISTRY, DIRECT_CODEC);
    private final ResourceLocation name;
    private final List<Pair<StructurePoolElement, Integer>> rawTemplates;
    private final List<StructurePoolElement> templates;
    private final ResourceLocation fallback;
    private int maxSize = Integer.MIN_VALUE;

    public StructureTemplatePool(ResourceLocation resourceLocation, ResourceLocation resourceLocation2, List<Pair<StructurePoolElement, Integer>> list) {
        this.name = resourceLocation;
        this.rawTemplates = list;
        this.templates = Lists.newArrayList();
        for (Pair<StructurePoolElement, Integer> pair : list) {
            StructurePoolElement structurePoolElement = (StructurePoolElement)pair.getFirst();
            for (int i = 0; i < (Integer)pair.getSecond(); ++i) {
                this.templates.add(structurePoolElement);
            }
        }
        this.fallback = resourceLocation2;
    }

    public StructureTemplatePool(ResourceLocation resourceLocation, ResourceLocation resourceLocation2, List<Pair<Function<Projection, ? extends StructurePoolElement>, Integer>> list, Projection projection) {
        this.name = resourceLocation;
        this.rawTemplates = Lists.newArrayList();
        this.templates = Lists.newArrayList();
        for (Pair<Function<Projection, ? extends StructurePoolElement>, Integer> pair : list) {
            StructurePoolElement structurePoolElement = (StructurePoolElement)((Function)pair.getFirst()).apply(projection);
            this.rawTemplates.add((Pair<StructurePoolElement, Integer>)Pair.of((Object)structurePoolElement, (Object)pair.getSecond()));
            for (int i = 0; i < (Integer)pair.getSecond(); ++i) {
                this.templates.add(structurePoolElement);
            }
        }
        this.fallback = resourceLocation2;
    }

    public int getMaxSize(StructureManager structureManager) {
        if (this.maxSize == Integer.MIN_VALUE) {
            this.maxSize = this.templates.stream().mapToInt(structurePoolElement -> structurePoolElement.getBoundingBox(structureManager, BlockPos.ZERO, Rotation.NONE).getYSpan()).max().orElse(0);
        }
        return this.maxSize;
    }

    public ResourceLocation getFallback() {
        return this.fallback;
    }

    public StructurePoolElement getRandomTemplate(Random random) {
        return this.templates.get(random.nextInt(this.templates.size()));
    }

    public List<StructurePoolElement> getShuffledTemplates(Random random) {
        return ImmutableList.copyOf((Object[])ObjectArrays.shuffle((Object[])this.templates.toArray(new StructurePoolElement[0]), (Random)random));
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public int size() {
        return this.templates.size();
    }

    public static enum Projection implements StringRepresentable
    {
        TERRAIN_MATCHING("terrain_matching", (ImmutableList<StructureProcessor>)ImmutableList.of((Object)new GravityProcessor(Heightmap.Types.WORLD_SURFACE_WG, -1))),
        RIGID("rigid", (ImmutableList<StructureProcessor>)ImmutableList.of());
        
        public static final Codec<Projection> CODEC;
        private static final Map<String, Projection> BY_NAME;
        private final String name;
        private final ImmutableList<StructureProcessor> processors;

        private Projection(String string2, ImmutableList<StructureProcessor> immutableList) {
            this.name = string2;
            this.processors = immutableList;
        }

        public String getName() {
            return this.name;
        }

        public static Projection byName(String string) {
            return BY_NAME.get(string);
        }

        public ImmutableList<StructureProcessor> getProcessors() {
            return this.processors;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum(Projection::values, Projection::byName);
            BY_NAME = Arrays.stream(Projection.values()).collect(Collectors.toMap(Projection::getName, projection -> projection));
        }
    }

}

