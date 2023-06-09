/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

public class EmptyLevelChunk
extends LevelChunk {
    private static final Biome[] BIOMES = Util.make(new Biome[ChunkBiomeContainer.BIOMES_SIZE], arrbiome -> Arrays.fill(arrbiome, Biomes.PLAINS));

    public EmptyLevelChunk(Level level, ChunkPos chunkPos) {
        super(level, chunkPos, new ChunkBiomeContainer(level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), BIOMES));
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos) {
        return Blocks.VOID_AIR.defaultBlockState();
    }

    @Nullable
    @Override
    public BlockState setBlockState(BlockPos blockPos, BlockState blockState, boolean bl) {
        return null;
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Nullable
    @Override
    public LevelLightEngine getLightEngine() {
        return null;
    }

    @Override
    public int getLightEmission(BlockPos blockPos) {
        return 0;
    }

    @Override
    public void addEntity(Entity entity) {
    }

    @Override
    public void removeEntity(Entity entity) {
    }

    @Override
    public void removeEntity(Entity entity, int n) {
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos blockPos, LevelChunk.EntityCreationType entityCreationType) {
        return null;
    }

    @Override
    public void addBlockEntity(BlockEntity blockEntity) {
    }

    @Override
    public void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity) {
    }

    @Override
    public void removeBlockEntity(BlockPos blockPos) {
    }

    @Override
    public void markUnsaved() {
    }

    @Override
    public void getEntities(@Nullable Entity entity, AABB aABB, List<Entity> list, Predicate<? super Entity> predicate) {
    }

    @Override
    public <T extends Entity> void getEntitiesOfClass(Class<? extends T> class_, AABB aABB, List<T> list, Predicate<? super T> predicate) {
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isYSpaceEmpty(int n, int n2) {
        return true;
    }

    @Override
    public ChunkHolder.FullChunkStatus getFullStatus() {
        return ChunkHolder.FullChunkStatus.BORDER;
    }
}

