/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item.trading;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

public interface Merchant {
    public void setTradingPlayer(@Nullable Player var1);

    @Nullable
    public Player getTradingPlayer();

    public MerchantOffers getOffers();

    public void overrideOffers(@Nullable MerchantOffers var1);

    public void notifyTrade(MerchantOffer var1);

    public void notifyTradeUpdated(ItemStack var1);

    public Level getLevel();

    public int getVillagerXp();

    public void overrideXp(int var1);

    public boolean showProgressBar();

    public SoundEvent getNotifyTradeSound();

    default public boolean canRestock() {
        return false;
    }

    default public void openTradingScreen(Player player2, Component component, int n2) {
        MerchantOffers merchantOffers;
        OptionalInt optionalInt = player2.openMenu(new SimpleMenuProvider((n, inventory, player) -> new MerchantMenu(n, inventory, this), component));
        if (optionalInt.isPresent() && !(merchantOffers = this.getOffers()).isEmpty()) {
            player2.sendMerchantOffers(optionalInt.getAsInt(), merchantOffers, n2, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
        }
    }
}

