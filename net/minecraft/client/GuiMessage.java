/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client;

public class GuiMessage<T> {
    private final int addedTime;
    private final T message;
    private final int id;

    public GuiMessage(int n, T t, int n2) {
        this.message = t;
        this.addedTime = n;
        this.id = n2;
    }

    public T getMessage() {
        return this.message;
    }

    public int getAddedTime() {
        return this.addedTime;
    }

    public int getId() {
        return this.id;
    }
}

