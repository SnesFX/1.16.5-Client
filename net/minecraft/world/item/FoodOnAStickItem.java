/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class FoodOnAStickItem<T extends Entity>
extends Item {
    private final EntityType<T> canInteractWith;
    private final int consumeItemDamage;

    public FoodOnAStickItem(Item.Properties properties, EntityType<T> entityType, int n) {
        super(properties);
        this.canInteractWith = entityType;
        this.consumeItemDamage = n;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player2, InteractionHand interactionHand) {
        ItemSteerable itemSteerable;
        ItemStack itemStack = player2.getItemInHand(interactionHand);
        if (level.isClientSide) {
            return InteractionResultHolder.pass(itemStack);
        }
        Entity entity = player2.getVehicle();
        if (player2.isPassenger() && entity instanceof ItemSteerable && entity.getType() == this.canInteractWith && (itemSteerable = (ItemSteerable)((Object)entity)).boost()) {
            itemStack.hurtAndBreak(this.consumeItemDamage, player2, player -> player.broadcastBreakEvent(interactionHand));
            if (itemStack.isEmpty()) {
                ItemStack itemStack2 = new ItemStack(Items.FISHING_ROD);
                itemStack2.setTag(itemStack.getTag());
                return InteractionResultHolder.success(itemStack2);
            }
            return InteractionResultHolder.success(itemStack);
        }
        player2.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.pass(itemStack);
    }
}

