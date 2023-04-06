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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class NoteParticle
extends TextureSheetParticle {
    private NoteParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4) {
        super(clientLevel, d, d2, d3, 0.0, 0.0, 0.0);
        this.xd *= 0.009999999776482582;
        this.yd *= 0.009999999776482582;
        this.zd *= 0.009999999776482582;
        this.yd += 0.2;
        this.rCol = Math.max(0.0f, Mth.sin(((float)d4 + 0.0f) * 6.2831855f) * 0.65f + 0.35f);
        this.gCol = Math.max(0.0f, Mth.sin(((float)d4 + 0.33333334f) * 6.2831855f) * 0.65f + 0.35f);
        this.bCol = Math.max(0.0f, Mth.sin(((float)d4 + 0.6666667f) * 6.2831855f) * 0.65f + 0.35f);
        this.quadSize *= 1.5f;
        this.lifetime = 6;
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
        this.move(this.xd, this.yd, this.zd);
        if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }
        this.xd *= 0.6600000262260437;
        this.yd *= 0.6600000262260437;
        this.zd *= 0.6600000262260437;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
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
            NoteParticle noteParticle = new NoteParticle(clientLevel, d, d2, d3, d4);
            noteParticle.pickSprite(this.sprite);
            return noteParticle;
        }
    }

}

