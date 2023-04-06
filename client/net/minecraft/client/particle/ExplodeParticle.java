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

public class ExplodeParticle
extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected ExplodeParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6, SpriteSet spriteSet) {
        super(clientLevel, d, d2, d3);
        float f;
        this.sprites = spriteSet;
        this.xd = d4 + (Math.random() * 2.0 - 1.0) * 0.05000000074505806;
        this.yd = d5 + (Math.random() * 2.0 - 1.0) * 0.05000000074505806;
        this.zd = d6 + (Math.random() * 2.0 - 1.0) * 0.05000000074505806;
        this.rCol = f = this.random.nextFloat() * 0.3f + 0.7f;
        this.gCol = f;
        this.bCol = f;
        this.quadSize = 0.1f * (this.random.nextFloat() * this.random.nextFloat() * 6.0f + 1.0f);
        this.lifetime = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2)) + 2;
        this.setSpriteFromAge(spriteSet);
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
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.setSpriteFromAge(this.sprites);
        this.yd += 0.004;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.8999999761581421;
        this.yd *= 0.8999999761581421;
        this.zd *= 0.8999999761581421;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new ExplodeParticle(clientLevel, d, d2, d3, d4, d5, d6, this.sprites);
        }
    }

}

