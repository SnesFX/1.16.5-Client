/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AreaEffectCloud
extends Entity {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_WAITING = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.PARTICLE);
    private Potion potion = Potions.EMPTY;
    private final List<MobEffectInstance> effects = Lists.newArrayList();
    private final Map<Entity, Integer> victims = Maps.newHashMap();
    private int duration = 600;
    private int waitTime = 20;
    private int reapplicationDelay = 20;
    private boolean fixedColor;
    private int durationOnUse;
    private float radiusOnUse;
    private float radiusPerTick;
    private LivingEntity owner;
    private UUID ownerUUID;

    public AreaEffectCloud(EntityType<? extends AreaEffectCloud> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        this.setRadius(3.0f);
    }

    public AreaEffectCloud(Level level, double d, double d2, double d3) {
        this(EntityType.AREA_EFFECT_CLOUD, level);
        this.setPos(d, d2, d3);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_COLOR, 0);
        this.getEntityData().define(DATA_RADIUS, Float.valueOf(0.5f));
        this.getEntityData().define(DATA_WAITING, false);
        this.getEntityData().define(DATA_PARTICLE, ParticleTypes.ENTITY_EFFECT);
    }

    public void setRadius(float f) {
        if (!this.level.isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Float.valueOf(f));
        }
    }

    @Override
    public void refreshDimensions() {
        double d = this.getX();
        double d2 = this.getY();
        double d3 = this.getZ();
        super.refreshDimensions();
        this.setPos(d, d2, d3);
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS).floatValue();
    }

    public void setPotion(Potion potion) {
        this.potion = potion;
        if (!this.fixedColor) {
            this.updateColor();
        }
    }

    private void updateColor() {
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.getEntityData().set(DATA_COLOR, 0);
        } else {
            this.getEntityData().set(DATA_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
        }
    }

    public void addEffect(MobEffectInstance mobEffectInstance) {
        this.effects.add(mobEffectInstance);
        if (!this.fixedColor) {
            this.updateColor();
        }
    }

    public int getColor() {
        return this.getEntityData().get(DATA_COLOR);
    }

    public void setFixedColor(int n) {
        this.fixedColor = true;
        this.getEntityData().set(DATA_COLOR, n);
    }

    public ParticleOptions getParticle() {
        return this.getEntityData().get(DATA_PARTICLE);
    }

    public void setParticle(ParticleOptions particleOptions) {
        this.getEntityData().set(DATA_PARTICLE, particleOptions);
    }

    protected void setWaiting(boolean bl) {
        this.getEntityData().set(DATA_WAITING, bl);
    }

    public boolean isWaiting() {
        return this.getEntityData().get(DATA_WAITING);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int n) {
        this.duration = n;
    }

    @Override
    public void tick() {
        block23 : {
            float f;
            boolean bl2;
            boolean bl;
            block21 : {
                ParticleOptions particleOptions;
                block22 : {
                    super.tick();
                    bl = this.isWaiting();
                    f = this.getRadius();
                    if (!this.level.isClientSide) break block21;
                    particleOptions = this.getParticle();
                    if (!bl) break block22;
                    if (!this.random.nextBoolean()) break block23;
                    for (int i = 0; i < 2; ++i) {
                        float f2 = this.random.nextFloat() * 6.2831855f;
                        float f3 = Mth.sqrt(this.random.nextFloat()) * 0.2f;
                        float f8 = Mth.cos(f2) * f3;
                        float f9 = Mth.sin(f2) * f3;
                        if (particleOptions.getType() == ParticleTypes.ENTITY_EFFECT) {
                            int f10 = this.random.nextBoolean() ? 16777215 : this.getColor();
                            int n5 = f10 >> 16 & 0xFF;
                            int n6 = f10 >> 8 & 0xFF;
                            int n7 = f10 & 0xFF;
                            this.level.addAlwaysVisibleParticle(particleOptions, this.getX() + (double)f8, this.getY(), this.getZ() + (double)f9, (float)n5 / 255.0f, (float)n6 / 255.0f, (float)n7 / 255.0f);
                            continue;
                        }
                        this.level.addAlwaysVisibleParticle(particleOptions, this.getX() + (double)f8, this.getY(), this.getZ() + (double)f9, 0.0, 0.0, 0.0);
                    }
                    break block23;
                }
                float f6 = 3.1415927f * f * f;
                int n = 0;
                while ((float)n < f6) {
                    float f7 = this.random.nextFloat() * 6.2831855f;
                    float livingEntity = Mth.sqrt(this.random.nextFloat()) * f;
                    float d = Mth.cos(f7) * livingEntity;
                    float f2 = Mth.sin(f7) * livingEntity;
                    if (particleOptions.getType() == ParticleTypes.ENTITY_EFFECT) {
                        int d2 = this.getColor();
                        int n2 = d2 >> 16 & 0xFF;
                        int d3 = d2 >> 8 & 0xFF;
                        int n3 = d2 & 0xFF;
                        this.level.addAlwaysVisibleParticle(particleOptions, this.getX() + (double)d, this.getY(), this.getZ() + (double)f2, (float)n2 / 255.0f, (float)d3 / 255.0f, (float)n3 / 255.0f);
                    } else {
                        this.level.addAlwaysVisibleParticle(particleOptions, this.getX() + (double)d, this.getY(), this.getZ() + (double)f2, (0.5 - this.random.nextDouble()) * 0.15, 0.009999999776482582, (0.5 - this.random.nextDouble()) * 0.15);
                    }
                    ++n;
                }
                break block23;
            }
            if (this.tickCount >= this.waitTime + this.duration) {
                this.remove();
                return;
            }
            boolean bl3 = bl2 = this.tickCount < this.waitTime;
            if (bl != bl2) {
                this.setWaiting(bl2);
            }
            if (bl2) {
                return;
            }
            if (this.radiusPerTick != 0.0f) {
                if ((f += this.radiusPerTick) < 0.5f) {
                    this.remove();
                    return;
                }
                this.setRadius(f);
            }
            if (this.tickCount % 5 == 0) {
                List<LivingEntity> list;
                Object object = this.victims.entrySet().iterator();
                while (object.hasNext()) {
                    list = object.next();
                    if (this.tickCount < list.getValue()) continue;
                    object.remove();
                }
                object = Lists.newArrayList();
                for (MobEffectInstance object2 : this.potion.getEffects()) {
                    object.add(new MobEffectInstance(object2.getEffect(), object2.getDuration() / 4, object2.getAmplifier(), object2.isAmbient(), object2.isVisible()));
                }
                object.addAll(this.effects);
                if (object.isEmpty()) {
                    this.victims.clear();
                } else {
                    list = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
                    if (!list.isEmpty()) {
                        for (LivingEntity livingEntity : list) {
                            double d;
                            double d2;
                            double d3;
                            if (this.victims.containsKey(livingEntity) || !livingEntity.isAffectedByPotions() || !((d = (d2 = livingEntity.getX() - this.getX()) * d2 + (d3 = livingEntity.getZ() - this.getZ()) * d3) <= (double)(f * f))) continue;
                            this.victims.put(livingEntity, this.tickCount + this.reapplicationDelay);
                            Iterator iterator = object.iterator();
                            while (iterator.hasNext()) {
                                MobEffectInstance mobEffectInstance = (MobEffectInstance)iterator.next();
                                if (mobEffectInstance.getEffect().isInstantenous()) {
                                    mobEffectInstance.getEffect().applyInstantenousEffect(this, this.getOwner(), livingEntity, mobEffectInstance.getAmplifier(), 0.5);
                                    continue;
                                }
                                livingEntity.addEffect(new MobEffectInstance(mobEffectInstance));
                            }
                            if (this.radiusOnUse != 0.0f) {
                                if ((f += this.radiusOnUse) < 0.5f) {
                                    this.remove();
                                    return;
                                }
                                this.setRadius(f);
                            }
                            if (this.durationOnUse == 0) continue;
                            this.duration += this.durationOnUse;
                            if (this.duration > 0) continue;
                            this.remove();
                            return;
                        }
                    }
                }
            }
        }
    }

    public void setRadiusOnUse(float f) {
        this.radiusOnUse = f;
    }

    public void setRadiusPerTick(float f) {
        this.radiusPerTick = f;
    }

    public void setWaitTime(int n) {
        this.waitTime = n;
    }

    public void setOwner(@Nullable LivingEntity livingEntity) {
        this.owner = livingEntity;
        this.ownerUUID = livingEntity == null ? null : livingEntity.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        Entity entity;
        if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel && (entity = ((ServerLevel)this.level).getEntity(this.ownerUUID)) instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
        }
        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.tickCount = compoundTag.getInt("Age");
        this.duration = compoundTag.getInt("Duration");
        this.waitTime = compoundTag.getInt("WaitTime");
        this.reapplicationDelay = compoundTag.getInt("ReapplicationDelay");
        this.durationOnUse = compoundTag.getInt("DurationOnUse");
        this.radiusOnUse = compoundTag.getFloat("RadiusOnUse");
        this.radiusPerTick = compoundTag.getFloat("RadiusPerTick");
        this.setRadius(compoundTag.getFloat("Radius"));
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
        }
        if (compoundTag.contains("Particle", 8)) {
            try {
                this.setParticle(ParticleArgument.readParticle(new StringReader(compoundTag.getString("Particle"))));
            }
            catch (CommandSyntaxException commandSyntaxException) {
                LOGGER.warn("Couldn't load custom particle {}", (Object)compoundTag.getString("Particle"), (Object)commandSyntaxException);
            }
        }
        if (compoundTag.contains("Color", 99)) {
            this.setFixedColor(compoundTag.getInt("Color"));
        }
        if (compoundTag.contains("Potion", 8)) {
            this.setPotion(PotionUtils.getPotion(compoundTag));
        }
        if (compoundTag.contains("Effects", 9)) {
            ListTag listTag = compoundTag.getList("Effects", 10);
            this.effects.clear();
            for (int i = 0; i < listTag.size(); ++i) {
                MobEffectInstance mobEffectInstance = MobEffectInstance.load(listTag.getCompound(i));
                if (mobEffectInstance == null) continue;
                this.addEffect(mobEffectInstance);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("Age", this.tickCount);
        compoundTag.putInt("Duration", this.duration);
        compoundTag.putInt("WaitTime", this.waitTime);
        compoundTag.putInt("ReapplicationDelay", this.reapplicationDelay);
        compoundTag.putInt("DurationOnUse", this.durationOnUse);
        compoundTag.putFloat("RadiusOnUse", this.radiusOnUse);
        compoundTag.putFloat("RadiusPerTick", this.radiusPerTick);
        compoundTag.putFloat("Radius", this.getRadius());
        compoundTag.putString("Particle", this.getParticle().writeToString());
        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
        if (this.fixedColor) {
            compoundTag.putInt("Color", this.getColor());
        }
        if (this.potion != Potions.EMPTY && this.potion != null) {
            compoundTag.putString("Potion", Registry.POTION.getKey(this.potion).toString());
        }
        if (!this.effects.isEmpty()) {
            ListTag listTag = new ListTag();
            for (MobEffectInstance mobEffectInstance : this.effects) {
                listTag.add(mobEffectInstance.save(new CompoundTag()));
            }
            compoundTag.put("Effects", listTag);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_RADIUS.equals(entityDataAccessor)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(this.getRadius() * 2.0f, 0.5f);
    }
}

