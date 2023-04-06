/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class HugeExplosionSeedParticle
extends NoRenderParticle {
    private int life;
    private final int lifeTime;

    private HugeExplosionSeedParticle(ClientLevel clientLevel, double d, double d2, double d3) {
        super(clientLevel, d, d2, d3, 0.0, 0.0, 0.0);
        this.lifeTime = 8;
    }

    @Override
    public void tick() {
        for (int i = 0; i < 6; ++i) {
            double d = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
            double d2 = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
            double d3 = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
            this.level.addParticle(ParticleTypes.EXPLOSION, d, d2, d3, (float)this.life / (float)this.lifeTime, 0.0, 0.0);
        }
        ++this.life;
        if (this.life == this.lifeTime) {
            this.remove();
        }
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new HugeExplosionSeedParticle(clientLevel, d, d2, d3);
        }
    }

}

