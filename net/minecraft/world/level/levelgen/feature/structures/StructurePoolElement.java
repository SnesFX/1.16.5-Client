/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.structures.EmptyPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.FeaturePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.ListPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.SinglePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public abstract class StructurePoolElement {
    public static final Codec<StructurePoolElement> CODEC = Registry.STRUCTURE_POOL_ELEMENT.dispatch("element_type", StructurePoolElement::getType, StructurePoolElementType::codec);
    @Nullable
    private volatile StructureTemplatePool.Projection projection;

    protected static <E extends StructurePoolElement> RecordCodecBuilder<E, StructureTemplatePool.Projection> projectionCodec() {
        return StructureTemplatePool.Projection.CODEC.fieldOf("projection").forGetter(StructurePoolElement::getProjection);
    }

    protected StructurePoolElement(StructureTemplatePool.Projection projection) {
        this.projection = projection;
    }

    public abstract List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureManager var1, BlockPos var2, Rotation var3, Random var4);

    public abstract BoundingBox getBoundingBox(StructureManager var1, BlockPos var2, Rotation var3);

    public abstract boolean place(StructureManager var1, WorldGenLevel var2, StructureFeatureManager var3, ChunkGenerator var4, BlockPos var5, BlockPos var6, Rotation var7, BoundingBox var8, Random var9, boolean var10);

    public abstract StructurePoolElementType<?> getType();

    public void handleDataMarker(LevelAccessor levelAccessor, StructureTemplate.StructureBlockInfo structureBlockInfo, BlockPos blockPos, Rotation rotation, Random random, BoundingBox boundingBox) {
    }

    public StructurePoolElement setProjection(StructureTemplatePool.Projection projection) {
        this.projection = projection;
        return this;
    }

    public StructureTemplatePool.Projection getProjection() {
        StructureTemplatePool.Projection projection = this.projection;
        if (projection == null) {
            throw new IllegalStateException();
        }
        return projection;
    }

    public int getGroundLevelDelta() {
        return 1;
    }

    public static Function<StructureTemplatePool.Projection, EmptyPoolElement> empty() {
        return projection -> EmptyPoolElement.INSTANCE;
    }

    public static Function<StructureTemplatePool.Projection, LegacySinglePoolElement> legacy(String string) {
        return projection -> new LegacySinglePoolElement((Either<ResourceLocation, StructureTemplate>)Either.left((Object)new ResourceLocation(string)), () -> ProcessorLists.EMPTY, (StructureTemplatePool.Projection)projection);
    }

    public static Function<StructureTemplatePool.Projection, LegacySinglePoolElement> legacy(String string, StructureProcessorList structureProcessorList) {
        return projection -> new LegacySinglePoolElement((Either<ResourceLocation, StructureTemplate>)Either.left((Object)new ResourceLocation(string)), () -> structureProcessorList, (StructureTemplatePool.Projection)projection);
    }

    public static Function<StructureTemplatePool.Projection, SinglePoolElement> single(String string) {
        return projection -> new SinglePoolElement((Either<ResourceLocation, StructureTemplate>)Either.left((Object)new ResourceLocation(string)), () -> ProcessorLists.EMPTY, (StructureTemplatePool.Projection)projection);
    }

    public static Function<StructureTemplatePool.Projection, SinglePoolElement> single(String string, StructureProcessorList structureProcessorList) {
        return projection -> new SinglePoolElement((Either<ResourceLocation, StructureTemplate>)Either.left((Object)new ResourceLocation(string)), () -> structureProcessorList, (StructureTemplatePool.Projection)projection);
    }

    public static Function<StructureTemplatePool.Projection, FeaturePoolElement> feature(ConfiguredFeature<?, ?> configuredFeature) {
        return projection -> new FeaturePoolElement(() -> configuredFeature, (StructureTemplatePool.Projection)projection);
    }

    public static Function<StructureTemplatePool.Projection, ListPoolElement> list(List<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>> list) {
        return projection -> new ListPoolElement(list.stream().map(function -> (StructurePoolElement)function.apply(projection)).collect(Collectors.toList()), (StructureTemplatePool.Projection)projection);
    }
}

