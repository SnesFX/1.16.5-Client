/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.List;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CatRenderer
extends MobRenderer<Cat, CatModel<Cat>> {
    public CatRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new CatModel(0.0f), 0.4f);
        this.addLayer(new CatCollarLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Cat cat) {
        return cat.getResourceLocation();
    }

    @Override
    protected void scale(Cat cat, PoseStack poseStack, float f) {
        super.scale(cat, poseStack, f);
        poseStack.scale(0.8f, 0.8f, 0.8f);
    }

    @Override
    protected void setupRotations(Cat cat, PoseStack poseStack, float f, float f2, float f3) {
        super.setupRotations(cat, poseStack, f, f2, f3);
        float f4 = cat.getLieDownAmount(f3);
        if (f4 > 0.0f) {
            poseStack.translate(0.4f * f4, 0.15f * f4, 0.1f * f4);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.rotLerp(f4, 0.0f, 90.0f)));
            BlockPos blockPos = cat.blockPosition();
            List<Player> list = cat.level.getEntitiesOfClass(Player.class, new AABB(blockPos).inflate(2.0, 2.0, 2.0));
            for (Player player : list) {
                if (!player.isSleeping()) continue;
                poseStack.translate(0.15f * f4, 0.0, 0.0);
                break;
            }
        }
    }
}

