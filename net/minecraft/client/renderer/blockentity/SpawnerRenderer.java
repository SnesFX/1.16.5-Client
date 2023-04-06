/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public class SpawnerRenderer
extends BlockEntityRenderer<SpawnerBlockEntity> {
    public SpawnerRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(SpawnerBlockEntity spawnerBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        BaseSpawner baseSpawner = spawnerBlockEntity.getSpawner();
        Entity entity = baseSpawner.getOrCreateDisplayEntity();
        if (entity != null) {
            float f2 = 0.53125f;
            float f3 = Math.max(entity.getBbWidth(), entity.getBbHeight());
            if ((double)f3 > 1.0) {
                f2 /= f3;
            }
            poseStack.translate(0.0, 0.4000000059604645, 0.0);
            poseStack.mulPose(Vector3f.YP.rotationDegrees((float)Mth.lerp((double)f, baseSpawner.getoSpin(), baseSpawner.getSpin()) * 10.0f));
            poseStack.translate(0.0, -0.20000000298023224, 0.0);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-30.0f));
            poseStack.scale(f2, f2, f2);
            Minecraft.getInstance().getEntityRenderDispatcher().render(entity, 0.0, 0.0, 0.0, 0.0f, f, poseStack, multiBufferSource, n);
        }
        poseStack.popPose();
    }
}

