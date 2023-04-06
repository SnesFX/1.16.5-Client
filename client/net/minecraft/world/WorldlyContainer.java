/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface WorldlyContainer
extends Container {
    public int[] getSlotsForFace(Direction var1);

    public boolean canPlaceItemThroughFace(int var1, ItemStack var2, @Nullable Direction var3);

    public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3);
}

