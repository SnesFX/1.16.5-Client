/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class StonecutterScreen
extends AbstractContainerScreen<StonecutterMenu> {
    private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/stonecutter.png");
    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private boolean displayRecipes;

    public StonecutterScreen(StonecutterMenu stonecutterMenu, Inventory inventory, Component component) {
        super(stonecutterMenu, inventory, component);
        stonecutterMenu.registerUpdateListener(this::containerChanged);
        --this.titleLabelY;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        super.render(poseStack, n, n2, f);
        this.renderTooltip(poseStack, n, n2);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int n, int n2) {
        this.renderBackground(poseStack);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(BG_LOCATION);
        int n3 = this.leftPos;
        int n4 = this.topPos;
        this.blit(poseStack, n3, n4, 0, 0, this.imageWidth, this.imageHeight);
        int n5 = (int)(41.0f * this.scrollOffs);
        this.blit(poseStack, n3 + 119, n4 + 15 + n5, 176 + (this.isScrollBarActive() ? 0 : 12), 0, 12, 15);
        int n6 = this.leftPos + 52;
        int n7 = this.topPos + 14;
        int n8 = this.startIndex + 12;
        this.renderButtons(poseStack, n, n2, n6, n7, n8);
        this.renderRecipes(n6, n7, n8);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int n, int n2) {
        super.renderTooltip(poseStack, n, n2);
        if (this.displayRecipes) {
            int n3 = this.leftPos + 52;
            int n4 = this.topPos + 14;
            int n5 = this.startIndex + 12;
            List<StonecutterRecipe> list = ((StonecutterMenu)this.menu).getRecipes();
            for (int i = this.startIndex; i < n5 && i < ((StonecutterMenu)this.menu).getNumRecipes(); ++i) {
                int n6 = i - this.startIndex;
                int n7 = n3 + n6 % 4 * 16;
                int n8 = n4 + n6 / 4 * 18 + 2;
                if (n < n7 || n >= n7 + 16 || n2 < n8 || n2 >= n8 + 18) continue;
                this.renderTooltip(poseStack, list.get(i).getResultItem(), n, n2);
            }
        }
    }

    private void renderButtons(PoseStack poseStack, int n, int n2, int n3, int n4, int n5) {
        for (int i = this.startIndex; i < n5 && i < ((StonecutterMenu)this.menu).getNumRecipes(); ++i) {
            int n6 = i - this.startIndex;
            int n7 = n3 + n6 % 4 * 16;
            int n8 = n6 / 4;
            int n9 = n4 + n8 * 18 + 2;
            int n10 = this.imageHeight;
            if (i == ((StonecutterMenu)this.menu).getSelectedRecipeIndex()) {
                n10 += 18;
            } else if (n >= n7 && n2 >= n9 && n < n7 + 16 && n2 < n9 + 18) {
                n10 += 36;
            }
            this.blit(poseStack, n7, n9 - 1, 0, n10, 16, 18);
        }
    }

    private void renderRecipes(int n, int n2, int n3) {
        List<StonecutterRecipe> list = ((StonecutterMenu)this.menu).getRecipes();
        for (int i = this.startIndex; i < n3 && i < ((StonecutterMenu)this.menu).getNumRecipes(); ++i) {
            int n4 = i - this.startIndex;
            int n5 = n + n4 % 4 * 16;
            int n6 = n4 / 4;
            int n7 = n2 + n6 * 18 + 2;
            this.minecraft.getItemRenderer().renderAndDecorateItem(list.get(i).getResultItem(), n5, n7);
        }
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        this.scrolling = false;
        if (this.displayRecipes) {
            int n2 = this.leftPos + 52;
            int n3 = this.topPos + 14;
            int n4 = this.startIndex + 12;
            for (int i = this.startIndex; i < n4; ++i) {
                int n5 = i - this.startIndex;
                double d3 = d - (double)(n2 + n5 % 4 * 16);
                double d4 = d2 - (double)(n3 + n5 / 4 * 18);
                if (!(d3 >= 0.0) || !(d4 >= 0.0) || !(d3 < 16.0) || !(d4 < 18.0) || !((StonecutterMenu)this.menu).clickMenuButton(this.minecraft.player, i)) continue;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0f));
                this.minecraft.gameMode.handleInventoryButtonClick(((StonecutterMenu)this.menu).containerId, i);
                return true;
            }
            n2 = this.leftPos + 119;
            n3 = this.topPos + 9;
            if (d >= (double)n2 && d < (double)(n2 + 12) && d2 >= (double)n3 && d2 < (double)(n3 + 54)) {
                this.scrolling = true;
            }
        }
        return super.mouseClicked(d, d2, n);
    }

    @Override
    public boolean mouseDragged(double d, double d2, int n, double d3, double d4) {
        if (this.scrolling && this.isScrollBarActive()) {
            int n2 = this.topPos + 14;
            int n3 = n2 + 54;
            this.scrollOffs = ((float)d2 - (float)n2 - 7.5f) / ((float)(n3 - n2) - 15.0f);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            this.startIndex = (int)((double)(this.scrollOffs * (float)this.getOffscreenRows()) + 0.5) * 4;
            return true;
        }
        return super.mouseDragged(d, d2, n, d3, d4);
    }

    @Override
    public boolean mouseScrolled(double d, double d2, double d3) {
        if (this.isScrollBarActive()) {
            int n = this.getOffscreenRows();
            this.scrollOffs = (float)((double)this.scrollOffs - d3 / (double)n);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            this.startIndex = (int)((double)(this.scrollOffs * (float)n) + 0.5) * 4;
        }
        return true;
    }

    private boolean isScrollBarActive() {
        return this.displayRecipes && ((StonecutterMenu)this.menu).getNumRecipes() > 12;
    }

    protected int getOffscreenRows() {
        return (((StonecutterMenu)this.menu).getNumRecipes() + 4 - 1) / 4 - 3;
    }

    private void containerChanged() {
        this.displayRecipes = ((StonecutterMenu)this.menu).hasInputItem();
        if (!this.displayRecipes) {
            this.scrollOffs = 0.0f;
            this.startIndex = 0;
        }
    }
}

