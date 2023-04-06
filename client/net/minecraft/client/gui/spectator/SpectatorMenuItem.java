/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.spectator;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.network.chat.Component;

public interface SpectatorMenuItem {
    public void selectItem(SpectatorMenu var1);

    public Component getName();

    public void renderIcon(PoseStack var1, float var2, int var3);

    public boolean isEnabled();
}

