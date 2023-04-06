/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.world.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;

public class MobEffect {
    private final Map<Attribute, AttributeModifier> attributeModifiers = Maps.newHashMap();
    private final MobEffectCategory category;
    private final int color;
    @Nullable
    private String descriptionId;

    @Nullable
    public static MobEffect byId(int n) {
        return (MobEffect)Registry.MOB_EFFECT.byId(n);
    }

    public static int getId(MobEffect mobEffect) {
        return Registry.MOB_EFFECT.getId(mobEffect);
    }

    protected MobEffect(MobEffectCategory mobEffectCategory, int n) {
        this.category = mobEffectCategory;
        this.color = n;
    }

    public void applyEffectTick(LivingEntity livingEntity, int n) {
        if (this == MobEffects.REGENERATION) {
            if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
                livingEntity.heal(1.0f);
            }
        } else if (this == MobEffects.POISON) {
            if (livingEntity.getHealth() > 1.0f) {
                livingEntity.hurt(DamageSource.MAGIC, 1.0f);
            }
        } else if (this == MobEffects.WITHER) {
            livingEntity.hurt(DamageSource.WITHER, 1.0f);
        } else if (this == MobEffects.HUNGER && livingEntity instanceof Player) {
            ((Player)livingEntity).causeFoodExhaustion(0.005f * (float)(n + 1));
        } else if (this == MobEffects.SATURATION && livingEntity instanceof Player) {
            if (!livingEntity.level.isClientSide) {
                ((Player)livingEntity).getFoodData().eat(n + 1, 1.0f);
            }
        } else if (this == MobEffects.HEAL && !livingEntity.isInvertedHealAndHarm() || this == MobEffects.HARM && livingEntity.isInvertedHealAndHarm()) {
            livingEntity.heal(Math.max(4 << n, 0));
        } else if (this == MobEffects.HARM && !livingEntity.isInvertedHealAndHarm() || this == MobEffects.HEAL && livingEntity.isInvertedHealAndHarm()) {
            livingEntity.hurt(DamageSource.MAGIC, 6 << n);
        }
    }

    public void applyInstantenousEffect(@Nullable Entity entity, @Nullable Entity entity2, LivingEntity livingEntity, int n, double d) {
        if (this == MobEffects.HEAL && !livingEntity.isInvertedHealAndHarm() || this == MobEffects.HARM && livingEntity.isInvertedHealAndHarm()) {
            int n2 = (int)(d * (double)(4 << n) + 0.5);
            livingEntity.heal(n2);
        } else if (this == MobEffects.HARM && !livingEntity.isInvertedHealAndHarm() || this == MobEffects.HEAL && livingEntity.isInvertedHealAndHarm()) {
            int n3 = (int)(d * (double)(6 << n) + 0.5);
            if (entity == null) {
                livingEntity.hurt(DamageSource.MAGIC, n3);
            } else {
                livingEntity.hurt(DamageSource.indirectMagic(entity, entity2), n3);
            }
        } else {
            this.applyEffectTick(livingEntity, n);
        }
    }

    public boolean isDurationEffectTick(int n, int n2) {
        if (this == MobEffects.REGENERATION) {
            int n3 = 50 >> n2;
            if (n3 > 0) {
                return n % n3 == 0;
            }
            return true;
        }
        if (this == MobEffects.POISON) {
            int n4 = 25 >> n2;
            if (n4 > 0) {
                return n % n4 == 0;
            }
            return true;
        }
        if (this == MobEffects.WITHER) {
            int n5 = 40 >> n2;
            if (n5 > 0) {
                return n % n5 == 0;
            }
            return true;
        }
        return this == MobEffects.HUNGER;
    }

    public boolean isInstantenous() {
        return false;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("effect", Registry.MOB_EFFECT.getKey(this));
        }
        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public Component getDisplayName() {
        return new TranslatableComponent(this.getDescriptionId());
    }

    public MobEffectCategory getCategory() {
        return this.category;
    }

    public int getColor() {
        return this.color;
    }

    public MobEffect addAttributeModifier(Attribute attribute, String string, double d, AttributeModifier.Operation operation) {
        AttributeModifier attributeModifier = new AttributeModifier(UUID.fromString(string), this::getDescriptionId, d, operation);
        this.attributeModifiers.put(attribute, attributeModifier);
        return this;
    }

    public Map<Attribute, AttributeModifier> getAttributeModifiers() {
        return this.attributeModifiers;
    }

    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int n) {
        for (Map.Entry<Attribute, AttributeModifier> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
            if (attributeInstance == null) continue;
            attributeInstance.removeModifier(entry.getValue());
        }
    }

    public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int n) {
        for (Map.Entry<Attribute, AttributeModifier> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
            if (attributeInstance == null) continue;
            AttributeModifier attributeModifier = entry.getValue();
            attributeInstance.removeModifier(attributeModifier);
            attributeInstance.addPermanentModifier(new AttributeModifier(attributeModifier.getId(), this.getDescriptionId() + " " + n, this.getAttributeModifierValue(n, attributeModifier), attributeModifier.getOperation()));
        }
    }

    public double getAttributeModifierValue(int n, AttributeModifier attributeModifier) {
        return attributeModifier.getAmount() * (double)(n + 1);
    }

    public boolean isBeneficial() {
        return this.category == MobEffectCategory.BENEFICIAL;
    }
}

