/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BookModel
extends Model {
    private final ModelPart leftLid = new ModelPart(64, 32, 0, 0).addBox(-6.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f);
    private final ModelPart rightLid = new ModelPart(64, 32, 16, 0).addBox(0.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f);
    private final ModelPart leftPages;
    private final ModelPart rightPages;
    private final ModelPart flipPage1;
    private final ModelPart flipPage2;
    private final ModelPart seam = new ModelPart(64, 32, 12, 0).addBox(-1.0f, -5.0f, 0.0f, 2.0f, 10.0f, 0.005f);
    private final List<ModelPart> parts;

    public BookModel() {
        super(RenderType::entitySolid);
        this.leftPages = new ModelPart(64, 32, 0, 10).addBox(0.0f, -4.0f, -0.99f, 5.0f, 8.0f, 1.0f);
        this.rightPages = new ModelPart(64, 32, 12, 10).addBox(0.0f, -4.0f, -0.01f, 5.0f, 8.0f, 1.0f);
        this.flipPage1 = new ModelPart(64, 32, 24, 10).addBox(0.0f, -4.0f, 0.0f, 5.0f, 8.0f, 0.005f);
        this.flipPage2 = new ModelPart(64, 32, 24, 10).addBox(0.0f, -4.0f, 0.0f, 5.0f, 8.0f, 0.005f);
        this.parts = ImmutableList.of((Object)this.leftLid, (Object)this.rightLid, (Object)this.seam, (Object)this.leftPages, (Object)this.rightPages, (Object)this.flipPage1, (Object)this.flipPage2);
        this.leftLid.setPos(0.0f, 0.0f, -1.0f);
        this.rightLid.setPos(0.0f, 0.0f, 1.0f);
        this.seam.yRot = 1.5707964f;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        this.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4);
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2, float f, float f2, float f3, float f4) {
        this.parts.forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, n, n2, f, f2, f3, f4));
    }

    public void setupAnim(float f, float f2, float f3, float f4) {
        float f5 = (Mth.sin(f * 0.02f) * 0.1f + 1.25f) * f4;
        this.leftLid.yRot = 3.1415927f + f5;
        this.rightLid.yRot = -f5;
        this.leftPages.yRot = f5;
        this.rightPages.yRot = -f5;
        this.flipPage1.yRot = f5 - f5 * 2.0f * f2;
        this.flipPage2.yRot = f5 - f5 * 2.0f * f3;
        this.leftPages.x = Mth.sin(f5);
        this.rightPages.x = Mth.sin(f5);
        this.flipPage1.x = Mth.sin(f5);
        this.flipPage2.x = Mth.sin(f5);
    }
}

