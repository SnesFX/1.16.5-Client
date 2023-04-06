/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemCrackinessLayer
extends RenderLayer<IronGolem, IronGolemModel<IronGolem>> {
    private static final Map<IronGolem.Crackiness, ResourceLocation> resourceLocations = ImmutableMap.of((Object)((Object)IronGolem.Crackiness.LOW), (Object)new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"), (Object)((Object)IronGolem.Crackiness.MEDIUM), (Object)new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), (Object)((Object)IronGolem.Crackiness.HIGH), (Object)new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

    public IronGolemCrackinessLayer(RenderLayerParent<IronGolem, IronGolemModel<IronGolem>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, IronGolem ironGolem, float f, float f2, float f3, float f4, float f5, float f6) {
        if (ironGolem.isInvisible()) {
            return;
        }
        IronGolem.Crackiness crackiness = ironGolem.getCrackiness();
        if (crackiness == IronGolem.Crackiness.NONE) {
            return;
        }
        ResourceLocation resourceLocation = resourceLocations.get((Object)crackiness);
        IronGolemCrackinessLayer.renderColoredCutoutModel(this.getParentModel(), resourceLocation, poseStack, multiBufferSource, n, ironGolem, 1.0f, 1.0f, 1.0f);
    }
}

