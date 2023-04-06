/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntAVLTreeSet
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.player;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public class StackedContents {
    public final Int2IntMap contents = new Int2IntOpenHashMap();

    public void accountSimpleStack(ItemStack itemStack) {
        if (!(itemStack.isDamaged() || itemStack.isEnchanted() || itemStack.hasCustomHoverName())) {
            this.accountStack(itemStack);
        }
    }

    public void accountStack(ItemStack itemStack) {
        this.accountStack(itemStack, 64);
    }

    public void accountStack(ItemStack itemStack, int n) {
        if (!itemStack.isEmpty()) {
            int n2 = StackedContents.getStackingIndex(itemStack);
            int n3 = Math.min(n, itemStack.getCount());
            this.put(n2, n3);
        }
    }

    public static int getStackingIndex(ItemStack itemStack) {
        return Registry.ITEM.getId(itemStack.getItem());
    }

    private boolean has(int n) {
        return this.contents.get(n) > 0;
    }

    private int take(int n, int n2) {
        int n3 = this.contents.get(n);
        if (n3 >= n2) {
            this.contents.put(n, n3 - n2);
            return n;
        }
        return 0;
    }

    private void put(int n, int n2) {
        this.contents.put(n, this.contents.get(n) + n2);
    }

    public boolean canCraft(Recipe<?> recipe, @Nullable IntList intList) {
        return this.canCraft(recipe, intList, 1);
    }

    public boolean canCraft(Recipe<?> recipe, @Nullable IntList intList, int n) {
        return new RecipePicker(recipe).tryPick(n, intList);
    }

    public int getBiggestCraftableStack(Recipe<?> recipe, @Nullable IntList intList) {
        return this.getBiggestCraftableStack(recipe, Integer.MAX_VALUE, intList);
    }

    public int getBiggestCraftableStack(Recipe<?> recipe, int n, @Nullable IntList intList) {
        return new RecipePicker(recipe).tryPickAll(n, intList);
    }

    public static ItemStack fromStackingIndex(int n) {
        if (n == 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(Item.byId(n));
    }

    public void clear() {
        this.contents.clear();
    }

    class RecipePicker {
        private final Recipe<?> recipe;
        private final List<Ingredient> ingredients = Lists.newArrayList();
        private final int ingredientCount;
        private final int[] items;
        private final int itemCount;
        private final BitSet data;
        private final IntList path = new IntArrayList();

        public RecipePicker(Recipe<?> recipe) {
            this.recipe = recipe;
            this.ingredients.addAll(recipe.getIngredients());
            this.ingredients.removeIf(Ingredient::isEmpty);
            this.ingredientCount = this.ingredients.size();
            this.items = this.getUniqueAvailableIngredientItems();
            this.itemCount = this.items.length;
            this.data = new BitSet(this.ingredientCount + this.itemCount + this.ingredientCount + this.ingredientCount * this.itemCount);
            for (int i = 0; i < this.ingredients.size(); ++i) {
                IntList intList = this.ingredients.get(i).getStackingIds();
                for (int j = 0; j < this.itemCount; ++j) {
                    if (!intList.contains(this.items[j])) continue;
                    this.data.set(this.getIndex(true, j, i));
                }
            }
        }

        public boolean tryPick(int n, @Nullable IntList intList) {
            int n2;
            int n3;
            if (n <= 0) {
                return true;
            }
            int n4 = 0;
            while (this.dfs(n)) {
                StackedContents.this.take(this.items[this.path.getInt(0)], n);
                n3 = this.path.size() - 1;
                this.setSatisfied(this.path.getInt(n3));
                for (n2 = 0; n2 < n3; ++n2) {
                    this.toggleResidual((n2 & 1) == 0, this.path.get(n2), this.path.get(n2 + 1));
                }
                this.path.clear();
                this.data.clear(0, this.ingredientCount + this.itemCount);
                ++n4;
            }
            n3 = n4 == this.ingredientCount ? 1 : 0;
            int n5 = n2 = n3 != 0 && intList != null ? 1 : 0;
            if (n2 != 0) {
                intList.clear();
            }
            this.data.clear(0, this.ingredientCount + this.itemCount + this.ingredientCount);
            int n6 = 0;
            NonNullList<Ingredient> nonNullList = this.recipe.getIngredients();
            for (int i = 0; i < nonNullList.size(); ++i) {
                if (n2 != 0 && ((Ingredient)nonNullList.get(i)).isEmpty()) {
                    intList.add(0);
                    continue;
                }
                for (int j = 0; j < this.itemCount; ++j) {
                    if (!this.hasResidual(false, n6, j)) continue;
                    this.toggleResidual(true, j, n6);
                    StackedContents.this.put(this.items[j], n);
                    if (n2 == 0) continue;
                    intList.add(this.items[j]);
                }
                ++n6;
            }
            return n3 != 0;
        }

        private int[] getUniqueAvailableIngredientItems() {
            IntAVLTreeSet intAVLTreeSet = new IntAVLTreeSet();
            for (Ingredient ingredient : this.ingredients) {
                intAVLTreeSet.addAll((IntCollection)ingredient.getStackingIds());
            }
            IntIterator intIterator = intAVLTreeSet.iterator();
            while (intIterator.hasNext()) {
                if (StackedContents.this.has(intIterator.nextInt())) continue;
                intIterator.remove();
            }
            return intAVLTreeSet.toIntArray();
        }

        private boolean dfs(int n) {
            int n2 = this.itemCount;
            for (int i = 0; i < n2; ++i) {
                if (StackedContents.this.contents.get(this.items[i]) < n) continue;
                this.visit(false, i);
                while (!this.path.isEmpty()) {
                    int n3;
                    int n4 = this.path.size();
                    boolean bl = (n4 & 1) == 1;
                    int n5 = this.path.getInt(n4 - 1);
                    if (!bl && !this.isSatisfied(n5)) break;
                    int n6 = bl ? this.ingredientCount : n2;
                    for (n3 = 0; n3 < n6; ++n3) {
                        if (this.hasVisited(bl, n3) || !this.hasConnection(bl, n5, n3) || !this.hasResidual(bl, n5, n3)) continue;
                        this.visit(bl, n3);
                        break;
                    }
                    if ((n3 = this.path.size()) != n4) continue;
                    this.path.removeInt(n3 - 1);
                }
                if (this.path.isEmpty()) continue;
                return true;
            }
            return false;
        }

        private boolean isSatisfied(int n) {
            return this.data.get(this.getSatisfiedIndex(n));
        }

        private void setSatisfied(int n) {
            this.data.set(this.getSatisfiedIndex(n));
        }

        private int getSatisfiedIndex(int n) {
            return this.ingredientCount + this.itemCount + n;
        }

        private boolean hasConnection(boolean bl, int n, int n2) {
            return this.data.get(this.getIndex(bl, n, n2));
        }

        private boolean hasResidual(boolean bl, int n, int n2) {
            return bl != this.data.get(1 + this.getIndex(bl, n, n2));
        }

        private void toggleResidual(boolean bl, int n, int n2) {
            this.data.flip(1 + this.getIndex(bl, n, n2));
        }

        private int getIndex(boolean bl, int n, int n2) {
            int n3 = bl ? n * this.ingredientCount + n2 : n2 * this.ingredientCount + n;
            return this.ingredientCount + this.itemCount + this.ingredientCount + 2 * n3;
        }

        private void visit(boolean bl, int n) {
            this.data.set(this.getVisitedIndex(bl, n));
            this.path.add(n);
        }

        private boolean hasVisited(boolean bl, int n) {
            return this.data.get(this.getVisitedIndex(bl, n));
        }

        private int getVisitedIndex(boolean bl, int n) {
            return (bl ? 0 : this.ingredientCount) + n;
        }

        public int tryPickAll(int n, @Nullable IntList intList) {
            int n2;
            int n3 = 0;
            int n4 = Math.min(n, this.getMinIngredientCount()) + 1;
            do {
                if (this.tryPick(n2 = (n3 + n4) / 2, null)) {
                    if (n4 - n3 <= 1) break;
                    n3 = n2;
                    continue;
                }
                n4 = n2;
            } while (true);
            if (n2 > 0) {
                this.tryPick(n2, intList);
            }
            return n2;
        }

        private int getMinIngredientCount() {
            int n = Integer.MAX_VALUE;
            for (Ingredient ingredient : this.ingredients) {
                int n2 = 0;
                IntListIterator intListIterator = ingredient.getStackingIds().iterator();
                while (intListIterator.hasNext()) {
                    int n3 = (Integer)intListIterator.next();
                    n2 = Math.max(n2, StackedContents.this.contents.get(n3));
                }
                if (n <= 0) continue;
                n = Math.min(n, n2);
            }
            return n;
        }
    }

}

