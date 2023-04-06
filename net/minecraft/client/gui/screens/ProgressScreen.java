/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.ProgressListener;

public class ProgressScreen
extends Screen
implements ProgressListener {
    @Nullable
    private Component header;
    @Nullable
    private Component stage;
    private int progress;
    private boolean stop;

    public ProgressScreen() {
        super(NarratorChatListener.NO_TITLE);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void progressStartNoAbort(Component component) {
        this.progressStart(component);
    }

    @Override
    public void progressStart(Component component) {
        this.header = component;
        this.progressStage(new TranslatableComponent("progress.working"));
    }

    @Override
    public void progressStage(Component component) {
        this.stage = component;
        this.progressStagePercentage(0);
    }

    @Override
    public void progressStagePercentage(int n) {
        this.progress = n;
    }

    @Override
    public void stop() {
        this.stop = true;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        if (this.stop) {
            if (!this.minecraft.isConnectedToRealms()) {
                this.minecraft.setScreen(null);
            }
            return;
        }
        this.renderBackground(poseStack);
        if (this.header != null) {
            ProgressScreen.drawCenteredString(poseStack, this.font, this.header, this.width / 2, 70, 16777215);
        }
        if (this.stage != null && this.progress != 0) {
            ProgressScreen.drawCenteredString(poseStack, this.font, new TextComponent("").append(this.stage).append(" " + this.progress + "%"), this.width / 2, 90, 16777215);
        }
        super.render(poseStack, n, n2, f);
    }
}

