/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class FurnaceResultSlot
extends Slot {
    private final Player player;
    private int removeCount;

    public FurnaceResultSlot(Player player, Container container, int n, int n2, int n3) {
        super(container, n, n2, n3);
        this.player = player;
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
    public ItemStack onTake(Player player, ItemStack itemStack) {
        this.checkTakeAchievements(itemStack);
        super.onTake(player, itemStack);
        return itemStack;
    }

    @Override
    protected void onQuickCraft(ItemStack itemStack, int n) {
        this.removeCount += n;
        this.checkTakeAchievements(itemStack);
    }

    @Override
    protected void checkTakeAchievements(ItemStack itemStack) {
        itemStack.onCraftedBy(this.player.level, this.player, this.removeCount);
        if (!this.player.level.isClientSide && this.container instanceof AbstractFurnaceBlockEntity) {
            ((AbstractFurnaceBlockEntity)this.container).awardUsedRecipesAndPopExperience(this.player);
        }
        this.removeCount = 0;
    }
}

