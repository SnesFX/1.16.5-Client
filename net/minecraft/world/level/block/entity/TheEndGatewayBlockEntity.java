/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.Features;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TheEndGatewayBlockEntity
extends TheEndPortalBlockEntity
implements TickableBlockEntity {
    private static final Logger LOGGER = LogManager.getLogger();
    private long age;
    private int teleportCooldown;
    @Nullable
    private BlockPos exitPortal;
    private boolean exactTeleport;

    public TheEndGatewayBlockEntity() {
        super(BlockEntityType.END_GATEWAY);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        compoundTag.putLong("Age", this.age);
        if (this.exitPortal != null) {
            compoundTag.put("ExitPortal", NbtUtils.writeBlockPos(this.exitPortal));
        }
        if (this.exactTeleport) {
            compoundTag.putBoolean("ExactTeleport", this.exactTeleport);
        }
        return compoundTag;
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        super.load(blockState, compoundTag);
        this.age = compoundTag.getLong("Age");
        if (compoundTag.contains("ExitPortal", 10)) {
            this.exitPortal = NbtUtils.readBlockPos(compoundTag.getCompound("ExitPortal"));
        }
        this.exactTeleport = compoundTag.getBoolean("ExactTeleport");
    }

    @Override
    public double getViewDistance() {
        return 256.0;
    }

    @Override
    public void tick() {
        boolean bl = this.isSpawning();
        boolean bl2 = this.isCoolingDown();
        ++this.age;
        if (bl2) {
            --this.teleportCooldown;
        } else if (!this.level.isClientSide) {
            List<Entity> list = this.level.getEntitiesOfClass(Entity.class, new AABB(this.getBlockPos()), TheEndGatewayBlockEntity::canEntityTeleport);
            if (!list.isEmpty()) {
                this.teleportEntity(list.get(this.level.random.nextInt(list.size())));
            }
            if (this.age % 2400L == 0L) {
                this.triggerCooldown();
            }
        }
        if (bl != this.isSpawning() || bl2 != this.isCoolingDown()) {
            this.setChanged();
        }
    }

    public static boolean canEntityTeleport(Entity entity) {
        return EntitySelector.NO_SPECTATORS.test(entity) && !entity.getRootVehicle().isOnPortalCooldown();
    }

    public boolean isSpawning() {
        return this.age < 200L;
    }

    public boolean isCoolingDown() {
        return this.teleportCooldown > 0;
    }

    public float getSpawnPercent(float f) {
        return Mth.clamp(((float)this.age + f) / 200.0f, 0.0f, 1.0f);
    }

    public float getCooldownPercent(float f) {
        return 1.0f - Mth.clamp(((float)this.teleportCooldown - f) / 40.0f, 0.0f, 1.0f);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 8, this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    public void triggerCooldown() {
        if (!this.level.isClientSide) {
            this.teleportCooldown = 40;
            this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 1, 0);
            this.setChanged();
        }
    }

    @Override
    public boolean triggerEvent(int n, int n2) {
        if (n == 1) {
            this.teleportCooldown = 40;
            return true;
        }
        return super.triggerEvent(n, n2);
    }

    public void teleportEntity(Entity entity) {
        if (!(this.level instanceof ServerLevel) || this.isCoolingDown()) {
            return;
        }
        this.teleportCooldown = 100;
        if (this.exitPortal == null && this.level.dimension() == Level.END) {
            this.findExitPortal((ServerLevel)this.level);
        }
        if (this.exitPortal != null) {
            BlockPos blockPos;
            Entity entity2;
            BlockPos blockPos2 = blockPos = this.exactTeleport ? this.exitPortal : this.findExitPosition();
            if (entity instanceof ThrownEnderpearl) {
                Entity entity3 = ((ThrownEnderpearl)entity).getOwner();
                if (entity3 instanceof ServerPlayer) {
                    CriteriaTriggers.ENTER_BLOCK.trigger((ServerPlayer)entity3, this.level.getBlockState(this.getBlockPos()));
                }
                if (entity3 != null) {
                    entity2 = entity3;
                    entity.remove();
                } else {
                    entity2 = entity;
                }
            } else {
                entity2 = entity.getRootVehicle();
            }
            entity2.setPortalCooldown();
            entity2.teleportToWithTicket((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5);
        }
        this.triggerCooldown();
    }

    private BlockPos findExitPosition() {
        BlockPos blockPos = TheEndGatewayBlockEntity.findTallestBlock(this.level, this.exitPortal.offset(0, 2, 0), 5, false);
        LOGGER.debug("Best exit position for portal at {} is {}", (Object)this.exitPortal, (Object)blockPos);
        return blockPos.above();
    }

    private void findExitPortal(ServerLevel serverLevel) {
        Vec3 vec3 = new Vec3(this.getBlockPos().getX(), 0.0, this.getBlockPos().getZ()).normalize();
        Vec3 vec32 = vec3.scale(1024.0);
        int n = 16;
        while (TheEndGatewayBlockEntity.getChunk(serverLevel, vec32).getHighestSectionPosition() > 0 && n-- > 0) {
            LOGGER.debug("Skipping backwards past nonempty chunk at {}", (Object)vec32);
            vec32 = vec32.add(vec3.scale(-16.0));
        }
        n = 16;
        while (TheEndGatewayBlockEntity.getChunk(serverLevel, vec32).getHighestSectionPosition() == 0 && n-- > 0) {
            LOGGER.debug("Skipping forward past empty chunk at {}", (Object)vec32);
            vec32 = vec32.add(vec3.scale(16.0));
        }
        LOGGER.debug("Found chunk at {}", (Object)vec32);
        LevelChunk levelChunk = TheEndGatewayBlockEntity.getChunk(serverLevel, vec32);
        this.exitPortal = TheEndGatewayBlockEntity.findValidSpawnInChunk(levelChunk);
        if (this.exitPortal == null) {
            this.exitPortal = new BlockPos(vec32.x + 0.5, 75.0, vec32.z + 0.5);
            LOGGER.debug("Failed to find suitable block, settling on {}", (Object)this.exitPortal);
            Features.END_ISLAND.place(serverLevel, serverLevel.getChunkSource().getGenerator(), new Random(this.exitPortal.asLong()), this.exitPortal);
        } else {
            LOGGER.debug("Found block at {}", (Object)this.exitPortal);
        }
        this.exitPortal = TheEndGatewayBlockEntity.findTallestBlock(serverLevel, this.exitPortal, 16, true);
        LOGGER.debug("Creating portal at {}", (Object)this.exitPortal);
        this.exitPortal = this.exitPortal.above(10);
        this.createExitPortal(serverLevel, this.exitPortal);
        this.setChanged();
    }

    private static BlockPos findTallestBlock(BlockGetter blockGetter, BlockPos blockPos, int n, boolean bl) {
        Vec3i vec3i = null;
        for (int i = -n; i <= n; ++i) {
            block1 : for (int j = -n; j <= n; ++j) {
                if (i == 0 && j == 0 && !bl) continue;
                for (int k = 255; k > (vec3i == null ? 0 : vec3i.getY()); --k) {
                    BlockPos blockPos2 = new BlockPos(blockPos.getX() + i, k, blockPos.getZ() + j);
                    BlockState blockState = blockGetter.getBlockState(blockPos2);
                    if (!blockState.isCollisionShapeFullBlock(blockGetter, blockPos2) || !bl && blockState.is(Blocks.BEDROCK)) continue;
                    vec3i = blockPos2;
                    continue block1;
                }
            }
        }
        return vec3i == null ? blockPos : vec3i;
    }

    private static LevelChunk getChunk(Level level, Vec3 vec3) {
        return level.getChunk(Mth.floor(vec3.x / 16.0), Mth.floor(vec3.z / 16.0));
    }

    @Nullable
    private static BlockPos findValidSpawnInChunk(LevelChunk levelChunk) {
        ChunkPos chunkPos = levelChunk.getPos();
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), 30, chunkPos.getMinBlockZ());
        int n = levelChunk.getHighestSectionPosition() + 16 - 1;
        BlockPos blockPos2 = new BlockPos(chunkPos.getMaxBlockX(), n, chunkPos.getMaxBlockZ());
        BlockPos blockPos3 = null;
        double d = 0.0;
        for (BlockPos blockPos4 : BlockPos.betweenClosed(blockPos, blockPos2)) {
            BlockState blockState = levelChunk.getBlockState(blockPos4);
            BlockPos blockPos5 = blockPos4.above();
            BlockPos blockPos6 = blockPos4.above(2);
            if (!blockState.is(Blocks.END_STONE) || levelChunk.getBlockState(blockPos5).isCollisionShapeFullBlock(levelChunk, blockPos5) || levelChunk.getBlockState(blockPos6).isCollisionShapeFullBlock(levelChunk, blockPos6)) continue;
            double d2 = blockPos4.distSqr(0.0, 0.0, 0.0, true);
            if (blockPos3 != null && !(d2 < d)) continue;
            blockPos3 = blockPos4;
            d = d2;
        }
        return blockPos3;
    }

    private void createExitPortal(ServerLevel serverLevel, BlockPos blockPos) {
        Feature.END_GATEWAY.configured(EndGatewayConfiguration.knownExit(this.getBlockPos(), false)).place(serverLevel, serverLevel.getChunkSource().getGenerator(), new Random(), blockPos);
    }

    @Override
    public boolean shouldRenderFace(Direction direction) {
        return Block.shouldRenderFace(this.getBlockState(), this.level, this.getBlockPos(), direction);
    }

    public int getParticleAmount() {
        int n = 0;
        for (Direction direction : Direction.values()) {
            n += this.shouldRenderFace(direction) ? 1 : 0;
        }
        return n;
    }

    public void setExitPosition(BlockPos blockPos, boolean bl) {
        this.exactTeleport = bl;
        this.exitPortal = blockPos;
    }
}

