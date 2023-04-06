/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.ShulkerHeadLayer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShulkerRenderer
extends MobRenderer<Shulker, ShulkerModel<Shulker>> {
    public static final ResourceLocation DEFAULT_TEXTURE_LOCATION = new ResourceLocation("textures/" + Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture().getPath() + ".png");
    public static final ResourceLocation[] TEXTURE_LOCATION = (ResourceLocation[])Sheets.SHULKER_TEXTURE_LOCATION.stream().map(material -> new ResourceLocation("textures/" + material.texture().getPath() + ".png")).toArray(n -> new ResourceLocation[n]);

    public ShulkerRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ShulkerModel(), 0.0f);
        this.addLayer(new ShulkerHeadLayer(this));
    }

    @Override
    public Vec3 getRenderOffset(Shulker shulker, float f) {
        int n = shulker.getClientSideTeleportInterpolation();
        if (n > 0 && shulker.hasValidInterpolationPositions()) {
            BlockPos blockPos = shulker.getAttachPosition();
            BlockPos blockPos2 = shulker.getOldAttachPosition();
            double d = (double)((float)n - f) / 6.0;
            d *= d;
            double d2 = (double)(blockPos.getX() - blockPos2.getX()) * d;
            double d3 = (double)(blockPos.getY() - blockPos2.getY()) * d;
            double d4 = (double)(blockPos.getZ() - blockPos2.getZ()) * d;
            return new Vec3(-d2, -d3, -d4);
        }
        return super.getRenderOffset(shulker, f);
    }

    @Override
    public boolean shouldRender(Shulker shulker, Frustum frustum, double d, double d2, double d3) {
        if (super.shouldRender(shulker, frustum, d, d2, d3)) {
            return true;
        }
        if (shulker.getClientSideTeleportInterpolation() > 0 && shulker.hasValidInterpolationPositions()) {
            Vec3 vec3 = Vec3.atLowerCornerOf(shulker.getAttachPosition());
            Vec3 vec32 = Vec3.atLowerCornerOf(shulker.getOldAttachPosition());
            if (frustum.isVisible(new AABB(vec32.x, vec32.y, vec32.z, vec3.x, vec3.y, vec3.z))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(Shulker shulker) {
        if (shulker.getColor() == null) {
            return DEFAULT_TEXTURE_LOCATION;
        }
        return TEXTURE_LOCATION[shulker.getColor().getId()];
    }

    @Override
    protected void setupRotations(Shulker shulker, PoseStack poseStack, float f, float f2, float f3) {
        super.setupRotations(shulker, poseStack, f, f2 + 180.0f, f3);
        poseStack.translate(0.0, 0.5, 0.0);
        poseStack.mulPose(shulker.getAttachFace().getOpposite().getRotation());
        poseStack.translate(0.0, -0.5, 0.0);
    }
}

