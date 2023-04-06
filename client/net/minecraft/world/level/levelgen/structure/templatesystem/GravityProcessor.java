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
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class GravityProcessor
extends StructureProcessor {
    public static final Codec<GravityProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Heightmap.Types.CODEC.fieldOf("heightmap").orElse((Object)Heightmap.Types.WORLD_SURFACE_WG).forGetter(gravityProcessor -> gravityProcessor.heightmap), (App)Codec.INT.fieldOf("offset").orElse((Object)0).forGetter(gravityProcessor -> gravityProcessor.offset)).apply((Applicative)instance, (arg_0, arg_1) -> GravityProcessor.new(arg_0, arg_1)));
    private final Heightmap.Types heightmap;
    private final int offset;

    public GravityProcessor(Heightmap.Types types, int n) {
        this.heightmap = types;
        this.offset = n;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, BlockPos blockPos2, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo structureBlockInfo2, StructurePlaceSettings structurePlaceSettings) {
        Heightmap.Types types = levelReader instanceof ServerLevel ? (this.heightmap == Heightmap.Types.WORLD_SURFACE_WG ? Heightmap.Types.WORLD_SURFACE : (this.heightmap == Heightmap.Types.OCEAN_FLOOR_WG ? Heightmap.Types.OCEAN_FLOOR : this.heightmap)) : this.heightmap;
        int n = levelReader.getHeight(types, structureBlockInfo2.pos.getX(), structureBlockInfo2.pos.getZ()) + this.offset;
        int n2 = structureBlockInfo.pos.getY();
        return new StructureTemplate.StructureBlockInfo(new BlockPos(structureBlockInfo2.pos.getX(), n + n2, structureBlockInfo2.pos.getZ()), structureBlockInfo2.state, structureBlockInfo2.nbt);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.GRAVITY;
    }
}

