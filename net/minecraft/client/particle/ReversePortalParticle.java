/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class ReversePortalParticle
extends PortalParticle {
    private ReversePortalParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
        super(clientLevel, d, d2, d3, d4, d5, d6);
        this.quadSize = (float)((double)this.quadSize * 1.5);
        this.lifetime = (int)(Math.random() * 2.0) + 60;
    }

    @Override
    public float getQuadSize(float f) {
        float f2 = 1.0f - ((float)this.age + f) / ((float)this.lifetime * 1.5f);
        return this.quadSize * f2;
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
        this.x += this.xd * (double)f;
        this.y += this.yd * (double)f;
        this.z += this.zd * (double)f;
    }

    public static class ReversePortalProvider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public ReversePortalProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            ReversePortalParticle reversePortalParticle = new ReversePortalParticle(clientLevel, d, d2, d3, d4, d5, d6);
            reversePortalParticle.pickSprite(this.sprite);
            return reversePortalParticle;
        }
    }

}

