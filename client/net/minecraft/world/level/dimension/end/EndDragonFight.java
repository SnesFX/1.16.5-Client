/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ContiguousSet
 *  com.google.common.collect.DiscreteDomain
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Range
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.Features;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonHoldingPatternPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.end.DragonRespawnAnimation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndDragonFight {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Predicate<Entity> VALID_PLAYER = EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.withinDistance(0.0, 128.0, 0.0, 192.0));
    private final ServerBossEvent dragonEvent = (ServerBossEvent)new ServerBossEvent(new TranslatableComponent("entity.minecraft.ender_dragon"), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS).setPlayBossMusic(true).setCreateWorldFog(true);
    private final ServerLevel level;
    private final List<Integer> gateways = Lists.newArrayList();
    private final BlockPattern exitPortalPattern;
    private int ticksSinceDragonSeen;
    private int crystalsAlive;
    private int ticksSinceCrystalsScanned;
    private int ticksSinceLastPlayerScan;
    private boolean dragonKilled;
    private boolean previouslyKilled;
    private UUID dragonUUID;
    private boolean needsStateScanning = true;
    private BlockPos portalLocation;
    private DragonRespawnAnimation respawnStage;
    private int respawnTime;
    private List<EndCrystal> respawnCrystals;

    public EndDragonFight(ServerLevel serverLevel, long l, CompoundTag compoundTag) {
        this.level = serverLevel;
        if (compoundTag.contains("DragonKilled", 99)) {
            if (compoundTag.hasUUID("Dragon")) {
                this.dragonUUID = compoundTag.getUUID("Dragon");
            }
            this.dragonKilled = compoundTag.getBoolean("DragonKilled");
            this.previouslyKilled = compoundTag.getBoolean("PreviouslyKilled");
            if (compoundTag.getBoolean("IsRespawning")) {
                this.respawnStage = DragonRespawnAnimation.START;
            }
            if (compoundTag.contains("ExitPortalLocation", 10)) {
                this.portalLocation = NbtUtils.readBlockPos(compoundTag.getCompound("ExitPortalLocation"));
            }
        } else {
            this.dragonKilled = true;
            this.previouslyKilled = true;
        }
        if (compoundTag.contains("Gateways", 9)) {
            ListTag listTag = compoundTag.getList("Gateways", 3);
            for (int i = 0; i < listTag.size(); ++i) {
                this.gateways.add(listTag.getInt(i));
            }
        } else {
            this.gateways.addAll((Collection<Integer>)ContiguousSet.create((Range)Range.closedOpen((Comparable)Integer.valueOf(0), (Comparable)Integer.valueOf(20)), (DiscreteDomain)DiscreteDomain.integers()));
            Collections.shuffle(this.gateways, new Random(l));
        }
        this.exitPortalPattern = BlockPatternBuilder.start().aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").where('#', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.BEDROCK))).build();
    }

    public CompoundTag saveData() {
        CompoundTag compoundTag = new CompoundTag();
        if (this.dragonUUID != null) {
            compoundTag.putUUID("Dragon", this.dragonUUID);
        }
        compoundTag.putBoolean("DragonKilled", this.dragonKilled);
        compoundTag.putBoolean("PreviouslyKilled", this.previouslyKilled);
        if (this.portalLocation != null) {
            compoundTag.put("ExitPortalLocation", NbtUtils.writeBlockPos(this.portalLocation));
        }
        ListTag listTag = new ListTag();
        for (int n : this.gateways) {
            listTag.add(IntTag.valueOf(n));
        }
        compoundTag.put("Gateways", listTag);
        return compoundTag;
    }

    public void tick() {
        this.dragonEvent.setVisible(!this.dragonKilled);
        if (++this.ticksSinceLastPlayerScan >= 20) {
            this.updatePlayers();
            this.ticksSinceLastPlayerScan = 0;
        }
        if (!this.dragonEvent.getPlayers().isEmpty()) {
            this.level.getChunkSource().addRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
            boolean bl = this.isArenaLoaded();
            if (this.needsStateScanning && bl) {
                this.scanState();
                this.needsStateScanning = false;
            }
            if (this.respawnStage != null) {
                if (this.respawnCrystals == null && bl) {
                    this.respawnStage = null;
                    this.tryRespawn();
                }
                this.respawnStage.tick(this.level, this, this.respawnCrystals, this.respawnTime++, this.portalLocation);
            }
            if (!this.dragonKilled) {
                if ((this.dragonUUID == null || ++this.ticksSinceDragonSeen >= 1200) && bl) {
                    this.findOrCreateDragon();
                    this.ticksSinceDragonSeen = 0;
                }
                if (++this.ticksSinceCrystalsScanned >= 100 && bl) {
                    this.updateCrystalCount();
                    this.ticksSinceCrystalsScanned = 0;
                }
            }
        } else {
            this.level.getChunkSource().removeRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
        }
    }

    private void scanState() {
        LOGGER.info("Scanning for legacy world dragon fight...");
        boolean bl = this.hasActiveExitPortal();
        if (bl) {
            LOGGER.info("Found that the dragon has been killed in this world already.");
            this.previouslyKilled = true;
        } else {
            LOGGER.info("Found that the dragon has not yet been killed in this world.");
            this.previouslyKilled = false;
            if (this.findExitPortal() == null) {
                this.spawnExitPortal(false);
            }
        }
        List<EnderDragon> list = this.level.getDragons();
        if (list.isEmpty()) {
            this.dragonKilled = true;
        } else {
            EnderDragon enderDragon = list.get(0);
            this.dragonUUID = enderDragon.getUUID();
            LOGGER.info("Found that there's a dragon still alive ({})", (Object)enderDragon);
            this.dragonKilled = false;
            if (!bl) {
                LOGGER.info("But we didn't have a portal, let's remove it.");
                enderDragon.remove();
                this.dragonUUID = null;
            }
        }
        if (!this.previouslyKilled && this.dragonKilled) {
            this.dragonKilled = false;
        }
    }

    private void findOrCreateDragon() {
        List<EnderDragon> list = this.level.getDragons();
        if (list.isEmpty()) {
            LOGGER.debug("Haven't seen the dragon, respawning it");
            this.createNewDragon();
        } else {
            LOGGER.debug("Haven't seen our dragon, but found another one to use.");
            this.dragonUUID = list.get(0).getUUID();
        }
    }

    protected void setRespawnStage(DragonRespawnAnimation dragonRespawnAnimation) {
        if (this.respawnStage == null) {
            throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
        }
        this.respawnTime = 0;
        if (dragonRespawnAnimation == DragonRespawnAnimation.END) {
            this.respawnStage = null;
            this.dragonKilled = false;
            EnderDragon enderDragon = this.createNewDragon();
            for (ServerPlayer serverPlayer : this.dragonEvent.getPlayers()) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, enderDragon);
            }
        } else {
            this.respawnStage = dragonRespawnAnimation;
        }
    }

    private boolean hasActiveExitPortal() {
        for (int i = -8; i <= 8; ++i) {
            for (int j = -8; j <= 8; ++j) {
                LevelChunk levelChunk = this.level.getChunk(i, j);
                for (BlockEntity blockEntity : levelChunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof TheEndPortalBlockEntity)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private BlockPattern.BlockPatternMatch findExitPortal() {
        int n;
        int n2;
        Object object;
        for (n2 = -8; n2 <= 8; ++n2) {
            for (n = -8; n <= 8; ++n) {
                object = this.level.getChunk(n2, n);
                for (BlockEntity blockEntity : ((LevelChunk)object).getBlockEntities().values()) {
                    BlockPattern.BlockPatternMatch blockPatternMatch;
                    if (!(blockEntity instanceof TheEndPortalBlockEntity) || (blockPatternMatch = this.exitPortalPattern.find(this.level, blockEntity.getBlockPos())) == null) continue;
                    BlockPos blockPos = blockPatternMatch.getBlock(3, 3, 3).getPos();
                    if (this.portalLocation == null && blockPos.getX() == 0 && blockPos.getZ() == 0) {
                        this.portalLocation = blockPos;
                    }
                    return blockPatternMatch;
                }
            }
        }
        for (n = n2 = this.level.getHeightmapPos((Heightmap.Types)Heightmap.Types.MOTION_BLOCKING, (BlockPos)EndPodiumFeature.END_PODIUM_LOCATION).getY(); n >= 0; --n) {
            object = this.exitPortalPattern.find(this.level, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION.getX(), n, EndPodiumFeature.END_PODIUM_LOCATION.getZ()));
            if (object == null) continue;
            if (this.portalLocation == null) {
                this.portalLocation = ((BlockPattern.BlockPatternMatch)object).getBlock(3, 3, 3).getPos();
            }
            return object;
        }
        return null;
    }

    private boolean isArenaLoaded() {
        for (int i = -8; i <= 8; ++i) {
            for (int j = 8; j <= 8; ++j) {
                ChunkAccess chunkAccess = this.level.getChunk(i, j, ChunkStatus.FULL, false);
                if (!(chunkAccess instanceof LevelChunk)) {
                    return false;
                }
                ChunkHolder.FullChunkStatus fullChunkStatus = ((LevelChunk)chunkAccess).getFullStatus();
                if (fullChunkStatus.isOrAfter(ChunkHolder.FullChunkStatus.TICKING)) continue;
                return false;
            }
        }
        return true;
    }

    private void updatePlayers() {
        HashSet hashSet = Sets.newHashSet();
        for (ServerPlayer object2 : this.level.getPlayers(VALID_PLAYER)) {
            this.dragonEvent.addPlayer(object2);
            hashSet.add(object2);
        }
        HashSet hashSet2 = Sets.newHashSet(this.dragonEvent.getPlayers());
        hashSet2.removeAll(hashSet);
        Iterator iterator = hashSet2.iterator();
        while (iterator.hasNext()) {
            ServerPlayer serverPlayer = (ServerPlayer)iterator.next();
            this.dragonEvent.removePlayer(serverPlayer);
        }
    }

    private void updateCrystalCount() {
        this.ticksSinceCrystalsScanned = 0;
        this.crystalsAlive = 0;
        for (SpikeFeature.EndSpike endSpike : SpikeFeature.getSpikesForLevel(this.level)) {
            this.crystalsAlive += this.level.getEntitiesOfClass(EndCrystal.class, endSpike.getTopBoundingBox()).size();
        }
        LOGGER.debug("Found {} end crystals still alive", (Object)this.crystalsAlive);
    }

    public void setDragonKilled(EnderDragon enderDragon) {
        if (enderDragon.getUUID().equals(this.dragonUUID)) {
            this.dragonEvent.setPercent(0.0f);
            this.dragonEvent.setVisible(false);
            this.spawnExitPortal(true);
            this.spawnNewGateway();
            if (!this.previouslyKilled) {
                this.level.setBlockAndUpdate(this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION), Blocks.DRAGON_EGG.defaultBlockState());
            }
            this.previouslyKilled = true;
            this.dragonKilled = true;
        }
    }

    private void spawnNewGateway() {
        if (this.gateways.isEmpty()) {
            return;
        }
        int n = this.gateways.remove(this.gateways.size() - 1);
        int n2 = Mth.floor(96.0 * Math.cos(2.0 * (-3.141592653589793 + 0.15707963267948966 * (double)n)));
        int n3 = Mth.floor(96.0 * Math.sin(2.0 * (-3.141592653589793 + 0.15707963267948966 * (double)n)));
        this.spawnNewGateway(new BlockPos(n2, 75, n3));
    }

    private void spawnNewGateway(BlockPos blockPos) {
        this.level.levelEvent(3000, blockPos, 0);
        Features.END_GATEWAY_DELAYED.place(this.level, this.level.getChunkSource().getGenerator(), new Random(), blockPos);
    }

    private void spawnExitPortal(boolean bl) {
        EndPodiumFeature endPodiumFeature = new EndPodiumFeature(bl);
        if (this.portalLocation == null) {
            this.portalLocation = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION).below();
            while (this.level.getBlockState(this.portalLocation).is(Blocks.BEDROCK) && this.portalLocation.getY() > this.level.getSeaLevel()) {
                this.portalLocation = this.portalLocation.below();
            }
        }
        endPodiumFeature.configured(FeatureConfiguration.NONE).place(this.level, this.level.getChunkSource().getGenerator(), new Random(), this.portalLocation);
    }

    private EnderDragon createNewDragon() {
        this.level.getChunkAt(new BlockPos(0, 128, 0));
        EnderDragon enderDragon = EntityType.ENDER_DRAGON.create(this.level);
        enderDragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
        enderDragon.moveTo(0.0, 128.0, 0.0, this.level.random.nextFloat() * 360.0f, 0.0f);
        this.level.addFreshEntity(enderDragon);
        this.dragonUUID = enderDragon.getUUID();
        return enderDragon;
    }

    public void updateDragon(EnderDragon enderDragon) {
        if (enderDragon.getUUID().equals(this.dragonUUID)) {
            this.dragonEvent.setPercent(enderDragon.getHealth() / enderDragon.getMaxHealth());
            this.ticksSinceDragonSeen = 0;
            if (enderDragon.hasCustomName()) {
                this.dragonEvent.setName(enderDragon.getDisplayName());
            }
        }
    }

    public int getCrystalsAlive() {
        return this.crystalsAlive;
    }

    public void onCrystalDestroyed(EndCrystal endCrystal, DamageSource damageSource) {
        if (this.respawnStage != null && this.respawnCrystals.contains(endCrystal)) {
            LOGGER.debug("Aborting respawn sequence");
            this.respawnStage = null;
            this.respawnTime = 0;
            this.resetSpikeCrystals();
            this.spawnExitPortal(true);
        } else {
            this.updateCrystalCount();
            Entity entity = this.level.getEntity(this.dragonUUID);
            if (entity instanceof EnderDragon) {
                ((EnderDragon)entity).onCrystalDestroyed(endCrystal, endCrystal.blockPosition(), damageSource);
            }
        }
    }

    public boolean hasPreviouslyKilledDragon() {
        return this.previouslyKilled;
    }

    public void tryRespawn() {
        if (this.dragonKilled && this.respawnStage == null) {
            Object object;
            BlockPos blockPos = this.portalLocation;
            if (blockPos == null) {
                LOGGER.debug("Tried to respawn, but need to find the portal first.");
                object = this.findExitPortal();
                if (object == null) {
                    LOGGER.debug("Couldn't find a portal, so we made one.");
                    this.spawnExitPortal(true);
                } else {
                    LOGGER.debug("Found the exit portal & temporarily using it.");
                }
                blockPos = this.portalLocation;
            }
            object = Lists.newArrayList();
            BlockPos blockPos2 = blockPos.above(1);
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                List<EndCrystal> list = this.level.getEntitiesOfClass(EndCrystal.class, new AABB(blockPos2.relative(direction, 2)));
                if (list.isEmpty()) {
                    return;
                }
                object.addAll(list);
            }
            LOGGER.debug("Found all crystals, respawning dragon.");
            this.respawnDragon((List<EndCrystal>)object);
        }
    }

    private void respawnDragon(List<EndCrystal> list) {
        if (this.dragonKilled && this.respawnStage == null) {
            BlockPattern.BlockPatternMatch blockPatternMatch = this.findExitPortal();
            while (blockPatternMatch != null) {
                for (int i = 0; i < this.exitPortalPattern.getWidth(); ++i) {
                    for (int j = 0; j < this.exitPortalPattern.getHeight(); ++j) {
                        for (int k = 0; k < this.exitPortalPattern.getDepth(); ++k) {
                            BlockInWorld blockInWorld = blockPatternMatch.getBlock(i, j, k);
                            if (!blockInWorld.getState().is(Blocks.BEDROCK) && !blockInWorld.getState().is(Blocks.END_PORTAL)) continue;
                            this.level.setBlockAndUpdate(blockInWorld.getPos(), Blocks.END_STONE.defaultBlockState());
                        }
                    }
                }
                blockPatternMatch = this.findExitPortal();
            }
            this.respawnStage = DragonRespawnAnimation.START;
            this.respawnTime = 0;
            this.spawnExitPortal(false);
            this.respawnCrystals = list;
        }
    }

    public void resetSpikeCrystals() {
        for (SpikeFeature.EndSpike endSpike : SpikeFeature.getSpikesForLevel(this.level)) {
            List<EndCrystal> list = this.level.getEntitiesOfClass(EndCrystal.class, endSpike.getTopBoundingBox());
            for (EndCrystal endCrystal : list) {
                endCrystal.setInvulnerable(false);
                endCrystal.setBeamTarget(null);
            }
        }
    }
}

