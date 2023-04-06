/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.recipebook;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.GhostRecipe;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractFurnaceRecipeBookComponent
extends RecipeBookComponent {
    private Iterator<Item> iterator;
    private Set<Item> fuels;
    private Slot fuelSlot;
    private Item fuel;
    private float time;

    @Override
    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(152, 182, 28, 18, RECIPE_BOOK_LOCATION);
    }

    @Override
    public void slotClicked(@Nullable Slot slot) {
        super.slotClicked(slot);
        if (slot != null && slot.index < this.menu.getSize()) {
            this.fuelSlot = null;
        }
    }

    @Override
    public void setupGhostRecipe(Recipe<?> recipe, List<Slot> list) {
        ItemStack itemStack = recipe.getResultItem();
        this.ghostRecipe.setRecipe(recipe);
        this.ghostRecipe.addIngredient(Ingredient.of(itemStack), list.get((int)2).x, list.get((int)2).y);
        NonNullList<Ingredient> nonNullList = recipe.getIngredients();
        this.fuelSlot = list.get(1);
        if (this.fuels == null) {
            this.fuels = this.getFuelItems();
        }
        this.iterator = this.fuels.iterator();
        this.fuel = null;
        Iterator iterator = nonNullList.iterator();
        for (int i = 0; i < 2; ++i) {
            if (!iterator.hasNext()) {
                return;
            }
            Ingredient ingredient = (Ingredient)iterator.next();
            if (ingredient.isEmpty()) continue;
            Slot slot = list.get(i);
            this.ghostRecipe.addIngredient(ingredient, slot.x, slot.y);
        }
    }

    protected abstract Set<Item> getFuelItems();

    @Override
    public void renderGhostRecipe(PoseStack poseStack, int n, int n2, boolean bl, float f) {
        super.renderGhostRecipe(poseStack, n, n2, bl, f);
        if (this.fuelSlot == null) {
            return;
        }
        if (!Screen.hasControlDown()) {
            this.time += f;
        }
        int n3 = this.fuelSlot.x + n;
        int n4 = this.fuelSlot.y + n2;
        GuiComponent.fill(poseStack, n3, n4, n3 + 16, n4 + 16, 822018048);
        this.minecraft.getItemRenderer().renderAndDecorateItem(this.minecraft.player, this.getFuel().getDefaultInstance(), n3, n4);
        RenderSystem.depthFunc(516);
        GuiComponent.fill(poseStack, n3, n4, n3 + 16, n4 + 16, 822083583);
        RenderSystem.depthFunc(515);
    }

    private Item getFuel() {
        if (this.fuel == null || this.time > 30.0f) {
            this.time = 0.0f;
            if (this.iterator == null || !this.iterator.hasNext()) {
                if (this.fuels == null) {
                    this.fuels = this.getFuelItems();
                }
                this.iterator = this.fuels.iterator();
            }
            this.fuel = this.iterator.next();
        }
        return this.fuel;
    }
}

