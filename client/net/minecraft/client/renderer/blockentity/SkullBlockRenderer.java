/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidHeadModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.dragon.DragonHeadModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class SkullBlockRenderer
extends BlockEntityRenderer<SkullBlockEntity> {
    private static final Map<SkullBlock.Type, SkullModel> MODEL_BY_TYPE = Util.make(Maps.newHashMap(), hashMap -> {
        SkullModel skullModel = new SkullModel(0, 0, 64, 32);
        HumanoidHeadModel humanoidHeadModel = new HumanoidHeadModel();
        DragonHeadModel dragonHeadModel = new DragonHeadModel(0.0f);
        hashMap.put(SkullBlock.Types.SKELETON, skullModel);
        hashMap.put(SkullBlock.Types.WITHER_SKELETON, skullModel);
        hashMap.put(SkullBlock.Types.PLAYER, humanoidHeadModel);
        hashMap.put(SkullBlock.Types.ZOMBIE, humanoidHeadModel);
        hashMap.put(SkullBlock.Types.CREEPER, skullModel);
        hashMap.put(SkullBlock.Types.DRAGON, dragonHeadModel);
    });
    private static final Map<SkullBlock.Type, ResourceLocation> SKIN_BY_TYPE = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(SkullBlock.Types.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
        hashMap.put(SkullBlock.Types.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
        hashMap.put(SkullBlock.Types.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
        hashMap.put(SkullBlock.Types.CREEPER, new ResourceLocation("textures/entity/creeper/creeper.png"));
        hashMap.put(SkullBlock.Types.DRAGON, new ResourceLocation("textures/entity/enderdragon/dragon.png"));
        hashMap.put(SkullBlock.Types.PLAYER, DefaultPlayerSkin.getDefaultSkin());
    });

    public SkullBlockRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(SkullBlockEntity skullBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        float f2 = skullBlockEntity.getMouthAnimation(f);
        BlockState blockState = skullBlockEntity.getBlockState();
        boolean bl = blockState.getBlock() instanceof WallSkullBlock;
        Direction direction = bl ? blockState.getValue(WallSkullBlock.FACING) : null;
        float f3 = 22.5f * (float)(bl ? (2 + direction.get2DDataValue()) * 4 : blockState.getValue(SkullBlock.ROTATION));
        SkullBlockRenderer.renderSkull(direction, f3, ((AbstractSkullBlock)blockState.getBlock()).getType(), skullBlockEntity.getOwnerProfile(), f2, poseStack, multiBufferSource, n);
    }

    public static void renderSkull(@Nullable Direction direction, float f, SkullBlock.Type type, @Nullable GameProfile gameProfile, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        SkullModel skullModel = MODEL_BY_TYPE.get(type);
        poseStack.pushPose();
        if (direction == null) {
            poseStack.translate(0.5, 0.0, 0.5);
        } else {
            float f3 = 0.25f;
            poseStack.translate(0.5f - (float)direction.getStepX() * 0.25f, 0.25, 0.5f - (float)direction.getStepZ() * 0.25f);
        }
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(SkullBlockRenderer.getRenderType(type, gameProfile));
        skullModel.setupAnim(f2, f, 0.0f);
        skullModel.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
    }

    private static RenderType getRenderType(SkullBlock.Type type, @Nullable GameProfile gameProfile) {
        ResourceLocation resourceLocation = SKIN_BY_TYPE.get(type);
        if (type != SkullBlock.Types.PLAYER || gameProfile == null) {
            return RenderType.entityCutoutNoCullZOffset(resourceLocation);
        }
        Minecraft minecraft = Minecraft.getInstance();
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(gameProfile);
        if (map.containsKey((Object)MinecraftProfileTexture.Type.SKIN)) {
            return RenderType.entityTranslucent(minecraft.getSkinManager().registerTexture(map.get((Object)MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN));
        }
        return RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(gameProfile)));
    }
}

