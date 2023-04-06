/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PufferfishBigModel;
import net.minecraft.client.model.PufferfishMidModel;
import net.minecraft.client.model.PufferfishSmallModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Pufferfish;

public class PufferfishRenderer
extends MobRenderer<Pufferfish, EntityModel<Pufferfish>> {
    private static final ResourceLocation PUFFER_LOCATION = new ResourceLocation("textures/entity/fish/pufferfish.png");
    private int puffStateO = 3;
    private final PufferfishSmallModel<Pufferfish> small = new PufferfishSmallModel();
    private final PufferfishMidModel<Pufferfish> mid = new PufferfishMidModel();
    private final PufferfishBigModel<Pufferfish> big = new PufferfishBigModel();

    public PufferfishRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new PufferfishBigModel(), 0.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(Pufferfish pufferfish) {
        return PUFFER_LOCATION;
    }

    @Override
    public void render(Pufferfish pufferfish, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        int n2 = pufferfish.getPuffState();
        if (n2 != this.puffStateO) {
            this.model = n2 == 0 ? this.small : (n2 == 1 ? this.mid : this.big);
        }
        this.puffStateO = n2;
        this.shadowRadius = 0.1f + 0.1f * (float)n2;
        super.render(pufferfish, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    protected void setupRotations(Pufferfish pufferfish, PoseStack poseStack, float f, float f2, float f3) {
        poseStack.translate(0.0, Mth.cos(f * 0.05f) * 0.08f, 0.0);
        super.setupRotations(pufferfish, poseStack, f, f2, f3);
    }
}

