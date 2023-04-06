/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.ZombieVillager;

public class ZombieVillagerRenderer
extends HumanoidMobRenderer<ZombieVillager, ZombieVillagerModel<ZombieVillager>> {
    private static final ResourceLocation ZOMBIE_VILLAGER_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");

    public ZombieVillagerRenderer(EntityRenderDispatcher entityRenderDispatcher, ReloadableResourceManager reloadableResourceManager) {
        super(entityRenderDispatcher, new ZombieVillagerModel(0.0f, false), 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, new ZombieVillagerModel(0.5f, true), new ZombieVillagerModel(1.0f, true)));
        this.addLayer(new VillagerProfessionLayer<ZombieVillager, ZombieVillagerModel<ZombieVillager>>(this, reloadableResourceManager, "zombie_villager"));
    }

    @Override
    public ResourceLocation getTextureLocation(ZombieVillager zombieVillager) {
        return ZOMBIE_VILLAGER_LOCATION;
    }

    @Override
    protected boolean isShaking(ZombieVillager zombieVillager) {
        return zombieVillager.isConverting();
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntity livingEntity) {
        return this.isShaking((ZombieVillager)livingEntity);
    }
}

