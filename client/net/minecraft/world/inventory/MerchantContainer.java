/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class MerchantContainer
implements Container {
    private final Merchant merchant;
    private final NonNullList<ItemStack> itemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
    @Nullable
    private MerchantOffer activeOffer;
    private int selectionHint;
    private int futureXp;

    public MerchantContainer(Merchant merchant) {
        this.merchant = merchant;
    }

    @Override
    public int getContainerSize() {
        return this.itemStacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.itemStacks) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int n) {
        return this.itemStacks.get(n);
    }

    @Override
    public ItemStack removeItem(int n, int n2) {
        ItemStack itemStack = this.itemStacks.get(n);
        if (n == 2 && !itemStack.isEmpty()) {
            return ContainerHelper.removeItem(this.itemStacks, n, itemStack.getCount());
        }
        ItemStack itemStack2 = ContainerHelper.removeItem(this.itemStacks, n, n2);
        if (!itemStack2.isEmpty() && this.isPaymentSlot(n)) {
            this.updateSellItem();
        }
        return itemStack2;
    }

    private boolean isPaymentSlot(int n) {
        return n == 0 || n == 1;
    }

    @Override
    public ItemStack removeItemNoUpdate(int n) {
        return ContainerHelper.takeItem(this.itemStacks, n);
    }

    @Override
    public void setItem(int n, ItemStack itemStack) {
        this.itemStacks.set(n, itemStack);
        if (!itemStack.isEmpty() && itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
        if (this.isPaymentSlot(n)) {
            this.updateSellItem();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.merchant.getTradingPlayer() == player;
    }

    @Override
    public void setChanged() {
        this.updateSellItem();
    }

    public void updateSellItem() {
        ItemStack itemStack;
        ItemStack itemStack2;
        this.activeOffer = null;
        if (this.itemStacks.get(0).isEmpty()) {
            itemStack = this.itemStacks.get(1);
            itemStack2 = ItemStack.EMPTY;
        } else {
            itemStack = this.itemStacks.get(0);
            itemStack2 = this.itemStacks.get(1);
        }
        if (itemStack.isEmpty()) {
            this.setItem(2, ItemStack.EMPTY);
            this.futureXp = 0;
            return;
        }
        MerchantOffers merchantOffers = this.merchant.getOffers();
        if (!merchantOffers.isEmpty()) {
            MerchantOffer merchantOffer = merchantOffers.getRecipeFor(itemStack, itemStack2, this.selectionHint);
            if (merchantOffer == null || merchantOffer.isOutOfStock()) {
                this.activeOffer = merchantOffer;
                merchantOffer = merchantOffers.getRecipeFor(itemStack2, itemStack, this.selectionHint);
            }
            if (merchantOffer != null && !merchantOffer.isOutOfStock()) {
                this.activeOffer = merchantOffer;
                this.setItem(2, merchantOffer.assemble());
                this.futureXp = merchantOffer.getXp();
            } else {
                this.setItem(2, ItemStack.EMPTY);
                this.futureXp = 0;
            }
        }
        this.merchant.notifyTradeUpdated(this.getItem(2));
    }

    @Nullable
    public MerchantOffer getActiveOffer() {
        return this.activeOffer;
    }

    public void setSelectionHint(int n) {
        this.selectionHint = n;
        this.updateSellItem();
    }

    @Override
    public void clearContent() {
        this.itemStacks.clear();
    }

    public int getFutureXp() {
        return this.futureXp;
    }
}

