/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.AABB;

public class EndRodParticle
extends SimpleAnimatedParticle {
    private EndRodParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6, SpriteSet spriteSet) {
        super(clientLevel, d, d2, d3, spriteSet, -5.0E-4f);
        this.xd = d4;
        this.yd = d5;
        this.zd = d6;
        this.quadSize *= 0.75f;
        this.lifetime = 60 + this.random.nextInt(12);
        this.setFadeColor(15916745);
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void move(double d, double d2, double d3) {
        this.setBoundingBox(this.getBoundingBox().move(d, d2, d3));
        this.setLocationFromBoundingbox();
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new EndRodParticle(clientLevel, d, d2, d3, d4, d5, d6, this.sprites);
        }
    }

}

