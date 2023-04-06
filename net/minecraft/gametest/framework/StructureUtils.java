/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.WritableRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerTickList;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickNextTickData;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.IOUtils;

public class StructureUtils {
    public static String testStructuresDir = "gameteststructures";

    public static Rotation getRotationForRotationSteps(int n) {
        switch (n) {
            case 0: {
                return Rotation.NONE;
            }
            case 1: {
                return Rotation.CLOCKWISE_90;
            }
            case 2: {
                return Rotation.CLOCKWISE_180;
            }
            case 3: {
                return Rotation.COUNTERCLOCKWISE_90;
            }
        }
        throw new IllegalArgumentException("rotationSteps must be a value from 0-3. Got value " + n);
    }

    public static AABB getStructureBounds(StructureBlockEntity structureBlockEntity) {
        BlockPos blockPos = structureBlockEntity.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(structureBlockEntity.getStructureSize().offset(-1, -1, -1));
        BlockPos blockPos3 = StructureTemplate.transform(blockPos2, Mirror.NONE, structureBlockEntity.getRotation(), blockPos);
        return new AABB(blockPos, blockPos3);
    }

    public static BoundingBox getStructureBoundingBox(StructureBlockEntity structureBlockEntity) {
        BlockPos blockPos = structureBlockEntity.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(structureBlockEntity.getStructureSize().offset(-1, -1, -1));
        BlockPos blockPos3 = StructureTemplate.transform(blockPos2, Mirror.NONE, structureBlockEntity.getRotation(), blockPos);
        return new BoundingBox(blockPos, blockPos3);
    }

    public static void addCommandBlockAndButtonToStartTest(BlockPos blockPos, BlockPos blockPos2, Rotation rotation, ServerLevel serverLevel) {
        BlockPos blockPos3 = StructureTemplate.transform(blockPos.offset(blockPos2), Mirror.NONE, rotation, blockPos);
        serverLevel.setBlockAndUpdate(blockPos3, Blocks.COMMAND_BLOCK.defaultBlockState());
        CommandBlockEntity commandBlockEntity = (CommandBlockEntity)serverLevel.getBlockEntity(blockPos3);
        commandBlockEntity.getCommandBlock().setCommand("test runthis");
        BlockPos blockPos4 = StructureTemplate.transform(blockPos3.offset(0, 0, -1), Mirror.NONE, rotation, blockPos3);
        serverLevel.setBlockAndUpdate(blockPos4, Blocks.STONE_BUTTON.defaultBlockState().rotate(rotation));
    }

    public static void createNewEmptyStructureBlock(String string, BlockPos blockPos, BlockPos blockPos2, Rotation rotation, ServerLevel serverLevel) {
        BoundingBox boundingBox = StructureUtils.getStructureBoundingBox(blockPos, blockPos2, rotation);
        StructureUtils.clearSpaceForStructure(boundingBox, blockPos.getY(), serverLevel);
        serverLevel.setBlockAndUpdate(blockPos, Blocks.STRUCTURE_BLOCK.defaultBlockState());
        StructureBlockEntity structureBlockEntity = (StructureBlockEntity)serverLevel.getBlockEntity(blockPos);
        structureBlockEntity.setIgnoreEntities(false);
        structureBlockEntity.setStructureName(new ResourceLocation(string));
        structureBlockEntity.setStructureSize(blockPos2);
        structureBlockEntity.setMode(StructureMode.SAVE);
        structureBlockEntity.setShowBoundingBox(true);
    }

