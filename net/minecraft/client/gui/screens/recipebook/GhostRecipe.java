/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class GhostRecipe {
    private Recipe<?> recipe;
    private final List<GhostIngredient> ingredients = Lists.newArrayList();
    private float time;

    public void clear() {
        this.recipe = null;
        this.ingredients.clear();
        this.time = 0.0f;
    }

    public void addIngredient(Ingredient ingredient, int n, int n2) {
        this.ingredients.add(new GhostIngredient(ingredient, n, n2));
    }

    public GhostIngredient get(int n) {
        return this.ingredients.get(n);
    }

    public int size() {
        return this.ingredients.size();
    }

    @Nullable
    public Recipe<?> getRecipe() {
        return this.recipe;
    }

    public void setRecipe(Recipe<?> recipe) {
        this.recipe = recipe;
    }

    public void render(PoseStack poseStack, Minecraft minecraft, int n, int n2, boolean bl, float f) {
        if (!Screen.hasControlDown()) {
            this.time += f;
        }
        for (int i = 0; i < this.ingredients.size(); ++i) {
            GhostIngredient ghostIngredient = this.ingredients.get(i);
            int n3 = ghostIngredient.getX() + n;
            int n4 = ghostIngredient.getY() + n2;
            if (i == 0 && bl) {
                GuiComponent.fill(poseStack, n3 - 4, n4 - 4, n3 + 20, n4 + 20, 822018048);
            } else {
                GuiComponent.fill(poseStack, n3, n4, n3 + 16, n4 + 16, 822018048);
            }
            ItemStack itemStack = ghostIngredient.getItem();
            ItemRenderer itemRenderer = minecraft.getItemRenderer();
            itemRenderer.renderAndDecorateFakeItem(itemStack, n3, n4);
            RenderSystem.depthFunc(516);
            GuiComponent.fill(poseStack, n3, n4, n3 + 16, n4 + 16, 822083583);
            RenderSystem.depthFunc(515);
            if (i != 0) continue;
            itemRenderer.renderGuiItemDecorations(minecraft.font, itemStack, n3, n4);
        }
    }

    public class GhostIngredient {
        private final Ingredient ingredient;
        private final int x;
        private final int y;

        public GhostIngredient(Ingredient ingredient, int n, int n2) {
            this.ingredient = ingredient;
            this.x = n;
            this.y = n2;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public ItemStack getItem() {
            ItemStack[] arritemStack = this.ingredient.getItems();
            return arritemStack[Mth.floor(GhostRecipe.this.time / 30.0f) % arritemStack.length];
        }
    }

}

