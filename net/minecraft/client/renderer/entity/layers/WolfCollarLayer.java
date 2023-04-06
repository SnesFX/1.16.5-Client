/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.DyeColor;

public class WolfCollarLayer
extends RenderLayer<Wolf, WolfModel<Wolf>> {
    private static final ResourceLocation WOLF_COLLAR_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_collar.png");

    public WolfCollarLayer(RenderLayerParent<Wolf, WolfModel<Wolf>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, Wolf wolf, float f, float f2, float f3, float f4, float f5, float f6) {
        if (!wolf.isTame() || wolf.isInvisible()) {
            return;
        }
        float[] arrf = wolf.getCollarColor().getTextureDiffuseColors();
        WolfCollarLayer.renderColoredCutoutModel(this.getParentModel(), WOLF_COLLAR_LOCATION, poseStack, multiBufferSource, n, wolf, arrf[0], arrf[1], arrf[2]);
    }
}

