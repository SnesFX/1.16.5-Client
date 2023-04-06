/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;

public class Button
extends AbstractButton {
    public static final OnTooltip NO_TOOLTIP = (button, poseStack, n, n2) -> {};
    protected final OnPress onPress;
    protected final OnTooltip onTooltip;

    public Button(int n, int n2, int n3, int n4, Component component, OnPress onPress) {
        this(n, n2, n3, n4, component, onPress, NO_TOOLTIP);
    }

    public Button(int n, int n2, int n3, int n4, Component component, OnPress onPress, OnTooltip onTooltip) {
        super(n, n2, n3, n4, component);
        this.onPress = onPress;
        this.onTooltip = onTooltip;
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    public void renderButton(PoseStack poseStack, int n, int n2, float f) {
        super.renderButton(poseStack, n, n2, f);
        if (this.isHovered()) {
            this.renderToolTip(poseStack, n, n2);
        }
    }

    @Override
    public void renderToolTip(PoseStack poseStack, int n, int n2) {
        this.onTooltip.onTooltip(this, poseStack, n, n2);
    }

    public static interface OnTooltip {
        public void onTooltip(Button var1, PoseStack var2, int var3, int var4);
    }

    public static interface OnPress {
        public void onPress(Button var1);
    }

}

