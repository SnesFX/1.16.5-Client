/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.shorts.Short2BooleanMap
 *  it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
 */
package net.minecraft.world.level.material;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class FlowingFluid
extends Fluid {
    public static final BooleanProperty FALLING = BlockStateProperties.FALLING;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_FLOWING;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>(200){

            protected void rehash(int n) {
            }
        };
        object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
        return object2ByteLinkedOpenHashMap;
    });
    private final Map<FluidState, VoxelShape> shapes = Maps.newIdentityHashMap();

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
        builder.add(FALLING);
    }

    @Override
    public Vec3 getFlow(BlockGetter blockGetter, BlockPos blockPos, FluidState fluidState) {
        double d = 0.0;
        double d2 = 0.0;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (Object object : Direction.Plane.HORIZONTAL) {
            mutableBlockPos.setWithOffset(blockPos, (Direction)object);
            Object object2 = blockGetter.getFluidState(mutableBlockPos);
            if (!this.affectsFlow((FluidState)object2)) continue;
            float f = ((FluidState)object2).getOwnHeight();
            float f2 = 0.0f;
            if (f == 0.0f) {
                FluidState fluidState2;
                Vec3i vec3i;
                if (!blockGetter.getBlockState(mutableBlockPos).getMaterial().blocksMotion() && this.affectsFlow(fluidState2 = blockGetter.getFluidState((BlockPos)(vec3i = mutableBlockPos.below()))) && (f = fluidState2.getOwnHeight()) > 0.0f) {
                    f2 = fluidState.getOwnHeight() - (f - 0.8888889f);
                }
            } else if (f > 0.0f) {
                f2 = fluidState.getOwnHeight() - f;
            }
            if (f2 == 0.0f) continue;
            d += (double)((float)((Direction)object).getStepX() * f2);
            d2 += (double)((float)((Direction)object).getStepZ() * f2);
        }
        Object object = new Vec3(d, 0.0, d2);
        if (fluidState.getValue(FALLING).booleanValue()) {
            for (Object object2 : Direction.Plane.HORIZONTAL) {
                mutableBlockPos.setWithOffset(blockPos, (Direction)object2);
                if (!this.isSolidFace(blockGetter, mutableBlockPos, (Direction)object2) && !this.isSolidFace(blockGetter, (BlockPos)mutableBlockPos.above(), (Direction)object2)) continue;
                object = ((Vec3)object).normalize().add(0.0, -6.0, 0.0);
                break;
            }
        }
        return ((Vec3)object).normalize();
    }

    private boolean affectsFlow(FluidState fluidState) {
        return fluidState.isEmpty() || fluidState.getType().isSame(this);
    }

    protected boolean isSolidFace(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        BlockState blockState = blockGetter.getBlockState(blockPos);
        FluidState fluidState = blockGetter.getFluidState(blockPos);
        if (fluidState.getType().isSame(this)) {
            return false;
        }
        if (direction == Direction.UP) {
            return true;
        }
        if (blockState.getMaterial() == Material.ICE) {
            return false;
        }
        return blockState.isFaceSturdy(blockGetter, blockPos, direction);
    }

    protected void spread(LevelAccessor levelAccessor, BlockPos blockPos, FluidState fluidState) {
        if (fluidState.isEmpty()) {
            return;
        }
        BlockState blockState = levelAccessor.getBlockState(blockPos);
        BlockPos blockPos2 = blockPos.below();
        BlockState blockState2 = levelAccessor.getBlockState(blockPos2);
        FluidState fluidState2 = this.getNewLiquid(levelAccessor, blockPos2, blockState2);
        if (this.canSpreadTo(levelAccessor, blockPos, blockState, Direction.DOWN, blockPos2, blockState2, levelAccessor.getFluidState(blockPos2), fluidState2.getType())) {
            this.spreadTo(levelAccessor, blockPos2, blockState2, Direction.DOWN, fluidState2);
            if (this.sourceNeighborCount(levelAccessor, blockPos) >= 3) {
                this.spreadToSides(levelAccessor, blockPos, fluidState, blockState);
            }
        } else if (fluidState.isSource() || !this.isWaterHole(levelAccessor, fluidState2.getType(), blockPos, blockState, blockPos2, blockState2)) {
            this.spreadToSides(levelAccessor, blockPos, fluidState, blockState);
        }
    }

    private void spreadToSides(LevelAccessor levelAccessor, BlockPos blockPos, FluidState fluidState, BlockState blockState) {
        int n = fluidState.getAmount() - this.getDropOff(levelAccessor);
        if (fluidState.getValue(FALLING).booleanValue()) {
            n = 7;
        }
        if (n <= 0) {
            return;
        }
        Map<Direction, FluidState> map = this.getSpread(levelAccessor, blockPos, blockState);
        for (Map.Entry<Direction, FluidState> entry : map.entrySet()) {
            BlockState blockState2;
            Direction direction = entry.getKey();
            FluidState fluidState2 = entry.getValue();
            BlockPos blockPos2 = blockPos.relative(direction);
            if (!this.canSpreadTo(levelAccessor, blockPos, blockState, direction, blockPos2, blockState2 = levelAccessor.getBlockState(blockPos2), levelAccessor.getFluidState(blockPos2), fluidState2.getType())) continue;
            this.spreadTo(levelAccessor, blockPos2, blockState2, direction, fluidState2);
        }
    }

    protected FluidState getNewLiquid(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        Object object;
        Object object2;
        int n = 0;
        int n2 = 0;
        Object object3 = Direction.Plane.HORIZONTAL.iterator();
        while (object3.hasNext()) {
            object = object3.next();
            object2 = blockPos.relative((Direction)object);
            BlockState blockState2 = levelReader.getBlockState((BlockPos)object2);
            FluidState fluidState = blockState2.getFluidState();
            if (!fluidState.getType().isSame(this) || !this.canPassThroughWall((Direction)object, levelReader, blockPos, blockState, (BlockPos)object2, blockState2)) continue;
            if (fluidState.isSource()) {
                ++n2;
            }
            n = Math.max(n, fluidState.getAmount());
        }
        if (this.canConvertToSource() && n2 >= 2) {
            object3 = levelReader.getBlockState(blockPos.below());
            object = ((BlockBehaviour.BlockStateBase)object3).getFluidState();
            if (((BlockBehaviour.BlockStateBase)object3).getMaterial().isSolid() || this.isSourceBlockOfThisType((FluidState)object)) {
                return this.getSource(false);
            }
        }
        if (!((FluidState)(object2 = ((BlockBehaviour.BlockStateBase)(object = levelReader.getBlockState((BlockPos)(object3 = blockPos.above())))).getFluidState())).isEmpty() && ((FluidState)object2).getType().isSame(this) && this.canPassThroughWall(Direction.UP, levelReader, blockPos, blockState, (BlockPos)object3, (BlockState)object)) {
            return this.getFlowing(8, true);
        }
        int n3 = n - this.getDropOff(levelReader);
        if (n3 <= 0) {
            return Fluids.EMPTY.defaultFluidState();
        }
        return this.getFlowing(n3, false);
    }

    private boolean canPassThroughWall(Direction direction, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2) {
        Block.BlockStatePairKey blockStatePairKey;
        VoxelShape voxelShape;
        VoxelShape voxelShape2;
        boolean bl;
        Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2ByteLinkedOpenHashMap = blockState.getBlock().hasDynamicShape() || blockState2.getBlock().hasDynamicShape() ? null : OCCLUSION_CACHE.get();
        if (object2ByteLinkedOpenHashMap != null) {
            blockStatePairKey = new Block.BlockStatePairKey(blockState, blockState2, direction);
            byte by = object2ByteLinkedOpenHashMap.getAndMoveToFirst((Object)blockStatePairKey);
            if (by != 127) {
                return by != 0;
            }
        } else {
            blockStatePairKey = null;
        }
        boolean bl2 = bl = !Shapes.mergedFaceOccludes(voxelShape2 = blockState.getCollisionShape(blockGetter, blockPos), voxelShape = blockState2.getCollisionShape(blockGetter, blockPos2), direction);
        if (object2ByteLinkedOpenHashMap != null) {
            if (object2ByteLinkedOpenHashMap.size() == 200) {
                object2ByteLinkedOpenHashMap.removeLastByte();
            }
            object2ByteLinkedOpenHashMap.putAndMoveToFirst((Object)blockStatePairKey, (byte)(bl ? 1 : 0));
        }
        return bl;
    }

    public abstract Fluid getFlowing();

    public FluidState getFlowing(int n, boolean bl) {
        return (FluidState)((FluidState)this.getFlowing().defaultFluidState().setValue(LEVEL, n)).setValue(FALLING, bl);
    }

    public abstract Fluid getSource();

    public FluidState getSource(boolean bl) {
        return (FluidState)this.getSource().defaultFluidState().setValue(FALLING, bl);
    }

    protected abstract boolean canConvertToSource();

    protected void spreadTo(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
        if (blockState.getBlock() instanceof LiquidBlockContainer) {
            ((LiquidBlockContainer)((Object)blockState.getBlock())).placeLiquid(levelAccessor, blockPos, blockState, fluidState);
        } else {
            if (!blockState.isAir()) {
                this.beforeDestroyingBlock(levelAccessor, blockPos, blockState);
            }
            levelAccessor.setBlock(blockPos, fluidState.createLegacyBlock(), 3);
        }
    }

    protected abstract void beforeDestroyingBlock(LevelAccessor var1, BlockPos var2, BlockState var3);

    private static short getCacheKey(BlockPos blockPos, BlockPos blockPos2) {
        int n = blockPos2.getX() - blockPos.getX();
        int n2 = blockPos2.getZ() - blockPos.getZ();
        return (short)((n + 128 & 0xFF) << 8 | n2 + 128 & 0xFF);
    }

    protected int getSlopeDistance(LevelReader levelReader, BlockPos blockPos, int n2, Direction direction, BlockState blockState, BlockPos blockPos2, Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap, Short2BooleanMap short2BooleanMap) {
        int n3 = 1000;
        for (Direction direction2 : Direction.Plane.HORIZONTAL) {
            int n4;
            if (direction2 == direction) continue;
            BlockPos blockPos3 = blockPos.relative(direction2);
            short s = FlowingFluid.getCacheKey(blockPos2, blockPos3);
            Pair pair = (Pair)short2ObjectMap.computeIfAbsent(s, n -> {
                BlockState blockState = levelReader.getBlockState(blockPos3);
                return Pair.of((Object)blockState, (Object)blockState.getFluidState());
            });
            BlockState blockState2 = (BlockState)pair.getFirst();
            FluidState fluidState = (FluidState)pair.getSecond();
            if (!this.canPassThrough(levelReader, this.getFlowing(), blockPos, blockState, direction2, blockPos3, blockState2, fluidState)) continue;
            boolean bl = short2BooleanMap.computeIfAbsent(s, n -> {
                BlockPos blockPos2 = blockPos3.below();
                BlockState blockState2 = levelReader.getBlockState(blockPos2);
                return this.isWaterHole(levelReader, this.getFlowing(), blockPos3, blockState2, blockPos2, blockState2);
            });
            if (bl) {
                return n2;
            }
            if (n2 >= this.getSlopeFindDistance(levelReader) || (n4 = this.getSlopeDistance(levelReader, blockPos3, n2 + 1, direction2.getOpposite(), blockState2, blockPos2, short2ObjectMap, short2BooleanMap)) >= n3) continue;
            n3 = n4;
        }
        return n3;
    }

    private boolean isWaterHole(BlockGetter blockGetter, Fluid fluid, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2) {
        if (!this.canPassThroughWall(Direction.DOWN, blockGetter, blockPos, blockState, blockPos2, blockState2)) {
            return false;
        }
        if (blockState2.getFluidState().getType().isSame(this)) {
            return true;
        }
        return this.canHoldFluid(blockGetter, blockPos2, blockState2, fluid);
    }

    private boolean canPassThrough(BlockGetter blockGetter, Fluid fluid, BlockPos blockPos, BlockState blockState, Direction direction, BlockPos blockPos2, BlockState blockState2, FluidState fluidState) {
        return !this.isSourceBlockOfThisType(fluidState) && this.canPassThroughWall(direction, blockGetter, blockPos, blockState, blockPos2, blockState2) && this.canHoldFluid(blockGetter, blockPos2, blockState2, fluid);
    }

    private boolean isSourceBlockOfThisType(FluidState fluidState) {
        return fluidState.getType().isSame(this) && fluidState.isSource();
    }

    protected abstract int getSlopeFindDistance(LevelReader var1);

    private int sourceNeighborCount(LevelReader levelReader, BlockPos blockPos) {
        int n = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.relative(direction);
            FluidState fluidState = levelReader.getFluidState(blockPos2);
            if (!this.isSourceBlockOfThisType(fluidState)) continue;
            ++n;
        }
        return n;
    }

    protected Map<Direction, FluidState> getSpread(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        int n2 = 1000;
        EnumMap enumMap = Maps.newEnumMap(Direction.class);
        Short2ObjectOpenHashMap short2ObjectOpenHashMap = new Short2ObjectOpenHashMap();
        Short2BooleanOpenHashMap short2BooleanOpenHashMap = new Short2BooleanOpenHashMap();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.relative(direction);
            short s = FlowingFluid.getCacheKey(blockPos, blockPos2);
            Pair pair = (Pair)short2ObjectOpenHashMap.computeIfAbsent(s, n -> {
                BlockState blockState = levelReader.getBlockState(blockPos2);
                return Pair.of((Object)blockState, (Object)blockState.getFluidState());
            });
            BlockState blockState2 = (BlockState)pair.getFirst();
            FluidState fluidState = (FluidState)pair.getSecond();
            FluidState fluidState2 = this.getNewLiquid(levelReader, blockPos2, blockState2);
            if (!this.canPassThrough(levelReader, fluidState2.getType(), blockPos, blockState, direction, blockPos2, blockState2, fluidState)) continue;
            BlockPos blockPos3 = blockPos2.below();
            boolean bl = short2BooleanOpenHashMap.computeIfAbsent(s, n -> {
                BlockState blockState2 = levelReader.getBlockState(blockPos3);
                return this.isWaterHole(levelReader, this.getFlowing(), blockPos2, blockState2, blockPos3, blockState2);
            });
            int n3 = bl ? 0 : this.getSlopeDistance(levelReader, blockPos2, 1, direction.getOpposite(), blockState2, blockPos, (Short2ObjectMap<Pair<BlockState, FluidState>>)short2ObjectOpenHashMap, (Short2BooleanMap)short2BooleanOpenHashMap);
            if (n3 < n2) {
                enumMap.clear();
            }
            if (n3 > n2) continue;
            enumMap.put(direction, fluidState2);
            n2 = n3;
        }
        return enumMap;
    }

    private boolean canHoldFluid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        Block block = blockState.getBlock();
        if (block instanceof LiquidBlockContainer) {
            return ((LiquidBlockContainer)((Object)block)).canPlaceLiquid(blockGetter, blockPos, blockState, fluid);
        }
        if (block instanceof DoorBlock || block.is(BlockTags.SIGNS) || block == Blocks.LADDER || block == Blocks.SUGAR_CANE || block == Blocks.BUBBLE_COLUMN) {
            return false;
        }
        Material material = blockState.getMaterial();
        if (material == Material.PORTAL || material == Material.STRUCTURAL_AIR || material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
            return false;
        }
        return !material.blocksMotion();
    }

    protected boolean canSpreadTo(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Direction direction, BlockPos blockPos2, BlockState blockState2, FluidState fluidState, Fluid fluid) {
        return fluidState.canBeReplacedWith(blockGetter, blockPos2, fluid, direction) && this.canPassThroughWall(direction, blockGetter, blockPos, blockState, blockPos2, blockState2) && this.canHoldFluid(blockGetter, blockPos2, blockState2, fluid);
    }

    protected abstract int getDropOff(LevelReader var1);

    protected int getSpreadDelay(Level level, BlockPos blockPos, FluidState fluidState, FluidState fluidState2) {
        return this.getTickDelay(level);
    }

    @Override
    public void tick(Level level, BlockPos blockPos, FluidState fluidState) {
        if (!fluidState.isSource()) {
            FluidState fluidState2 = this.getNewLiquid(level, blockPos, level.getBlockState(blockPos));
            int n = this.getSpreadDelay(level, blockPos, fluidState, fluidState2);
            if (fluidState2.isEmpty()) {
                fluidState = fluidState2;
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
            } else if (!fluidState2.equals(fluidState)) {
                fluidState = fluidState2;
                BlockState blockState = fluidState.createLegacyBlock();
                level.setBlock(blockPos, blockState, 2);
                level.getLiquidTicks().scheduleTick(blockPos, fluidState.getType(), n);
                level.updateNeighborsAt(blockPos, blockState.getBlock());
            }
        }
        this.spread(level, blockPos, fluidState);
    }

    protected static int getLegacyLevel(FluidState fluidState) {
        if (fluidState.isSource()) {
            return 0;
        }
        return 8 - Math.min(fluidState.getAmount(), 8) + (fluidState.getValue(FALLING) != false ? 8 : 0);
    }

    private static boolean hasSameAbove(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos) {
        return fluidState.getType().isSame(blockGetter.getFluidState(blockPos.above()).getType());
    }

    @Override
    public float getHeight(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos) {
        if (FlowingFluid.hasSameAbove(fluidState, blockGetter, blockPos)) {
            return 1.0f;
        }
        return fluidState.getOwnHeight();
    }

    @Override
    public float getOwnHeight(FluidState fluidState) {
        return (float)fluidState.getAmount() / 9.0f;
    }

    @Override
    public VoxelShape getShape(FluidState fluidState2, BlockGetter blockGetter, BlockPos blockPos) {
        if (fluidState2.getAmount() == 9 && FlowingFluid.hasSameAbove(fluidState2, blockGetter, blockPos)) {
            return Shapes.block();
        }
        return this.shapes.computeIfAbsent(fluidState2, fluidState -> Shapes.box(0.0, 0.0, 0.0, 1.0, fluidState.getHeight(blockGetter, blockPos), 1.0));
    }

}

