/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.crafting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class FireworkRocketRecipe
extends CustomRecipe {
    private static final Ingredient PAPER_INGREDIENT = Ingredient.of(Items.PAPER);
    private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);
    private static final Ingredient STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);

    public FireworkRocketRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        boolean bl = false;
        int n = 0;
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemStack = craftingContainer.getItem(i);
            if (itemStack.isEmpty()) continue;
            if (PAPER_INGREDIENT.test(itemStack)) {
                if (bl) {
                    return false;
                }
                bl = true;
                continue;
            }
            if (!(GUNPOWDER_INGREDIENT.test(itemStack) ? ++n > 3 : !STAR_INGREDIENT.test(itemStack))) continue;
            return false;
        }
        return bl && n >= 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 3);
        CompoundTag compoundTag = itemStack.getOrCreateTagElement("Fireworks");
        ListTag listTag = new ListTag();
        int n = 0;
        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            CompoundTag compoundTag2;
            ItemStack itemStack2 = craftingContainer.getItem(i);
            if (itemStack2.isEmpty()) continue;
            if (GUNPOWDER_INGREDIENT.test(itemStack2)) {
                ++n;
                continue;
            }
            if (!STAR_INGREDIENT.test(itemStack2) || (compoundTag2 = itemStack2.getTagElement("Explosion")) == null) continue;
            listTag.add(compoundTag2);
        }
        compoundTag.putByte("Flight", (byte)n);
        if (!listTag.isEmpty()) {
            compoundTag.put("Explosions", listTag);
        }
        return itemStack;
    }

    @Override
    public boolean canCraftInDimensions(int n, int n2) {
        return n * n2 >= 2;
    }

    @Override
    public ItemStack getResultItem() {
        return new ItemStack(Items.FIREWORK_ROCKET);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_ROCKET;
    }
}

