/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;

public class BrewingStandScreen
extends AbstractContainerScreen<BrewingStandMenu> {
    private static final ResourceLocation BREWING_STAND_LOCATION = new ResourceLocation("textures/gui/container/brewing_stand.png");
    private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};

    public BrewingStandScreen(BrewingStandMenu brewingStandMenu, Inventory inventory, Component component) {
        super(brewingStandMenu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        super.render(poseStack, n, n2, f);
        this.renderTooltip(poseStack, n, n2);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int n, int n2) {
        int n3;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(BREWING_STAND_LOCATION);
        int n4 = (this.width - this.imageWidth) / 2;
        int n5 = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, n4, n5, 0, 0, this.imageWidth, this.imageHeight);
        int n6 = ((BrewingStandMenu)this.menu).getFuel();
        int n7 = Mth.clamp((18 * n6 + 20 - 1) / 20, 0, 18);
        if (n7 > 0) {
            this.blit(poseStack, n4 + 60, n5 + 44, 176, 29, n7, 4);
        }
        if ((n3 = ((BrewingStandMenu)this.menu).getBrewingTicks()) > 0) {
            int n8 = (int)(28.0f * (1.0f - (float)n3 / 400.0f));
            if (n8 > 0) {
                this.blit(poseStack, n4 + 97, n5 + 16, 176, 0, 9, n8);
            }
            if ((n8 = BUBBLELENGTHS[n3 / 2 % 7]) > 0) {
                this.blit(poseStack, n4 + 63, n5 + 14 + 29 - n8, 185, 29 - n8, 12, n8);
            }
        }
    }
}

