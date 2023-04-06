/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BarrierParticle
extends TextureSheetParticle {
    private BarrierParticle(ClientLevel clientLevel, double d, double d2, double d3, ItemLike itemLike) {
        super(clientLevel, d, d2, d3);
        this.setSprite(Minecraft.getInstance().getItemRenderer().getItemModelShaper().getParticleIcon(itemLike));
        this.gravity = 0.0f;
        this.lifetime = 80;
        this.hasPhysics = false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    @Override
    public float getQuadSize(float f) {
        return 0.5f;
    }

    public static class Provider
    implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new BarrierParticle(clientLevel, d, d2, d3, Blocks.BARRIER.asItem());
        }
    }

}

