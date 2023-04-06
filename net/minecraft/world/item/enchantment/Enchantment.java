/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item.enchantment;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class Enchantment {
    private final EquipmentSlot[] slots;
    private final Rarity rarity;
    public final EnchantmentCategory category;
    @Nullable
    protected String descriptionId;

    @Nullable
    public static Enchantment byId(int n) {
        return (Enchantment)Registry.ENCHANTMENT.byId(n);
    }

    protected Enchantment(Rarity rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot[] arrequipmentSlot) {
        this.rarity = rarity;
        this.category = enchantmentCategory;
        this.slots = arrequipmentSlot;
    }

    public Map<EquipmentSlot, ItemStack> getSlotItems(LivingEntity livingEntity) {
        EnumMap enumMap = Maps.newEnumMap(EquipmentSlot.class);
        for (EquipmentSlot equipmentSlot : this.slots) {
            ItemStack itemStack = livingEntity.getItemBySlot(equipmentSlot);
            if (itemStack.isEmpty()) continue;
            enumMap.put(equipmentSlot, itemStack);
        }
        return enumMap;
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    public int getMinLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return 1;
    }

    public int getMinCost(int n) {
        return 1 + n * 10;
    }

    public int getMaxCost(int n) {
        return this.getMinCost(n) + 5;
    }

    public int getDamageProtection(int n, DamageSource damageSource) {
        return 0;
    }

    public float getDamageBonus(int n, MobType mobType) {
        return 0.0f;
    }

    public final boolean isCompatibleWith(Enchantment enchantment) {
        return this.checkCompatibility(enchantment) && enchantment.checkCompatibility(this);
    }

    protected boolean checkCompatibility(Enchantment enchantment) {
        return this != enchantment;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("enchantment", Registry.ENCHANTMENT.getKey(this));
        }
        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public Component getFullname(int n) {
        TranslatableComponent translatableComponent = new TranslatableComponent(this.getDescriptionId());
        if (this.isCurse()) {
            translatableComponent.withStyle(ChatFormatting.RED);
        } else {
            translatableComponent.withStyle(ChatFormatting.GRAY);
        }
        if (n != 1 || this.getMaxLevel() != 1) {
            translatableComponent.append(" ").append(new TranslatableComponent("enchantment.level." + n));
        }
        return translatableComponent;
    }

    public boolean canEnchant(ItemStack itemStack) {
        return this.category.canEnchant(itemStack.getItem());
    }

    public void doPostAttack(LivingEntity livingEntity, Entity entity, int n) {
    }

    public void doPostHurt(LivingEntity livingEntity, Entity entity, int n) {
    }

    public boolean isTreasureOnly() {
        return false;
    }

    public boolean isCurse() {
        return false;
    }

    public boolean isTradeable() {
        return true;
    }

    public boolean isDiscoverable() {
        return true;
    }

    public static enum Rarity {
        COMMON(10),
        UNCOMMON(5),
        RARE(2),
        VERY_RARE(1);
        
        private final int weight;

        private Rarity(int n2) {
            this.weight = n2;
        }

        public int getWeight() {
            return this.weight;
        }
    }

}

