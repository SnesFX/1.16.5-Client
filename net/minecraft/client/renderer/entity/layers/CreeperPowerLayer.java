/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public class CreeperPowerLayer
extends EnergySwirlLayer<Creeper, CreeperModel<Creeper>> {
    private static final ResourceLocation POWER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final CreeperModel<Creeper> model = new CreeperModel(2.0f);

    public CreeperPowerLayer(RenderLayerParent<Creeper, CreeperModel<Creeper>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    protected float xOffset(float f) {
        return f * 0.01f;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return POWER_LOCATION;
    }

    @Override
    protected EntityModel<Creeper> model() {
        return this.model;
    }
}

