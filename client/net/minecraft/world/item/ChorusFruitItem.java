/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import java.util.Random;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ChorusFruitItem
extends Item {
    public ChorusFruitItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        ItemStack itemStack2 = super.finishUsingItem(itemStack, level, livingEntity);
        if (!level.isClientSide) {
            double d = livingEntity.getX();
            double d2 = livingEntity.getY();
            double d3 = livingEntity.getZ();
            for (int i = 0; i < 16; ++i) {
                double d4 = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
                double d5 = Mth.clamp(livingEntity.getY() + (double)(livingEntity.getRandom().nextInt(16) - 8), 0.0, (double)(level.getHeight() - 1));
                double d6 = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
                if (livingEntity.isPassenger()) {
                    livingEntity.stopRiding();
                }
                if (!livingEntity.randomTeleport(d4, d5, d6, true)) continue;
                SoundEvent soundEvent = livingEntity instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                level.playSound(null, d, d2, d3, soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f);
                livingEntity.playSound(soundEvent, 1.0f, 1.0f);
                break;
            }
            if (livingEntity instanceof Player) {
                ((Player)livingEntity).getCooldowns().addCooldown(this, 20);
            }
        }
        return itemStack2;
    }
}

