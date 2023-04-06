/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class HealthBoostMobEffect
extends MobEffect {
    public HealthBoostMobEffect(MobEffectCategory mobEffectCategory, int n) {
        super(mobEffectCategory, n);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int n) {
        super.removeAttributeModifiers(livingEntity, attributeMap, n);
        if (livingEntity.getHealth() > livingEntity.getMaxHealth()) {
            livingEntity.setHealth(livingEntity.getMaxHealth());
        }
    }
}

