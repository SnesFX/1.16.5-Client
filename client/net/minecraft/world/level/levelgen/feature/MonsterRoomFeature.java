/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonsterRoomFeature
extends Feature<NoneFeatureConfiguration> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final EntityType<?>[] MOBS = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
    private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

    public MonsterRoomFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        Object object;
        int n;
        Object object2;
        int n2;
        Object object3;
        int n3;
        int n4 = 3;
        int n5 = random.nextInt(2) + 2;
        int n6 = -n5 - 1;
        int n7 = n5 + 1;
        int n8 = -1;
        int n9 = 4;
        int n10 = random.nextInt(2) + 2;
        int n11 = -n10 - 1;
        int n12 = n10 + 1;
        int n13 = 0;
        for (n = n6; n <= n7; ++n) {
            for (n2 = -1; n2 <= 4; ++n2) {
                for (n3 = n11; n3 <= n12; ++n3) {
                    object3 = blockPos.offset(n, n2, n3);
                    object = worldGenLevel.getBlockState((BlockPos)object3).getMaterial();
                    object2 = ((Material)object).isSolid();
                    if (n2 == -1 && object2 == false) {
                        return false;
                    }
                    if (n2 == 4 && object2 == false) {
                        return false;
                    }
                    if (n != n6 && n != n7 && n3 != n11 && n3 != n12 || n2 != 0 || !worldGenLevel.isEmptyBlock((BlockPos)object3) || !worldGenLevel.isEmptyBlock(((BlockPos)object3).above())) continue;
                    ++n13;
                }
            }
        }
        if (n13 < 1 || n13 > 5) {
            return false;
        }
        for (n = n6; n <= n7; ++n) {
            for (n2 = 3; n2 >= -1; --n2) {
                for (n3 = n11; n3 <= n12; ++n3) {
                    object3 = blockPos.offset(n, n2, n3);
                    object = worldGenLevel.getBlockState((BlockPos)object3);
                    if (n == n6 || n2 == -1 || n3 == n11 || n == n7 || n2 == 4 || n3 == n12) {
                        if (((Vec3i)object3).getY() >= 0 && !worldGenLevel.getBlockState(((BlockPos)object3).below()).getMaterial().isSolid()) {
                            worldGenLevel.setBlock((BlockPos)object3, AIR, 2);
                            continue;
                        }
                        if (!((BlockBehaviour.BlockStateBase)object).getMaterial().isSolid() || ((BlockBehaviour.BlockStateBase)object).is(Blocks.CHEST)) continue;
                        if (n2 == -1 && random.nextInt(4) != 0) {
                            worldGenLevel.setBlock((BlockPos)object3, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
                            continue;
                        }
                        worldGenLevel.setBlock((BlockPos)object3, Blocks.COBBLESTONE.defaultBlockState(), 2);
                        continue;
                    }
                    if (((BlockBehaviour.BlockStateBase)object).is(Blocks.CHEST) || ((BlockBehaviour.BlockStateBase)object).is(Blocks.SPAWNER)) continue;
                    worldGenLevel.setBlock((BlockPos)object3, AIR, 2);
                }
            }
        }
        block6 : for (n = 0; n < 2; ++n) {
            for (n2 = 0; n2 < 3; ++n2) {
                n3 = blockPos.getX() + random.nextInt(n5 * 2 + 1) - n5;
                object2 = new BlockPos(n3, (int)(object3 = (Object)blockPos.getY()), (int)(object = (Object)(blockPos.getZ() + random.nextInt(n10 * 2 + 1) - n10)));
                if (!worldGenLevel.isEmptyBlock((BlockPos)object2)) continue;
                int n14 = 0;
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    if (!worldGenLevel.getBlockState(((BlockPos)object2).relative(direction)).getMaterial().isSolid()) continue;
                    ++n14;
                }
                if (n14 != 1) continue;
                worldGenLevel.setBlock((BlockPos)object2, StructurePiece.reorient(worldGenLevel, (BlockPos)object2, Blocks.CHEST.defaultBlockState()), 2);
                RandomizableContainerBlockEntity.setLootTable(worldGenLevel, random, (BlockPos)object2, BuiltInLootTables.SIMPLE_DUNGEON);
                continue block6;
            }
        }
        worldGenLevel.setBlock(blockPos, Blocks.SPAWNER.defaultBlockState(), 2);
        BlockEntity blockEntity = worldGenLevel.getBlockEntity(blockPos);
        if (blockEntity instanceof SpawnerBlockEntity) {
            ((SpawnerBlockEntity)blockEntity).getSpawner().setEntityId(this.randomEntityId(random));
        } else {
            LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", (Object)blockPos.getX(), (Object)blockPos.getY(), (Object)blockPos.getZ());
        }
        return true;
    }

    private EntityType<?> randomEntityId(Random random) {
        return Util.getRandom(MOBS, random);
    }
}

