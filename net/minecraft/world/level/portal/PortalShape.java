/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.portal;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;

public class PortalShape {
    private static final BlockBehaviour.StatePredicate FRAME = (blockState, blockGetter, blockPos) -> blockState.is(Blocks.OBSIDIAN);
    private final LevelAccessor level;
    private final Direction.Axis axis;
    private final Direction rightDir;
    private int numPortalBlocks;
    @Nullable
    private BlockPos bottomLeft;
    private int height;
    private int width;

    public static Optional<PortalShape> findEmptyPortalShape(LevelAccessor levelAccessor, BlockPos blockPos, Direction.Axis axis) {
        return PortalShape.findPortalShape(levelAccessor, blockPos, portalShape -> portalShape.isValid() && portalShape.numPortalBlocks == 0, axis);
    }

    public static Optional<PortalShape> findPortalShape(LevelAccessor levelAccessor, BlockPos blockPos, Predicate<PortalShape> predicate, Direction.Axis axis) {
        Optional<PortalShape> optional = Optional.of(new PortalShape(levelAccessor, blockPos, axis)).filter(predicate);
        if (optional.isPresent()) {
            return optional;
        }
        Direction.Axis axis2 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        return Optional.of(new PortalShape(levelAccessor, blockPos, axis2)).filter(predicate);
    }

    public PortalShape(LevelAccessor levelAccessor, BlockPos blockPos, Direction.Axis axis) {
        this.level = levelAccessor;
        this.axis = axis;
        this.rightDir = axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.bottomLeft = this.calculateBottomLeft(blockPos);
        if (this.bottomLeft == null) {
            this.bottomLeft = blockPos;
            this.width = 1;
            this.height = 1;
        } else {
            this.width = this.calculateWidth();
            if (this.width > 0) {
                this.height = this.calculateHeight();
            }
        }
    }

    @Nullable
    private BlockPos calculateBottomLeft(BlockPos blockPos) {
        int n = Math.max(0, blockPos.getY() - 21);
        while (blockPos.getY() > n && PortalShape.isEmpty(this.level.getBlockState(blockPos.below()))) {
            blockPos = blockPos.below();
        }
        Direction direction = this.rightDir.getOpposite();
        int n2 = this.getDistanceUntilEdgeAboveFrame(blockPos, direction) - 1;
        if (n2 < 0) {
            return null;
        }
        return blockPos.relative(direction, n2);
    }

    private int calculateWidth() {
        int n = this.getDistanceUntilEdgeAboveFrame(this.bottomLeft, this.rightDir);
        if (n < 2 || n > 21) {
            return 0;
        }
        return n;
    }

