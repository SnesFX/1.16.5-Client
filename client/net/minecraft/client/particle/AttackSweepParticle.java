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

public class AttackSweepParticle
extends TextureSheetParticle {
    private final SpriteSet sprites;

    private AttackSweepParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, SpriteSet spriteSet) {
        super(clientLevel, d, d2, d3, 0.0, 0.0, 0.0);
        float f;
        this.sprites = spriteSet;
        this.lifetime = 4;
        this.rCol = f = this.random.nextFloat() * 0.6f + 0.4f;
        this.gCol = f;
        this.bCol = f;
        this.quadSize = 1.0f - (float)d4 * 0.5f;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public int getLightColor(float f) {
        return 15728880;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new AttackSweepParticle(clientLevel, d, d2, d3, d4, this.sprites);
        }
    }

}

