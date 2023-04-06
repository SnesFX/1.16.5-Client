/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;

public abstract class RisingParticle
extends TextureSheetParticle {
    protected RisingParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
        super(clientLevel, d, d2, d3, d4, d5, d6);
        this.xd = this.xd * 0.009999999776482582 + d4;
        this.yd = this.yd * 0.009999999776482582 + d5;
        this.zd = this.zd * 0.009999999776482582 + d6;
        this.x += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05f);
        this.y += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05f);
        this.z += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05f);
        this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
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
        this.xd *= 0.9599999785423279;
        this.yd *= 0.9599999785423279;
        this.zd *= 0.9599999785423279;
        if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
        }
    }
}

