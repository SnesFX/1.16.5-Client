/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemUtils {
    public static InteractionResultHolder<ItemStack> useDrink(Level level, Player player, InteractionHand interactionHand) {
        player.startUsingItem(interactionHand);
        return InteractionResultHolder.consume(player.getItemInHand(interactionHand));
    }

    public static ItemStack createFilledResult(ItemStack itemStack, Player player, ItemStack itemStack2, boolean bl) {
        boolean bl2 = player.abilities.instabuild;
        if (bl && bl2) {
            if (!player.inventory.contains(itemStack2)) {
                player.inventory.add(itemStack2);
            }
            return itemStack;
        }
        if (!bl2) {
            itemStack.shrink(1);
        }
        if (itemStack.isEmpty()) {
            return itemStack2;
        }
        if (!player.inventory.add(itemStack2)) {
            player.drop(itemStack2, false);
        }
        return itemStack;
    }

    public static ItemStack createFilledResult(ItemStack itemStack, Player player, ItemStack itemStack2) {
        return ItemUtils.createFilledResult(itemStack, player, itemStack2, true);
    }
}

