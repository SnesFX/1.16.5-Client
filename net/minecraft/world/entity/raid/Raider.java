/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.raid;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.PathfindToRaidGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class Raider
extends PatrollingMonster {
    protected static final EntityDataAccessor<Boolean> IS_CELEBRATING = SynchedEntityData.defineId(Raider.class, EntityDataSerializers.BOOLEAN);
    private static final Predicate<ItemEntity> ALLOWED_ITEMS = itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && ItemStack.matches(itemEntity.getItem(), Raid.getLeaderBannerInstance());
    @Nullable
    protected Raid raid;
    private int wave;
    private boolean canJoinRaid;
    private int ticksOutsideRaid;

    protected Raider(EntityType<? extends Raider> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ObtainRaidLeaderBannerGoal(this, this));
        this.goalSelector.addGoal(3, new PathfindToRaidGoal<Raider>(this));
        this.goalSelector.addGoal(4, new RaiderMoveThroughVillageGoal(this, 1.0499999523162842, 1));
        this.goalSelector.addGoal(5, new RaiderCelebration(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CELEBRATING, false);
    }

    public abstract void applyRaidBuffs(int var1, boolean var2);

    public boolean canJoinRaid() {
        return this.canJoinRaid;
    }

    public void setCanJoinRaid(boolean bl) {
        this.canJoinRaid = bl;
    }

    @Override
    public void aiStep() {
        if (this.level instanceof ServerLevel && this.isAlive()) {
            Raid raid = this.getCurrentRaid();
            if (this.canJoinRaid()) {
                if (raid == null) {
                    Raid raid2;
                    if (this.level.getGameTime() % 20L == 0L && (raid2 = ((ServerLevel)this.level).getRaidAt(this.blockPosition())) != null && Raids.canJoinRaid(this, raid2)) {
                        raid2.joinRaid(raid2.getGroupsSpawned(), this, null, true);
                    }
                } else {
                    LivingEntity livingEntity = this.getTarget();
                    if (livingEntity != null && (livingEntity.getType() == EntityType.PLAYER || livingEntity.getType() == EntityType.IRON_GOLEM)) {
                        this.noActionTime = 0;
                    }
                }
            }
        }
        super.aiStep();
    }

    @Override
    protected void updateNoActionTime() {
        this.noActionTime += 2;
    }

    @Override
    public void die(DamageSource damageSource) {
        if (this.level instanceof ServerLevel) {
            Entity entity = damageSource.getEntity();
            Raid raid = this.getCurrentRaid();
            if (raid != null) {
                if (this.isPatrolLeader()) {
                    raid.removeLeader(this.getWave());
                }
                if (entity != null && entity.getType() == EntityType.PLAYER) {
                    raid.addHeroOfTheVillage(entity);
                }
                raid.removeFromRaid(this, false);
            }
            if (this.isPatrolLeader() && raid == null && ((ServerLevel)this.level).getRaidAt(this.blockPosition()) == null) {
                Object object;
                ItemStack itemStack = this.getItemBySlot(EquipmentSlot.HEAD);
                Player player = null;
                Entity entity2 = entity;
                if (entity2 instanceof Player) {
                    player = (Player)entity2;
                } else if (entity2 instanceof Wolf) {
                    object = (Wolf)entity2;
                    LivingEntity livingEntity = ((TamableAnimal)object).getOwner();
                    if (((TamableAnimal)object).isTame() && livingEntity instanceof Player) {
                        player = (Player)livingEntity;
                    }
                }
                if (!itemStack.isEmpty() && ItemStack.matches(itemStack, Raid.getLeaderBannerInstance()) && player != null) {
                    object = player.getEffect(MobEffects.BAD_OMEN);
                    int n = 1;
                    if (object != null) {
                        n += ((MobEffectInstance)object).getAmplifier();
                        player.removeEffectNoUpdate(MobEffects.BAD_OMEN);
                    } else {
                        --n;
                    }
                    n = Mth.clamp(n, 0, 4);
                    MobEffectInstance mobEffectInstance = new MobEffectInstance(MobEffects.BAD_OMEN, 120000, n, false, false, true);
                    if (!this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                        player.addEffect(mobEffectInstance);
                    }
                }
            }
        }
        super.die(damageSource);
    }

    @Override
    public boolean canJoinPatrol() {
        return !this.hasActiveRaid();
    }

    public void setCurrentRaid(@Nullable Raid raid) {
        this.raid = raid;
    }

    @Nullable
    public Raid getCurrentRaid() {
        return this.raid;
    }

    public boolean hasActiveRaid() {
        return this.getCurrentRaid() != null && this.getCurrentRaid().isActive();
    }

    public void setWave(int n) {
        this.wave = n;
    }

    public int getWave() {
        return this.wave;
    }

    public boolean isCelebrating() {
        return this.entityData.get(IS_CELEBRATING);
    }

    public void setCelebrating(boolean bl) {
        this.entityData.set(IS_CELEBRATING, bl);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Wave", this.wave);
        compoundTag.putBoolean("CanJoinRaid", this.canJoinRaid);
        if (this.raid != null) {
            compoundTag.putInt("RaidId", this.raid.getId());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.wave = compoundTag.getInt("Wave");
        this.canJoinRaid = compoundTag.getBoolean("CanJoinRaid");
        if (compoundTag.contains("RaidId", 3)) {
            if (this.level instanceof ServerLevel) {
                this.raid = ((ServerLevel)this.level).getRaids().get(compoundTag.getInt("RaidId"));
            }
            if (this.raid != null) {
                this.raid.addWaveMob(this.wave, this, false);
                if (this.isPatrolLeader()) {
                    this.raid.setLeader(this.wave, this);
                }
            }
        }
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        boolean bl;
        ItemStack itemStack = itemEntity.getItem();
        boolean bl2 = bl = this.hasActiveRaid() && this.getCurrentRaid().getLeader(this.getWave()) != null;
        if (this.hasActiveRaid() && !bl && ItemStack.matches(itemStack, Raid.getLeaderBannerInstance())) {
            EquipmentSlot equipmentSlot = EquipmentSlot.HEAD;
            ItemStack itemStack2 = this.getItemBySlot(equipmentSlot);
            double d = this.getEquipmentDropChance(equipmentSlot);
            if (!itemStack2.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1f, 0.0f) < d) {
                this.spawnAtLocation(itemStack2);
            }
            this.onItemPickup(itemEntity);
            this.setItemSlot(equipmentSlot, itemStack);
            this.take(itemEntity, itemStack.getCount());
            itemEntity.remove();
            this.getCurrentRaid().setLeader(this.getWave(), this);
            this.setPatrolLeader(true);
        } else {
            super.pickUpItem(itemEntity);
        }
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        if (this.getCurrentRaid() == null) {
            return super.removeWhenFarAway(d);
        }
        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.getCurrentRaid() != null;
    }

    public int getTicksOutsideRaid() {
        return this.ticksOutsideRaid;
    }

    public void setTicksOutsideRaid(int n) {
        this.ticksOutsideRaid = n;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.hasActiveRaid()) {
            this.getCurrentRaid().updateBossbar();
        }
        return super.hurt(damageSource, f);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.setCanJoinRaid(this.getType() != EntityType.WITCH || mobSpawnType != MobSpawnType.NATURAL);
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    public abstract SoundEvent getCelebrateSound();

    static class RaiderMoveThroughVillageGoal
    extends Goal {
        private final Raider raider;
        private final double speedModifier;
        private BlockPos poiPos;
        private final List<BlockPos> visited = Lists.newArrayList();
        private final int distanceToPoi;
        private boolean stuck;

        public RaiderMoveThroughVillageGoal(Raider raider, double d, int n) {
            this.raider = raider;
            this.speedModifier = d;
            this.distanceToPoi = n;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            this.updateVisited();
            return this.isValidRaid() && this.hasSuitablePoi() && this.raider.getTarget() == null;
        }

        private boolean isValidRaid() {
            return this.raider.hasActiveRaid() && !this.raider.getCurrentRaid().isOver();
        }

        private boolean hasSuitablePoi() {
            ServerLevel serverLevel = (ServerLevel)this.raider.level;
            BlockPos blockPos = this.raider.blockPosition();
            Optional<BlockPos> optional = serverLevel.getPoiManager().getRandom(poiType -> poiType == PoiType.HOME, this::hasNotVisited, PoiManager.Occupancy.ANY, blockPos, 48, this.raider.random);
            if (!optional.isPresent()) {
                return false;
            }
            this.poiPos = optional.get().immutable();
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.raider.getNavigation().isDone()) {
                return false;
            }
            return this.raider.getTarget() == null && !this.poiPos.closerThan(this.raider.position(), (double)(this.raider.getBbWidth() + (float)this.distanceToPoi)) && !this.stuck;
        }

        @Override
        public void stop() {
            if (this.poiPos.closerThan(this.raider.position(), (double)this.distanceToPoi)) {
                this.visited.add(this.poiPos);
            }
        }

        @Override
        public void start() {
            super.start();
            this.raider.setNoActionTime(0);
            this.raider.getNavigation().moveTo(this.poiPos.getX(), this.poiPos.getY(), this.poiPos.getZ(), this.speedModifier);
            this.stuck = false;
        }

        @Override
        public void tick() {
            if (this.raider.getNavigation().isDone()) {
                Vec3 vec3 = Vec3.atBottomCenterOf(this.poiPos);
                Vec3 vec32 = RandomPos.getPosTowards(this.raider, 16, 7, vec3, 0.3141592741012573);
                if (vec32 == null) {
                    vec32 = RandomPos.getPosTowards(this.raider, 8, 7, vec3);
                }
                if (vec32 == null) {
                    this.stuck = true;
                    return;
                }
                this.raider.getNavigation().moveTo(vec32.x, vec32.y, vec32.z, this.speedModifier);
            }
        }

        private boolean hasNotVisited(BlockPos blockPos) {
            for (BlockPos blockPos2 : this.visited) {
                if (!Objects.equals(blockPos, blockPos2)) continue;
                return false;
            }
            return true;
        }

        private void updateVisited() {
            if (this.visited.size() > 2) {
                this.visited.remove(0);
            }
        }
    }

    public class HoldGroundAttackGoal
    extends Goal {
        private final Raider mob;
        private final float hostileRadiusSqr;
        public final TargetingConditions shoutTargeting = new TargetingConditions().range(8.0).allowNonAttackable().allowInvulnerable().allowSameTeam().allowUnseeable().ignoreInvisibilityTesting();

        public HoldGroundAttackGoal(AbstractIllager abstractIllager, float f) {
            this.mob = abstractIllager;
            this.hostileRadiusSqr = f * f;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingEntity = this.mob.getLastHurtByMob();
            return this.mob.getCurrentRaid() == null && this.mob.isPatrolling() && this.mob.getTarget() != null && !this.mob.isAggressive() && (livingEntity == null || livingEntity.getType() != EntityType.PLAYER);
        }

        @Override
        public void start() {
            super.start();
            this.mob.getNavigation().stop();
            List<Raider> list = this.mob.level.getNearbyEntities(Raider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0));
            for (Raider raider : list) {
                raider.setTarget(this.mob.getTarget());
            }
        }

        @Override
        public void stop() {
            super.stop();
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity != null) {
                List<Raider> list = this.mob.level.getNearbyEntities(Raider.class, this.shoutTargeting, this.mob, this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0));
                for (Raider raider : list) {
                    raider.setTarget(livingEntity);
                    raider.setAggressive(true);
                }
                this.mob.setAggressive(true);
            }
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null) {
                return;
            }
            if (this.mob.distanceToSqr(livingEntity) > (double)this.hostileRadiusSqr) {
                this.mob.getLookControl().setLookAt(livingEntity, 30.0f, 30.0f);
                if (this.mob.random.nextInt(50) == 0) {
                    this.mob.playAmbientSound();
                }
            } else {
                this.mob.setAggressive(true);
            }
            super.tick();
        }
    }

    public class RaiderCelebration
    extends Goal {
        private final Raider mob;

        RaiderCelebration(Raider raider2) {
            this.mob = raider2;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            Raid raid = this.mob.getCurrentRaid();
            return this.mob.isAlive() && this.mob.getTarget() == null && raid != null && raid.isLoss();
        }

        @Override
        public void start() {
            this.mob.setCelebrating(true);
            super.start();
        }

        @Override
        public void stop() {
            this.mob.setCelebrating(false);
            super.stop();
        }

        @Override
        public void tick() {
            if (!this.mob.isSilent() && this.mob.random.nextInt(100) == 0) {
                Raider.this.playSound(Raider.this.getCelebrateSound(), Raider.this.getSoundVolume(), Raider.this.getVoicePitch());
            }
            if (!this.mob.isPassenger() && this.mob.random.nextInt(50) == 0) {
                this.mob.getJumpControl().jump();
            }
            super.tick();
        }
    }

    public class ObtainRaidLeaderBannerGoal<T extends Raider>
    extends Goal {
        private final T mob;

        public ObtainRaidLeaderBannerGoal(T t) {
            this.mob = t;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            List<ItemEntity> list;
            Raid raid = ((Raider)this.mob).getCurrentRaid();
            if (!((Raider)this.mob).hasActiveRaid() || ((Raider)this.mob).getCurrentRaid().isOver() || !((PatrollingMonster)this.mob).canBeLeader() || ItemStack.matches(((Mob)this.mob).getItemBySlot(EquipmentSlot.HEAD), Raid.getLeaderBannerInstance())) {
                return false;
            }
            Raider raider = raid.getLeader(((Raider)this.mob).getWave());
            if (!(raider != null && raider.isAlive() || (list = ((Raider)this.mob).level.getEntitiesOfClass(ItemEntity.class, ((Entity)this.mob).getBoundingBox().inflate(16.0, 8.0, 16.0), ALLOWED_ITEMS)).isEmpty())) {
                return ((Mob)this.mob).getNavigation().moveTo(list.get(0), 1.149999976158142);
            }
            return false;
        }

        @Override
        public void tick() {
            List<ItemEntity> list;
            if (((Mob)this.mob).getNavigation().getTargetPos().closerThan(((Entity)this.mob).position(), 1.414) && !(list = ((Raider)this.mob).level.getEntitiesOfClass(ItemEntity.class, ((Entity)this.mob).getBoundingBox().inflate(4.0, 4.0, 4.0), ALLOWED_ITEMS)).isEmpty()) {
                ((Raider)this.mob).pickUpItem(list.get(0));
            }
        }
    }

}

