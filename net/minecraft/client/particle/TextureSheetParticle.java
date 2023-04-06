/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public abstract class TextureSheetParticle
extends SingleQuadParticle {
    protected TextureAtlasSprite sprite;

    protected TextureSheetParticle(ClientLevel clientLevel, double d, double d2, double d3) {
        super(clientLevel, d, d2, d3);
    }

    protected TextureSheetParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
        super(clientLevel, d, d2, d3, d4, d5, d6);
    }

    protected void setSprite(TextureAtlasSprite textureAtlasSprite) {
        this.sprite = textureAtlasSprite;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU0();
    }

    @Override
    protected float getU1() {
        return this.sprite.getU1();
    }

    @Override
    protected float getV0() {
        return this.sprite.getV0();
    }

    @Override
    protected float getV1() {
        return this.sprite.getV1();
    }

    public void pickSprite(SpriteSet spriteSet) {
        this.setSprite(spriteSet.get(this.random));
    }

    public void setSpriteFromAge(SpriteSet spriteSet) {
        this.setSprite(spriteSet.get(this.age, this.lifetime));
    }
}

