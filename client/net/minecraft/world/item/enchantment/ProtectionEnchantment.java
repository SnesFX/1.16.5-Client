/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class ProtectionEnchantment
extends Enchantment {
    public final Type type;

    public ProtectionEnchantment(Enchantment.Rarity rarity, Type type, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, type == Type.FALL ? EnchantmentCategory.ARMOR_FEET : EnchantmentCategory.ARMOR, arrequipmentSlot);
        this.type = type;
    }

    @Override
    public int getMinCost(int n) {
        return this.type.getMinCost() + (n - 1) * this.type.getLevelCost();
    }

    @Override
    public int getMaxCost(int n) {
        return this.getMinCost(n) + this.type.getLevelCost();
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public int getDamageProtection(int n, DamageSource damageSource) {
        if (damageSource.isBypassInvul()) {
            return 0;
        }
        if (this.type == Type.ALL) {
            return n;
        }
        if (this.type == Type.FIRE && damageSource.isFire()) {
            return n * 2;
        }
        if (this.type == Type.FALL && damageSource == DamageSource.FALL) {
            return n * 3;
        }
        if (this.type == Type.EXPLOSION && damageSource.isExplosion()) {
            return n * 2;
        }
        if (this.type == Type.PROJECTILE && damageSource.isProjectile()) {
            return n * 2;
        }
        return 0;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        if (enchantment instanceof ProtectionEnchantment) {
            ProtectionEnchantment protectionEnchantment = (ProtectionEnchantment)enchantment;
            if (this.type == protectionEnchantment.type) {
                return false;
            }
            return this.type == Type.FALL || protectionEnchantment.type == Type.FALL;
        }
        return super.checkCompatibility(enchantment);
    }

    public static int getFireAfterDampener(LivingEntity livingEntity, int n) {
        int n2 = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, livingEntity);
        if (n2 > 0) {
            n -= Mth.floor((float)n * ((float)n2 * 0.15f));
        }
        return n;
    }

    public static double getExplosionKnockbackAfterDampener(LivingEntity livingEntity, double d) {
        int n = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, livingEntity);
        if (n > 0) {
            d -= (double)Mth.floor(d * (double)((float)n * 0.15f));
        }
        return d;
    }

    public static enum Type {
        ALL("all", 1, 11),
        FIRE("fire", 10, 8),
        FALL("fall", 5, 6),
        EXPLOSION("explosion", 5, 8),
        PROJECTILE("projectile", 3, 6);
        
        private final String name;
        private final int minCost;
        private final int levelCost;

        private Type(String string2, int n2, int n3) {
            this.name = string2;
            this.minCost = n2;
            this.levelCost = n3;
        }

        public int getMinCost() {
            return this.minCost;
        }

        public int getLevelCost() {
            return this.levelCost;
        }
    }

}

