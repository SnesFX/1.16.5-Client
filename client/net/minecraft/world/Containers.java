/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world;

import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Containers {
    private static final Random RANDOM = new Random();

    public static void dropContents(Level level, BlockPos blockPos, Container container) {
        Containers.dropContents(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), container);
    }

    public static void dropContents(Level level, Entity entity, Container container) {
        Containers.dropContents(level, entity.getX(), entity.getY(), entity.getZ(), container);
    }

    private static void dropContents(Level level, double d, double d2, double d3, Container container) {
        for (int i = 0; i < container.getContainerSize(); ++i) {
            Containers.dropItemStack(level, d, d2, d3, container.getItem(i));
        }
    }

    public static void dropContents(Level level, BlockPos blockPos, NonNullList<ItemStack> nonNullList) {
        nonNullList.forEach(itemStack -> Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack));
    }

    public static void dropItemStack(Level level, double d, double d2, double d3, ItemStack itemStack) {
        double d4 = EntityType.ITEM.getWidth();
        double d5 = 1.0 - d4;
        double d6 = d4 / 2.0;
        double d7 = Math.floor(d) + RANDOM.nextDouble() * d5 + d6;
        double d8 = Math.floor(d2) + RANDOM.nextDouble() * d5;
        double d9 = Math.floor(d3) + RANDOM.nextDouble() * d5 + d6;
        while (!itemStack.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(level, d7, d8, d9, itemStack.split(RANDOM.nextInt(21) + 10));
            float f = 0.05f;
            itemEntity.setDeltaMovement(RANDOM.nextGaussian() * 0.05000000074505806, RANDOM.nextGaussian() * 0.05000000074505806 + 0.20000000298023224, RANDOM.nextGaussian() * 0.05000000074505806);
            level.addFreshEntity(itemEntity);
        }
    }
}

