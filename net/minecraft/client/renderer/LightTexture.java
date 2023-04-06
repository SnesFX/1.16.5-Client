/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.Float2FloatFunction
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class LightTexture
implements AutoCloseable {
    private final DynamicTexture lightTexture;
    private final NativeImage lightPixels;
    private final ResourceLocation lightTextureLocation;
    private boolean updateLightTexture;
    private float blockLightRedFlicker;
    private final GameRenderer renderer;
    private final Minecraft minecraft;

    public LightTexture(GameRenderer gameRenderer, Minecraft minecraft) {
        this.renderer = gameRenderer;
        this.minecraft = minecraft;
        this.lightTexture = new DynamicTexture(16, 16, false);
        this.lightTextureLocation = this.minecraft.getTextureManager().register("light_map", this.lightTexture);
        this.lightPixels = this.lightTexture.getPixels();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                this.lightPixels.setPixelRGBA(j, i, -1);
            }
        }
        this.lightTexture.upload();
    }

    @Override
    public void close() {
        this.lightTexture.close();
    }

    public void tick() {
        this.blockLightRedFlicker = (float)((double)this.blockLightRedFlicker + (Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
        this.blockLightRedFlicker = (float)((double)this.blockLightRedFlicker * 0.9);
        this.updateLightTexture = true;
    }

    public void turnOffLightLayer() {
        RenderSystem.activeTexture(33986);
        RenderSystem.disableTexture();
        RenderSystem.activeTexture(33984);
    }

    public void turnOnLightLayer() {
        RenderSystem.activeTexture(33986);
        RenderSystem.matrixMode(5890);
        RenderSystem.loadIdentity();
        float f = 0.00390625f;
        RenderSystem.scalef(0.00390625f, 0.00390625f, 0.00390625f);
        RenderSystem.translatef(8.0f, 8.0f, 8.0f);
        RenderSystem.matrixMode(5888);
        this.minecraft.getTextureManager().bind(this.lightTextureLocation);
        RenderSystem.texParameter(3553, 10241, 9729);
        RenderSystem.texParameter(3553, 10240, 9729);
        RenderSystem.texParameter(3553, 10242, 10496);
        RenderSystem.texParameter(3553, 10243, 10496);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableTexture();
        RenderSystem.activeTexture(33984);
    }

    public void updateLightTexture(float f) {
        if (!this.updateLightTexture) {
            return;
        }
        this.updateLightTexture = false;
        this.minecraft.getProfiler().push("lightTex");
        ClientLevel clientLevel = this.minecraft.level;
        if (clientLevel == null) {
            return;
        }
        float f2 = clientLevel.getSkyDarken(1.0f);
        float f3 = clientLevel.getSkyFlashTime() > 0 ? 1.0f : f2 * 0.95f + 0.05f;
        float f4 = this.minecraft.player.getWaterVision();
        float f5 = this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION) ? GameRenderer.getNightVisionScale(this.minecraft.player, f) : (f4 > 0.0f && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER) ? f4 : 0.0f);
        Vector3f vector3f = new Vector3f(f2, f2, 1.0f);
        vector3f.lerp(new Vector3f(1.0f, 1.0f, 1.0f), 0.35f);
        float f6 = this.blockLightRedFlicker + 1.5f;
        Vector3f vector3f2 = new Vector3f();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                float f7;
                float f8;
                float f9;
                Vector3f vector3f3;
                float f10 = this.getBrightness(clientLevel, i) * f3;
                float f11 = f7 = this.getBrightness(clientLevel, j) * f6;
                float f12 = f7 * ((f7 * 0.6f + 0.4f) * 0.6f + 0.4f);
                float f13 = f7 * (f7 * f7 * 0.6f + 0.4f);
                vector3f2.set(f11, f12, f13);
                if (clientLevel.effects().forceBrightLightmap()) {
                    vector3f2.lerp(new Vector3f(0.99f, 1.12f, 1.0f), 0.25f);
                } else {
                    Vector3f vector3f4 = vector3f.copy();
                    vector3f4.mul(f10);
                    vector3f2.add(vector3f4);
                    vector3f2.lerp(new Vector3f(0.75f, 0.75f, 0.75f), 0.04f);
                    if (this.renderer.getDarkenWorldAmount(f) > 0.0f) {
                        f8 = this.renderer.getDarkenWorldAmount(f);
                        vector3f3 = vector3f2.copy();
                        vector3f3.mul(0.7f, 0.6f, 0.6f);
                        vector3f2.lerp(vector3f3, f8);
                    }
                }
                vector3f2.clamp(0.0f, 1.0f);
                if (f5 > 0.0f && (f9 = Math.max(vector3f2.x(), Math.max(vector3f2.y(), vector3f2.z()))) < 1.0f) {
                    f8 = 1.0f / f9;
                    vector3f3 = vector3f2.copy();
                    vector3f3.mul(f8);
                    vector3f2.lerp(vector3f3, f5);
                }
                float f14 = (float)this.minecraft.options.gamma;
                Vector3f vector3f5 = vector3f2.copy();
                vector3f5.map(this::notGamma);
                vector3f2.lerp(vector3f5, f14);
                vector3f2.lerp(new Vector3f(0.75f, 0.75f, 0.75f), 0.04f);
                vector3f2.clamp(0.0f, 1.0f);
                vector3f2.mul(255.0f);
                int n = 255;
                int n2 = (int)vector3f2.x();
                int n3 = (int)vector3f2.y();
                int n4 = (int)vector3f2.z();
                this.lightPixels.setPixelRGBA(j, i, 0xFF000000 | n4 << 16 | n3 << 8 | n2);
            }
        }
        this.lightTexture.upload();
        this.minecraft.getProfiler().pop();
    }

    private float notGamma(float f) {
        float f2 = 1.0f - f;
        return 1.0f - f2 * f2 * f2 * f2;
    }

    private float getBrightness(Level level, int n) {
        return level.dimensionType().brightness(n);
    }

    public static int pack(int n, int n2) {
        return n << 4 | n2 << 20;
    }

    public static int block(int n) {
        return n >> 4 & 0xFFFF;
    }

    public static int sky(int n) {
        return n >> 20 & 0xFFFF;
    }
}

