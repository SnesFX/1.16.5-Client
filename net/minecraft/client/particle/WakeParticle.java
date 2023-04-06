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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class WakeParticle
extends TextureSheetParticle {
    private final SpriteSet sprites;

    private WakeParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6, SpriteSet spriteSet) {
        super(clientLevel, d, d2, d3, 0.0, 0.0, 0.0);
        this.sprites = spriteSet;
        this.xd *= 0.30000001192092896;
        this.yd = Math.random() * 0.20000000298023224 + 0.10000000149011612;
        this.zd *= 0.30000001192092896;
        this.setSize(0.01f, 0.01f);
        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.setSpriteFromAge(spriteSet);
        this.gravity = 0.0f;
        this.xd = d4;
        this.yd = d5;
        this.zd = d6;
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
        int n = 60 - this.lifetime;
        if (this.lifetime-- <= 0) {
            this.remove();
            return;
        }
        this.yd -= (double)this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.9800000190734863;
        this.yd *= 0.9800000190734863;
        this.zd *= 0.9800000190734863;
        float f = (float)n * 0.001f;
        this.setSize(f, f);
        this.setSprite(this.sprites.get(n % 4, 4));
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new WakeParticle(clientLevel, d, d2, d3, d4, d5, d6, this.sprites);
        }
    }

}

