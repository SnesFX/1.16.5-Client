/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PlayerModel<T extends LivingEntity>
extends HumanoidModel<T> {
    private List<ModelPart> cubes = Lists.newArrayList();
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPants;
    public final ModelPart rightPants;
    public final ModelPart jacket;
    private final ModelPart cloak;
    private final ModelPart ear;
    private final boolean slim;

    public PlayerModel(float f, boolean bl) {
        super(RenderType::entityTranslucent, f, 0.0f, 64, 64);
        this.slim = bl;
        this.ear = new ModelPart(this, 24, 0);
        this.ear.addBox(-3.0f, -6.0f, -1.0f, 6.0f, 6.0f, 1.0f, f);
        this.cloak = new ModelPart(this, 0, 0);
        this.cloak.setTexSize(64, 32);
        this.cloak.addBox(-5.0f, 0.0f, -1.0f, 10.0f, 16.0f, 1.0f, f);
        if (bl) {
            this.leftArm = new ModelPart(this, 32, 48);
            this.leftArm.addBox(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, f);
            this.leftArm.setPos(5.0f, 2.5f, 0.0f);
            this.rightArm = new ModelPart(this, 40, 16);
            this.rightArm.addBox(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, f);
            this.rightArm.setPos(-5.0f, 2.5f, 0.0f);
            this.leftSleeve = new ModelPart(this, 48, 48);
            this.leftSleeve.addBox(-1.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, f + 0.25f);
            this.leftSleeve.setPos(5.0f, 2.5f, 0.0f);
            this.rightSleeve = new ModelPart(this, 40, 32);
            this.rightSleeve.addBox(-2.0f, -2.0f, -2.0f, 3.0f, 12.0f, 4.0f, f + 0.25f);
            this.rightSleeve.setPos(-5.0f, 2.5f, 10.0f);
        } else {
            this.leftArm = new ModelPart(this, 32, 48);
            this.leftArm.addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
            this.leftArm.setPos(5.0f, 2.0f, 0.0f);
            this.leftSleeve = new ModelPart(this, 48, 48);
            this.leftSleeve.addBox(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f + 0.25f);
            this.leftSleeve.setPos(5.0f, 2.0f, 0.0f);
            this.rightSleeve = new ModelPart(this, 40, 32);
            this.rightSleeve.addBox(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f + 0.25f);
            this.rightSleeve.setPos(-5.0f, 2.0f, 10.0f);
        }
        this.leftLeg = new ModelPart(this, 16, 48);
        this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.leftLeg.setPos(1.9f, 12.0f, 0.0f);
        this.leftPants = new ModelPart(this, 0, 48);
        this.leftPants.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f + 0.25f);
        this.leftPants.setPos(1.9f, 12.0f, 0.0f);
        this.rightPants = new ModelPart(this, 0, 32);
        this.rightPants.addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f + 0.25f);
        this.rightPants.setPos(-1.9f, 12.0f, 0.0f);
        this.jacket = new ModelPart(this, 16, 32);
        this.jacket.addBox(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, f + 0.25f);
        this.jacket.setPos(0.0f, 0.0f, 0.0f);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), (Iterable)ImmutableList.of((Object)this.leftPants, (Object)this.rightPants, (Object)this.leftSleeve, (Object)this.rightSleeve, (Object)this.jacket));
    }

    public void renderEars(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2) {
        this.ear.copyFrom(this.head);
        this.ear.x = 0.0f;
        this.ear.y = 0.0f;
        this.ear.render(poseStack, vertexConsumer, n, n2);
    }

    public void renderCloak(PoseStack poseStack, VertexConsumer vertexConsumer, int n, int n2) {
        this.cloak.render(poseStack, vertexConsumer, n, n2);
    }

    @Override
    public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
        super.setupAnim(t, f, f2, f3, f4, f5);
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        if (((LivingEntity)t).getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            if (((Entity)t).isCrouching()) {
                this.cloak.z = 1.4f;
                this.cloak.y = 1.85f;
            } else {
                this.cloak.z = 0.0f;
                this.cloak.y = 0.0f;
            }
        } else if (((Entity)t).isCrouching()) {
            this.cloak.z = 0.3f;
            this.cloak.y = 0.8f;
        } else {
            this.cloak.z = -1.1f;
            this.cloak.y = -0.85f;
        }
    }

    @Override
    public void setAllVisible(boolean bl) {
        super.setAllVisible(bl);
        this.leftSleeve.visible = bl;
        this.rightSleeve.visible = bl;
        this.leftPants.visible = bl;
        this.rightPants.visible = bl;
        this.jacket.visible = bl;
        this.cloak.visible = bl;
        this.ear.visible = bl;
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        ModelPart modelPart = this.getArm(humanoidArm);
        if (this.slim) {
            float f = 0.5f * (float)(humanoidArm == HumanoidArm.RIGHT ? 1 : -1);
            modelPart.x += f;
            modelPart.translateAndRotate(poseStack);
            modelPart.x -= f;
        } else {
            modelPart.translateAndRotate(poseStack);
        }
    }

    public ModelPart getRandomModelPart(Random random) {
        return this.cubes.get(random.nextInt(this.cubes.size()));
    }

    @Override
    public void accept(ModelPart modelPart) {
        if (this.cubes == null) {
            this.cubes = Lists.newArrayList();
        }
        this.cubes.add(modelPart);
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((ModelPart)object);
    }
}

