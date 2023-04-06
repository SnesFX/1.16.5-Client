/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class Checkbox
extends AbstractButton {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
    private boolean selected;
    private final boolean showLabel;

    public Checkbox(int n, int n2, int n3, int n4, Component component, boolean bl) {
        this(n, n2, n3, n4, component, bl, true);
    }

    public Checkbox(int n, int n2, int n3, int n4, Component component, boolean bl, boolean bl2) {
        super(n, n2, n3, n4, component);
        this.selected = bl;
        this.showLabel = bl2;
    }

    @Override
    public void onPress() {
        this.selected = !this.selected;
    }

    public boolean selected() {
        return this.selected;
    }

    @Override
    public void renderButton(PoseStack poseStack, int n, int n2, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(TEXTURE);
        RenderSystem.enableDepthTest();
        Font font = minecraft.font;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Checkbox.blit(poseStack, this.x, this.y, this.isFocused() ? 20.0f : 0.0f, this.selected ? 20.0f : 0.0f, 20, this.height, 64, 64);
        this.renderBg(poseStack, minecraft, n, n2);
        if (this.showLabel) {
            Checkbox.drawString(poseStack, font, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 0xE0E0E0 | Mth.ceil(this.alpha * 255.0f) << 24);
        }
    }
}

