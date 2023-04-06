/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;

public class VillagerRenderer
extends MobRenderer<Villager, VillagerModel<Villager>> {
    private static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/villager/villager.png");

    public VillagerRenderer(EntityRenderDispatcher entityRenderDispatcher, ReloadableResourceManager reloadableResourceManager) {
        super(entityRenderDispatcher, new VillagerModel(0.0f), 0.5f);
        this.addLayer(new CustomHeadLayer<Villager, VillagerModel<Villager>>(this));
        this.addLayer(new VillagerProfessionLayer<Villager, VillagerModel<Villager>>(this, reloadableResourceManager, "villager"));
        this.addLayer(new CrossedArmsItemLayer<Villager, VillagerModel<Villager>>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Villager villager) {
        return VILLAGER_BASE_SKIN;
    }

    @Override
    protected void scale(Villager villager, PoseStack poseStack, float f) {
        float f2 = 0.9375f;
        if (villager.isBaby()) {
            f2 = (float)((double)f2 * 0.5);
            this.shadowRadius = 0.25f;
        } else {
            this.shadowRadius = 0.5f;
        }
        poseStack.scale(f2, f2, f2);
    }
}

