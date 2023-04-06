/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class SuspendedParticle
extends TextureSheetParticle {
    private SuspendedParticle(ClientLevel clientLevel, double d, double d2, double d3) {
        super(clientLevel, d, d2 - 0.125, d3);
        this.rCol = 0.4f;
        this.gCol = 0.4f;
        this.bCol = 0.7f;
        this.setSize(0.01f, 0.01f);
        this.quadSize *= this.random.nextFloat() * 0.6f + 0.2f;
        this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.hasPhysics = false;
    }

    private SuspendedParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
        super(clientLevel, d, d2 - 0.125, d3, d4, d5, d6);
        this.setSize(0.01f, 0.01f);
        this.quadSize *= this.random.nextFloat() * 0.6f + 0.6f;
        this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        this.hasPhysics = false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
            return;
        }
        this.move(this.xd, this.yd, this.zd);
    }

    public static class WarpedSporeProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public WarpedSporeProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            double d7 = (double)clientLevel.random.nextFloat() * -1.9 * (double)clientLevel.random.nextFloat() * 0.1;
            SuspendedParticle suspendedParticle = new SuspendedParticle(clientLevel, d, d2, d3, 0.0, d7, 0.0);
            suspendedParticle.pickSprite(this.sprite);
            suspendedParticle.setColor(0.1f, 0.1f, 0.3f);
            suspendedParticle.setSize(0.001f, 0.001f);
            return suspendedParticle;
        }
    }

    public static class CrimsonSporeProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public CrimsonSporeProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            Random random = clientLevel.random;
            double d7 = random.nextGaussian() * 9.999999974752427E-7;
            double d8 = random.nextGaussian() * 9.999999747378752E-5;
            double d9 = random.nextGaussian() * 9.999999974752427E-7;
            SuspendedParticle suspendedParticle = new SuspendedParticle(clientLevel, d, d2, d3, d7, d8, d9);
            suspendedParticle.pickSprite(this.sprite);
            suspendedParticle.setColor(0.9f, 0.4f, 0.5f);
            return suspendedParticle;
        }
    }

    public static class UnderwaterProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public UnderwaterProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            SuspendedParticle suspendedParticle = new SuspendedParticle(clientLevel, d, d2, d3);
            suspendedParticle.pickSprite(this.sprite);
            return suspendedParticle;
        }
    }

}

