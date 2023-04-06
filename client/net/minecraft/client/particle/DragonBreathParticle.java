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
import net.minecraft.util.Mth;

public class DragonBreathParticle
extends TextureSheetParticle {
    private boolean hasHitGround;
    private final SpriteSet sprites;

    private DragonBreathParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6, SpriteSet spriteSet) {
        super(clientLevel, d, d2, d3);
        this.xd = d4;
        this.yd = d5;
        this.zd = d6;
        this.rCol = Mth.nextFloat(this.random, 0.7176471f, 0.8745098f);
        this.gCol = Mth.nextFloat(this.random, 0.0f, 0.0f);
        this.bCol = Mth.nextFloat(this.random, 0.8235294f, 0.9764706f);
        this.quadSize *= 0.75f;
        this.lifetime = (int)(20.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
        this.hasHitGround = false;
        this.hasPhysics = false;
        this.sprites = spriteSet;
        this.setSpriteFromAge(spriteSet);
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
        if (this.onGround) {
            this.yd = 0.0;
            this.hasHitGround = true;
        }
        if (this.hasHitGround) {
            this.yd += 0.002;
        }
        this.move(this.xd, this.yd, this.zd);
        if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }
        this.xd *= 0.9599999785423279;
        this.zd *= 0.9599999785423279;
        if (this.hasHitGround) {
            this.yd *= 0.9599999785423279;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float f) {
        return this.quadSize * Mth.clamp(((float)this.age + f) / (float)this.lifetime * 32.0f, 0.0f, 1.0f);
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new DragonBreathParticle(clientLevel, d, d2, d3, d4, d5, d6, this.sprites);
        }
    }

}

