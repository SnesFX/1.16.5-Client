/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.realms;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RealmsLabel
implements GuiEventListener {
    private final Component text;
    private final int x;
    private final int y;
    private final int color;

    public RealmsLabel(Component component, int n, int n2, int n3) {
        this.text = component;
        this.x = n;
        this.y = n2;
        this.color = n3;
    }

    public void render(Screen screen, PoseStack poseStack) {
        Screen.drawCenteredString(poseStack, Minecraft.getInstance().font, this.text, this.x, this.y, this.color);
    }

    public String getText() {
        return this.text.getString();
    }
}

