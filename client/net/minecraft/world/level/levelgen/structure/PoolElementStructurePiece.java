/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.structures.EmptyPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PoolElementStructurePiece
extends StructurePiece {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final StructurePoolElement element;
    protected BlockPos position;
    private final int groundLevelDelta;
    protected final Rotation rotation;
    private final List<JigsawJunction> junctions = Lists.newArrayList();
    private final StructureManager structureManager;

    public PoolElementStructurePiece(StructureManager structureManager, StructurePoolElement structurePoolElement, BlockPos blockPos, int n, Rotation rotation, BoundingBox boundingBox) {
        super(StructurePieceType.JIGSAW, 0);
        this.structureManager = structureManager;
        this.element = structurePoolElement;
        this.position = blockPos;
        this.groundLevelDelta = n;
        this.rotation = rotation;
        this.boundingBox = boundingBox;
    }

    public PoolElementStructurePiece(StructureManager structureManager, CompoundTag compoundTag) {
        super(StructurePieceType.JIGSAW, compoundTag);
        this.structureManager = structureManager;
        this.position = new BlockPos(compoundTag.getInt("PosX"), compoundTag.getInt("PosY"), compoundTag.getInt("PosZ"));
        this.groundLevelDelta = compoundTag.getInt("ground_level_delta");
        this.element = StructurePoolElement.CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)compoundTag.getCompound("pool_element")).resultOrPartial(((Logger)LOGGER)::error).orElse(EmptyPoolElement.INSTANCE);
        this.rotation = Rotation.valueOf(compoundTag.getString("rotation"));
        this.boundingBox = this.element.getBoundingBox(structureManager, this.position, this.rotation);
        ListTag listTag = compoundTag.getList("junctions", 10);
        this.junctions.clear();
        listTag.forEach(tag -> this.junctions.add(JigsawJunction.deserialize(new Dynamic((DynamicOps)NbtOps.INSTANCE, tag))));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("PosX", this.position.getX());
        compoundTag.putInt("PosY", this.position.getY());
        compoundTag.putInt("PosZ", this.position.getZ());
        compoundTag.putInt("ground_level_delta", this.groundLevelDelta);
        StructurePoolElement.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.element).resultOrPartial(((Logger)LOGGER)::error).ifPresent(tag -> compoundTag.put("pool_element", (Tag)tag));
        compoundTag.putString("rotation", this.rotation.name());
        ListTag listTag = new ListTag();
        for (JigsawJunction jigsawJunction : this.junctions) {
            listTag.add(jigsawJunction.serialize(NbtOps.INSTANCE).getValue());
        }
        compoundTag.put("junctions", listTag);
    }

    @Override
    public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        return this.place(worldGenLevel, structureFeatureManager, chunkGenerator, random, boundingBox, blockPos, false);
    }

    public boolean place(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, BlockPos blockPos, boolean bl) {
        return this.element.place(this.structureManager, worldGenLevel, structureFeatureManager, chunkGenerator, this.position, blockPos, this.rotation, boundingBox, random, bl);
    }

    @Override
    public void move(int n, int n2, int n3) {
        super.move(n, n2, n3);
        this.position = this.position.offset(n, n2, n3);
    }

    @Override
    public Rotation getRotation() {
        return this.rotation;
    }

    public String toString() {
        return String.format("<%s | %s | %s | %s>", new Object[]{this.getClass().getSimpleName(), this.position, this.rotation, this.element});
    }

    public StructurePoolElement getElement() {
        return this.element;
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public int getGroundLevelDelta() {
        return this.groundLevelDelta;
    }

    public void addJunction(JigsawJunction jigsawJunction) {
        this.junctions.add(jigsawJunction);
    }

    public List<JigsawJunction> getJunctions() {
        return this.junctions;
    }
}

