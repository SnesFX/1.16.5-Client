/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class ThornsEnchantment
extends Enchantment {
    public ThornsEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.ARMOR_CHEST, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return 10 + 20 * (n - 1);
    }

    @Override
    public int getMaxCost(int n) {
        return super.getMinCost(n) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ArmorItem) {
            return true;
        }
        return super.canEnchant(itemStack);
    }

    @Override
    public void doPostHurt(LivingEntity livingEntity2, Entity entity, int n) {
        Random random = livingEntity2.getRandom();
        Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomItemWith(Enchantments.THORNS, livingEntity2);
        if (ThornsEnchantment.shouldHit(n, random)) {
            if (entity != null) {
                entity.hurt(DamageSource.thorns(livingEntity2), ThornsEnchantment.getDamage(n, random));
            }
            if (entry != null) {
                entry.getValue().hurtAndBreak(2, livingEntity2, livingEntity -> livingEntity.broadcastBreakEvent((EquipmentSlot)((Object)((Object)entry.getKey()))));
            }
        }
    }

    public static boolean shouldHit(int n, Random random) {
        if (n <= 0) {
            return false;
        }
        return random.nextFloat() < 0.15f * (float)n;
    }

    public static int getDamage(int n, Random random) {
        if (n > 10) {
            return n - 10;
        }
        return 1 + random.nextInt(4);
    }
}

