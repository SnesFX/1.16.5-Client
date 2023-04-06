/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeBookTabButton
extends StateSwitchingButton {
    private final RecipeBookCategories category;
    private float animationTime;

    public RecipeBookTabButton(RecipeBookCategories recipeBookCategories) {
        super(0, 0, 35, 27, false);
        this.category = recipeBookCategories;
        this.initTextureValues(153, 2, 35, 0, RecipeBookComponent.RECIPE_BOOK_LOCATION);
    }

    public void startAnimation(Minecraft minecraft) {
        ClientRecipeBook clientRecipeBook = minecraft.player.getRecipeBook();
        List<RecipeCollection> list = clientRecipeBook.getCollection(this.category);
        if (!(minecraft.player.containerMenu instanceof RecipeBookMenu)) {
            return;
        }
        for (RecipeCollection recipeCollection : list) {
            for (Recipe<?> recipe : recipeCollection.getRecipes(clientRecipeBook.isFiltering((RecipeBookMenu)minecraft.player.containerMenu))) {
                if (!clientRecipeBook.willHighlight(recipe)) continue;
                this.animationTime = 15.0f;
                return;
            }
        }
    }

    @Override
    public void renderButton(PoseStack poseStack, int n, int n2, float f) {
        if (this.animationTime > 0.0f) {
            float f2 = 1.0f + 0.1f * (float)Math.sin(this.animationTime / 15.0f * 3.1415927f);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.x + 8, this.y + 12, 0.0f);
            RenderSystem.scalef(1.0f, f2, 1.0f);
            RenderSystem.translatef(-(this.x + 8), -(this.y + 12), 0.0f);
        }
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(this.resourceLocation);
        RenderSystem.disableDepthTest();
        int n3 = this.xTexStart;
        int n4 = this.yTexStart;
        if (this.isStateTriggered) {
            n3 += this.xDiffTex;
        }
        if (this.isHovered()) {
            n4 += this.yDiffTex;
        }
        int n5 = this.x;
        if (this.isStateTriggered) {
            n5 -= 2;
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.blit(poseStack, n5, this.y, n3, n4, this.width, this.height);
        RenderSystem.enableDepthTest();
        this.renderIcon(minecraft.getItemRenderer());
        if (this.animationTime > 0.0f) {
            RenderSystem.popMatrix();
            this.animationTime -= f;
        }
    }

    private void renderIcon(ItemRenderer itemRenderer) {
        int n;
        List<ItemStack> list = this.category.getIconItems();
        int n2 = n = this.isStateTriggered ? -2 : 0;
        if (list.size() == 1) {
            itemRenderer.renderAndDecorateFakeItem(list.get(0), this.x + 9 + n, this.y + 5);
        } else if (list.size() == 2) {
            itemRenderer.renderAndDecorateFakeItem(list.get(0), this.x + 3 + n, this.y + 5);
            itemRenderer.renderAndDecorateFakeItem(list.get(1), this.x + 14 + n, this.y + 5);
        }
    }

    public RecipeBookCategories getCategory() {
        return this.category;
    }

    public boolean updateVisibility(ClientRecipeBook clientRecipeBook) {
        List<RecipeCollection> list = clientRecipeBook.getCollection(this.category);
        this.visible = false;
        if (list != null) {
            for (RecipeCollection recipeCollection : list) {
                if (!recipeCollection.hasKnownRecipes() || !recipeCollection.hasFitting()) continue;
                this.visible = true;
                break;
            }
        }
        return this.visible;
    }
}

