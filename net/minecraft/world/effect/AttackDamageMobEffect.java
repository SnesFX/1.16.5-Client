/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttackDamageMobEffect
extends MobEffect {
    protected final double multiplier;

    protected AttackDamageMobEffect(MobEffectCategory mobEffectCategory, int n, double d) {
        super(mobEffectCategory, n);
        this.multiplier = d;
    }

    @Override
    public double getAttributeModifierValue(int n, AttributeModifier attributeModifier) {
        return this.multiplier * (double)(n + 1);
    }
}

