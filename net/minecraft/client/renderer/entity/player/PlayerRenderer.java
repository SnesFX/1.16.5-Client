/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5EarsLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class PlayerRenderer
extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        this(entityRenderDispatcher, false);
    }

    public PlayerRenderer(EntityRenderDispatcher entityRenderDispatcher, boolean bl) {
        super(entityRenderDispatcher, new PlayerModel(0.0f, bl), 0.5f);
        this.addLayer(new HumanoidArmorLayer(this, new HumanoidModel(0.5f), new HumanoidModel(1.0f)));
        this.addLayer(new ItemInHandLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(this));
        this.addLayer(new ArrowLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(this));
        this.addLayer(new Deadmau5EarsLayer(this));
        this.addLayer(new CapeLayer(this));
        this.addLayer(new CustomHeadLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(this));
        this.addLayer(new ElytraLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(this));
        this.addLayer(new ParrotOnShoulderLayer<AbstractClientPlayer>(this));
        this.addLayer(new SpinAttackEffectLayer<AbstractClientPlayer>(this));
        this.addLayer(new BeeStingerLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>(this));
    }

    @Override
    public void render(AbstractClientPlayer abstractClientPlayer, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        this.setModelProperties(abstractClientPlayer);
        super.render(abstractClientPlayer, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public Vec3 getRenderOffset(AbstractClientPlayer abstractClientPlayer, float f) {
        if (abstractClientPlayer.isCrouching()) {
            return new Vec3(0.0, -0.125, 0.0);
        }
        return super.getRenderOffset(abstractClientPlayer, f);
    }

    private void setModelProperties(AbstractClientPlayer abstractClientPlayer) {
        PlayerModel playerModel = (PlayerModel)this.getModel();
        if (abstractClientPlayer.isSpectator()) {
            playerModel.setAllVisible(false);
            playerModel.head.visible = true;
            playerModel.hat.visible = true;
        } else {
            playerModel.setAllVisible(true);
            playerModel.hat.visible = abstractClientPlayer.isModelPartShown(PlayerModelPart.HAT);
            playerModel.jacket.visible = abstractClientPlayer.isModelPartShown(PlayerModelPart.JACKET);
            playerModel.leftPants.visible = abstractClientPlayer.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            playerModel.rightPants.visible = abstractClientPlayer.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            playerModel.leftSleeve.visible = abstractClientPlayer.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            playerModel.rightSleeve.visible = abstractClientPlayer.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            playerModel.crouching = abstractClientPlayer.isCrouching();
            HumanoidModel.ArmPose armPose = PlayerRenderer.getArmPose(abstractClientPlayer, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose armPose2 = PlayerRenderer.getArmPose(abstractClientPlayer, InteractionHand.OFF_HAND);
            if (armPose.isTwoHanded()) {
                HumanoidModel.ArmPose armPose3 = armPose2 = abstractClientPlayer.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }
            if (abstractClientPlayer.getMainArm() == HumanoidArm.RIGHT) {
                playerModel.rightArmPose = armPose;
                playerModel.leftArmPose = armPose2;
            } else {
                playerModel.rightArmPose = armPose2;
                playerModel.leftArmPose = armPose;
            }
        }
    }

    private static HumanoidModel.ArmPose getArmPose(AbstractClientPlayer abstractClientPlayer, InteractionHand interactionHand) {
        ItemStack itemStack = abstractClientPlayer.getItemInHand(interactionHand);
        if (itemStack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        }
        if (abstractClientPlayer.getUsedItemHand() == interactionHand && abstractClientPlayer.getUseItemRemainingTicks() > 0) {
            UseAnim useAnim = itemStack.getUseAnimation();
            if (useAnim == UseAnim.BLOCK) {
                return HumanoidModel.ArmPose.BLOCK;
            }
            if (useAnim == UseAnim.BOW) {
                return HumanoidModel.ArmPose.BOW_AND_ARROW;
            }
            if (useAnim == UseAnim.SPEAR) {
                return HumanoidModel.ArmPose.THROW_SPEAR;
            }
            if (useAnim == UseAnim.CROSSBOW && interactionHand == abstractClientPlayer.getUsedItemHand()) {
                return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
            }
        } else if (!abstractClientPlayer.swinging && itemStack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack)) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
        return HumanoidModel.ArmPose.ITEM;
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractClientPlayer abstractClientPlayer) {
        return abstractClientPlayer.getSkinTextureLocation();
    }

    @Override
    protected void scale(AbstractClientPlayer abstractClientPlayer, PoseStack poseStack, float f) {
        float f2 = 0.9375f;
        poseStack.scale(0.9375f, 0.9375f, 0.9375f);
    }

    @Override
    protected void renderNameTag(AbstractClientPlayer abstractClientPlayer, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        Objective objective;
        Scoreboard scoreboard;
        double d = this.entityRenderDispatcher.distanceToSqr(abstractClientPlayer);
        poseStack.pushPose();
        if (d < 100.0 && (objective = (scoreboard = abstractClientPlayer.getScoreboard()).getDisplayObjective(2)) != null) {
            Score score = scoreboard.getOrCreatePlayerScore(abstractClientPlayer.getScoreboardName(), objective);
            super.renderNameTag(abstractClientPlayer, new TextComponent(Integer.toString(score.getScore())).append(" ").append(objective.getDisplayName()), poseStack, multiBufferSource, n);
            this.getFont().getClass();
            poseStack.translate(0.0, 9.0f * 1.15f * 0.025f, 0.0);
        }
        super.renderNameTag(abstractClientPlayer, component, poseStack, multiBufferSource, n);
        poseStack.popPose();
    }

    public void renderRightHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, AbstractClientPlayer abstractClientPlayer) {
        this.renderHand(poseStack, multiBufferSource, n, abstractClientPlayer, ((PlayerModel)this.model).rightArm, ((PlayerModel)this.model).rightSleeve);
    }

    public void renderLeftHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, AbstractClientPlayer abstractClientPlayer) {
        this.renderHand(poseStack, multiBufferSource, n, abstractClientPlayer, ((PlayerModel)this.model).leftArm, ((PlayerModel)this.model).leftSleeve);
    }

    private void renderHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, AbstractClientPlayer abstractClientPlayer, ModelPart modelPart, ModelPart modelPart2) {
        PlayerModel playerModel = (PlayerModel)this.getModel();
        this.setModelProperties(abstractClientPlayer);
        playerModel.attackTime = 0.0f;
        playerModel.crouching = false;
        playerModel.swimAmount = 0.0f;
        playerModel.setupAnim(abstractClientPlayer, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        modelPart.xRot = 0.0f;
        modelPart.render(poseStack, multiBufferSource.getBuffer(RenderType.entitySolid(abstractClientPlayer.getSkinTextureLocation())), n, OverlayTexture.NO_OVERLAY);
        modelPart2.xRot = 0.0f;
        modelPart2.render(poseStack, multiBufferSource.getBuffer(RenderType.entityTranslucent(abstractClientPlayer.getSkinTextureLocation())), n, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected void setupRotations(AbstractClientPlayer abstractClientPlayer, PoseStack poseStack, float f, float f2, float f3) {
        float f4 = abstractClientPlayer.getSwimAmount(f3);
        if (abstractClientPlayer.isFallFlying()) {
            super.setupRotations(abstractClientPlayer, poseStack, f, f2, f3);
            float f5 = (float)abstractClientPlayer.getFallFlyingTicks() + f3;
            float f6 = Mth.clamp(f5 * f5 / 100.0f, 0.0f, 1.0f);
            if (!abstractClientPlayer.isAutoSpinAttack()) {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(f6 * (-90.0f - abstractClientPlayer.xRot)));
            }
            Vec3 vec3 = abstractClientPlayer.getViewVector(f3);
            Vec3 vec32 = abstractClientPlayer.getDeltaMovement();
            double d = Entity.getHorizontalDistanceSqr(vec32);
            double d2 = Entity.getHorizontalDistanceSqr(vec3);
            if (d > 0.0 && d2 > 0.0) {
                double d3 = (vec32.x * vec3.x + vec32.z * vec3.z) / Math.sqrt(d * d2);
                double d4 = vec32.x * vec3.z - vec32.z * vec3.x;
                poseStack.mulPose(Vector3f.YP.rotation((float)(Math.signum(d4) * Math.acos(d3))));
            }
        } else if (f4 > 0.0f) {
            super.setupRotations(abstractClientPlayer, poseStack, f, f2, f3);
            float f7 = abstractClientPlayer.isInWater() ? -90.0f - abstractClientPlayer.xRot : -90.0f;
            float f8 = Mth.lerp(f4, 0.0f, f7);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(f8));
            if (abstractClientPlayer.isVisuallySwimming()) {
                poseStack.translate(0.0, -1.0, 0.30000001192092896);
            }
        } else {
            super.setupRotations(abstractClientPlayer, poseStack, f, f2, f3);
        }
    }
}

