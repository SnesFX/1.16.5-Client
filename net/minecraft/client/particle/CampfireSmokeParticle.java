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

public class CampfireSmokeParticle
extends TextureSheetParticle {
    private CampfireSmokeParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6, boolean bl) {
        super(clientLevel, d, d2, d3);
        this.scale(3.0f);
        this.setSize(0.25f, 0.25f);
        this.lifetime = bl ? this.random.nextInt(50) + 280 : this.random.nextInt(50) + 80;
        this.gravity = 3.0E-6f;
        this.xd = d4;
        this.yd = d5 + (double)(this.random.nextFloat() / 500.0f);
        this.zd = d6;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime || this.alpha <= 0.0f) {
            this.remove();
            return;
        }
        this.xd += (double)(this.random.nextFloat() / 5000.0f * (float)(this.random.nextBoolean() ? 1 : -1));
        this.zd += (double)(this.random.nextFloat() / 5000.0f * (float)(this.random.nextBoolean() ? 1 : -1));
        this.yd -= (double)this.gravity;
        this.move(this.xd, this.yd, this.zd);
        if (this.age >= this.lifetime - 60 && this.alpha > 0.01f) {
            this.alpha -= 0.015f;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class SignalProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public SignalProvider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            CampfireSmokeParticle campfireSmokeParticle = new CampfireSmokeParticle(clientLevel, d, d2, d3, d4, d5, d6, true);
            campfireSmokeParticle.setAlpha(0.95f);
            campfireSmokeParticle.pickSprite(this.sprites);
            return campfireSmokeParticle;
        }
    }

    public static class CosyProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public CosyProvider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            CampfireSmokeParticle campfireSmokeParticle = new CampfireSmokeParticle(clientLevel, d, d2, d3, d4, d5, d6, false);
            campfireSmokeParticle.setAlpha(0.9f);
            campfireSmokeParticle.pickSprite(this.sprites);
            return campfireSmokeParticle;
        }
    }

}

