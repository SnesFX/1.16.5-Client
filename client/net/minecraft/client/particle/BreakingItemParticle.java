/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class BreakingItemParticle
extends TextureSheetParticle {
    private final float uo;
    private final float vo;

    private BreakingItemParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6, ItemStack itemStack) {
        this(clientLevel, d, d2, d3, itemStack);
        this.xd *= 0.10000000149011612;
        this.yd *= 0.10000000149011612;
        this.zd *= 0.10000000149011612;
        this.xd += d4;
        this.yd += d5;
        this.zd += d6;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    protected BreakingItemParticle(ClientLevel clientLevel, double d, double d2, double d3, ItemStack itemStack) {
        super(clientLevel, d, d2, d3, 0.0, 0.0, 0.0);
        this.setSprite(Minecraft.getInstance().getItemRenderer().getModel(itemStack, clientLevel, null).getParticleIcon());
        this.gravity = 1.0f;
        this.quadSize /= 2.0f;
        this.uo = this.random.nextFloat() * 3.0f;
        this.vo = this.random.nextFloat() * 3.0f;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU((this.uo + 1.0f) / 4.0f * 16.0f);
    }

    @Override
    protected float getU1() {
        return this.sprite.getU(this.uo / 4.0f * 16.0f);
    }

    @Override
    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0f * 16.0f);
    }

    @Override
    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0f) / 4.0f * 16.0f);
    }

    public static class SnowballProvider
    implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new BreakingItemParticle(clientLevel, d, d2, d3, new ItemStack(Items.SNOWBALL));
        }
    }

    public static class SlimeProvider
    implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new BreakingItemParticle(clientLevel, d, d2, d3, new ItemStack(Items.SLIME_BALL));
        }
    }

    public static class Provider
    implements ParticleProvider<ItemParticleOption> {
        @Override
        public Particle createParticle(ItemParticleOption itemParticleOption, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            return new BreakingItemParticle(clientLevel, d, d2, d3, d4, d5, d6, itemParticleOption.getItem());
        }
    }

}

