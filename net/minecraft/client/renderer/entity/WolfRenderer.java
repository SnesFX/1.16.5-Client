/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Wolf;

public class WolfRenderer
extends MobRenderer<Wolf, WolfModel<Wolf>> {
    private static final ResourceLocation WOLF_LOCATION = new ResourceLocation("textures/entity/wolf/wolf.png");
    private static final ResourceLocation WOLF_TAME_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
    private static final ResourceLocation WOLF_ANGRY_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_angry.png");

    public WolfRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new WolfModel(), 0.5f);
        this.addLayer(new WolfCollarLayer(this));
    }

    @Override
    protected float getBob(Wolf wolf, float f) {
        return wolf.getTailAngle();
    }

    @Override
    public void render(Wolf wolf, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        if (wolf.isWet()) {
            float f3 = wolf.getWetShade(f2);
            ((WolfModel)this.model).setColor(f3, f3, f3);
        }
        super.render(wolf, f, f2, poseStack, multiBufferSource, n);
        if (wolf.isWet()) {
            ((WolfModel)this.model).setColor(1.0f, 1.0f, 1.0f);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Wolf wolf) {
        if (wolf.isTame()) {
            return WOLF_TAME_LOCATION;
        }
        if (wolf.isAngry()) {
            return WOLF_ANGRY_LOCATION;
        }
        return WOLF_LOCATION;
    }
}

