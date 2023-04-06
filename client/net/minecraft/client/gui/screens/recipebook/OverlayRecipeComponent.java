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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class OverlayRecipeComponent
extends GuiComponent
implements Widget,
GuiEventListener {
    private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    private final List<OverlayRecipeButton> recipeButtons = Lists.newArrayList();
    private boolean isVisible;
    private int x;
    private int y;
    private Minecraft minecraft;
    private RecipeCollection collection;
    private Recipe<?> lastRecipeClicked;
    private float time;
    private boolean isFurnaceMenu;

    public void init(Minecraft minecraft, RecipeCollection recipeCollection, int n, int n2, int n3, int n4, float f) {
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        this.minecraft = minecraft;
        this.collection = recipeCollection;
        if (minecraft.player.containerMenu instanceof AbstractFurnaceMenu) {
            this.isFurnaceMenu = true;
        }
        boolean bl = minecraft.player.getRecipeBook().isFiltering((RecipeBookMenu)minecraft.player.containerMenu);
        List<Recipe<?>> list = recipeCollection.getDisplayRecipes(true);
        List list2 = bl ? Collections.emptyList() : recipeCollection.getDisplayRecipes(false);
        int n5 = list.size();
        int n6 = n5 + list2.size();
        int n7 = n6 <= 16 ? 4 : 5;
        int n8 = (int)Math.ceil((float)n6 / (float)n7);
        this.x = n;
        this.y = n2;
        int n9 = 25;
        float f7 = this.x + Math.min(n6, n7) * 25;
        if (f7 > (f6 = (float)(n3 + 50))) {
            this.x = (int)((float)this.x - f * (float)((int)((f7 - f6) / f)));
        }
        if ((f4 = (float)(this.y + n8 * 25)) > (f3 = (float)(n4 + 50))) {
            this.y = (int)((float)this.y - f * (float)Mth.ceil((f4 - f3) / f));
        }
        if ((f5 = (float)this.y) < (f2 = (float)(n4 - 100))) {
            this.y = (int)((float)this.y - f * (float)Mth.ceil((f5 - f2) / f));
        }
        this.isVisible = true;
        this.recipeButtons.clear();
        for (int i = 0; i < n6; ++i) {
            boolean bl2 = i < n5;
            Recipe recipe = bl2 ? list.get(i) : (Recipe)list2.get(i - n5);
            int n10 = this.x + 4 + 25 * (i % n7);
            int n11 = this.y + 5 + 25 * (i / n7);
            if (this.isFurnaceMenu) {
                this.recipeButtons.add(new OverlaySmeltingRecipeButton(n10, n11, recipe, bl2));
                continue;
            }
            this.recipeButtons.add(new OverlayRecipeButton(n10, n11, recipe, bl2));
        }
        this.lastRecipeClicked = null;
    }

    @Override
    public boolean changeFocus(boolean bl) {
        return false;
    }

    public RecipeCollection getRecipeCollection() {
        return this.collection;
    }

    public Recipe<?> getLastRecipeClicked() {
        return this.lastRecipeClicked;
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (n != 0) {
            return false;
        }
        for (OverlayRecipeButton overlayRecipeButton : this.recipeButtons) {
            if (!overlayRecipeButton.mouseClicked(d, d2, n)) continue;
            this.lastRecipeClicked = overlayRecipeButton.recipe;
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double d, double d2) {
        return false;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        if (!this.isVisible) {
            return;
        }
        this.time += f;
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(RECIPE_BOOK_LOCATION);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.0f, 170.0f);
        int n3 = this.recipeButtons.size() <= 16 ? 4 : 5;
        int n4 = Math.min(this.recipeButtons.size(), n3);
        int n5 = Mth.ceil((float)this.recipeButtons.size() / (float)n3);
        int n6 = 24;
        int n7 = 4;
        int n8 = 82;
        int n9 = 208;
        this.nineInchSprite(poseStack, n4, n5, 24, 4, 82, 208);
        RenderSystem.disableBlend();
        for (OverlayRecipeButton overlayRecipeButton : this.recipeButtons) {
            overlayRecipeButton.render(poseStack, n, n2, f);
        }
        RenderSystem.popMatrix();
    }

    private void nineInchSprite(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6) {
        this.blit(poseStack, this.x, this.y, n5, n6, n4, n4);
        this.blit(poseStack, this.x + n4 * 2 + n * n3, this.y, n5 + n3 + n4, n6, n4, n4);
        this.blit(poseStack, this.x, this.y + n4 * 2 + n2 * n3, n5, n6 + n3 + n4, n4, n4);
        this.blit(poseStack, this.x + n4 * 2 + n * n3, this.y + n4 * 2 + n2 * n3, n5 + n3 + n4, n6 + n3 + n4, n4, n4);
        for (int i = 0; i < n; ++i) {
            this.blit(poseStack, this.x + n4 + i * n3, this.y, n5 + n4, n6, n3, n4);
            this.blit(poseStack, this.x + n4 + (i + 1) * n3, this.y, n5 + n4, n6, n4, n4);
            for (int j = 0; j < n2; ++j) {
                if (i == 0) {
                    this.blit(poseStack, this.x, this.y + n4 + j * n3, n5, n6 + n4, n4, n3);
                    this.blit(poseStack, this.x, this.y + n4 + (j + 1) * n3, n5, n6 + n4, n4, n4);
                }
                this.blit(poseStack, this.x + n4 + i * n3, this.y + n4 + j * n3, n5 + n4, n6 + n4, n3, n3);
                this.blit(poseStack, this.x + n4 + (i + 1) * n3, this.y + n4 + j * n3, n5 + n4, n6 + n4, n4, n3);
                this.blit(poseStack, this.x + n4 + i * n3, this.y + n4 + (j + 1) * n3, n5 + n4, n6 + n4, n3, n4);
                this.blit(poseStack, this.x + n4 + (i + 1) * n3 - 1, this.y + n4 + (j + 1) * n3 - 1, n5 + n4, n6 + n4, n4 + 1, n4 + 1);
                if (i != n - 1) continue;
                this.blit(poseStack, this.x + n4 * 2 + n * n3, this.y + n4 + j * n3, n5 + n3 + n4, n6 + n4, n4, n3);
                this.blit(poseStack, this.x + n4 * 2 + n * n3, this.y + n4 + (j + 1) * n3, n5 + n3 + n4, n6 + n4, n4, n4);
            }
            this.blit(poseStack, this.x + n4 + i * n3, this.y + n4 * 2 + n2 * n3, n5 + n4, n6 + n3 + n4, n3, n4);
            this.blit(poseStack, this.x + n4 + (i + 1) * n3, this.y + n4 * 2 + n2 * n3, n5 + n4, n6 + n3 + n4, n4, n4);
        }
    }

    public void setVisible(boolean bl) {
        this.isVisible = bl;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    class OverlayRecipeButton
    extends AbstractWidget
    implements PlaceRecipe<Ingredient> {
        private final Recipe<?> recipe;
        private final boolean isCraftable;
        protected final List<Pos> ingredientPos;

        public OverlayRecipeButton(int n, int n2, Recipe<?> recipe, boolean bl) {
            super(n, n2, 200, 20, TextComponent.EMPTY);
            this.ingredientPos = Lists.newArrayList();
            this.width = 24;
            this.height = 24;
            this.recipe = recipe;
            this.isCraftable = bl;
            this.calculateIngredientsPositions(recipe);
        }

        protected void calculateIngredientsPositions(Recipe<?> recipe) {
            this.placeRecipe(3, 3, -1, recipe, recipe.getIngredients().iterator(), 0);
        }

        @Override
        public void addItemToSlot(Iterator<Ingredient> iterator, int n, int n2, int n3, int n4) {
            ItemStack[] arritemStack = iterator.next().getItems();
            if (arritemStack.length != 0) {
                this.ingredientPos.add(new Pos(3 + n4 * 7, 3 + n3 * 7, arritemStack));
            }
        }

        @Override
        public void renderButton(PoseStack poseStack, int n, int n2, float f) {
            int n3;
            RenderSystem.enableAlphaTest();
            OverlayRecipeComponent.this.minecraft.getTextureManager().bind(RECIPE_BOOK_LOCATION);
            int n4 = 152;
            if (!this.isCraftable) {
                n4 += 26;
            }
            int n5 = n3 = OverlayRecipeComponent.this.isFurnaceMenu ? 130 : 78;
            if (this.isHovered()) {
                n3 += 26;
            }
            this.blit(poseStack, this.x, this.y, n4, n3, this.width, this.height);
            for (Pos pos : this.ingredientPos) {
                RenderSystem.pushMatrix();
                float f2 = 0.42f;
                int n6 = (int)((float)(this.x + pos.x) / 0.42f - 3.0f);
                int n7 = (int)((float)(this.y + pos.y) / 0.42f - 3.0f);
                RenderSystem.scalef(0.42f, 0.42f, 1.0f);
                OverlayRecipeComponent.this.minecraft.getItemRenderer().renderAndDecorateItem(pos.ingredients[Mth.floor(OverlayRecipeComponent.this.time / 30.0f) % pos.ingredients.length], n6, n7);
                RenderSystem.popMatrix();
            }
            RenderSystem.disableAlphaTest();
        }

        public class Pos {
            public final ItemStack[] ingredients;
            public final int x;
            public final int y;

            public Pos(int n, int n2, ItemStack[] arritemStack) {
                this.x = n;
                this.y = n2;
                this.ingredients = arritemStack;
            }
        }

    }

    class OverlaySmeltingRecipeButton
    extends OverlayRecipeButton {
        public OverlaySmeltingRecipeButton(int n, int n2, Recipe<?> recipe, boolean bl) {
            super(n, n2, recipe, bl);
        }

        @Override
        protected void calculateIngredientsPositions(Recipe<?> recipe) {
            ItemStack[] arritemStack = recipe.getIngredients().get(0).getItems();
            this.ingredientPos.add(new OverlayRecipeButton.Pos(10, 10, arritemStack));
        }
    }

}

