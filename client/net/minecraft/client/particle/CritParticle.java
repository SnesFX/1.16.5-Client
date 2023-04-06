/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class CritParticle
extends TextureSheetParticle {
    private CritParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
        super(clientLevel, d, d2, d3, 0.0, 0.0, 0.0);
        float f;
        this.xd *= 0.10000000149011612;
        this.yd *= 0.10000000149011612;
        this.zd *= 0.10000000149011612;
        this.xd += d4 * 0.4;
        this.yd += d5 * 0.4;
        this.zd += d6 * 0.4;
        this.rCol = f = (float)(Math.random() * 0.30000001192092896 + 0.6000000238418579);
        this.gCol = f;
        this.bCol = f;
        this.quadSize *= 0.75f;
        this.lifetime = Math.max((int)(6.0 / (Math.random() * 0.8 + 0.6)), 1);
        this.hasPhysics = false;
        this.tick();
    }

    @Override
    public float getQuadSize(float f) {
        return this.quadSize * Mth.clamp(((float)this.age + f) / (float)this.lifetime * 32.0f, 0.0f, 1.0f);
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
        this.move(this.xd, this.yd, this.zd);
        this.gCol = (float)((double)this.gCol * 0.96);
        this.bCol = (float)((double)this.bCol * 0.9);
        this.xd *= 0.699999988079071;
        this.yd *= 0.699999988079071;
        this.zd *= 0.699999988079071;
        this.yd -= 0.019999999552965164;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class DamageIndicatorProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public DamageIndicatorProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            CritParticle critParticle = new CritParticle(clientLevel, d, d2, d3, d4, d5 + 1.0, d6);
            critParticle.setLifetime(20);
            critParticle.pickSprite(this.sprite);
            return critParticle;
        }
    }

    public static class MagicProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public MagicProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            CritParticle critParticle = new CritParticle(clientLevel, d, d2, d3, d4, d5, d6);
            critParticle.rCol *= 0.3f;
            critParticle.gCol *= 0.8f;
            critParticle.pickSprite(this.sprite);
            return critParticle;
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
            CritParticle critParticle = new CritParticle(clientLevel, d, d2, d3, d4, d5, d6);
            critParticle.pickSprite(this.sprite);
            return critParticle;
        }
    }

}

