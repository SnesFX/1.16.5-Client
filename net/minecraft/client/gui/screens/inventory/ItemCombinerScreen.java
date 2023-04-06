/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ItemCombinerScreen<T extends ItemCombinerMenu>
extends AbstractContainerScreen<T>
implements ContainerListener {
    private ResourceLocation menuResource;

    public ItemCombinerScreen(T t, Inventory inventory, Component component, ResourceLocation resourceLocation) {
        super(t, inventory, component);
        this.menuResource = resourceLocation;
    }

    protected void subInit() {
    }

    @Override
    protected void init() {
        super.init();
        this.subInit();
        ((ItemCombinerMenu)this.menu).addSlotListener(this);
    }

    @Override
    public void removed() {
        super.removed();
        ((ItemCombinerMenu)this.menu).removeSlotListener(this);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        super.render(poseStack, n, n2, f);
        RenderSystem.disableBlend();
        this.renderFg(poseStack, n, n2, f);
        this.renderTooltip(poseStack, n, n2);
    }

    protected void renderFg(PoseStack poseStack, int n, int n2, float f) {
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int n, int n2) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(this.menuResource);
        int n3 = (this.width - this.imageWidth) / 2;
        int n4 = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, n3, n4, 0, 0, this.imageWidth, this.imageHeight);
        this.blit(poseStack, n3 + 59, n4 + 20, 0, this.imageHeight + (((ItemCombinerMenu)this.menu).getSlot(0).hasItem() ? 0 : 16), 110, 16);
        if ((((ItemCombinerMenu)this.menu).getSlot(0).hasItem() || ((ItemCombinerMenu)this.menu).getSlot(1).hasItem()) && !((ItemCombinerMenu)this.menu).getSlot(2).hasItem()) {
            this.blit(poseStack, n3 + 99, n4 + 45, this.imageWidth, 0, 28, 21);
        }
    }

    @Override
    public void refreshContainer(AbstractContainerMenu abstractContainerMenu, NonNullList<ItemStack> nonNullList) {
        this.slotChanged(abstractContainerMenu, 0, abstractContainerMenu.getSlot(0).getItem());
    }

    @Override
    public void setContainerData(AbstractContainerMenu abstractContainerMenu, int n, int n2) {
    }

    @Override
    public void slotChanged(AbstractContainerMenu abstractContainerMenu, int n, ItemStack itemStack) {
    }
}

