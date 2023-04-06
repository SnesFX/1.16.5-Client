/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer;

public class Rect2i {
    private int xPos;
    private int yPos;
    private int width;
    private int height;

    public Rect2i(int n, int n2, int n3, int n4) {
        this.xPos = n;
        this.yPos = n2;
        this.width = n3;
        this.height = n4;
    }

    public int getX() {
        return this.xPos;
    }

    public int getY() {
        return this.yPos;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean contains(int n, int n2) {
        return n >= this.xPos && n <= this.xPos + this.width && n2 >= this.yPos && n2 <= this.yPos + this.height;
    }
}

