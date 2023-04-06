/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class StructureTemplate {
    private final List<Palette> palettes = Lists.newArrayList();
    private final List<StructureEntityInfo> entityInfoList = Lists.newArrayList();
    private BlockPos size = BlockPos.ZERO;
    private String author = "?";

    public BlockPos getSize() {
        return this.size;
    }

    public void setAuthor(String string) {
        this.author = string;
    }

    public String getAuthor() {
        return this.author;
    }

    public void fillFromWorld(Level level, BlockPos blockPos, BlockPos blockPos2, boolean bl, @Nullable Block block) {
        if (blockPos2.getX() < 1 || blockPos2.getY() < 1 || blockPos2.getZ() < 1) {
            return;
        }
        BlockPos blockPos3 = blockPos.offset(blockPos2).offset(-1, -1, -1);
        ArrayList arrayList = Lists.newArrayList();
        ArrayList arrayList2 = Lists.newArrayList();
        ArrayList arrayList3 = Lists.newArrayList();
        BlockPos blockPos4 = new BlockPos(Math.min(blockPos.getX(), blockPos3.getX()), Math.min(blockPos.getY(), blockPos3.getY()), Math.min(blockPos.getZ(), blockPos3.getZ()));
        BlockPos blockPos5 = new BlockPos(Math.max(blockPos.getX(), blockPos3.getX()), Math.max(blockPos.getY(), blockPos3.getY()), Math.max(blockPos.getZ(), blockPos3.getZ()));
        this.size = blockPos2;
        for (BlockPos blockPos6 : BlockPos.betweenClosed(blockPos4, blockPos5)) {
            StructureBlockInfo structureBlockInfo;
            BlockPos blockPos7 = blockPos6.subtract(blockPos4);
            BlockState blockState = level.getBlockState(blockPos6);
            if (block != null && block == blockState.getBlock()) continue;
            BlockEntity blockEntity = level.getBlockEntity(blockPos6);
            if (blockEntity != null) {
                CompoundTag compoundTag = blockEntity.save(new CompoundTag());
                compoundTag.remove("x");
                compoundTag.remove("y");
                compoundTag.remove("z");
                structureBlockInfo = new StructureBlockInfo(blockPos7, blockState, compoundTag.copy());
            } else {
                structureBlockInfo = new StructureBlockInfo(blockPos7, blockState, null);
            }
            StructureTemplate.addToLists(structureBlockInfo, arrayList, arrayList2, arrayList3);
        }
        List<StructureBlockInfo> list = StructureTemplate.buildInfoList(arrayList, arrayList2, arrayList3);
        this.palettes.clear();
        this.palettes.add(new Palette(list));
        if (bl) {
            this.fillEntityList(level, blockPos4, blockPos5.offset(1, 1, 1));
        } else {
            this.entityInfoList.clear();
        }
    }

    private static void addToLists(StructureBlockInfo structureBlockInfo, List<StructureBlockInfo> list, List<StructureBlockInfo> list2, List<StructureBlockInfo> list3) {
        if (structureBlockInfo.nbt != null) {
            list2.add(structureBlockInfo);
        } else if (!structureBlockInfo.state.getBlock().hasDynamicShape() && structureBlockInfo.state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
            list.add(structureBlockInfo);
        } else {
            list3.add(structureBlockInfo);
        }
    }

    private static List<StructureBlockInfo> buildInfoList(List<StructureBlockInfo> list, List<StructureBlockInfo> list2, List<StructureBlockInfo> list3) {
        Comparator<StructureBlockInfo> comparator = Comparator.comparingInt(structureBlockInfo -> structureBlockInfo.pos.getY()).thenComparingInt(structureBlockInfo -> structureBlockInfo.pos.getX()).thenComparingInt(structureBlockInfo -> structureBlockInfo.pos.getZ());
        list.sort(comparator);
        list3.sort(comparator);
        list2.sort(comparator);
        ArrayList arrayList = Lists.newArrayList();
        arrayList.addAll(list);
        arrayList.addAll(list3);
        arrayList.addAll(list2);
        return arrayList;
    }

    private void fillEntityList(Level level, BlockPos blockPos, BlockPos blockPos2) {
        List<Entity> list = level.getEntitiesOfClass(Entity.class, new AABB(blockPos, blockPos2), entity -> !(entity instanceof Player));
        this.entityInfoList.clear();
        for (Entity entity2 : list) {
            Vec3 vec3 = new Vec3(entity2.getX() - (double)blockPos.getX(), entity2.getY() - (double)blockPos.getY(), entity2.getZ() - (double)blockPos.getZ());
            CompoundTag compoundTag = new CompoundTag();
            entity2.save(compoundTag);
            BlockPos blockPos3 = entity2 instanceof Painting ? ((Painting)entity2).getPos().subtract(blockPos) : new BlockPos(vec3);
            this.entityInfoList.add(new StructureEntityInfo(vec3, blockPos3, compoundTag.copy()));
        }
    }

    public List<StructureBlockInfo> filterBlocks(BlockPos blockPos, StructurePlaceSettings structurePlaceSettings, Block block) {
        return this.filterBlocks(blockPos, structurePlaceSettings, block, true);
    }

    public List<StructureBlockInfo> filterBlocks(BlockPos blockPos, StructurePlaceSettings structurePlaceSettings, Block block, boolean bl) {
        ArrayList arrayList = Lists.newArrayList();
        BoundingBox boundingBox = structurePlaceSettings.getBoundingBox();
        if (this.palettes.isEmpty()) {
            return Collections.emptyList();
        }
        for (StructureBlockInfo structureBlockInfo : structurePlaceSettings.getRandomPalette(this.palettes, blockPos).blocks(block)) {
            BlockPos blockPos2;
            BlockPos blockPos3 = blockPos2 = bl ? StructureTemplate.calculateRelativePosition(structurePlaceSettings, structureBlockInfo.pos).offset(blockPos) : structureBlockInfo.pos;
            if (boundingBox != null && !boundingBox.isInside(blockPos2)) continue;
            arrayList.add(new StructureBlockInfo(blockPos2, structureBlockInfo.state.rotate(structurePlaceSettings.getRotation()), structureBlockInfo.nbt));
        }
        return arrayList;
    }

    public BlockPos calculateConnectedPosition(StructurePlaceSettings structurePlaceSettings, BlockPos blockPos, StructurePlaceSettings structurePlaceSettings2, BlockPos blockPos2) {
        BlockPos blockPos3 = StructureTemplate.calculateRelativePosition(structurePlaceSettings, blockPos);
        BlockPos blockPos4 = StructureTemplate.calculateRelativePosition(structurePlaceSettings2, blockPos2);
        return blockPos3.subtract(blockPos4);
    }

    public static BlockPos calculateRelativePosition(StructurePlaceSettings structurePlaceSettings, BlockPos blockPos) {
        return StructureTemplate.transform(blockPos, structurePlaceSettings.getMirror(), structurePlaceSettings.getRotation(), structurePlaceSettings.getRotationPivot());
    }

    public void placeInWorldChunk(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, StructurePlaceSettings structurePlaceSettings, Random random) {
        structurePlaceSettings.updateBoundingBoxFromChunkPos();
        this.placeInWorld(serverLevelAccessor, blockPos, structurePlaceSettings, random);
    }

    public void placeInWorld(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, StructurePlaceSettings structurePlaceSettings, Random random) {
        this.placeInWorld(serverLevelAccessor, blockPos, blockPos, structurePlaceSettings, random, 2);
    }

    public boolean placeInWorld(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, BlockPos blockPos2, StructurePlaceSettings structurePlaceSettings, Random random, int n) {
        Object object;
        Object n10;
        Object object2;
        Object n9;
        Object n8;
        if (this.palettes.isEmpty()) {
            return false;
        }
        List<StructureBlockInfo> list = structurePlaceSettings.getRandomPalette(this.palettes, blockPos).blocks();
        if (list.isEmpty() && (structurePlaceSettings.isIgnoreEntities() || this.entityInfoList.isEmpty()) || this.size.getX() < 1 || this.size.getY() < 1 || this.size.getZ() < 1) {
            return false;
        }
        BoundingBox boundingBox = structurePlaceSettings.getBoundingBox();
        ArrayList arrayList = Lists.newArrayListWithCapacity((int)(structurePlaceSettings.shouldKeepLiquids() ? list.size() : 0));
        ArrayList arrayList2 = Lists.newArrayListWithCapacity((int)list.size());
        int n2 = Integer.MAX_VALUE;
        int n3 = Integer.MAX_VALUE;
        int n4 = Integer.MAX_VALUE;
        int n5 = Integer.MIN_VALUE;
        int n6 = Integer.MIN_VALUE;
        int n7 = Integer.MIN_VALUE;
        List<StructureBlockInfo> list2 = StructureTemplate.processBlockInfos(serverLevelAccessor, blockPos, blockPos2, structurePlaceSettings, list);
        for (StructureBlockInfo arrdirection2 : list2) {
            object2 = arrdirection2.pos;
            if (boundingBox != null && !boundingBox.isInside((Vec3i)object2)) continue;
            n8 = structurePlaceSettings.shouldKeepLiquids() ? serverLevelAccessor.getFluidState((BlockPos)object2) : null;
            n9 = arrdirection2.state.mirror(structurePlaceSettings.getMirror()).rotate(structurePlaceSettings.getRotation());
            if (arrdirection2.nbt != null) {
                n10 = serverLevelAccessor.getBlockEntity((BlockPos)object2);
                Clearable.tryClear(n10);
                serverLevelAccessor.setBlock((BlockPos)object2, Blocks.BARRIER.defaultBlockState(), 20);
            }
            if (!serverLevelAccessor.setBlock((BlockPos)object2, (BlockState)n9, n)) continue;
            n2 = Math.min(n2, ((Vec3i)object2).getX());
            n3 = Math.min(n3, ((Vec3i)object2).getY());
            n4 = Math.min(n4, ((Vec3i)object2).getZ());
            n5 = Math.max(n5, ((Vec3i)object2).getX());
            n6 = Math.max(n6, ((Vec3i)object2).getY());
            n7 = Math.max(n7, ((Vec3i)object2).getZ());
            arrayList2.add(Pair.of((Object)object2, (Object)arrdirection2.nbt));
            if (arrdirection2.nbt != null && (n10 = serverLevelAccessor.getBlockEntity((BlockPos)object2)) != null) {
                arrdirection2.nbt.putInt("x", ((Vec3i)object2).getX());
                arrdirection2.nbt.putInt("y", ((Vec3i)object2).getY());
                arrdirection2.nbt.putInt("z", ((Vec3i)object2).getZ());
                if (n10 instanceof RandomizableContainerBlockEntity) {
                    arrdirection2.nbt.putLong("LootTableSeed", random.nextLong());
                }
                ((BlockEntity)n10).load(arrdirection2.state, arrdirection2.nbt);
                ((BlockEntity)n10).mirror(structurePlaceSettings.getMirror());
                ((BlockEntity)n10).rotate(structurePlaceSettings.getRotation());
            }
            if (n8 == null || !(((BlockBehaviour.BlockStateBase)n9).getBlock() instanceof LiquidBlockContainer)) continue;
            ((LiquidBlockContainer)((Object)((BlockBehaviour.BlockStateBase)n9).getBlock())).placeLiquid(serverLevelAccessor, (BlockPos)object2, (BlockState)n9, (FluidState)n8);
            if (((FluidState)n8).isSource()) continue;
            arrayList.add(object2);
        }
        boolean bl = true;
        Direction[] arrdirection = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        while (bl && !arrayList.isEmpty()) {
            bl = false;
            object2 = arrayList.iterator();
            while (object2.hasNext()) {
                Object object3;
                BlockState blockState;
                n9 = n8 = (BlockPos)object2.next();
                n10 = serverLevelAccessor.getFluidState((BlockPos)n9);
                for (int blockState2 = 0; blockState2 < arrdirection.length && !((FluidState)n10).isSource(); ++blockState2) {
                    object3 = ((BlockPos)n9).relative(arrdirection[blockState2]);
                    object = serverLevelAccessor.getFluidState((BlockPos)object3);
                    if (!(((FluidState)object).getHeight(serverLevelAccessor, (BlockPos)object3) > ((FluidState)n10).getHeight(serverLevelAccessor, (BlockPos)n9)) && (!((FluidState)object).isSource() || ((FluidState)n10).isSource())) continue;
                    n10 = object;
                    n9 = object3;
                }
                if (!((FluidState)n10).isSource() || !((object3 = (blockState = serverLevelAccessor.getBlockState((BlockPos)n8)).getBlock()) instanceof LiquidBlockContainer)) continue;
                ((LiquidBlockContainer)object3).placeLiquid(serverLevelAccessor, (BlockPos)n8, blockState, (FluidState)n10);
                bl = true;
                object2.remove();
            }
        }
        if (n2 <= n5) {
            if (!structurePlaceSettings.getKnownShape()) {
                object2 = new BitSetDiscreteVoxelShape(n5 - n2 + 1, n6 - n3 + 1, n7 - n4 + 1);
                int pair = n2;
                int blockPos3 = n3;
                int n11 = n4;
                for (Object object3 : arrayList2) {
                    object = (BlockPos)object3.getFirst();
                    ((DiscreteVoxelShape)object2).setFull(((Vec3i)object).getX() - pair, ((Vec3i)object).getY() - blockPos3, ((Vec3i)object).getZ() - n11, true, true);
                }
                StructureTemplate.updateShapeAtEdge(serverLevelAccessor, n, (DiscreteVoxelShape)object2, pair, blockPos3, n11);
            }
            for (Pair pair : arrayList2) {
                BlockEntity blockEntity;
                BlockPos blockPos3 = (BlockPos)pair.getFirst();
                if (!structurePlaceSettings.getKnownShape()) {
                    BlockState blockState;
                    BlockState blockEntity2 = serverLevelAccessor.getBlockState(blockPos3);
                    if (blockEntity2 != (blockState = Block.updateFromNeighbourShapes(blockEntity2, serverLevelAccessor, blockPos3))) {
                        serverLevelAccessor.setBlock(blockPos3, blockState, n & 0xFFFFFFFE | 0x10);
                    }
                    serverLevelAccessor.blockUpdated(blockPos3, blockState.getBlock());
                }
                if (pair.getSecond() == null || (blockEntity = serverLevelAccessor.getBlockEntity(blockPos3)) == null) continue;
                blockEntity.setChanged();
            }
        }
        if (!structurePlaceSettings.isIgnoreEntities()) {
            this.placeEntities(serverLevelAccessor, blockPos, structurePlaceSettings.getMirror(), structurePlaceSettings.getRotation(), structurePlaceSettings.getRotationPivot(), boundingBox, structurePlaceSettings.shouldFinalizeEntities());
        }
        return true;
    }

    public static void updateShapeAtEdge(LevelAccessor levelAccessor, int n, DiscreteVoxelShape discreteVoxelShape, int n2, int n3, int n4) {
        discreteVoxelShape.forAllFaces((direction, n5, n6, n7) -> {
            BlockState blockState;
            BlockState blockState2;
            BlockState blockState3;
            BlockPos blockPos = new BlockPos(n2 + n5, n3 + n6, n4 + n7);
            BlockPos blockPos2 = blockPos.relative(direction);
            BlockState blockState4 = levelAccessor.getBlockState(blockPos);
            if (blockState4 != (blockState2 = blockState4.updateShape(direction, blockState = levelAccessor.getBlockState(blockPos2), levelAccessor, blockPos, blockPos2))) {
                levelAccessor.setBlock(blockPos, blockState2, n & 0xFFFFFFFE);
            }
            if (blockState != (blockState3 = blockState.updateShape(direction.getOpposite(), blockState2, levelAccessor, blockPos2, blockPos))) {
                levelAccessor.setBlock(blockPos2, blockState3, n & 0xFFFFFFFE);
            }
        });
    }

    public static List<StructureBlockInfo> processBlockInfos(LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2, StructurePlaceSettings structurePlaceSettings, List<StructureBlockInfo> list) {
        ArrayList arrayList = Lists.newArrayList();
        for (StructureBlockInfo structureBlockInfo : list) {
            BlockPos blockPos3 = StructureTemplate.calculateRelativePosition(structurePlaceSettings, structureBlockInfo.pos).offset(blockPos);
            StructureBlockInfo structureBlockInfo2 = new StructureBlockInfo(blockPos3, structureBlockInfo.state, structureBlockInfo.nbt != null ? structureBlockInfo.nbt.copy() : null);
            Iterator<StructureProcessor> iterator = structurePlaceSettings.getProcessors().iterator();
            while (structureBlockInfo2 != null && iterator.hasNext()) {
                structureBlockInfo2 = iterator.next().processBlock(levelAccessor, blockPos, blockPos2, structureBlockInfo, structureBlockInfo2, structurePlaceSettings);
            }
            if (structureBlockInfo2 == null) continue;
            arrayList.add(structureBlockInfo2);
        }
        return arrayList;
    }

    private void placeEntities(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, Mirror mirror, Rotation rotation, BlockPos blockPos2, @Nullable BoundingBox boundingBox, boolean bl) {
        for (StructureEntityInfo structureEntityInfo : this.entityInfoList) {
            BlockPos blockPos3 = StructureTemplate.transform(structureEntityInfo.blockPos, mirror, rotation, blockPos2).offset(blockPos);
            if (boundingBox != null && !boundingBox.isInside(blockPos3)) continue;
            CompoundTag compoundTag = structureEntityInfo.nbt.copy();
            Vec3 vec3 = StructureTemplate.transform(structureEntityInfo.pos, mirror, rotation, blockPos2);
            Vec3 vec32 = vec3.add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            ListTag listTag = new ListTag();
            listTag.add(DoubleTag.valueOf(vec32.x));
            listTag.add(DoubleTag.valueOf(vec32.y));
            listTag.add(DoubleTag.valueOf(vec32.z));
            compoundTag.put("Pos", listTag);
            compoundTag.remove("UUID");
            StructureTemplate.createEntityIgnoreException(serverLevelAccessor, compoundTag).ifPresent(entity -> {
                float f = entity.mirror(mirror);
                entity.moveTo(vec3.x, vec3.y, vec3.z, f += entity.yRot - entity.rotate(rotation), entity.xRot);
                if (bl && entity instanceof Mob) {
                    ((Mob)entity).finalizeSpawn(serverLevelAccessor, serverLevelAccessor.getCurrentDifficultyAt(new BlockPos(vec32)), MobSpawnType.STRUCTURE, null, compoundTag);
                }
                serverLevelAccessor.addFreshEntityWithPassengers((Entity)entity);
            });
        }
    }

    private static Optional<Entity> createEntityIgnoreException(ServerLevelAccessor serverLevelAccessor, CompoundTag compoundTag) {
        try {
            return EntityType.create(compoundTag, (Level)serverLevelAccessor.getLevel());
        }
        catch (Exception exception) {
            return Optional.empty();
        }
    }

    public BlockPos getSize(Rotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
            }
        }
        return this.size;
    }

    public static BlockPos transform(BlockPos blockPos, Mirror mirror, Rotation rotation, BlockPos blockPos2) {
        int n = blockPos.getX();
        int n2 = blockPos.getY();
        int n3 = blockPos.getZ();
        boolean bl = true;
        switch (mirror) {
            case LEFT_RIGHT: {
                n3 = -n3;
                break;
            }
            case FRONT_BACK: {
                n = -n;
                break;
            }
            default: {
                bl = false;
            }
        }
        int n4 = blockPos2.getX();
        int n5 = blockPos2.getZ();
        switch (rotation) {
            case CLOCKWISE_180: {
                return new BlockPos(n4 + n4 - n, n2, n5 + n5 - n3);
            }
            case COUNTERCLOCKWISE_90: {
                return new BlockPos(n4 - n5 + n3, n2, n4 + n5 - n);
            }
            case CLOCKWISE_90: {
                return new BlockPos(n4 + n5 - n3, n2, n5 - n4 + n);
            }
        }
        return bl ? new BlockPos(n, n2, n3) : blockPos;
    }

    public static Vec3 transform(Vec3 vec3, Mirror mirror, Rotation rotation, BlockPos blockPos) {
        double d = vec3.x;
        double d2 = vec3.y;
        double d3 = vec3.z;
        boolean bl = true;
        switch (mirror) {
            case LEFT_RIGHT: {
                d3 = 1.0 - d3;
                break;
            }
            case FRONT_BACK: {
                d = 1.0 - d;
                break;
            }
            default: {
                bl = false;
            }
        }
        int n = blockPos.getX();
        int n2 = blockPos.getZ();
        switch (rotation) {
            case CLOCKWISE_180: {
                return new Vec3((double)(n + n + 1) - d, d2, (double)(n2 + n2 + 1) - d3);
            }
            case COUNTERCLOCKWISE_90: {
                return new Vec3((double)(n - n2) + d3, d2, (double)(n + n2 + 1) - d);
            }
            case CLOCKWISE_90: {
                return new Vec3((double)(n + n2 + 1) - d3, d2, (double)(n2 - n) + d);
            }
        }
        return bl ? new Vec3(d, d2, d3) : vec3;
    }

    public BlockPos getZeroPositionWithTransform(BlockPos blockPos, Mirror mirror, Rotation rotation) {
        return StructureTemplate.getZeroPositionWithTransform(blockPos, mirror, rotation, this.getSize().getX(), this.getSize().getZ());
    }

    public static BlockPos getZeroPositionWithTransform(BlockPos blockPos, Mirror mirror, Rotation rotation, int n, int n2) {
        int n3 = mirror == Mirror.FRONT_BACK ? --n : 0;
        int n4 = mirror == Mirror.LEFT_RIGHT ? --n2 : 0;
        BlockPos blockPos2 = blockPos;
        switch (rotation) {
            case NONE: {
                blockPos2 = blockPos.offset(n3, 0, n4);
                break;
            }
            case CLOCKWISE_90: {
                blockPos2 = blockPos.offset(n2 - n4, 0, n3);
                break;
            }
            case CLOCKWISE_180: {
                blockPos2 = blockPos.offset(n - n3, 0, n2 - n4);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                blockPos2 = blockPos.offset(n4, 0, n - n3);
            }
        }
        return blockPos2;
    }

    public BoundingBox getBoundingBox(StructurePlaceSettings structurePlaceSettings, BlockPos blockPos) {
        return this.getBoundingBox(blockPos, structurePlaceSettings.getRotation(), structurePlaceSettings.getRotationPivot(), structurePlaceSettings.getMirror());
    }

    public BoundingBox getBoundingBox(BlockPos blockPos, Rotation rotation, BlockPos blockPos2, Mirror mirror) {
        BlockPos blockPos3 = this.getSize(rotation);
        int n = blockPos2.getX();
        int n2 = blockPos2.getZ();
        int n3 = blockPos3.getX() - 1;
        int n4 = blockPos3.getY() - 1;
        int n5 = blockPos3.getZ() - 1;
        BoundingBox boundingBox = new BoundingBox(0, 0, 0, 0, 0, 0);
        switch (rotation) {
            case NONE: {
                boundingBox = new BoundingBox(0, 0, 0, n3, n4, n5);
                break;
            }
            case CLOCKWISE_180: {
                boundingBox = new BoundingBox(n + n - n3, 0, n2 + n2 - n5, n + n, n4, n2 + n2);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                boundingBox = new BoundingBox(n - n2, 0, n + n2 - n5, n - n2 + n3, n4, n + n2);
                break;
            }
            case CLOCKWISE_90: {
                boundingBox = new BoundingBox(n + n2 - n3, 0, n2 - n, n + n2, n4, n2 - n + n5);
            }
        }
        switch (mirror) {
            case NONE: {
                break;
            }
            case FRONT_BACK: {
                this.mirrorAABB(rotation, n3, n5, boundingBox, Direction.WEST, Direction.EAST);
                break;
            }
            case LEFT_RIGHT: {
                this.mirrorAABB(rotation, n5, n3, boundingBox, Direction.NORTH, Direction.SOUTH);
            }
        }
        boundingBox.move(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        return boundingBox;
    }

    private void mirrorAABB(Rotation rotation, int n, int n2, BoundingBox boundingBox, Direction direction, Direction direction2) {
        BlockPos blockPos = BlockPos.ZERO;
        blockPos = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90 ? blockPos.relative(rotation.rotate(direction), n2) : (rotation == Rotation.CLOCKWISE_180 ? blockPos.relative(direction2, n) : blockPos.relative(direction, n));
        boundingBox.move(blockPos.getX(), 0, blockPos.getZ());
    }

    public CompoundTag save(CompoundTag compoundTag) {
        Object object;
        AbstractList abstractList;
        if (this.palettes.isEmpty()) {
            compoundTag.put("blocks", new ListTag());
            compoundTag.put("palette", new ListTag());
        } else {
            Object object22;
            Iterator iterator;
            Object object3;
            ListTag listTag;
            abstractList = Lists.newArrayList();
            SimplePalette simplePalette = new SimplePalette();
            abstractList.add(simplePalette);
            for (int i = 1; i < this.palettes.size(); ++i) {
                abstractList.add(new SimplePalette());
            }
            ListTag object4 = new ListTag();
            object = this.palettes.get(0).blocks();
            for (int i = 0; i < object.size(); ++i) {
                iterator = (StructureBlockInfo)object.get(i);
                object22 = new CompoundTag();
                ((CompoundTag)object22).put("pos", this.newIntegerList(((StructureBlockInfo)iterator).pos.getX(), ((StructureBlockInfo)iterator).pos.getY(), ((StructureBlockInfo)iterator).pos.getZ()));
                int n = simplePalette.idFor(((StructureBlockInfo)iterator).state);
                ((CompoundTag)object22).putInt("state", n);
                if (((StructureBlockInfo)iterator).nbt != null) {
                    ((CompoundTag)object22).put("nbt", ((StructureBlockInfo)iterator).nbt);
                }
                object4.add(object22);
                for (int j = 1; j < this.palettes.size(); ++j) {
                    object3 = (SimplePalette)abstractList.get(j);
                    ((SimplePalette)object3).addMapping(this.palettes.get((int)j).blocks().get((int)i).state, n);
                }
            }
            compoundTag.put("blocks", object4);
            if (abstractList.size() == 1) {
                listTag = new ListTag();
                iterator = simplePalette.iterator();
                while (iterator.hasNext()) {
                    object22 = (BlockState)iterator.next();
                    listTag.add(NbtUtils.writeBlockState((BlockState)object22));
                }
                compoundTag.put("palette", listTag);
            } else {
                listTag = new ListTag();
                for (Object object22 : abstractList) {
                    ListTag listTag2 = new ListTag();
                    Iterator<BlockState> iterator2 = ((SimplePalette)object22).iterator();
                    while (iterator2.hasNext()) {
                        object3 = iterator2.next();
                        listTag2.add(NbtUtils.writeBlockState((BlockState)object3));
                    }
                    listTag.add(listTag2);
                }
                compoundTag.put("palettes", listTag);
            }
        }
        abstractList = new ListTag();
        for (StructureEntityInfo structureEntityInfo : this.entityInfoList) {
            object = new CompoundTag();
            ((CompoundTag)object).put("pos", this.newDoubleList(structureEntityInfo.pos.x, structureEntityInfo.pos.y, structureEntityInfo.pos.z));
            ((CompoundTag)object).put("blockPos", this.newIntegerList(structureEntityInfo.blockPos.getX(), structureEntityInfo.blockPos.getY(), structureEntityInfo.blockPos.getZ()));
            if (structureEntityInfo.nbt != null) {
                ((CompoundTag)object).put("nbt", structureEntityInfo.nbt);
            }
            abstractList.add(object);
        }
        compoundTag.put("entities", (Tag)((Object)abstractList));
        compoundTag.put("size", this.newIntegerList(this.size.getX(), this.size.getY(), this.size.getZ()));
        compoundTag.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        return compoundTag;
    }

    public void load(CompoundTag compoundTag) {
        int n;
        ListTag listTag;
        this.palettes.clear();
        this.entityInfoList.clear();
        ListTag listTag2 = compoundTag.getList("size", 3);
        this.size = new BlockPos(listTag2.getInt(0), listTag2.getInt(1), listTag2.getInt(2));
        ListTag listTag3 = compoundTag.getList("blocks", 10);
        if (compoundTag.contains("palettes", 9)) {
            listTag = compoundTag.getList("palettes", 9);
            for (n = 0; n < listTag.size(); ++n) {
                this.loadPalette(listTag.getList(n), listTag3);
            }
        } else {
            this.loadPalette(compoundTag.getList("palette", 10), listTag3);
        }
        listTag = compoundTag.getList("entities", 10);
        for (n = 0; n < listTag.size(); ++n) {
            CompoundTag compoundTag2 = listTag.getCompound(n);
            ListTag listTag4 = compoundTag2.getList("pos", 6);
            Vec3 vec3 = new Vec3(listTag4.getDouble(0), listTag4.getDouble(1), listTag4.getDouble(2));
            ListTag listTag5 = compoundTag2.getList("blockPos", 3);
            BlockPos blockPos = new BlockPos(listTag5.getInt(0), listTag5.getInt(1), listTag5.getInt(2));
            if (!compoundTag2.contains("nbt")) continue;
            CompoundTag compoundTag3 = compoundTag2.getCompound("nbt");
            this.entityInfoList.add(new StructureEntityInfo(vec3, blockPos, compoundTag3));
        }
    }

    private void loadPalette(ListTag listTag, ListTag listTag2) {
        SimplePalette simplePalette = new SimplePalette();
        for (int i = 0; i < listTag.size(); ++i) {
            simplePalette.addMapping(NbtUtils.readBlockState(listTag.getCompound(i)), i);
        }
        ArrayList arrayList = Lists.newArrayList();
        ArrayList arrayList2 = Lists.newArrayList();
        ArrayList arrayList3 = Lists.newArrayList();
        for (int i = 0; i < listTag2.size(); ++i) {
            CompoundTag compoundTag = listTag2.getCompound(i);
            ListTag listTag3 = compoundTag.getList("pos", 3);
            BlockPos blockPos = new BlockPos(listTag3.getInt(0), listTag3.getInt(1), listTag3.getInt(2));
            BlockState blockState = simplePalette.stateFor(compoundTag.getInt("state"));
            CompoundTag compoundTag2 = compoundTag.contains("nbt") ? compoundTag.getCompound("nbt") : null;
            StructureBlockInfo structureBlockInfo = new StructureBlockInfo(blockPos, blockState, compoundTag2);
            StructureTemplate.addToLists(structureBlockInfo, arrayList, arrayList2, arrayList3);
        }
        List<StructureBlockInfo> list = StructureTemplate.buildInfoList(arrayList, arrayList2, arrayList3);
        this.palettes.add(new Palette(list));
    }

    private ListTag newIntegerList(int ... arrn) {
        ListTag listTag = new ListTag();
        for (int n : arrn) {
            listTag.add(IntTag.valueOf(n));
        }
        return listTag;
    }

    private ListTag newDoubleList(double ... arrd) {
        ListTag listTag = new ListTag();
        for (double d : arrd) {
            listTag.add(DoubleTag.valueOf(d));
        }
        return listTag;
    }

    public static final class Palette {
        private final List<StructureBlockInfo> blocks;
        private final Map<Block, List<StructureBlockInfo>> cache = Maps.newHashMap();

        private Palette(List<StructureBlockInfo> list) {
            this.blocks = list;
        }

        public List<StructureBlockInfo> blocks() {
            return this.blocks;
        }

        public List<StructureBlockInfo> blocks(Block block2) {
            return this.cache.computeIfAbsent(block2, block -> this.blocks.stream().filter(structureBlockInfo -> structureBlockInfo.state.is((Block)block)).collect(Collectors.toList()));
        }
    }

    public static class StructureEntityInfo {
        public final Vec3 pos;
        public final BlockPos blockPos;
        public final CompoundTag nbt;

        public StructureEntityInfo(Vec3 vec3, BlockPos blockPos, CompoundTag compoundTag) {
            this.pos = vec3;
            this.blockPos = blockPos;
            this.nbt = compoundTag;
        }
    }

    public static class StructureBlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        public final CompoundTag nbt;

        public StructureBlockInfo(BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag) {
            this.pos = blockPos;
            this.state = blockState;
            this.nbt = compoundTag;
        }

        public String toString() {
            return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
        }
    }

    static class SimplePalette
    implements Iterable<BlockState> {
        public static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
        private final IdMapper<BlockState> ids = new IdMapper(16);
        private int lastId;

        private SimplePalette() {
        }

        public int idFor(BlockState blockState) {
            int n = this.ids.getId(blockState);
            if (n == -1) {
                n = this.lastId++;
                this.ids.addMapping(blockState, n);
            }
            return n;
        }

        @Nullable
        public BlockState stateFor(int n) {
            BlockState blockState = this.ids.byId(n);
            return blockState == null ? DEFAULT_BLOCK_STATE : blockState;
        }

        @Override
        public Iterator<BlockState> iterator() {
            return this.ids.iterator();
        }

        public void addMapping(BlockState blockState, int n) {
            this.ids.addMapping(blockState, n);
        }
    }

}

