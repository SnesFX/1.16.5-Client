/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ShieldModel
extends Model {
    private final ModelPart plate;
    private final ModelPart handle;

    public ShieldModel() {
        super(RenderType::entitySolid);
        this.texWidth = 64;
        this.texHeight = 64;
        this.plate = new ModelPart(this, 0, 0);
        this.plate.addBox(-6.0f, -11.0f, -2.0f, 12.0f, 22.0f, 1.0f, 0.0f);
        this.handle = new ModelPart(this, 26, 0);
        this.handle.addBox(-1.0f, -3.0f, -1.0f, 2.0f, 6.0f, 6.0f, 0.0f);
    }

    public ModelPart plate() {
        return this.plate;
    }

    public ModelPart handle() {
        return this.handle;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        this.plate.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
        this.handle.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
    }
}

