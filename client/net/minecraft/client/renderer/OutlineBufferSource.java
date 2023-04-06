/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import java.util.Optional;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class OutlineBufferSource
implements MultiBufferSource {
    private final MultiBufferSource.BufferSource bufferSource;
    private final MultiBufferSource.BufferSource outlineBufferSource = MultiBufferSource.immediate(new BufferBuilder(256));
    private int teamR = 255;
    private int teamG = 255;
    private int teamB = 255;
    private int teamA = 255;

    public OutlineBufferSource(MultiBufferSource.BufferSource bufferSource) {
        this.bufferSource = bufferSource;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        if (renderType.isOutline()) {
            VertexConsumer vertexConsumer = this.outlineBufferSource.getBuffer(renderType);
            return new EntityOutlineGenerator(vertexConsumer, this.teamR, this.teamG, this.teamB, this.teamA);
        }
        VertexConsumer vertexConsumer = this.bufferSource.getBuffer(renderType);
        Optional<RenderType> optional = renderType.outline();
        if (optional.isPresent()) {
            VertexConsumer vertexConsumer2 = this.outlineBufferSource.getBuffer(optional.get());
            EntityOutlineGenerator entityOutlineGenerator = new EntityOutlineGenerator(vertexConsumer2, this.teamR, this.teamG, this.teamB, this.teamA);
            return VertexMultiConsumer.create(entityOutlineGenerator, vertexConsumer);
        }
        return vertexConsumer;
    }

    public void setColor(int n, int n2, int n3, int n4) {
        this.teamR = n;
        this.teamG = n2;
        this.teamB = n3;
        this.teamA = n4;
    }

    public void endOutlineBatch() {
        this.outlineBufferSource.endBatch();
    }

    static class EntityOutlineGenerator
    extends DefaultedVertexConsumer {
        private final VertexConsumer delegate;
        private double x;
        private double y;
        private double z;
        private float u;
        private float v;

        private EntityOutlineGenerator(VertexConsumer vertexConsumer, int n, int n2, int n3, int n4) {
            this.delegate = vertexConsumer;
            super.defaultColor(n, n2, n3, n4);
        }

        @Override
        public void defaultColor(int n, int n2, int n3, int n4) {
        }

        @Override
        public VertexConsumer vertex(double d, double d2, double d3) {
            this.x = d;
            this.y = d2;
            this.z = d3;
            return this;
        }

        @Override
        public VertexConsumer color(int n, int n2, int n3, int n4) {
            return this;
        }

        @Override
        public VertexConsumer uv(float f, float f2) {
            this.u = f;
            this.v = f2;
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int n, int n2) {
            return this;
        }

        @Override
        public VertexConsumer uv2(int n, int n2) {
            return this;
        }

        @Override
        public VertexConsumer normal(float f, float f2, float f3) {
            return this;
        }

        @Override
        public void vertex(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, int n, int n2, float f10, float f11, float f12) {
            this.delegate.vertex(f, f2, f3).color(this.defaultR, this.defaultG, this.defaultB, this.defaultA).uv(f8, f9).endVertex();
        }

        @Override
        public void endVertex() {
            this.delegate.vertex(this.x, this.y, this.z).color(this.defaultR, this.defaultG, this.defaultB, this.defaultA).uv(this.u, this.v).endVertex();
        }
    }

}

