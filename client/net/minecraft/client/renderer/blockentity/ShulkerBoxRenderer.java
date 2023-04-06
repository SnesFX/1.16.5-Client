/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class ShulkerBoxRenderer
extends BlockEntityRenderer<ShulkerBoxBlockEntity> {
    private final ShulkerModel<?> model;

    public ShulkerBoxRenderer(ShulkerModel<?> shulkerModel, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        this.model = shulkerModel;
    }

    @Override
    public void render(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        DyeColor dyeColor;
        Object object;
        Direction direction = Direction.UP;
        if (shulkerBoxBlockEntity.hasLevel() && ((BlockBehaviour.BlockStateBase)(object = shulkerBoxBlockEntity.getLevel().getBlockState(shulkerBoxBlockEntity.getBlockPos()))).getBlock() instanceof ShulkerBoxBlock) {
            direction = ((StateHolder)object).getValue(ShulkerBoxBlock.FACING);
        }
        object = (dyeColor = shulkerBoxBlockEntity.getColor()) == null ? Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION : Sheets.SHULKER_TEXTURE_LOCATION.get(dyeColor.getId());
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        float f2 = 0.9995f;
        poseStack.scale(0.9995f, 0.9995f, 0.9995f);
        poseStack.mulPose(direction.getRotation());
        poseStack.scale(1.0f, -1.0f, -1.0f);
        poseStack.translate(0.0, -1.0, 0.0);
        VertexConsumer vertexConsumer = ((Material)object).buffer(multiBufferSource, RenderType::entityCutoutNoCull);
        this.model.getBase().render(poseStack, vertexConsumer, n, n2);
        poseStack.translate(0.0, -shulkerBoxBlockEntity.getProgress(f) * 0.5f, 0.0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(270.0f * shulkerBoxBlockEntity.getProgress(f)));
        this.model.getLid().render(poseStack, vertexConsumer, n, n2);
        poseStack.popPose();
    }
}