    private int getDistanceUntilEdgeAboveFrame(BlockPos blockPos, Direction direction) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i <= 21; ++i) {
            mutableBlockPos.set(blockPos).move(direction, i);
            BlockState blockState = this.level.getBlockState(mutableBlockPos);
            if (!PortalShape.isEmpty(blockState)) {
                if (!FRAME.test(blockState, this.level, mutableBlockPos)) break;
                return i;
            }
            BlockState blockState2 = this.level.getBlockState(mutableBlockPos.move(Direction.DOWN));
            if (!FRAME.test(blockState2, this.level, mutableBlockPos)) break;
        }
        return 0;
    }

    private int calculateHeight() {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int n = this.getDistanceUntilTop(mutableBlockPos);
        if (n < 3 || n > 21 || !this.hasTopFrame(mutableBlockPos, n)) {
            return 0;
        }
        return n;
    }

    private boolean hasTopFrame(BlockPos.MutableBlockPos mutableBlockPos, int n) {
        for (int i = 0; i < this.width; ++i) {
            BlockPos.MutableBlockPos mutableBlockPos2 = mutableBlockPos.set(this.bottomLeft).move(Direction.UP, n).move(this.rightDir, i);
            if (FRAME.test(this.level.getBlockState(mutableBlockPos2), this.level, mutableBlockPos2)) continue;
            return false;
        }
        return true;
    }

    private int getDistanceUntilTop(BlockPos.MutableBlockPos mutableBlockPos) {
        for (int i = 0; i < 21; ++i) {
            mutableBlockPos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
            if (!FRAME.test(this.level.getBlockState(mutableBlockPos), this.level, mutableBlockPos)) {
                return i;
            }
            mutableBlockPos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
            if (!FRAME.test(this.level.getBlockState(mutableBlockPos), this.level, mutableBlockPos)) {
                return i;
            }
            for (int j = 0; j < this.width; ++j) {
                mutableBlockPos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
                BlockState blockState = this.level.getBlockState(mutableBlockPos);
                if (!PortalShape.isEmpty(blockState)) {
                    return i;
                }
                if (!blockState.is(Blocks.NETHER_PORTAL)) continue;
                ++this.numPortalBlocks;
            }
        }
        return 21;
    }

    private static boolean isEmpty(BlockState blockState) {
        return blockState.isAir() || blockState.is(BlockTags.FIRE) || blockState.is(Blocks.NETHER_PORTAL);
    }

    public boolean isValid() {
        return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    public void createPortalBlocks() {
        BlockState blockState = (BlockState)Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, this.axis);
        BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach(blockPos -> this.level.setBlock((BlockPos)blockPos, blockState, 18));
    }

    public boolean isComplete() {
        return this.isValid() && this.numPortalBlocks == this.width * this.height;
    }

    public static Vec3 getRelativePosition(BlockUtil.FoundRectangle foundRectangle, Direction.Axis axis, Vec3 vec3, EntityDimensions entityDimensions) {
        double d;
        Direction.Axis axis2;
        double d2;
        double d3 = (double)foundRectangle.axis1Size - (double)entityDimensions.width;
        double d4 = (double)foundRectangle.axis2Size - (double)entityDimensions.height;
        BlockPos blockPos = foundRectangle.minCorner;
        if (d3 > 0.0) {
            float f = (float)blockPos.get(axis) + entityDimensions.width / 2.0f;
            d2 = Mth.clamp(Mth.inverseLerp(vec3.get(axis) - (double)f, 0.0, d3), 0.0, 1.0);
        } else {
            d2 = 0.5;
        }
        if (d4 > 0.0) {
            axis2 = Direction.Axis.Y;
            d = Mth.clamp(Mth.inverseLerp(vec3.get(axis2) - (double)blockPos.get(axis2), 0.0, d4), 0.0, 1.0);
        } else {
            d = 0.0;
        }
        axis2 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        double d5 = vec3.get(axis2) - ((double)blockPos.get(axis2) + 0.5);
        return new Vec3(d2, d, d5);
    }

    public static PortalInfo createPortalInfo(ServerLevel serverLevel, BlockUtil.FoundRectangle foundRectangle, Direction.Axis axis, Vec3 vec3, EntityDimensions entityDimensions, Vec3 vec32, float f, float f2) {
        BlockPos blockPos = foundRectangle.minCorner;
        BlockState blockState = serverLevel.getBlockState(blockPos);
        Direction.Axis axis2 = blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        double d = foundRectangle.axis1Size;
        double d2 = foundRectangle.axis2Size;
        int n = axis == axis2 ? 0 : 90;
        Vec3 vec33 = axis == axis2 ? vec32 : new Vec3(vec32.z, vec32.y, -vec32.x);
        double d3 = (double)entityDimensions.width / 2.0 + (d - (double)entityDimensions.width) * vec3.x();
        double d4 = (d2 - (double)entityDimensions.height) * vec3.y();
        double d5 = 0.5 + vec3.z();
        boolean bl = axis2 == Direction.Axis.X;
        Vec3 vec34 = new Vec3((double)blockPos.getX() + (bl ? d3 : d5), (double)blockPos.getY() + d4, (double)blockPos.getZ() + (bl ? d5 : d3));
        return new PortalInfo(vec34, vec33, f + (float)n, f2);
    }
}

