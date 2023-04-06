/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.phys.Vec3;

public class FireworkParticles {

    public static class SparkProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public SparkProvider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            SparkParticle sparkParticle = new SparkParticle(clientLevel, d, d2, d3, d4, d5, d6, Minecraft.getInstance().particleEngine, this.sprites);
            sparkParticle.setAlpha(0.99f);
            return sparkParticle;
        }
    }

    public static class FlashProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public FlashProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            OverlayParticle overlayParticle = new OverlayParticle(clientLevel, d, d2, d3);
            overlayParticle.pickSprite(this.sprite);
            return overlayParticle;
        }
    }

    public static class OverlayParticle
    extends TextureSheetParticle {
        private OverlayParticle(ClientLevel clientLevel, double d, double d2, double d3) {
            super(clientLevel, d, d2, d3);
            this.lifetime = 4;
        }

        @Override
        public ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }

        @Override
        public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
            this.setAlpha(0.6f - ((float)this.age + f - 1.0f) * 0.25f * 0.5f);
            super.render(vertexConsumer, camera, f);
        }

        @Override
        public float getQuadSize(float f) {
            return 7.1f * Mth.sin(((float)this.age + f - 1.0f) * 0.25f * 3.1415927f);
        }
    }

    static class SparkParticle
    extends SimpleAnimatedParticle {
        private boolean trail;
        private boolean flicker;
        private final ParticleEngine engine;
        private float fadeR;
        private float fadeG;
        private float fadeB;
        private boolean hasFade;

        private SparkParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6, ParticleEngine particleEngine, SpriteSet spriteSet) {
            super(clientLevel, d, d2, d3, spriteSet, -0.004f);
            this.xd = d4;
            this.yd = d5;
            this.zd = d6;
            this.engine = particleEngine;
            this.quadSize *= 0.75f;
            this.lifetime = 48 + this.random.nextInt(12);
            this.setSpriteFromAge(spriteSet);
        }

        public void setTrail(boolean bl) {
            this.trail = bl;
        }

        public void setFlicker(boolean bl) {
            this.flicker = bl;
        }

        @Override
        public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
            if (!this.flicker || this.age < this.lifetime / 3 || (this.age + this.lifetime) / 3 % 2 == 0) {
                super.render(vertexConsumer, camera, f);
            }
        }

        @Override
        public void tick() {
            super.tick();
            if (this.trail && this.age < this.lifetime / 2 && (this.age + this.lifetime) % 2 == 0) {
                SparkParticle sparkParticle = new SparkParticle(this.level, this.x, this.y, this.z, 0.0, 0.0, 0.0, this.engine, this.sprites);
                sparkParticle.setAlpha(0.99f);
                sparkParticle.setColor(this.rCol, this.gCol, this.bCol);
                sparkParticle.age = sparkParticle.lifetime / 2;
                if (this.hasFade) {
                    sparkParticle.hasFade = true;
                    sparkParticle.fadeR = this.fadeR;
                    sparkParticle.fadeG = this.fadeG;
                    sparkParticle.fadeB = this.fadeB;
                }
                sparkParticle.flicker = this.flicker;
                this.engine.add(sparkParticle);
            }
        }
    }

    public static class Starter
    extends NoRenderParticle {
        private int life;
        private final ParticleEngine engine;
        private ListTag explosions;
        private boolean twinkleDelay;

        public Starter(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6, ParticleEngine particleEngine, @Nullable CompoundTag compoundTag) {
            super(clientLevel, d, d2, d3);
            this.xd = d4;
            this.yd = d5;
            this.zd = d6;
            this.engine = particleEngine;
            this.lifetime = 8;
            if (compoundTag != null) {
                this.explosions = compoundTag.getList("Explosions", 10);
                if (this.explosions.isEmpty()) {
                    this.explosions = null;
                } else {
                    this.lifetime = this.explosions.size() * 2 - 1;
                    for (int i = 0; i < this.explosions.size(); ++i) {
                        CompoundTag compoundTag2 = this.explosions.getCompound(i);
                        if (!compoundTag2.getBoolean("Flicker")) continue;
                        this.twinkleDelay = true;
                        this.lifetime += 15;
                        break;
                    }
                }
            }
        }

        @Override
        public void tick() {
            int n;
            Object object;
            if (this.life == 0 && this.explosions != null) {
                n = this.isFarAwayFromCamera();
                boolean bl = false;
                if (this.explosions.size() >= 3) {
                    bl = true;
                } else {
                    for (int i = 0; i < this.explosions.size(); ++i) {
                        CompoundTag compoundTag = this.explosions.getCompound(i);
                        if (FireworkRocketItem.Shape.byId(compoundTag.getByte("Type")) != FireworkRocketItem.Shape.LARGE_BALL) continue;
                        bl = true;
                        break;
                    }
                }
                object = bl ? (n != 0 ? SoundEvents.FIREWORK_ROCKET_LARGE_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_LARGE_BLAST) : (n != 0 ? SoundEvents.FIREWORK_ROCKET_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_BLAST);
                this.level.playLocalSound(this.x, this.y, this.z, (SoundEvent)object, SoundSource.AMBIENT, 20.0f, 0.95f + this.random.nextFloat() * 0.1f, true);
            }
            if (this.life % 2 == 0 && this.explosions != null && this.life / 2 < this.explosions.size()) {
                n = this.life / 2;
                CompoundTag compoundTag = this.explosions.getCompound(n);
                object = FireworkRocketItem.Shape.byId(compoundTag.getByte("Type"));
                boolean bl = compoundTag.getBoolean("Trail");
                boolean bl2 = compoundTag.getBoolean("Flicker");
                int[] arrn = compoundTag.getIntArray("Colors");
                int[] arrn2 = compoundTag.getIntArray("FadeColors");
                if (arrn.length == 0) {
                    arrn = new int[]{DyeColor.BLACK.getFireworkColor()};
                }
                switch (1.$SwitchMap$net$minecraft$world$item$FireworkRocketItem$Shape[((Enum)object).ordinal()]) {
                    default: {
                        this.createParticleBall(0.25, 2, arrn, arrn2, bl, bl2);
                        break;
                    }
                    case 2: {
                        this.createParticleBall(0.5, 4, arrn, arrn2, bl, bl2);
                        break;
                    }
                    case 3: {
                        this.createParticleShape(0.5, new double[][]{{0.0, 1.0}, {0.3455, 0.309}, {0.9511, 0.309}, {0.3795918367346939, -0.12653061224489795}, {0.6122448979591837, -0.8040816326530612}, {0.0, -0.35918367346938773}}, arrn, arrn2, bl, bl2, false);
                        break;
                    }
                    case 4: {
                        this.createParticleShape(0.5, new double[][]{{0.0, 0.2}, {0.2, 0.2}, {0.2, 0.6}, {0.6, 0.6}, {0.6, 0.2}, {0.2, 0.2}, {0.2, 0.0}, {0.4, 0.0}, {0.4, -0.6}, {0.2, -0.6}, {0.2, -0.4}, {0.0, -0.4}}, arrn, arrn2, bl, bl2, true);
                        break;
                    }
                    case 5: {
                        this.createParticleBurst(arrn, arrn2, bl, bl2);
                    }
                }
                int n2 = arrn[0];
                float f = (float)((n2 & 0xFF0000) >> 16) / 255.0f;
                float f2 = (float)((n2 & 0xFF00) >> 8) / 255.0f;
                float f3 = (float)((n2 & 0xFF) >> 0) / 255.0f;
                Particle particle = this.engine.createParticle(ParticleTypes.FLASH, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                particle.setColor(f, f2, f3);
            }
            ++this.life;
            if (this.life > this.lifetime) {
                if (this.twinkleDelay) {
                    n = this.isFarAwayFromCamera() ? 1 : 0;
                    SoundEvent soundEvent = n != 0 ? SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR : SoundEvents.FIREWORK_ROCKET_TWINKLE;
                    this.level.playLocalSound(this.x, this.y, this.z, soundEvent, SoundSource.AMBIENT, 20.0f, 0.9f + this.random.nextFloat() * 0.15f, true);
                }
                this.remove();
            }
        }

        private boolean isFarAwayFromCamera() {
            Minecraft minecraft = Minecraft.getInstance();
            return minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(this.x, this.y, this.z) >= 256.0;
        }

        private void createParticle(double d, double d2, double d3, double d4, double d5, double d6, int[] arrn, int[] arrn2, boolean bl, boolean bl2) {
            SparkParticle sparkParticle = (SparkParticle)this.engine.createParticle(ParticleTypes.FIREWORK, d, d2, d3, d4, d5, d6);
            sparkParticle.setTrail(bl);
            sparkParticle.setFlicker(bl2);
            sparkParticle.setAlpha(0.99f);
            int n = this.random.nextInt(arrn.length);
            sparkParticle.setColor(arrn[n]);
            if (arrn2.length > 0) {
                sparkParticle.setFadeColor(Util.getRandom(arrn2, this.random));
            }
        }

        private void createParticleBall(double d, int n, int[] arrn, int[] arrn2, boolean bl, boolean bl2) {
            double d2 = this.x;
            double d3 = this.y;
            double d4 = this.z;
            for (int i = -n; i <= n; ++i) {
                for (int j = -n; j <= n; ++j) {
                    for (int k = -n; k <= n; ++k) {
                        double d5 = (double)j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        double d6 = (double)i + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        double d7 = (double)k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        double d8 = (double)Mth.sqrt(d5 * d5 + d6 * d6 + d7 * d7) / d + this.random.nextGaussian() * 0.05;
                        this.createParticle(d2, d3, d4, d5 / d8, d6 / d8, d7 / d8, arrn, arrn2, bl, bl2);
                        if (i == -n || i == n || j == -n || j == n) continue;
                        k += n * 2 - 1;
                    }
                }
            }
        }

        private void createParticleShape(double d, double[][] arrd, int[] arrn, int[] arrn2, boolean bl, boolean bl2, boolean bl3) {
            double d2 = arrd[0][0];
            double d3 = arrd[0][1];
            this.createParticle(this.x, this.y, this.z, d2 * d, d3 * d, 0.0, arrn, arrn2, bl, bl2);
            float f = this.random.nextFloat() * 3.1415927f;
            double d4 = bl3 ? 0.034 : 0.34;
            for (int i = 0; i < 3; ++i) {
                double d5 = (double)f + (double)((float)i * 3.1415927f) * d4;
                double d6 = d2;
                double d7 = d3;
                for (int j = 1; j < arrd.length; ++j) {
                    double d8 = arrd[j][0];
                    double d9 = arrd[j][1];
                    for (double d10 = 0.25; d10 <= 1.0; d10 += 0.25) {
                        double d11 = Mth.lerp(d10, d6, d8) * d;
                        double d12 = Mth.lerp(d10, d7, d9) * d;
                        double d13 = d11 * Math.sin(d5);
                        d11 *= Math.cos(d5);
                        for (double d14 = -1.0; d14 <= 1.0; d14 += 2.0) {
                            this.createParticle(this.x, this.y, this.z, d11 * d14, d12, d13 * d14, arrn, arrn2, bl, bl2);
                        }
                    }
                    d6 = d8;
                    d7 = d9;
                }
            }
        }

        private void createParticleBurst(int[] arrn, int[] arrn2, boolean bl, boolean bl2) {
            double d = this.random.nextGaussian() * 0.05;
            double d2 = this.random.nextGaussian() * 0.05;
            for (int i = 0; i < 70; ++i) {
                double d3 = this.xd * 0.5 + this.random.nextGaussian() * 0.15 + d;
                double d4 = this.zd * 0.5 + this.random.nextGaussian() * 0.15 + d2;
                double d5 = this.yd * 0.5 + this.random.nextDouble() * 0.5;
                this.createParticle(this.x, this.y, this.z, d3, d5, d4, arrn, arrn2, bl, bl2);
            }
        }
    }

}

