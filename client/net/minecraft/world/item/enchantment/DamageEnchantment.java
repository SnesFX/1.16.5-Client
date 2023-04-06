/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import java.util.Random;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DamageEnchantment
extends Enchantment {
    private static final String[] NAMES = new String[]{"all", "undead", "arthropods"};
    private static final int[] MIN_COST = new int[]{1, 5, 5};
    private static final int[] LEVEL_COST = new int[]{11, 8, 8};
    private static final int[] LEVEL_COST_SPAN = new int[]{20, 20, 20};
    public final int type;

    public DamageEnchantment(Enchantment.Rarity rarity, int n, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.WEAPON, arrequipmentSlot);
        this.type = n;
    }

    @Override
    public int getMinCost(int n) {
        return MIN_COST[this.type] + (n - 1) * LEVEL_COST[this.type];
    }

    @Override
    public int getMaxCost(int n) {
        return this.getMinCost(n) + LEVEL_COST_SPAN[this.type];
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float getDamageBonus(int n, MobType mobType) {
        if (this.type == 0) {
            return 1.0f + (float)Math.max(0, n - 1) * 0.5f;
        }
        if (this.type == 1 && mobType == MobType.UNDEAD) {
            return (float)n * 2.5f;
        }
        if (this.type == 2 && mobType == MobType.ARTHROPOD) {
            return (float)n * 2.5f;
        }
        return 0.0f;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return !(enchantment instanceof DamageEnchantment);
    }

    @Override
    public boolean canEnchant(ItemStack itemStack) {
        if (itemStack.getItem() instanceof AxeItem) {
            return true;
        }
        return super.canEnchant(itemStack);
    }

    @Override
    public void doPostAttack(LivingEntity livingEntity, Entity entity, int n) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)entity;
            if (this.type == 2 && livingEntity2.getMobType() == MobType.ARTHROPOD) {
                int n2 = 20 + livingEntity.getRandom().nextInt(10 * n);
                livingEntity2.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, n2, 3));
            }
        }
    }
}

