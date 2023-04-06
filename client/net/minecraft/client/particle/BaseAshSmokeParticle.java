/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class BaseAshSmokeParticle
extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final double fallSpeed;

    protected BaseAshSmokeParticle(ClientLevel clientLevel, double d, double d2, double d3, float f, float f2, float f3, double d4, double d5, double d6, float f4, SpriteSet spriteSet, float f5, int n, double d7, boolean bl) {
        super(clientLevel, d, d2, d3, 0.0, 0.0, 0.0);
        float f6;
        this.fallSpeed = d7;
        this.sprites = spriteSet;
        this.xd *= (double)f;
        this.yd *= (double)f2;
        this.zd *= (double)f3;
        this.xd += d4;
        this.yd += d5;
        this.zd += d6;
        this.rCol = f6 = clientLevel.random.nextFloat() * f5;
        this.gCol = f6;
        this.bCol = f6;
        this.quadSize *= 0.75f * f4;
        this.lifetime = (int)((double)n / ((double)clientLevel.random.nextFloat() * 0.8 + 0.2));
        this.lifetime = (int)((float)this.lifetime * f4);
        this.lifetime = Math.max(this.lifetime, 1);
        this.setSpriteFromAge(spriteSet);
        this.hasPhysics = bl;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float f) {
        return this.quadSize * Mth.clamp(((float)this.age + f) / (float)this.lifetime * 32.0f, 0.0f, 1.0f);
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
        this.yd += this.fallSpeed;
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
}

