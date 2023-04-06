/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.phys.Vec3;

public class IllusionerRenderer
extends IllagerRenderer<Illusioner> {
    private static final ResourceLocation ILLUSIONER = new ResourceLocation("textures/entity/illager/illusioner.png");

    public IllusionerRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new IllagerModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addLayer(new ItemInHandLayer<Illusioner, IllagerModel<Illusioner>>((RenderLayerParent)this){

            @Override
            public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, Illusioner illusioner, float f, float f2, float f3, float f4, float f5, float f6) {
                if (illusioner.isCastingSpell() || illusioner.isAggressive()) {
                    super.render(poseStack, multiBufferSource, n, illusioner, f, f2, f3, f4, f5, f6);
                }
            }
        });
        ((IllagerModel)this.model).getHat().visible = true;
    }

    @Override
    public ResourceLocation getTextureLocation(Illusioner illusioner) {
        return ILLUSIONER;
    }

    @Override
    public void render(Illusioner illusioner, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        if (illusioner.isInvisible()) {
            Vec3[] arrvec3 = illusioner.getIllusionOffsets(f2);
            float f3 = this.getBob(illusioner, f2);
            for (int i = 0; i < arrvec3.length; ++i) {
                poseStack.pushPose();
                poseStack.translate(arrvec3[i].x + (double)Mth.cos((float)i + f3 * 0.5f) * 0.025, arrvec3[i].y + (double)Mth.cos((float)i + f3 * 0.75f) * 0.0125, arrvec3[i].z + (double)Mth.cos((float)i + f3 * 0.7f) * 0.025);
                super.render(illusioner, f, f2, poseStack, multiBufferSource, n);
                poseStack.popPose();
            }
        } else {
            super.render(illusioner, f, f2, poseStack, multiBufferSource, n);
        }
    }

    @Override
    protected boolean isBodyVisible(Illusioner illusioner) {
        return true;
    }

    @Override
    protected /* synthetic */ boolean isBodyVisible(LivingEntity livingEntity) {
        return this.isBodyVisible((Illusioner)livingEntity);
    }

}

