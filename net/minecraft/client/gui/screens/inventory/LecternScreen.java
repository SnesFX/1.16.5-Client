/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;

public class LecternScreen
extends BookViewScreen
implements MenuAccess<LecternMenu> {
    private final LecternMenu menu;
    private final ContainerListener listener = new ContainerListener(){

        @Override
        public void refreshContainer(AbstractContainerMenu abstractContainerMenu, NonNullList<ItemStack> nonNullList) {
            LecternScreen.this.bookChanged();
        }

        @Override
        public void slotChanged(AbstractContainerMenu abstractContainerMenu, int n, ItemStack itemStack) {
            LecternScreen.this.bookChanged();
        }

        @Override
        public void setContainerData(AbstractContainerMenu abstractContainerMenu, int n, int n2) {
            if (n == 0) {
                LecternScreen.this.pageChanged();
            }
        }
    };

    public LecternScreen(LecternMenu lecternMenu, Inventory inventory, Component component) {
        this.menu = lecternMenu;
    }

    @Override
    public LecternMenu getMenu() {
        return this.menu;
    }

    @Override
    protected void init() {
        super.init();
        this.menu.addSlotListener(this.listener);
    }

    @Override
    public void onClose() {
        this.minecraft.player.closeContainer();
        super.onClose();
    }

    @Override
    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this.listener);
    }

    @Override
    protected void createMenuControls() {
        if (this.minecraft.player.mayBuild()) {
            this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(null)));
            this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, new TranslatableComponent("lectern.take_book"), button -> this.sendButtonClick(3)));
        } else {
            super.createMenuControls();
        }
    }

    @Override
    protected void pageBack() {
        this.sendButtonClick(1);
    }

    @Override
    protected void pageForward() {
        this.sendButtonClick(2);
    }

    @Override
    protected boolean forcePage(int n) {
        if (n != this.menu.getPage()) {
            this.sendButtonClick(100 + n);
            return true;
        }
        return false;
    }

    private void sendButtonClick(int n) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, n);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void bookChanged() {
        ItemStack itemStack = this.menu.getBook();
        this.setBookAccess(BookViewScreen.BookAccess.fromItem(itemStack));
    }

    private void pageChanged() {
        this.setPage(this.menu.getPage());
    }

    @Override
    public /* synthetic */ AbstractContainerMenu getMenu() {
        return this.getMenu();
    }

}

