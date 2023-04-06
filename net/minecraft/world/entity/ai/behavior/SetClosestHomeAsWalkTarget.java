/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  it.unimi.dsi.fastutil.longs.Long2LongMap
 *  it.unimi.dsi.fastutil.longs.Long2LongMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;

public class SetClosestHomeAsWalkTarget
extends Behavior<LivingEntity> {
    private final float speedModifier;
    private final Long2LongMap batchCache = new Long2LongOpenHashMap();
    private int triedCount;
    private long lastUpdate;

    public SetClosestHomeAsWalkTarget(float f) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.HOME, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
        this.speedModifier = f;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
        if (serverLevel.getGameTime() - this.lastUpdate < 20L) {
            return false;
        }
        PathfinderMob pathfinderMob = (PathfinderMob)livingEntity;
        PoiManager poiManager = serverLevel.getPoiManager();
        Optional<BlockPos> optional = poiManager.findClosest(PoiType.HOME.getPredicate(), livingEntity.blockPosition(), 48, PoiManager.Occupancy.ANY);
        return optional.isPresent() && !(optional.get().distSqr(pathfinderMob.blockPosition()) <= 4.0);
    }

    @Override
    protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        this.triedCount = 0;
        this.lastUpdate = serverLevel.getGameTime() + (long)serverLevel.getRandom().nextInt(20);
        PathfinderMob pathfinderMob = (PathfinderMob)livingEntity;
        PoiManager poiManager = serverLevel.getPoiManager();
        Predicate<BlockPos> predicate = blockPos -> {
            long l = blockPos.asLong();
            if (this.batchCache.containsKey(l)) {
                return false;
            }
            if (++this.triedCount >= 5) {
                return false;
            }
            this.batchCache.put(l, this.lastUpdate + 40L);
            return true;
        };
        Stream<BlockPos> stream = poiManager.findAll(PoiType.HOME.getPredicate(), predicate, livingEntity.blockPosition(), 48, PoiManager.Occupancy.ANY);
        Path path = pathfinderMob.getNavigation().createPath(stream, PoiType.HOME.getValidRange());
        if (path != null && path.canReach()) {
            BlockPos blockPos2 = path.getTarget();
            Optional<PoiType> optional = poiManager.getType(blockPos2);
            if (optional.isPresent()) {
                livingEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(blockPos2, this.speedModifier, 1));
                DebugPackets.sendPoiTicketCountPacket(serverLevel, blockPos2);
            }
        } else if (this.triedCount < 5) {
            this.batchCache.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.lastUpdate);
        }
    }
}

