/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class EmptyGlyph
extends BakedGlyph {
    public EmptyGlyph() {
        super(RenderType.text(new ResourceLocation("")), RenderType.textSeeThrough(new ResourceLocation("")), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void render(boolean bl, float f, float f2, Matrix4f matrix4f, VertexConsumer vertexConsumer, float f3, float f4, float f5, float f6, int n) {
    }
}

