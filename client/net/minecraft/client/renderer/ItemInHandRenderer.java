/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 */
package net.minecraft.client.renderer;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class ItemInHandRenderer {
    private static final RenderType MAP_BACKGROUND = RenderType.text(new ResourceLocation("textures/map/map_background.png"));
    private static final RenderType MAP_BACKGROUND_CHECKERBOARD = RenderType.text(new ResourceLocation("textures/map/map_background_checkerboard.png"));
    private final Minecraft minecraft;
    private ItemStack mainHandItem = ItemStack.EMPTY;
    private ItemStack offHandItem = ItemStack.EMPTY;
    private float mainHandHeight;
    private float oMainHandHeight;
    private float offHandHeight;
    private float oOffHandHeight;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final ItemRenderer itemRenderer;

    public ItemInHandRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
        this.itemRenderer = minecraft.getItemRenderer();
    }

    public void renderItem(LivingEntity livingEntity, ItemStack itemStack, ItemTransforms.TransformType transformType, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        if (itemStack.isEmpty()) {
            return;
        }
        this.itemRenderer.renderStatic(livingEntity, itemStack, transformType, bl, poseStack, multiBufferSource, livingEntity.level, n, OverlayTexture.NO_OVERLAY);
    }

    private float calculateMapTilt(float f) {
        float f2 = 1.0f - f / 45.0f + 0.1f;
        f2 = Mth.clamp(f2, 0.0f, 1.0f);
        f2 = -Mth.cos(f2 * 3.1415927f) * 0.5f + 0.5f;
        return f2;
    }

    private void renderMapHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, HumanoidArm humanoidArm) {
        this.minecraft.getTextureManager().bind(this.minecraft.player.getSkinTextureLocation());
        PlayerRenderer playerRenderer = (PlayerRenderer)this.entityRenderDispatcher.getRenderer(this.minecraft.player);
        poseStack.pushPose();
        float f = humanoidArm == HumanoidArm.RIGHT ? 1.0f : -1.0f;
        poseStack.mulPose(Vector3f.YP.rotationDegrees(92.0f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(45.0f));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(f * -41.0f));
        poseStack.translate(f * 0.3f, -1.100000023841858, 0.44999998807907104);
        if (humanoidArm == HumanoidArm.RIGHT) {
            playerRenderer.renderRightHand(poseStack, multiBufferSource, n, this.minecraft.player);
        } else {
            playerRenderer.renderLeftHand(poseStack, multiBufferSource, n, this.minecraft.player);
        }
        poseStack.popPose();
    }

    private void renderOneHandedMap(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, float f, HumanoidArm humanoidArm, float f2, ItemStack itemStack) {
        float f3 = humanoidArm == HumanoidArm.RIGHT ? 1.0f : -1.0f;
        poseStack.translate(f3 * 0.125f, -0.125, 0.0);
        if (!this.minecraft.player.isInvisible()) {
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(f3 * 10.0f));
            this.renderPlayerArm(poseStack, multiBufferSource, n, f, f2, humanoidArm);
            poseStack.popPose();
        }
        poseStack.pushPose();
        poseStack.translate(f3 * 0.51f, -0.08f + f * -1.2f, -0.75);
        float f4 = Mth.sqrt(f2);
        float f5 = Mth.sin(f4 * 3.1415927f);
        float f6 = -0.5f * f5;
        float f7 = 0.4f * Mth.sin(f4 * 6.2831855f);
        float f8 = -0.3f * Mth.sin(f2 * 3.1415927f);
        poseStack.translate(f3 * f6, f7 - 0.3f * f5, f8);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(f5 * -45.0f));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f3 * f5 * -30.0f));
        this.renderMap(poseStack, multiBufferSource, n, itemStack);
        poseStack.popPose();
    }

    private void renderTwoHandedMap(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, float f, float f2, float f3) {
        float f4 = Mth.sqrt(f3);
        float f5 = -0.2f * Mth.sin(f3 * 3.1415927f);
        float f6 = -0.4f * Mth.sin(f4 * 3.1415927f);
        poseStack.translate(0.0, -f5 / 2.0f, f6);
        float f7 = this.calculateMapTilt(f);
        poseStack.translate(0.0, 0.04f + f2 * -1.2f + f7 * -0.5f, -0.7200000286102295);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(f7 * -85.0f));
        if (!this.minecraft.player.isInvisible()) {
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0f));
            this.renderMapHand(poseStack, multiBufferSource, n, HumanoidArm.RIGHT);
            this.renderMapHand(poseStack, multiBufferSource, n, HumanoidArm.LEFT);
            poseStack.popPose();
        }
        float f8 = Mth.sin(f4 * 3.1415927f);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(f8 * 20.0f));
        poseStack.scale(2.0f, 2.0f, 2.0f);
        this.renderMap(poseStack, multiBufferSource, n, this.mainHandItem);
    }

    private void renderMap(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, ItemStack itemStack) {
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
        poseStack.scale(0.38f, 0.38f, 0.38f);
        poseStack.translate(-0.5, -0.5, 0.0);
        poseStack.scale(0.0078125f, 0.0078125f, 0.0078125f);
        MapItemSavedData mapItemSavedData = MapItem.getOrCreateSavedData(itemStack, this.minecraft.level);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(mapItemSavedData == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
        Matrix4f matrix4f = poseStack.last().pose();
        vertexConsumer.vertex(matrix4f, -7.0f, 135.0f, 0.0f).color(255, 255, 255, 255).uv(0.0f, 1.0f).uv2(n).endVertex();
        vertexConsumer.vertex(matrix4f, 135.0f, 135.0f, 0.0f).color(255, 255, 255, 255).uv(1.0f, 1.0f).uv2(n).endVertex();
        vertexConsumer.vertex(matrix4f, 135.0f, -7.0f, 0.0f).color(255, 255, 255, 255).uv(1.0f, 0.0f).uv2(n).endVertex();
        vertexConsumer.vertex(matrix4f, -7.0f, -7.0f, 0.0f).color(255, 255, 255, 255).uv(0.0f, 0.0f).uv2(n).endVertex();
        if (mapItemSavedData != null) {
            this.minecraft.gameRenderer.getMapRenderer().render(poseStack, multiBufferSource, mapItemSavedData, false, n);
        }
    }

    private void renderPlayerArm(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, float f, float f2, HumanoidArm humanoidArm) {
        boolean bl = humanoidArm != HumanoidArm.LEFT;
        float f3 = bl ? 1.0f : -1.0f;
        float f4 = Mth.sqrt(f2);
        float f5 = -0.3f * Mth.sin(f4 * 3.1415927f);
        float f6 = 0.4f * Mth.sin(f4 * 6.2831855f);
        float f7 = -0.4f * Mth.sin(f2 * 3.1415927f);
        poseStack.translate(f3 * (f5 + 0.64000005f), f6 + -0.6f + f * -0.6f, f7 + -0.71999997f);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f3 * 45.0f));
        float f8 = Mth.sin(f2 * f2 * 3.1415927f);
        float f9 = Mth.sin(f4 * 3.1415927f);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f3 * f9 * 70.0f));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(f3 * f8 * -20.0f));
        LocalPlayer localPlayer = this.minecraft.player;
        this.minecraft.getTextureManager().bind(localPlayer.getSkinTextureLocation());
        poseStack.translate(f3 * -1.0f, 3.5999999046325684, 3.5);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(f3 * 120.0f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(200.0f));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f3 * -135.0f));
        poseStack.translate(f3 * 5.6f, 0.0, 0.0);
        PlayerRenderer playerRenderer = (PlayerRenderer)this.entityRenderDispatcher.getRenderer(localPlayer);
        if (bl) {
            playerRenderer.renderRightHand(poseStack, multiBufferSource, n, localPlayer);
        } else {
            playerRenderer.renderLeftHand(poseStack, multiBufferSource, n, localPlayer);
        }
    }

    private void applyEatTransform(PoseStack poseStack, float f, HumanoidArm humanoidArm, ItemStack itemStack) {
        float f2;
        float f3 = (float)this.minecraft.player.getUseItemRemainingTicks() - f + 1.0f;
        float f4 = f3 / (float)itemStack.getUseDuration();
        if (f4 < 0.8f) {
            f2 = Mth.abs(Mth.cos(f3 / 4.0f * 3.1415927f) * 0.1f);
            poseStack.translate(0.0, f2, 0.0);
        }
        f2 = 1.0f - (float)Math.pow(f4, 27.0);
        int n = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(f2 * 0.6f * (float)n, f2 * -0.5f, f2 * 0.0f);
        poseStack.mulPose(Vector3f.YP.rotationDegrees((float)n * f2 * 90.0f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(f2 * 10.0f));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees((float)n * f2 * 30.0f));
    }

    private void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm humanoidArm, float f) {
        int n = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        float f2 = Mth.sin(f * f * 3.1415927f);
        poseStack.mulPose(Vector3f.YP.rotationDegrees((float)n * (45.0f + f2 * -20.0f)));
        float f3 = Mth.sin(Mth.sqrt(f) * 3.1415927f);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees((float)n * f3 * -20.0f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(f3 * -80.0f));
        poseStack.mulPose(Vector3f.YP.rotationDegrees((float)n * -45.0f));
    }

    private void applyItemArmTransform(PoseStack poseStack, HumanoidArm humanoidArm, float f) {
        int n = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate((float)n * 0.56f, -0.52f + f * -0.6f, -0.7200000286102295);
    }

    public void renderHandsWithItems(float f, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LocalPlayer localPlayer, int n) {
        Object object;
        float f2;
        ItemStack itemStack;
        float f3 = localPlayer.getAttackAnim(f);
        InteractionHand interactionHand = (InteractionHand)((Object)MoreObjects.firstNonNull((Object)((Object)localPlayer.swingingArm), (Object)((Object)InteractionHand.MAIN_HAND)));
        float f4 = Mth.lerp(f, localPlayer.xRotO, localPlayer.xRot);
        boolean bl = true;
        boolean bl2 = true;
        if (localPlayer.isUsingItem()) {
            ItemStack itemStack2;
            itemStack = localPlayer.getUseItem();
            if (itemStack.getItem() == Items.BOW || itemStack.getItem() == Items.CROSSBOW) {
                bl = localPlayer.getUsedItemHand() == InteractionHand.MAIN_HAND;
                boolean bl3 = bl2 = !bl;
            }
            if ((object = localPlayer.getUsedItemHand()) == InteractionHand.MAIN_HAND && (itemStack2 = localPlayer.getOffhandItem()).getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack2)) {
                bl2 = false;
            }
        } else {
            itemStack = localPlayer.getMainHandItem();
            object = localPlayer.getOffhandItem();
            if (itemStack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack)) {
                boolean bl4 = bl2 = !bl;
            }
            if (object.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(object)) {
                bl = !itemStack.isEmpty();
                bl2 = !bl;
            }
        }
        float f5 = Mth.lerp(f, localPlayer.xBobO, localPlayer.xBob);
        float f6 = Mth.lerp(f, localPlayer.yBobO, localPlayer.yBob);
        poseStack.mulPose(Vector3f.XP.rotationDegrees((localPlayer.getViewXRot(f) - f5) * 0.1f));
        poseStack.mulPose(Vector3f.YP.rotationDegrees((localPlayer.getViewYRot(f) - f6) * 0.1f));
        if (bl) {
            float f7 = interactionHand == InteractionHand.MAIN_HAND ? f3 : 0.0f;
            f2 = 1.0f - Mth.lerp(f, this.oMainHandHeight, this.mainHandHeight);
            this.renderArmWithItem(localPlayer, f, f4, InteractionHand.MAIN_HAND, f7, this.mainHandItem, f2, poseStack, bufferSource, n);
        }
        if (bl2) {
            float f8 = interactionHand == InteractionHand.OFF_HAND ? f3 : 0.0f;
            f2 = 1.0f - Mth.lerp(f, this.oOffHandHeight, this.offHandHeight);
            this.renderArmWithItem(localPlayer, f, f4, InteractionHand.OFF_HAND, f8, this.offHandItem, f2, poseStack, bufferSource, n);
        }
        bufferSource.endBatch();
    }

    private void renderArmWithItem(AbstractClientPlayer abstractClientPlayer, float f, float f2, InteractionHand interactionHand, float f3, ItemStack itemStack, float f4, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        boolean bl = interactionHand == InteractionHand.MAIN_HAND;
        HumanoidArm humanoidArm = bl ? abstractClientPlayer.getMainArm() : abstractClientPlayer.getMainArm().getOpposite();
        poseStack.pushPose();
        if (itemStack.isEmpty()) {
            if (bl && !abstractClientPlayer.isInvisible()) {
                this.renderPlayerArm(poseStack, multiBufferSource, n, f4, f3, humanoidArm);
            }
        } else if (itemStack.getItem() == Items.FILLED_MAP) {
            if (bl && this.offHandItem.isEmpty()) {
                this.renderTwoHandedMap(poseStack, multiBufferSource, n, f2, f4, f3);
            } else {
                this.renderOneHandedMap(poseStack, multiBufferSource, n, f4, humanoidArm, f3, itemStack);
            }
        } else if (itemStack.getItem() == Items.CROSSBOW) {
            int n2;
            boolean bl2 = CrossbowItem.isCharged(itemStack);
            boolean bl3 = humanoidArm == HumanoidArm.RIGHT;
            int n3 = n2 = bl3 ? 1 : -1;
            if (abstractClientPlayer.isUsingItem() && abstractClientPlayer.getUseItemRemainingTicks() > 0 && abstractClientPlayer.getUsedItemHand() == interactionHand) {
                this.applyItemArmTransform(poseStack, humanoidArm, f4);
                poseStack.translate((float)n2 * -0.4785682f, -0.0943870022892952, 0.05731530860066414);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-11.935f));
                poseStack.mulPose(Vector3f.YP.rotationDegrees((float)n2 * 65.3f));
                poseStack.mulPose(Vector3f.ZP.rotationDegrees((float)n2 * -9.785f));
                float f5 = (float)itemStack.getUseDuration() - ((float)this.minecraft.player.getUseItemRemainingTicks() - f + 1.0f);
                float f6 = f5 / (float)CrossbowItem.getChargeDuration(itemStack);
                if (f6 > 1.0f) {
                    f6 = 1.0f;
                }
                if (f6 > 0.1f) {
                    float f7 = Mth.sin((f5 - 0.1f) * 1.3f);
                    float f8 = f6 - 0.1f;
                    float f9 = f7 * f8;
                    poseStack.translate(f9 * 0.0f, f9 * 0.004f, f9 * 0.0f);
                }
                poseStack.translate(f6 * 0.0f, f6 * 0.0f, f6 * 0.04f);
                poseStack.scale(1.0f, 1.0f, 1.0f + f6 * 0.2f);
                poseStack.mulPose(Vector3f.YN.rotationDegrees((float)n2 * 45.0f));
            } else {
                float f10 = -0.4f * Mth.sin(Mth.sqrt(f3) * 3.1415927f);
                float f11 = 0.2f * Mth.sin(Mth.sqrt(f3) * 6.2831855f);
                float f12 = -0.2f * Mth.sin(f3 * 3.1415927f);
                poseStack.translate((float)n2 * f10, f11, f12);
                this.applyItemArmTransform(poseStack, humanoidArm, f4);
                this.applyItemArmAttackTransform(poseStack, humanoidArm, f3);
                if (bl2 && f3 < 0.001f) {
                    poseStack.translate((float)n2 * -0.641864f, 0.0, 0.0);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees((float)n2 * 10.0f));
                }
            }
            this.renderItem(abstractClientPlayer, itemStack, bl3 ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !bl3, poseStack, multiBufferSource, n);
        } else {
            boolean bl4;
            boolean bl5 = bl4 = humanoidArm == HumanoidArm.RIGHT;
            if (abstractClientPlayer.isUsingItem() && abstractClientPlayer.getUseItemRemainingTicks() > 0 && abstractClientPlayer.getUsedItemHand() == interactionHand) {
                int n4 = bl4 ? 1 : -1;
                switch (itemStack.getUseAnimation()) {
                    case NONE: {
                        this.applyItemArmTransform(poseStack, humanoidArm, f4);
                        break;
                    }
                    case EAT: 
                    case DRINK: {
                        this.applyEatTransform(poseStack, f, humanoidArm, itemStack);
                        this.applyItemArmTransform(poseStack, humanoidArm, f4);
                        break;
                    }
                    case BLOCK: {
                        this.applyItemArmTransform(poseStack, humanoidArm, f4);
                        break;
                    }
                    case BOW: {
                        this.applyItemArmTransform(poseStack, humanoidArm, f4);
                        poseStack.translate((float)n4 * -0.2785682f, 0.18344387412071228, 0.15731531381607056);
                        poseStack.mulPose(Vector3f.XP.rotationDegrees(-13.935f));
                        poseStack.mulPose(Vector3f.YP.rotationDegrees((float)n4 * 35.3f));
                        poseStack.mulPose(Vector3f.ZP.rotationDegrees((float)n4 * -9.785f));
                        float f13 = (float)itemStack.getUseDuration() - ((float)this.minecraft.player.getUseItemRemainingTicks() - f + 1.0f);
                        float f14 = f13 / 20.0f;
                        f14 = (f14 * f14 + f14 * 2.0f) / 3.0f;
                        if (f14 > 1.0f) {
                            f14 = 1.0f;
                        }
                        if (f14 > 0.1f) {
                            float f15 = Mth.sin((f13 - 0.1f) * 1.3f);
                            float f16 = f14 - 0.1f;
                            float f17 = f15 * f16;
                            poseStack.translate(f17 * 0.0f, f17 * 0.004f, f17 * 0.0f);
                        }
                        poseStack.translate(f14 * 0.0f, f14 * 0.0f, f14 * 0.04f);
                        poseStack.scale(1.0f, 1.0f, 1.0f + f14 * 0.2f);
                        poseStack.mulPose(Vector3f.YN.rotationDegrees((float)n4 * 45.0f));
                        break;
                    }
                    case SPEAR: {
                        this.applyItemArmTransform(poseStack, humanoidArm, f4);
                        poseStack.translate((float)n4 * -0.5f, 0.699999988079071, 0.10000000149011612);
                        poseStack.mulPose(Vector3f.XP.rotationDegrees(-55.0f));
                        poseStack.mulPose(Vector3f.YP.rotationDegrees((float)n4 * 35.3f));
                        poseStack.mulPose(Vector3f.ZP.rotationDegrees((float)n4 * -9.785f));
                        float f18 = (float)itemStack.getUseDuration() - ((float)this.minecraft.player.getUseItemRemainingTicks() - f + 1.0f);
                        float f19 = f18 / 10.0f;
                        if (f19 > 1.0f) {
                            f19 = 1.0f;
                        }
                        if (f19 > 0.1f) {
                            float f20 = Mth.sin((f18 - 0.1f) * 1.3f);
                            float f21 = f19 - 0.1f;
                            float f22 = f20 * f21;
                            poseStack.translate(f22 * 0.0f, f22 * 0.004f, f22 * 0.0f);
                        }
                        poseStack.translate(0.0, 0.0, f19 * 0.2f);
                        poseStack.scale(1.0f, 1.0f, 1.0f + f19 * 0.2f);
                        poseStack.mulPose(Vector3f.YN.rotationDegrees((float)n4 * 45.0f));
                        break;
                    }
                }
            } else if (abstractClientPlayer.isAutoSpinAttack()) {
                this.applyItemArmTransform(poseStack, humanoidArm, f4);
                int n5 = bl4 ? 1 : -1;
                poseStack.translate((float)n5 * -0.4f, 0.800000011920929, 0.30000001192092896);
                poseStack.mulPose(Vector3f.YP.rotationDegrees((float)n5 * 65.0f));
                poseStack.mulPose(Vector3f.ZP.rotationDegrees((float)n5 * -85.0f));
            } else {
                float f23 = -0.4f * Mth.sin(Mth.sqrt(f3) * 3.1415927f);
                float f24 = 0.2f * Mth.sin(Mth.sqrt(f3) * 6.2831855f);
                float f25 = -0.2f * Mth.sin(f3 * 3.1415927f);
                int n6 = bl4 ? 1 : -1;
                poseStack.translate((float)n6 * f23, f24, f25);
                this.applyItemArmTransform(poseStack, humanoidArm, f4);
                this.applyItemArmAttackTransform(poseStack, humanoidArm, f3);
            }
            this.renderItem(abstractClientPlayer, itemStack, bl4 ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !bl4, poseStack, multiBufferSource, n);
        }
        poseStack.popPose();
    }

    public void tick() {
        this.oMainHandHeight = this.mainHandHeight;
        this.oOffHandHeight = this.offHandHeight;
        LocalPlayer localPlayer = this.minecraft.player;
        ItemStack itemStack = localPlayer.getMainHandItem();
        ItemStack itemStack2 = localPlayer.getOffhandItem();
        if (ItemStack.matches(this.mainHandItem, itemStack)) {
            this.mainHandItem = itemStack;
        }
        if (ItemStack.matches(this.offHandItem, itemStack2)) {
            this.offHandItem = itemStack2;
        }
        if (localPlayer.isHandsBusy()) {
            this.mainHandHeight = Mth.clamp(this.mainHandHeight - 0.4f, 0.0f, 1.0f);
            this.offHandHeight = Mth.clamp(this.offHandHeight - 0.4f, 0.0f, 1.0f);
        } else {
            float f = localPlayer.getAttackStrengthScale(1.0f);
            this.mainHandHeight += Mth.clamp((this.mainHandItem == itemStack ? f * f * f : 0.0f) - this.mainHandHeight, -0.4f, 0.4f);
            this.offHandHeight += Mth.clamp((float)(this.offHandItem == itemStack2) - this.offHandHeight, -0.4f, 0.4f);
        }
        if (this.mainHandHeight < 0.1f) {
            this.mainHandItem = itemStack;
        }
        if (this.offHandHeight < 0.1f) {
            this.offHandItem = itemStack2;
        }
    }

    public void itemUsed(InteractionHand interactionHand) {
        if (interactionHand == InteractionHand.MAIN_HAND) {
            this.mainHandHeight = 0.0f;
        } else {
            this.offHandHeight = 0.0f;
        }
    }

}

