/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class PhantomEyesLayer<T extends Entity>
extends EyesLayer<T, PhantomModel<T>> {
    private static final RenderType PHANTOM_EYES = RenderType.eyes(new ResourceLocation("textures/entity/phantom_eyes.png"));

    public PhantomEyesLayer(RenderLayerParent<T, PhantomModel<T>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public RenderType renderType() {
        return PHANTOM_EYES;
    }
}

