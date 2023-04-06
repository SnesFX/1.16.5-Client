/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;

public abstract class DimensionSpecialEffects {
    private static final Object2ObjectMap<ResourceLocation, DimensionSpecialEffects> EFFECTS = (Object2ObjectMap)Util.make(new Object2ObjectArrayMap(), object2ObjectArrayMap -> {
        OverworldEffects overworldEffects = new OverworldEffects();
        object2ObjectArrayMap.defaultReturnValue((Object)overworldEffects);
        object2ObjectArrayMap.put((Object)DimensionType.OVERWORLD_EFFECTS, (Object)overworldEffects);
        object2ObjectArrayMap.put((Object)DimensionType.NETHER_EFFECTS, (Object)new NetherEffects());
        object2ObjectArrayMap.put((Object)DimensionType.END_EFFECTS, (Object)new EndEffects());
    });
    private final float[] sunriseCol = new float[4];
    private final float cloudLevel;
    private final boolean hasGround;
    private final SkyType skyType;
    private final boolean forceBrightLightmap;
    private final boolean constantAmbientLight;

    public DimensionSpecialEffects(float f, boolean bl, SkyType skyType, boolean bl2, boolean bl3) {
        this.cloudLevel = f;
        this.hasGround = bl;
        this.skyType = skyType;
        this.forceBrightLightmap = bl2;
        this.constantAmbientLight = bl3;
    }

    public static DimensionSpecialEffects forType(DimensionType dimensionType) {
        return (DimensionSpecialEffects)EFFECTS.get((Object)dimensionType.effectsLocation());
    }

    @Nullable
    public float[] getSunriseColor(float f, float f2) {
        float f3 = 0.4f;
        float f4 = Mth.cos(f * 6.2831855f) - 0.0f;
        float f5 = -0.0f;
        if (f4 >= -0.4f && f4 <= 0.4f) {
            float f6 = (f4 - -0.0f) / 0.4f * 0.5f + 0.5f;
            float f7 = 1.0f - (1.0f - Mth.sin(f6 * 3.1415927f)) * 0.99f;
            f7 *= f7;
            this.sunriseCol[0] = f6 * 0.3f + 0.7f;
            this.sunriseCol[1] = f6 * f6 * 0.7f + 0.2f;
            this.sunriseCol[2] = f6 * f6 * 0.0f + 0.2f;
            this.sunriseCol[3] = f7;
            return this.sunriseCol;
        }
        return null;
    }

    public float getCloudHeight() {
        return this.cloudLevel;
    }

    public boolean hasGround() {
        return this.hasGround;
    }

    public abstract Vec3 getBrightnessDependentFogColor(Vec3 var1, float var2);

    public abstract boolean isFoggyAt(int var1, int var2);

    public SkyType skyType() {
        return this.skyType;
    }

    public boolean forceBrightLightmap() {
        return this.forceBrightLightmap;
    }

    public boolean constantAmbientLight() {
        return this.constantAmbientLight;
    }

    public static class EndEffects
    extends DimensionSpecialEffects {
        public EndEffects() {
            super(Float.NaN, false, SkyType.END, true, false);
        }

        @Override
        public Vec3 getBrightnessDependentFogColor(Vec3 vec3, float f) {
            return vec3.scale(0.15000000596046448);
        }

        @Override
        public boolean isFoggyAt(int n, int n2) {
            return false;
        }

        @Nullable
        @Override
        public float[] getSunriseColor(float f, float f2) {
            return null;
        }
    }

    public static class OverworldEffects
    extends DimensionSpecialEffects {
        public OverworldEffects() {
            super(128.0f, true, SkyType.NORMAL, false, false);
        }

        @Override
        public Vec3 getBrightnessDependentFogColor(Vec3 vec3, float f) {
            return vec3.multiply(f * 0.94f + 0.06f, f * 0.94f + 0.06f, f * 0.91f + 0.09f);
        }

        @Override
        public boolean isFoggyAt(int n, int n2) {
            return false;
        }
    }

    public static class NetherEffects
    extends DimensionSpecialEffects {
        public NetherEffects() {
            super(Float.NaN, true, SkyType.NONE, false, true);
        }

        @Override
        public Vec3 getBrightnessDependentFogColor(Vec3 vec3, float f) {
            return vec3;
        }

        @Override
        public boolean isFoggyAt(int n, int n2) {
            return true;
        }
    }

    public static enum SkyType {
        NONE,
        NORMAL,
        END;
        
    }

}

