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
import net.minecraft.world.phys.AABB;

public class EnchantmentTableParticle
extends TextureSheetParticle {
    private final double xStart;
    private final double yStart;
    private final double zStart;

    private EnchantmentTableParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
        super(clientLevel, d, d2, d3);
        this.xd = d4;
        this.yd = d5;
        this.zd = d6;
        this.xStart = d;
        this.yStart = d2;
        this.zStart = d3;
        this.xo = d + d4;
        this.yo = d2 + d5;
        this.zo = d3 + d6;
        this.x = this.xo;
        this.y = this.yo;
        this.z = this.zo;
        this.quadSize = 0.1f * (this.random.nextFloat() * 0.5f + 0.2f);
        float f = this.random.nextFloat() * 0.6f + 0.4f;
        this.rCol = 0.9f * f;
        this.gCol = 0.9f * f;
        this.bCol = f;
        this.hasPhysics = false;
        this.lifetime = (int)(Math.random() * 10.0) + 30;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double d, double d2, double d3) {
        this.setBoundingBox(this.getBoundingBox().move(d, d2, d3));
        this.setLocationFromBoundingbox();
    }

    @Override
    public int getLightColor(float f) {
        int n = super.getLightColor(f);
        float f2 = (float)this.age / (float)this.lifetime;
        f2 *= f2;
        f2 *= f2;
        int n2 = n & 0xFF;
        int n3 = n >> 16 & 0xFF;
        if ((n3 += (int)(f2 * 15.0f * 16.0f)) > 240) {
            n3 = 240;
        }
        return n2 | n3 << 16;
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
        float f = (float)this.age / (float)this.lifetime;
        f = 1.0f - f;
        float f2 = 1.0f - f;
        f2 *= f2;
        f2 *= f2;
        this.x = this.xStart + this.xd * (double)f;
        this.y = this.yStart + this.yd * (double)f - (double)(f2 * 1.2f);
        this.z = this.zStart + this.zd * (double)f;
    }

    public static class NautilusProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public NautilusProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            EnchantmentTableParticle enchantmentTableParticle = new EnchantmentTableParticle(clientLevel, d, d2, d3, d4, d5, d6);
            enchantmentTableParticle.pickSprite(this.sprite);
            return enchantmentTableParticle;
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
            EnchantmentTableParticle enchantmentTableParticle = new EnchantmentTableParticle(clientLevel, d, d2, d3, d4, d5, d6);
            enchantmentTableParticle.pickSprite(this.sprite);
            return enchantmentTableParticle;
        }
    }

}

