/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.portal;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Material;

public class PortalForcer {
    private final ServerLevel level;

    public PortalForcer(ServerLevel serverLevel) {
        this.level = serverLevel;
    }

    public Optional<BlockUtil.FoundRectangle> findPortalAround(BlockPos blockPos, boolean bl) {
        PoiManager poiManager = this.level.getPoiManager();
        int n = bl ? 16 : 128;
        poiManager.ensureLoadedAndValid(this.level, blockPos, n);
        Optional<PoiRecord> optional = poiManager.getInSquare(poiType -> poiType == PoiType.NETHER_PORTAL, blockPos, n, PoiManager.Occupancy.ANY).sorted(Comparator.comparingDouble(poiRecord -> poiRecord.getPos().distSqr(blockPos)).thenComparingInt(poiRecord -> poiRecord.getPos().getY())).filter(poiRecord -> this.level.getBlockState(poiRecord.getPos()).hasProperty(BlockStateProperties.HORIZONTAL_AXIS)).findFirst();
        return optional.map(poiRecord -> {
            BlockPos blockPos2 = poiRecord.getPos();
            this.level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(blockPos2), 3, blockPos2);
            BlockState blockState = this.level.getBlockState(blockPos2);
            return BlockUtil.getLargestRectangleAround(blockPos2, blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, blockPos -> this.level.getBlockState((BlockPos)blockPos) == blockState);
        });
    }

    public Optional<BlockUtil.FoundRectangle> createPortal(BlockPos blockPos, Direction.Axis axis) {
        int n;
        reference var14_1422;
        int n2;
        Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
        double d = -1.0;
        BlockPos blockPos2 = null;
        double d2 = -1.0;
        BlockPos blockPos3 = null;
        WorldBorder worldBorder = this.level.getWorldBorder();
        int n3 = this.level.getHeight() - 1;
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
        for (reference var14_1422 : BlockPos.spiralAround(blockPos, 16, Direction.EAST, Direction.SOUTH)) {
            n = Math.min(n3, this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, var14_1422.getX(), var14_1422.getZ()));
            n2 = 1;
            if (!worldBorder.isWithinBounds(var14_1422) || !worldBorder.isWithinBounds(var14_1422.move(direction, 1))) continue;
            var14_1422.move(direction.getOpposite(), 1);
            for (int i = n; i >= 0; --i) {
                int n4;
                var14_1422.setY(i);
                if (!this.level.isEmptyBlock(var14_1422)) continue;
                int n5 = i;
                while (i > 0 && this.level.isEmptyBlock(var14_1422.move(Direction.DOWN))) {
                    --i;
                }
                if (i + 4 > n3 || (n4 = n5 - i) > 0 && n4 < 3) continue;
                var14_1422.setY(i);
                if (!this.canHostFrame(var14_1422, mutableBlockPos, direction, 0)) continue;
                double d3 = blockPos.distSqr(var14_1422);
                if (this.canHostFrame(var14_1422, mutableBlockPos, direction, -1) && this.canHostFrame(var14_1422, mutableBlockPos, direction, 1) && (d == -1.0 || d > d3)) {
                    d = d3;
                    blockPos2 = var14_1422.immutable();
                }
                if (d != -1.0 || d2 != -1.0 && !(d2 > d3)) continue;
                d2 = d3;
                blockPos3 = var14_1422.immutable();
            }
        }
        if (d == -1.0 && d2 != -1.0) {
            blockPos2 = blockPos3;
            d = d2;
        }
        if (d == -1.0) {
            blockPos2 = new BlockPos(blockPos.getX(), Mth.clamp(blockPos.getY(), 70, this.level.getHeight() - 10), blockPos.getZ()).immutable();
            Direction direction2 = direction.getClockWise();
            if (!worldBorder.isWithinBounds(blockPos2)) {
                return Optional.empty();
            }
            for (int i = -1; i < 2; ++i) {
                for (n = 0; n < 2; ++n) {
                    for (n2 = -1; n2 < 3; ++n2) {
                        BlockState blockState = n2 < 0 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
                        mutableBlockPos.setWithOffset(blockPos2, n * direction.getStepX() + i * direction2.getStepX(), n2, n * direction.getStepZ() + i * direction2.getStepZ());
                        this.level.setBlockAndUpdate(mutableBlockPos, blockState);
                    }
                }
            }
        }
        for (int i = -1; i < 3; ++i) {
            for (var14_1422 = (reference)-1; var14_1422 < 4; ++var14_1422) {
                if (i != -1 && i != 2 && var14_1422 != -1 && var14_1422 != 3) continue;
                mutableBlockPos.setWithOffset(blockPos2, i * direction.getStepX(), (int)var14_1422, i * direction.getStepZ());
                this.level.setBlock(mutableBlockPos, Blocks.OBSIDIAN.defaultBlockState(), 3);
            }
        }
        BlockState blockState = (BlockState)Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, axis);
        for (var14_1422 = (reference)false; var14_1422 < 2; ++var14_1422) {
            for (n = 0; n < 3; ++n) {
                mutableBlockPos.setWithOffset(blockPos2, (int)(var14_1422 * direction.getStepX()), n, (int)(var14_1422 * direction.getStepZ()));
                this.level.setBlock(mutableBlockPos, blockState, 18);
            }
        }
        return Optional.of(new BlockUtil.FoundRectangle(blockPos2.immutable(), 2, 3));
    }

    private boolean canHostFrame(BlockPos blockPos, BlockPos.MutableBlockPos mutableBlockPos, Direction direction, int n) {
        Direction direction2 = direction.getClockWise();
        for (int i = -1; i < 3; ++i) {
            for (int j = -1; j < 4; ++j) {
                mutableBlockPos.setWithOffset(blockPos, direction.getStepX() * i + direction2.getStepX() * n, j, direction.getStepZ() * i + direction2.getStepZ() * n);
                if (j < 0 && !this.level.getBlockState(mutableBlockPos).getMaterial().isSolid()) {
                    return false;
                }
                if (j < 0 || this.level.isEmptyBlock(mutableBlockPos)) continue;
                return false;
            }
        }
        return true;
    }
}

