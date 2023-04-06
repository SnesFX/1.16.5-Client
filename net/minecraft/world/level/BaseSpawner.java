/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseSpawner {
    private static final Logger LOGGER = LogManager.getLogger();
    private int spawnDelay = 20;
    private final List<SpawnData> spawnPotentials = Lists.newArrayList();
    private SpawnData nextSpawnData = new SpawnData();
    private double spin;
    private double oSpin;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    @Nullable
    private Entity displayEntity;
    private int maxNearbyEntities = 6;
    private int requiredPlayerRange = 16;
    private int spawnRange = 4;

    @Nullable
    private ResourceLocation getEntityId() {
        String string = this.nextSpawnData.getTag().getString("id");
        try {
            return StringUtil.isNullOrEmpty(string) ? null : new ResourceLocation(string);
        }
        catch (ResourceLocationException resourceLocationException) {
            BlockPos blockPos = this.getPos();
            LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", (Object)string, (Object)this.getLevel().dimension().location(), (Object)blockPos.getX(), (Object)blockPos.getY(), (Object)blockPos.getZ());
            return null;
        }
    }

    public void setEntityId(EntityType<?> entityType) {
        this.nextSpawnData.getTag().putString("id", Registry.ENTITY_TYPE.getKey(entityType).toString());
    }

    private boolean isNearPlayer() {
        BlockPos blockPos = this.getPos();
        return this.getLevel().hasNearbyAlivePlayer((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, this.requiredPlayerRange);
    }

    public void tick() {
        if (!this.isNearPlayer()) {
            this.oSpin = this.spin;
            return;
        }
        Level level = this.getLevel();
        BlockPos blockPos = this.getPos();
        if (!(level instanceof ServerLevel)) {
            double d = (double)blockPos.getX() + level.random.nextDouble();
            double d2 = (double)blockPos.getY() + level.random.nextDouble();
            double d3 = (double)blockPos.getZ() + level.random.nextDouble();
            level.addParticle(ParticleTypes.SMOKE, d, d2, d3, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, d, d2, d3, 0.0, 0.0, 0.0);
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            }
            this.oSpin = this.spin;
            this.spin = (this.spin + (double)(1000.0f / ((float)this.spawnDelay + 200.0f))) % 360.0;
        } else {
            if (this.spawnDelay == -1) {
                this.delay();
            }
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
                return;
            }
            boolean bl = false;
            for (int i = 0; i < this.spawnCount; ++i) {
                double d;
                CompoundTag compoundTag = this.nextSpawnData.getTag();
                Optional<EntityType<?>> optional = EntityType.by(compoundTag);
                if (!optional.isPresent()) {
                    this.delay();
                    return;
                }
                ListTag listTag = compoundTag.getList("Pos", 6);
                int n = listTag.size();
                double d4 = n >= 1 ? listTag.getDouble(0) : (double)blockPos.getX() + (level.random.nextDouble() - level.random.nextDouble()) * (double)this.spawnRange + 0.5;
                double d5 = n >= 2 ? listTag.getDouble(1) : (double)(blockPos.getY() + level.random.nextInt(3) - 1);
                double d6 = d = n >= 3 ? listTag.getDouble(2) : (double)blockPos.getZ() + (level.random.nextDouble() - level.random.nextDouble()) * (double)this.spawnRange + 0.5;
                if (!level.noCollision(optional.get().getAABB(d4, d5, d))) continue;
                ServerLevel serverLevel = (ServerLevel)level;
                if (!SpawnPlacements.checkSpawnRules(optional.get(), serverLevel, MobSpawnType.SPAWNER, new BlockPos(d4, d5, d), level.getRandom())) continue;
                Entity entity2 = EntityType.loadEntityRecursive(compoundTag, level, entity -> {
                    entity.moveTo(d4, d5, d, entity.yRot, entity.xRot);
                    return entity;
                });
                if (entity2 == null) {
                    this.delay();
                    return;
                }
                int n2 = level.getEntitiesOfClass(entity2.getClass(), new AABB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1).inflate(this.spawnRange)).size();
                if (n2 >= this.maxNearbyEntities) {
                    this.delay();
                    return;
                }
                entity2.moveTo(entity2.getX(), entity2.getY(), entity2.getZ(), level.random.nextFloat() * 360.0f, 0.0f);
                if (entity2 instanceof Mob) {
                    Mob mob = (Mob)entity2;
                    if (!mob.checkSpawnRules(level, MobSpawnType.SPAWNER) || !mob.checkSpawnObstruction(level)) continue;
                    if (this.nextSpawnData.getTag().size() == 1 && this.nextSpawnData.getTag().contains("id", 8)) {
                        ((Mob)entity2).finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(entity2.blockPosition()), MobSpawnType.SPAWNER, null, null);
                    }
                }
                if (!serverLevel.tryAddFreshEntityWithPassengers(entity2)) {
                    this.delay();
                    return;
                }
                level.levelEvent(2004, blockPos, 0);
                if (entity2 instanceof Mob) {
                    ((Mob)entity2).spawnAnim();
                }
                bl = true;
            }
            if (bl) {
                this.delay();
            }
        }
    }

    private void delay() {
        this.spawnDelay = this.maxSpawnDelay <= this.minSpawnDelay ? this.minSpawnDelay : this.minSpawnDelay + this.getLevel().random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        if (!this.spawnPotentials.isEmpty()) {
            this.setNextSpawnData(WeighedRandom.getRandomItem(this.getLevel().random, this.spawnPotentials));
        }
        this.broadcastEvent(1);
    }

    public void load(CompoundTag compoundTag) {
        this.spawnDelay = compoundTag.getShort("Delay");
        this.spawnPotentials.clear();
        if (compoundTag.contains("SpawnPotentials", 9)) {
            ListTag listTag = compoundTag.getList("SpawnPotentials", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                this.spawnPotentials.add(new SpawnData(listTag.getCompound(i)));
            }
        }
        if (compoundTag.contains("SpawnData", 10)) {
            this.setNextSpawnData(new SpawnData(1, compoundTag.getCompound("SpawnData")));
        } else if (!this.spawnPotentials.isEmpty()) {
            this.setNextSpawnData(WeighedRandom.getRandomItem(this.getLevel().random, this.spawnPotentials));
        }
        if (compoundTag.contains("MinSpawnDelay", 99)) {
            this.minSpawnDelay = compoundTag.getShort("MinSpawnDelay");
            this.maxSpawnDelay = compoundTag.getShort("MaxSpawnDelay");
            this.spawnCount = compoundTag.getShort("SpawnCount");
        }
        if (compoundTag.contains("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = compoundTag.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = compoundTag.getShort("RequiredPlayerRange");
        }
        if (compoundTag.contains("SpawnRange", 99)) {
            this.spawnRange = compoundTag.getShort("SpawnRange");
        }
        if (this.getLevel() != null) {
            this.displayEntity = null;
        }
    }

    public CompoundTag save(CompoundTag compoundTag) {
        ResourceLocation resourceLocation = this.getEntityId();
        if (resourceLocation == null) {
            return compoundTag;
        }
        compoundTag.putShort("Delay", (short)this.spawnDelay);
        compoundTag.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        compoundTag.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        compoundTag.putShort("SpawnCount", (short)this.spawnCount);
        compoundTag.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        compoundTag.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        compoundTag.putShort("SpawnRange", (short)this.spawnRange);
        compoundTag.put("SpawnData", this.nextSpawnData.getTag().copy());
        ListTag listTag = new ListTag();
        if (this.spawnPotentials.isEmpty()) {
            listTag.add(this.nextSpawnData.save());
        } else {
            for (SpawnData spawnData : this.spawnPotentials) {
                listTag.add(spawnData.save());
            }
        }
        compoundTag.put("SpawnPotentials", listTag);
        return compoundTag;
    }

    @Nullable
    public Entity getOrCreateDisplayEntity() {
        if (this.displayEntity == null) {
            this.displayEntity = EntityType.loadEntityRecursive(this.nextSpawnData.getTag(), this.getLevel(), Function.identity());
            if (this.nextSpawnData.getTag().size() != 1 || !this.nextSpawnData.getTag().contains("id", 8) || this.displayEntity instanceof Mob) {
                // empty if block
            }
        }
        return this.displayEntity;
    }

    public boolean onEventTriggered(int n) {
        if (n == 1 && this.getLevel().isClientSide) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        }
        return false;
    }

    public void setNextSpawnData(SpawnData spawnData) {
        this.nextSpawnData = spawnData;
    }

    public abstract void broadcastEvent(int var1);

    public abstract Level getLevel();

    public abstract BlockPos getPos();

    public double getSpin() {
        return this.spin;
    }

    public double getoSpin() {
        return this.oSpin;
    }
}

