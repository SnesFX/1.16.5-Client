/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.state.BlockState;

public class SquidInkParticle
extends SimpleAnimatedParticle {
    private SquidInkParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6, SpriteSet spriteSet) {
        super(clientLevel, d, d2, d3, spriteSet, 0.0f);
        this.quadSize = 0.5f;
        this.setAlpha(1.0f);
        this.setColor(0.0f, 0.0f, 0.0f);
        this.lifetime = (int)((double)(this.quadSize * 12.0f) / (Math.random() * 0.800000011920929 + 0.20000000298023224));
        this.setSpriteFromAge(spriteSet);
        this.hasPhysics = false;
        this.xd = d4;
        this.yd = d5;
        this.zd = d6;
        this.setBaseAirFriction(0.0f);
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
        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0f - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);
        }
        this.move(this.xd, this.yd, this.zd);
        if (this.level.getBlockState(new BlockPos(this.x, this.y, this.z)).isAir()) {
            this.yd -= 0.00800000037997961;
        }
        this.xd *= 0.9200000166893005;
        this.yd *= 0.9200000166893005;
        this.zd *= 0.9200000166893005;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new SquidInkParticle(clientLevel, d, d2, d3, d4, d5, d6, this.sprites);
        }
    }

}

