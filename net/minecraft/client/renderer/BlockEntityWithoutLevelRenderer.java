/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Pair
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import org.apache.commons.lang3.StringUtils;

public class BlockEntityWithoutLevelRenderer {
    private static final ShulkerBoxBlockEntity[] SHULKER_BOXES = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(ShulkerBoxBlockEntity::new).toArray(n -> new ShulkerBoxBlockEntity[n]);
    private static final ShulkerBoxBlockEntity DEFAULT_SHULKER_BOX = new ShulkerBoxBlockEntity(null);
    public static final BlockEntityWithoutLevelRenderer instance = new BlockEntityWithoutLevelRenderer();
    private final ChestBlockEntity chest = new ChestBlockEntity();
    private final ChestBlockEntity trappedChest = new TrappedChestBlockEntity();
    private final EnderChestBlockEntity enderChest = new EnderChestBlockEntity();
    private final BannerBlockEntity banner = new BannerBlockEntity();
    private final BedBlockEntity bed = new BedBlockEntity();
    private final ConduitBlockEntity conduit = new ConduitBlockEntity();
    private final ShieldModel shieldModel = new ShieldModel();
    private final TridentModel tridentModel = new TridentModel();

    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2) {
        Item item = itemStack.getItem();
        if (item instanceof BlockItem) {
            BlockEntity blockEntity;
            Block block = ((BlockItem)item).getBlock();
            if (block instanceof AbstractSkullBlock) {
                GameProfile gameProfile = null;
                if (itemStack.hasTag()) {
                    CompoundTag compoundTag = itemStack.getTag();
                    if (compoundTag.contains("SkullOwner", 10)) {
                        gameProfile = NbtUtils.readGameProfile(compoundTag.getCompound("SkullOwner"));
                    } else if (compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)compoundTag.getString("SkullOwner"))) {
                        gameProfile = new GameProfile(null, compoundTag.getString("SkullOwner"));
                        gameProfile = SkullBlockEntity.updateGameprofile(gameProfile);
                        compoundTag.remove("SkullOwner");
                        compoundTag.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), gameProfile));
                    }
                }
                SkullBlockRenderer.renderSkull(null, 180.0f, ((AbstractSkullBlock)block).getType(), gameProfile, 0.0f, poseStack, multiBufferSource, n);
                return;
            }
            if (block instanceof AbstractBannerBlock) {
                this.banner.fromItem(itemStack, ((AbstractBannerBlock)block).getColor());
                blockEntity = this.banner;
            } else if (block instanceof BedBlock) {
                this.bed.setColor(((BedBlock)block).getColor());
                blockEntity = this.bed;
            } else if (block == Blocks.CONDUIT) {
                blockEntity = this.conduit;
            } else if (block == Blocks.CHEST) {
                blockEntity = this.chest;
            } else if (block == Blocks.ENDER_CHEST) {
                blockEntity = this.enderChest;
            } else if (block == Blocks.TRAPPED_CHEST) {
                blockEntity = this.trappedChest;
            } else if (block instanceof ShulkerBoxBlock) {
                DyeColor dyeColor = ShulkerBoxBlock.getColorFromItem(item);
                blockEntity = dyeColor == null ? DEFAULT_SHULKER_BOX : SHULKER_BOXES[dyeColor.getId()];
            } else {
                return;
            }
            BlockEntityRenderDispatcher.instance.renderItem(blockEntity, poseStack, multiBufferSource, n, n2);
            return;
        }
        if (item == Items.SHIELD) {
            boolean bl = itemStack.getTagElement("BlockEntityTag") != null;
            poseStack.pushPose();
            poseStack.scale(1.0f, -1.0f, -1.0f);
            Material material = bl ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
            VertexConsumer vertexConsumer = material.sprite().wrap(ItemRenderer.getFoilBufferDirect(multiBufferSource, this.shieldModel.renderType(material.atlasLocation()), true, itemStack.hasFoil()));
            this.shieldModel.handle().render(poseStack, vertexConsumer, n, n2, 1.0f, 1.0f, 1.0f, 1.0f);
            if (bl) {
                List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.createPatterns(ShieldItem.getColor(itemStack), BannerBlockEntity.getItemPatterns(itemStack));
                BannerRenderer.renderPatterns(poseStack, multiBufferSource, n, n2, this.shieldModel.plate(), material, false, list, itemStack.hasFoil());
            } else {
                this.shieldModel.plate().render(poseStack, vertexConsumer, n, n2, 1.0f, 1.0f, 1.0f, 1.0f);
            }
            poseStack.popPose();
        } else if (item == Items.TRIDENT) {
            poseStack.pushPose();
            poseStack.scale(1.0f, -1.0f, -1.0f);
            VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(multiBufferSource, this.tridentModel.renderType(TridentModel.TEXTURE), false, itemStack.hasFoil());
            this.tridentModel.renderToBuffer(poseStack, vertexConsumer, n, n2, 1.0f, 1.0f, 1.0f, 1.0f);
            poseStack.popPose();
        }
    }
}

