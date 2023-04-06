/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherArmorLayer
extends EnergySwirlLayer<WitherBoss, WitherBossModel<WitherBoss>> {
    private static final ResourceLocation WITHER_ARMOR_LOCATION = new ResourceLocation("textures/entity/wither/wither_armor.png");
    private final WitherBossModel<WitherBoss> model = new WitherBossModel(0.5f);

    public WitherArmorLayer(RenderLayerParent<WitherBoss, WitherBossModel<WitherBoss>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    protected float xOffset(float f) {
        return Mth.cos(f * 0.02f) * 3.0f;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return WITHER_ARMOR_LOCATION;
    }

    @Override
    protected EntityModel<WitherBoss> model() {
        return this.model;
    }
}

