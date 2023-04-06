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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class LavaParticle
extends TextureSheetParticle {
    private LavaParticle(ClientLevel clientLevel, double d, double d2, double d3) {
        super(clientLevel, d, d2, d3, 0.0, 0.0, 0.0);
        this.xd *= 0.800000011920929;
        this.yd *= 0.800000011920929;
        this.zd *= 0.800000011920929;
        this.yd = this.random.nextFloat() * 0.4f + 0.05f;
        this.quadSize *= this.random.nextFloat() * 2.0f + 0.2f;
        this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float f) {
        int n = super.getLightColor(f);
        int n2 = 240;
        int n3 = n >> 16 & 0xFF;
        return 0xF0 | n3 << 16;
    }

    @Override
    public float getQuadSize(float f) {
        float f2 = ((float)this.age + f) / (float)this.lifetime;
        return this.quadSize * (1.0f - f2 * f2);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float f = (float)this.age / (float)this.lifetime;
        if (this.random.nextFloat() > f) {
            this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
        }
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.yd -= 0.03;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.9990000128746033;
        this.yd *= 0.9990000128746033;
        this.zd *= 0.9990000128746033;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
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
            LavaParticle lavaParticle = new LavaParticle(clientLevel, d, d2, d3);
            lavaParticle.pickSprite(this.sprite);
            return lavaParticle;
        }
    }

}

