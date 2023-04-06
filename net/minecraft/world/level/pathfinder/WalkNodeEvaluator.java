/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.function.LongFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WalkNodeEvaluator
extends NodeEvaluator {
    protected float oldWaterCost;
    private final Long2ObjectMap<BlockPathTypes> pathTypesByPosCache = new Long2ObjectOpenHashMap();
    private final Object2BooleanMap<AABB> collisionCache = new Object2BooleanOpenHashMap();

    @Override
    public void prepare(PathNavigationRegion pathNavigationRegion, Mob mob) {
        super.prepare(pathNavigationRegion, mob);
        this.oldWaterCost = mob.getPathfindingMalus(BlockPathTypes.WATER);
    }

    @Override
    public void done() {
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        this.pathTypesByPosCache.clear();
        this.collisionCache.clear();
        super.done();
    }

    @Override
    public Node getStart() {
        BlockPos blockPos;
        Object object;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int n = Mth.floor(this.mob.getY());
        BlockState blockState = this.level.getBlockState(mutableBlockPos.set(this.mob.getX(), (double)n, this.mob.getZ()));
        if (this.mob.canStandOnFluid(blockState.getFluidState().getType())) {
            while (this.mob.canStandOnFluid(blockState.getFluidState().getType())) {
                blockState = this.level.getBlockState(mutableBlockPos.set(this.mob.getX(), (double)(++n), this.mob.getZ()));
            }
            --n;
        } else if (this.canFloat() && this.mob.isInWater()) {
            while (blockState.getBlock() == Blocks.WATER || blockState.getFluidState() == Fluids.WATER.getSource(false)) {
                blockState = this.level.getBlockState(mutableBlockPos.set(this.mob.getX(), (double)(++n), this.mob.getZ()));
            }
            --n;
        } else if (this.mob.isOnGround()) {
            n = Mth.floor(this.mob.getY() + 0.5);
        } else {
            blockPos = this.mob.blockPosition();
            while ((this.level.getBlockState(blockPos).isAir() || this.level.getBlockState(blockPos).isPathfindable(this.level, blockPos, PathComputationType.LAND)) && blockPos.getY() > 0) {
                blockPos = blockPos.below();
            }
            n = blockPos.above().getY();
        }
        blockPos = this.mob.blockPosition();
        BlockPathTypes blockPathTypes = this.getCachedBlockType(this.mob, blockPos.getX(), n, blockPos.getZ());
        if (this.mob.getPathfindingMalus(blockPathTypes) < 0.0f) {
            object = this.mob.getBoundingBox();
            if (this.hasPositiveMalus(mutableBlockPos.set(((AABB)object).minX, (double)n, ((AABB)object).minZ)) || this.hasPositiveMalus(mutableBlockPos.set(((AABB)object).minX, (double)n, ((AABB)object).maxZ)) || this.hasPositiveMalus(mutableBlockPos.set(((AABB)object).maxX, (double)n, ((AABB)object).minZ)) || this.hasPositiveMalus(mutableBlockPos.set(((AABB)object).maxX, (double)n, ((AABB)object).maxZ))) {
                Node node = this.getNode(mutableBlockPos);
                node.type = this.getBlockPathType(this.mob, node.asBlockPos());
                node.costMalus = this.mob.getPathfindingMalus(node.type);
                return node;
            }
        }
        object = this.getNode(blockPos.getX(), n, blockPos.getZ());
        ((Node)object).type = this.getBlockPathType(this.mob, ((Node)object).asBlockPos());
        ((Node)object).costMalus = this.mob.getPathfindingMalus(((Node)object).type);
        return object;
    }

    private boolean hasPositiveMalus(BlockPos blockPos) {
        BlockPathTypes blockPathTypes = this.getBlockPathType(this.mob, blockPos);
        return this.mob.getPathfindingMalus(blockPathTypes) >= 0.0f;
    }

    @Override
    public Target getGoal(double d, double d2, double d3) {
        return new Target(this.getNode(Mth.floor(d), Mth.floor(d2), Mth.floor(d3)));
    }

    @Override
    public int getNeighbors(Node[] arrnode, Node node) {
        Node node2;
        double d;
        Node node3;
        Node node4;
        Node node5;
        Node node6;
        Node node7;
        Node node8;
        Node node9;
        int n = 0;
        int n2 = 0;
        BlockPathTypes blockPathTypes = this.getCachedBlockType(this.mob, node.x, node.y + 1, node.z);
        BlockPathTypes blockPathTypes2 = this.getCachedBlockType(this.mob, node.x, node.y, node.z);
        if (this.mob.getPathfindingMalus(blockPathTypes) >= 0.0f && blockPathTypes2 != BlockPathTypes.STICKY_HONEY) {
            n2 = Mth.floor(Math.max(1.0f, this.mob.maxUpStep));
        }
        if (this.isNeighborValid(node7 = this.getLandNode(node.x, node.y, node.z + 1, n2, d = WalkNodeEvaluator.getFloorLevel(this.level, new BlockPos(node.x, node.y, node.z)), Direction.SOUTH, blockPathTypes2), node)) {
            arrnode[n++] = node7;
        }
        if (this.isNeighborValid(node4 = this.getLandNode(node.x - 1, node.y, node.z, n2, d, Direction.WEST, blockPathTypes2), node)) {
            arrnode[n++] = node4;
        }
        if (this.isNeighborValid(node3 = this.getLandNode(node.x + 1, node.y, node.z, n2, d, Direction.EAST, blockPathTypes2), node)) {
            arrnode[n++] = node3;
        }
        if (this.isNeighborValid(node9 = this.getLandNode(node.x, node.y, node.z - 1, n2, d, Direction.NORTH, blockPathTypes2), node)) {
            arrnode[n++] = node9;
        }
        if (this.isDiagonalValid(node, node4, node9, node8 = this.getLandNode(node.x - 1, node.y, node.z - 1, n2, d, Direction.NORTH, blockPathTypes2))) {
            arrnode[n++] = node8;
        }
        if (this.isDiagonalValid(node, node3, node9, node6 = this.getLandNode(node.x + 1, node.y, node.z - 1, n2, d, Direction.NORTH, blockPathTypes2))) {
            arrnode[n++] = node6;
        }
        if (this.isDiagonalValid(node, node4, node7, node5 = this.getLandNode(node.x - 1, node.y, node.z + 1, n2, d, Direction.SOUTH, blockPathTypes2))) {
            arrnode[n++] = node5;
        }
        if (this.isDiagonalValid(node, node3, node7, node2 = this.getLandNode(node.x + 1, node.y, node.z + 1, n2, d, Direction.SOUTH, blockPathTypes2))) {
            arrnode[n++] = node2;
        }
        return n;
    }

    private boolean isNeighborValid(Node node, Node node2) {
        return node != null && !node.closed && (node.costMalus >= 0.0f || node2.costMalus < 0.0f);
    }

    private boolean isDiagonalValid(Node node, @Nullable Node node2, @Nullable Node node3, @Nullable Node node4) {
        if (node4 == null || node3 == null || node2 == null) {
            return false;
        }
        if (node4.closed) {
            return false;
        }
        if (node3.y > node.y || node2.y > node.y) {
            return false;
        }
        if (node2.type == BlockPathTypes.WALKABLE_DOOR || node3.type == BlockPathTypes.WALKABLE_DOOR || node4.type == BlockPathTypes.WALKABLE_DOOR) {
            return false;
        }
        boolean bl = node3.type == BlockPathTypes.FENCE && node2.type == BlockPathTypes.FENCE && (double)this.mob.getBbWidth() < 0.5;
        return node4.costMalus >= 0.0f && (node3.y < node.y || node3.costMalus >= 0.0f || bl) && (node2.y < node.y || node2.costMalus >= 0.0f || bl);
    }

    private boolean canReachWithoutCollision(Node node) {
        Vec3 vec3 = new Vec3((double)node.x - this.mob.getX(), (double)node.y - this.mob.getY(), (double)node.z - this.mob.getZ());
        AABB aABB = this.mob.getBoundingBox();
        int n = Mth.ceil(vec3.length() / aABB.getSize());
        vec3 = vec3.scale(1.0f / (float)n);
        for (int i = 1; i <= n; ++i) {
            if (!this.hasCollisions(aABB = aABB.move(vec3))) continue;
            return false;
        }
        return true;
    }

    public static double getFloorLevel(BlockGetter blockGetter, BlockPos blockPos) {
        BlockPos blockPos2;
        VoxelShape voxelShape;
        return (double)blockPos2.getY() + ((voxelShape = blockGetter.getBlockState(blockPos2 = blockPos.below()).getCollisionShape(blockGetter, blockPos2)).isEmpty() ? 0.0 : voxelShape.max(Direction.Axis.Y));
    }

    @Nullable
    private Node getLandNode(int n, int n2, int n3, int n4, double d, Direction direction, BlockPathTypes blockPathTypes) {
        double d2;
        AABB aABB;
        double d3;
        Node node = null;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        double d4 = WalkNodeEvaluator.getFloorLevel(this.level, mutableBlockPos.set(n, n2, n3));
        if (d4 - d > 1.125) {
            return null;
        }
        BlockPathTypes blockPathTypes2 = this.getCachedBlockType(this.mob, n, n2, n3);
        float f = this.mob.getPathfindingMalus(blockPathTypes2);
        double d5 = (double)this.mob.getBbWidth() / 2.0;
        if (f >= 0.0f) {
            node = this.getNode(n, n2, n3);
            node.type = blockPathTypes2;
            node.costMalus = Math.max(node.costMalus, f);
        }
        if (blockPathTypes == BlockPathTypes.FENCE && node != null && node.costMalus >= 0.0f && !this.canReachWithoutCollision(node)) {
            node = null;
        }
        if (blockPathTypes2 == BlockPathTypes.WALKABLE) {
            return node;
        }
        if ((node == null || node.costMalus < 0.0f) && n4 > 0 && blockPathTypes2 != BlockPathTypes.FENCE && blockPathTypes2 != BlockPathTypes.UNPASSABLE_RAIL && blockPathTypes2 != BlockPathTypes.TRAPDOOR && (node = this.getLandNode(n, n2 + 1, n3, n4 - 1, d, direction, blockPathTypes)) != null && (node.type == BlockPathTypes.OPEN || node.type == BlockPathTypes.WALKABLE) && this.mob.getBbWidth() < 1.0f && this.hasCollisions(aABB = new AABB((d3 = (double)(n - direction.getStepX()) + 0.5) - d5, WalkNodeEvaluator.getFloorLevel(this.level, mutableBlockPos.set(d3, (double)(n2 + 1), d2 = (double)(n3 - direction.getStepZ()) + 0.5)) + 0.001, d2 - d5, d3 + d5, (double)this.mob.getBbHeight() + WalkNodeEvaluator.getFloorLevel(this.level, mutableBlockPos.set((double)node.x, (double)node.y, (double)node.z)) - 0.002, d2 + d5))) {
            node = null;
        }
        if (blockPathTypes2 == BlockPathTypes.WATER && !this.canFloat()) {
            if (this.getCachedBlockType(this.mob, n, n2 - 1, n3) != BlockPathTypes.WATER) {
                return node;
            }
            while (n2 > 0) {
                if ((blockPathTypes2 = this.getCachedBlockType(this.mob, n, --n2, n3)) == BlockPathTypes.WATER) {
                    node = this.getNode(n, n2, n3);
                    node.type = blockPathTypes2;
                    node.costMalus = Math.max(node.costMalus, this.mob.getPathfindingMalus(blockPathTypes2));
                    continue;
                }
                return node;
            }
        }
        if (blockPathTypes2 == BlockPathTypes.OPEN) {
            int n5 = 0;
            int n6 = n2;
            while (blockPathTypes2 == BlockPathTypes.OPEN) {
                if (--n2 < 0) {
                    Node node2 = this.getNode(n, n6, n3);
                    node2.type = BlockPathTypes.BLOCKED;
                    node2.costMalus = -1.0f;
                    return node2;
                }
                if (n5++ >= this.mob.getMaxFallDistance()) {
                    Node node3 = this.getNode(n, n2, n3);
                    node3.type = BlockPathTypes.BLOCKED;
                    node3.costMalus = -1.0f;
                    return node3;
                }
                blockPathTypes2 = this.getCachedBlockType(this.mob, n, n2, n3);
                f = this.mob.getPathfindingMalus(blockPathTypes2);
                if (blockPathTypes2 != BlockPathTypes.OPEN && f >= 0.0f) {
                    node = this.getNode(n, n2, n3);
                    node.type = blockPathTypes2;
                    node.costMalus = Math.max(node.costMalus, f);
                    break;
                }
                if (!(f < 0.0f)) continue;
                Node node4 = this.getNode(n, n2, n3);
                node4.type = BlockPathTypes.BLOCKED;
                node4.costMalus = -1.0f;
                return node4;
            }
        }
        if (blockPathTypes2 == BlockPathTypes.FENCE) {
            node = this.getNode(n, n2, n3);
            node.closed = true;
            node.type = blockPathTypes2;
            node.costMalus = blockPathTypes2.getMalus();
        }
        return node;
    }

    private boolean hasCollisions(AABB aABB) {
        return (Boolean)this.collisionCache.computeIfAbsent((Object)aABB, aABB2 -> !this.level.noCollision(this.mob, aABB));
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int n, int n2, int n3, Mob mob, int n4, int n5, int n6, boolean bl, boolean bl2) {
        EnumSet<BlockPathTypes> enumSet = EnumSet.noneOf(BlockPathTypes.class);
        BlockPathTypes blockPathTypes = BlockPathTypes.BLOCKED;
        BlockPos blockPos = mob.blockPosition();
        blockPathTypes = this.getBlockPathTypes(blockGetter, n, n2, n3, n4, n5, n6, bl, bl2, enumSet, blockPathTypes, blockPos);
        if (enumSet.contains((Object)BlockPathTypes.FENCE)) {
            return BlockPathTypes.FENCE;
        }
        if (enumSet.contains((Object)BlockPathTypes.UNPASSABLE_RAIL)) {
            return BlockPathTypes.UNPASSABLE_RAIL;
        }
        BlockPathTypes blockPathTypes2 = BlockPathTypes.BLOCKED;
        for (BlockPathTypes blockPathTypes3 : enumSet) {
            if (mob.getPathfindingMalus(blockPathTypes3) < 0.0f) {
                return blockPathTypes3;
            }
            if (!(mob.getPathfindingMalus(blockPathTypes3) >= mob.getPathfindingMalus(blockPathTypes2))) continue;
            blockPathTypes2 = blockPathTypes3;
        }
        if (blockPathTypes == BlockPathTypes.OPEN && mob.getPathfindingMalus(blockPathTypes2) == 0.0f && n4 <= 1) {
            return BlockPathTypes.OPEN;
        }
        return blockPathTypes2;
    }

    public BlockPathTypes getBlockPathTypes(BlockGetter blockGetter, int n, int n2, int n3, int n4, int n5, int n6, boolean bl, boolean bl2, EnumSet<BlockPathTypes> enumSet, BlockPathTypes blockPathTypes, BlockPos blockPos) {
        for (int i = 0; i < n4; ++i) {
            for (int j = 0; j < n5; ++j) {
                for (int k = 0; k < n6; ++k) {
                    int n7 = i + n;
                    int n8 = j + n2;
                    int n9 = k + n3;
                    BlockPathTypes blockPathTypes2 = this.getBlockPathType(blockGetter, n7, n8, n9);
                    blockPathTypes2 = this.evaluateBlockPathType(blockGetter, bl, bl2, blockPos, blockPathTypes2);
                    if (i == 0 && j == 0 && k == 0) {
                        blockPathTypes = blockPathTypes2;
                    }
                    enumSet.add(blockPathTypes2);
                }
            }
        }
        return blockPathTypes;
    }

    protected BlockPathTypes evaluateBlockPathType(BlockGetter blockGetter, boolean bl, boolean bl2, BlockPos blockPos, BlockPathTypes blockPathTypes) {
        if (blockPathTypes == BlockPathTypes.DOOR_WOOD_CLOSED && bl && bl2) {
            blockPathTypes = BlockPathTypes.WALKABLE_DOOR;
        }
        if (blockPathTypes == BlockPathTypes.DOOR_OPEN && !bl2) {
            blockPathTypes = BlockPathTypes.BLOCKED;
        }
        if (blockPathTypes == BlockPathTypes.RAIL && !(blockGetter.getBlockState(blockPos).getBlock() instanceof BaseRailBlock) && !(blockGetter.getBlockState(blockPos.below()).getBlock() instanceof BaseRailBlock)) {
            blockPathTypes = BlockPathTypes.UNPASSABLE_RAIL;
        }
        if (blockPathTypes == BlockPathTypes.LEAVES) {
            blockPathTypes = BlockPathTypes.BLOCKED;
        }
        return blockPathTypes;
    }

    private BlockPathTypes getBlockPathType(Mob mob, BlockPos blockPos) {
        return this.getCachedBlockType(mob, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    private BlockPathTypes getCachedBlockType(Mob mob, int n, int n2, int n3) {
        return (BlockPathTypes)((Object)this.pathTypesByPosCache.computeIfAbsent(BlockPos.asLong(n, n2, n3), l -> this.getBlockPathType(this.level, n, n2, n3, mob, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors())));
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int n, int n2, int n3) {
        return WalkNodeEvaluator.getBlockPathTypeStatic(blockGetter, new BlockPos.MutableBlockPos(n, n2, n3));
    }

    public static BlockPathTypes getBlockPathTypeStatic(BlockGetter blockGetter, BlockPos.MutableBlockPos mutableBlockPos) {
        int n = mutableBlockPos.getX();
        int n2 = mutableBlockPos.getY();
        int n3 = mutableBlockPos.getZ();
        BlockPathTypes blockPathTypes = WalkNodeEvaluator.getBlockPathTypeRaw(blockGetter, mutableBlockPos);
        if (blockPathTypes == BlockPathTypes.OPEN && n2 >= 1) {
            BlockPathTypes blockPathTypes2 = WalkNodeEvaluator.getBlockPathTypeRaw(blockGetter, mutableBlockPos.set(n, n2 - 1, n3));
            BlockPathTypes blockPathTypes3 = blockPathTypes = blockPathTypes2 == BlockPathTypes.WALKABLE || blockPathTypes2 == BlockPathTypes.OPEN || blockPathTypes2 == BlockPathTypes.WATER || blockPathTypes2 == BlockPathTypes.LAVA ? BlockPathTypes.OPEN : BlockPathTypes.WALKABLE;
            if (blockPathTypes2 == BlockPathTypes.DAMAGE_FIRE) {
                blockPathTypes = BlockPathTypes.DAMAGE_FIRE;
            }
            if (blockPathTypes2 == BlockPathTypes.DAMAGE_CACTUS) {
                blockPathTypes = BlockPathTypes.DAMAGE_CACTUS;
            }
            if (blockPathTypes2 == BlockPathTypes.DAMAGE_OTHER) {
                blockPathTypes = BlockPathTypes.DAMAGE_OTHER;
            }
            if (blockPathTypes2 == BlockPathTypes.STICKY_HONEY) {
                blockPathTypes = BlockPathTypes.STICKY_HONEY;
            }
        }
        if (blockPathTypes == BlockPathTypes.WALKABLE) {
            blockPathTypes = WalkNodeEvaluator.checkNeighbourBlocks(blockGetter, mutableBlockPos.set(n, n2, n3), blockPathTypes);
        }
        return blockPathTypes;
    }

    public static BlockPathTypes checkNeighbourBlocks(BlockGetter blockGetter, BlockPos.MutableBlockPos mutableBlockPos, BlockPathTypes blockPathTypes) {
        int n = mutableBlockPos.getX();
        int n2 = mutableBlockPos.getY();
        int n3 = mutableBlockPos.getZ();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    if (i == 0 && k == 0) continue;
                    mutableBlockPos.set(n + i, n2 + j, n3 + k);
                    BlockState blockState = blockGetter.getBlockState(mutableBlockPos);
                    if (blockState.is(Blocks.CACTUS)) {
                        return BlockPathTypes.DANGER_CACTUS;
                    }
                    if (blockState.is(Blocks.SWEET_BERRY_BUSH)) {
                        return BlockPathTypes.DANGER_OTHER;
                    }
                    if (WalkNodeEvaluator.isBurningBlock(blockState)) {
                        return BlockPathTypes.DANGER_FIRE;
                    }
                    if (!blockGetter.getFluidState(mutableBlockPos).is(FluidTags.WATER)) continue;
                    return BlockPathTypes.WATER_BORDER;
                }
            }
        }
        return blockPathTypes;
    }

    protected static BlockPathTypes getBlockPathTypeRaw(BlockGetter blockGetter, BlockPos blockPos) {
        BlockState blockState = blockGetter.getBlockState(blockPos);
        Block block = blockState.getBlock();
        Material material = blockState.getMaterial();
        if (blockState.isAir()) {
            return BlockPathTypes.OPEN;
        }
        if (blockState.is(BlockTags.TRAPDOORS) || blockState.is(Blocks.LILY_PAD)) {
            return BlockPathTypes.TRAPDOOR;
        }
        if (blockState.is(Blocks.CACTUS)) {
            return BlockPathTypes.DAMAGE_CACTUS;
        }
        if (blockState.is(Blocks.SWEET_BERRY_BUSH)) {
            return BlockPathTypes.DAMAGE_OTHER;
        }
        if (blockState.is(Blocks.HONEY_BLOCK)) {
            return BlockPathTypes.STICKY_HONEY;
        }
        if (blockState.is(Blocks.COCOA)) {
            return BlockPathTypes.COCOA;
        }
        FluidState fluidState = blockGetter.getFluidState(blockPos);
        if (fluidState.is(FluidTags.WATER)) {
            return BlockPathTypes.WATER;
        }
        if (fluidState.is(FluidTags.LAVA)) {
            return BlockPathTypes.LAVA;
        }
        if (WalkNodeEvaluator.isBurningBlock(blockState)) {
            return BlockPathTypes.DAMAGE_FIRE;
        }
        if (DoorBlock.isWoodenDoor(blockState) && !blockState.getValue(DoorBlock.OPEN).booleanValue()) {
            return BlockPathTypes.DOOR_WOOD_CLOSED;
        }
        if (block instanceof DoorBlock && material == Material.METAL && !blockState.getValue(DoorBlock.OPEN).booleanValue()) {
            return BlockPathTypes.DOOR_IRON_CLOSED;
        }
        if (block instanceof DoorBlock && blockState.getValue(DoorBlock.OPEN).booleanValue()) {
            return BlockPathTypes.DOOR_OPEN;
        }
        if (block instanceof BaseRailBlock) {
            return BlockPathTypes.RAIL;
        }
        if (block instanceof LeavesBlock) {
            return BlockPathTypes.LEAVES;
        }
        if (block.is(BlockTags.FENCES) || block.is(BlockTags.WALLS) || block instanceof FenceGateBlock && !blockState.getValue(FenceGateBlock.OPEN).booleanValue()) {
            return BlockPathTypes.FENCE;
        }
        if (!blockState.isPathfindable(blockGetter, blockPos, PathComputationType.LAND)) {
            return BlockPathTypes.BLOCKED;
        }
        return BlockPathTypes.OPEN;
    }

    private static boolean isBurningBlock(BlockState blockState) {
        return blockState.is(BlockTags.FIRE) || blockState.is(Blocks.LAVA) || blockState.is(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(blockState);
    }
}

