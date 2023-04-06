/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class SuspiciousStewItem
extends Item {
    public SuspiciousStewItem(Item.Properties properties) {
        super(properties);
    }

    public static void saveMobEffect(ItemStack itemStack, MobEffect mobEffect, int n) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        ListTag listTag = compoundTag.getList("Effects", 9);
        CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.putByte("EffectId", (byte)MobEffect.getId(mobEffect));
        compoundTag2.putInt("EffectDuration", n);
        listTag.add(compoundTag2);
        compoundTag.put("Effects", listTag);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        ItemStack itemStack2 = super.finishUsingItem(itemStack, level, livingEntity);
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null && compoundTag.contains("Effects", 9)) {
            ListTag listTag = compoundTag.getList("Effects", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                MobEffect mobEffect;
                int n = 160;
                CompoundTag compoundTag2 = listTag.getCompound(i);
                if (compoundTag2.contains("EffectDuration", 3)) {
                    n = compoundTag2.getInt("EffectDuration");
                }
                if ((mobEffect = MobEffect.byId(compoundTag2.getByte("EffectId"))) == null) continue;
                livingEntity.addEffect(new MobEffectInstance(mobEffect, n));
            }
        }
        if (livingEntity instanceof Player && ((Player)livingEntity).abilities.instabuild) {
            return itemStack2;
        }
        return new ItemStack(Items.BOWL);
    }
}

