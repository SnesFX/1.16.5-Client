/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components.events;

public interface GuiEventListener {
    default public void mouseMoved(double d, double d2) {
    }

    default public boolean mouseClicked(double d, double d2, int n) {
        return false;
    }

    default public boolean mouseReleased(double d, double d2, int n) {
        return false;
    }

    default public boolean mouseDragged(double d, double d2, int n, double d3, double d4) {
        return false;
    }

    default public boolean mouseScrolled(double d, double d2, double d3) {
        return false;
    }

    default public boolean keyPressed(int n, int n2, int n3) {
        return false;
    }

    default public boolean keyReleased(int n, int n2, int n3) {
        return false;
    }

    default public boolean charTyped(char c, int n) {
        return false;
    }

    default public boolean changeFocus(boolean bl) {
        return false;
    }

    default public boolean isMouseOver(double d, double d2) {
        return false;
    }
}

