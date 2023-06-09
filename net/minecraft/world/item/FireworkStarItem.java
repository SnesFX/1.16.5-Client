/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class FireworkStarItem
extends Item {
    public FireworkStarItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        CompoundTag compoundTag = itemStack.getTagElement("Explosion");
        if (compoundTag != null) {
            FireworkStarItem.appendHoverText(compoundTag, list);
        }
    }

    public static void appendHoverText(CompoundTag compoundTag, List<Component> list) {
        int[] arrn;
        FireworkRocketItem.Shape shape = FireworkRocketItem.Shape.byId(compoundTag.getByte("Type"));
        list.add(new TranslatableComponent("item.minecraft.firework_star.shape." + shape.getName()).withStyle(ChatFormatting.GRAY));
        int[] arrn2 = compoundTag.getIntArray("Colors");
        if (arrn2.length > 0) {
            list.add(FireworkStarItem.appendColors(new TextComponent("").withStyle(ChatFormatting.GRAY), arrn2));
        }
        if ((arrn = compoundTag.getIntArray("FadeColors")).length > 0) {
            list.add(FireworkStarItem.appendColors(new TranslatableComponent("item.minecraft.firework_star.fade_to").append(" ").withStyle(ChatFormatting.GRAY), arrn));
        }
        if (compoundTag.getBoolean("Trail")) {
            list.add(new TranslatableComponent("item.minecraft.firework_star.trail").withStyle(ChatFormatting.GRAY));
        }
        if (compoundTag.getBoolean("Flicker")) {
            list.add(new TranslatableComponent("item.minecraft.firework_star.flicker").withStyle(ChatFormatting.GRAY));
        }
    }

    private static Component appendColors(MutableComponent mutableComponent, int[] arrn) {
        for (int i = 0; i < arrn.length; ++i) {
            if (i > 0) {
                mutableComponent.append(", ");
            }
            mutableComponent.append(FireworkStarItem.getColorName(arrn[i]));
        }
        return mutableComponent;
    }

    private static Component getColorName(int n) {
        DyeColor dyeColor = DyeColor.byFireworkColor(n);
        if (dyeColor == null) {
            return new TranslatableComponent("item.minecraft.firework_star.custom_color");
        }
        return new TranslatableComponent("item.minecraft.firework_star." + dyeColor.getName());
    }
}

