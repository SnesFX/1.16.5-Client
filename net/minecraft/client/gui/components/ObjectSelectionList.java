/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;

public abstract class ObjectSelectionList<E extends AbstractSelectionList.Entry<E>>
extends AbstractSelectionList<E> {
    private boolean inFocus;

    public ObjectSelectionList(Minecraft minecraft, int n, int n2, int n3, int n4, int n5) {
        super(minecraft, n, n2, n3, n4, n5);
    }

    @Override
    public boolean changeFocus(boolean bl) {
        if (!this.inFocus && this.getItemCount() == 0) {
            return false;
        }
        boolean bl2 = this.inFocus = !this.inFocus;
        if (this.inFocus && this.getSelected() == null && this.getItemCount() > 0) {
            this.moveSelection(AbstractSelectionList.SelectionDirection.DOWN);
        } else if (this.inFocus && this.getSelected() != null) {
            this.refreshSelection();
        }
        return this.inFocus;
    }

    public static abstract class Entry<E extends Entry<E>>
    extends AbstractSelectionList.Entry<E> {
        @Override
        public boolean changeFocus(boolean bl) {
            return false;
        }
    }

}

