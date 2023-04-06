/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;

public class BoatRenderer
extends EntityRenderer<Boat> {
    private static final ResourceLocation[] BOAT_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/boat/oak.png"), new ResourceLocation("textures/entity/boat/spruce.png"), new ResourceLocation("textures/entity/boat/birch.png"), new ResourceLocation("textures/entity/boat/jungle.png"), new ResourceLocation("textures/entity/boat/acacia.png"), new ResourceLocation("textures/entity/boat/dark_oak.png")};
    protected final BoatModel model = new BoatModel();

    public BoatRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(Boat boat, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        float f3;
        poseStack.pushPose();
        poseStack.translate(0.0, 0.375, 0.0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f - f));
        float f4 = (float)boat.getHurtTime() - f2;
        float f5 = boat.getDamage() - f2;
        if (f5 < 0.0f) {
            f5 = 0.0f;
        }
        if (f4 > 0.0f) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(f4) * f4 * f5 / 10.0f * (float)boat.getHurtDir()));
        }
        if (!Mth.equal(f3 = boat.getBubbleAngle(f2), 0.0f)) {
            poseStack.mulPose(new Quaternion(new Vector3f(1.0f, 0.0f, 1.0f), boat.getBubbleAngle(f2), true));
        }
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0f));
        this.model.setupAnim(boat, f2, 0.0f, -0.1f, 0.0f, 0.0f);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(this.model.renderType(this.getTextureLocation(boat)));
        this.model.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        if (!boat.isUnderWater()) {
            VertexConsumer vertexConsumer2 = multiBufferSource.getBuffer(RenderType.waterMask());
            this.model.waterPatch().render(poseStack, vertexConsumer2, n, OverlayTexture.NO_OVERLAY);
        }
        poseStack.popPose();
        super.render(boat, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(Boat boat) {
        return BOAT_TEXTURE_LOCATIONS[boat.getBoatType().ordinal()];
    }
}

