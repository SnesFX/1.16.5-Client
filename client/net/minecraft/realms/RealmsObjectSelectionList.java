/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.realms;

import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;

public abstract class RealmsObjectSelectionList<E extends ObjectSelectionList.Entry<E>>
extends ObjectSelectionList<E> {
    protected RealmsObjectSelectionList(int n, int n2, int n3, int n4, int n5) {
        super(Minecraft.getInstance(), n, n2, n3, n4, n5);
    }

    public void setSelectedItem(int n) {
        if (n == -1) {
            this.setSelected(null);
        } else if (super.getItemCount() != 0) {
            this.setSelected(this.getEntry(n));
        }
    }

    public void selectItem(int n) {
        this.setSelectedItem(n);
    }

    public void itemClicked(int n, int n2, double d, double d2, int n3) {
    }

    @Override
    public int getMaxPosition() {
        return 0;
    }

    @Override
    public int getScrollbarPosition() {
        return this.getRowLeft() + this.getRowWidth();
    }

    @Override
    public int getRowWidth() {
        return (int)((double)this.width * 0.6);
    }

    @Override
    public void replaceEntries(Collection<E> collection) {
        super.replaceEntries(collection);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getRowTop(int n) {
        return super.getRowTop(n);
    }

    @Override
    public int getRowLeft() {
        return super.getRowLeft();
    }

    @Override
    public int addEntry(E e) {
        return super.addEntry(e);
    }

    public void clear() {
        this.clearEntries();
    }

    @Override
    public /* synthetic */ int addEntry(AbstractSelectionList.Entry entry) {
        return this.addEntry((E)((ObjectSelectionList.Entry)entry));
    }
}

