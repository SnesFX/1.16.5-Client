/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;

public abstract class StructurePiece {
    protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
    protected BoundingBox boundingBox;
    @Nullable
    private Direction orientation;
    private Mirror mirror;
    private Rotation rotation;
    protected int genDepth;
    private final StructurePieceType type;
    private static final Set<Block> SHAPE_CHECK_BLOCKS = ImmutableSet.builder().add((Object)Blocks.NETHER_BRICK_FENCE).add((Object)Blocks.TORCH).add((Object)Blocks.WALL_TORCH).add((Object)Blocks.OAK_FENCE).add((Object)Blocks.SPRUCE_FENCE).add((Object)Blocks.DARK_OAK_FENCE).add((Object)Blocks.ACACIA_FENCE).add((Object)Blocks.BIRCH_FENCE).add((Object)Blocks.JUNGLE_FENCE).add((Object)Blocks.LADDER).add((Object)Blocks.IRON_BARS).build();

    protected StructurePiece(StructurePieceType structurePieceType, int n) {
        this.type = structurePieceType;
        this.genDepth = n;
    }

    public StructurePiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
        this(structurePieceType, compoundTag.getInt("GD"));
        int n;
        if (compoundTag.contains("BB")) {
            this.boundingBox = new BoundingBox(compoundTag.getIntArray("BB"));
        }
        this.setOrientation((n = compoundTag.getInt("O")) == -1 ? null : Direction.from2DDataValue(n));
    }

    public final CompoundTag createTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", Registry.STRUCTURE_PIECE.getKey(this.getType()).toString());
        compoundTag.put("BB", this.boundingBox.createTag());
        Direction direction = this.getOrientation();
        compoundTag.putInt("O", direction == null ? -1 : direction.get2DDataValue());
        compoundTag.putInt("GD", this.genDepth);
        this.addAdditionalSaveData(compoundTag);
        return compoundTag;
    }

    protected abstract void addAdditionalSaveData(CompoundTag var1);

    public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
    }

    public abstract boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7);

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public int getGenDepth() {
        return this.genDepth;
    }

    public boolean isCloseToChunk(ChunkPos chunkPos, int n) {
        int n2 = chunkPos.x << 4;
        int n3 = chunkPos.z << 4;
        return this.boundingBox.intersects(n2 - n, n3 - n, n2 + 15 + n, n3 + 15 + n);
    }

    public static StructurePiece findCollisionPiece(List<StructurePiece> list, BoundingBox boundingBox) {
        for (StructurePiece structurePiece : list) {
            if (structurePiece.getBoundingBox() == null || !structurePiece.getBoundingBox().intersects(boundingBox)) continue;
            return structurePiece;
        }
        return null;
    }

    protected boolean edgesLiquid(BlockGetter blockGetter, BoundingBox boundingBox) {
        int n;
        int n2;
        int n3 = Math.max(this.boundingBox.x0 - 1, boundingBox.x0);
        int n4 = Math.max(this.boundingBox.y0 - 1, boundingBox.y0);
        int n5 = Math.max(this.boundingBox.z0 - 1, boundingBox.z0);
        int n6 = Math.min(this.boundingBox.x1 + 1, boundingBox.x1);
        int n7 = Math.min(this.boundingBox.y1 + 1, boundingBox.y1);
        int n8 = Math.min(this.boundingBox.z1 + 1, boundingBox.z1);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (n2 = n3; n2 <= n6; ++n2) {
            for (n = n5; n <= n8; ++n) {
                if (blockGetter.getBlockState(mutableBlockPos.set(n2, n4, n)).getMaterial().isLiquid()) {
                    return true;
                }
                if (!blockGetter.getBlockState(mutableBlockPos.set(n2, n7, n)).getMaterial().isLiquid()) continue;
                return true;
            }
        }
        for (n2 = n3; n2 <= n6; ++n2) {
            for (n = n4; n <= n7; ++n) {
                if (blockGetter.getBlockState(mutableBlockPos.set(n2, n, n5)).getMaterial().isLiquid()) {
                    return true;
                }
                if (!blockGetter.getBlockState(mutableBlockPos.set(n2, n, n8)).getMaterial().isLiquid()) continue;
                return true;
            }
        }
        for (n2 = n5; n2 <= n8; ++n2) {
            for (n = n4; n <= n7; ++n) {
                if (blockGetter.getBlockState(mutableBlockPos.set(n3, n, n2)).getMaterial().isLiquid()) {
                    return true;
                }
                if (!blockGetter.getBlockState(mutableBlockPos.set(n6, n, n2)).getMaterial().isLiquid()) continue;
                return true;
            }
        }
        return false;
    }

    protected int getWorldX(int n, int n2) {
        Direction direction = this.getOrientation();
        if (direction == null) {
            return n;
        }
        switch (direction) {
            case NORTH: 
            case SOUTH: {
                return this.boundingBox.x0 + n;
            }
            case WEST: {
                return this.boundingBox.x1 - n2;
            }
            case EAST: {
                return this.boundingBox.x0 + n2;
            }
        }
        return n;
    }

    protected int getWorldY(int n) {
        if (this.getOrientation() == null) {
            return n;
        }
        return n + this.boundingBox.y0;
    }

    protected int getWorldZ(int n, int n2) {
        Direction direction = this.getOrientation();
        if (direction == null) {
            return n2;
        }
        switch (direction) {
            case NORTH: {
                return this.boundingBox.z1 - n2;
            }
            case SOUTH: {
                return this.boundingBox.z0 + n2;
            }
            case WEST: 
            case EAST: {
                return this.boundingBox.z0 + n;
            }
        }
        return n2;
    }

    protected void placeBlock(WorldGenLevel worldGenLevel, BlockState blockState, int n, int n2, int n3, BoundingBox boundingBox) {
        BlockPos blockPos = new BlockPos(this.getWorldX(n, n3), this.getWorldY(n2), this.getWorldZ(n, n3));
        if (!boundingBox.isInside(blockPos)) {
            return;
        }
        if (this.mirror != Mirror.NONE) {
            blockState = blockState.mirror(this.mirror);
        }
        if (this.rotation != Rotation.NONE) {
            blockState = blockState.rotate(this.rotation);
        }
        worldGenLevel.setBlock(blockPos, blockState, 2);
        FluidState fluidState = worldGenLevel.getFluidState(blockPos);
        if (!fluidState.isEmpty()) {
            worldGenLevel.getLiquidTicks().scheduleTick(blockPos, fluidState.getType(), 0);
        }
        if (SHAPE_CHECK_BLOCKS.contains(blockState.getBlock())) {
            worldGenLevel.getChunk(blockPos).markPosForPostprocessing(blockPos);
        }
    }

    protected BlockState getBlock(BlockGetter blockGetter, int n, int n2, int n3, BoundingBox boundingBox) {
        int n4;
        int n5;
        int n6 = this.getWorldX(n, n3);
        BlockPos blockPos = new BlockPos(n6, n5 = this.getWorldY(n2), n4 = this.getWorldZ(n, n3));
        if (!boundingBox.isInside(blockPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return blockGetter.getBlockState(blockPos);
    }

    protected boolean isInterior(LevelReader levelReader, int n, int n2, int n3, BoundingBox boundingBox) {
        int n4;
        int n5;
        int n6 = this.getWorldX(n, n3);
        BlockPos blockPos = new BlockPos(n6, n5 = this.getWorldY(n2 + 1), n4 = this.getWorldZ(n, n3));
        if (!boundingBox.isInside(blockPos)) {
            return false;
        }
        return n5 < levelReader.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, n6, n4);
    }

    protected void generateAirBox(WorldGenLevel worldGenLevel, BoundingBox boundingBox, int n, int n2, int n3, int n4, int n5, int n6) {
        for (int i = n2; i <= n5; ++i) {
            for (int j = n; j <= n4; ++j) {
                for (int k = n3; k <= n6; ++k) {
                    this.placeBlock(worldGenLevel, Blocks.AIR.defaultBlockState(), j, i, k, boundingBox);
                }
            }
        }
    }

    protected void generateBox(WorldGenLevel worldGenLevel, BoundingBox boundingBox, int n, int n2, int n3, int n4, int n5, int n6, BlockState blockState, BlockState blockState2, boolean bl) {
        for (int i = n2; i <= n5; ++i) {
            for (int j = n; j <= n4; ++j) {
                for (int k = n3; k <= n6; ++k) {
                    if (bl && this.getBlock(worldGenLevel, j, i, k, boundingBox).isAir()) continue;
                    if (i == n2 || i == n5 || j == n || j == n4 || k == n3 || k == n6) {
                        this.placeBlock(worldGenLevel, blockState, j, i, k, boundingBox);
                        continue;
                    }
                    this.placeBlock(worldGenLevel, blockState2, j, i, k, boundingBox);
                }
            }
        }
    }

    protected void generateBox(WorldGenLevel worldGenLevel, BoundingBox boundingBox, int n, int n2, int n3, int n4, int n5, int n6, boolean bl, Random random, BlockSelector blockSelector) {
        for (int i = n2; i <= n5; ++i) {
            for (int j = n; j <= n4; ++j) {
                for (int k = n3; k <= n6; ++k) {
                    if (bl && this.getBlock(worldGenLevel, j, i, k, boundingBox).isAir()) continue;
                    blockSelector.next(random, j, i, k, i == n2 || i == n5 || j == n || j == n4 || k == n3 || k == n6);
                    this.placeBlock(worldGenLevel, blockSelector.getNext(), j, i, k, boundingBox);
                }
            }
        }
    }

    protected void generateMaybeBox(WorldGenLevel worldGenLevel, BoundingBox boundingBox, Random random, float f, int n, int n2, int n3, int n4, int n5, int n6, BlockState blockState, BlockState blockState2, boolean bl, boolean bl2) {
        for (int i = n2; i <= n5; ++i) {
            for (int j = n; j <= n4; ++j) {
                for (int k = n3; k <= n6; ++k) {
                    if (random.nextFloat() > f || bl && this.getBlock(worldGenLevel, j, i, k, boundingBox).isAir() || bl2 && !this.isInterior(worldGenLevel, j, i, k, boundingBox)) continue;
                    if (i == n2 || i == n5 || j == n || j == n4 || k == n3 || k == n6) {
                        this.placeBlock(worldGenLevel, blockState, j, i, k, boundingBox);
                        continue;
                    }
                    this.placeBlock(worldGenLevel, blockState2, j, i, k, boundingBox);
                }
            }
        }
    }

    protected void maybeGenerateBlock(WorldGenLevel worldGenLevel, BoundingBox boundingBox, Random random, float f, int n, int n2, int n3, BlockState blockState) {
        if (random.nextFloat() < f) {
            this.placeBlock(worldGenLevel, blockState, n, n2, n3, boundingBox);
        }
    }

    protected void generateUpperHalfSphere(WorldGenLevel worldGenLevel, BoundingBox boundingBox, int n, int n2, int n3, int n4, int n5, int n6, BlockState blockState, boolean bl) {
        float f = n4 - n + 1;
        float f2 = n5 - n2 + 1;
        float f3 = n6 - n3 + 1;
        float f4 = (float)n + f / 2.0f;
        float f5 = (float)n3 + f3 / 2.0f;
        for (int i = n2; i <= n5; ++i) {
            float f6 = (float)(i - n2) / f2;
            for (int j = n; j <= n4; ++j) {
                float f7 = ((float)j - f4) / (f * 0.5f);
                for (int k = n3; k <= n6; ++k) {
                    float f8;
                    float f9 = ((float)k - f5) / (f3 * 0.5f);
                    if (bl && this.getBlock(worldGenLevel, j, i, k, boundingBox).isAir() || !((f8 = f7 * f7 + f6 * f6 + f9 * f9) <= 1.05f)) continue;
                    this.placeBlock(worldGenLevel, blockState, j, i, k, boundingBox);
                }
            }
        }
    }

    protected void fillColumnDown(WorldGenLevel worldGenLevel, BlockState blockState, int n, int n2, int n3, BoundingBox boundingBox) {
        int n4;
        int n5;
        int n6 = this.getWorldX(n, n3);
        if (!boundingBox.isInside(new BlockPos(n6, n5 = this.getWorldY(n2), n4 = this.getWorldZ(n, n3)))) {
            return;
        }
        while ((worldGenLevel.isEmptyBlock(new BlockPos(n6, n5, n4)) || worldGenLevel.getBlockState(new BlockPos(n6, n5, n4)).getMaterial().isLiquid()) && n5 > 1) {
            worldGenLevel.setBlock(new BlockPos(n6, n5, n4), blockState, 2);
            --n5;
        }
    }

    protected boolean createChest(WorldGenLevel worldGenLevel, BoundingBox boundingBox, Random random, int n, int n2, int n3, ResourceLocation resourceLocation) {
        BlockPos blockPos = new BlockPos(this.getWorldX(n, n3), this.getWorldY(n2), this.getWorldZ(n, n3));
        return this.createChest(worldGenLevel, boundingBox, random, blockPos, resourceLocation, null);
    }

    public static BlockState reorient(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        Object object3;
        Object object2 = null;
        for (Object object3 : Direction.Plane.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.relative((Direction)object3);
            BlockState blockState2 = blockGetter.getBlockState(blockPos2);
            if (blockState2.is(Blocks.CHEST)) {
                return blockState;
            }
            if (!blockState2.isSolidRender(blockGetter, blockPos2)) continue;
            if (object2 == null) {
                object2 = object3;
                continue;
            }
            object2 = null;
            break;
        }
        if (object2 != null) {
            return (BlockState)blockState.setValue(HorizontalDirectionalBlock.FACING, ((Direction)object2).getOpposite());
        }
        Object object4 = blockState.getValue(HorizontalDirectionalBlock.FACING);
        object3 = blockPos.relative((Direction)object4);
        if (blockGetter.getBlockState((BlockPos)object3).isSolidRender(blockGetter, (BlockPos)object3)) {
            object4 = ((Direction)object4).getOpposite();
            object3 = blockPos.relative((Direction)object4);
        }
        if (blockGetter.getBlockState((BlockPos)object3).isSolidRender(blockGetter, (BlockPos)object3)) {
            object4 = ((Direction)object4).getClockWise();
            object3 = blockPos.relative((Direction)object4);
        }
        if (blockGetter.getBlockState((BlockPos)object3).isSolidRender(blockGetter, (BlockPos)object3)) {
            object4 = ((Direction)object4).getOpposite();
            object3 = blockPos.relative((Direction)object4);
        }
        return (BlockState)blockState.setValue(HorizontalDirectionalBlock.FACING, object4);
    }

    protected boolean createChest(ServerLevelAccessor serverLevelAccessor, BoundingBox boundingBox, Random random, BlockPos blockPos, ResourceLocation resourceLocation, @Nullable BlockState blockState) {
        if (!boundingBox.isInside(blockPos) || serverLevelAccessor.getBlockState(blockPos).is(Blocks.CHEST)) {
            return false;
        }
        if (blockState == null) {
            blockState = StructurePiece.reorient(serverLevelAccessor, blockPos, Blocks.CHEST.defaultBlockState());
        }
        serverLevelAccessor.setBlock(blockPos, blockState, 2);
        BlockEntity blockEntity = serverLevelAccessor.getBlockEntity(blockPos);
        if (blockEntity instanceof ChestBlockEntity) {
            ((ChestBlockEntity)blockEntity).setLootTable(resourceLocation, random.nextLong());
        }
        return true;
    }

    protected boolean createDispenser(WorldGenLevel worldGenLevel, BoundingBox boundingBox, Random random, int n, int n2, int n3, Direction direction, ResourceLocation resourceLocation) {
        BlockPos blockPos = new BlockPos(this.getWorldX(n, n3), this.getWorldY(n2), this.getWorldZ(n, n3));
        if (boundingBox.isInside(blockPos) && !worldGenLevel.getBlockState(blockPos).is(Blocks.DISPENSER)) {
            this.placeBlock(worldGenLevel, (BlockState)Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, direction), n, n2, n3, boundingBox);
            BlockEntity blockEntity = worldGenLevel.getBlockEntity(blockPos);
            if (blockEntity instanceof DispenserBlockEntity) {
                ((DispenserBlockEntity)blockEntity).setLootTable(resourceLocation, random.nextLong());
            }
            return true;
        }
        return false;
    }

    public void move(int n, int n2, int n3) {
        this.boundingBox.move(n, n2, n3);
    }

    @Nullable
    public Direction getOrientation() {
        return this.orientation;
    }

    public void setOrientation(@Nullable Direction direction) {
        this.orientation = direction;
        if (direction == null) {
            this.rotation = Rotation.NONE;
            this.mirror = Mirror.NONE;
        } else {
            switch (direction) {
                case SOUTH: {
                    this.mirror = Mirror.LEFT_RIGHT;
                    this.rotation = Rotation.NONE;
                    break;
                }
                case WEST: {
                    this.mirror = Mirror.LEFT_RIGHT;
                    this.rotation = Rotation.CLOCKWISE_90;
                    break;
                }
                case EAST: {
                    this.mirror = Mirror.NONE;
                    this.rotation = Rotation.CLOCKWISE_90;
                    break;
                }
                default: {
                    this.mirror = Mirror.NONE;
                    this.rotation = Rotation.NONE;
                }
            }
        }
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public StructurePieceType getType() {
        return this.type;
    }

    public static abstract class BlockSelector {
        protected BlockState next = Blocks.AIR.defaultBlockState();

        protected BlockSelector() {
        }

        public abstract void next(Random var1, int var2, int var3, int var4, boolean var5);

        public BlockState getNext() {
            return this.next;
        }
    }

}

