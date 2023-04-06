/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class FogRenderer {
    private static float fogRed;
    private static float fogGreen;
    private static float fogBlue;
    private static int targetBiomeFog;
    private static int previousBiomeFog;
    private static long biomeChangedTime;

    public static void setupColor(Camera camera, float f, ClientLevel clientLevel, int n4, float f2) {
        int n5;
        FluidState fluidState = camera.getFluidInCamera();
        if (fluidState.is(FluidTags.WATER)) {
            long l = Util.getMillis();
            n5 = clientLevel.getBiome(new BlockPos(camera.getPosition())).getWaterFogColor();
            if (biomeChangedTime < 0L) {
                targetBiomeFog = n5;
                previousBiomeFog = n5;
                biomeChangedTime = l;
            }
            int n6 = targetBiomeFog >> 16 & 0xFF;
            int n7 = targetBiomeFog >> 8 & 0xFF;
            int n8 = targetBiomeFog & 0xFF;
            int n9 = previousBiomeFog >> 16 & 0xFF;
            int n10 = previousBiomeFog >> 8 & 0xFF;
            int n11 = previousBiomeFog & 0xFF;
            float f3 = Mth.clamp((float)(l - biomeChangedTime) / 5000.0f, 0.0f, 1.0f);
            float f4 = Mth.lerp(f3, n9, n6);
            float f5 = Mth.lerp(f3, n10, n7);
            float f6 = Mth.lerp(f3, n11, n8);
            fogRed = f4 / 255.0f;
            fogGreen = f5 / 255.0f;
            fogBlue = f6 / 255.0f;
            if (targetBiomeFog != n5) {
                targetBiomeFog = n5;
                previousBiomeFog = Mth.floor(f4) << 16 | Mth.floor(f5) << 8 | Mth.floor(f6);
                biomeChangedTime = l;
            }
        } else if (fluidState.is(FluidTags.LAVA)) {
            fogRed = 0.6f;
            fogGreen = 0.1f;
            fogBlue = 0.0f;
            biomeChangedTime = -1L;
        } else {
            float f7;
            float f8;
            float f9;
            float f10 = 0.25f + 0.75f * (float)n4 / 32.0f;
            f10 = 1.0f - (float)Math.pow(f10, 0.25);
            Vec3 vec3 = clientLevel.getSkyColor(camera.getBlockPosition(), f);
            float f11 = (float)vec3.x;
            float f12 = (float)vec3.y;
            float f13 = (float)vec3.z;
            float f14 = Mth.clamp(Mth.cos(clientLevel.getTimeOfDay(f) * 6.2831855f) * 2.0f + 0.5f, 0.0f, 1.0f);
            BiomeManager biomeManager = clientLevel.getBiomeManager();
            Vec3 vec32 = camera.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
            Vec3 vec33 = CubicSampler.gaussianSampleVec3(vec32, (n, n2, n3) -> clientLevel.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(biomeManager.getNoiseBiomeAtQuart(n, n2, n3).getFogColor()), f14));
            fogRed = (float)vec33.x();
            fogGreen = (float)vec33.y();
            fogBlue = (float)vec33.z();
            if (n4 >= 4) {
                float[] arrf;
                f8 = Mth.sin(clientLevel.getSunAngle(f)) > 0.0f ? -1.0f : 1.0f;
                Vector3f vector3f = new Vector3f(f8, 0.0f, 0.0f);
                f7 = camera.getLookVector().dot(vector3f);
                if (f7 < 0.0f) {
                    f7 = 0.0f;
                }
                if (f7 > 0.0f && (arrf = clientLevel.effects().getSunriseColor(clientLevel.getTimeOfDay(f), f)) != null) {
                    fogRed = fogRed * (1.0f - (f7 *= arrf[3])) + arrf[0] * f7;
                    fogGreen = fogGreen * (1.0f - f7) + arrf[1] * f7;
                    fogBlue = fogBlue * (1.0f - f7) + arrf[2] * f7;
                }
            }
            fogRed += (f11 - fogRed) * f10;
            fogGreen += (f12 - fogGreen) * f10;
            fogBlue += (f13 - fogBlue) * f10;
            f8 = clientLevel.getRainLevel(f);
            if (f8 > 0.0f) {
                float f15 = 1.0f - f8 * 0.5f;
                f7 = 1.0f - f8 * 0.4f;
                fogRed *= f15;
                fogGreen *= f15;
                fogBlue *= f7;
            }
            if ((f9 = clientLevel.getThunderLevel(f)) > 0.0f) {
                f7 = 1.0f - f9 * 0.5f;
                fogRed *= f7;
                fogGreen *= f7;
                fogBlue *= f7;
            }
            biomeChangedTime = -1L;
        }
        double d = camera.getPosition().y * clientLevel.getLevelData().getClearColorScale();
        if (camera.getEntity() instanceof LivingEntity && ((LivingEntity)camera.getEntity()).hasEffect(MobEffects.BLINDNESS)) {
            n5 = ((LivingEntity)camera.getEntity()).getEffect(MobEffects.BLINDNESS).getDuration();
            d = n5 < 20 ? (d *= (double)(1.0f - (float)n5 / 20.0f)) : 0.0;
        }
        if (d < 1.0 && !fluidState.is(FluidTags.LAVA)) {
            if (d < 0.0) {
                d = 0.0;
            }
            d *= d;
            fogRed = (float)((double)fogRed * d);
            fogGreen = (float)((double)fogGreen * d);
            fogBlue = (float)((double)fogBlue * d);
        }
        if (f2 > 0.0f) {
            fogRed = fogRed * (1.0f - f2) + fogRed * 0.7f * f2;
            fogGreen = fogGreen * (1.0f - f2) + fogGreen * 0.6f * f2;
            fogBlue = fogBlue * (1.0f - f2) + fogBlue * 0.6f * f2;
        }
        if (fluidState.is(FluidTags.WATER)) {
            float f16 = 0.0f;
            if (camera.getEntity() instanceof LocalPlayer) {
                LocalPlayer localPlayer = (LocalPlayer)camera.getEntity();
                f16 = localPlayer.getWaterVision();
            }
            float f17 = Math.min(1.0f / fogRed, Math.min(1.0f / fogGreen, 1.0f / fogBlue));
            fogRed = fogRed * (1.0f - f16) + fogRed * f17 * f16;
            fogGreen = fogGreen * (1.0f - f16) + fogGreen * f17 * f16;
            fogBlue = fogBlue * (1.0f - f16) + fogBlue * f17 * f16;
        } else if (camera.getEntity() instanceof LivingEntity && ((LivingEntity)camera.getEntity()).hasEffect(MobEffects.NIGHT_VISION)) {
            float f18 = GameRenderer.getNightVisionScale((LivingEntity)camera.getEntity(), f);
            float f19 = Math.min(1.0f / fogRed, Math.min(1.0f / fogGreen, 1.0f / fogBlue));
            fogRed = fogRed * (1.0f - f18) + fogRed * f19 * f18;
            fogGreen = fogGreen * (1.0f - f18) + fogGreen * f19 * f18;
            fogBlue = fogBlue * (1.0f - f18) + fogBlue * f19 * f18;
        }
        RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0f);
    }

    public static void setupNoFog() {
        RenderSystem.fogDensity(0.0f);
        RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
    }

    public static void setupFog(Camera camera, FogMode fogMode, float f, boolean bl) {
        FluidState fluidState = camera.getFluidInCamera();
        Entity entity = camera.getEntity();
        if (fluidState.is(FluidTags.WATER)) {
            float f2 = 1.0f;
            f2 = 0.05f;
            if (entity instanceof LocalPlayer) {
                LocalPlayer localPlayer = (LocalPlayer)entity;
                f2 -= localPlayer.getWaterVision() * localPlayer.getWaterVision() * 0.03f;
                Biome biome = localPlayer.level.getBiome(localPlayer.blockPosition());
                if (biome.getBiomeCategory() == Biome.BiomeCategory.SWAMP) {
                    f2 += 0.005f;
                }
            }
            RenderSystem.fogDensity(f2);
            RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
        } else {
            float f3;
            float f4;
            if (fluidState.is(FluidTags.LAVA)) {
                if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    f3 = 0.0f;
                    f4 = 3.0f;
                } else {
                    f3 = 0.25f;
                    f4 = 1.0f;
                }
            } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(MobEffects.BLINDNESS)) {
                int n = ((LivingEntity)entity).getEffect(MobEffects.BLINDNESS).getDuration();
                float f5 = Mth.lerp(Math.min(1.0f, (float)n / 20.0f), f, 5.0f);
                if (fogMode == FogMode.FOG_SKY) {
                    f3 = 0.0f;
                    f4 = f5 * 0.8f;
                } else {
                    f3 = f5 * 0.25f;
                    f4 = f5;
                }
            } else if (bl) {
                f3 = f * 0.05f;
                f4 = Math.min(f, 192.0f) * 0.5f;
            } else if (fogMode == FogMode.FOG_SKY) {
                f3 = 0.0f;
                f4 = f;
            } else {
                f3 = f * 0.75f;
                f4 = f;
            }
            RenderSystem.fogStart(f3);
            RenderSystem.fogEnd(f4);
            RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
            RenderSystem.setupNvFogDistance();
        }
    }

    public static void levelFogColor() {
        RenderSystem.fog(2918, fogRed, fogGreen, fogBlue, 1.0f);
    }

    static {
        targetBiomeFog = -1;
        previousBiomeFog = -1;
        biomeChangedTime = -1L;
    }

    public static enum FogMode {
        FOG_SKY,
        FOG_TERRAIN;
        
    }

}

