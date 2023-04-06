/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;

public class MerchantResultSlot
extends Slot {
    private final MerchantContainer slots;
    private final Player player;
    private int removeCount;
    private final Merchant merchant;

    public MerchantResultSlot(Player player, Merchant merchant, MerchantContainer merchantContainer, int n, int n2, int n3) {
        super(merchantContainer, n, n2, n3);
        this.player = player;
        this.merchant = merchant;
        this.slots = merchantContainer;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }

    @Override
    public ItemStack remove(int n) {
        if (this.hasItem()) {
            this.removeCount += Math.min(n, this.getItem().getCount());
        }
        return super.remove(n);
    }

    @Override
    protected void onQuickCraft(ItemStack itemStack, int n) {
        this.removeCount += n;
        this.checkTakeAchievements(itemStack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack itemStack) {
        itemStack.onCraftedBy(this.player.level, this.player, this.removeCount);
        this.removeCount = 0;
    }

    @Override
    public ItemStack onTake(Player player, ItemStack itemStack) {
        this.checkTakeAchievements(itemStack);
        MerchantOffer merchantOffer = this.slots.getActiveOffer();
        if (merchantOffer != null) {
            ItemStack itemStack2;
            ItemStack itemStack3 = this.slots.getItem(0);
            if (merchantOffer.take(itemStack3, itemStack2 = this.slots.getItem(1)) || merchantOffer.take(itemStack2, itemStack3)) {
                this.merchant.notifyTrade(merchantOffer);
                player.awardStat(Stats.TRADED_WITH_VILLAGER);
                this.slots.setItem(0, itemStack3);
                this.slots.setItem(1, itemStack2);
            }
            this.merchant.overrideXp(this.merchant.getVillagerXp() + merchantOffer.getXp());
        }
        return itemStack;
    }
}

