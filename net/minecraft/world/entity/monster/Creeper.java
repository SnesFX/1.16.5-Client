/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.monster;

import java.util.Collection;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class Creeper
extends Monster
implements PowerableMob {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.BOOLEAN);
    private int oldSwell;
    private int swell;
    private int maxSwell = 30;
    private int explosionRadius = 3;
    private int droppedSkulls;

    public Creeper(EntityType<? extends Creeper> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<Ocelot>(this, Ocelot.class, 6.0f, 1.0, 1.2));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<Cat>(this, Cat.class, 6.0f, 1.0, 1.2));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<Player>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public int getMaxFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        }
        return 3 + (int)(this.getHealth() - 1.0f);
    }

    @Override
    public boolean causeFallDamage(float f, float f2) {
        boolean bl = super.causeFallDamage(f, f2);
        this.swell = (int)((float)this.swell + f * 1.5f);
        if (this.swell > this.maxSwell - 5) {
            this.swell = this.maxSwell - 5;
        }
        return bl;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SWELL_DIR, -1);
        this.entityData.define(DATA_IS_POWERED, false);
        this.entityData.define(DATA_IS_IGNITED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.entityData.get(DATA_IS_POWERED).booleanValue()) {
            compoundTag.putBoolean("powered", true);
        }
        compoundTag.putShort("Fuse", (short)this.maxSwell);
        compoundTag.putByte("ExplosionRadius", (byte)this.explosionRadius);
        compoundTag.putBoolean("ignited", this.isIgnited());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.entityData.set(DATA_IS_POWERED, compoundTag.getBoolean("powered"));
        if (compoundTag.contains("Fuse", 99)) {
            this.maxSwell = compoundTag.getShort("Fuse");
        }
        if (compoundTag.contains("ExplosionRadius", 99)) {
            this.explosionRadius = compoundTag.getByte("ExplosionRadius");
        }
        if (compoundTag.getBoolean("ignited")) {
            this.ignite();
        }
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            int n;
            this.oldSwell = this.swell;
            if (this.isIgnited()) {
                this.setSwellDir(1);
            }
            if ((n = this.getSwellDir()) > 0 && this.swell == 0) {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0f, 0.5f);
            }
            this.swell += n;
            if (this.swell < 0) {
                this.swell = 0;
            }
            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
                this.explodeCreeper();
            }
        }
        super.tick();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int n, boolean bl) {
        Creeper creeper;
        super.dropCustomDeathLoot(damageSource, n, bl);
        Entity entity = damageSource.getEntity();
        if (entity != this && entity instanceof Creeper && (creeper = (Creeper)entity).canDropMobsSkull()) {
            creeper.increaseDroppedSkulls();
            this.spawnAtLocation(Items.CREEPER_HEAD);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        return true;
    }

    @Override
    public boolean isPowered() {
        return this.entityData.get(DATA_IS_POWERED);
    }

    public float getSwelling(float f) {
        return Mth.lerp(f, this.oldSwell, this.swell) / (float)(this.maxSwell - 2);
    }

    public int getSwellDir() {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    public void setSwellDir(int n) {
        this.entityData.set(DATA_SWELL_DIR, n);
    }

    @Override
    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
        super.thunderHit(serverLevel, lightningBolt);
        this.entityData.set(DATA_IS_POWERED, true);
    }

    @Override
    protected InteractionResult mobInteract(Player player2, InteractionHand interactionHand) {
        ItemStack itemStack = player2.getItemInHand(interactionHand);
        if (itemStack.getItem() == Items.FLINT_AND_STEEL) {
            this.level.playSound(player2, this.getX(), this.getY(), this.getZ(), SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0f, this.random.nextFloat() * 0.4f + 0.8f);
            if (!this.level.isClientSide) {
                this.ignite();
                itemStack.hurtAndBreak(1, player2, player -> player.broadcastBreakEvent(interactionHand));
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.mobInteract(player2, interactionHand);
    }

    private void explodeCreeper() {
        if (!this.level.isClientSide) {
            Explosion.BlockInteraction blockInteraction = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
            float f = this.isPowered() ? 2.0f : 1.0f;
            this.dead = true;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * f, blockInteraction);
            this.remove();
            this.spawnLingeringCloud();
        }
    }

    private void spawnLingeringCloud() {
        Collection<MobEffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaEffectCloud = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
            areaEffectCloud.setRadius(2.5f);
            areaEffectCloud.setRadiusOnUse(-0.5f);
            areaEffectCloud.setWaitTime(10);
            areaEffectCloud.setDuration(areaEffectCloud.getDuration() / 2);
            areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / (float)areaEffectCloud.getDuration());
            for (MobEffectInstance mobEffectInstance : collection) {
                areaEffectCloud.addEffect(new MobEffectInstance(mobEffectInstance));
            }
            this.level.addFreshEntity(areaEffectCloud);
        }
    }

    public boolean isIgnited() {
        return this.entityData.get(DATA_IS_IGNITED);
    }

    public void ignite() {
        this.entityData.set(DATA_IS_IGNITED, true);
    }

    public boolean canDropMobsSkull() {
        return this.isPowered() && this.droppedSkulls < 1;
    }

    public void increaseDroppedSkulls() {
        ++this.droppedSkulls;
    }
}

