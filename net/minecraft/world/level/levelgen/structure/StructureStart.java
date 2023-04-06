/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public abstract class StructureStart<C extends FeatureConfiguration> {
    public static final StructureStart<?> INVALID_START = new StructureStart<MineshaftConfiguration>(StructureFeature.MINESHAFT, 0, 0, BoundingBox.getUnknownBox(), 0, 0L){

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, int n, int n2, Biome biome, MineshaftConfiguration mineshaftConfiguration) {
        }
    };
    private final StructureFeature<C> feature;
    protected final List<StructurePiece> pieces = Lists.newArrayList();
    protected BoundingBox boundingBox;
    private final int chunkX;
    private final int chunkZ;
    private int references;
    protected final WorldgenRandom random;

    public StructureStart(StructureFeature<C> structureFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
        this.feature = structureFeature;
        this.chunkX = n;
        this.chunkZ = n2;
        this.references = n3;
        this.random = new WorldgenRandom();
        this.random.setLargeFeatureSeed(l, n, n2);
        this.boundingBox = boundingBox;
    }

    public abstract void generatePieces(RegistryAccess var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, C var7);

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public List<StructurePiece> getPieces() {
        return this.pieces;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void placeInChunk(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
        List<StructurePiece> list = this.pieces;
        synchronized (list) {
            if (this.pieces.isEmpty()) {
                return;
            }
            BoundingBox boundingBox2 = this.pieces.get((int)0).boundingBox;
            Vec3i vec3i = boundingBox2.getCenter();
            BlockPos blockPos = new BlockPos(vec3i.getX(), boundingBox2.y0, vec3i.getZ());
            Iterator<StructurePiece> iterator = this.pieces.iterator();
            while (iterator.hasNext()) {
                StructurePiece structurePiece = iterator.next();
                if (!structurePiece.getBoundingBox().intersects(boundingBox) || structurePiece.postProcess(worldGenLevel, structureFeatureManager, chunkGenerator, random, boundingBox, chunkPos, blockPos)) continue;
                iterator.remove();
            }
            this.calculateBoundingBox();
        }
    }

    protected void calculateBoundingBox() {
        this.boundingBox = BoundingBox.getUnknownBox();
        for (StructurePiece structurePiece : this.pieces) {
            this.boundingBox.expand(structurePiece.getBoundingBox());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompoundTag createTag(int n, int n2) {
        CompoundTag compoundTag = new CompoundTag();
        if (!this.isValid()) {
            compoundTag.putString("id", "INVALID");
            return compoundTag;
        }
        compoundTag.putString("id", Registry.STRUCTURE_FEATURE.getKey(this.getFeature()).toString());
        compoundTag.putInt("ChunkX", n);
        compoundTag.putInt("ChunkZ", n2);
        compoundTag.putInt("references", this.references);
        compoundTag.put("BB", this.boundingBox.createTag());
        ListTag listTag = new ListTag();
        List<StructurePiece> list = this.pieces;
        synchronized (list) {
            for (StructurePiece structurePiece : this.pieces) {
                listTag.add(structurePiece.createTag());
            }
        }
        compoundTag.put("Children", listTag);
        return compoundTag;
    }

    protected void moveBelowSeaLevel(int n, Random random, int n2) {
        int n3 = n - n2;
        int n4 = this.boundingBox.getYSpan() + 1;
        if (n4 < n3) {
            n4 += random.nextInt(n3 - n4);
        }
        int n5 = n4 - this.boundingBox.y1;
        this.boundingBox.move(0, n5, 0);
        for (StructurePiece structurePiece : this.pieces) {
            structurePiece.move(0, n5, 0);
        }
    }

    protected void moveInsideHeights(Random random, int n, int n2) {
        int n3 = n2 - n + 1 - this.boundingBox.getYSpan();
        int n4 = n3 > 1 ? n + random.nextInt(n3) : n;
        int n5 = n4 - this.boundingBox.y0;
        this.boundingBox.move(0, n5, 0);
        for (StructurePiece structurePiece : this.pieces) {
            structurePiece.move(0, n5, 0);
        }
    }

    public boolean isValid() {
        return !this.pieces.isEmpty();
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkZ() {
        return this.chunkZ;
    }

    public BlockPos getLocatePos() {
        return new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
    }

    public boolean canBeReferenced() {
        return this.references < this.getMaxReferences();
    }

    public void addReference() {
        ++this.references;
    }

    public int getReferences() {
        return this.references;
    }

    protected int getMaxReferences() {
        return 1;
    }

    public StructureFeature<?> getFeature() {
        return this.feature;
    }

}

