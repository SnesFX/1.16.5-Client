/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.ErrorCallback;
import com.mojang.realmsclient.util.task.LongRunningTask;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsLongRunningMcoTaskScreen
extends RealmsScreen
implements ErrorCallback {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen lastScreen;
    private volatile Component title = TextComponent.EMPTY;
    @Nullable
    private volatile Component errorMessage;
    private volatile boolean aborted;
    private int animTicks;
    private final LongRunningTask task;
    private final int buttonLength = 212;
    public static final String[] SYMBOLS = new String[]{"\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _"};

    public RealmsLongRunningMcoTaskScreen(Screen screen, LongRunningTask longRunningTask) {
        this.lastScreen = screen;
        this.task = longRunningTask;
        longRunningTask.setScreen(this);
        Thread thread = new Thread((Runnable)longRunningTask, "Realms-long-running-task");
        thread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
    }

    @Override
    public void tick() {
        super.tick();
        NarrationHelper.repeatedly(this.title.getString());
        ++this.animTicks;
        this.task.tick();
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.cancelOrBackButtonClicked();
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    @Override
    public void init() {
        this.task.init();
        this.addButton(new Button(this.width / 2 - 106, RealmsLongRunningMcoTaskScreen.row(12), 212, 20, CommonComponents.GUI_CANCEL, button -> this.cancelOrBackButtonClicked()));
    }

    private void cancelOrBackButtonClicked() {
        this.aborted = true;
        this.task.abortTask();
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        RealmsLongRunningMcoTaskScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, RealmsLongRunningMcoTaskScreen.row(3), 16777215);
        Component component = this.errorMessage;
        if (component == null) {
            RealmsLongRunningMcoTaskScreen.drawCenteredString(poseStack, this.font, SYMBOLS[this.animTicks % SYMBOLS.length], this.width / 2, RealmsLongRunningMcoTaskScreen.row(8), 8421504);
        } else {
            RealmsLongRunningMcoTaskScreen.drawCenteredString(poseStack, this.font, component, this.width / 2, RealmsLongRunningMcoTaskScreen.row(8), 16711680);
        }
        super.render(poseStack, n, n2, f);
    }

    @Override
    public void error(Component component) {
        this.errorMessage = component;
        NarrationHelper.now(component.getString());
        this.buttonsClear();
        this.addButton(new Button(this.width / 2 - 106, this.height / 4 + 120 + 12, 200, 20, CommonComponents.GUI_BACK, button -> this.cancelOrBackButtonClicked()));
    }

    private void buttonsClear() {
        HashSet hashSet = Sets.newHashSet((Iterable)this.buttons);
        this.children.removeIf(hashSet::contains);
        this.buttons.clear();
    }

    public void setTitle(Component component) {
        this.title = component;
    }

    public boolean aborted() {
        return this.aborted;
    }
}

