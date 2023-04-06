/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;

public class HorseInventoryScreen
extends AbstractContainerScreen<HorseInventoryMenu> {
    private static final ResourceLocation HORSE_INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/horse.png");
    private final AbstractHorse horse;
    private float xMouse;
    private float yMouse;

    public HorseInventoryScreen(HorseInventoryMenu horseInventoryMenu, Inventory inventory, AbstractHorse abstractHorse) {
        super(horseInventoryMenu, inventory, abstractHorse.getDisplayName());
        this.horse = abstractHorse;
        this.passEvents = false;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int n, int n2) {
        AbstractChestedHorse abstractChestedHorse;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(HORSE_INVENTORY_LOCATION);
        int n3 = (this.width - this.imageWidth) / 2;
        int n4 = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, n3, n4, 0, 0, this.imageWidth, this.imageHeight);
        if (this.horse instanceof AbstractChestedHorse && (abstractChestedHorse = (AbstractChestedHorse)this.horse).hasChest()) {
            this.blit(poseStack, n3 + 79, n4 + 17, 0, this.imageHeight, abstractChestedHorse.getInventoryColumns() * 18, 54);
        }
        if (this.horse.isSaddleable()) {
            this.blit(poseStack, n3 + 7, n4 + 35 - 18, 18, this.imageHeight + 54, 18, 18);
        }
        if (this.horse.canWearArmor()) {
            if (this.horse instanceof Llama) {
                this.blit(poseStack, n3 + 7, n4 + 35, 36, this.imageHeight + 54, 18, 18);
            } else {
                this.blit(poseStack, n3 + 7, n4 + 35, 0, this.imageHeight + 54, 18, 18);
            }
        }
        InventoryScreen.renderEntityInInventory(n3 + 51, n4 + 60, 17, (float)(n3 + 51) - this.xMouse, (float)(n4 + 75 - 50) - this.yMouse, this.horse);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.xMouse = n;
        this.yMouse = n2;
        super.render(poseStack, n, n2, f);
        this.renderTooltip(poseStack, n, n2);
    }
}

