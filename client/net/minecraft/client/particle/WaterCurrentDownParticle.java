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
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class WaterCurrentDownParticle
extends TextureSheetParticle {
    private float angle;

    private WaterCurrentDownParticle(ClientLevel clientLevel, double d, double d2, double d3) {
        super(clientLevel, d, d2, d3);
        this.lifetime = (int)(Math.random() * 60.0) + 30;
        this.hasPhysics = false;
        this.xd = 0.0;
        this.yd = -0.05;
        this.zd = 0.0;
        this.setSize(0.02f, 0.02f);
        this.quadSize *= this.random.nextFloat() * 0.6f + 0.2f;
        this.gravity = 0.002f;
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
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        float f = 0.6f;
        this.xd += (double)(0.6f * Mth.cos(this.angle));
        this.zd += (double)(0.6f * Mth.sin(this.angle));
        this.xd *= 0.07;
        this.zd *= 0.07;
        this.move(this.xd, this.yd, this.zd);
        if (!this.level.getFluidState(new BlockPos(this.x, this.y, this.z)).is(FluidTags.WATER) || this.onGround) {
            this.remove();
        }
        this.angle = (float)((double)this.angle + 0.08);
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            WaterCurrentDownParticle waterCurrentDownParticle = new WaterCurrentDownParticle(clientLevel, d, d2, d3);
            waterCurrentDownParticle.pickSprite(this.sprite);
            return waterCurrentDownParticle;
        }
    }

}

