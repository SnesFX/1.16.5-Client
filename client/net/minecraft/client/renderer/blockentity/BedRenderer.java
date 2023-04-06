/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntFunction
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class BedRenderer
extends BlockEntityRenderer<BedBlockEntity> {
    private final ModelPart headPiece;
    private final ModelPart footPiece;
    private final ModelPart[] legs = new ModelPart[4];

    public BedRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        this.headPiece = new ModelPart(64, 64, 0, 0);
        this.headPiece.addBox(0.0f, 0.0f, 0.0f, 16.0f, 16.0f, 6.0f, 0.0f);
        this.footPiece = new ModelPart(64, 64, 0, 22);
        this.footPiece.addBox(0.0f, 0.0f, 0.0f, 16.0f, 16.0f, 6.0f, 0.0f);
        this.legs[0] = new ModelPart(64, 64, 50, 0);
        this.legs[1] = new ModelPart(64, 64, 50, 6);
        this.legs[2] = new ModelPart(64, 64, 50, 12);
        this.legs[3] = new ModelPart(64, 64, 50, 18);
        this.legs[0].addBox(0.0f, 6.0f, -16.0f, 3.0f, 3.0f, 3.0f);
        this.legs[1].addBox(0.0f, 6.0f, 0.0f, 3.0f, 3.0f, 3.0f);
        this.legs[2].addBox(-16.0f, 6.0f, -16.0f, 3.0f, 3.0f, 3.0f);
        this.legs[3].addBox(-16.0f, 6.0f, 0.0f, 3.0f, 3.0f, 3.0f);
        this.legs[0].xRot = 1.5707964f;
        this.legs[1].xRot = 1.5707964f;
        this.legs[2].xRot = 1.5707964f;
        this.legs[3].xRot = 1.5707964f;
        this.legs[0].zRot = 0.0f;
        this.legs[1].zRot = 1.5707964f;
        this.legs[2].zRot = 4.712389f;
        this.legs[3].zRot = 3.1415927f;
    }

    @Override
    public void render(BedBlockEntity bedBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        Material material = Sheets.BED_TEXTURES[bedBlockEntity.getColor().getId()];
        Level level = bedBlockEntity.getLevel();
        if (level != null) {
            BlockState blockState = bedBlockEntity.getBlockState();
            DoubleBlockCombiner.NeighborCombineResult<BedBlockEntity> neighborCombineResult = DoubleBlockCombiner.combineWithNeigbour(BlockEntityType.BED, BedBlock::getBlockType, BedBlock::getConnectedDirection, ChestBlock.FACING, blockState, level, bedBlockEntity.getBlockPos(), (levelAccessor, blockPos) -> false);
            int n3 = ((Int2IntFunction)neighborCombineResult.apply(new BrightnessCombiner())).get(n);
            this.renderPiece(poseStack, multiBufferSource, blockState.getValue(BedBlock.PART) == BedPart.HEAD, blockState.getValue(BedBlock.FACING), material, n3, n2, false);
        } else {
            this.renderPiece(poseStack, multiBufferSource, true, Direction.SOUTH, material, n, n2, false);
            this.renderPiece(poseStack, multiBufferSource, false, Direction.SOUTH, material, n, n2, true);
        }
    }

    private void renderPiece(PoseStack poseStack, MultiBufferSource multiBufferSource, boolean bl, Direction direction, Material material, int n, int n2, boolean bl2) {
        this.headPiece.visible = bl;
        this.footPiece.visible = !bl;
        this.legs[0].visible = !bl;
        this.legs[1].visible = bl;
        this.legs[2].visible = !bl;
        this.legs[3].visible = bl;
        poseStack.pushPose();
        poseStack.translate(0.0, 0.5625, bl2 ? -1.0 : 0.0);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0f));
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0f + direction.toYRot()));
        poseStack.translate(-0.5, -0.5, -0.5);
        VertexConsumer vertexConsumer = material.buffer(multiBufferSource, RenderType::entitySolid);
        this.headPiece.render(poseStack, vertexConsumer, n, n2);
        this.footPiece.render(poseStack, vertexConsumer, n, n2);
        this.legs[0].render(poseStack, vertexConsumer, n, n2);
        this.legs[1].render(poseStack, vertexConsumer, n, n2);
        this.legs[2].render(poseStack, vertexConsumer, n, n2);
        this.legs[3].render(poseStack, vertexConsumer, n, n2);
        poseStack.popPose();
    }
}

