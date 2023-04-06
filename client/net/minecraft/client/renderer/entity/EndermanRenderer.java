/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Random;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CarriedBlockLayer;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EndermanRenderer
extends MobRenderer<EnderMan, EndermanModel<EnderMan>> {
    private static final ResourceLocation ENDERMAN_LOCATION = new ResourceLocation("textures/entity/enderman/enderman.png");
    private final Random random = new Random();

    public EndermanRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EndermanModel(0.0f), 0.5f);
        this.addLayer(new EnderEyesLayer<EnderMan>(this));
        this.addLayer(new CarriedBlockLayer(this));
    }

    @Override
    public void render(EnderMan enderMan, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        BlockState blockState = enderMan.getCarriedBlock();
        EndermanModel endermanModel = (EndermanModel)this.getModel();
        endermanModel.carrying = blockState != null;
        endermanModel.creepy = enderMan.isCreepy();
        super.render(enderMan, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public Vec3 getRenderOffset(EnderMan enderMan, float f) {
        if (enderMan.isCreepy()) {
            double d = 0.02;
            return new Vec3(this.random.nextGaussian() * 0.02, 0.0, this.random.nextGaussian() * 0.02);
        }
        return super.getRenderOffset(enderMan, f);
    }

    @Override
    public ResourceLocation getTextureLocation(EnderMan enderMan) {
        return ENDERMAN_LOCATION;
    }
}

