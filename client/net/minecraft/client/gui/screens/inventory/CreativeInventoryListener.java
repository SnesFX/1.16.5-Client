/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

public class CreativeInventoryListener
implements ContainerListener {
    private final Minecraft minecraft;

    public CreativeInventoryListener(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void refreshContainer(AbstractContainerMenu abstractContainerMenu, NonNullList<ItemStack> nonNullList) {
    }

    @Override
    public void slotChanged(AbstractContainerMenu abstractContainerMenu, int n, ItemStack itemStack) {
        this.minecraft.gameMode.handleCreativeModeItemAdd(itemStack, n);
    }

    @Override
    public void setContainerData(AbstractContainerMenu abstractContainerMenu, int n, int n2) {
    }
}

