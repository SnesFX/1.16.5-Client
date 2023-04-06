/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class StateSwitchingButton
extends AbstractWidget {
    protected ResourceLocation resourceLocation;
    protected boolean isStateTriggered;
    protected int xTexStart;
    protected int yTexStart;
    protected int xDiffTex;
    protected int yDiffTex;

    public StateSwitchingButton(int n, int n2, int n3, int n4, boolean bl) {
        super(n, n2, n3, n4, TextComponent.EMPTY);
        this.isStateTriggered = bl;
    }

    public void initTextureValues(int n, int n2, int n3, int n4, ResourceLocation resourceLocation) {
        this.xTexStart = n;
        this.yTexStart = n2;
        this.xDiffTex = n3;
        this.yDiffTex = n4;
        this.resourceLocation = resourceLocation;
    }

    public void setStateTriggered(boolean bl) {
        this.isStateTriggered = bl;
    }

    public boolean isStateTriggered() {
        return this.isStateTriggered;
    }

    public void setPosition(int n, int n2) {
        this.x = n;
        this.y = n2;
    }

    @Override
    public void renderButton(PoseStack poseStack, int n, int n2, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(this.resourceLocation);
        RenderSystem.disableDepthTest();
        int n3 = this.xTexStart;
        int n4 = this.yTexStart;
        if (this.isStateTriggered) {
            n3 += this.xDiffTex;
        }
        if (this.isHovered()) {
            n4 += this.yDiffTex;
        }
        this.blit(poseStack, this.x, this.y, n3, n4, this.width, this.height);
        RenderSystem.enableDepthTest();
    }
}

