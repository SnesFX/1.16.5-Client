/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class CartographyTableScreen
extends AbstractContainerScreen<CartographyTableMenu> {
    private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/cartography_table.png");

    public CartographyTableScreen(CartographyTableMenu cartographyTableMenu, Inventory inventory, Component component) {
        super(cartographyTableMenu, inventory, component);
        this.titleLabelY -= 2;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        super.render(poseStack, n, n2, f);
        this.renderTooltip(poseStack, n, n2);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int n, int n2) {
        MapItemSavedData mapItemSavedData;
        this.renderBackground(poseStack);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(BG_LOCATION);
        int n3 = this.leftPos;
        int n4 = this.topPos;
        this.blit(poseStack, n3, n4, 0, 0, this.imageWidth, this.imageHeight);
        Item item = ((CartographyTableMenu)this.menu).getSlot(1).getItem().getItem();
        boolean bl = item == Items.MAP;
        boolean bl2 = item == Items.PAPER;
        boolean bl3 = item == Items.GLASS_PANE;
        ItemStack itemStack = ((CartographyTableMenu)this.menu).getSlot(0).getItem();
        boolean bl4 = false;
        if (itemStack.getItem() == Items.FILLED_MAP) {
            mapItemSavedData = MapItem.getSavedData(itemStack, this.minecraft.level);
            if (mapItemSavedData != null) {
                if (mapItemSavedData.locked) {
                    bl4 = true;
                    if (bl2 || bl3) {
                        this.blit(poseStack, n3 + 35, n4 + 31, this.imageWidth + 50, 132, 28, 21);
                    }
                }
                if (bl2 && mapItemSavedData.scale >= 4) {
                    bl4 = true;
                    this.blit(poseStack, n3 + 35, n4 + 31, this.imageWidth + 50, 132, 28, 21);
                }
            }
        } else {
            mapItemSavedData = null;
        }
        this.renderResultingMap(poseStack, mapItemSavedData, bl, bl2, bl3, bl4);
    }

    private void renderResultingMap(PoseStack poseStack, @Nullable MapItemSavedData mapItemSavedData, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        int n = this.leftPos;
        int n2 = this.topPos;
        if (bl2 && !bl4) {
            this.blit(poseStack, n + 67, n2 + 13, this.imageWidth, 66, 66, 66);
            this.renderMap(mapItemSavedData, n + 85, n2 + 31, 0.226f);
        } else if (bl) {
            this.blit(poseStack, n + 67 + 16, n2 + 13, this.imageWidth, 132, 50, 66);
            this.renderMap(mapItemSavedData, n + 86, n2 + 16, 0.34f);
            this.minecraft.getTextureManager().bind(BG_LOCATION);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0f, 0.0f, 1.0f);
            this.blit(poseStack, n + 67, n2 + 13 + 16, this.imageWidth, 132, 50, 66);
            this.renderMap(mapItemSavedData, n + 70, n2 + 32, 0.34f);
            RenderSystem.popMatrix();
        } else if (bl3) {
            this.blit(poseStack, n + 67, n2 + 13, this.imageWidth, 0, 66, 66);
            this.renderMap(mapItemSavedData, n + 71, n2 + 17, 0.45f);
            this.minecraft.getTextureManager().bind(BG_LOCATION);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0f, 0.0f, 1.0f);
            this.blit(poseStack, n + 66, n2 + 12, 0, this.imageHeight, 66, 66);
            RenderSystem.popMatrix();
        } else {
            this.blit(poseStack, n + 67, n2 + 13, this.imageWidth, 0, 66, 66);
            this.renderMap(mapItemSavedData, n + 71, n2 + 17, 0.45f);
        }
    }

    private void renderMap(@Nullable MapItemSavedData mapItemSavedData, int n, int n2, float f) {
        if (mapItemSavedData != null) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(n, n2, 1.0f);
            RenderSystem.scalef(f, f, 1.0f);
            MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            this.minecraft.gameRenderer.getMapRenderer().render(new PoseStack(), bufferSource, mapItemSavedData, true, 15728880);
            bufferSource.endBatch();
            RenderSystem.popMatrix();
        }
    }
}

