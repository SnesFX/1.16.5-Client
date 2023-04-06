/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class ListModel<E extends Entity>
extends EntityModel<E> {
    public ListModel() {
        this(RenderType::entityCutoutNoCull);
    }

    public ListModel(Function<ResourceLocation, RenderType> function) {
        super(function);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        this.parts().forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4));
    }

    public abstract Iterable<ModelPart> parts();
}

