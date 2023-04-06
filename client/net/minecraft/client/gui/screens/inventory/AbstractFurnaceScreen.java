/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;

public abstract class AbstractFurnaceScreen<T extends AbstractFurnaceMenu>
extends AbstractContainerScreen<T>
implements RecipeUpdateListener {
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    public final AbstractFurnaceRecipeBookComponent recipeBookComponent;
    private boolean widthTooNarrow;
    private final ResourceLocation texture;

    public AbstractFurnaceScreen(T t, AbstractFurnaceRecipeBookComponent abstractFurnaceRecipeBookComponent, Inventory inventory, Component component, ResourceLocation resourceLocation) {
        super(t, inventory, component);
        this.recipeBookComponent = abstractFurnaceRecipeBookComponent;
        this.texture = resourceLocation;
    }

    @Override
    public void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, (RecipeBookMenu)this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
        this.addButton(new ImageButton(this.leftPos + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, button -> {
            this.recipeBookComponent.initVisuals(this.widthTooNarrow);
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
            ((ImageButton)button).setPosition(this.leftPos + 20, this.height / 2 - 49);
        }));
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void tick() {
        super.tick();
        this.recipeBookComponent.tick();
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(poseStack, f, n, n2);
            this.recipeBookComponent.render(poseStack, n, n2, f);
        } else {
            this.recipeBookComponent.render(poseStack, n, n2, f);
            super.render(poseStack, n, n2, f);
            this.recipeBookComponent.renderGhostRecipe(poseStack, this.leftPos, this.topPos, true, f);
        }
        this.renderTooltip(poseStack, n, n2);
        this.recipeBookComponent.renderTooltip(poseStack, this.leftPos, this.topPos, n, n2);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int n, int n2) {
        int n3;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(this.texture);
        int n4 = this.leftPos;
        int n5 = this.topPos;
        this.blit(poseStack, n4, n5, 0, 0, this.imageWidth, this.imageHeight);
        if (((AbstractFurnaceMenu)this.menu).isLit()) {
            n3 = ((AbstractFurnaceMenu)this.menu).getLitProgress();
            this.blit(poseStack, n4 + 56, n5 + 36 + 12 - n3, 176, 12 - n3, 14, n3 + 1);
        }
        n3 = ((AbstractFurnaceMenu)this.menu).getBurnProgress();
        this.blit(poseStack, n4 + 79, n5 + 34, 176, 14, n3 + 1, 16);
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (this.recipeBookComponent.mouseClicked(d, d2, n)) {
            return true;
        }
        if (this.widthTooNarrow && this.recipeBookComponent.isVisible()) {
            return true;
        }
        return super.mouseClicked(d, d2, n);
    }

    @Override
    protected void slotClicked(Slot slot, int n, int n2, ClickType clickType) {
        super.slotClicked(slot, n, n2, clickType);
        this.recipeBookComponent.slotClicked(slot);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (this.recipeBookComponent.keyPressed(n, n2, n3)) {
            return false;
        }
        return super.keyPressed(n, n2, n3);
    }

    @Override
    protected boolean hasClickedOutside(double d, double d2, int n, int n2, int n3) {
        boolean bl = d < (double)n || d2 < (double)n2 || d >= (double)(n + this.imageWidth) || d2 >= (double)(n2 + this.imageHeight);
        return this.recipeBookComponent.hasClickedOutside(d, d2, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, n3) && bl;
    }

    @Override
    public boolean charTyped(char c, int n) {
        if (this.recipeBookComponent.charTyped(c, n)) {
            return true;
        }
        return super.charTyped(c, n);
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }

    @Override
    public void removed() {
        this.recipeBookComponent.removed();
        super.removed();
    }
}

