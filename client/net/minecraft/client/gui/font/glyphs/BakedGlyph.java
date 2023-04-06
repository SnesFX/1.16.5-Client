/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.RenderType;

public class BakedGlyph {
    private final RenderType normalType;
    private final RenderType seeThroughType;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;
    private final float left;
    private final float right;
    private final float up;
    private final float down;

    public BakedGlyph(RenderType renderType, RenderType renderType2, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        this.normalType = renderType;
        this.seeThroughType = renderType2;
        this.u0 = f;
        this.u1 = f2;
        this.v0 = f3;
        this.v1 = f4;
        this.left = f5;
        this.right = f6;
        this.up = f7;
        this.down = f8;
    }

    public void render(boolean bl, float f, float f2, Matrix4f matrix4f, VertexConsumer vertexConsumer, float f3, float f4, float f5, float f6, int n) {
        int n2 = 3;
        float f7 = f + this.left;
        float f8 = f + this.right;
        float f9 = this.up - 3.0f;
        float f10 = this.down - 3.0f;
        float f11 = f2 + f9;
        float f12 = f2 + f10;
        float f13 = bl ? 1.0f - 0.25f * f9 : 0.0f;
        float f14 = bl ? 1.0f - 0.25f * f10 : 0.0f;
        vertexConsumer.vertex(matrix4f, f7 + f13, f11, 0.0f).color(f3, f4, f5, f6).uv(this.u0, this.v0).uv2(n).endVertex();
        vertexConsumer.vertex(matrix4f, f7 + f14, f12, 0.0f).color(f3, f4, f5, f6).uv(this.u0, this.v1).uv2(n).endVertex();
        vertexConsumer.vertex(matrix4f, f8 + f14, f12, 0.0f).color(f3, f4, f5, f6).uv(this.u1, this.v1).uv2(n).endVertex();
        vertexConsumer.vertex(matrix4f, f8 + f13, f11, 0.0f).color(f3, f4, f5, f6).uv(this.u1, this.v0).uv2(n).endVertex();
    }

    public void renderEffect(Effect effect, Matrix4f matrix4f, VertexConsumer vertexConsumer, int n) {
        vertexConsumer.vertex(matrix4f, effect.x0, effect.y0, effect.depth).color(effect.r, effect.g, effect.b, effect.a).uv(this.u0, this.v0).uv2(n).endVertex();
        vertexConsumer.vertex(matrix4f, effect.x1, effect.y0, effect.depth).color(effect.r, effect.g, effect.b, effect.a).uv(this.u0, this.v1).uv2(n).endVertex();
        vertexConsumer.vertex(matrix4f, effect.x1, effect.y1, effect.depth).color(effect.r, effect.g, effect.b, effect.a).uv(this.u1, this.v1).uv2(n).endVertex();
        vertexConsumer.vertex(matrix4f, effect.x0, effect.y1, effect.depth).color(effect.r, effect.g, effect.b, effect.a).uv(this.u1, this.v0).uv2(n).endVertex();
    }

    public RenderType renderType(boolean bl) {
        return bl ? this.seeThroughType : this.normalType;
    }

    public static class Effect {
        protected final float x0;
        protected final float y0;
        protected final float x1;
        protected final float y1;
        protected final float depth;
        protected final float r;
        protected final float g;
        protected final float b;
        protected final float a;

        public Effect(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9) {
            this.x0 = f;
            this.y0 = f2;
            this.x1 = f3;
            this.y1 = f4;
            this.depth = f5;
            this.r = f6;
            this.g = f7;
            this.b = f8;
            this.a = f9;
        }
    }

}

