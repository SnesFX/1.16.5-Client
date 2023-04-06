/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import java.util.Random;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnderpearlItem
extends Item {
    public EnderpearlItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (random.nextFloat() * 0.4f + 0.8f));
        player.getCooldowns().addCooldown(this, 20);
        if (!level.isClientSide) {
            ThrownEnderpearl thrownEnderpearl = new ThrownEnderpearl(level, player);
            thrownEnderpearl.setItem(itemStack);
            thrownEnderpearl.shootFromRotation(player, player.xRot, player.yRot, 0.0f, 1.5f, 1.0f);
            level.addFreshEntity(thrownEnderpearl);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.abilities.instabuild) {
            itemStack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}

