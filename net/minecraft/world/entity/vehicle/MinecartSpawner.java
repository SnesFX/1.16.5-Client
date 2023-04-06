/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartSpawner
extends AbstractMinecart {
    private final BaseSpawner spawner = new BaseSpawner(){

        @Override
        public void broadcastEvent(int n) {
            MinecartSpawner.this.level.broadcastEntityEvent(MinecartSpawner.this, (byte)n);
        }

        @Override
        public Level getLevel() {
            return MinecartSpawner.this.level;
        }

        @Override
        public BlockPos getPos() {
            return MinecartSpawner.this.blockPosition();
        }
    };

    public MinecartSpawner(EntityType<? extends MinecartSpawner> entityType, Level level) {
        super(entityType, level);
    }

    public MinecartSpawner(Level level, double d, double d2, double d3) {
        super(EntityType.SPAWNER_MINECART, level, d, d2, d3);
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.SPAWNER;
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.SPAWNER.defaultBlockState();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.spawner.load(compoundTag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.spawner.save(compoundTag);
    }

    @Override
    public void handleEntityEvent(byte by) {
        this.spawner.onEventTriggered(by);
    }

    @Override
    public void tick() {
        super.tick();
        this.spawner.tick();
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

}

