/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.levelgen;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;

public class PatrolSpawner
implements CustomSpawner {
    private int nextTick;

    @Override
    public int tick(ServerLevel serverLevel, boolean bl, boolean bl2) {
        if (!bl) {
            return 0;
        }
        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_DO_PATROL_SPAWNING)) {
            return 0;
        }
        Random random = serverLevel.random;
        --this.nextTick;
        if (this.nextTick > 0) {
            return 0;
        }
        this.nextTick += 12000 + random.nextInt(1200);
        long l = serverLevel.getDayTime() / 24000L;
        if (l < 5L || !serverLevel.isDay()) {
            return 0;
        }
        if (random.nextInt(5) != 0) {
            return 0;
        }
        int n = serverLevel.players().size();
        if (n < 1) {
            return 0;
        }
        Player player = serverLevel.players().get(random.nextInt(n));
        if (player.isSpectator()) {
            return 0;
        }
        if (serverLevel.isCloseToVillage(player.blockPosition(), 2)) {
            return 0;
        }
        int n2 = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        int n3 = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        BlockPos.MutableBlockPos mutableBlockPos = player.blockPosition().mutable().move(n2, 0, n3);
        if (!serverLevel.hasChunksAt(mutableBlockPos.getX() - 10, mutableBlockPos.getY() - 10, mutableBlockPos.getZ() - 10, mutableBlockPos.getX() + 10, mutableBlockPos.getY() + 10, mutableBlockPos.getZ() + 10)) {
            return 0;
        }
        Biome biome = serverLevel.getBiome(mutableBlockPos);
        Biome.BiomeCategory biomeCategory = biome.getBiomeCategory();
        if (biomeCategory == Biome.BiomeCategory.MUSHROOM) {
            return 0;
        }
        int n4 = 0;
        int n5 = (int)Math.ceil(serverLevel.getCurrentDifficultyAt(mutableBlockPos).getEffectiveDifficulty()) + 1;
        for (int i = 0; i < n5; ++i) {
            ++n4;
            mutableBlockPos.setY(serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutableBlockPos).getY());
            if (i == 0) {
                if (!this.spawnPatrolMember(serverLevel, mutableBlockPos, random, true)) {
                    break;
                }
            } else {
                this.spawnPatrolMember(serverLevel, mutableBlockPos, random, false);
            }
            mutableBlockPos.setX(mutableBlockPos.getX() + random.nextInt(5) - random.nextInt(5));
            mutableBlockPos.setZ(mutableBlockPos.getZ() + random.nextInt(5) - random.nextInt(5));
        }
        return n4;
    }

    private boolean spawnPatrolMember(ServerLevel serverLevel, BlockPos blockPos, Random random, boolean bl) {
        BlockState blockState = serverLevel.getBlockState(blockPos);
        if (!NaturalSpawner.isValidEmptySpawnBlock(serverLevel, blockPos, blockState, blockState.getFluidState(), EntityType.PILLAGER)) {
            return false;
        }
        if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, serverLevel, MobSpawnType.PATROL, blockPos, random)) {
            return false;
        }
        PatrollingMonster patrollingMonster = EntityType.PILLAGER.create(serverLevel);
        if (patrollingMonster != null) {
            if (bl) {
                patrollingMonster.setPatrolLeader(true);
                patrollingMonster.findPatrolTarget();
            }
            patrollingMonster.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            patrollingMonster.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockPos), MobSpawnType.PATROL, null, null);
            serverLevel.addFreshEntityWithPassengers(patrollingMonster);
            return true;
        }
        return false;
    }
}

