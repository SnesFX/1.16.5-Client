/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;

public class DrownedRenderer
extends AbstractZombieRenderer<Drowned, DrownedModel<Drowned>> {
    private static final ResourceLocation DROWNED_LOCATION = new ResourceLocation("textures/entity/zombie/drowned.png");

    public DrownedRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new DrownedModel(0.0f, 0.0f, 64, 64), new DrownedModel(0.5f, true), new DrownedModel(1.0f, true));
        this.addLayer(new DrownedOuterLayer<Drowned>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Zombie zombie) {
        return DROWNED_LOCATION;
    }

    @Override
    protected void setupRotations(Drowned drowned, PoseStack poseStack, float f, float f2, float f3) {
        super.setupRotations(drowned, poseStack, f, f2, f3);
        float f4 = drowned.getSwimAmount(f3);
        if (f4 > 0.0f) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(f4, drowned.xRot, -10.0f - drowned.xRot)));
        }
    }
}

