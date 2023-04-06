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
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;

public class BackupConfirmScreen
extends Screen {
    @Nullable
    private final Screen lastScreen;
    protected final Listener listener;
    private final Component description;
    private final boolean promptForCacheErase;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    private Checkbox eraseCache;

    public BackupConfirmScreen(@Nullable Screen screen, Listener listener, Component component, Component component2, boolean bl) {
        super(component);
        this.lastScreen = screen;
        this.listener = listener;
        this.description = component2;
        this.promptForCacheErase = bl;
    }

    @Override
    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, (FormattedText)this.description, this.width - 50);
        this.font.getClass();
        int n = (this.message.getLineCount() + 1) * 9;
        this.addButton(new Button(this.width / 2 - 155, 100 + n, 150, 20, new TranslatableComponent("selectWorld.backupJoinConfirmButton"), button -> this.listener.proceed(true, this.eraseCache.selected())));
        this.addButton(new Button(this.width / 2 - 155 + 160, 100 + n, 150, 20, new TranslatableComponent("selectWorld.backupJoinSkipButton"), button -> this.listener.proceed(false, this.eraseCache.selected())));
        this.addButton(new Button(this.width / 2 - 155 + 80, 124 + n, 150, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(this.lastScreen)));
        this.eraseCache = new Checkbox(this.width / 2 - 155 + 80, 76 + n, 150, 20, new TranslatableComponent("selectWorld.backupEraseCache"), false);
        if (this.promptForCacheErase) {
            this.addButton(this.eraseCache);
        }
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        BackupConfirmScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 50, 16777215);
        this.message.renderCentered(poseStack, this.width / 2, 70);
        super.render(poseStack, n, n2, f);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    public static interface Listener {
        public void proceed(boolean var1, boolean var2);
    }

}

