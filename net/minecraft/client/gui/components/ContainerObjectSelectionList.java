/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

public abstract class ContainerObjectSelectionList<E extends Entry<E>>
extends AbstractSelectionList<E> {
    public ContainerObjectSelectionList(Minecraft minecraft, int n, int n2, int n3, int n4, int n5) {
        super(minecraft, n, n2, n3, n4, n5);
    }

    @Override
    public boolean changeFocus(boolean bl) {
        boolean bl2 = super.changeFocus(bl);
        if (bl2) {
            this.ensureVisible(this.getFocused());
        }
        return bl2;
    }

    @Override
    protected boolean isSelectedItem(int n) {
        return false;
    }

    public static abstract class Entry<E extends Entry<E>>
    extends AbstractSelectionList.Entry<E>
    implements ContainerEventHandler {
        @Nullable
        private GuiEventListener focused;
        private boolean dragging;

        @Override
        public boolean isDragging() {
            return this.dragging;
        }

        @Override
        public void setDragging(boolean bl) {
            this.dragging = bl;
        }

        @Override
        public void setFocused(@Nullable GuiEventListener guiEventListener) {
            this.focused = guiEventListener;
        }

        @Nullable
        @Override
        public GuiEventListener getFocused() {
            return this.focused;
        }
    }

}

