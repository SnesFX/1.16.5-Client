/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ItemRenderer
implements ResourceManagerReloadListener {
    public static final ResourceLocation ENCHANT_GLINT_LOCATION = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final Set<Item> IGNORED = Sets.newHashSet((Object[])new Item[]{Items.AIR});
    public float blitOffset;
    private final ItemModelShaper itemModelShaper;
    private final TextureManager textureManager;
    private final ItemColors itemColors;

    public ItemRenderer(TextureManager textureManager, ModelManager modelManager, ItemColors itemColors) {
        this.textureManager = textureManager;
        this.itemModelShaper = new ItemModelShaper(modelManager);
        for (Item item : Registry.ITEM) {
            if (IGNORED.contains(item)) continue;
            this.itemModelShaper.register(item, new ModelResourceLocation(Registry.ITEM.getKey(item), "inventory"));
        }
        this.itemColors = itemColors;
    }

    public ItemModelShaper getItemModelShaper() {
        return this.itemModelShaper;
    }

    private void renderModelLists(BakedModel bakedModel, ItemStack itemStack, int n, int n2, PoseStack poseStack, VertexConsumer vertexConsumer) {
        Random random = new Random();
        long l = 42L;
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            this.renderQuadList(poseStack, vertexConsumer, bakedModel.getQuads(null, direction, random), itemStack, n, n2);
        }
        random.setSeed(42L);
        this.renderQuadList(poseStack, vertexConsumer, bakedModel.getQuads(null, null, random), itemStack, n, n2);
    }

    public void render(ItemStack itemStack, ItemTransforms.TransformType transformType, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int n, int n2, BakedModel bakedModel) {
        boolean bl2;
        if (itemStack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        boolean bl3 = bl2 = transformType == ItemTransforms.TransformType.GUI || transformType == ItemTransforms.TransformType.GROUND || transformType == ItemTransforms.TransformType.FIXED;
        if (itemStack.getItem() == Items.TRIDENT && bl2) {
            bakedModel = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
        }
        bakedModel.getTransforms().getTransform(transformType).apply(bl, poseStack);
        poseStack.translate(-0.5, -0.5, -0.5);
        if (bakedModel.isCustomRenderer() || itemStack.getItem() == Items.TRIDENT && !bl2) {
            BlockEntityWithoutLevelRenderer.instance.renderByItem(itemStack, transformType, poseStack, multiBufferSource, n, n2);
        } else {
            VertexConsumer vertexConsumer;
            Object object;
            boolean bl4 = transformType != ItemTransforms.TransformType.GUI && !transformType.firstPerson() && itemStack.getItem() instanceof BlockItem ? !((object = ((BlockItem)itemStack.getItem()).getBlock()) instanceof HalfTransparentBlock) && !(object instanceof StainedGlassPaneBlock) : true;
            object = ItemBlockRenderTypes.getRenderType(itemStack, bl4);
            if (itemStack.getItem() == Items.COMPASS && itemStack.hasFoil()) {
                poseStack.pushPose();
                PoseStack.Pose pose = poseStack.last();
                if (transformType == ItemTransforms.TransformType.GUI) {
                    pose.pose().multiply(0.5f);
                } else if (transformType.firstPerson()) {
                    pose.pose().multiply(0.75f);
                }
                vertexConsumer = bl4 ? ItemRenderer.getCompassFoilBufferDirect(multiBufferSource, (RenderType)object, pose) : ItemRenderer.getCompassFoilBuffer(multiBufferSource, (RenderType)object, pose);
                poseStack.popPose();
            } else {
                vertexConsumer = bl4 ? ItemRenderer.getFoilBufferDirect(multiBufferSource, (RenderType)object, true, itemStack.hasFoil()) : ItemRenderer.getFoilBuffer(multiBufferSource, (RenderType)object, true, itemStack.hasFoil());
            }
            this.renderModelLists(bakedModel, itemStack, n, n2, poseStack, vertexConsumer);
        }
        poseStack.popPose();
    }

    public static VertexConsumer getArmorFoilBuffer(MultiBufferSource multiBufferSource, RenderType renderType, boolean bl, boolean bl2) {
        if (bl2) {
            return VertexMultiConsumer.create(multiBufferSource.getBuffer(bl ? RenderType.armorGlint() : RenderType.armorEntityGlint()), multiBufferSource.getBuffer(renderType));
        }
        return multiBufferSource.getBuffer(renderType);
    }

    public static VertexConsumer getCompassFoilBuffer(MultiBufferSource multiBufferSource, RenderType renderType, PoseStack.Pose pose) {
        return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(multiBufferSource.getBuffer(RenderType.glint()), pose.pose(), pose.normal()), multiBufferSource.getBuffer(renderType));
    }

    public static VertexConsumer getCompassFoilBufferDirect(MultiBufferSource multiBufferSource, RenderType renderType, PoseStack.Pose pose) {
        return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(multiBufferSource.getBuffer(RenderType.glintDirect()), pose.pose(), pose.normal()), multiBufferSource.getBuffer(renderType));
    }

    public static VertexConsumer getFoilBuffer(MultiBufferSource multiBufferSource, RenderType renderType, boolean bl, boolean bl2) {
        if (bl2) {
            if (Minecraft.useShaderTransparency() && renderType == Sheets.translucentItemSheet()) {
                return VertexMultiConsumer.create(multiBufferSource.getBuffer(RenderType.glintTranslucent()), multiBufferSource.getBuffer(renderType));
            }
            return VertexMultiConsumer.create(multiBufferSource.getBuffer(bl ? RenderType.glint() : RenderType.entityGlint()), multiBufferSource.getBuffer(renderType));
        }
        return multiBufferSource.getBuffer(renderType);
    }

    public static VertexConsumer getFoilBufferDirect(MultiBufferSource multiBufferSource, RenderType renderType, boolean bl, boolean bl2) {
        if (bl2) {
            return VertexMultiConsumer.create(multiBufferSource.getBuffer(bl ? RenderType.glintDirect() : RenderType.entityGlintDirect()), multiBufferSource.getBuffer(renderType));
        }
        return multiBufferSource.getBuffer(renderType);
    }

    private void renderQuadList(PoseStack poseStack, VertexConsumer vertexConsumer, List<BakedQuad> list, ItemStack itemStack, int n, int n2) {
        boolean bl = !itemStack.isEmpty();
        PoseStack.Pose pose = poseStack.last();
        for (BakedQuad bakedQuad : list) {
            int n3 = -1;
            if (bl && bakedQuad.isTinted()) {
                n3 = this.itemColors.getColor(itemStack, bakedQuad.getTintIndex());
            }
            float f = (float)(n3 >> 16 & 0xFF) / 255.0f;
            float f2 = (float)(n3 >> 8 & 0xFF) / 255.0f;
            float f3 = (float)(n3 & 0xFF) / 255.0f;
            vertexConsumer.putBulkData(pose, bakedQuad, f, f2, f3, n, n2);
        }
    }

    public BakedModel getModel(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity livingEntity) {
        Item item = itemStack.getItem();
        BakedModel bakedModel = item == Items.TRIDENT ? this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory")) : this.itemModelShaper.getItemModel(itemStack);
        ClientLevel clientLevel = level instanceof ClientLevel ? (ClientLevel)level : null;
        BakedModel bakedModel2 = bakedModel.getOverrides().resolve(bakedModel, itemStack, clientLevel, livingEntity);
        return bakedModel2 == null ? this.itemModelShaper.getModelManager().getMissingModel() : bakedModel2;
    }

    public void renderStatic(ItemStack itemStack, ItemTransforms.TransformType transformType, int n, int n2, PoseStack poseStack, MultiBufferSource multiBufferSource) {
        this.renderStatic(null, itemStack, transformType, false, poseStack, multiBufferSource, null, n, n2);
    }

    public void renderStatic(@Nullable LivingEntity livingEntity, ItemStack itemStack, ItemTransforms.TransformType transformType, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, @Nullable Level level, int n, int n2) {
        if (itemStack.isEmpty()) {
            return;
        }
        BakedModel bakedModel = this.getModel(itemStack, level, livingEntity);
        this.render(itemStack, transformType, bl, poseStack, multiBufferSource, n, n2, bakedModel);
    }

    public void renderGuiItem(ItemStack itemStack, int n, int n2) {
        this.renderGuiItem(itemStack, n, n2, this.getModel(itemStack, null, null));
    }

    protected void renderGuiItem(ItemStack itemStack, int n, int n2, BakedModel bakedModel) {
        boolean bl;
        RenderSystem.pushMatrix();
        this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
        this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.translatef(n, n2, 100.0f + this.blitOffset);
        RenderSystem.translatef(8.0f, 8.0f, 0.0f);
        RenderSystem.scalef(1.0f, -1.0f, 1.0f);
        RenderSystem.scalef(16.0f, 16.0f, 16.0f);
        PoseStack poseStack = new PoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean bl2 = bl = !bakedModel.usesBlockLight();
        if (bl) {
            Lighting.setupForFlatItems();
        }
        this.render(itemStack, ItemTransforms.TransformType.GUI, false, poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
        bufferSource.endBatch();
        RenderSystem.enableDepthTest();
        if (bl) {
            Lighting.setupFor3DItems();
        }
        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }

    public void renderAndDecorateItem(ItemStack itemStack, int n, int n2) {
        this.tryRenderGuiItem(Minecraft.getInstance().player, itemStack, n, n2);
    }

    public void renderAndDecorateFakeItem(ItemStack itemStack, int n, int n2) {
        this.tryRenderGuiItem(null, itemStack, n, n2);
    }

    public void renderAndDecorateItem(LivingEntity livingEntity, ItemStack itemStack, int n, int n2) {
        this.tryRenderGuiItem(livingEntity, itemStack, n, n2);
    }

    private void tryRenderGuiItem(@Nullable LivingEntity livingEntity, ItemStack itemStack, int n, int n2) {
        if (itemStack.isEmpty()) {
            return;
        }
        this.blitOffset += 50.0f;
        try {
            this.renderGuiItem(itemStack, n, n2, this.getModel(itemStack, null, livingEntity));
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Rendering item");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Item being rendered");
            crashReportCategory.setDetail("Item Type", () -> String.valueOf(itemStack.getItem()));
            crashReportCategory.setDetail("Item Damage", () -> String.valueOf(itemStack.getDamageValue()));
            crashReportCategory.setDetail("Item NBT", () -> String.valueOf(itemStack.getTag()));
            crashReportCategory.setDetail("Item Foil", () -> String.valueOf(itemStack.hasFoil()));
            throw new ReportedException(crashReport);
        }
        this.blitOffset -= 50.0f;
    }

    public void renderGuiItemDecorations(Font font, ItemStack itemStack, int n, int n2) {
        this.renderGuiItemDecorations(font, itemStack, n, n2, null);
    }

    public void renderGuiItemDecorations(Font font, ItemStack itemStack, int n, int n2, @Nullable String string) {
        float f;
        Object object;
        Object object2;
        if (itemStack.isEmpty()) {
            return;
        }
        PoseStack poseStack = new PoseStack();
        if (itemStack.getCount() != 1 || string != null) {
            object = string == null ? String.valueOf(itemStack.getCount()) : string;
            poseStack.translate(0.0, 0.0, this.blitOffset + 200.0f);
            object2 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch((String)object, (float)(n + 19 - 2 - font.width((String)object)), (float)(n2 + 6 + 3), 16777215, true, poseStack.last().pose(), (MultiBufferSource)object2, false, 0, 15728880);
            ((MultiBufferSource.BufferSource)object2).endBatch();
        }
        if (itemStack.isDamaged()) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            object = Tesselator.getInstance();
            object2 = ((Tesselator)object).getBuilder();
            float f2 = itemStack.getDamageValue();
            float f3 = itemStack.getMaxDamage();
            float f4 = Math.max(0.0f, (f3 - f2) / f3);
            int n3 = Math.round(13.0f - f2 * 13.0f / f3);
            int n4 = Mth.hsvToRgb(f4 / 3.0f, 1.0f, 1.0f);
            this.fillRect((BufferBuilder)object2, n + 2, n2 + 13, 13, 2, 0, 0, 0, 255);
            this.fillRect((BufferBuilder)object2, n + 2, n2 + 13, n3, 1, n4 >> 16 & 0xFF, n4 >> 8 & 0xFF, n4 & 0xFF, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
        float f5 = f = (object = Minecraft.getInstance().player) == null ? 0.0f : ((Player)object).getCooldowns().getCooldownPercent(itemStack.getItem(), Minecraft.getInstance().getFrameTime());
        if (f > 0.0f) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            this.fillRect(bufferBuilder, n, n2 + Mth.floor(16.0f * (1.0f - f)), 16, Mth.ceil(16.0f * f), 255, 255, 255, 127);
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }

    private void fillRect(BufferBuilder bufferBuilder, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(n + 0, n2 + 0, 0.0).color(n5, n6, n7, n8).endVertex();
        bufferBuilder.vertex(n + 0, n2 + n4, 0.0).color(n5, n6, n7, n8).endVertex();
        bufferBuilder.vertex(n + n3, n2 + n4, 0.0).color(n5, n6, n7, n8).endVertex();
        bufferBuilder.vertex(n + n3, n2 + 0, 0.0).color(n5, n6, n7, n8).endVertex();
        Tesselator.getInstance().end();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.itemModelShaper.rebuildCache();
    }
}

