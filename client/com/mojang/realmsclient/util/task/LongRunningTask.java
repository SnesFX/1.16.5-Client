/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.gui.ErrorCallback;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LongRunningTask
implements ErrorCallback,
Runnable {
    public static final Logger LOGGER = LogManager.getLogger();
    protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

    protected static void pause(int n) {
        try {
            Thread.sleep(n * 1000);
        }
        catch (InterruptedException interruptedException) {
            LOGGER.error("", (Throwable)interruptedException);
        }
    }

    public static void setScreen(Screen screen) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> minecraft.setScreen(screen));
    }

    public void setScreen(RealmsLongRunningMcoTaskScreen realmsLongRunningMcoTaskScreen) {
        this.longRunningMcoTaskScreen = realmsLongRunningMcoTaskScreen;
    }

    @Override
    public void error(Component component) {
        this.longRunningMcoTaskScreen.error(component);
    }

    public void setTitle(Component component) {
        this.longRunningMcoTaskScreen.setTitle(component);
    }

    public boolean aborted() {
        return this.longRunningMcoTaskScreen.aborted();
    }

    public void tick() {
    }

    public void init() {
    }

    public void abortTask() {
    }
}

