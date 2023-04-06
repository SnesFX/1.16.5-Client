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

public class SpellParticle
extends TextureSheetParticle {
    private static final Random RANDOM = new Random();
    private final SpriteSet sprites;

    private SpellParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6, SpriteSet spriteSet) {
        super(clientLevel, d, d2, d3, 0.5 - RANDOM.nextDouble(), d5, 0.5 - RANDOM.nextDouble());
        this.sprites = spriteSet;
        this.yd *= 0.20000000298023224;
        if (d4 == 0.0 && d6 == 0.0) {
            this.xd *= 0.10000000149011612;
            this.zd *= 0.10000000149011612;
        }
        this.quadSize *= 0.75f;
        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
        this.hasPhysics = false;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
        if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }
        this.xd *= 0.9599999785423279;
        this.yd *= 0.9599999785423279;
        this.zd *= 0.9599999785423279;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }

    public static class InstantProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public InstantProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new SpellParticle(clientLevel, d, d2, d3, d4, d5, d6, this.sprite);
        }
    }

    public static class WitchProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public WitchProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            SpellParticle spellParticle = new SpellParticle(clientLevel, d, d2, d3, d4, d5, d6, this.sprite);
            float f = clientLevel.random.nextFloat() * 0.5f + 0.35f;
            spellParticle.setColor(1.0f * f, 0.0f * f, 1.0f * f);
            return spellParticle;
        }
    }

    public static class AmbientMobProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public AmbientMobProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            SpellParticle spellParticle = new SpellParticle(clientLevel, d, d2, d3, d4, d5, d6, this.sprite);
            spellParticle.setAlpha(0.15f);
            spellParticle.setColor((float)d4, (float)d5, (float)d6);
            return spellParticle;
        }
    }

    public static class MobProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public MobProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            SpellParticle spellParticle = new SpellParticle(clientLevel, d, d2, d3, d4, d5, d6, this.sprite);
            spellParticle.setColor((float)d4, (float)d5, (float)d6);
            return spellParticle;
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
            return new SpellParticle(clientLevel, d, d2, d3, d4, d5, d6, this.sprite);
        }
    }

}

