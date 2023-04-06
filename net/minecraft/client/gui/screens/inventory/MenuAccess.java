/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;

public interface MenuAccess<T extends AbstractContainerMenu> {
    public T getMenu();
}

