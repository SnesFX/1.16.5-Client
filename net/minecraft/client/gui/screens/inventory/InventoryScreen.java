/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;

public class InventoryScreen
extends EffectRenderingInventoryScreen<InventoryMenu>
implements RecipeUpdateListener {
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private float xMouse;
    private float yMouse;
    private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
    private boolean recipeBookComponentInitialized;
    private boolean widthTooNarrow;
    private boolean buttonClicked;

    public InventoryScreen(Player player) {
        super(player.inventoryMenu, player.inventory, new TranslatableComponent("container.crafting"));
        this.passEvents = true;
        this.titleLabelX = 97;
    }

    @Override
    public void tick() {
        if (this.minecraft.gameMode.hasInfiniteItems()) {
            this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player));
            return;
        }
        this.recipeBookComponent.tick();
    }

    @Override
    protected void init() {
        if (this.minecraft.gameMode.hasInfiniteItems()) {
            this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player));
            return;
        }
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, (RecipeBookMenu)this.menu);
        this.recipeBookComponentInitialized = true;
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
        this.children.add(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
        this.addButton(new ImageButton(this.leftPos + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, button -> {
            this.recipeBookComponent.initVisuals(this.widthTooNarrow);
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
            ((ImageButton)button).setPosition(this.leftPos + 104, this.height / 2 - 22);
            this.buttonClicked = true;
        }));
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int n, int n2) {
        this.font.draw(poseStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        boolean bl = this.doRenderEffects = !this.recipeBookComponent.isVisible();
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(poseStack, f, n, n2);
            this.recipeBookComponent.render(poseStack, n, n2, f);
        } else {
            this.recipeBookComponent.render(poseStack, n, n2, f);
            super.render(poseStack, n, n2, f);
            this.recipeBookComponent.renderGhostRecipe(poseStack, this.leftPos, this.topPos, false, f);
        }
        this.renderTooltip(poseStack, n, n2);
        this.recipeBookComponent.renderTooltip(poseStack, this.leftPos, this.topPos, n, n2);
        this.xMouse = n;
        this.yMouse = n2;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int n, int n2) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(INVENTORY_LOCATION);
        int n3 = this.leftPos;
        int n4 = this.topPos;
        this.blit(poseStack, n3, n4, 0, 0, this.imageWidth, this.imageHeight);
        InventoryScreen.renderEntityInInventory(n3 + 51, n4 + 75, 30, (float)(n3 + 51) - this.xMouse, (float)(n4 + 75 - 50) - this.yMouse, this.minecraft.player);
    }

    public static void renderEntityInInventory(int n, int n2, int n3, float f, float f2, LivingEntity livingEntity) {
        float f3 = (float)Math.atan(f / 40.0f);
        float f4 = (float)Math.atan(f2 / 40.0f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(n, n2, 1050.0f);
        RenderSystem.scalef(1.0f, 1.0f, -1.0f);
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.0, 0.0, 1000.0);
        poseStack.scale(n3, n3, n3);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0f);
        Quaternion quaternion2 = Vector3f.XP.rotationDegrees(f4 * 20.0f);
        quaternion.mul(quaternion2);
        poseStack.mulPose(quaternion);
        float f5 = livingEntity.yBodyRot;
        float f6 = livingEntity.yRot;
        float f7 = livingEntity.xRot;
        float f8 = livingEntity.yHeadRotO;
        float f9 = livingEntity.yHeadRot;
        livingEntity.yBodyRot = 180.0f + f3 * 20.0f;
        livingEntity.yRot = 180.0f + f3 * 40.0f;
        livingEntity.xRot = -f4 * 20.0f;
        livingEntity.yHeadRot = livingEntity.yRot;
        livingEntity.yHeadRotO = livingEntity.yRot;
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion2.conj();
        entityRenderDispatcher.overrideCameraOrientation(quaternion2);
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(livingEntity, 0.0, 0.0, 0.0, 0.0f, 1.0f, poseStack, bufferSource, 15728880));
        bufferSource.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        livingEntity.yBodyRot = f5;
        livingEntity.yRot = f6;
        livingEntity.xRot = f7;
        livingEntity.yHeadRotO = f8;
        livingEntity.yHeadRot = f9;
        RenderSystem.popMatrix();
    }

    @Override
    protected boolean isHovering(int n, int n2, int n3, int n4, double d, double d2) {
        return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(n, n2, n3, n4, d, d2);
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (this.recipeBookComponent.mouseClicked(d, d2, n)) {
            this.setFocused(this.recipeBookComponent);
            return true;
        }
        if (this.widthTooNarrow && this.recipeBookComponent.isVisible()) {
            return false;
        }
        return super.mouseClicked(d, d2, n);
    }

    @Override
    public boolean mouseReleased(double d, double d2, int n) {
        if (this.buttonClicked) {
            this.buttonClicked = false;
            return true;
        }
        return super.mouseReleased(d, d2, n);
    }

    @Override
    protected boolean hasClickedOutside(double d, double d2, int n, int n2, int n3) {
        boolean bl = d < (double)n || d2 < (double)n2 || d >= (double)(n + this.imageWidth) || d2 >= (double)(n2 + this.imageHeight);
        return this.recipeBookComponent.hasClickedOutside(d, d2, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, n3) && bl;
    }

    @Override
    protected void slotClicked(Slot slot, int n, int n2, ClickType clickType) {
        super.slotClicked(slot, n, n2, clickType);
        this.recipeBookComponent.slotClicked(slot);
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }

    @Override
    public void removed() {
        if (this.recipeBookComponentInitialized) {
            this.recipeBookComponent.removed();
        }
        super.removed();
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }
}

