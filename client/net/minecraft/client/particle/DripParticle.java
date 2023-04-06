/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class DripParticle
extends TextureSheetParticle {
    private final Fluid type;
    protected boolean isGlowing;

    private DripParticle(ClientLevel clientLevel, double d, double d2, double d3, Fluid fluid) {
        super(clientLevel, d, d2, d3);
        this.setSize(0.01f, 0.01f);
        this.gravity = 0.06f;
        this.type = fluid;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float f) {
        if (this.isGlowing) {
            return 240;
        }
        return super.getLightColor(f);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.preMoveUpdate();
        if (this.removed) {
            return;
        }
        this.yd -= (double)this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.postMoveUpdate();
        if (this.removed) {
            return;
        }
        this.xd *= 0.9800000190734863;
        this.yd *= 0.9800000190734863;
        this.zd *= 0.9800000190734863;
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        FluidState fluidState = this.level.getFluidState(blockPos);
        if (fluidState.getType() == this.type && this.y < (double)((float)blockPos.getY() + fluidState.getHeight(this.level, blockPos))) {
            this.remove();
        }
    }

    protected void preMoveUpdate() {
        if (this.lifetime-- <= 0) {
            this.remove();
        }
    }

    protected void postMoveUpdate() {
    }

    public static class ObsidianTearLandProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public ObsidianTearLandProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            DripLandParticle dripLandParticle = new DripLandParticle(clientLevel, d, d2, d3, Fluids.EMPTY);
            dripLandParticle.isGlowing = true;
            dripLandParticle.lifetime = (int)(28.0 / (Math.random() * 0.8 + 0.2));
            dripLandParticle.setColor(0.51171875f, 0.03125f, 0.890625f);
            dripLandParticle.pickSprite(this.sprite);
            return dripLandParticle;
        }
    }

    public static class ObsidianTearFallProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public ObsidianTearFallProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            FallAndLandParticle fallAndLandParticle = new FallAndLandParticle(clientLevel, d, d2, d3, Fluids.EMPTY, ParticleTypes.LANDING_OBSIDIAN_TEAR);
            fallAndLandParticle.isGlowing = true;
            fallAndLandParticle.gravity = 0.01f;
            fallAndLandParticle.setColor(0.51171875f, 0.03125f, 0.890625f);
            fallAndLandParticle.pickSprite(this.sprite);
            return fallAndLandParticle;
        }
    }

    public static class ObsidianTearHangProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public ObsidianTearHangProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            DripHangParticle dripHangParticle = new DripHangParticle(clientLevel, d, d2, d3, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR);
            dripHangParticle.isGlowing = true;
            dripHangParticle.gravity *= 0.01f;
            dripHangParticle.lifetime = 100;
            dripHangParticle.setColor(0.51171875f, 0.03125f, 0.890625f);
            dripHangParticle.pickSprite(this.sprite);
            return dripHangParticle;
        }
    }

    public static class NectarFallProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public NectarFallProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            FallingParticle fallingParticle = new FallingParticle(clientLevel, d, d2, d3, Fluids.EMPTY);
            fallingParticle.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
            fallingParticle.gravity = 0.007f;
            fallingParticle.setColor(0.92f, 0.782f, 0.72f);
            fallingParticle.pickSprite(this.sprite);
            return fallingParticle;
        }
    }

    public static class HoneyLandProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public HoneyLandProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            DripLandParticle dripLandParticle = new DripLandParticle(clientLevel, d, d2, d3, Fluids.EMPTY);
            dripLandParticle.lifetime = (int)(128.0 / (Math.random() * 0.8 + 0.2));
            dripLandParticle.setColor(0.522f, 0.408f, 0.082f);
            dripLandParticle.pickSprite(this.sprite);
            return dripLandParticle;
        }
    }

    public static class HoneyFallProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public HoneyFallProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            HoneyFallAndLandParticle honeyFallAndLandParticle = new HoneyFallAndLandParticle(clientLevel, d, d2, d3, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
            honeyFallAndLandParticle.gravity = 0.01f;
            honeyFallAndLandParticle.setColor(0.582f, 0.448f, 0.082f);
            honeyFallAndLandParticle.pickSprite(this.sprite);
            return honeyFallAndLandParticle;
        }
    }

    public static class HoneyHangProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public HoneyHangProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            DripHangParticle dripHangParticle = new DripHangParticle(clientLevel, d, d2, d3, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
            dripHangParticle.gravity *= 0.01f;
            dripHangParticle.lifetime = 100;
            dripHangParticle.setColor(0.622f, 0.508f, 0.082f);
            dripHangParticle.pickSprite(this.sprite);
            return dripHangParticle;
        }
    }

    public static class LavaLandProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public LavaLandProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            DripLandParticle dripLandParticle = new DripLandParticle(clientLevel, d, d2, d3, Fluids.LAVA);
            dripLandParticle.setColor(1.0f, 0.2857143f, 0.083333336f);
            dripLandParticle.pickSprite(this.sprite);
            return dripLandParticle;
        }
    }

    public static class LavaFallProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public LavaFallProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            FallAndLandParticle fallAndLandParticle = new FallAndLandParticle(clientLevel, d, d2, d3, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
            fallAndLandParticle.setColor(1.0f, 0.2857143f, 0.083333336f);
            fallAndLandParticle.pickSprite(this.sprite);
            return fallAndLandParticle;
        }
    }

    public static class LavaHangProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public LavaHangProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            CoolingDripHangParticle coolingDripHangParticle = new CoolingDripHangParticle(clientLevel, d, d2, d3, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
            coolingDripHangParticle.pickSprite(this.sprite);
            return coolingDripHangParticle;
        }
    }

    public static class WaterFallProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public WaterFallProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            FallAndLandParticle fallAndLandParticle = new FallAndLandParticle(clientLevel, d, d2, d3, Fluids.WATER, ParticleTypes.SPLASH);
            fallAndLandParticle.setColor(0.2f, 0.3f, 1.0f);
            fallAndLandParticle.pickSprite(this.sprite);
            return fallAndLandParticle;
        }
    }

    public static class WaterHangProvider
    implements ParticleProvider<SimpleParticleType> {
        protected final SpriteSet sprite;

        public WaterHangProvider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double d2, double d3, double d4, double d5, double d6) {
            DripHangParticle dripHangParticle = new DripHangParticle(clientLevel, d, d2, d3, Fluids.WATER, ParticleTypes.FALLING_WATER);
            dripHangParticle.setColor(0.2f, 0.3f, 1.0f);
            dripHangParticle.pickSprite(this.sprite);
            return dripHangParticle;
        }
    }

    static class DripLandParticle
    extends DripParticle {
        private DripLandParticle(ClientLevel clientLevel, double d, double d2, double d3, Fluid fluid) {
            super(clientLevel, d, d2, d3, fluid);
            this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
        }
    }

    static class FallingParticle
    extends DripParticle {
        private FallingParticle(ClientLevel clientLevel, double d, double d2, double d3, Fluid fluid) {
            super(clientLevel, d, d2, d3, fluid);
            this.lifetime = (int)(64.0 / (Math.random() * 0.8 + 0.2));
        }

        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
            }
        }
    }

    static class HoneyFallAndLandParticle
    extends FallAndLandParticle {
        private HoneyFallAndLandParticle(ClientLevel clientLevel, double d, double d2, double d3, Fluid fluid, ParticleOptions particleOptions) {
            super(clientLevel, d, d2, d3, fluid, particleOptions);
        }

        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                this.level.playLocalSound(this.x + 0.5, this.y, this.z + 0.5, SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, 0.3f + this.level.random.nextFloat() * 2.0f / 3.0f, 1.0f, false);
            }
        }
    }

    static class FallAndLandParticle
    extends FallingParticle {
        protected final ParticleOptions landParticle;

        private FallAndLandParticle(ClientLevel clientLevel, double d, double d2, double d3, Fluid fluid, ParticleOptions particleOptions) {
            super(clientLevel, d, d2, d3, fluid);
            this.landParticle = particleOptions;
        }

        @Override
        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            }
        }
    }

    static class CoolingDripHangParticle
    extends DripHangParticle {
        private CoolingDripHangParticle(ClientLevel clientLevel, double d, double d2, double d3, Fluid fluid, ParticleOptions particleOptions) {
            super(clientLevel, d, d2, d3, fluid, particleOptions);
        }

        @Override
        protected void preMoveUpdate() {
            this.rCol = 1.0f;
            this.gCol = 16.0f / (float)(40 - this.lifetime + 16);
            this.bCol = 4.0f / (float)(40 - this.lifetime + 8);
            super.preMoveUpdate();
        }
    }

    static class DripHangParticle
    extends DripParticle {
        private final ParticleOptions fallingParticle;

        private DripHangParticle(ClientLevel clientLevel, double d, double d2, double d3, Fluid fluid, ParticleOptions particleOptions) {
            super(clientLevel, d, d2, d3, fluid);
            this.fallingParticle = particleOptions;
            this.gravity *= 0.02f;
            this.lifetime = 40;
        }

        @Override
        protected void preMoveUpdate() {
            if (this.lifetime-- <= 0) {
                this.remove();
                this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
            }
        }

        @Override
        protected void postMoveUpdate() {
            this.xd *= 0.02;
            this.yd *= 0.02;
            this.zd *= 0.02;
        }
    }

}

