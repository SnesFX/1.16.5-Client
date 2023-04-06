/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.effect;

import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public final class MobEffectUtil {
    public static String formatDuration(MobEffectInstance mobEffectInstance, float f) {
        if (mobEffectInstance.isNoCounter()) {
            return "**:**";
        }
        int n = Mth.floor((float)mobEffectInstance.getDuration() * f);
        return StringUtil.formatTickDuration(n);
    }

    public static boolean hasDigSpeed(LivingEntity livingEntity) {
        return livingEntity.hasEffect(MobEffects.DIG_SPEED) || livingEntity.hasEffect(MobEffects.CONDUIT_POWER);
    }

    public static int getDigSpeedAmplification(LivingEntity livingEntity) {
        int n = 0;
        int n2 = 0;
        if (livingEntity.hasEffect(MobEffects.DIG_SPEED)) {
            n = livingEntity.getEffect(MobEffects.DIG_SPEED).getAmplifier();
        }
        if (livingEntity.hasEffect(MobEffects.CONDUIT_POWER)) {
            n2 = livingEntity.getEffect(MobEffects.CONDUIT_POWER).getAmplifier();
        }
        return Math.max(n, n2);
    }

    public static boolean hasWaterBreathing(LivingEntity livingEntity) {
        return livingEntity.hasEffect(MobEffects.WATER_BREATHING) || livingEntity.hasEffect(MobEffects.CONDUIT_POWER);
    }
}

