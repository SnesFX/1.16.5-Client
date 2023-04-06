/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class WhiteAshParticle
extends BaseAshSmokeParticle {
    protected WhiteAshParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6, float f, SpriteSet spriteSet) {
        super(clientLevel, d, d2, d3, 0.1f, -0.1f, 0.1f, d4, d5, d6, f, spriteSet, 0.0f, 20, -5.0E-4, false);
        this.rCol = 0.7294118f;
        this.gCol = 0.69411767f;
        this.bCol = 0.7607843f;
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            Random random = clientLevel.random;
            double d7 = (double)random.nextFloat() * -1.9 * (double)random.nextFloat() * 0.1;
            double d8 = (double)random.nextFloat() * -0.5 * (double)random.nextFloat() * 0.1 * 5.0;
            double d9 = (double)random.nextFloat() * -1.9 * (double)random.nextFloat() * 0.1;
            return new WhiteAshParticle(clientLevel, d, d2, d3, d7, d8, d9, 1.0f, this.sprites);
        }
    }

}

