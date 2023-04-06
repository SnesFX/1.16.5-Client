/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.npc;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WanderingTraderSpawner
implements CustomSpawner {
    private final Random random = new Random();
    private final ServerLevelData serverLevelData;
    private int tickDelay;
    private int spawnDelay;
    private int spawnChance;

    public WanderingTraderSpawner(ServerLevelData serverLevelData) {
        this.serverLevelData = serverLevelData;
        this.tickDelay = 1200;
        this.spawnDelay = serverLevelData.getWanderingTraderSpawnDelay();
        this.spawnChance = serverLevelData.getWanderingTraderSpawnChance();
        if (this.spawnDelay == 0 && this.spawnChance == 0) {
            this.spawnDelay = 24000;
            serverLevelData.setWanderingTraderSpawnDelay(this.spawnDelay);
            this.spawnChance = 25;
            serverLevelData.setWanderingTraderSpawnChance(this.spawnChance);
        }
    }

    @Override
    public int tick(ServerLevel serverLevel, boolean bl, boolean bl2) {
        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_DO_TRADER_SPAWNING)) {
            return 0;
        }
        if (--this.tickDelay > 0) {
            return 0;
        }
        this.tickDelay = 1200;
        this.spawnDelay -= 1200;
        this.serverLevelData.setWanderingTraderSpawnDelay(this.spawnDelay);
        if (this.spawnDelay > 0) {
            return 0;
        }
        this.spawnDelay = 24000;
        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            return 0;
        }
        int n = this.spawnChance;
        this.spawnChance = Mth.clamp(this.spawnChance + 25, 25, 75);
        this.serverLevelData.setWanderingTraderSpawnChance(this.spawnChance);
        if (this.random.nextInt(100) > n) {
            return 0;
        }
        if (this.spawn(serverLevel)) {
            this.spawnChance = 25;
            return 1;
        }
        return 0;
    }

    private boolean spawn(ServerLevel serverLevel) {
        ServerPlayer serverPlayer = serverLevel.getRandomPlayer();
        if (serverPlayer == null) {
            return true;
        }
        if (this.random.nextInt(10) != 0) {
            return false;
        }
        BlockPos blockPos2 = serverPlayer.blockPosition();
        int n = 48;
        PoiManager poiManager = serverLevel.getPoiManager();
        Optional<BlockPos> optional = poiManager.find(PoiType.MEETING.getPredicate(), blockPos -> true, blockPos2, 48, PoiManager.Occupancy.ANY);
        BlockPos blockPos3 = optional.orElse(blockPos2);
        BlockPos blockPos4 = this.findSpawnPositionNear(serverLevel, blockPos3, 48);
        if (blockPos4 != null && this.hasEnoughSpace(serverLevel, blockPos4)) {
            if (serverLevel.getBiomeName(blockPos4).equals(Optional.of(Biomes.THE_VOID))) {
                return false;
            }
            WanderingTrader wanderingTrader = EntityType.WANDERING_TRADER.spawn(serverLevel, null, null, null, blockPos4, MobSpawnType.EVENT, false, false);
            if (wanderingTrader != null) {
                for (int i = 0; i < 2; ++i) {
                    this.tryToSpawnLlamaFor(serverLevel, wanderingTrader, 4);
                }
                this.serverLevelData.setWanderingTraderId(wanderingTrader.getUUID());
                wanderingTrader.setDespawnDelay(48000);
                wanderingTrader.setWanderTarget(blockPos3);
                wanderingTrader.restrictTo(blockPos3, 16);
                return true;
            }
        }
        return false;
    }

    private void tryToSpawnLlamaFor(ServerLevel serverLevel, WanderingTrader wanderingTrader, int n) {
        BlockPos blockPos = this.findSpawnPositionNear(serverLevel, wanderingTrader.blockPosition(), n);
        if (blockPos == null) {
            return;
        }
        TraderLlama traderLlama = EntityType.TRADER_LLAMA.spawn(serverLevel, null, null, null, blockPos, MobSpawnType.EVENT, false, false);
        if (traderLlama == null) {
            return;
        }
        traderLlama.setLeashedTo(wanderingTrader, true);
    }

    @Nullable
    private BlockPos findSpawnPositionNear(LevelReader levelReader, BlockPos blockPos, int n) {
        BlockPos blockPos2 = null;
        for (int i = 0; i < 10; ++i) {
            int n2;
            int n3;
            int n4 = blockPos.getX() + this.random.nextInt(n * 2) - n;
            BlockPos blockPos3 = new BlockPos(n4, n2 = levelReader.getHeight(Heightmap.Types.WORLD_SURFACE, n4, n3 = blockPos.getZ() + this.random.nextInt(n * 2) - n), n3);
            if (!NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, levelReader, blockPos3, EntityType.WANDERING_TRADER)) continue;
            blockPos2 = blockPos3;
            break;
        }
        return blockPos2;
    }

    private boolean hasEnoughSpace(BlockGetter blockGetter, BlockPos blockPos) {
        for (BlockPos blockPos2 : BlockPos.betweenClosed(blockPos, blockPos.offset(1, 2, 1))) {
            if (blockGetter.getBlockState(blockPos2).getCollisionShape(blockGetter, blockPos2).isEmpty()) continue;
            return false;
        }
        return true;
    }
}

