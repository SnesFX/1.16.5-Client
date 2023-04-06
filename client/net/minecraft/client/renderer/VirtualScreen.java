/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.MonitorCreator;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public final class VirtualScreen
implements AutoCloseable {
    private final Minecraft minecraft;
    private final ScreenManager screenManager;

    public VirtualScreen(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.screenManager = new ScreenManager(Monitor::new);
    }

    public Window newWindow(DisplayData displayData, @Nullable String string, String string2) {
        return new Window(this.minecraft, this.screenManager, displayData, string, string2);
    }

    @Override
    public void close() {
        this.screenManager.shutdown();
    }
}

