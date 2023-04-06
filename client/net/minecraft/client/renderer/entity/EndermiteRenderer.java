/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EndermiteModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Endermite;

public class EndermiteRenderer
extends MobRenderer<Endermite, EndermiteModel<Endermite>> {
    private static final ResourceLocation ENDERMITE_LOCATION = new ResourceLocation("textures/entity/endermite.png");

    public EndermiteRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EndermiteModel(), 0.3f);
    }

    @Override
    protected float getFlipDegrees(Endermite endermite) {
        return 180.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(Endermite endermite) {
        return ENDERMITE_LOCATION;
    }

    @Override
    protected /* synthetic */ float getFlipDegrees(LivingEntity livingEntity) {
        return this.getFlipDegrees((Endermite)livingEntity);
    }

    @Override
    public /* synthetic */ ResourceLocation getTextureLocation(Entity entity) {
        return this.getTextureLocation((Endermite)entity);
    }
}

