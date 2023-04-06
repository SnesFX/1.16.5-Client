/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class SpriteCoordinateExpander
implements VertexConsumer {
    private final VertexConsumer delegate;
    private final TextureAtlasSprite sprite;

    public SpriteCoordinateExpander(VertexConsumer vertexConsumer, TextureAtlasSprite textureAtlasSprite) {
        this.delegate = vertexConsumer;
        this.sprite = textureAtlasSprite;
    }

    @Override
    public VertexConsumer vertex(double d, double d2, double d3) {
        return this.delegate.vertex(d, d2, d3);
    }

    @Override
    public VertexConsumer color(int n, int n2, int n3, int n4) {
        return this.delegate.color(n, n2, n3, n4);
    }

    @Override
    public VertexConsumer uv(float f, float f2) {
        return this.delegate.uv(this.sprite.getU(f * 16.0f), this.sprite.getV(f2 * 16.0f));
    }

    @Override
    public VertexConsumer overlayCoords(int n, int n2) {
        return this.delegate.overlayCoords(n, n2);
    }

    @Override
    public VertexConsumer uv2(int n, int n2) {
        return this.delegate.uv2(n, n2);
    }

    @Override
    public VertexConsumer normal(float f, float f2, float f3) {
        return this.delegate.normal(f, f2, f3);
    }

    @Override
    public void endVertex() {
        this.delegate.endVertex();
    }

    @Override
    public void vertex(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int n, int n2, float f10, float f11, float f12) {
        this.delegate.vertex(f, f2, f3, f4, f5, f6, f7, this.sprite.getU(f8 * 16.0f), this.sprite.getV(f9 * 16.0f), n, n2, f10, f11, f12);
    }
}

