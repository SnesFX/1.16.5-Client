/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface DyeableLeatherItem {
    default public boolean hasCustomColor(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTagElement("display");
        return compoundTag != null && compoundTag.contains("color", 99);
    }

    default public int getColor(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTagElement("display");
        if (compoundTag != null && compoundTag.contains("color", 99)) {
            return compoundTag.getInt("color");
        }
        return 10511680;
    }

    default public void clearColor(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTagElement("display");
        if (compoundTag != null && compoundTag.contains("color")) {
            compoundTag.remove("color");
        }
    }

    default public void setColor(ItemStack itemStack, int n) {
        itemStack.getOrCreateTagElement("display").putInt("color", n);
    }

    public static ItemStack dyeArmor(ItemStack itemStack, List<DyeItem> list) {
        int n;
        float f;
        ItemStack itemStack2 = ItemStack.EMPTY;
        int[] arrn = new int[3];
        int n2 = 0;
        int n3 = 0;
        DyeableLeatherItem dyeableLeatherItem = null;
        Item item = itemStack.getItem();
        if (item instanceof DyeableLeatherItem) {
            dyeableLeatherItem = (DyeableLeatherItem)((Object)item);
            itemStack2 = itemStack.copy();
            itemStack2.setCount(1);
            if (dyeableLeatherItem.hasCustomColor(itemStack)) {
                int n4 = dyeableLeatherItem.getColor(itemStack2);
                float f2 = (float)(n4 >> 16 & 0xFF) / 255.0f;
                float f3 = (float)(n4 >> 8 & 0xFF) / 255.0f;
                f = (float)(n4 & 0xFF) / 255.0f;
                n2 = (int)((float)n2 + Math.max(f2, Math.max(f3, f)) * 255.0f);
                int[] arrn2 = arrn;
                arrn2[0] = (int)((float)arrn2[0] + f2 * 255.0f);
                int[] arrn3 = arrn;
                arrn3[1] = (int)((float)arrn3[1] + f3 * 255.0f);
                int[] arrn4 = arrn;
                arrn4[2] = (int)((float)arrn4[2] + f * 255.0f);
                ++n3;
            }
            for (DyeItem dyeItem : list) {
                float[] arrf = dyeItem.getDyeColor().getTextureDiffuseColors();
                int n5 = (int)(arrf[0] * 255.0f);
                int n6 = (int)(arrf[1] * 255.0f);
                n = (int)(arrf[2] * 255.0f);
                n2 += Math.max(n5, Math.max(n6, n));
                int[] arrn5 = arrn;
                arrn5[0] = arrn5[0] + n5;
                int[] arrn6 = arrn;
                arrn6[1] = arrn6[1] + n6;
                int[] arrn7 = arrn;
                arrn7[2] = arrn7[2] + n;
                ++n3;
            }
        }
        if (dyeableLeatherItem == null) {
            return ItemStack.EMPTY;
        }
        int n7 = arrn[0] / n3;
        int n8 = arrn[1] / n3;
        int n9 = arrn[2] / n3;
        f = (float)n2 / (float)n3;
        float f4 = Math.max(n7, Math.max(n8, n9));
        n7 = (int)((float)n7 * f / f4);
        n8 = (int)((float)n8 * f / f4);
        n9 = (int)((float)n9 * f / f4);
        n = n7;
        n = (n << 8) + n8;
        n = (n << 8) + n9;
        dyeableLeatherItem.setColor(itemStack2, n);
        return itemStack2;
    }
}

