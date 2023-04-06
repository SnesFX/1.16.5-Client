/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public class AcquirePoi
extends Behavior<PathfinderMob> {
    private final PoiType poiType;
    private final MemoryModuleType<GlobalPos> memoryToAcquire;
    private final boolean onlyIfAdult;
    private final Optional<Byte> onPoiAcquisitionEvent;
    private long nextScheduledStart;
    private final Long2ObjectMap<JitteredLinearRetry> batchCache = new Long2ObjectOpenHashMap();

    public AcquirePoi(PoiType poiType, MemoryModuleType<GlobalPos> memoryModuleType, MemoryModuleType<GlobalPos> memoryModuleType2, boolean bl, Optional<Byte> optional) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)AcquirePoi.constructEntryConditionMap(memoryModuleType, memoryModuleType2));
        this.poiType = poiType;
        this.memoryToAcquire = memoryModuleType2;
        this.onlyIfAdult = bl;
        this.onPoiAcquisitionEvent = optional;
    }

    public AcquirePoi(PoiType poiType, MemoryModuleType<GlobalPos> memoryModuleType, boolean bl, Optional<Byte> optional) {
        this(poiType, memoryModuleType, memoryModuleType, bl, optional);
    }

    private static ImmutableMap<MemoryModuleType<?>, MemoryStatus> constructEntryConditionMap(MemoryModuleType<GlobalPos> memoryModuleType, MemoryModuleType<GlobalPos> memoryModuleType2) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(memoryModuleType, (Object)MemoryStatus.VALUE_ABSENT);
        if (memoryModuleType2 != memoryModuleType) {
            builder.put(memoryModuleType2, (Object)MemoryStatus.VALUE_ABSENT);
        }
        return builder.build();
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, PathfinderMob pathfinderMob) {
        if (this.onlyIfAdult && pathfinderMob.isBaby()) {
            return false;
        }
        if (this.nextScheduledStart == 0L) {
            this.nextScheduledStart = pathfinderMob.level.getGameTime() + (long)serverLevel.random.nextInt(20);
            return false;
        }
        return serverLevel.getGameTime() >= this.nextScheduledStart;
    }

    @Override
    protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long l) {
        this.nextScheduledStart = l + 20L + (long)serverLevel.getRandom().nextInt(20);
        PoiManager poiManager = serverLevel.getPoiManager();
        this.batchCache.long2ObjectEntrySet().removeIf(entry -> !((JitteredLinearRetry)entry.getValue()).isStillValid(l));
        Predicate<BlockPos> predicate = blockPos -> {
            JitteredLinearRetry jitteredLinearRetry = (JitteredLinearRetry)this.batchCache.get(blockPos.asLong());
            if (jitteredLinearRetry == null) {
                return true;
            }
            if (!jitteredLinearRetry.shouldRetry(l)) {
                return false;
            }
            jitteredLinearRetry.markAttempt(l);
            return true;
        };
        Set<BlockPos> set = poiManager.findAllClosestFirst(this.poiType.getPredicate(), predicate, pathfinderMob.blockPosition(), 48, PoiManager.Occupancy.HAS_SPACE).limit(5L).collect(Collectors.toSet());
        Path path = pathfinderMob.getNavigation().createPath(set, this.poiType.getValidRange());
        if (path != null && path.canReach()) {
            BlockPos blockPos2 = path.getTarget();
            poiManager.getType(blockPos2).ifPresent(poiType -> {
                poiManager.take(this.poiType.getPredicate(), blockPos2 -> blockPos2.equals(blockPos2), blockPos2, 1);
                pathfinderMob.getBrain().setMemory(this.memoryToAcquire, GlobalPos.of(serverLevel.dimension(), blockPos2));
                this.onPoiAcquisitionEvent.ifPresent(by -> serverLevel.broadcastEntityEvent(pathfinderMob, (byte)by));
                this.batchCache.clear();
                DebugPackets.sendPoiTicketCountPacket(serverLevel, blockPos2);
            });
        } else {
            for (BlockPos blockPos3 : set) {
                this.batchCache.computeIfAbsent(blockPos3.asLong(), l2 -> new JitteredLinearRetry(pathfinderMob.level.random, l));
            }
        }
    }

    static class JitteredLinearRetry {
        private final Random random;
        private long previousAttemptTimestamp;
        private long nextScheduledAttemptTimestamp;
        private int currentDelay;

        JitteredLinearRetry(Random random, long l) {
            this.random = random;
            this.markAttempt(l);
        }

        public void markAttempt(long l) {
            this.previousAttemptTimestamp = l;
            int n = this.currentDelay + this.random.nextInt(40) + 40;
            this.currentDelay = Math.min(n, 400);
            this.nextScheduledAttemptTimestamp = l + (long)this.currentDelay;
        }

        public boolean isStillValid(long l) {
            return l - this.previousAttemptTimestamp < 400L;
        }

        public boolean shouldRetry(long l) {
            return l >= this.nextScheduledAttemptTimestamp;
        }

        public String toString() {
            return "RetryMarker{, previousAttemptAt=" + this.previousAttemptTimestamp + ", nextScheduledAttemptAt=" + this.nextScheduledAttemptTimestamp + ", currentDelay=" + this.currentDelay + '}';
        }
    }

}

