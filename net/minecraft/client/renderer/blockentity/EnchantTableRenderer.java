/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.function.Function;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;

public class EnchantTableRenderer
extends BlockEntityRenderer<EnchantmentTableBlockEntity> {
    public static final Material BOOK_LOCATION = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/enchanting_table_book"));
    private final BookModel bookModel = new BookModel();

    public EnchantTableRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(EnchantmentTableBlockEntity enchantmentTableBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        float f2;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.75, 0.5);
        float f3 = (float)enchantmentTableBlockEntity.time + f;
        poseStack.translate(0.0, 0.1f + Mth.sin(f3 * 0.1f) * 0.01f, 0.0);
        for (f2 = enchantmentTableBlockEntity.rot - enchantmentTableBlockEntity.oRot; f2 >= 3.1415927f; f2 -= 6.2831855f) {
        }
        while (f2 < -3.1415927f) {
            f2 += 6.2831855f;
        }
        float f4 = enchantmentTableBlockEntity.oRot + f2 * f;
        poseStack.mulPose(Vector3f.YP.rotation(-f4));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(80.0f));
        float f5 = Mth.lerp(f, enchantmentTableBlockEntity.oFlip, enchantmentTableBlockEntity.flip);
        float f6 = Mth.frac(f5 + 0.25f) * 1.6f - 0.3f;
        float f7 = Mth.frac(f5 + 0.75f) * 1.6f - 0.3f;
        float f8 = Mth.lerp(f, enchantmentTableBlockEntity.oOpen, enchantmentTableBlockEntity.open);
        this.bookModel.setupAnim(f3, Mth.clamp(f6, 0.0f, 1.0f), Mth.clamp(f7, 0.0f, 1.0f), f8);
        VertexConsumer vertexConsumer = BOOK_LOCATION.buffer(multiBufferSource, RenderType::entitySolid);
        this.bookModel.render(poseStack, vertexConsumer, n, n2, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
    }
}

