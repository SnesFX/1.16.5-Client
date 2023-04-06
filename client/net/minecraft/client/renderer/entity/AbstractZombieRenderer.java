/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;

public abstract class AbstractZombieRenderer<T extends Zombie, M extends ZombieModel<T>>
extends HumanoidMobRenderer<T, M> {
    private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");

    protected AbstractZombieRenderer(EntityRenderDispatcher entityRenderDispatcher, M m, M m2, M m3) {
        super(entityRenderDispatcher, m, 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, m2, m3));
    }

    @Override
    public ResourceLocation getTextureLocation(Zombie zombie) {
        return ZOMBIE_LOCATION;
    }

    @Override
    protected boolean isShaking(T t) {
        return ((Zombie)t).isUnderWaterConverting();
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntity livingEntity) {
        return this.isShaking((T)((Zombie)livingEntity));
    }
}

