/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public class EnderDragonRenderer
extends EntityRenderer<EnderDragon> {
    public static final ResourceLocation CRYSTAL_BEAM_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal_beam.png");
    private static final ResourceLocation DRAGON_EXPLODING_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
    private static final ResourceLocation DRAGON_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon.png");
    private static final ResourceLocation DRAGON_EYES_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(DRAGON_LOCATION);
    private static final RenderType DECAL = RenderType.entityDecal(DRAGON_LOCATION);
    private static final RenderType EYES = RenderType.eyes(DRAGON_EYES_LOCATION);
    private static final RenderType BEAM = RenderType.entitySmoothCutout(CRYSTAL_BEAM_LOCATION);
    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
    private final DragonModel model = new DragonModel();

    public EnderDragonRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(EnderDragon enderDragon, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        poseStack.pushPose();
        float f3 = (float)enderDragon.getLatencyPos(7, f2)[0];
        float f4 = (float)(enderDragon.getLatencyPos(5, f2)[1] - enderDragon.getLatencyPos(10, f2)[1]);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-f3));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(f4 * 10.0f));
        poseStack.translate(0.0, 0.0, 1.0);
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.translate(0.0, -1.5010000467300415, 0.0);
        boolean bl = enderDragon.hurtTime > 0;
        this.model.prepareMobModel(enderDragon, 0.0f, 0.0f, f2);
        if (enderDragon.dragonDeathTime > 0) {
            float f5 = (float)enderDragon.dragonDeathTime / 200.0f;
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.dragonExplosionAlpha(DRAGON_EXPLODING_LOCATION, f5));
            this.model.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
            VertexConsumer vertexConsumer2 = multiBufferSource.getBuffer(DECAL);
            this.model.renderToBuffer(poseStack, vertexConsumer2, n, OverlayTexture.pack(0.0f, bl), 1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RENDER_TYPE);
            this.model.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.pack(0.0f, bl), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(EYES);
        this.model.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        if (enderDragon.dragonDeathTime > 0) {
            float f6 = ((float)enderDragon.dragonDeathTime + f2) / 200.0f;
            float f7 = Math.min(f6 > 0.8f ? (f6 - 0.8f) / 0.2f : 0.0f, 1.0f);
            Random random = new Random(432L);
            VertexConsumer vertexConsumer3 = multiBufferSource.getBuffer(RenderType.lightning());
            poseStack.pushPose();
            poseStack.translate(0.0, -1.0, -2.0);
            int n2 = 0;
            while ((float)n2 < (f6 + f6 * f6) / 2.0f * 60.0f) {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0f));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0f));
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0f));
                poseStack.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0f));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0f));
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0f + f6 * 90.0f));
                float f8 = random.nextFloat() * 20.0f + 5.0f + f7 * 10.0f;
                float f9 = random.nextFloat() * 2.0f + 1.0f + f7 * 2.0f;
                Matrix4f matrix4f = poseStack.last().pose();
                int n3 = (int)(255.0f * (1.0f - f7));
                EnderDragonRenderer.vertex01(vertexConsumer3, matrix4f, n3);
                EnderDragonRenderer.vertex2(vertexConsumer3, matrix4f, f8, f9);
                EnderDragonRenderer.vertex3(vertexConsumer3, matrix4f, f8, f9);
                EnderDragonRenderer.vertex01(vertexConsumer3, matrix4f, n3);
                EnderDragonRenderer.vertex3(vertexConsumer3, matrix4f, f8, f9);
                EnderDragonRenderer.vertex4(vertexConsumer3, matrix4f, f8, f9);
                EnderDragonRenderer.vertex01(vertexConsumer3, matrix4f, n3);
                EnderDragonRenderer.vertex4(vertexConsumer3, matrix4f, f8, f9);
                EnderDragonRenderer.vertex2(vertexConsumer3, matrix4f, f8, f9);
                ++n2;
            }
            poseStack.popPose();
        }
        poseStack.popPose();
        if (enderDragon.nearestCrystal != null) {
            poseStack.pushPose();
            float f10 = (float)(enderDragon.nearestCrystal.getX() - Mth.lerp((double)f2, enderDragon.xo, enderDragon.getX()));
            float f11 = (float)(enderDragon.nearestCrystal.getY() - Mth.lerp((double)f2, enderDragon.yo, enderDragon.getY()));
            float f12 = (float)(enderDragon.nearestCrystal.getZ() - Mth.lerp((double)f2, enderDragon.zo, enderDragon.getZ()));
            EnderDragonRenderer.renderCrystalBeams(f10, f11 + EndCrystalRenderer.getY(enderDragon.nearestCrystal, f2), f12, f2, enderDragon.tickCount, poseStack, multiBufferSource, n);
            poseStack.popPose();
        }
        super.render(enderDragon, f, f2, poseStack, multiBufferSource, n);
    }

    private static void vertex01(VertexConsumer vertexConsumer, Matrix4f matrix4f, int n) {
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(255, 255, 255, n).endVertex();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(255, 255, 255, n).endVertex();
    }

    private static void vertex2(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float f2) {
        vertexConsumer.vertex(matrix4f, -HALF_SQRT_3 * f2, f, -0.5f * f2).color(255, 0, 255, 0).endVertex();
    }

    private static void vertex3(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float f2) {
        vertexConsumer.vertex(matrix4f, HALF_SQRT_3 * f2, f, -0.5f * f2).color(255, 0, 255, 0).endVertex();
    }

    private static void vertex4(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float f2) {
        vertexConsumer.vertex(matrix4f, 0.0f, f, 1.0f * f2).color(255, 0, 255, 0).endVertex();
    }

    public static void renderCrystalBeams(float f, float f2, float f3, float f4, int n, PoseStack poseStack, MultiBufferSource multiBufferSource, int n2) {
        float f5 = Mth.sqrt(f * f + f3 * f3);
        float f6 = Mth.sqrt(f * f + f2 * f2 + f3 * f3);
        poseStack.pushPose();
        poseStack.translate(0.0, 2.0, 0.0);
        poseStack.mulPose(Vector3f.YP.rotation((float)(-Math.atan2(f3, f)) - 1.5707964f));
        poseStack.mulPose(Vector3f.XP.rotation((float)(-Math.atan2(f5, f2)) - 1.5707964f));
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(BEAM);
        float f7 = 0.0f - ((float)n + f4) * 0.01f;
        float f8 = Mth.sqrt(f * f + f2 * f2 + f3 * f3) / 32.0f - ((float)n + f4) * 0.01f;
        int n3 = 8;
        float f9 = 0.0f;
        float f10 = 0.75f;
        float f11 = 0.0f;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        for (int i = 1; i <= 8; ++i) {
            float f12 = Mth.sin((float)i * 6.2831855f / 8.0f) * 0.75f;
            float f13 = Mth.cos((float)i * 6.2831855f / 8.0f) * 0.75f;
            float f14 = (float)i / 8.0f;
            vertexConsumer.vertex(matrix4f, f9 * 0.2f, f10 * 0.2f, 0.0f).color(0, 0, 0, 255).uv(f11, f7).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2).normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, f9, f10, f6).color(255, 255, 255, 255).uv(f11, f8).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2).normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, f12, f13, f6).color(255, 255, 255, 255).uv(f14, f8).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2).normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, f12 * 0.2f, f13 * 0.2f, 0.0f).color(0, 0, 0, 255).uv(f14, f7).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2).normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            f9 = f12;
            f10 = f13;
            f11 = f14;
        }
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EnderDragon enderDragon) {
        return DRAGON_LOCATION;
    }

    public static class DragonModel
    extends EntityModel<EnderDragon> {
        private final ModelPart head;
        private final ModelPart neck;
        private final ModelPart jaw;
        private final ModelPart body;
        private ModelPart leftWing;
        private ModelPart leftWingTip;
        private ModelPart leftFrontLeg;
        private ModelPart leftFrontLegTip;
        private ModelPart leftFrontFoot;
        private ModelPart leftRearLeg;
        private ModelPart leftRearLegTip;
        private ModelPart leftRearFoot;
        private ModelPart rightWing;
        private ModelPart rightWingTip;
        private ModelPart rightFrontLeg;
        private ModelPart rightFrontLegTip;
        private ModelPart rightFrontFoot;
        private ModelPart rightRearLeg;
        private ModelPart rightRearLegTip;
        private ModelPart rightRearFoot;
        @Nullable
        private EnderDragon entity;
        private float a;

        public DragonModel() {
            this.texWidth = 256;
            this.texHeight = 256;
            float f = -16.0f;
            this.head = new ModelPart(this);
            this.head.addBox("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, 0.0f, 176, 44);
            this.head.addBox("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, 0.0f, 112, 30);
            this.head.mirror = true;
            this.head.addBox("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, 0.0f, 0, 0);
            this.head.addBox("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, 0.0f, 112, 0);
            this.head.mirror = false;
            this.head.addBox("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, 0.0f, 0, 0);
            this.head.addBox("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, 0.0f, 112, 0);
            this.jaw = new ModelPart(this);
            this.jaw.setPos(0.0f, 4.0f, -8.0f);
            this.jaw.addBox("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, 0.0f, 176, 65);
            this.head.addChild(this.jaw);
            this.neck = new ModelPart(this);
            this.neck.addBox("box", -5.0f, -5.0f, -5.0f, 10, 10, 10, 0.0f, 192, 104);
            this.neck.addBox("scale", -1.0f, -9.0f, -3.0f, 2, 4, 6, 0.0f, 48, 0);
            this.body = new ModelPart(this);
            this.body.setPos(0.0f, 4.0f, 8.0f);
            this.body.addBox("body", -12.0f, 0.0f, -16.0f, 24, 24, 64, 0.0f, 0, 0);
            this.body.addBox("scale", -1.0f, -6.0f, -10.0f, 2, 6, 12, 0.0f, 220, 53);
            this.body.addBox("scale", -1.0f, -6.0f, 10.0f, 2, 6, 12, 0.0f, 220, 53);
            this.body.addBox("scale", -1.0f, -6.0f, 30.0f, 2, 6, 12, 0.0f, 220, 53);
            this.leftWing = new ModelPart(this);
            this.leftWing.mirror = true;
            this.leftWing.setPos(12.0f, 5.0f, 2.0f);
            this.leftWing.addBox("bone", 0.0f, -4.0f, -4.0f, 56, 8, 8, 0.0f, 112, 88);
            this.leftWing.addBox("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 88);
            this.leftWingTip = new ModelPart(this);
            this.leftWingTip.mirror = true;
            this.leftWingTip.setPos(56.0f, 0.0f, 0.0f);
            this.leftWingTip.addBox("bone", 0.0f, -2.0f, -2.0f, 56, 4, 4, 0.0f, 112, 136);
            this.leftWingTip.addBox("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 144);
            this.leftWing.addChild(this.leftWingTip);
            this.leftFrontLeg = new ModelPart(this);
            this.leftFrontLeg.setPos(12.0f, 20.0f, 2.0f);
            this.leftFrontLeg.addBox("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 0.0f, 112, 104);
            this.leftFrontLegTip = new ModelPart(this);
            this.leftFrontLegTip.setPos(0.0f, 20.0f, -1.0f);
            this.leftFrontLegTip.addBox("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 0.0f, 226, 138);
            this.leftFrontLeg.addChild(this.leftFrontLegTip);
            this.leftFrontFoot = new ModelPart(this);
            this.leftFrontFoot.setPos(0.0f, 23.0f, 0.0f);
            this.leftFrontFoot.addBox("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 0.0f, 144, 104);
            this.leftFrontLegTip.addChild(this.leftFrontFoot);
            this.leftRearLeg = new ModelPart(this);
            this.leftRearLeg.setPos(16.0f, 16.0f, 42.0f);
            this.leftRearLeg.addBox("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0.0f, 0, 0);
            this.leftRearLegTip = new ModelPart(this);
            this.leftRearLegTip.setPos(0.0f, 32.0f, -4.0f);
            this.leftRearLegTip.addBox("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 0.0f, 196, 0);
            this.leftRearLeg.addChild(this.leftRearLegTip);
            this.leftRearFoot = new ModelPart(this);
            this.leftRearFoot.setPos(0.0f, 31.0f, 4.0f);
            this.leftRearFoot.addBox("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 0.0f, 112, 0);
            this.leftRearLegTip.addChild(this.leftRearFoot);
            this.rightWing = new ModelPart(this);
            this.rightWing.setPos(-12.0f, 5.0f, 2.0f);
            this.rightWing.addBox("bone", -56.0f, -4.0f, -4.0f, 56, 8, 8, 0.0f, 112, 88);
            this.rightWing.addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 88);
            this.rightWingTip = new ModelPart(this);
            this.rightWingTip.setPos(-56.0f, 0.0f, 0.0f);
            this.rightWingTip.addBox("bone", -56.0f, -2.0f, -2.0f, 56, 4, 4, 0.0f, 112, 136);
            this.rightWingTip.addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 144);
            this.rightWing.addChild(this.rightWingTip);
            this.rightFrontLeg = new ModelPart(this);
            this.rightFrontLeg.setPos(-12.0f, 20.0f, 2.0f);
            this.rightFrontLeg.addBox("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 0.0f, 112, 104);
            this.rightFrontLegTip = new ModelPart(this);
            this.rightFrontLegTip.setPos(0.0f, 20.0f, -1.0f);
            this.rightFrontLegTip.addBox("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 0.0f, 226, 138);
            this.rightFrontLeg.addChild(this.rightFrontLegTip);
            this.rightFrontFoot = new ModelPart(this);
            this.rightFrontFoot.setPos(0.0f, 23.0f, 0.0f);
            this.rightFrontFoot.addBox("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 0.0f, 144, 104);
            this.rightFrontLegTip.addChild(this.rightFrontFoot);
            this.rightRearLeg = new ModelPart(this);
            this.rightRearLeg.setPos(-16.0f, 16.0f, 42.0f);
            this.rightRearLeg.addBox("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0.0f, 0, 0);
            this.rightRearLegTip = new ModelPart(this);
            this.rightRearLegTip.setPos(0.0f, 32.0f, -4.0f);
            this.rightRearLegTip.addBox("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 0.0f, 196, 0);
            this.rightRearLeg.addChild(this.rightRearLegTip);
            this.rightRearFoot = new ModelPart(this);
            this.rightRearFoot.setPos(0.0f, 31.0f, 4.0f);
            this.rightRearFoot.addBox("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 0.0f, 112, 0);
            this.rightRearLegTip.addChild(this.rightRearFoot);
        }

        @Override
        public void prepareMobModel(EnderDragon enderDragon, float f, float f2, float f3) {
            this.entity = enderDragon;
            this.a = f3;
        }

        @Override
        public void setupAnim(EnderDragon enderDragon, float f, float f2, float f3, float f4, float f5) {
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
            float f5;
            poseStack.pushPose();
            float f6 = Mth.lerp(this.a, this.entity.oFlapTime, this.entity.flapTime);
            this.jaw.xRot = (float)(Math.sin(f6 * 6.2831855f) + 1.0) * 0.2f;
            float f7 = (float)(Math.sin(f6 * 6.2831855f - 1.0f) + 1.0);
            f7 = (f7 * f7 + f7 * 2.0f) * 0.05f;
            poseStack.translate(0.0, f7 - 2.0f, -3.0);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(f7 * 2.0f));
            float f8 = 0.0f;
            float f9 = 20.0f;
            float f10 = -12.0f;
            float f11 = 1.5f;
            double[] arrd = this.entity.getLatencyPos(6, this.a);
            float f12 = Mth.rotWrap(this.entity.getLatencyPos(5, this.a)[0] - this.entity.getLatencyPos(10, this.a)[0]);
            float f13 = Mth.rotWrap(this.entity.getLatencyPos(5, this.a)[0] + (double)(f12 / 2.0f));
            float f14 = f6 * 6.2831855f;
            for (int i = 0; i < 5; ++i) {
                double[] arrd2 = this.entity.getLatencyPos(5 - i, this.a);
                f5 = (float)Math.cos((float)i * 0.45f + f14) * 0.15f;
                this.neck.yRot = Mth.rotWrap(arrd2[0] - arrd[0]) * 0.017453292f * 1.5f;
                this.neck.xRot = f5 + this.entity.getHeadPartYOffset(i, arrd, arrd2) * 0.017453292f * 1.5f * 5.0f;
                this.neck.zRot = -Mth.rotWrap(arrd2[0] - (double)f13) * 0.017453292f * 1.5f;
                this.neck.y = f9;
                this.neck.z = f10;
                this.neck.x = f8;
                f9 = (float)((double)f9 + Math.sin(this.neck.xRot) * 10.0);
                f10 = (float)((double)f10 - Math.cos(this.neck.yRot) * Math.cos(this.neck.xRot) * 10.0);
                f8 = (float)((double)f8 - Math.sin(this.neck.yRot) * Math.cos(this.neck.xRot) * 10.0);
                this.neck.render(poseStack, vertexConsumer, n, n2);
            }
            this.head.y = f9;
            this.head.z = f10;
            this.head.x = f8;
            double[] arrd3 = this.entity.getLatencyPos(0, this.a);
            this.head.yRot = Mth.rotWrap(arrd3[0] - arrd[0]) * 0.017453292f;
            this.head.xRot = Mth.rotWrap(this.entity.getHeadPartYOffset(6, arrd, arrd3)) * 0.017453292f * 1.5f * 5.0f;
            this.head.zRot = -Mth.rotWrap(arrd3[0] - (double)f13) * 0.017453292f;
            this.head.render(poseStack, vertexConsumer, n, n2);
            poseStack.pushPose();
            poseStack.translate(0.0, 1.0, 0.0);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(-f12 * 1.5f));
            poseStack.translate(0.0, -1.0, 0.0);
            this.body.zRot = 0.0f;
            this.body.render(poseStack, vertexConsumer, n, n2);
            float f15 = f6 * 6.2831855f;
            this.leftWing.xRot = 0.125f - (float)Math.cos(f15) * 0.2f;
            this.leftWing.yRot = -0.25f;
            this.leftWing.zRot = -((float)(Math.sin(f15) + 0.125)) * 0.8f;
            this.leftWingTip.zRot = (float)(Math.sin(f15 + 2.0f) + 0.5) * 0.75f;
            this.rightWing.xRot = this.leftWing.xRot;
            this.rightWing.yRot = -this.leftWing.yRot;
            this.rightWing.zRot = -this.leftWing.zRot;
            this.rightWingTip.zRot = -this.leftWingTip.zRot;
            this.renderSide(poseStack, vertexConsumer, n, n2, f7, this.leftWing, this.leftFrontLeg, this.leftFrontLegTip, this.leftFrontFoot, this.leftRearLeg, this.leftRearLegTip, this.leftRearFoot);
            this.renderSide(poseStack, vertexConsumer, n, n2, f7, this.rightWing, this.rightFrontLeg, this.rightFrontLegTip, this.rightFrontFoot, this.rightRearLeg, this.rightRearLegTip, this.rightRearFoot);
            poseStack.popPose();
            f5 = -((float)Math.sin(f6 * 6.2831855f)) * 0.0f;
            f14 = f6 * 6.2831855f;
            f9 = 10.0f;
            f10 = 60.0f;
            f8 = 0.0f;
            arrd = this.entity.getLatencyPos(11, this.a);
            for (int i = 0; i < 12; ++i) {
                arrd3 = this.entity.getLatencyPos(12 + i, this.a);
                f5 = (float)((double)f5 + Math.sin((float)i * 0.45f + f14) * 0.05000000074505806);
                this.neck.yRot = (Mth.rotWrap(arrd3[0] - arrd[0]) * 1.5f + 180.0f) * 0.017453292f;
                this.neck.xRot = f5 + (float)(arrd3[1] - arrd[1]) * 0.017453292f * 1.5f * 5.0f;
                this.neck.zRot = Mth.rotWrap(arrd3[0] - (double)f13) * 0.017453292f * 1.5f;
                this.neck.y = f9;
                this.neck.z = f10;
                this.neck.x = f8;
                f9 = (float)((double)f9 + Math.sin(this.neck.xRot) * 10.0);
                f10 = (float)((double)f10 - Math.cos(this.neck.yRot) * Math.cos(this.neck.xRot) * 10.0);
                f8 = (float)((double)f8 - Math.sin(this.neck.yRot) * Math.cos(this.neck.xRot) * 10.0);
                this.neck.render(poseStack, vertexConsumer, n, n2);
            }
            poseStack.popPose();
        }

        private void renderSide(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, ModelPart modelPart, ModelPart modelPart2, ModelPart modelPart3, ModelPart modelPart4, ModelPart modelPart5, ModelPart modelPart6, ModelPart modelPart7) {
            modelPart5.xRot = 1.0f + f * 0.1f;
            modelPart6.xRot = 0.5f + f * 0.1f;
            modelPart7.xRot = 0.75f + f * 0.1f;
            modelPart2.xRot = 1.3f + f * 0.1f;
            modelPart3.xRot = -0.5f - f * 0.1f;
            modelPart4.xRot = 0.75f + f * 0.1f;
            modelPart.render(poseStack, vertexConsumer, n, n2);
            modelPart2.render(poseStack, vertexConsumer, n, n2);
            modelPart5.render(poseStack, vertexConsumer, n, n2);
        }
    }

}

