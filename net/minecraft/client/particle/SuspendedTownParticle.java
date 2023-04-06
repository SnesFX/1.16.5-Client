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

public class SuspendedTownParticle
extends TextureSheetParticle {
    private SuspendedTownParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
        super(clientLevel, d, d2, d3, d4, d5, d6);
        float f;
        this.rCol = f = this.random.nextFloat() * 0.1f + 0.2f;
        this.gCol = f;
        this.bCol = f;
        this.setSize(0.02f, 0.02f);
        this.quadSize *= this.random.nextFloat() * 0.6f + 0.5f;
        this.xd *= 0.019999999552965164;
        this.yd *= 0.019999999552965164;
        this.zd *= 0.019999999552965164;
        this.lifetime = (int)(20.0 / (Math.random() * 0.8 + 0.2));
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
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
            return;
        }
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.99;
        this.yd *= 0.99;
        this.zd *= 0.99;
    }

    public static class DolphinSpeedProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public DolphinSpeedProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            SuspendedTownParticle suspendedTownParticle = new SuspendedTownParticle(clientLevel, d, d2, d3, d4, d5, d6);
            suspendedTownParticle.setColor(0.3f, 0.5f, 1.0f);
            suspendedTownParticle.pickSprite(this.sprite);
            suspendedTownParticle.setAlpha(1.0f - clientLevel.random.nextFloat() * 0.7f);
            suspendedTownParticle.setLifetime(suspendedTownParticle.getLifetime() / 2);
            return suspendedTownParticle;
        }
    }

    public static class ComposterFillProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public ComposterFillProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            SuspendedTownParticle suspendedTownParticle = new SuspendedTownParticle(clientLevel, d, d2, d3, d4, d5, d6);
            suspendedTownParticle.pickSprite(this.sprite);
            suspendedTownParticle.setColor(1.0f, 1.0f, 1.0f);
            suspendedTownParticle.setLifetime(3 + clientLevel.getRandom().nextInt(5));
            return suspendedTownParticle;
        }
    }

    public static class HappyVillagerProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public HappyVillagerProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            SuspendedTownParticle suspendedTownParticle = new SuspendedTownParticle(clientLevel, d, d2, d3, d4, d5, d6);
            suspendedTownParticle.pickSprite(this.sprite);
            suspendedTownParticle.setColor(1.0f, 1.0f, 1.0f);
            return suspendedTownParticle;
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
            SuspendedTownParticle suspendedTownParticle = new SuspendedTownParticle(clientLevel, d, d2, d3, d4, d5, d6);
            suspendedTownParticle.pickSprite(this.sprite);
            return suspendedTownParticle;
        }
    }

}

