/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeButton
extends AbstractWidget {
    private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    private static final Component MORE_RECIPES_TOOLTIP = new TranslatableComponent("gui.recipebook.moreRecipes");
    private RecipeBookMenu<?> menu;
    private RecipeBook book;
    private RecipeCollection collection;
    private float time;
    private float animationTime;
    private int currentIndex;

    public RecipeButton() {
        super(0, 0, 25, 25, TextComponent.EMPTY);
    }

    public void init(RecipeCollection recipeCollection, RecipeBookPage recipeBookPage) {
        this.collection = recipeCollection;
        this.menu = (RecipeBookMenu)recipeBookPage.getMinecraft().player.containerMenu;
        this.book = recipeBookPage.getRecipeBook();
        List<Recipe<?>> list = recipeCollection.getRecipes(this.book.isFiltering(this.menu));
        for (Recipe<?> recipe : list) {
            if (!this.book.willHighlight(recipe)) continue;
            recipeBookPage.recipesShown(list);
            this.animationTime = 15.0f;
            break;
        }
    }

    public RecipeCollection getCollection() {
        return this.collection;
    }

    public void setPosition(int n, int n2) {
        this.x = n;
        this.y = n2;
    }

    @Override
    public void renderButton(PoseStack poseStack, int n, int n2, float f) {
        boolean bl;
        if (!Screen.hasControlDown()) {
            this.time += f;
        }
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(RECIPE_BOOK_LOCATION);
        int n3 = 29;
        if (!this.collection.hasCraftable()) {
            n3 += 25;
        }
        int n4 = 206;
        if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
            n4 += 25;
        }
        boolean bl2 = bl = this.animationTime > 0.0f;
        if (bl) {
            float f2 = 1.0f + 0.1f * (float)Math.sin(this.animationTime / 15.0f * 3.1415927f);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.x + 8, this.y + 12, 0.0f);
            RenderSystem.scalef(f2, f2, 1.0f);
            RenderSystem.translatef(-(this.x + 8), -(this.y + 12), 0.0f);
            this.animationTime -= f;
        }
        this.blit(poseStack, this.x, this.y, n3, n4, this.width, this.height);
        List<Recipe<?>> list = this.getOrderedRecipes();
        this.currentIndex = Mth.floor(this.time / 30.0f) % list.size();
        ItemStack itemStack = list.get(this.currentIndex).getResultItem();
        int n5 = 4;
        if (this.collection.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
            minecraft.getItemRenderer().renderAndDecorateItem(itemStack, this.x + n5 + 1, this.y + n5 + 1);
            --n5;
        }
        minecraft.getItemRenderer().renderAndDecorateFakeItem(itemStack, this.x + n5, this.y + n5);
        if (bl) {
            RenderSystem.popMatrix();
        }
    }

    private List<Recipe<?>> getOrderedRecipes() {
        List<Recipe<?>> list = this.collection.getDisplayRecipes(true);
        if (!this.book.isFiltering(this.menu)) {
            list.addAll(this.collection.getDisplayRecipes(false));
        }
        return list;
    }

    public boolean isOnlyOption() {
        return this.getOrderedRecipes().size() == 1;
    }

    public Recipe<?> getRecipe() {
        List<Recipe<?>> list = this.getOrderedRecipes();
        return list.get(this.currentIndex);
    }

    public List<Component> getTooltipText(Screen screen) {
        ItemStack itemStack = this.getOrderedRecipes().get(this.currentIndex).getResultItem();
        ArrayList arrayList = Lists.newArrayList(screen.getTooltipFromItem(itemStack));
        if (this.collection.getRecipes(this.book.isFiltering(this.menu)).size() > 1) {
            arrayList.add(MORE_RECIPES_TOOLTIP);
        }
        return arrayList;
    }

    @Override
    public int getWidth() {
        return 25;
    }

    @Override
    protected boolean isValidClickButton(int n) {
        return n == 0 || n == 1;
    }
}

