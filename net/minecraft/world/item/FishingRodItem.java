/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import java.util.Random;
import java.util.function.Consumer;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class FishingRodItem
extends Item
implements Vanishable {
    public FishingRodItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player2, InteractionHand interactionHand) {
        ItemStack itemStack = player2.getItemInHand(interactionHand);
        if (player2.fishing != null) {
            if (!level.isClientSide) {
                int n = player2.fishing.retrieve(itemStack);
                itemStack.hurtAndBreak(n, player2, player -> player.broadcastBreakEvent(interactionHand));
            }
            level.playSound(null, player2.getX(), player2.getY(), player2.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0f, 0.4f / (random.nextFloat() * 0.4f + 0.8f));
        } else {
            level.playSound(null, player2.getX(), player2.getY(), player2.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (random.nextFloat() * 0.4f + 0.8f));
            if (!level.isClientSide) {
                int n = EnchantmentHelper.getFishingSpeedBonus(itemStack);
                int n2 = EnchantmentHelper.getFishingLuckBonus(itemStack);
                level.addFreshEntity(new FishingHook(player2, level, n2, n));
            }
            player2.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}

