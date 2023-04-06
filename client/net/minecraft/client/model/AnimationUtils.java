/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public class AnimationUtils {
    public static void animateCrossbowHold(ModelPart modelPart, ModelPart modelPart2, ModelPart modelPart3, boolean bl) {
        ModelPart modelPart4 = bl ? modelPart : modelPart2;
        ModelPart modelPart5 = bl ? modelPart2 : modelPart;
        modelPart4.yRot = (bl ? -0.3f : 0.3f) + modelPart3.yRot;
        modelPart5.yRot = (bl ? 0.6f : -0.6f) + modelPart3.yRot;
        modelPart4.xRot = -1.5707964f + modelPart3.xRot + 0.1f;
        modelPart5.xRot = -1.5f + modelPart3.xRot;
    }

    public static void animateCrossbowCharge(ModelPart modelPart, ModelPart modelPart2, LivingEntity livingEntity, boolean bl) {
        ModelPart modelPart3 = bl ? modelPart : modelPart2;
        ModelPart modelPart4 = bl ? modelPart2 : modelPart;
        modelPart3.yRot = bl ? -0.8f : 0.8f;
        modelPart4.xRot = modelPart3.xRot = -0.97079635f;
        float f = CrossbowItem.getChargeDuration(livingEntity.getUseItem());
        float f2 = Mth.clamp((float)livingEntity.getTicksUsingItem(), 0.0f, f);
        float f3 = f2 / f;
        modelPart4.yRot = Mth.lerp(f3, 0.4f, 0.85f) * (float)(bl ? 1 : -1);
        modelPart4.xRot = Mth.lerp(f3, modelPart4.xRot, -1.5707964f);
    }

    public static <T extends Mob> void swingWeaponDown(ModelPart modelPart, ModelPart modelPart2, T t, float f, float f2) {
        float f3 = Mth.sin(f * 3.1415927f);
        float f4 = Mth.sin((1.0f - (1.0f - f) * (1.0f - f)) * 3.1415927f);
        modelPart.zRot = 0.0f;
        modelPart2.zRot = 0.0f;
        modelPart.yRot = 0.15707964f;
        modelPart2.yRot = -0.15707964f;
        if (((Mob)t).getMainArm() == HumanoidArm.RIGHT) {
            modelPart.xRot = -1.8849558f + Mth.cos(f2 * 0.09f) * 0.15f;
            modelPart2.xRot = -0.0f + Mth.cos(f2 * 0.19f) * 0.5f;
            modelPart.xRot += f3 * 2.2f - f4 * 0.4f;
            modelPart2.xRot += f3 * 1.2f - f4 * 0.4f;
        } else {
            modelPart.xRot = -0.0f + Mth.cos(f2 * 0.19f) * 0.5f;
            modelPart2.xRot = -1.8849558f + Mth.cos(f2 * 0.09f) * 0.15f;
            modelPart.xRot += f3 * 1.2f - f4 * 0.4f;
            modelPart2.xRot += f3 * 2.2f - f4 * 0.4f;
        }
        AnimationUtils.bobArms(modelPart, modelPart2, f2);
    }

    public static void bobArms(ModelPart modelPart, ModelPart modelPart2, float f) {
        modelPart.zRot += Mth.cos(f * 0.09f) * 0.05f + 0.05f;
        modelPart2.zRot -= Mth.cos(f * 0.09f) * 0.05f + 0.05f;
        modelPart.xRot += Mth.sin(f * 0.067f) * 0.05f;
        modelPart2.xRot -= Mth.sin(f * 0.067f) * 0.05f;
    }

    public static void animateZombieArms(ModelPart modelPart, ModelPart modelPart2, boolean bl, float f, float f2) {
        float f3;
        float f4 = Mth.sin(f * 3.1415927f);
        float f5 = Mth.sin((1.0f - (1.0f - f) * (1.0f - f)) * 3.1415927f);
        modelPart2.zRot = 0.0f;
        modelPart.zRot = 0.0f;
        modelPart2.yRot = -(0.1f - f4 * 0.6f);
        modelPart.yRot = 0.1f - f4 * 0.6f;
        modelPart2.xRot = f3 = -3.1415927f / (bl ? 1.5f : 2.25f);
        modelPart.xRot = f3;
        modelPart2.xRot += f4 * 1.2f - f5 * 0.4f;
        modelPart.xRot += f4 * 1.2f - f5 * 0.4f;
        AnimationUtils.bobArms(modelPart2, modelPart, f2);
    }
}

