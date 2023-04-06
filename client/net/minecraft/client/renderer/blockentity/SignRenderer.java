/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WoodType;

public class SignRenderer
extends BlockEntityRenderer<SignBlockEntity> {
    private final SignModel signModel = new SignModel();

    public SignRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(SignBlockEntity signBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        float f2;
        BlockState blockState = signBlockEntity.getBlockState();
        poseStack.pushPose();
        float f3 = 0.6666667f;
        if (blockState.getBlock() instanceof StandingSignBlock) {
            poseStack.translate(0.5, 0.5, 0.5);
            f2 = -((float)(blockState.getValue(StandingSignBlock.ROTATION) * 360) / 16.0f);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(f2));
            this.signModel.stick.visible = true;
        } else {
            poseStack.translate(0.5, 0.5, 0.5);
            f2 = -blockState.getValue(WallSignBlock.FACING).toYRot();
            poseStack.mulPose(Vector3f.YP.rotationDegrees(f2));
            poseStack.translate(0.0, -0.3125, -0.4375);
            this.signModel.stick.visible = false;
        }
        poseStack.pushPose();
        poseStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
        Material material = SignRenderer.getMaterial(blockState.getBlock());
        VertexConsumer vertexConsumer = material.buffer(multiBufferSource, this.signModel::renderType);
        this.signModel.sign.render(poseStack, vertexConsumer, n, n2);
        this.signModel.stick.render(poseStack, vertexConsumer, n, n2);
        poseStack.popPose();
        Font font = this.renderer.getFont();
        float f4 = 0.010416667f;
        poseStack.translate(0.0, 0.3333333432674408, 0.046666666865348816);
        poseStack.scale(0.010416667f, -0.010416667f, 0.010416667f);
        int n3 = signBlockEntity.getColor().getTextColor();
        double d = 0.4;
        int n4 = (int)((double)NativeImage.getR(n3) * 0.4);
        int n5 = (int)((double)NativeImage.getG(n3) * 0.4);
        int n6 = (int)((double)NativeImage.getB(n3) * 0.4);
        int n7 = NativeImage.combine(0, n6, n5, n4);
        int n8 = 20;
        for (int i = 0; i < 4; ++i) {
            FormattedCharSequence formattedCharSequence = signBlockEntity.getRenderMessage(i, component -> {
                List<FormattedCharSequence> list = font.split((FormattedText)component, 90);
                return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
            });
            if (formattedCharSequence == null) continue;
            float f5 = -font.width(formattedCharSequence) / 2;
            font.drawInBatch(formattedCharSequence, f5, (float)(i * 10 - 20), n7, false, poseStack.last().pose(), multiBufferSource, false, 0, n);
        }
        poseStack.popPose();
    }

    public static Material getMaterial(Block block) {
        WoodType woodType = block instanceof SignBlock ? ((SignBlock)block).type() : WoodType.OAK;
        return Sheets.signTexture(woodType);
    }

    public static final class SignModel
    extends Model {
        public final ModelPart sign = new ModelPart(64, 32, 0, 0);
        public final ModelPart stick;

        public SignModel() {
            super(RenderType::entityCutoutNoCull);
            this.sign.addBox(-12.0f, -14.0f, -1.0f, 24.0f, 12.0f, 2.0f, 0.0f);
            this.stick = new ModelPart(64, 32, 0, 14);
            this.stick.addBox(-1.0f, -2.0f, -1.0f, 2.0f, 14.0f, 2.0f, 0.0f);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
            this.sign.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
            this.stick.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
        }
    }

}

