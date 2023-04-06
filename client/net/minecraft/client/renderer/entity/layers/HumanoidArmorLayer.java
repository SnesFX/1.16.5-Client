/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>>
extends RenderLayer<T, M> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
    private final A innerModel;
    private final A outerModel;

    public HumanoidArmorLayer(RenderLayerParent<T, M> renderLayerParent, A a, A a2) {
        super(renderLayerParent);
        this.innerModel = a;
        this.outerModel = a2;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        this.renderArmorPiece(poseStack, multiBufferSource, t, EquipmentSlot.CHEST, n, this.getArmorModel(EquipmentSlot.CHEST));
        this.renderArmorPiece(poseStack, multiBufferSource, t, EquipmentSlot.LEGS, n, this.getArmorModel(EquipmentSlot.LEGS));
        this.renderArmorPiece(poseStack, multiBufferSource, t, EquipmentSlot.FEET, n, this.getArmorModel(EquipmentSlot.FEET));
        this.renderArmorPiece(poseStack, multiBufferSource, t, EquipmentSlot.HEAD, n, this.getArmorModel(EquipmentSlot.HEAD));
    }

    private void renderArmorPiece(PoseStack poseStack, MultiBufferSource multiBufferSource, T t, EquipmentSlot equipmentSlot, int n, A a) {
        ItemStack itemStack = ((LivingEntity)t).getItemBySlot(equipmentSlot);
        if (!(itemStack.getItem() instanceof ArmorItem)) {
            return;
        }
        ArmorItem armorItem = (ArmorItem)itemStack.getItem();
        if (armorItem.getSlot() != equipmentSlot) {
            return;
        }
        ((HumanoidModel)this.getParentModel()).copyPropertiesTo(a);
        this.setPartVisibility(a, equipmentSlot);
        boolean bl = this.usesInnerModel(equipmentSlot);
        boolean bl2 = itemStack.hasFoil();
        if (armorItem instanceof DyeableArmorItem) {
            int n2 = ((DyeableArmorItem)armorItem).getColor(itemStack);
            float f = (float)(n2 >> 16 & 0xFF) / 255.0f;
            float f2 = (float)(n2 >> 8 & 0xFF) / 255.0f;
            float f3 = (float)(n2 & 0xFF) / 255.0f;
            this.renderModel(poseStack, multiBufferSource, n, armorItem, bl2, a, bl, f, f2, f3, null);
            this.renderModel(poseStack, multiBufferSource, n, armorItem, bl2, a, bl, 1.0f, 1.0f, 1.0f, "overlay");
        } else {
            this.renderModel(poseStack, multiBufferSource, n, armorItem, bl2, a, bl, 1.0f, 1.0f, 1.0f, null);
        }
    }

    protected void setPartVisibility(A a, EquipmentSlot equipmentSlot) {
        ((HumanoidModel)a).setAllVisible(false);
        switch (equipmentSlot) {
            case HEAD: {
                a.head.visible = true;
                a.hat.visible = true;
                break;
            }
            case CHEST: {
                a.body.visible = true;
                a.rightArm.visible = true;
                a.leftArm.visible = true;
                break;
            }
            case LEGS: {
                a.body.visible = true;
                a.rightLeg.visible = true;
                a.leftLeg.visible = true;
                break;
            }
            case FEET: {
                a.rightLeg.visible = true;
                a.leftLeg.visible = true;
            }
        }
    }

    private void renderModel(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, ArmorItem armorItem, boolean bl, A a, boolean bl2, float f, float f2, float f3, @Nullable String string) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource, RenderType.armorCutoutNoCull(this.getArmorLocation(armorItem, bl2, string)), false, bl);
        ((AgeableListModel)a).renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, f, f2, f3, 1.0f);
    }

    private A getArmorModel(EquipmentSlot equipmentSlot) {
        return this.usesInnerModel(equipmentSlot) ? this.innerModel : this.outerModel;
    }

    private boolean usesInnerModel(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.LEGS;
    }

    private ResourceLocation getArmorLocation(ArmorItem armorItem, boolean bl, @Nullable String string) {
        String string2 = "textures/models/armor/" + armorItem.getMaterial().getName() + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";
        return ARMOR_LOCATION_CACHE.computeIfAbsent(string2, ResourceLocation::new);
    }

}

