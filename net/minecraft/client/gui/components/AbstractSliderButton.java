/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class AbstractSliderButton
extends AbstractWidget {
    protected double value;

    public AbstractSliderButton(int n, int n2, int n3, int n4, Component component, double d) {
        super(n, n2, n3, n4, component);
        this.value = d;
    }

    @Override
    protected int getYImage(boolean bl) {
        return 0;
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return new TranslatableComponent("gui.narrate.slider", this.getMessage());
    }

    @Override
    protected void renderBg(PoseStack poseStack, Minecraft minecraft, int n, int n2) {
        minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int n3 = (this.isHovered() ? 2 : 1) * 20;
        this.blit(poseStack, this.x + (int)(this.value * (double)(this.width - 8)), this.y, 0, 46 + n3, 4, 20);
        this.blit(poseStack, this.x + (int)(this.value * (double)(this.width - 8)) + 4, this.y, 196, 46 + n3, 4, 20);
    }

    @Override
    public void onClick(double d, double d2) {
        this.setValueFromMouse(d);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        boolean bl;
        boolean bl2 = bl = n == 263;
        if (bl || n == 262) {
            float f = bl ? -1.0f : 1.0f;
            this.setValue(this.value + (double)(f / (float)(this.width - 8)));
        }
        return false;
    }

    private void setValueFromMouse(double d) {
        this.setValue((d - (double)(this.x + 4)) / (double)(this.width - 8));
    }

    private void setValue(double d) {
        double d2 = this.value;
        this.value = Mth.clamp(d, 0.0, 1.0);
        if (d2 != this.value) {
            this.applyValue();
        }
        this.updateMessage();
    }

    @Override
    protected void onDrag(double d, double d2, double d3, double d4) {
        this.setValueFromMouse(d);
        super.onDrag(d, d2, d3, d4);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    public void onRelease(double d, double d2) {
        super.playDownSound(Minecraft.getInstance().getSoundManager());
    }

    protected abstract void updateMessage();

    protected abstract void applyValue();
}

