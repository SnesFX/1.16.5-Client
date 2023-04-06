/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class BannerRenderer
extends BlockEntityRenderer<BannerBlockEntity> {
    private final ModelPart flag = BannerRenderer.makeFlag();
    private final ModelPart pole = new ModelPart(64, 64, 44, 0);
    private final ModelPart bar;

    public BannerRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        this.pole.addBox(-1.0f, -30.0f, -1.0f, 2.0f, 42.0f, 2.0f, 0.0f);
        this.bar = new ModelPart(64, 64, 0, 42);
        this.bar.addBox(-10.0f, -32.0f, -1.0f, 20.0f, 2.0f, 2.0f, 0.0f);
    }

    public static ModelPart makeFlag() {
        ModelPart modelPart = new ModelPart(64, 64, 0, 0);
        modelPart.addBox(-10.0f, 0.0f, -2.0f, 20.0f, 40.0f, 1.0f, 0.0f);
        return modelPart;
    }

    @Override
    public void render(BannerBlockEntity bannerBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        Object object;
        long l;
        List<Pair<BannerPattern, DyeColor>> list = bannerBlockEntity.getPatterns();
        if (list == null) {
            return;
        }
        float f2 = 0.6666667f;
        boolean bl = bannerBlockEntity.getLevel() == null;
        poseStack.pushPose();
        if (bl) {
            l = 0L;
            poseStack.translate(0.5, 0.5, 0.5);
            this.pole.visible = true;
        } else {
            float f3;
            l = bannerBlockEntity.getLevel().getGameTime();
            object = bannerBlockEntity.getBlockState();
            if (((BlockBehaviour.BlockStateBase)object).getBlock() instanceof BannerBlock) {
                poseStack.translate(0.5, 0.5, 0.5);
                f3 = (float)(-((StateHolder)object).getValue(BannerBlock.ROTATION).intValue() * 360) / 16.0f;
                poseStack.mulPose(Vector3f.YP.rotationDegrees(f3));
                this.pole.visible = true;
            } else {
                poseStack.translate(0.5, -0.1666666716337204, 0.5);
                f3 = -((StateHolder)object).getValue(WallBannerBlock.FACING).toYRot();
                poseStack.mulPose(Vector3f.YP.rotationDegrees(f3));
                poseStack.translate(0.0, -0.3125, -0.4375);
                this.pole.visible = false;
            }
        }
        poseStack.pushPose();
        poseStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
        object = ModelBakery.BANNER_BASE.buffer(multiBufferSource, RenderType::entitySolid);
        this.pole.render(poseStack, (VertexConsumer)object, n, n2);
        this.bar.render(poseStack, (VertexConsumer)object, n, n2);
        BlockPos blockPos = bannerBlockEntity.getBlockPos();
        float f4 = ((float)Math.floorMod((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l, 100L) + f) / 100.0f;
        this.flag.xRot = (-0.0125f + 0.01f * Mth.cos(6.2831855f * f4)) * 3.1415927f;
        this.flag.y = -32.0f;
        BannerRenderer.renderPatterns(poseStack, multiBufferSource, n, n2, this.flag, ModelBakery.BANNER_BASE, true, list);
        poseStack.popPose();
        poseStack.popPose();
    }

    public static void renderPatterns(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2, ModelPart modelPart, Material material, boolean bl, List<Pair<BannerPattern, DyeColor>> list) {
        BannerRenderer.renderPatterns(poseStack, multiBufferSource, n, n2, modelPart, material, bl, list, false);
    }

    public static void renderPatterns(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2, ModelPart modelPart, Material material, boolean bl, List<Pair<BannerPattern, DyeColor>> list, boolean bl2) {
        modelPart.render(poseStack, material.buffer(multiBufferSource, RenderType::entitySolid, bl2), n, n2);
        for (int i = 0; i < 17 && i < list.size(); ++i) {
            Pair<BannerPattern, DyeColor> pair = list.get(i);
            float[] arrf = ((DyeColor)pair.getSecond()).getTextureDiffuseColors();
            Material material2 = new Material(bl ? Sheets.BANNER_SHEET : Sheets.SHIELD_SHEET, ((BannerPattern)((Object)pair.getFirst())).location(bl));
            modelPart.render(poseStack, material2.buffer(multiBufferSource, RenderType::entityNoOutline), n, n2, arrf[0], arrf[1], arrf[2], 1.0f);
        }
    }
}

