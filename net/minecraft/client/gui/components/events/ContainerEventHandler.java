/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components.events;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.events.GuiEventListener;

public interface ContainerEventHandler
extends GuiEventListener {
    public List<? extends GuiEventListener> children();

    default public Optional<GuiEventListener> getChildAt(double d, double d2) {
        for (GuiEventListener guiEventListener : this.children()) {
            if (!guiEventListener.isMouseOver(d, d2)) continue;
            return Optional.of(guiEventListener);
        }
        return Optional.empty();
    }

    @Override
    default public boolean mouseClicked(double d, double d2, int n) {
        for (GuiEventListener guiEventListener : this.children()) {
            if (!guiEventListener.mouseClicked(d, d2, n)) continue;
            this.setFocused(guiEventListener);
            if (n == 0) {
                this.setDragging(true);
            }
            return true;
        }
        return false;
    }

    @Override
    default public boolean mouseReleased(double d, double d2, int n) {
        this.setDragging(false);
        return this.getChildAt(d, d2).filter(guiEventListener -> guiEventListener.mouseReleased(d, d2, n)).isPresent();
    }

    @Override
    default public boolean mouseDragged(double d, double d2, int n, double d3, double d4) {
        if (this.getFocused() != null && this.isDragging() && n == 0) {
            return this.getFocused().mouseDragged(d, d2, n, d3, d4);
        }
        return false;
    }

    public boolean isDragging();

    public void setDragging(boolean var1);

    @Override
    default public boolean mouseScrolled(double d, double d2, double d3) {
        return this.getChildAt(d, d2).filter(guiEventListener -> guiEventListener.mouseScrolled(d, d2, d3)).isPresent();
    }

    @Override
    default public boolean keyPressed(int n, int n2, int n3) {
        return this.getFocused() != null && this.getFocused().keyPressed(n, n2, n3);
    }

    @Override
    default public boolean keyReleased(int n, int n2, int n3) {
        return this.getFocused() != null && this.getFocused().keyReleased(n, n2, n3);
    }

    @Override
    default public boolean charTyped(char c, int n) {
        return this.getFocused() != null && this.getFocused().charTyped(c, n);
    }

    @Nullable
    public GuiEventListener getFocused();

    public void setFocused(@Nullable GuiEventListener var1);

    default public void setInitialFocus(@Nullable GuiEventListener guiEventListener) {
        this.setFocused(guiEventListener);
        guiEventListener.changeFocus(true);
    }

    default public void magicalSpecialHackyFocus(@Nullable GuiEventListener guiEventListener) {
        this.setFocused(guiEventListener);
    }

    @Override
    default public boolean changeFocus(boolean bl) {
        boolean bl2;
        BooleanSupplier booleanSupplier;
        Supplier<GuiEventListener> supplier;
        GuiEventListener guiEventListener = this.getFocused();
        boolean bl3 = bl2 = guiEventListener != null;
        if (bl2 && guiEventListener.changeFocus(bl)) {
            return true;
        }
        List<? extends GuiEventListener> list = this.children();
        int n = list.indexOf(guiEventListener);
        int n2 = bl2 && n >= 0 ? n + (bl ? 1 : 0) : (bl ? 0 : list.size());
        ListIterator<? extends GuiEventListener> listIterator = list.listIterator(n2);
        BooleanSupplier booleanSupplier2 = bl ? listIterator::hasNext : (booleanSupplier = listIterator::hasPrevious);
        Supplier<GuiEventListener> supplier2 = bl ? listIterator::next : (supplier = listIterator::previous);
        while (booleanSupplier.getAsBoolean()) {
            GuiEventListener guiEventListener2 = supplier.get();
            if (!guiEventListener2.changeFocus(bl)) continue;
            this.setFocused(guiEventListener2);
            return true;
        }
        this.setFocused(null);
        return false;
    }
}

