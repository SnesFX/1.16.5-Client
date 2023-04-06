/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public interface ErrorCallback {
    public void error(Component var1);

    default public void error(String string) {
        this.error(new TextComponent(string));
    }
}

