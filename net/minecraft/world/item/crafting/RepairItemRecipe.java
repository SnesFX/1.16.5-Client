/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class RepairItemRecipe
extends CustomRecipe {
    public RepairItemRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        ArrayList arrayList = Lists.newArrayList();
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemStack = craftingContainer.getItem(i);
            if (itemStack.isEmpty()) continue;
            arrayList.add(itemStack);
            if (arrayList.size() <= 1) continue;
            ItemStack itemStack2 = (ItemStack)arrayList.get(0);
            if (itemStack.getItem() == itemStack2.getItem() && itemStack2.getCount() == 1 && itemStack.getCount() == 1 && itemStack2.getItem().canBeDepleted()) continue;
            return false;
        }
        return arrayList.size() == 2;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        ItemStack itemStack;
        Object object;
        ArrayList arrayList = Lists.newArrayList();
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            itemStack = craftingContainer.getItem(i);
            if (itemStack.isEmpty()) continue;
            arrayList.add(itemStack);
            if (arrayList.size() <= 1) continue;
            object = (ItemStack)arrayList.get(0);
            if (itemStack.getItem() == ((ItemStack)object).getItem() && ((ItemStack)object).getCount() == 1 && itemStack.getCount() == 1 && ((ItemStack)object).getItem().canBeDepleted()) continue;
            return ItemStack.EMPTY;
        }
        if (arrayList.size() == 2) {
            ItemStack itemStack2 = (ItemStack)arrayList.get(0);
            itemStack = (ItemStack)arrayList.get(1);
            if (itemStack2.getItem() == itemStack.getItem() && itemStack2.getCount() == 1 && itemStack.getCount() == 1 && itemStack2.getItem().canBeDepleted()) {
                object = itemStack2.getItem();
                int n = ((Item)object).getMaxDamage() - itemStack2.getDamageValue();
                int n2 = ((Item)object).getMaxDamage() - itemStack.getDamageValue();
                int n3 = n + n2 + ((Item)object).getMaxDamage() * 5 / 100;
                int n4 = ((Item)object).getMaxDamage() - n3;
                if (n4 < 0) {
                    n4 = 0;
                }
                ItemStack itemStack3 = new ItemStack(itemStack2.getItem());
                itemStack3.setDamageValue(n4);
                HashMap hashMap = Maps.newHashMap();
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack2);
                Map<Enchantment, Integer> map2 = EnchantmentHelper.getEnchantments(itemStack);
                Registry.ENCHANTMENT.stream().filter(Enchantment::isCurse).forEach(enchantment -> {
                    int n = Math.max(map.getOrDefault(enchantment, 0), map2.getOrDefault(enchantment, 0));
                    if (n > 0) {
                        hashMap.put(enchantment, n);
                    }
                });
                if (!hashMap.isEmpty()) {
                    EnchantmentHelper.setEnchantments(hashMap, itemStack3);
                }
                return itemStack3;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int n, int n2) {
        return n * n2 >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.REPAIR_ITEM;
    }
}

