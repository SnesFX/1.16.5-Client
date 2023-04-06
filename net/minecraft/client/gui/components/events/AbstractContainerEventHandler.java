/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components.events;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

public abstract class AbstractContainerEventHandler
extends GuiComponent
implements ContainerEventHandler {
    @Nullable
    private GuiEventListener focused;
    private boolean isDragging;

    @Override
    public final boolean isDragging() {
        return this.isDragging;
    }

    @Override
    public final void setDragging(boolean bl) {
        this.isDragging = bl;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener guiEventListener) {
        this.focused = guiEventListener;
    }
}

