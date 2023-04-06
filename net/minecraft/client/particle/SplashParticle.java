/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class SplashParticle
extends WaterDropParticle {
    private SplashParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
        super(clientLevel, d, d2, d3);
        this.gravity = 0.04f;
        if (d5 == 0.0 && (d4 != 0.0 || d6 != 0.0)) {
            this.xd = d4;
            this.yd = 0.1;
            this.zd = d6;
        }
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            SplashParticle splashParticle = new SplashParticle(clientLevel, d, d2, d3, d4, d5, d6);
            splashParticle.pickSprite(this.sprite);
            return splashParticle;
        }
    }

}

