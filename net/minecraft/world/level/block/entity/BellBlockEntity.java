/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;

public class BellBlockEntity
extends BlockEntity
implements TickableBlockEntity {
    private long lastRingTimestamp;
    public int ticks;
    public boolean shaking;
    public Direction clickDirection;
    private List<LivingEntity> nearbyEntities;
    private boolean resonating;
    private int resonationTicks;

    public BellBlockEntity() {
        super(BlockEntityType.BELL);
    }

    @Override
    public boolean triggerEvent(int n, int n2) {
        if (n == 1) {
            this.updateEntities();
            this.resonationTicks = 0;
            this.clickDirection = Direction.from3DDataValue(n2);
            this.ticks = 0;
            this.shaking = true;
            return true;
        }
        return super.triggerEvent(n, n2);
    }

    @Override
    public void tick() {
        if (this.shaking) {
            ++this.ticks;
        }
        if (this.ticks >= 50) {
            this.shaking = false;
            this.ticks = 0;
        }
        if (this.ticks >= 5 && this.resonationTicks == 0 && this.areRaidersNearby()) {
            this.resonating = true;
            this.playResonateSound();
        }
        if (this.resonating) {
            if (this.resonationTicks < 40) {
                ++this.resonationTicks;
            } else {
                this.makeRaidersGlow(this.level);
                this.showBellParticles(this.level);
                this.resonating = false;
            }
        }
    }

    private void playResonateSound() {
        this.level.playSound(null, this.getBlockPos(), SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public void onHit(Direction direction) {
        BlockPos blockPos = this.getBlockPos();
        this.clickDirection = direction;
        if (this.shaking) {
            this.ticks = 0;
        } else {
            this.shaking = true;
        }
        this.level.blockEvent(blockPos, this.getBlockState().getBlock(), 1, direction.get3DDataValue());
    }

    private void updateEntities() {
        BlockPos blockPos = this.getBlockPos();
        if (this.level.getGameTime() > this.lastRingTimestamp + 60L || this.nearbyEntities == null) {
            this.lastRingTimestamp = this.level.getGameTime();
            AABB aABB = new AABB(blockPos).inflate(48.0);
            this.nearbyEntities = this.level.getEntitiesOfClass(LivingEntity.class, aABB);
        }
        if (!this.level.isClientSide) {
            for (LivingEntity livingEntity : this.nearbyEntities) {
                if (!livingEntity.isAlive() || livingEntity.removed || !blockPos.closerThan(livingEntity.position(), 32.0)) continue;
                livingEntity.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, this.level.getGameTime());
            }
        }
    }

    private boolean areRaidersNearby() {
        BlockPos blockPos = this.getBlockPos();
        for (LivingEntity livingEntity : this.nearbyEntities) {
            if (!livingEntity.isAlive() || livingEntity.removed || !blockPos.closerThan(livingEntity.position(), 32.0) || !livingEntity.getType().is(EntityTypeTags.RAIDERS)) continue;
            return true;
        }
        return false;
    }

    private void makeRaidersGlow(Level level) {
        if (level.isClientSide) {
            return;
        }
        this.nearbyEntities.stream().filter(this::isRaiderWithinRange).forEach(this::glow);
    }

    private void showBellParticles(Level level) {
        if (!level.isClientSide) {
            return;
        }
        BlockPos blockPos = this.getBlockPos();
        MutableInt mutableInt = new MutableInt(16700985);
        int n = (int)this.nearbyEntities.stream().filter(livingEntity -> blockPos.closerThan(livingEntity.position(), 48.0)).count();
        this.nearbyEntities.stream().filter(this::isRaiderWithinRange).forEach(livingEntity -> {
            float f = 1.0f;
            float f2 = Mth.sqrt((livingEntity.getX() - (double)blockPos.getX()) * (livingEntity.getX() - (double)blockPos.getX()) + (livingEntity.getZ() - (double)blockPos.getZ()) * (livingEntity.getZ() - (double)blockPos.getZ()));
            double d = (double)((float)blockPos.getX() + 0.5f) + (double)(1.0f / f2) * (livingEntity.getX() - (double)blockPos.getX());
            double d2 = (double)((float)blockPos.getZ() + 0.5f) + (double)(1.0f / f2) * (livingEntity.getZ() - (double)blockPos.getZ());
            int n2 = Mth.clamp((n - 21) / -2, 3, 15);
            for (int i = 0; i < n2; ++i) {
                int n3 = mutableInt.addAndGet(5);
                double d3 = (double)FastColor.ARGB32.red(n3) / 255.0;
                double d4 = (double)FastColor.ARGB32.green(n3) / 255.0;
                double d5 = (double)FastColor.ARGB32.blue(n3) / 255.0;
                level.addParticle(ParticleTypes.ENTITY_EFFECT, d, (float)blockPos.getY() + 0.5f, d2, d3, d4, d5);
            }
        });
    }

    private boolean isRaiderWithinRange(LivingEntity livingEntity) {
        return livingEntity.isAlive() && !livingEntity.removed && this.getBlockPos().closerThan(livingEntity.position(), 48.0) && livingEntity.getType().is(EntityTypeTags.RAIDERS);
    }

    private void glow(LivingEntity livingEntity) {
        livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
    }
}

