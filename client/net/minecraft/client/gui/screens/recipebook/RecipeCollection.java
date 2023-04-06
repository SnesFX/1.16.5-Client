/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeCollection {
    private final List<Recipe<?>> recipes;
    private final boolean singleResultItem;
    private final Set<Recipe<?>> craftable = Sets.newHashSet();
    private final Set<Recipe<?>> fitsDimensions = Sets.newHashSet();
    private final Set<Recipe<?>> known = Sets.newHashSet();

    public RecipeCollection(List<Recipe<?>> list) {
        this.recipes = ImmutableList.copyOf(list);
        this.singleResultItem = list.size() <= 1 ? true : RecipeCollection.allRecipesHaveSameResult(list);
    }

    private static boolean allRecipesHaveSameResult(List<Recipe<?>> list) {
        int n = list.size();
        ItemStack itemStack = list.get(0).getResultItem();
        for (int i = 1; i < n; ++i) {
            ItemStack itemStack2 = list.get(i).getResultItem();
            if (ItemStack.isSame(itemStack, itemStack2) && ItemStack.tagMatches(itemStack, itemStack2)) continue;
            return false;
        }
        return true;
    }

    public boolean hasKnownRecipes() {
        return !this.known.isEmpty();
    }

    public void updateKnownRecipes(RecipeBook recipeBook) {
        for (Recipe<?> recipe : this.recipes) {
            if (!recipeBook.contains(recipe)) continue;
            this.known.add(recipe);
        }
    }

    public void canCraft(StackedContents stackedContents, int n, int n2, RecipeBook recipeBook) {
        for (Recipe<?> recipe : this.recipes) {
            boolean bl;
            boolean bl2 = bl = recipe.canCraftInDimensions(n, n2) && recipeBook.contains(recipe);
            if (bl) {
                this.fitsDimensions.add(recipe);
            } else {
                this.fitsDimensions.remove(recipe);
            }
            if (bl && stackedContents.canCraft(recipe, null)) {
                this.craftable.add(recipe);
                continue;
            }
            this.craftable.remove(recipe);
        }
    }

    public boolean isCraftable(Recipe<?> recipe) {
        return this.craftable.contains(recipe);
    }

    public boolean hasCraftable() {
        return !this.craftable.isEmpty();
    }

    public boolean hasFitting() {
        return !this.fitsDimensions.isEmpty();
    }

    public List<Recipe<?>> getRecipes() {
        return this.recipes;
    }

    public List<Recipe<?>> getRecipes(boolean bl) {
        ArrayList arrayList = Lists.newArrayList();
        Set<Recipe<?>> set = bl ? this.craftable : this.fitsDimensions;
        for (Recipe<?> recipe : this.recipes) {
            if (!set.contains(recipe)) continue;
            arrayList.add(recipe);
        }
        return arrayList;
    }

    public List<Recipe<?>> getDisplayRecipes(boolean bl) {
        ArrayList arrayList = Lists.newArrayList();
        for (Recipe<?> recipe : this.recipes) {
            if (!this.fitsDimensions.contains(recipe) || this.craftable.contains(recipe) != bl) continue;
            arrayList.add(recipe);
        }
        return arrayList;
    }

    public boolean hasSingleResultItem() {
        return this.singleResultItem;
    }
}

