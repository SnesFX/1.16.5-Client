/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.scores.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends EntityRenderer<T>
implements RenderLayerParent<T, M> {
    private static final Logger LOGGER = LogManager.getLogger();
    protected M model;
    protected final List<RenderLayer<T, M>> layers = Lists.newArrayList();

    public LivingEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M m, float f) {
        super(entityRenderDispatcher);
        this.model = m;
        this.shadowRadius = f;
    }

    protected final boolean addLayer(RenderLayer<T, M> renderLayer) {
        return this.layers.add(renderLayer);
    }

    @Override
    public M getModel() {
        return this.model;
    }

    @Override
    public void render(T t, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        float f4;
        float f3;
        Direction direction;
        poseStack.pushPose();
        ((EntityModel)this.model).attackTime = this.getAttackAnim(t, f2);
        ((EntityModel)this.model).riding = ((Entity)t).isPassenger();
        ((EntityModel)this.model).young = ((LivingEntity)t).isBaby();
        float f5 = Mth.rotLerp(f2, ((LivingEntity)t).yBodyRotO, ((LivingEntity)t).yBodyRot);
        float f6 = Mth.rotLerp(f2, ((LivingEntity)t).yHeadRotO, ((LivingEntity)t).yHeadRot);
        float f7 = f6 - f5;
        if (((Entity)t).isPassenger() && ((Entity)t).getVehicle() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)((Entity)t).getVehicle();
            f5 = Mth.rotLerp(f2, livingEntity.yBodyRotO, livingEntity.yBodyRot);
            f7 = f6 - f5;
            f3 = Mth.wrapDegrees(f7);
            if (f3 < -85.0f) {
                f3 = -85.0f;
            }
            if (f3 >= 85.0f) {
                f3 = 85.0f;
            }
            f5 = f6 - f3;
            if (f3 * f3 > 2500.0f) {
                f5 += f3 * 0.2f;
            }
            f7 = f6 - f5;
        }
        float f8 = Mth.lerp(f2, ((LivingEntity)t).xRotO, ((LivingEntity)t).xRot);
        if (((Entity)t).getPose() == Pose.SLEEPING && (direction = ((LivingEntity)t).getBedOrientation()) != null) {
            f4 = ((Entity)t).getEyeHeight(Pose.STANDING) - 0.1f;
            poseStack.translate((float)(-direction.getStepX()) * f4, 0.0, (float)(-direction.getStepZ()) * f4);
        }
        f3 = this.getBob(t, f2);
        this.setupRotations(t, poseStack, f3, f5, f2);
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        this.scale(t, poseStack, f2);
        poseStack.translate(0.0, -1.5010000467300415, 0.0);
        f4 = 0.0f;
        float f9 = 0.0f;
        if (!((Entity)t).isPassenger() && ((LivingEntity)t).isAlive()) {
            f4 = Mth.lerp(f2, ((LivingEntity)t).animationSpeedOld, ((LivingEntity)t).animationSpeed);
            f9 = ((LivingEntity)t).animationPosition - ((LivingEntity)t).animationSpeed * (1.0f - f2);
            if (((LivingEntity)t).isBaby()) {
                f9 *= 3.0f;
            }
            if (f4 > 1.0f) {
                f4 = 1.0f;
            }
        }
        ((EntityModel)this.model).prepareMobModel(t, f9, f4, f2);
        ((EntityModel)this.model).setupAnim(t, f9, f4, f3, f7, f8);
        Minecraft minecraft = Minecraft.getInstance();
        boolean bl = this.isBodyVisible(t);
        boolean bl2 = !bl && !((Entity)t).isInvisibleTo(minecraft.player);
        boolean bl3 = minecraft.shouldEntityAppearGlowing((Entity)t);
        RenderType renderType = this.getRenderType(t, bl, bl2, bl3);
        if (renderType != null) {
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(renderType);
            int n2 = LivingEntityRenderer.getOverlayCoords(t, this.getWhiteOverlayProgress(t, f2));
            ((Model)this.model).renderToBuffer(poseStack, vertexConsumer, n, n2, 1.0f, 1.0f, 1.0f, bl2 ? 0.15f : 1.0f);
        }
        if (!((Entity)t).isSpectator()) {
            for (RenderLayer renderLayer : this.layers) {
                renderLayer.render(poseStack, multiBufferSource, n, t, f9, f4, f2, f3, f7, f8);
            }
        }
        poseStack.popPose();
        super.render(t, f, f2, poseStack, multiBufferSource, n);
    }

    @Nullable
    protected RenderType getRenderType(T t, boolean bl, boolean bl2, boolean bl3) {
        ResourceLocation resourceLocation = this.getTextureLocation(t);
        if (bl2) {
            return RenderType.itemEntityTranslucentCull(resourceLocation);
        }
        if (bl) {
            return ((Model)this.model).renderType(resourceLocation);
        }
        if (bl3) {
            return RenderType.outline(resourceLocation);
        }
        return null;
    }

    public static int getOverlayCoords(LivingEntity livingEntity, float f) {
        return OverlayTexture.pack(OverlayTexture.u(f), OverlayTexture.v(livingEntity.hurtTime > 0 || livingEntity.deathTime > 0));
    }

    protected boolean isBodyVisible(T t) {
        return !((Entity)t).isInvisible();
    }

    private static float sleepDirectionToRotation(Direction direction) {
        switch (direction) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
                return 180.0f;
            }
        }
        return 0.0f;
    }

    protected boolean isShaking(T t) {
        return false;
    }

    protected void setupRotations(T t, PoseStack poseStack, float f, float f2, float f3) {
        String string;
        Pose pose;
        if (this.isShaking(t)) {
            f2 += (float)(Math.cos((double)((LivingEntity)t).tickCount * 3.25) * 3.141592653589793 * 0.4000000059604645);
        }
        if ((pose = ((Entity)t).getPose()) != Pose.SLEEPING) {
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f - f2));
        }
        if (((LivingEntity)t).deathTime > 0) {
            float f4 = ((float)((LivingEntity)t).deathTime + f3 - 1.0f) / 20.0f * 1.6f;
            if ((f4 = Mth.sqrt(f4)) > 1.0f) {
                f4 = 1.0f;
            }
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(f4 * this.getFlipDegrees(t)));
        } else if (((LivingEntity)t).isAutoSpinAttack()) {
            poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0f - ((LivingEntity)t).xRot));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(((float)((LivingEntity)t).tickCount + f3) * -75.0f));
        } else if (pose == Pose.SLEEPING) {
            Direction direction = ((LivingEntity)t).getBedOrientation();
            float f5 = direction != null ? LivingEntityRenderer.sleepDirectionToRotation(direction) : f2;
            poseStack.mulPose(Vector3f.YP.rotationDegrees(f5));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(this.getFlipDegrees(t)));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(270.0f));
        } else if ((((Entity)t).hasCustomName() || t instanceof Player) && ("Dinnerbone".equals(string = ChatFormatting.stripFormatting(((Entity)t).getName().getString())) || "Grumm".equals(string)) && (!(t instanceof Player) || ((Player)t).isModelPartShown(PlayerModelPart.CAPE))) {
            poseStack.translate(0.0, ((Entity)t).getBbHeight() + 0.1f, 0.0);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
        }
    }

    protected float getAttackAnim(T t, float f) {
        return ((LivingEntity)t).getAttackAnim(f);
    }

    protected float getBob(T t, float f) {
        return (float)((LivingEntity)t).tickCount + f;
    }

    protected float getFlipDegrees(T t) {
        return 90.0f;
    }

    protected float getWhiteOverlayProgress(T t, float f) {
        return 0.0f;
    }

    protected void scale(T t, PoseStack poseStack, float f) {
    }

    @Override
    protected boolean shouldShowName(T t) {
        float f;
        boolean bl;
        double d = this.entityRenderDispatcher.distanceToSqr((Entity)t);
        float f2 = f = ((Entity)t).isDiscrete() ? 32.0f : 64.0f;
        if (d >= (double)(f * f)) {
            return false;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        boolean bl2 = bl = !((Entity)t).isInvisibleTo(localPlayer);
        if (t != localPlayer) {
            Team team = ((Entity)t).getTeam();
            Team team2 = localPlayer.getTeam();
            if (team != null) {
                Team.Visibility visibility = team.getNameTagVisibility();
                switch (visibility) {
                    case ALWAYS: {
                        return bl;
                    }
                    case NEVER: {
                        return false;
                    }
                    case HIDE_FOR_OTHER_TEAMS: {
                        return team2 == null ? bl : team.isAlliedTo(team2) && (team.canSeeFriendlyInvisibles() || bl);
                    }
                    case HIDE_FOR_OWN_TEAM: {
                        return team2 == null ? bl : !team.isAlliedTo(team2) && bl;
                    }
                }
                return true;
            }
        }
        return Minecraft.renderNames() && t != minecraft.getCameraEntity() && bl && !((Entity)t).isVehicle();
    }

}

