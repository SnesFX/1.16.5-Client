/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

public class MerchantMenu
extends AbstractContainerMenu {
    private final Merchant trader;
    private final MerchantContainer tradeContainer;
    private int merchantLevel;
    private boolean showProgressBar;
    private boolean canRestock;

    public MerchantMenu(int n, Inventory inventory) {
        this(n, inventory, new ClientSideMerchant(inventory.player));
    }

    public MerchantMenu(int n, Inventory inventory, Merchant merchant) {
        super(MenuType.MERCHANT, n);
        int n2;
        this.trader = merchant;
        this.tradeContainer = new MerchantContainer(merchant);
        this.addSlot(new Slot(this.tradeContainer, 0, 136, 37));
        this.addSlot(new Slot(this.tradeContainer, 1, 162, 37));
        this.addSlot(new MerchantResultSlot(inventory.player, merchant, this.tradeContainer, 2, 220, 37));
        for (n2 = 0; n2 < 3; ++n2) {
            for (int i = 0; i < 9; ++i) {
                this.addSlot(new Slot(inventory, i + n2 * 9 + 9, 108 + i * 18, 84 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.addSlot(new Slot(inventory, n2, 108 + n2 * 18, 142));
        }
    }

    public void setShowProgressBar(boolean bl) {
        this.showProgressBar = bl;
    }

    @Override
    public void slotsChanged(Container container) {
        this.tradeContainer.updateSellItem();
        super.slotsChanged(container);
    }

    public void setSelectionHint(int n) {
        this.tradeContainer.setSelectionHint(n);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.trader.getTradingPlayer() == player;
    }

    public int getTraderXp() {
        return this.trader.getVillagerXp();
    }

    public int getFutureTraderXp() {
        return this.tradeContainer.getFutureXp();
    }

    public void setXp(int n) {
        this.trader.overrideXp(n);
    }

    public int getTraderLevel() {
        return this.merchantLevel;
    }

    public void setMerchantLevel(int n) {
        this.merchantLevel = n;
    }

    public void setCanRestock(boolean bl) {
        this.canRestock = bl;
    }

    public boolean canRestock() {
        return this.canRestock;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int n) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(n);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (n == 2) {
                if (!this.moveItemStackTo(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
                this.playTradeSound();
            } else if (n == 0 || n == 1 ? !this.moveItemStackTo(itemStack2, 3, 39, false) : (n >= 3 && n < 30 ? !this.moveItemStackTo(itemStack2, 30, 39, false) : n >= 30 && n < 39 && !this.moveItemStackTo(itemStack2, 3, 30, false))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack2);
        }
        return itemStack;
    }

    private void playTradeSound() {
        if (!this.trader.getLevel().isClientSide) {
            Entity entity = (Entity)((Object)this.trader);
            this.trader.getLevel().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), this.trader.getNotifyTradeSound(), SoundSource.NEUTRAL, 1.0f, 1.0f, false);
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.trader.setTradingPlayer(null);
        if (this.trader.getLevel().isClientSide) {
            return;
        }
        if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer)player).hasDisconnected()) {
            ItemStack itemStack = this.tradeContainer.removeItemNoUpdate(0);
            if (!itemStack.isEmpty()) {
                player.drop(itemStack, false);
            }
            if (!(itemStack = this.tradeContainer.removeItemNoUpdate(1)).isEmpty()) {
                player.drop(itemStack, false);
            }
        } else {
            player.inventory.placeItemBackInInventory(player.level, this.tradeContainer.removeItemNoUpdate(0));
            player.inventory.placeItemBackInInventory(player.level, this.tradeContainer.removeItemNoUpdate(1));
        }
    }

    public void tryMoveItems(int n) {
        ItemStack itemStack;
        if (this.getOffers().size() <= n) {
            return;
        }
        ItemStack itemStack2 = this.tradeContainer.getItem(0);
        if (!itemStack2.isEmpty()) {
            if (!this.moveItemStackTo(itemStack2, 3, 39, true)) {
                return;
            }
            this.tradeContainer.setItem(0, itemStack2);
        }
        if (!(itemStack = this.tradeContainer.getItem(1)).isEmpty()) {
            if (!this.moveItemStackTo(itemStack, 3, 39, true)) {
                return;
            }
            this.tradeContainer.setItem(1, itemStack);
        }
        if (this.tradeContainer.getItem(0).isEmpty() && this.tradeContainer.getItem(1).isEmpty()) {
            ItemStack itemStack3 = ((MerchantOffer)this.getOffers().get(n)).getCostA();
            this.moveFromInventoryToPaymentSlot(0, itemStack3);
            ItemStack itemStack4 = ((MerchantOffer)this.getOffers().get(n)).getCostB();
            this.moveFromInventoryToPaymentSlot(1, itemStack4);
        }
    }

    private void moveFromInventoryToPaymentSlot(int n, ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            for (int i = 3; i < 39; ++i) {
                ItemStack itemStack2 = ((Slot)this.slots.get(i)).getItem();
                if (itemStack2.isEmpty() || !this.isSameItem(itemStack, itemStack2)) continue;
                ItemStack itemStack3 = this.tradeContainer.getItem(n);
                int n2 = itemStack3.isEmpty() ? 0 : itemStack3.getCount();
                int n3 = Math.min(itemStack.getMaxStackSize() - n2, itemStack2.getCount());
                ItemStack itemStack4 = itemStack2.copy();
                int n4 = n2 + n3;
                itemStack2.shrink(n3);
                itemStack4.setCount(n4);
                this.tradeContainer.setItem(n, itemStack4);
                if (n4 >= itemStack.getMaxStackSize()) break;
            }
        }
    }

    private boolean isSameItem(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack.getItem() == itemStack2.getItem() && ItemStack.tagMatches(itemStack, itemStack2);
    }

    public void setOffers(MerchantOffers merchantOffers) {
        this.trader.overrideOffers(merchantOffers);
    }

    public MerchantOffers getOffers() {
        return this.trader.getOffers();
    }

    public boolean showProgressBar() {
        return this.showProgressBar;
    }
}

