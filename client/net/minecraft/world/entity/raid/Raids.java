/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.raid;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

public class Raids
extends SavedData {
    private final Map<Integer, Raid> raidMap = Maps.newHashMap();
    private final ServerLevel level;
    private int nextAvailableID;
    private int tick;

    public Raids(ServerLevel serverLevel) {
        super(Raids.getFileId(serverLevel.dimensionType()));
        this.level = serverLevel;
        this.nextAvailableID = 1;
        this.setDirty();
    }

    public Raid get(int n) {
        return this.raidMap.get(n);
    }

    public void tick() {
        ++this.tick;
        Iterator<Raid> iterator = this.raidMap.values().iterator();
        while (iterator.hasNext()) {
            Raid raid = iterator.next();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                raid.stop();
            }
            if (raid.isStopped()) {
                iterator.remove();
                this.setDirty();
                continue;
            }
            raid.tick();
        }
        if (this.tick % 200 == 0) {
            this.setDirty();
        }
        DebugPackets.sendRaids(this.level, this.raidMap.values());
    }

    public static boolean canJoinRaid(Raider raider, Raid raid) {
        if (raider != null && raid != null && raid.getLevel() != null) {
            return raider.isAlive() && raider.canJoinRaid() && raider.getNoActionTime() <= 2400 && raider.level.dimensionType() == raid.getLevel().dimensionType();
        }
        return false;
    }

    @Nullable
    public Raid createOrExtendRaid(ServerPlayer serverPlayer) {
        BlockPos blockPos;
        if (serverPlayer.isSpectator()) {
            return null;
        }
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
            return null;
        }
        DimensionType dimensionType = serverPlayer.level.dimensionType();
        if (!dimensionType.hasRaids()) {
            return null;
        }
        BlockPos blockPos2 = serverPlayer.blockPosition();
        List list = this.level.getPoiManager().getInRange(PoiType.ALL, blockPos2, 64, PoiManager.Occupancy.IS_OCCUPIED).collect(Collectors.toList());
        int n = 0;
        Vec3 vec3 = Vec3.ZERO;
        for (PoiRecord poiRecord : list) {
            BlockPos blockPos3 = poiRecord.getPos();
            vec3 = vec3.add(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ());
            ++n;
        }
        if (n > 0) {
            vec3 = vec3.scale(1.0 / (double)n);
            blockPos = new BlockPos(vec3);
        } else {
            blockPos = blockPos2;
        }
        Raid raid = this.getOrCreateRaid(serverPlayer.getLevel(), blockPos);
        boolean bl = false;
        if (!raid.isStarted()) {
            if (!this.raidMap.containsKey(raid.getId())) {
                this.raidMap.put(raid.getId(), raid);
            }
            bl = true;
        } else if (raid.getBadOmenLevel() < raid.getMaxBadOmenLevel()) {
            bl = true;
        } else {
            serverPlayer.removeEffect(MobEffects.BAD_OMEN);
            serverPlayer.connection.send(new ClientboundEntityEventPacket(serverPlayer, 43));
        }
        if (bl) {
            raid.absorbBadOmen(serverPlayer);
            serverPlayer.connection.send(new ClientboundEntityEventPacket(serverPlayer, 43));
            if (!raid.hasFirstWaveSpawned()) {
                serverPlayer.awardStat(Stats.RAID_TRIGGER);
                CriteriaTriggers.BAD_OMEN.trigger(serverPlayer);
            }
        }
        this.setDirty();
        return raid;
    }

    private Raid getOrCreateRaid(ServerLevel serverLevel, BlockPos blockPos) {
        Raid raid = serverLevel.getRaidAt(blockPos);
        return raid != null ? raid : new Raid(this.getUniqueId(), serverLevel, blockPos);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        this.nextAvailableID = compoundTag.getInt("NextAvailableID");
        this.tick = compoundTag.getInt("Tick");
        ListTag listTag = compoundTag.getList("Raids", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag2 = listTag.getCompound(i);
            Raid raid = new Raid(this.level, compoundTag2);
            this.raidMap.put(raid.getId(), raid);
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putInt("NextAvailableID", this.nextAvailableID);
        compoundTag.putInt("Tick", this.tick);
        ListTag listTag = new ListTag();
        for (Raid raid : this.raidMap.values()) {
            CompoundTag compoundTag2 = new CompoundTag();
            raid.save(compoundTag2);
            listTag.add(compoundTag2);
        }
        compoundTag.put("Raids", listTag);
        return compoundTag;
    }

    public static String getFileId(DimensionType dimensionType) {
        return "raids" + dimensionType.getFileSuffix();
    }

    private int getUniqueId() {
        return ++this.nextAvailableID;
    }

    @Nullable
    public Raid getNearbyRaid(BlockPos blockPos, int n) {
        Raid raid = null;
        double d = n;
        for (Raid raid2 : this.raidMap.values()) {
            double d2 = raid2.getCenter().distSqr(blockPos);
            if (!raid2.isActive() || !(d2 < d)) continue;
            raid = raid2;
            d = d2;
        }
        return raid;
    }
}

