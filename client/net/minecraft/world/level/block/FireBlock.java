/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.Tag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FireBlock
extends BaseFireBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(entry -> entry.getKey() != Direction.DOWN).collect(Util.toMap());
    private static final VoxelShape UP_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    private final Map<BlockState, VoxelShape> shapesCache;
    private final Object2IntMap<Block> flameOdds = new Object2IntOpenHashMap();
    private final Object2IntMap<Block> burnOdds = new Object2IntOpenHashMap();

    public FireBlock(BlockBehaviour.Properties properties) {
        super(properties, 1.0f);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(UP, false));
        this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().filter(blockState -> blockState.getValue(AGE) == 0).collect(Collectors.toMap(Function.identity(), FireBlock::calculateShape)));
    }

    private static VoxelShape calculateShape(BlockState blockState) {
        VoxelShape voxelShape = Shapes.empty();
        if (blockState.getValue(UP).booleanValue()) {
            voxelShape = UP_AABB;
        }
        if (blockState.getValue(NORTH).booleanValue()) {
            voxelShape = Shapes.or(voxelShape, NORTH_AABB);
        }
        if (blockState.getValue(SOUTH).booleanValue()) {
            voxelShape = Shapes.or(voxelShape, SOUTH_AABB);
        }
        if (blockState.getValue(EAST).booleanValue()) {
            voxelShape = Shapes.or(voxelShape, EAST_AABB);
        }
        if (blockState.getValue(WEST).booleanValue()) {
            voxelShape = Shapes.or(voxelShape, WEST_AABB);
        }
        return voxelShape.isEmpty() ? DOWN_AABB : voxelShape;
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (this.canSurvive(blockState, levelAccessor, blockPos)) {
            return this.getStateWithAge(levelAccessor, blockPos, blockState.getValue(AGE));
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.shapesCache.get(blockState.setValue(AGE, 0));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.getStateForPlacement(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos());
    }

    protected BlockState getStateForPlacement(BlockGetter blockGetter, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.below();
        BlockState blockState = blockGetter.getBlockState(blockPos2);
        if (this.canBurn(blockState) || blockState.isFaceSturdy(blockGetter, blockPos2, Direction.UP)) {
            return this.defaultBlockState();
        }
        BlockState blockState2 = this.defaultBlockState();
        for (Direction direction : Direction.values()) {
            BooleanProperty booleanProperty = PROPERTY_BY_DIRECTION.get(direction);
            if (booleanProperty == null) continue;
            blockState2 = (BlockState)blockState2.setValue(booleanProperty, this.canBurn(blockGetter.getBlockState(blockPos.relative(direction))));
        }
        return blockState2;
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.below();
        return levelReader.getBlockState(blockPos2).isFaceSturdy(levelReader, blockPos2, Direction.UP) || this.isValidFireLocation(levelReader, blockPos);
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        boolean bl;
        serverLevel.getBlockTicks().scheduleTick(blockPos, this, FireBlock.getFireTickDelay(serverLevel.random));
        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            return;
        }
        if (!blockState.canSurvive(serverLevel, blockPos)) {
            serverLevel.removeBlock(blockPos, false);
        }
        BlockState blockState2 = serverLevel.getBlockState(blockPos.below());
        boolean bl2 = blockState2.is(serverLevel.dimensionType().infiniburn());
        int n = blockState.getValue(AGE);
        if (!bl2 && serverLevel.isRaining() && this.isNearRain(serverLevel, blockPos) && random.nextFloat() < 0.2f + (float)n * 0.03f) {
            serverLevel.removeBlock(blockPos, false);
            return;
        }
        int n2 = Math.min(15, n + random.nextInt(3) / 2);
        if (n != n2) {
            blockState = (BlockState)blockState.setValue(AGE, n2);
            serverLevel.setBlock(blockPos, blockState, 4);
        }
        if (!bl2) {
            if (!this.isValidFireLocation(serverLevel, blockPos)) {
                BlockPos blockPos2 = blockPos.below();
                if (!serverLevel.getBlockState(blockPos2).isFaceSturdy(serverLevel, blockPos2, Direction.UP) || n > 3) {
                    serverLevel.removeBlock(blockPos, false);
                }
                return;
            }
            if (n == 15 && random.nextInt(4) == 0 && !this.canBurn(serverLevel.getBlockState(blockPos.below()))) {
                serverLevel.removeBlock(blockPos, false);
                return;
            }
        }
        int n3 = (bl = serverLevel.isHumidAt(blockPos)) ? -50 : 0;
        this.checkBurnOut(serverLevel, blockPos.east(), 300 + n3, random, n);
        this.checkBurnOut(serverLevel, blockPos.west(), 300 + n3, random, n);
        this.checkBurnOut(serverLevel, blockPos.below(), 250 + n3, random, n);
        this.checkBurnOut(serverLevel, blockPos.above(), 250 + n3, random, n);
        this.checkBurnOut(serverLevel, blockPos.north(), 300 + n3, random, n);
        this.checkBurnOut(serverLevel, blockPos.south(), 300 + n3, random, n);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 4; ++k) {
                    if (i == 0 && k == 0 && j == 0) continue;
                    int n4 = 100;
                    if (k > 1) {
                        n4 += (k - 1) * 100;
                    }
                    mutableBlockPos.setWithOffset(blockPos, i, k, j);
                    int n5 = this.getFireOdds(serverLevel, mutableBlockPos);
                    if (n5 <= 0) continue;
                    int n6 = (n5 + 40 + serverLevel.getDifficulty().getId() * 7) / (n + 30);
                    if (bl) {
                        n6 /= 2;
                    }
                    if (n6 <= 0 || random.nextInt(n4) > n6 || serverLevel.isRaining() && this.isNearRain(serverLevel, mutableBlockPos)) continue;
                    int n7 = Math.min(15, n + random.nextInt(5) / 4);
                    serverLevel.setBlock(mutableBlockPos, this.getStateWithAge(serverLevel, mutableBlockPos, n7), 3);
                }
            }
        }
    }

    protected boolean isNearRain(Level level, BlockPos blockPos) {
        return level.isRainingAt(blockPos) || level.isRainingAt(blockPos.west()) || level.isRainingAt(blockPos.east()) || level.isRainingAt(blockPos.north()) || level.isRainingAt(blockPos.south());
    }

    private int getBurnOdd(BlockState blockState) {
        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED).booleanValue()) {
            return 0;
        }
        return this.burnOdds.getInt((Object)blockState.getBlock());
    }

    private int getFlameOdds(BlockState blockState) {
        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED).booleanValue()) {
            return 0;
        }
        return this.flameOdds.getInt((Object)blockState.getBlock());
    }

    private void checkBurnOut(Level level, BlockPos blockPos, int n, Random random, int n2) {
        int n3 = this.getBurnOdd(level.getBlockState(blockPos));
        if (random.nextInt(n) < n3) {
            BlockState blockState = level.getBlockState(blockPos);
            if (random.nextInt(n2 + 10) < 5 && !level.isRainingAt(blockPos)) {
                int n4 = Math.min(n2 + random.nextInt(5) / 4, 15);
                level.setBlock(blockPos, this.getStateWithAge(level, blockPos, n4), 3);
            } else {
                level.removeBlock(blockPos, false);
            }
            Block block = blockState.getBlock();
            if (block instanceof TntBlock) {
                TntBlock cfr_ignored_0 = (TntBlock)block;
                TntBlock.explode(level, blockPos);
            }
        }
    }

    private BlockState getStateWithAge(LevelAccessor levelAccessor, BlockPos blockPos, int n) {
        BlockState blockState = FireBlock.getState(levelAccessor, blockPos);
        if (blockState.is(Blocks.FIRE)) {
            return (BlockState)blockState.setValue(AGE, n);
        }
        return blockState;
    }

    private boolean isValidFireLocation(BlockGetter blockGetter, BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            if (!this.canBurn(blockGetter.getBlockState(blockPos.relative(direction)))) continue;
            return true;
        }
        return false;
    }

    private int getFireOdds(LevelReader levelReader, BlockPos blockPos) {
        if (!levelReader.isEmptyBlock(blockPos)) {
            return 0;
        }
        int n = 0;
        for (Direction direction : Direction.values()) {
            BlockState blockState = levelReader.getBlockState(blockPos.relative(direction));
            n = Math.max(this.getFlameOdds(blockState), n);
        }
        return n;
    }

    @Override
    protected boolean canBurn(BlockState blockState) {
        return this.getFlameOdds(blockState) > 0;
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        super.onPlace(blockState, level, blockPos, blockState2, bl);
        level.getBlockTicks().scheduleTick(blockPos, this, FireBlock.getFireTickDelay(level.random));
    }

    private static int getFireTickDelay(Random random) {
        return 30 + random.nextInt(10);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
    }

    private void setFlammable(Block block, int n, int n2) {
        this.flameOdds.put((Object)block, n);
        this.burnOdds.put((Object)block, n2);
    }

    public static void bootStrap() {
        FireBlock fireBlock = (FireBlock)Blocks.FIRE;
        fireBlock.setFlammable(Blocks.OAK_PLANKS, 5, 20);
        fireBlock.setFlammable(Blocks.SPRUCE_PLANKS, 5, 20);
        fireBlock.setFlammable(Blocks.BIRCH_PLANKS, 5, 20);
        fireBlock.setFlammable(Blocks.JUNGLE_PLANKS, 5, 20);
        fireBlock.setFlammable(Blocks.ACACIA_PLANKS, 5, 20);
        fireBlock.setFlammable(Blocks.DARK_OAK_PLANKS, 5, 20);
        fireBlock.setFlammable(Blocks.OAK_SLAB, 5, 20);
        fireBlock.setFlammable(Blocks.SPRUCE_SLAB, 5, 20);
        fireBlock.setFlammable(Blocks.BIRCH_SLAB, 5, 20);
        fireBlock.setFlammable(Blocks.JUNGLE_SLAB, 5, 20);
        fireBlock.setFlammable(Blocks.ACACIA_SLAB, 5, 20);
        fireBlock.setFlammable(Blocks.DARK_OAK_SLAB, 5, 20);
        fireBlock.setFlammable(Blocks.OAK_FENCE_GATE, 5, 20);
        fireBlock.setFlammable(Blocks.SPRUCE_FENCE_GATE, 5, 20);
        fireBlock.setFlammable(Blocks.BIRCH_FENCE_GATE, 5, 20);
        fireBlock.setFlammable(Blocks.JUNGLE_FENCE_GATE, 5, 20);
        fireBlock.setFlammable(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
        fireBlock.setFlammable(Blocks.ACACIA_FENCE_GATE, 5, 20);
        fireBlock.setFlammable(Blocks.OAK_FENCE, 5, 20);
        fireBlock.setFlammable(Blocks.SPRUCE_FENCE, 5, 20);
        fireBlock.setFlammable(Blocks.BIRCH_FENCE, 5, 20);
        fireBlock.setFlammable(Blocks.JUNGLE_FENCE, 5, 20);
        fireBlock.setFlammable(Blocks.DARK_OAK_FENCE, 5, 20);
        fireBlock.setFlammable(Blocks.ACACIA_FENCE, 5, 20);
        fireBlock.setFlammable(Blocks.OAK_STAIRS, 5, 20);
        fireBlock.setFlammable(Blocks.BIRCH_STAIRS, 5, 20);
        fireBlock.setFlammable(Blocks.SPRUCE_STAIRS, 5, 20);
        fireBlock.setFlammable(Blocks.JUNGLE_STAIRS, 5, 20);
        fireBlock.setFlammable(Blocks.ACACIA_STAIRS, 5, 20);
        fireBlock.setFlammable(Blocks.DARK_OAK_STAIRS, 5, 20);
        fireBlock.setFlammable(Blocks.OAK_LOG, 5, 5);
        fireBlock.setFlammable(Blocks.SPRUCE_LOG, 5, 5);
        fireBlock.setFlammable(Blocks.BIRCH_LOG, 5, 5);
        fireBlock.setFlammable(Blocks.JUNGLE_LOG, 5, 5);
        fireBlock.setFlammable(Blocks.ACACIA_LOG, 5, 5);
        fireBlock.setFlammable(Blocks.DARK_OAK_LOG, 5, 5);
        fireBlock.setFlammable(Blocks.STRIPPED_OAK_LOG, 5, 5);
        fireBlock.setFlammable(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
        fireBlock.setFlammable(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
        fireBlock.setFlammable(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
        fireBlock.setFlammable(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
        fireBlock.setFlammable(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
        fireBlock.setFlammable(Blocks.STRIPPED_OAK_WOOD, 5, 5);
        fireBlock.setFlammable(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
        fireBlock.setFlammable(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
        fireBlock.setFlammable(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
        fireBlock.setFlammable(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
        fireBlock.setFlammable(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
        fireBlock.setFlammable(Blocks.OAK_WOOD, 5, 5);
        fireBlock.setFlammable(Blocks.SPRUCE_WOOD, 5, 5);
        fireBlock.setFlammable(Blocks.BIRCH_WOOD, 5, 5);
        fireBlock.setFlammable(Blocks.JUNGLE_WOOD, 5, 5);
        fireBlock.setFlammable(Blocks.ACACIA_WOOD, 5, 5);
        fireBlock.setFlammable(Blocks.DARK_OAK_WOOD, 5, 5);
        fireBlock.setFlammable(Blocks.OAK_LEAVES, 30, 60);
        fireBlock.setFlammable(Blocks.SPRUCE_LEAVES, 30, 60);
        fireBlock.setFlammable(Blocks.BIRCH_LEAVES, 30, 60);
        fireBlock.setFlammable(Blocks.JUNGLE_LEAVES, 30, 60);
        fireBlock.setFlammable(Blocks.ACACIA_LEAVES, 30, 60);
        fireBlock.setFlammable(Blocks.DARK_OAK_LEAVES, 30, 60);
        fireBlock.setFlammable(Blocks.BOOKSHELF, 30, 20);
        fireBlock.setFlammable(Blocks.TNT, 15, 100);
        fireBlock.setFlammable(Blocks.GRASS, 60, 100);
        fireBlock.setFlammable(Blocks.FERN, 60, 100);
        fireBlock.setFlammable(Blocks.DEAD_BUSH, 60, 100);
        fireBlock.setFlammable(Blocks.SUNFLOWER, 60, 100);
        fireBlock.setFlammable(Blocks.LILAC, 60, 100);
        fireBlock.setFlammable(Blocks.ROSE_BUSH, 60, 100);
        fireBlock.setFlammable(Blocks.PEONY, 60, 100);
        fireBlock.setFlammable(Blocks.TALL_GRASS, 60, 100);
        fireBlock.setFlammable(Blocks.LARGE_FERN, 60, 100);
        fireBlock.setFlammable(Blocks.DANDELION, 60, 100);
        fireBlock.setFlammable(Blocks.POPPY, 60, 100);
        fireBlock.setFlammable(Blocks.BLUE_ORCHID, 60, 100);
        fireBlock.setFlammable(Blocks.ALLIUM, 60, 100);
        fireBlock.setFlammable(Blocks.AZURE_BLUET, 60, 100);
        fireBlock.setFlammable(Blocks.RED_TULIP, 60, 100);
        fireBlock.setFlammable(Blocks.ORANGE_TULIP, 60, 100);
        fireBlock.setFlammable(Blocks.WHITE_TULIP, 60, 100);
        fireBlock.setFlammable(Blocks.PINK_TULIP, 60, 100);
        fireBlock.setFlammable(Blocks.OXEYE_DAISY, 60, 100);
        fireBlock.setFlammable(Blocks.CORNFLOWER, 60, 100);
        fireBlock.setFlammable(Blocks.LILY_OF_THE_VALLEY, 60, 100);
        fireBlock.setFlammable(Blocks.WITHER_ROSE, 60, 100);
        fireBlock.setFlammable(Blocks.WHITE_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.ORANGE_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.MAGENTA_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.LIGHT_BLUE_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.YELLOW_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.LIME_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.PINK_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.GRAY_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.LIGHT_GRAY_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.CYAN_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.PURPLE_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.BLUE_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.BROWN_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.GREEN_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.RED_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.BLACK_WOOL, 30, 60);
        fireBlock.setFlammable(Blocks.VINE, 15, 100);
        fireBlock.setFlammable(Blocks.COAL_BLOCK, 5, 5);
        fireBlock.setFlammable(Blocks.HAY_BLOCK, 60, 20);
        fireBlock.setFlammable(Blocks.TARGET, 15, 20);
        fireBlock.setFlammable(Blocks.WHITE_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.ORANGE_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.MAGENTA_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.LIGHT_BLUE_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.YELLOW_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.LIME_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.PINK_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.GRAY_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.LIGHT_GRAY_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.CYAN_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.PURPLE_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.BLUE_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.BROWN_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.GREEN_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.RED_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.BLACK_CARPET, 60, 20);
        fireBlock.setFlammable(Blocks.DRIED_KELP_BLOCK, 30, 60);
        fireBlock.setFlammable(Blocks.BAMBOO, 60, 60);
        fireBlock.setFlammable(Blocks.SCAFFOLDING, 60, 60);
        fireBlock.setFlammable(Blocks.LECTERN, 30, 20);
        fireBlock.setFlammable(Blocks.COMPOSTER, 5, 20);
        fireBlock.setFlammable(Blocks.SWEET_BERRY_BUSH, 60, 100);
        fireBlock.setFlammable(Blocks.BEEHIVE, 5, 20);
        fireBlock.setFlammable(Blocks.BEE_NEST, 30, 20);
    }
}

