/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantmentTableBlockEntity
extends BlockEntity
implements Nameable,
TickableBlockEntity {
    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    public float rot;
    public float oRot;
    public float tRot;
    private static final Random RANDOM = new Random();
    private Component name;

    public EnchantmentTableBlockEntity() {
        super(BlockEntityType.ENCHANTING_TABLE);
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        if (this.hasCustomName()) {
            compoundTag.putString("CustomName", Component.Serializer.toJson(this.name));
        }
        return compoundTag;
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        super.load(blockState, compoundTag);
        if (compoundTag.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(compoundTag.getString("CustomName"));
        }
    }

    @Override
    public void tick() {
        float f;
        this.oOpen = this.open;
        this.oRot = this.rot;
        Player player = this.level.getNearestPlayer((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5, 3.0, false);
        if (player != null) {
            double d = player.getX() - ((double)this.worldPosition.getX() + 0.5);
            double d2 = player.getZ() - ((double)this.worldPosition.getZ() + 0.5);
            this.tRot = (float)Mth.atan2(d2, d);
            this.open += 0.1f;
            if (this.open < 0.5f || RANDOM.nextInt(40) == 0) {
                float f2 = this.flipT;
                do {
                    this.flipT += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
                } while (f2 == this.flipT);
            }
        } else {
            this.tRot += 0.02f;
            this.open -= 0.1f;
        }
        while (this.rot >= 3.1415927f) {
            this.rot -= 6.2831855f;
        }
        while (this.rot < -3.1415927f) {
            this.rot += 6.2831855f;
        }
        while (this.tRot >= 3.1415927f) {
            this.tRot -= 6.2831855f;
        }
        while (this.tRot < -3.1415927f) {
            this.tRot += 6.2831855f;
        }
        for (f = this.tRot - this.rot; f >= 3.1415927f; f -= 6.2831855f) {
        }
        while (f < -3.1415927f) {
            f += 6.2831855f;
        }
        this.rot += f * 0.4f;
        this.open = Mth.clamp(this.open, 0.0f, 1.0f);
        ++this.time;
        this.oFlip = this.flip;
        float f3 = (this.flipT - this.flip) * 0.4f;
        float f4 = 0.2f;
        f3 = Mth.clamp(f3, -0.2f, 0.2f);
        this.flipA += (f3 - this.flipA) * 0.9f;
        this.flip += this.flipA;
    }

    @Override
    public Component getName() {
        if (this.name != null) {
            return this.name;
        }
        return new TranslatableComponent("container.enchant");
    }

    public void setCustomName(@Nullable Component component) {
        this.name = component;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }
}

