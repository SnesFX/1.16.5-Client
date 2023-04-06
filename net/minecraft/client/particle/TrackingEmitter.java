/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class TrackingEmitter
extends NoRenderParticle {
    private final Entity entity;
    private int life;
    private final int lifeTime;
    private final ParticleOptions particleType;

    public TrackingEmitter(ClientLevel clientLevel, Entity entity, ParticleOptions particleOptions) {
        this(clientLevel, entity, particleOptions, 3);
    }

    public TrackingEmitter(ClientLevel clientLevel, Entity entity, ParticleOptions particleOptions, int n) {
        this(clientLevel, entity, particleOptions, n, entity.getDeltaMovement());
    }

    private TrackingEmitter(ClientLevel clientLevel, Entity entity, ParticleOptions particleOptions, int n, Vec3 vec3) {
        super(clientLevel, entity.getX(), entity.getY(0.5), entity.getZ(), vec3.x, vec3.y, vec3.z);
        this.entity = entity;
        this.lifeTime = n;
        this.particleType = particleOptions;
        this.tick();
    }

    @Override
    public void tick() {
        for (int i = 0; i < 16; ++i) {
            double d;
            double d2;
            double d3 = this.random.nextFloat() * 2.0f - 1.0f;
            if (d3 * d3 + (d2 = (double)(this.random.nextFloat() * 2.0f - 1.0f)) * d2 + (d = (double)(this.random.nextFloat() * 2.0f - 1.0f)) * d > 1.0) continue;
            double d4 = this.entity.getX(d3 / 4.0);
            double d5 = this.entity.getY(0.5 + d2 / 4.0);
            double d6 = this.entity.getZ(d / 4.0);
            this.level.addParticle(this.particleType, false, d4, d5, d6, d3, d2 + 0.2, d);
        }
        ++this.life;
        if (this.life >= this.lifeTime) {
            this.remove();
        }
    }
}

