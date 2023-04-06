/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class ListPoolElement
extends StructurePoolElement {
    public static final Codec<ListPoolElement> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)StructurePoolElement.CODEC.listOf().fieldOf("elements").forGetter(listPoolElement -> listPoolElement.elements), ListPoolElement.projectionCodec()).apply((Applicative)instance, (arg_0, arg_1) -> ListPoolElement.new(arg_0, arg_1)));
    private final List<StructurePoolElement> elements;

    public ListPoolElement(List<StructurePoolElement> list, StructureTemplatePool.Projection projection) {
        super(projection);
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Elements are empty");
        }
        this.elements = list;
        this.setProjectionOnEachElement(projection);
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureManager structureManager, BlockPos blockPos, Rotation rotation, Random random) {
        return this.elements.get(0).getShuffledJigsawBlocks(structureManager, blockPos, rotation, random);
    }

    @Override
    public BoundingBox getBoundingBox(StructureManager structureManager, BlockPos blockPos, Rotation rotation) {
        BoundingBox boundingBox = BoundingBox.getUnknownBox();
        for (StructurePoolElement structurePoolElement : this.elements) {
            BoundingBox boundingBox2 = structurePoolElement.getBoundingBox(structureManager, blockPos, rotation);
            boundingBox.expand(boundingBox2);
        }
        return boundingBox;
    }

    @Override
    public boolean place(StructureManager structureManager, WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, BlockPos blockPos, BlockPos blockPos2, Rotation rotation, BoundingBox boundingBox, Random random, boolean bl) {
        for (StructurePoolElement structurePoolElement : this.elements) {
            if (structurePoolElement.place(structureManager, worldGenLevel, structureFeatureManager, chunkGenerator, blockPos, blockPos2, rotation, boundingBox, random, bl)) continue;
            return false;
        }
        return true;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.LIST;
    }

    @Override
    public StructurePoolElement setProjection(StructureTemplatePool.Projection projection) {
        super.setProjection(projection);
        this.setProjectionOnEachElement(projection);
        return this;
    }

    public String toString() {
        return "List[" + this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }

    private void setProjectionOnEachElement(StructureTemplatePool.Projection projection) {
        this.elements.forEach(structurePoolElement -> structurePoolElement.setProjection(projection));
    }
}

