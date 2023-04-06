/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class CampfireRenderer
extends BlockEntityRenderer<CampfireBlockEntity> {
    public CampfireRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(CampfireBlockEntity campfireBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        Direction direction = campfireBlockEntity.getBlockState().getValue(CampfireBlock.FACING);
        NonNullList<ItemStack> nonNullList = campfireBlockEntity.getItems();
        for (int i = 0; i < nonNullList.size(); ++i) {
            ItemStack itemStack = nonNullList.get(i);
            if (itemStack == ItemStack.EMPTY) continue;
            poseStack.pushPose();
            poseStack.translate(0.5, 0.44921875, 0.5);
            Direction direction2 = Direction.from2DDataValue((i + direction.get2DDataValue()) % 4);
            float f2 = -direction2.toYRot();
            poseStack.mulPose(Vector3f.YP.rotationDegrees(f2));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0f));
            poseStack.translate(-0.3125, -0.3125, 0.0);
            poseStack.scale(0.375f, 0.375f, 0.375f);
            Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.FIXED, n, n2, poseStack, multiBufferSource);
            poseStack.popPose();
        }
    }
}

