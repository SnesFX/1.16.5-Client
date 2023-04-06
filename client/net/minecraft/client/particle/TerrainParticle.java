/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.particle;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TerrainParticle
extends TextureSheetParticle {
    private final BlockState blockState;
    private BlockPos pos;
    private final float uo;
    private final float vo;

    public TerrainParticle(ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6, BlockState blockState) {
        super(clientLevel, d, d2, d3, d4, d5, d6);
        this.blockState = blockState;
        this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(blockState));
        this.gravity = 1.0f;
        this.rCol = 0.6f;
        this.gCol = 0.6f;
        this.bCol = 0.6f;
        this.quadSize /= 2.0f;
        this.uo = this.random.nextFloat() * 3.0f;
        this.vo = this.random.nextFloat() * 3.0f;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    public TerrainParticle init(BlockPos blockPos) {
        this.pos = blockPos;
        if (this.blockState.is(Blocks.GRASS_BLOCK)) {
            return this;
        }
        this.multiplyColor(blockPos);
        return this;
    }

    public TerrainParticle init() {
        this.pos = new BlockPos(this.x, this.y, this.z);
        if (this.blockState.is(Blocks.GRASS_BLOCK)) {
            return this;
        }
        this.multiplyColor(this.pos);
        return this;
    }

    protected void multiplyColor(@Nullable BlockPos blockPos) {
        int n = Minecraft.getInstance().getBlockColors().getColor(this.blockState, this.level, blockPos, 0);
        this.rCol *= (float)(n >> 16 & 0xFF) / 255.0f;
        this.gCol *= (float)(n >> 8 & 0xFF) / 255.0f;
        this.bCol *= (float)(n & 0xFF) / 255.0f;
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

    @Override
    public int getLightColor(float f) {
        int n = super.getLightColor(f);
        int n2 = 0;
        if (this.level.hasChunkAt(this.pos)) {
            n2 = LevelRenderer.getLightColor(this.level, this.pos);
        }
        return n == 0 ? n2 : n;
    }

    public static class Provider
    implements ParticleProvider<BlockParticleOption> {
        @Override
        public Particle createParticle(BlockParticleOption blockParticleOption, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            BlockState blockState = blockParticleOption.getState();
            if (blockState.isAir() || blockState.is(Blocks.MOVING_PISTON)) {
                return null;
            }
            return new TerrainParticle(clientLevel, d, d2, d3, d4, d5, d6, blockState).init();
        }
    }

}

