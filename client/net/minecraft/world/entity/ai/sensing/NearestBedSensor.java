/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  it.unimi.dsi.fastutil.longs.Long2LongMap
 *  it.unimi.dsi.fastutil.longs.Long2LongMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;

public class NearestBedSensor
extends Sensor<Mob> {
    private final Long2LongMap batchCache = new Long2LongOpenHashMap();
    private int triedCount;
    private long lastUpdate;

    public NearestBedSensor() {
        super(20);
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_BED);
    }

    @Override
    protected void doTick(ServerLevel serverLevel, Mob mob) {
        if (!mob.isBaby()) {
            return;
        }
        this.triedCount = 0;
        this.lastUpdate = serverLevel.getGameTime() + (long)serverLevel.getRandom().nextInt(20);
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
        Stream<BlockPos> stream = poiManager.findAll(PoiType.HOME.getPredicate(), predicate, mob.blockPosition(), 48, PoiManager.Occupancy.ANY);
        Path path = mob.getNavigation().createPath(stream, PoiType.HOME.getValidRange());
        if (path != null && path.canReach()) {
            BlockPos blockPos2 = path.getTarget();
            Optional<PoiType> optional = poiManager.getType(blockPos2);
            if (optional.isPresent()) {
                mob.getBrain().setMemory(MemoryModuleType.NEAREST_BED, blockPos2);
            }
        } else if (this.triedCount < 5) {
            this.batchCache.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < this.lastUpdate);
        }
    }
}

