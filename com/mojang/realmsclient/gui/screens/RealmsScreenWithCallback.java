/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.WorldTemplate;
import javax.annotation.Nullable;
import net.minecraft.realms.RealmsScreen;

public abstract class RealmsScreenWithCallback
extends RealmsScreen {
    protected abstract void callback(@Nullable WorldTemplate var1);
}

