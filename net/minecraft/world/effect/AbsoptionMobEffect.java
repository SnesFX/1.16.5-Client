/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class AbsoptionMobEffect
extends MobEffect {
    protected AbsoptionMobEffect(MobEffectCategory mobEffectCategory, int n) {
        super(mobEffectCategory, n);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int n) {
        livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() - (float)(4 * (n + 1)));
        super.removeAttributeModifiers(livingEntity, attributeMap, n);
    }

    @Override
    public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int n) {
        livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() + (float)(4 * (n + 1)));
        super.addAttributeModifiers(livingEntity, attributeMap, n);
    }
}

