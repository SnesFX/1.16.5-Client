/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;

public class LoomScreen
extends AbstractContainerScreen<LoomMenu> {
    private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/loom.png");
    private static final int TOTAL_PATTERN_ROWS = (BannerPattern.COUNT - BannerPattern.PATTERN_ITEM_COUNT - 1 + 4 - 1) / 4;
    private final ModelPart flag;
    @Nullable
    private List<Pair<BannerPattern, DyeColor>> resultBannerPatterns;
    private ItemStack bannerStack = ItemStack.EMPTY;
    private ItemStack dyeStack = ItemStack.EMPTY;
    private ItemStack patternStack = ItemStack.EMPTY;
    private boolean displayPatterns;
    private boolean displaySpecialPattern;
    private boolean hasMaxPatterns;
    private float scrollOffs;
    private boolean scrolling;
    private int startIndex = 1;

    public LoomScreen(LoomMenu loomMenu, Inventory inventory, Component component) {
        super(loomMenu, inventory, component);
        this.flag = BannerRenderer.makeFlag();
        loomMenu.registerUpdateListener(this::containerChanged);
        this.titleLabelY -= 2;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        super.render(poseStack, n, n2, f);
        this.renderTooltip(poseStack, n, n2);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int n, int n2) {
        this.renderBackground(poseStack);
        this.minecraft.getTextureManager().bind(BG_LOCATION);
        int n3 = this.leftPos;
        int n4 = this.topPos;
        this.blit(poseStack, n3, n4, 0, 0, this.imageWidth, this.imageHeight);
        Slot slot = ((LoomMenu)this.menu).getBannerSlot();
        Slot slot2 = ((LoomMenu)this.menu).getDyeSlot();
        Slot slot3 = ((LoomMenu)this.menu).getPatternSlot();
        Slot slot4 = ((LoomMenu)this.menu).getResultSlot();
        if (!slot.hasItem()) {
            this.blit(poseStack, n3 + slot.x, n4 + slot.y, this.imageWidth, 0, 16, 16);
        }
        if (!slot2.hasItem()) {
            this.blit(poseStack, n3 + slot2.x, n4 + slot2.y, this.imageWidth + 16, 0, 16, 16);
        }
        if (!slot3.hasItem()) {
            this.blit(poseStack, n3 + slot3.x, n4 + slot3.y, this.imageWidth + 32, 0, 16, 16);
        }
        int n5 = (int)(41.0f * this.scrollOffs);
        this.blit(poseStack, n3 + 119, n4 + 13 + n5, 232 + (this.displayPatterns ? 0 : 12), 0, 12, 15);
        Lighting.setupForFlatItems();
        if (this.resultBannerPatterns != null && !this.hasMaxPatterns) {
            MultiBufferSource.BufferSource bufferSource = this.minecraft.renderBuffers().bufferSource();
            poseStack.pushPose();
            poseStack.translate(n3 + 139, n4 + 52, 0.0);
            poseStack.scale(24.0f, -24.0f, 1.0f);
            poseStack.translate(0.5, 0.5, 0.5);
            float f2 = 0.6666667f;
            poseStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
            this.flag.xRot = 0.0f;
            this.flag.y = -32.0f;
            BannerRenderer.renderPatterns(poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, this.resultBannerPatterns);
            poseStack.popPose();
            bufferSource.endBatch();
        } else if (this.hasMaxPatterns) {
            this.blit(poseStack, n3 + slot4.x - 2, n4 + slot4.y - 2, this.imageWidth, 17, 17, 16);
        }
        if (this.displayPatterns) {
            int n6 = n3 + 60;
            int n7 = n4 + 13;
            int n8 = this.startIndex + 16;
            for (int i = this.startIndex; i < n8 && i < BannerPattern.COUNT - BannerPattern.PATTERN_ITEM_COUNT; ++i) {
                int n9 = i - this.startIndex;
                int n10 = n6 + n9 % 4 * 14;
                int n11 = n7 + n9 / 4 * 14;
                this.minecraft.getTextureManager().bind(BG_LOCATION);
                int n12 = this.imageHeight;
                if (i == ((LoomMenu)this.menu).getSelectedBannerPatternIndex()) {
                    n12 += 14;
                } else if (n >= n10 && n2 >= n11 && n < n10 + 14 && n2 < n11 + 14) {
                    n12 += 28;
                }
                this.blit(poseStack, n10, n11, 0, n12, 14, 14);
                this.renderPattern(i, n10, n11);
            }
        } else if (this.displaySpecialPattern) {
            int n13 = n3 + 60;
            int n14 = n4 + 13;
            this.minecraft.getTextureManager().bind(BG_LOCATION);
            this.blit(poseStack, n13, n14, 0, this.imageHeight, 14, 14);
            int n15 = ((LoomMenu)this.menu).getSelectedBannerPatternIndex();
            this.renderPattern(n15, n13, n14);
        }
        Lighting.setupFor3DItems();
    }

    private void renderPattern(int n, int n2, int n3) {
        ItemStack itemStack = new ItemStack(Items.GRAY_BANNER);
        CompoundTag compoundTag = itemStack.getOrCreateTagElement("BlockEntityTag");
        ListTag listTag = new BannerPattern.Builder().addPattern(BannerPattern.BASE, DyeColor.GRAY).addPattern(BannerPattern.values()[n], DyeColor.WHITE).toListTag();
        compoundTag.put("Patterns", listTag);
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate((float)n2 + 0.5f, n3 + 16, 0.0);
        poseStack.scale(6.0f, -6.0f, 1.0f);
        poseStack.translate(0.5, 0.5, 0.0);
        poseStack.translate(0.5, 0.5, 0.5);
        float f = 0.6666667f;
        poseStack.scale(0.6666667f, -0.6666667f, -0.6666667f);
        MultiBufferSource.BufferSource bufferSource = this.minecraft.renderBuffers().bufferSource();
        this.flag.xRot = 0.0f;
        this.flag.y = -32.0f;
        List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.createPatterns(DyeColor.GRAY, BannerBlockEntity.getItemPatterns(itemStack));
        BannerRenderer.renderPatterns(poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, list);
        poseStack.popPose();
        bufferSource.endBatch();
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        this.scrolling = false;
        if (this.displayPatterns) {
            int n2 = this.leftPos + 60;
            int n3 = this.topPos + 13;
            int n4 = this.startIndex + 16;
            for (int i = this.startIndex; i < n4; ++i) {
                int n5 = i - this.startIndex;
                double d3 = d - (double)(n2 + n5 % 4 * 14);
                double d4 = d2 - (double)(n3 + n5 / 4 * 14);
                if (!(d3 >= 0.0) || !(d4 >= 0.0) || !(d3 < 14.0) || !(d4 < 14.0) || !((LoomMenu)this.menu).clickMenuButton(this.minecraft.player, i)) continue;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0f));
                this.minecraft.gameMode.handleInventoryButtonClick(((LoomMenu)this.menu).containerId, i);
                return true;
            }
            n2 = this.leftPos + 119;
            n3 = this.topPos + 9;
            if (d >= (double)n2 && d < (double)(n2 + 12) && d2 >= (double)n3 && d2 < (double)(n3 + 56)) {
                this.scrolling = true;
            }
        }
        return super.mouseClicked(d, d2, n);
    }

    @Override
    public boolean mouseDragged(double d, double d2, int n, double d3, double d4) {
        if (this.scrolling && this.displayPatterns) {
            int n2 = this.topPos + 13;
            int n3 = n2 + 56;
            this.scrollOffs = ((float)d2 - (float)n2 - 7.5f) / ((float)(n3 - n2) - 15.0f);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            int n4 = TOTAL_PATTERN_ROWS - 4;
            int n5 = (int)((double)(this.scrollOffs * (float)n4) + 0.5);
            if (n5 < 0) {
                n5 = 0;
            }
            this.startIndex = 1 + n5 * 4;
            return true;
        }
        return super.mouseDragged(d, d2, n, d3, d4);
    }

    @Override
    public boolean mouseScrolled(double d, double d2, double d3) {
        if (this.displayPatterns) {
            int n = TOTAL_PATTERN_ROWS - 4;
            this.scrollOffs = (float)((double)this.scrollOffs - d3 / (double)n);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            this.startIndex = 1 + (int)((double)(this.scrollOffs * (float)n) + 0.5) * 4;
        }
        return true;
    }

    @Override
    protected boolean hasClickedOutside(double d, double d2, int n, int n2, int n3) {
        return d < (double)n || d2 < (double)n2 || d >= (double)(n + this.imageWidth) || d2 >= (double)(n2 + this.imageHeight);
    }

    private void containerChanged() {
        ItemStack itemStack = ((LoomMenu)this.menu).getResultSlot().getItem();
        this.resultBannerPatterns = itemStack.isEmpty() ? null : BannerBlockEntity.createPatterns(((BannerItem)itemStack.getItem()).getColor(), BannerBlockEntity.getItemPatterns(itemStack));
        ItemStack itemStack2 = ((LoomMenu)this.menu).getBannerSlot().getItem();
        ItemStack itemStack3 = ((LoomMenu)this.menu).getDyeSlot().getItem();
        ItemStack itemStack4 = ((LoomMenu)this.menu).getPatternSlot().getItem();
        CompoundTag compoundTag = itemStack2.getOrCreateTagElement("BlockEntityTag");
        boolean bl = this.hasMaxPatterns = compoundTag.contains("Patterns", 9) && !itemStack2.isEmpty() && compoundTag.getList("Patterns", 10).size() >= 6;
        if (this.hasMaxPatterns) {
            this.resultBannerPatterns = null;
        }
        if (!(ItemStack.matches(itemStack2, this.bannerStack) && ItemStack.matches(itemStack3, this.dyeStack) && ItemStack.matches(itemStack4, this.patternStack))) {
            this.displayPatterns = !itemStack2.isEmpty() && !itemStack3.isEmpty() && itemStack4.isEmpty() && !this.hasMaxPatterns;
            this.displaySpecialPattern = !this.hasMaxPatterns && !itemStack4.isEmpty() && !itemStack2.isEmpty() && !itemStack3.isEmpty();
        }
        this.bannerStack = itemStack2.copy();
        this.dyeStack = itemStack3.copy();
        this.patternStack = itemStack4.copy();
    }
}

