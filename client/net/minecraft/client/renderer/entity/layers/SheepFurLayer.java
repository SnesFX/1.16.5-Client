/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class SheepFurLayer
extends RenderLayer<Sheep, SheepModel<Sheep>> {
    private static final ResourceLocation SHEEP_FUR_LOCATION = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
    private final SheepFurModel<Sheep> model = new SheepFurModel();

    public SheepFurLayer(RenderLayerParent<Sheep, SheepModel<Sheep>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, Sheep sheep, float f, float f2, float f3, float f4, float f5, float f6) {
        float f7;
        float f8;
        float f9;
        if (sheep.isSheared() || sheep.isInvisible()) {
            return;
        }
        if (sheep.hasCustomName() && "jeb_".equals(sheep.getName().getContents())) {
            int n2 = 25;
            int n3 = sheep.tickCount / 25 + sheep.getId();
            int n4 = DyeColor.values().length;
            int n5 = n3 % n4;
            int n6 = (n3 + 1) % n4;
            float f10 = ((float)(sheep.tickCount % 25) + f3) / 25.0f;
            float[] arrf = Sheep.getColorArray(DyeColor.byId(n5));
            float[] arrf2 = Sheep.getColorArray(DyeColor.byId(n6));
            f8 = arrf[0] * (1.0f - f10) + arrf2[0] * f10;
            f7 = arrf[1] * (1.0f - f10) + arrf2[1] * f10;
            f9 = arrf[2] * (1.0f - f10) + arrf2[2] * f10;
        } else {
            float[] arrf = Sheep.getColorArray(sheep.getColor());
            f8 = arrf[0];
            f7 = arrf[1];
            f9 = arrf[2];
        }
        SheepFurLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, SHEEP_FUR_LOCATION, poseStack, multiBufferSource, n, sheep, f, f2, f4, f5, f6, f3, f8, f7, f9);
    }
}

