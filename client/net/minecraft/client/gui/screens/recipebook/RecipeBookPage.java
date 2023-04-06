/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.RecipeShownListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeBookPage {
    private final List<RecipeButton> buttons = Lists.newArrayListWithCapacity((int)20);
    private RecipeButton hoveredButton;
    private final OverlayRecipeComponent overlay = new OverlayRecipeComponent();
    private Minecraft minecraft;
    private final List<RecipeShownListener> showListeners = Lists.newArrayList();
    private List<RecipeCollection> recipeCollections;
    private StateSwitchingButton forwardButton;
    private StateSwitchingButton backButton;
    private int totalPages;
    private int currentPage;
    private RecipeBook recipeBook;
    private Recipe<?> lastClickedRecipe;
    private RecipeCollection lastClickedRecipeCollection;

    public RecipeBookPage() {
        for (int i = 0; i < 20; ++i) {
            this.buttons.add(new RecipeButton());
        }
    }

    public void init(Minecraft minecraft, int n, int n2) {
        this.minecraft = minecraft;
        this.recipeBook = minecraft.player.getRecipeBook();
        for (int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).setPosition(n + 11 + 25 * (i % 5), n2 + 31 + 25 * (i / 5));
        }
        this.forwardButton = new StateSwitchingButton(n + 93, n2 + 137, 12, 17, false);
        this.forwardButton.initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
        this.backButton = new StateSwitchingButton(n + 38, n2 + 137, 12, 17, true);
        this.backButton.initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
    }

    public void addListener(RecipeBookComponent recipeBookComponent) {
        this.showListeners.remove(recipeBookComponent);
        this.showListeners.add(recipeBookComponent);
    }

    public void updateCollections(List<RecipeCollection> list, boolean bl) {
        this.recipeCollections = list;
        this.totalPages = (int)Math.ceil((double)list.size() / 20.0);
        if (this.totalPages <= this.currentPage || bl) {
            this.currentPage = 0;
        }
        this.updateButtonsForPage();
    }

    private void updateButtonsForPage() {
        int n = 20 * this.currentPage;
        for (int i = 0; i < this.buttons.size(); ++i) {
            RecipeButton recipeButton = this.buttons.get(i);
            if (n + i < this.recipeCollections.size()) {
                RecipeCollection recipeCollection = this.recipeCollections.get(n + i);
                recipeButton.init(recipeCollection, this);
                recipeButton.visible = true;
                continue;
            }
            recipeButton.visible = false;
        }
        this.updateArrowButtons();
    }

    private void updateArrowButtons() {
        this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
        this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
    }

    public void render(PoseStack poseStack, int n, int n2, int n3, int n4, float f) {
        if (this.totalPages > 1) {
            String string = this.currentPage + 1 + "/" + this.totalPages;
            int n5 = this.minecraft.font.width(string);
            this.minecraft.font.draw(poseStack, string, (float)(n - n5 / 2 + 73), (float)(n2 + 141), -1);
        }
        this.hoveredButton = null;
        for (RecipeButton recipeButton : this.buttons) {
            recipeButton.render(poseStack, n3, n4, f);
            if (!recipeButton.visible || !recipeButton.isHovered()) continue;
            this.hoveredButton = recipeButton;
        }
        this.backButton.render(poseStack, n3, n4, f);
        this.forwardButton.render(poseStack, n3, n4, f);
        this.overlay.render(poseStack, n3, n4, f);
    }

    public void renderTooltip(PoseStack poseStack, int n, int n2) {
        if (this.minecraft.screen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
            this.minecraft.screen.renderComponentTooltip(poseStack, this.hoveredButton.getTooltipText(this.minecraft.screen), n, n2);
        }
    }

    @Nullable
    public Recipe<?> getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Nullable
    public RecipeCollection getLastClickedRecipeCollection() {
        return this.lastClickedRecipeCollection;
    }

    public void setInvisible() {
        this.overlay.setVisible(false);
    }

    public boolean mouseClicked(double d, double d2, int n, int n2, int n3, int n4, int n5) {
        this.lastClickedRecipe = null;
        this.lastClickedRecipeCollection = null;
        if (this.overlay.isVisible()) {
            if (this.overlay.mouseClicked(d, d2, n)) {
                this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
                this.lastClickedRecipeCollection = this.overlay.getRecipeCollection();
            } else {
                this.overlay.setVisible(false);
            }
            return true;
        }
        if (this.forwardButton.mouseClicked(d, d2, n)) {
            ++this.currentPage;
            this.updateButtonsForPage();
            return true;
        }
        if (this.backButton.mouseClicked(d, d2, n)) {
            --this.currentPage;
            this.updateButtonsForPage();
            return true;
        }
        for (RecipeButton recipeButton : this.buttons) {
            if (!recipeButton.mouseClicked(d, d2, n)) continue;
            if (n == 0) {
                this.lastClickedRecipe = recipeButton.getRecipe();
                this.lastClickedRecipeCollection = recipeButton.getCollection();
            } else if (n == 1 && !this.overlay.isVisible() && !recipeButton.isOnlyOption()) {
                this.overlay.init(this.minecraft, recipeButton.getCollection(), recipeButton.x, recipeButton.y, n2 + n4 / 2, n3 + 13 + n5 / 2, recipeButton.getWidth());
            }
            return true;
        }
        return false;
    }

    public void recipesShown(List<Recipe<?>> list) {
        for (RecipeShownListener recipeShownListener : this.showListeners) {
            recipeShownListener.recipesShown(list);
        }
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public RecipeBook getRecipeBook() {
        return this.recipeBook;
    }
}

