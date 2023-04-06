/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockEntityRenderer<T extends BlockEntity> {
    protected final BlockEntityRenderDispatcher renderer;

    public BlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        this.renderer = blockEntityRenderDispatcher;
    }

    public abstract void render(T var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6);

    public boolean shouldRenderOffScreen(T t) {
        return false;
    }
}

