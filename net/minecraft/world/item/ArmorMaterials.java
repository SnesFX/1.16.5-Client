/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public enum ArmorMaterials implements ArmorMaterial
{
    LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, () -> Ingredient.of(Items.LEATHER)),
    CHAIN("chainmail", 15, new int[]{1, 4, 5, 2}, 12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0f, 0.0f, () -> Ingredient.of(Items.IRON_INGOT)),
    IRON("iron", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0f, 0.0f, () -> Ingredient.of(Items.IRON_INGOT)),
    GOLD("gold", 7, new int[]{1, 3, 5, 2}, 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0f, 0.0f, () -> Ingredient.of(Items.GOLD_INGOT)),
    DIAMOND("diamond", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0f, 0.0f, () -> Ingredient.of(Items.DIAMOND)),
    TURTLE("turtle", 25, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0f, 0.0f, () -> Ingredient.of(Items.SCUTE)),
    NETHERITE("netherite", 37, new int[]{3, 6, 8, 3}, 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0f, 0.1f, () -> Ingredient.of(Items.NETHERITE_INGOT));
    
    private static final int[] HEALTH_PER_SLOT;
    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    private ArmorMaterials(String string2, int n2, int[] arrn, int n3, SoundEvent soundEvent, float f, float f2, Supplier<Ingredient> supplier) {
        this.name = string2;
        this.durabilityMultiplier = n2;
        this.slotProtections = arrn;
        this.enchantmentValue = n3;
        this.sound = soundEvent;
        this.toughness = f;
        this.knockbackResistance = f2;
        this.repairIngredient = new LazyLoadedValue<Ingredient>(supplier);
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot equipmentSlot) {
        return HEALTH_PER_SLOT[equipmentSlot.getIndex()] * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot equipmentSlot) {
        return this.slotProtections[equipmentSlot.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.sound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    static {
        HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    }
}

