/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block.entity;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;

public class EnderChestBlockEntity
extends BlockEntity
implements LidBlockEntity,
TickableBlockEntity {
    public float openness;
    public float oOpenness;
    public int openCount;
    private int tickInterval;

    public EnderChestBlockEntity() {
        super(BlockEntityType.ENDER_CHEST);
    }

    @Override
    public void tick() {
        double d;
        if (++this.tickInterval % 20 * 4 == 0) {
            this.level.blockEvent(this.worldPosition, Blocks.ENDER_CHEST, 1, this.openCount);
        }
        this.oOpenness = this.openness;
        int n = this.worldPosition.getX();
        int n2 = this.worldPosition.getY();
        int n3 = this.worldPosition.getZ();
        float f = 0.1f;
        if (this.openCount > 0 && this.openness == 0.0f) {
            double d2 = (double)n + 0.5;
            d = (double)n3 + 0.5;
            this.level.playSound(null, d2, (double)n2 + 0.5, d, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS, 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
        }
        if (this.openCount == 0 && this.openness > 0.0f || this.openCount > 0 && this.openness < 1.0f) {
            float f2 = this.openness;
            this.openness = this.openCount > 0 ? (this.openness += 0.1f) : (this.openness -= 0.1f);
            if (this.openness > 1.0f) {
                this.openness = 1.0f;
            }
            float f3 = 0.5f;
            if (this.openness < 0.5f && f2 >= 0.5f) {
                d = (double)n + 0.5;
                double d3 = (double)n3 + 0.5;
                this.level.playSound(null, d, (double)n2 + 0.5, d3, SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
            }
            if (this.openness < 0.0f) {
                this.openness = 0.0f;
            }
        }
    }

    @Override
    public boolean triggerEvent(int n, int n2) {
        if (n == 1) {
            this.openCount = n2;
            return true;
        }
        return super.triggerEvent(n, n2);
    }

    @Override
    public void setRemoved() {
        this.clearCache();
        super.setRemoved();
    }

    public void startOpen() {
        ++this.openCount;
        this.level.blockEvent(this.worldPosition, Blocks.ENDER_CHEST, 1, this.openCount);
    }

    public void stopOpen() {
        --this.openCount;
        this.level.blockEvent(this.worldPosition, Blocks.ENDER_CHEST, 1, this.openCount);
    }

    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return !(player.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) > 64.0);
    }

    @Override
    public float getOpenNess(float f) {
        return Mth.lerp(f, this.oOpenness, this.openness);
    }
}

