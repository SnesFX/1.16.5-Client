/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class PageButton
extends Button {
    private final boolean isForward;
    private final boolean playTurnSound;

    public PageButton(int n, int n2, boolean bl, Button.OnPress onPress, boolean bl2) {
        super(n, n2, 23, 13, TextComponent.EMPTY, onPress);
        this.isForward = bl;
        this.playTurnSound = bl2;
    }

    @Override
    public void renderButton(PoseStack poseStack, int n, int n2, float f) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getInstance().getTextureManager().bind(BookViewScreen.BOOK_LOCATION);
        int n3 = 0;
        int n4 = 192;
        if (this.isHovered()) {
            n3 += 23;
        }
        if (!this.isForward) {
            n4 += 13;
        }
        this.blit(poseStack, this.x, this.y, n3, n4, 23, 13);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        if (this.playTurnSound) {
            soundManager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0f));
        }
    }
}

