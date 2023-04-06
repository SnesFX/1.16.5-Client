/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.decoration;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddPaintingPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class Painting
extends HangingEntity {
    public Motive motive;

    public Painting(EntityType<? extends Painting> entityType, Level level) {
        super(entityType, level);
    }

    public Painting(Level level, BlockPos blockPos, Direction direction) {
        super(EntityType.PAINTING, level, blockPos);
        Motive motive;
        ArrayList arrayList = Lists.newArrayList();
        int n = 0;
        Iterator<Object> iterator = Registry.MOTIVE.iterator();
        while (iterator.hasNext()) {
            this.motive = motive = (Motive)iterator.next();
            this.setDirection(direction);
            if (!this.survives()) continue;
            arrayList.add(motive);
            int n2 = motive.getWidth() * motive.getHeight();
            if (n2 <= n) continue;
            n = n2;
        }
        if (!arrayList.isEmpty()) {
            iterator = arrayList.iterator();
            while (iterator.hasNext()) {
                motive = (Motive)iterator.next();
                if (motive.getWidth() * motive.getHeight() >= n) continue;
                iterator.remove();
            }
            this.motive = (Motive)arrayList.get(this.random.nextInt(arrayList.size()));
        }
        this.setDirection(direction);
    }

    public Painting(Level level, BlockPos blockPos, Direction direction, Motive motive) {
        this(level, blockPos, direction);
        this.motive = motive;
        this.setDirection(direction);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putString("Motive", Registry.MOTIVE.getKey(this.motive).toString());
        compoundTag.putByte("Facing", (byte)this.direction.get2DDataValue());
        super.addAdditionalSaveData(compoundTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        this.motive = Registry.MOTIVE.get(ResourceLocation.tryParse(compoundTag.getString("Motive")));
        this.direction = Direction.from2DDataValue(compoundTag.getByte("Facing"));
        super.readAdditionalSaveData(compoundTag);
        this.setDirection(this.direction);
    }

    @Override
    public int getWidth() {
        if (this.motive == null) {
            return 1;
        }
        return this.motive.getWidth();
    }

    @Override
    public int getHeight() {
        if (this.motive == null) {
            return 1;
        }
        return this.motive.getHeight();
    }

    @Override
    public void dropItem(@Nullable Entity entity) {
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            return;
        }
        this.playSound(SoundEvents.PAINTING_BREAK, 1.0f, 1.0f);
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (player.abilities.instabuild) {
                return;
            }
        }
        this.spawnAtLocation(Items.PAINTING);
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0f, 1.0f);
    }

    @Override
    public void moveTo(double d, double d2, double d3, float f, float f2) {
        this.setPos(d, d2, d3);
    }

    @Override
    public void lerpTo(double d, double d2, double d3, float f, float f2, int n, boolean bl) {
        BlockPos blockPos = this.pos.offset(d - this.getX(), d2 - this.getY(), d3 - this.getZ());
        this.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddPaintingPacket(this);
    }
}

