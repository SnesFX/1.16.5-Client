/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WitchItemLayer<T extends LivingEntity>
extends CrossedArmsItemLayer<T, WitchModel<T>> {
    public WitchItemLayer(RenderLayerParent<T, WitchModel<T>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        ItemStack itemStack = ((LivingEntity)t).getMainHandItem();
        poseStack.pushPose();
        if (itemStack.getItem() == Items.POTION) {
            ((WitchModel)this.getParentModel()).getHead().translateAndRotate(poseStack);
            ((WitchModel)this.getParentModel()).getNose().translateAndRotate(poseStack);
            poseStack.translate(0.0625, 0.25, 0.0);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(140.0f));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(10.0f));
            poseStack.translate(0.0, -0.4000000059604645, 0.4000000059604645);
        }
        super.render(poseStack, multiBufferSource, n, t, f, f2, f3, f4, f5, f6);
        poseStack.popPose();
    }
}

