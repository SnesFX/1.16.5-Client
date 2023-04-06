/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.tutorial;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public interface TutorialStepInstance {
    default public void clear() {
    }

    default public void tick() {
    }

    default public void onInput(Input input) {
    }

    default public void onMouse(double d, double d2) {
    }

    default public void onLookAt(ClientLevel clientLevel, HitResult hitResult) {
    }

    default public void onDestroyBlock(ClientLevel clientLevel, BlockPos blockPos, BlockState blockState, float f) {
    }

    default public void onOpenInventory() {
    }

    default public void onGetItem(ItemStack itemStack) {
    }
}

