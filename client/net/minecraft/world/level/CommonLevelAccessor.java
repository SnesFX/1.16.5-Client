/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CommonLevelAccessor
extends EntityGetter,
LevelReader,
LevelSimulatedRW {
    @Override
    default public Stream<VoxelShape> getEntityCollisions(@Nullable Entity entity, AABB aABB, Predicate<Entity> predicate) {
        return EntityGetter.super.getEntityCollisions(entity, aABB, predicate);
    }

    @Override
    default public boolean isUnobstructed(@Nullable Entity entity, VoxelShape voxelShape) {
        return EntityGetter.super.isUnobstructed(entity, voxelShape);
    }

    @Override
    default public BlockPos getHeightmapPos(Heightmap.Types types, BlockPos blockPos) {
        return LevelReader.super.getHeightmapPos(types, blockPos);
    }

    public RegistryAccess registryAccess();

    default public Optional<ResourceKey<Biome>> getBiomeName(BlockPos blockPos) {
        return this.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getResourceKey(this.getBiome(blockPos));
    }
}

