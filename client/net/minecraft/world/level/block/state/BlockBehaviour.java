/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BlockBehaviour {
    protected static final Direction[] UPDATE_SHAPE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
    protected final Material material;
    protected final boolean hasCollision;
    protected final float explosionResistance;
    protected final boolean isRandomlyTicking;
    protected final SoundType soundType;
    protected final float friction;
    protected final float speedFactor;
    protected final float jumpFactor;
    protected final boolean dynamicShape;
    protected final Properties properties;
    @Nullable
    protected ResourceLocation drops;

    public BlockBehaviour(Properties properties) {
        this.material = properties.material;
        this.hasCollision = properties.hasCollision;
        this.drops = properties.drops;
        this.explosionResistance = properties.explosionResistance;
        this.isRandomlyTicking = properties.isRandomlyTicking;
        this.soundType = properties.soundType;
        this.friction = properties.friction;
        this.speedFactor = properties.speedFactor;
        this.jumpFactor = properties.jumpFactor;
        this.dynamicShape = properties.dynamicShape;
        this.properties = properties;
    }

    @Deprecated
    public void updateIndirectNeighbourShapes(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, int n, int n2) {
    }

    @Deprecated
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        switch (pathComputationType) {
            case LAND: {
                return !blockState.isCollisionShapeFullBlock(blockGetter, blockPos);
            }
            case WATER: {
                return blockGetter.getFluidState(blockPos).is(FluidTags.WATER);
            }
            case AIR: {
                return !blockState.isCollisionShapeFullBlock(blockGetter, blockPos);
            }
        }
        return false;
    }

    @Deprecated
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        return blockState;
    }

    @Deprecated
    public boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
        return false;
    }

    @Deprecated
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        DebugPackets.sendNeighborsUpdatePacket(level, blockPos);
    }

    @Deprecated
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
    }

    @Deprecated
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (this.isEntityBlock() && !blockState.is(blockState2.getBlock())) {
            level.removeBlockEntity(blockPos);
        }
    }

    @Deprecated
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return InteractionResult.PASS;
    }

    @Deprecated
    public boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int n, int n2) {
        return false;
    }

    @Deprecated
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Deprecated
    public boolean useShapeForLightOcclusion(BlockState blockState) {
        return false;
    }

    @Deprecated
    public boolean isSignalSource(BlockState blockState) {
        return false;
    }

    @Deprecated
    public PushReaction getPistonPushReaction(BlockState blockState) {
        return this.material.getPushReaction();
    }

    @Deprecated
    public FluidState getFluidState(BlockState blockState) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Deprecated
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return false;
    }

    public OffsetType getOffsetType() {
        return OffsetType.NONE;
    }

    @Deprecated
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState;
    }

    @Deprecated
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState;
    }

    @Deprecated
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return this.material.isReplaceable() && (blockPlaceContext.getItemInHand().isEmpty() || blockPlaceContext.getItemInHand().getItem() != this.asItem());
    }

    @Deprecated
    public boolean canBeReplaced(BlockState blockState, Fluid fluid) {
        return this.material.isReplaceable() || !this.material.isSolid();
    }

    @Deprecated
    public List<ItemStack> getDrops(BlockState blockState, LootContext.Builder builder) {
        ResourceLocation resourceLocation = this.getLootTable();
        if (resourceLocation == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        }
        LootContext lootContext = builder.withParameter(LootContextParams.BLOCK_STATE, blockState).create(LootContextParamSets.BLOCK);
        ServerLevel serverLevel = lootContext.getLevel();
        LootTable lootTable = serverLevel.getServer().getLootTables().get(resourceLocation);
        return lootTable.getRandomItems(lootContext);
    }

    @Deprecated
    public long getSeed(BlockState blockState, BlockPos blockPos) {
        return Mth.getSeed(blockPos);
    }

    @Deprecated
    public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.getShape(blockGetter, blockPos);
    }

    @Deprecated
    public VoxelShape getBlockSupportShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return this.getCollisionShape(blockState, blockGetter, blockPos, CollisionContext.empty());
    }

    @Deprecated
    public VoxelShape getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return Shapes.empty();
    }

    @Deprecated
    public int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        if (blockState.isSolidRender(blockGetter, blockPos)) {
            return blockGetter.getMaxLightLevel();
        }
        return blockState.propagatesSkylightDown(blockGetter, blockPos) ? 0 : 1;
    }

    @Nullable
    @Deprecated
    public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
        return null;
    }

    @Deprecated
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return true;
    }

    @Deprecated
    public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.isCollisionShapeFullBlock(blockGetter, blockPos) ? 0.2f : 1.0f;
    }

    @Deprecated
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return 0;
    }

    @Deprecated
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.block();
    }

    @Deprecated
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.hasCollision ? blockState.getShape(blockGetter, blockPos) : Shapes.empty();
    }

    @Deprecated
    public VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.getCollisionShape(blockState, blockGetter, blockPos, collisionContext);
    }

    @Deprecated
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        this.tick(blockState, serverLevel, blockPos, random);
    }

    @Deprecated
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
    }

    @Deprecated
    public float getDestroyProgress(BlockState blockState, Player player, BlockGetter blockGetter, BlockPos blockPos) {
        float f = blockState.getDestroySpeed(blockGetter, blockPos);
        if (f == -1.0f) {
            return 0.0f;
        }
        int n = player.hasCorrectToolForDrops(blockState) ? 30 : 100;
        return player.getDestroySpeed(blockState) / f / (float)n;
    }

    @Deprecated
    public void spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack) {
    }

    @Deprecated
    public void attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
    }

    @Deprecated
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return 0;
    }

    @Deprecated
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
    }

    @Deprecated
    public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return 0;
    }

    public final boolean isEntityBlock() {
        return this instanceof EntityBlock;
    }

    public final ResourceLocation getLootTable() {
        if (this.drops == null) {
            ResourceLocation resourceLocation = Registry.BLOCK.getKey(this.asBlock());
            this.drops = new ResourceLocation(resourceLocation.getNamespace(), "blocks/" + resourceLocation.getPath());
        }
        return this.drops;
    }

    @Deprecated
    public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
    }

    public abstract Item asItem();

    protected abstract Block asBlock();

    public MaterialColor defaultMaterialColor() {
        return (MaterialColor)this.properties.materialColor.apply(this.asBlock().defaultBlockState());
    }

    public static interface StateArgumentPredicate<A> {
        public boolean test(BlockState var1, BlockGetter var2, BlockPos var3, A var4);
    }

    public static interface StatePredicate {
        public boolean test(BlockState var1, BlockGetter var2, BlockPos var3);
    }

    public static abstract class BlockStateBase
    extends StateHolder<Block, BlockState> {
        private final int lightEmission;
        private final boolean useShapeForLightOcclusion;
        private final boolean isAir;
        private final Material material;
        private final MaterialColor materialColor;
        private final float destroySpeed;
        private final boolean requiresCorrectToolForDrops;
        private final boolean canOcclude;
        private final StatePredicate isRedstoneConductor;
        private final StatePredicate isSuffocating;
        private final StatePredicate isViewBlocking;
        private final StatePredicate hasPostProcess;
        private final StatePredicate emissiveRendering;
        @Nullable
        protected Cache cache;

        protected BlockStateBase(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap, MapCodec<BlockState> mapCodec) {
            super(block, immutableMap, mapCodec);
            Properties properties = block.properties;
            this.lightEmission = properties.lightEmission.applyAsInt(this.asState());
            this.useShapeForLightOcclusion = block.useShapeForLightOcclusion(this.asState());
            this.isAir = properties.isAir;
            this.material = properties.material;
            this.materialColor = (MaterialColor)properties.materialColor.apply(this.asState());
            this.destroySpeed = properties.destroyTime;
            this.requiresCorrectToolForDrops = properties.requiresCorrectToolForDrops;
            this.canOcclude = properties.canOcclude;
            this.isRedstoneConductor = properties.isRedstoneConductor;
            this.isSuffocating = properties.isSuffocating;
            this.isViewBlocking = properties.isViewBlocking;
            this.hasPostProcess = properties.hasPostProcess;
            this.emissiveRendering = properties.emissiveRendering;
        }

        public void initCache() {
            if (!this.getBlock().hasDynamicShape()) {
                this.cache = new Cache(this.asState());
            }
        }

        public Block getBlock() {
            return (Block)this.owner;
        }

        public Material getMaterial() {
            return this.material;
        }

        public boolean isValidSpawn(BlockGetter blockGetter, BlockPos blockPos, EntityType<?> entityType) {
            return this.getBlock().properties.isValidSpawn.test(this.asState(), blockGetter, blockPos, entityType);
        }

        public boolean propagatesSkylightDown(BlockGetter blockGetter, BlockPos blockPos) {
            if (this.cache != null) {
                return this.cache.propagatesSkylightDown;
            }
            return this.getBlock().propagatesSkylightDown(this.asState(), blockGetter, blockPos);
        }

        public int getLightBlock(BlockGetter blockGetter, BlockPos blockPos) {
            if (this.cache != null) {
                return this.cache.lightBlock;
            }
            return this.getBlock().getLightBlock(this.asState(), blockGetter, blockPos);
        }

        public VoxelShape getFaceOcclusionShape(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
            if (this.cache != null && this.cache.occlusionShapes != null) {
                return this.cache.occlusionShapes[direction.ordinal()];
            }
            return Shapes.getFaceShape(this.getOcclusionShape(blockGetter, blockPos), direction);
        }

        public VoxelShape getOcclusionShape(BlockGetter blockGetter, BlockPos blockPos) {
            return this.getBlock().getOcclusionShape(this.asState(), blockGetter, blockPos);
        }

        public boolean hasLargeCollisionShape() {
            return this.cache == null || this.cache.largeCollisionShape;
        }

        public boolean useShapeForLightOcclusion() {
            return this.useShapeForLightOcclusion;
        }

        public int getLightEmission() {
            return this.lightEmission;
        }

        public boolean isAir() {
            return this.isAir;
        }

        public MaterialColor getMapColor(BlockGetter blockGetter, BlockPos blockPos) {
            return this.materialColor;
        }

        public BlockState rotate(Rotation rotation) {
            return this.getBlock().rotate(this.asState(), rotation);
        }

        public BlockState mirror(Mirror mirror) {
            return this.getBlock().mirror(this.asState(), mirror);
        }

        public RenderShape getRenderShape() {
            return this.getBlock().getRenderShape(this.asState());
        }

        public boolean emissiveRendering(BlockGetter blockGetter, BlockPos blockPos) {
            return this.emissiveRendering.test(this.asState(), blockGetter, blockPos);
        }

        public float getShadeBrightness(BlockGetter blockGetter, BlockPos blockPos) {
            return this.getBlock().getShadeBrightness(this.asState(), blockGetter, blockPos);
        }

        public boolean isRedstoneConductor(BlockGetter blockGetter, BlockPos blockPos) {
            return this.isRedstoneConductor.test(this.asState(), blockGetter, blockPos);
        }

        public boolean isSignalSource() {
            return this.getBlock().isSignalSource(this.asState());
        }

        public int getSignal(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
            return this.getBlock().getSignal(this.asState(), blockGetter, blockPos, direction);
        }

        public boolean hasAnalogOutputSignal() {
            return this.getBlock().hasAnalogOutputSignal(this.asState());
        }

        public int getAnalogOutputSignal(Level level, BlockPos blockPos) {
            return this.getBlock().getAnalogOutputSignal(this.asState(), level, blockPos);
        }

        public float getDestroySpeed(BlockGetter blockGetter, BlockPos blockPos) {
            return this.destroySpeed;
        }

        public float getDestroyProgress(Player player, BlockGetter blockGetter, BlockPos blockPos) {
            return this.getBlock().getDestroyProgress(this.asState(), player, blockGetter, blockPos);
        }

        public int getDirectSignal(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
            return this.getBlock().getDirectSignal(this.asState(), blockGetter, blockPos, direction);
        }

        public PushReaction getPistonPushReaction() {
            return this.getBlock().getPistonPushReaction(this.asState());
        }

        public boolean isSolidRender(BlockGetter blockGetter, BlockPos blockPos) {
            if (this.cache != null) {
                return this.cache.solidRender;
            }
            BlockState blockState = this.asState();
            if (blockState.canOcclude()) {
                return Block.isShapeFullBlock(blockState.getOcclusionShape(blockGetter, blockPos));
            }
            return false;
        }

        public boolean canOcclude() {
            return this.canOcclude;
        }

        public boolean skipRendering(BlockState blockState, Direction direction) {
            return this.getBlock().skipRendering(this.asState(), blockState, direction);
        }

        public VoxelShape getShape(BlockGetter blockGetter, BlockPos blockPos) {
            return this.getShape(blockGetter, blockPos, CollisionContext.empty());
        }

        public VoxelShape getShape(BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
            return this.getBlock().getShape(this.asState(), blockGetter, blockPos, collisionContext);
        }

        public VoxelShape getCollisionShape(BlockGetter blockGetter, BlockPos blockPos) {
            if (this.cache != null) {
                return this.cache.collisionShape;
            }
            return this.getCollisionShape(blockGetter, blockPos, CollisionContext.empty());
        }

        public VoxelShape getCollisionShape(BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
            return this.getBlock().getCollisionShape(this.asState(), blockGetter, blockPos, collisionContext);
        }

        public VoxelShape getBlockSupportShape(BlockGetter blockGetter, BlockPos blockPos) {
            return this.getBlock().getBlockSupportShape(this.asState(), blockGetter, blockPos);
        }

        public VoxelShape getVisualShape(BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
            return this.getBlock().getVisualShape(this.asState(), blockGetter, blockPos, collisionContext);
        }

        public VoxelShape getInteractionShape(BlockGetter blockGetter, BlockPos blockPos) {
            return this.getBlock().getInteractionShape(this.asState(), blockGetter, blockPos);
        }

        public final boolean entityCanStandOn(BlockGetter blockGetter, BlockPos blockPos, Entity entity) {
            return this.entityCanStandOnFace(blockGetter, blockPos, entity, Direction.UP);
        }

        public final boolean entityCanStandOnFace(BlockGetter blockGetter, BlockPos blockPos, Entity entity, Direction direction) {
            return Block.isFaceFull(this.getCollisionShape(blockGetter, blockPos, CollisionContext.of(entity)), direction);
        }

        public Vec3 getOffset(BlockGetter blockGetter, BlockPos blockPos) {
            OffsetType offsetType = this.getBlock().getOffsetType();
            if (offsetType == OffsetType.NONE) {
                return Vec3.ZERO;
            }
            long l = Mth.getSeed(blockPos.getX(), 0, blockPos.getZ());
            return new Vec3(((double)((float)(l & 0xFL) / 15.0f) - 0.5) * 0.5, offsetType == OffsetType.XYZ ? ((double)((float)(l >> 4 & 0xFL) / 15.0f) - 1.0) * 0.2 : 0.0, ((double)((float)(l >> 8 & 0xFL) / 15.0f) - 0.5) * 0.5);
        }

        public boolean triggerEvent(Level level, BlockPos blockPos, int n, int n2) {
            return this.getBlock().triggerEvent(this.asState(), level, blockPos, n, n2);
        }

        public void neighborChanged(Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
            this.getBlock().neighborChanged(this.asState(), level, blockPos, block, blockPos2, bl);
        }

        public final void updateNeighbourShapes(LevelAccessor levelAccessor, BlockPos blockPos, int n) {
            this.updateNeighbourShapes(levelAccessor, blockPos, n, 512);
        }

        public final void updateNeighbourShapes(LevelAccessor levelAccessor, BlockPos blockPos, int n, int n2) {
            this.getBlock();
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            for (Direction direction : UPDATE_SHAPE_ORDER) {
                mutableBlockPos.setWithOffset(blockPos, direction);
                BlockState blockState = levelAccessor.getBlockState(mutableBlockPos);
                BlockState blockState2 = blockState.updateShape(direction.getOpposite(), this.asState(), levelAccessor, mutableBlockPos, blockPos);
                Block.updateOrDestroy(blockState, blockState2, levelAccessor, mutableBlockPos, n, n2);
            }
        }

        public final void updateIndirectNeighbourShapes(LevelAccessor levelAccessor, BlockPos blockPos, int n) {
            this.updateIndirectNeighbourShapes(levelAccessor, blockPos, n, 512);
        }

        public void updateIndirectNeighbourShapes(LevelAccessor levelAccessor, BlockPos blockPos, int n, int n2) {
            this.getBlock().updateIndirectNeighbourShapes(this.asState(), levelAccessor, blockPos, n, n2);
        }

        public void onPlace(Level level, BlockPos blockPos, BlockState blockState, boolean bl) {
            this.getBlock().onPlace(this.asState(), level, blockPos, blockState, bl);
        }

        public void onRemove(Level level, BlockPos blockPos, BlockState blockState, boolean bl) {
            this.getBlock().onRemove(this.asState(), level, blockPos, blockState, bl);
        }

        public void tick(ServerLevel serverLevel, BlockPos blockPos, Random random) {
            this.getBlock().tick(this.asState(), serverLevel, blockPos, random);
        }

        public void randomTick(ServerLevel serverLevel, BlockPos blockPos, Random random) {
            this.getBlock().randomTick(this.asState(), serverLevel, blockPos, random);
        }

        public void entityInside(Level level, BlockPos blockPos, Entity entity) {
            this.getBlock().entityInside(this.asState(), level, blockPos, entity);
        }

        public void spawnAfterBreak(ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack) {
            this.getBlock().spawnAfterBreak(this.asState(), serverLevel, blockPos, itemStack);
        }

        public List<ItemStack> getDrops(LootContext.Builder builder) {
            return this.getBlock().getDrops(this.asState(), builder);
        }

        public InteractionResult use(Level level, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
            return this.getBlock().use(this.asState(), level, blockHitResult.getBlockPos(), player, interactionHand, blockHitResult);
        }

        public void attack(Level level, BlockPos blockPos, Player player) {
            this.getBlock().attack(this.asState(), level, blockPos, player);
        }

        public boolean isSuffocating(BlockGetter blockGetter, BlockPos blockPos) {
            return this.isSuffocating.test(this.asState(), blockGetter, blockPos);
        }

        public boolean isViewBlocking(BlockGetter blockGetter, BlockPos blockPos) {
            return this.isViewBlocking.test(this.asState(), blockGetter, blockPos);
        }

        public BlockState updateShape(Direction direction, BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
            return this.getBlock().updateShape(this.asState(), direction, blockState, levelAccessor, blockPos, blockPos2);
        }

        public boolean isPathfindable(BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
            return this.getBlock().isPathfindable(this.asState(), blockGetter, blockPos, pathComputationType);
        }

        public boolean canBeReplaced(BlockPlaceContext blockPlaceContext) {
            return this.getBlock().canBeReplaced(this.asState(), blockPlaceContext);
        }

        public boolean canBeReplaced(Fluid fluid) {
            return this.getBlock().canBeReplaced(this.asState(), fluid);
        }

        public boolean canSurvive(LevelReader levelReader, BlockPos blockPos) {
            return this.getBlock().canSurvive(this.asState(), levelReader, blockPos);
        }

        public boolean hasPostProcess(BlockGetter blockGetter, BlockPos blockPos) {
            return this.hasPostProcess.test(this.asState(), blockGetter, blockPos);
        }

        @Nullable
        public MenuProvider getMenuProvider(Level level, BlockPos blockPos) {
            return this.getBlock().getMenuProvider(this.asState(), level, blockPos);
        }

        public boolean is(Tag<Block> tag) {
            return this.getBlock().is(tag);
        }

        public boolean is(Tag<Block> tag, Predicate<BlockStateBase> predicate) {
            return this.getBlock().is(tag) && predicate.test(this);
        }

        public boolean is(Block block) {
            return this.getBlock().is(block);
        }

        public FluidState getFluidState() {
            return this.getBlock().getFluidState(this.asState());
        }

        public boolean isRandomlyTicking() {
            return this.getBlock().isRandomlyTicking(this.asState());
        }

        public long getSeed(BlockPos blockPos) {
            return this.getBlock().getSeed(this.asState(), blockPos);
        }

        public SoundType getSoundType() {
            return this.getBlock().getSoundType(this.asState());
        }

        public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
            this.getBlock().onProjectileHit(level, blockState, blockHitResult, projectile);
        }

        public boolean isFaceSturdy(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
            return this.isFaceSturdy(blockGetter, blockPos, direction, SupportType.FULL);
        }

        public boolean isFaceSturdy(BlockGetter blockGetter, BlockPos blockPos, Direction direction, SupportType supportType) {
            if (this.cache != null) {
                return this.cache.isFaceSturdy(direction, supportType);
            }
            return supportType.isSupporting(this.asState(), blockGetter, blockPos, direction);
        }

        public boolean isCollisionShapeFullBlock(BlockGetter blockGetter, BlockPos blockPos) {
            if (this.cache != null) {
                return this.cache.isCollisionShapeFullBlock;
            }
            return Block.isShapeFullBlock(this.getCollisionShape(blockGetter, blockPos));
        }

        protected abstract BlockState asState();

        public boolean requiresCorrectToolForDrops() {
            return this.requiresCorrectToolForDrops;
        }

        static final class Cache {
            private static final Direction[] DIRECTIONS = Direction.values();
            private static final int SUPPORT_TYPE_COUNT = SupportType.values().length;
            protected final boolean solidRender;
            private final boolean propagatesSkylightDown;
            private final int lightBlock;
            @Nullable
            private final VoxelShape[] occlusionShapes;
            protected final VoxelShape collisionShape;
            protected final boolean largeCollisionShape;
            private final boolean[] faceSturdy;
            protected final boolean isCollisionShapeFullBlock;

            private Cache(BlockState blockState) {
                Block block = blockState.getBlock();
                this.solidRender = blockState.isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                this.propagatesSkylightDown = block.propagatesSkylightDown(blockState, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                this.lightBlock = block.getLightBlock(blockState, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                if (!blockState.canOcclude()) {
                    this.occlusionShapes = null;
                } else {
                    this.occlusionShapes = new VoxelShape[DIRECTIONS.length];
                    Direction[] arrdirection = block.getOcclusionShape(blockState, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                    Direction[] arrdirection2 = DIRECTIONS;
                    int n = arrdirection2.length;
                    for (int i = 0; i < n; ++i) {
                        SupportType[] arrsupportType = arrdirection2[i];
                        this.occlusionShapes[arrsupportType.ordinal()] = Shapes.getFaceShape((VoxelShape)arrdirection, (Direction)arrsupportType);
                    }
                }
                this.collisionShape = block.getCollisionShape(blockState, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty());
                this.largeCollisionShape = Arrays.stream(Direction.Axis.values()).anyMatch(axis -> this.collisionShape.min((Direction.Axis)axis) < 0.0 || this.collisionShape.max((Direction.Axis)axis) > 1.0);
                this.faceSturdy = new boolean[DIRECTIONS.length * SUPPORT_TYPE_COUNT];
                for (Direction direction : DIRECTIONS) {
                    for (SupportType supportType : SupportType.values()) {
                        this.faceSturdy[Cache.getFaceSupportIndex((Direction)direction, (SupportType)supportType)] = supportType.isSupporting(blockState, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, direction);
                    }
                }
                this.isCollisionShapeFullBlock = Block.isShapeFullBlock(blockState.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
            }

            public boolean isFaceSturdy(Direction direction, SupportType supportType) {
                return this.faceSturdy[Cache.getFaceSupportIndex(direction, supportType)];
            }

            private static int getFaceSupportIndex(Direction direction, SupportType supportType) {
                return direction.ordinal() * SUPPORT_TYPE_COUNT + supportType.ordinal();
            }
        }

    }

    public static class Properties {
        private Material material;
        private Function<BlockState, MaterialColor> materialColor;
        private boolean hasCollision = true;
        private SoundType soundType = SoundType.STONE;
        private ToIntFunction<BlockState> lightEmission = blockState -> 0;
        private float explosionResistance;
        private float destroyTime;
        private boolean requiresCorrectToolForDrops;
        private boolean isRandomlyTicking;
        private float friction = 0.6f;
        private float speedFactor = 1.0f;
        private float jumpFactor = 1.0f;
        private ResourceLocation drops;
        private boolean canOcclude = true;
        private boolean isAir;
        private StateArgumentPredicate<EntityType<?>> isValidSpawn = (blockState, blockGetter, blockPos, entityType) -> blockState.isFaceSturdy(blockGetter, blockPos, Direction.UP) && blockState.getLightEmission() < 14;
        private StatePredicate isRedstoneConductor = (blockState, blockGetter, blockPos) -> blockState.getMaterial().isSolidBlocking() && blockState.isCollisionShapeFullBlock(blockGetter, blockPos);
        private StatePredicate isSuffocating;
        private StatePredicate isViewBlocking = this.isSuffocating = (blockState, blockGetter, blockPos) -> this.material.blocksMotion() && blockState.isCollisionShapeFullBlock(blockGetter, blockPos);
        private StatePredicate hasPostProcess = (blockState, blockGetter, blockPos) -> false;
        private StatePredicate emissiveRendering = (blockState, blockGetter, blockPos) -> false;
        private boolean dynamicShape;

        private Properties(Material material, MaterialColor materialColor) {
            this(material, blockState -> materialColor);
        }

        private Properties(Material material, Function<BlockState, MaterialColor> function) {
            this.material = material;
            this.materialColor = function;
        }

        public static Properties of(Material material) {
            return Properties.of(material, material.getColor());
        }

        public static Properties of(Material material, DyeColor dyeColor) {
            return Properties.of(material, dyeColor.getMaterialColor());
        }

        public static Properties of(Material material, MaterialColor materialColor) {
            return new Properties(material, materialColor);
        }

        public static Properties of(Material material, Function<BlockState, MaterialColor> function) {
            return new Properties(material, function);
        }

        public static Properties copy(BlockBehaviour blockBehaviour) {
            Properties properties = new Properties(blockBehaviour.material, blockBehaviour.properties.materialColor);
            properties.material = blockBehaviour.properties.material;
            properties.destroyTime = blockBehaviour.properties.destroyTime;
            properties.explosionResistance = blockBehaviour.properties.explosionResistance;
            properties.hasCollision = blockBehaviour.properties.hasCollision;
            properties.isRandomlyTicking = blockBehaviour.properties.isRandomlyTicking;
            properties.lightEmission = blockBehaviour.properties.lightEmission;
            properties.materialColor = blockBehaviour.properties.materialColor;
            properties.soundType = blockBehaviour.properties.soundType;
            properties.friction = blockBehaviour.properties.friction;
            properties.speedFactor = blockBehaviour.properties.speedFactor;
            properties.dynamicShape = blockBehaviour.properties.dynamicShape;
            properties.canOcclude = blockBehaviour.properties.canOcclude;
            properties.isAir = blockBehaviour.properties.isAir;
            properties.requiresCorrectToolForDrops = blockBehaviour.properties.requiresCorrectToolForDrops;
            return properties;
        }

        public Properties noCollission() {
            this.hasCollision = false;
            this.canOcclude = false;
            return this;
        }

        public Properties noOcclusion() {
            this.canOcclude = false;
            return this;
        }

        public Properties friction(float f) {
            this.friction = f;
            return this;
        }

        public Properties speedFactor(float f) {
            this.speedFactor = f;
            return this;
        }

        public Properties jumpFactor(float f) {
            this.jumpFactor = f;
            return this;
        }

        public Properties sound(SoundType soundType) {
            this.soundType = soundType;
            return this;
        }

        public Properties lightLevel(ToIntFunction<BlockState> toIntFunction) {
            this.lightEmission = toIntFunction;
            return this;
        }

        public Properties strength(float f, float f2) {
            this.destroyTime = f;
            this.explosionResistance = Math.max(0.0f, f2);
            return this;
        }

        public Properties instabreak() {
            return this.strength(0.0f);
        }

        public Properties strength(float f) {
            this.strength(f, f);
            return this;
        }

        public Properties randomTicks() {
            this.isRandomlyTicking = true;
            return this;
        }

        public Properties dynamicShape() {
            this.dynamicShape = true;
            return this;
        }

        public Properties noDrops() {
            this.drops = BuiltInLootTables.EMPTY;
            return this;
        }

        public Properties dropsLike(Block block) {
            this.drops = block.getLootTable();
            return this;
        }

        public Properties air() {
            this.isAir = true;
            return this;
        }

        public Properties isValidSpawn(StateArgumentPredicate<EntityType<?>> stateArgumentPredicate) {
            this.isValidSpawn = stateArgumentPredicate;
            return this;
        }

        public Properties isRedstoneConductor(StatePredicate statePredicate) {
            this.isRedstoneConductor = statePredicate;
            return this;
        }

        public Properties isSuffocating(StatePredicate statePredicate) {
            this.isSuffocating = statePredicate;
            return this;
        }

        public Properties isViewBlocking(StatePredicate statePredicate) {
            this.isViewBlocking = statePredicate;
            return this;
        }

        public Properties hasPostProcess(StatePredicate statePredicate) {
            this.hasPostProcess = statePredicate;
            return this;
        }

        public Properties emissiveRendering(StatePredicate statePredicate) {
            this.emissiveRendering = statePredicate;
            return this;
        }

        public Properties requiresCorrectToolForDrops() {
            this.requiresCorrectToolForDrops = true;
            return this;
        }
    }

    public static enum OffsetType {
        NONE,
        XZ,
        XYZ;
        
    }

}

