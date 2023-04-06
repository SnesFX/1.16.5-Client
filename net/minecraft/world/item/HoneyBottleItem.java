/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class HoneyBottleItem
extends Item {
    public HoneyBottleItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        Object object;
        super.finishUsingItem(itemStack, level, livingEntity);
        if (livingEntity instanceof ServerPlayer) {
            object = (ServerPlayer)livingEntity;
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)object, itemStack);
            ((Player)object).awardStat(Stats.ITEM_USED.get(this));
        }
        if (!level.isClientSide) {
            livingEntity.removeEffect(MobEffects.POISON);
        }
        if (itemStack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        if (livingEntity instanceof Player && !((Player)livingEntity).abilities.instabuild) {
            object = new ItemStack(Items.GLASS_BOTTLE);
            Player player = (Player)livingEntity;
            if (!player.inventory.add((ItemStack)object)) {
                player.drop((ItemStack)object, false);
            }
        }
        return itemStack;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 40;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        return ItemUtils.useDrink(level, player, interactionHand);
    }
}

