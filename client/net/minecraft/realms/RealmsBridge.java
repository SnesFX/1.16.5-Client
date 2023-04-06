/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.realms;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.realms.RealmsScreen;

public class RealmsBridge
extends RealmsScreen {
    private Screen previousScreen;

    public void switchToRealms(Screen screen) {
        this.previousScreen = screen;
        Minecraft.getInstance().setScreen(new RealmsMainScreen(this));
    }

    @Nullable
    public RealmsScreen getNotificationScreen(Screen screen) {
        this.previousScreen = screen;
        return new RealmsNotificationsScreen();
    }

    @Override
    public void init() {
        Minecraft.getInstance().setScreen(this.previousScreen);
    }
}

