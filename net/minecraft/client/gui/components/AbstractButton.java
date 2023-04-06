/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public abstract class AbstractButton
extends AbstractWidget {
    public AbstractButton(int n, int n2, int n3, int n4, Component component) {
        super(n, n2, n3, n4, component);
    }

    public abstract void onPress();

    @Override
    public void onClick(double d, double d2) {
        this.onPress();
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (n == 257 || n == 32 || n == 335) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress();
            return true;
        }
        return false;
    }
}

