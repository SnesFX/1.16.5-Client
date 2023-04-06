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

public class TridentModel
extends Model {
    public static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/trident.png");
    private final ModelPart pole = new ModelPart(32, 32, 0, 6);

    public TridentModel() {
        super(RenderType::entitySolid);
        this.pole.addBox(-0.5f, 2.0f, -0.5f, 1.0f, 25.0f, 1.0f, 0.0f);
        ModelPart modelPart = new ModelPart(32, 32, 4, 0);
        modelPart.addBox(-1.5f, 0.0f, -0.5f, 3.0f, 2.0f, 1.0f);
        this.pole.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(32, 32, 4, 3);
        modelPart2.addBox(-2.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f);
        this.pole.addChild(modelPart2);
        ModelPart modelPart3 = new ModelPart(32, 32, 0, 0);
        modelPart3.addBox(-0.5f, -4.0f, -0.5f, 1.0f, 4.0f, 1.0f, 0.0f);
        this.pole.addChild(modelPart3);
        ModelPart modelPart4 = new ModelPart(32, 32, 4, 3);
        modelPart4.mirror = true;
        modelPart4.addBox(1.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f);
        this.pole.addChild(modelPart4);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        this.pole.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
    }
}

