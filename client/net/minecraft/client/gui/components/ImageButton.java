/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class ImageButton
extends Button {
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;

    public ImageButton(int n, int n2, int n3, int n4, int n5, int n6, int n7, ResourceLocation resourceLocation, Button.OnPress onPress) {
        this(n, n2, n3, n4, n5, n6, n7, resourceLocation, 256, 256, onPress);
    }

    public ImageButton(int n, int n2, int n3, int n4, int n5, int n6, int n7, ResourceLocation resourceLocation, int n8, int n9, Button.OnPress onPress) {
        this(n, n2, n3, n4, n5, n6, n7, resourceLocation, n8, n9, onPress, TextComponent.EMPTY);
    }

    public ImageButton(int n, int n2, int n3, int n4, int n5, int n6, int n7, ResourceLocation resourceLocation, int n8, int n9, Button.OnPress onPress, Component component) {
        this(n, n2, n3, n4, n5, n6, n7, resourceLocation, n8, n9, onPress, NO_TOOLTIP, component);
    }

    public ImageButton(int n, int n2, int n3, int n4, int n5, int n6, int n7, ResourceLocation resourceLocation, int n8, int n9, Button.OnPress onPress, Button.OnTooltip onTooltip, Component component) {
        super(n, n2, n3, n4, component, onPress, onTooltip);
        this.textureWidth = n8;
        this.textureHeight = n9;
        this.xTexStart = n5;
        this.yTexStart = n6;
        this.yDiffTex = n7;
        this.resourceLocation = resourceLocation;
    }

    public void setPosition(int n, int n2) {
        this.x = n;
        this.y = n2;
    }

    @Override
    public void renderButton(PoseStack poseStack, int n, int n2, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(this.resourceLocation);
        int n3 = this.yTexStart;
        if (this.isHovered()) {
            n3 += this.yDiffTex;
        }
        RenderSystem.enableDepthTest();
        ImageButton.blit(poseStack, this.x, this.y, this.xTexStart, n3, this.width, this.height, this.textureWidth, this.textureHeight);
        if (this.isHovered()) {
            this.renderToolTip(poseStack, n, n2);
        }
    }
}

