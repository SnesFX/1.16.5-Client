/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LecternMenu
extends AbstractContainerMenu {
    private final Container lectern;
    private final ContainerData lecternData;

    public LecternMenu(int n) {
        this(n, new SimpleContainer(1), new SimpleContainerData(1));
    }

    public LecternMenu(int n, Container container, ContainerData containerData) {
        super(MenuType.LECTERN, n);
        LecternMenu.checkContainerSize(container, 1);
        LecternMenu.checkContainerDataCount(containerData, 1);
        this.lectern = container;
        this.lecternData = containerData;
        this.addSlot(new Slot(container, 0, 0, 0){

            @Override
            public void setChanged() {
                super.setChanged();
                LecternMenu.this.slotsChanged(this.container);
            }
        });
        this.addDataSlots(containerData);
    }

    @Override
    public boolean clickMenuButton(Player player, int n) {
        if (n >= 100) {
            int n2 = n - 100;
            this.setData(0, n2);
            return true;
        }
        switch (n) {
            case 2: {
                int n3 = this.lecternData.get(0);
                this.setData(0, n3 + 1);
                return true;
            }
            case 1: {
                int n4 = this.lecternData.get(0);
                this.setData(0, n4 - 1);
                return true;
            }
            case 3: {
                if (!player.mayBuild()) {
                    return false;
                }
                ItemStack itemStack = this.lectern.removeItemNoUpdate(0);
                this.lectern.setChanged();
                if (!player.inventory.add(itemStack)) {
                    player.drop(itemStack, false);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setData(int n, int n2) {
        super.setData(n, n2);
        this.broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.lectern.stillValid(player);
    }

    public ItemStack getBook() {
        return this.lectern.getItem(0);
    }

    public int getPage() {
        return this.lecternData.get(0);
    }

}

