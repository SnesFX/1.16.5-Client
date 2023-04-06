/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Drowned;

public class DrownedOuterLayer<T extends Drowned>
extends RenderLayer<T, DrownedModel<T>> {
    private static final ResourceLocation DROWNED_OUTER_LAYER_LOCATION = new ResourceLocation("textures/entity/zombie/drowned_outer_layer.png");
    private final DrownedModel<T> model = new DrownedModel(0.25f, 0.0f, 64, 64);

    public DrownedOuterLayer(RenderLayerParent<T, DrownedModel<T>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        DrownedOuterLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, DROWNED_OUTER_LAYER_LOCATION, poseStack, multiBufferSource, n, t, f, f2, f4, f5, f6, f3, 1.0f, 1.0f, 1.0f);
    }
}