    public static StructureBlockEntity spawnStructure(String string, BlockPos blockPos, Rotation rotation, int n, ServerLevel serverLevel, boolean bl) {
        BlockPos blockPos2;
        BlockPos blockPos3 = StructureUtils.getStructureTemplate(string, serverLevel).getSize();
        BoundingBox boundingBox = StructureUtils.getStructureBoundingBox(blockPos, blockPos3, rotation);
        if (rotation == Rotation.NONE) {
            blockPos2 = blockPos;
        } else if (rotation == Rotation.CLOCKWISE_90) {
            blockPos2 = blockPos.offset(blockPos3.getZ() - 1, 0, 0);
        } else if (rotation == Rotation.CLOCKWISE_180) {
            blockPos2 = blockPos.offset(blockPos3.getX() - 1, 0, blockPos3.getZ() - 1);
        } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
            blockPos2 = blockPos.offset(0, 0, blockPos3.getX() - 1);
        } else {
            throw new IllegalArgumentException("Invalid rotation: " + (Object)((Object)rotation));
        }
        StructureUtils.forceLoadChunks(blockPos, serverLevel);
        StructureUtils.clearSpaceForStructure(boundingBox, blockPos.getY(), serverLevel);
        StructureBlockEntity structureBlockEntity = StructureUtils.createStructureBlock(string, blockPos2, rotation, serverLevel, bl);
        ((ServerTickList)serverLevel.getBlockTicks()).fetchTicksInArea(boundingBox, true, false);
        serverLevel.clearBlockEvents(boundingBox);
        return structureBlockEntity;
    }

    private static void forceLoadChunks(BlockPos blockPos, ServerLevel serverLevel) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        for (int i = -1; i < 4; ++i) {
            for (int j = -1; j < 4; ++j) {
                int n = chunkPos.x + i;
                int n2 = chunkPos.z + j;
                serverLevel.setChunkForced(n, n2, true);
            }
        }
    }

    public static void clearSpaceForStructure(BoundingBox boundingBox, int n, ServerLevel serverLevel) {
        BoundingBox boundingBox2 = new BoundingBox(boundingBox.x0 - 2, boundingBox.y0 - 3, boundingBox.z0 - 3, boundingBox.x1 + 3, boundingBox.y1 + 20, boundingBox.z1 + 3);
        BlockPos.betweenClosedStream(boundingBox2).forEach(blockPos -> StructureUtils.clearBlock(n, blockPos, serverLevel));
        ((ServerTickList)serverLevel.getBlockTicks()).fetchTicksInArea(boundingBox2, true, false);
        serverLevel.clearBlockEvents(boundingBox2);
        AABB aABB = new AABB(boundingBox2.x0, boundingBox2.y0, boundingBox2.z0, boundingBox2.x1, boundingBox2.y1, boundingBox2.z1);
        List<Entity> list = serverLevel.getEntitiesOfClass(Entity.class, aABB, entity -> !(entity instanceof Player));
        list.forEach(Entity::remove);
    }

    public static BoundingBox getStructureBoundingBox(BlockPos blockPos, BlockPos blockPos2, Rotation rotation) {
        BlockPos blockPos3 = blockPos.offset(blockPos2).offset(-1, -1, -1);
        BlockPos blockPos4 = StructureTemplate.transform(blockPos3, Mirror.NONE, rotation, blockPos);
        BoundingBox boundingBox = BoundingBox.createProper(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos4.getX(), blockPos4.getY(), blockPos4.getZ());
        int n = Math.min(boundingBox.x0, boundingBox.x1);
        int n2 = Math.min(boundingBox.z0, boundingBox.z1);
        BlockPos blockPos5 = new BlockPos(blockPos.getX() - n, 0, blockPos.getZ() - n2);
        boundingBox.move(blockPos5);
        return boundingBox;
    }

    public static Optional<BlockPos> findStructureBlockContainingPos(BlockPos blockPos, int n, ServerLevel serverLevel) {
        return StructureUtils.findStructureBlocks(blockPos, n, serverLevel).stream().filter(blockPos2 -> StructureUtils.doesStructureContain(blockPos2, blockPos, serverLevel)).findFirst();
    }

    @Nullable
    public static BlockPos findNearestStructureBlock(BlockPos blockPos, int n, ServerLevel serverLevel) {
        Comparator<BlockPos> comparator = Comparator.comparingInt(blockPos2 -> blockPos2.distManhattan(blockPos));
        Collection<BlockPos> collection = StructureUtils.findStructureBlocks(blockPos, n, serverLevel);
        Optional<BlockPos> optional = collection.stream().min(comparator);
        return optional.orElse(null);
    }

    public static Collection<BlockPos> findStructureBlocks(BlockPos blockPos, int n, ServerLevel serverLevel) {
        ArrayList arrayList = Lists.newArrayList();
        AABB aABB = new AABB(blockPos);
        aABB = aABB.inflate(n);
        for (int i = (int)aABB.minX; i <= (int)aABB.maxX; ++i) {
            for (int j = (int)aABB.minY; j <= (int)aABB.maxY; ++j) {
                for (int k = (int)aABB.minZ; k <= (int)aABB.maxZ; ++k) {
                    BlockPos blockPos2 = new BlockPos(i, j, k);
                    BlockState blockState = serverLevel.getBlockState(blockPos2);
                    if (!blockState.is(Blocks.STRUCTURE_BLOCK)) continue;
                    arrayList.add(blockPos2);
                }
            }
        }
        return arrayList;
    }

    private static StructureTemplate getStructureTemplate(String string, ServerLevel serverLevel) {
        StructureManager structureManager = serverLevel.getStructureManager();
        StructureTemplate structureTemplate = structureManager.get(new ResourceLocation(string));
        if (structureTemplate != null) {
            return structureTemplate;
        }
        String string2 = string + ".snbt";
        Path path = Paths.get(testStructuresDir, string2);
        CompoundTag compoundTag = StructureUtils.tryLoadStructure(path);
        if (compoundTag == null) {
            throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
        }
        return structureManager.readStructure(compoundTag);
    }

    private static StructureBlockEntity createStructureBlock(String string, BlockPos blockPos, Rotation rotation, ServerLevel serverLevel, boolean bl) {
        serverLevel.setBlockAndUpdate(blockPos, Blocks.STRUCTURE_BLOCK.defaultBlockState());
        StructureBlockEntity structureBlockEntity = (StructureBlockEntity)serverLevel.getBlockEntity(blockPos);
        structureBlockEntity.setMode(StructureMode.LOAD);
        structureBlockEntity.setRotation(rotation);
        structureBlockEntity.setIgnoreEntities(false);
        structureBlockEntity.setStructureName(new ResourceLocation(string));
        structureBlockEntity.loadStructure(serverLevel, bl);
        if (structureBlockEntity.getStructureSize() != BlockPos.ZERO) {
            return structureBlockEntity;
        }
        StructureTemplate structureTemplate = StructureUtils.getStructureTemplate(string, serverLevel);
        structureBlockEntity.loadStructure(serverLevel, bl, structureTemplate);
        if (structureBlockEntity.getStructureSize() == BlockPos.ZERO) {
            throw new RuntimeException("Failed to load structure " + string);
        }
        return structureBlockEntity;
    }

    @Nullable
    private static CompoundTag tryLoadStructure(Path path) {
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            String string = IOUtils.toString((Reader)bufferedReader);
            return TagParser.parseTag(string);
        }
        catch (IOException iOException) {
            return null;
        }
        catch (CommandSyntaxException commandSyntaxException) {
            throw new RuntimeException("Error while trying to load structure " + path, commandSyntaxException);
        }
    }

    private static void clearBlock(int n, BlockPos blockPos, ServerLevel serverLevel) {
        Object object;
        BlockState blockState = null;
        FlatLevelGeneratorSettings flatLevelGeneratorSettings = FlatLevelGeneratorSettings.getDefault(serverLevel.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY));
        if (flatLevelGeneratorSettings instanceof FlatLevelGeneratorSettings) {
            object = flatLevelGeneratorSettings.getLayers();
            if (blockPos.getY() < n && blockPos.getY() <= ((BlockState[])object).length) {
                blockState = object[blockPos.getY() - 1];
            }
        } else if (blockPos.getY() == n - 1) {
            blockState = serverLevel.getBiome(blockPos).getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
        } else if (blockPos.getY() < n - 1) {
            blockState = serverLevel.getBiome(blockPos).getGenerationSettings().getSurfaceBuilderConfig().getUnderMaterial();
        }
        if (blockState == null) {
            blockState = Blocks.AIR.defaultBlockState();
        }
        object = new BlockInput(blockState, Collections.emptySet(), null);
        ((BlockInput)object).place(serverLevel, blockPos, 2);
        serverLevel.blockUpdated(blockPos, blockState.getBlock());
    }

    private static boolean doesStructureContain(BlockPos blockPos, BlockPos blockPos2, ServerLevel serverLevel) {
        StructureBlockEntity structureBlockEntity = (StructureBlockEntity)serverLevel.getBlockEntity(blockPos);
        AABB aABB = StructureUtils.getStructureBounds(structureBlockEntity).inflate(1.0);
        return aABB.contains(Vec3.atCenterOf(blockPos2));
    }
}

