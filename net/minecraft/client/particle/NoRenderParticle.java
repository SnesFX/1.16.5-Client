/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;

public class NoRenderParticle
extends Particle {
    protected NoRenderParticle(ClientLevel clientLevel, double d, double d2, double d3) {
        super(clientLevel, d, d2, d3);
    }

    protected NoRenderParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
        super(clientLevel, d, d2, d3, d4, d5, d6);
    }

    @Override
    public final void render(VertexConsumer vertexConsumer, Camera camera, float f) {
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.NO_RENDER;
    }
}

