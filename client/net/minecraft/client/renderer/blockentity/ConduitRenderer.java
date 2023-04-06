/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.function.Function;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;

public class ConduitRenderer
extends BlockEntityRenderer<ConduitBlockEntity> {
    public static final Material SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/base"));
    public static final Material ACTIVE_SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/cage"));
    public static final Material WIND_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/wind"));
    public static final Material VERTICAL_WIND_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/wind_vertical"));
    public static final Material OPEN_EYE_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/open_eye"));
    public static final Material CLOSED_EYE_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/closed_eye"));
    private final ModelPart eye = new ModelPart(16, 16, 0, 0);
    private final ModelPart wind;
    private final ModelPart shell;
    private final ModelPart cage;

    public ConduitRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        this.eye.addBox(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 0.0f, 0.01f);
        this.wind = new ModelPart(64, 32, 0, 0);
        this.wind.addBox(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f);
        this.shell = new ModelPart(32, 16, 0, 0);
        this.shell.addBox(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f);
        this.cage = new ModelPart(32, 16, 0, 0);
        this.cage.addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
    }

    @Override
    public void render(ConduitBlockEntity conduitBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        float f2 = (float)conduitBlockEntity.tickCount + f;
        if (!conduitBlockEntity.isActive()) {
            float f3 = conduitBlockEntity.getActiveRotation(0.0f);
            VertexConsumer vertexConsumer = SHELL_TEXTURE.buffer(multiBufferSource, RenderType::entitySolid);
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(f3));
            this.shell.render(poseStack, vertexConsumer, n, n2);
            poseStack.popPose();
            return;
        }
        float f4 = conduitBlockEntity.getActiveRotation(f) * 57.295776f;
        float f5 = Mth.sin(f2 * 0.1f) / 2.0f + 0.5f;
        f5 = f5 * f5 + f5;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.3f + f5 * 0.2f, 0.5);
        Vector3f vector3f = new Vector3f(0.5f, 1.0f, 0.5f);
        vector3f.normalize();
        poseStack.mulPose(new Quaternion(vector3f, f4, true));
        this.cage.render(poseStack, ACTIVE_SHELL_TEXTURE.buffer(multiBufferSource, RenderType::entityCutoutNoCull), n, n2);
        poseStack.popPose();
        int n3 = conduitBlockEntity.tickCount / 66 % 3;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        if (n3 == 1) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0f));
        } else if (n3 == 2) {
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0f));
        }
        VertexConsumer vertexConsumer = (n3 == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).buffer(multiBufferSource, RenderType::entityCutoutNoCull);
        this.wind.render(poseStack, vertexConsumer, n, n2);
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(0.875f, 0.875f, 0.875f);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
        this.wind.render(poseStack, vertexConsumer, n, n2);
        poseStack.popPose();
        Camera camera = this.renderer.camera;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.3f + f5 * 0.2f, 0.5);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        float f6 = -camera.getYRot();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f6));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(camera.getXRot()));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
        float f7 = 1.3333334f;
        poseStack.scale(1.3333334f, 1.3333334f, 1.3333334f);
        this.eye.render(poseStack, (conduitBlockEntity.isHunting() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).buffer(multiBufferSource, RenderType::entityCutoutNoCull), n, n2);
        poseStack.popPose();
    }
}

