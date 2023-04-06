/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.dimension;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public final class LevelStem {
    public static final Codec<LevelStem> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)DimensionType.CODEC.fieldOf("type").forGetter(LevelStem::typeSupplier), (App)ChunkGenerator.CODEC.fieldOf("generator").forGetter(LevelStem::generator)).apply((Applicative)instance, instance.stable((arg_0, arg_1) -> LevelStem.new(arg_0, arg_1))));
    public static final ResourceKey<LevelStem> OVERWORLD = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation("overworld"));
    public static final ResourceKey<LevelStem> NETHER = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation("the_nether"));
    public static final ResourceKey<LevelStem> END = ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, new ResourceLocation("the_end"));
    private static final LinkedHashSet<ResourceKey<LevelStem>> BUILTIN_ORDER = Sets.newLinkedHashSet((Iterable)ImmutableList.of(OVERWORLD, NETHER, END));
    private final Supplier<DimensionType> type;
    private final ChunkGenerator generator;

    public LevelStem(Supplier<DimensionType> supplier, ChunkGenerator chunkGenerator) {
        this.type = supplier;
        this.generator = chunkGenerator;
    }

    public Supplier<DimensionType> typeSupplier() {
        return this.type;
    }

    public DimensionType type() {
        return this.type.get();
    }

    public ChunkGenerator generator() {
        return this.generator;
    }

    public static MappedRegistry<LevelStem> sortMap(MappedRegistry<LevelStem> mappedRegistry) {
        Object object;
        MappedRegistry<LevelStem> mappedRegistry2 = new MappedRegistry<LevelStem>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
        for (ResourceKey object2 : BUILTIN_ORDER) {
            object = mappedRegistry.get(object2);
            if (object == null) continue;
            mappedRegistry2.register(object2, object, mappedRegistry.lifecycle((LevelStem)object));
        }
        for (Map.Entry entry : mappedRegistry.entrySet()) {
            object = (ResourceKey)entry.getKey();
            if (BUILTIN_ORDER.contains(object)) continue;
            mappedRegistry2.register((ResourceKey<LevelStem>)object, entry.getValue(), mappedRegistry.lifecycle((LevelStem)entry.getValue()));
        }
        return mappedRegistry2;
    }

    public static boolean stable(long l, MappedRegistry<LevelStem> mappedRegistry) {
        ArrayList arrayList = Lists.newArrayList(mappedRegistry.entrySet());
        if (arrayList.size() != BUILTIN_ORDER.size()) {
            return false;
        }
        Map.Entry entry = (Map.Entry)arrayList.get(0);
        Map.Entry entry2 = (Map.Entry)arrayList.get(1);
        Map.Entry entry3 = (Map.Entry)arrayList.get(2);
        if (entry.getKey() != OVERWORLD || entry2.getKey() != NETHER || entry3.getKey() != END) {
            return false;
        }
        if (!((LevelStem)entry.getValue()).type().equalTo(DimensionType.DEFAULT_OVERWORLD) && ((LevelStem)entry.getValue()).type() != DimensionType.DEFAULT_OVERWORLD_CAVES) {
            return false;
        }
        if (!((LevelStem)entry2.getValue()).type().equalTo(DimensionType.DEFAULT_NETHER)) {
            return false;
        }
        if (!((LevelStem)entry3.getValue()).type().equalTo(DimensionType.DEFAULT_END)) {
            return false;
        }
        if (!(((LevelStem)entry2.getValue()).generator() instanceof NoiseBasedChunkGenerator) || !(((LevelStem)entry3.getValue()).generator() instanceof NoiseBasedChunkGenerator)) {
            return false;
        }
        NoiseBasedChunkGenerator noiseBasedChunkGenerator = (NoiseBasedChunkGenerator)((LevelStem)entry2.getValue()).generator();
        NoiseBasedChunkGenerator noiseBasedChunkGenerator2 = (NoiseBasedChunkGenerator)((LevelStem)entry3.getValue()).generator();
        if (!noiseBasedChunkGenerator.stable(l, NoiseGeneratorSettings.NETHER)) {
            return false;
        }
        if (!noiseBasedChunkGenerator2.stable(l, NoiseGeneratorSettings.END)) {
            return false;
        }
        if (!(noiseBasedChunkGenerator.getBiomeSource() instanceof MultiNoiseBiomeSource)) {
            return false;
        }
        MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource)noiseBasedChunkGenerator.getBiomeSource();
        if (!multiNoiseBiomeSource.stable(l)) {
            return false;
        }
        if (!(noiseBasedChunkGenerator2.getBiomeSource() instanceof TheEndBiomeSource)) {
            return false;
        }
        TheEndBiomeSource theEndBiomeSource = (TheEndBiomeSource)noiseBasedChunkGenerator2.getBiomeSource();
        return theEndBiomeSource.stable(l);
    }
}

