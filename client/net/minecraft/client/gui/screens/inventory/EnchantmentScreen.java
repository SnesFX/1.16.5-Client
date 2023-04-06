/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentScreen
extends AbstractContainerScreen<EnchantmentMenu> {
    private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");
    private static final ResourceLocation ENCHANTING_BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
    private static final BookModel BOOK_MODEL = new BookModel();
    private final Random random = new Random();
    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;

    public EnchantmentScreen(EnchantmentMenu enchantmentMenu, Inventory inventory, Component component) {
        super(enchantmentMenu, inventory, component);
    }

    @Override
    public void tick() {
        super.tick();
        this.tickBook();
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        int n2 = (this.width - this.imageWidth) / 2;
        int n3 = (this.height - this.imageHeight) / 2;
        for (int i = 0; i < 3; ++i) {
            double d3 = d - (double)(n2 + 60);
            double d4 = d2 - (double)(n3 + 14 + 19 * i);
            if (!(d3 >= 0.0) || !(d4 >= 0.0) || !(d3 < 108.0) || !(d4 < 19.0) || !((EnchantmentMenu)this.menu).clickMenuButton(this.minecraft.player, i)) continue;
            this.minecraft.gameMode.handleInventoryButtonClick(((EnchantmentMenu)this.menu).containerId, i);
            return true;
        }
        return super.mouseClicked(d, d2, n);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int n, int n2) {
        Lighting.setupForFlatItems();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(ENCHANTING_TABLE_LOCATION);
        int n3 = (this.width - this.imageWidth) / 2;
        int n4 = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, n3, n4, 0, 0, this.imageWidth, this.imageHeight);
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        int n5 = (int)this.minecraft.getWindow().getGuiScale();
        RenderSystem.viewport((this.width - 320) / 2 * n5, (this.height - 240) / 2 * n5, 320 * n5, 240 * n5);
        RenderSystem.translatef(-0.34f, 0.23f, 0.0f);
        RenderSystem.multMatrix(Matrix4f.perspective(90.0, 1.3333334f, 9.0f, 80.0f));
        RenderSystem.matrixMode(5888);
        poseStack.pushPose();
        PoseStack.Pose pose = poseStack.last();
        pose.pose().setIdentity();
        pose.normal().setIdentity();
        poseStack.translate(0.0, 3.299999952316284, 1984.0);
        float f2 = 5.0f;
        poseStack.scale(5.0f, 5.0f, 5.0f);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(20.0f));
        float f3 = Mth.lerp(f, this.oOpen, this.open);
        poseStack.translate((1.0f - f3) * 0.2f, (1.0f - f3) * 0.1f, (1.0f - f3) * 0.25f);
        float f4 = -(1.0f - f3) * 90.0f - 90.0f;
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f4));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
        float f5 = Mth.lerp(f, this.oFlip, this.flip) + 0.25f;
        float f6 = Mth.lerp(f, this.oFlip, this.flip) + 0.75f;
        f5 = (f5 - (float)Mth.fastFloor(f5)) * 1.6f - 0.3f;
        f6 = (f6 - (float)Mth.fastFloor(f6)) * 1.6f - 0.3f;
        if (f5 < 0.0f) {
            f5 = 0.0f;
        }
        if (f6 < 0.0f) {
            f6 = 0.0f;
        }
        if (f5 > 1.0f) {
            f5 = 1.0f;
        }
        if (f6 > 1.0f) {
            f6 = 1.0f;
        }
        RenderSystem.enableRescaleNormal();
        BOOK_MODEL.setupAnim(0.0f, f5, f6, f3);
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        VertexConsumer vertexConsumer = bufferSource.getBuffer(BOOK_MODEL.renderType(ENCHANTING_BOOK_LOCATION));
        BOOK_MODEL.renderToBuffer(poseStack, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        bufferSource.endBatch();
        poseStack.popPose();
        RenderSystem.matrixMode(5889);
        RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        Lighting.setupFor3DItems();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        EnchantmentNames.getInstance().initSeed(((EnchantmentMenu)this.menu).getEnchantmentSeed());
        int n6 = ((EnchantmentMenu)this.menu).getGoldCount();
        for (int i = 0; i < 3; ++i) {
            int n7 = n3 + 60;
            int n8 = n7 + 20;
            this.setBlitOffset(0);
            this.minecraft.getTextureManager().bind(ENCHANTING_TABLE_LOCATION);
            int n9 = ((EnchantmentMenu)this.menu).costs[i];
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (n9 == 0) {
                this.blit(poseStack, n7, n4 + 14 + 19 * i, 0, 185, 108, 19);
                continue;
            }
            String string = "" + n9;
            int n10 = 86 - this.font.width(string);
            FormattedText formattedText = EnchantmentNames.getInstance().getRandomName(this.font, n10);
            int n11 = 6839882;
            if (!(n6 >= i + 1 && this.minecraft.player.experienceLevel >= n9 || this.minecraft.player.abilities.instabuild)) {
                this.blit(poseStack, n7, n4 + 14 + 19 * i, 0, 185, 108, 19);
                this.blit(poseStack, n7 + 1, n4 + 15 + 19 * i, 16 * i, 239, 16, 16);
                this.font.drawWordWrap(formattedText, n8, n4 + 16 + 19 * i, n10, (n11 & 0xFEFEFE) >> 1);
                n11 = 4226832;
            } else {
                int n12 = n - (n3 + 60);
                int n13 = n2 - (n4 + 14 + 19 * i);
                if (n12 >= 0 && n13 >= 0 && n12 < 108 && n13 < 19) {
                    this.blit(poseStack, n7, n4 + 14 + 19 * i, 0, 204, 108, 19);
                    n11 = 16777088;
                } else {
                    this.blit(poseStack, n7, n4 + 14 + 19 * i, 0, 166, 108, 19);
                }
                this.blit(poseStack, n7 + 1, n4 + 15 + 19 * i, 16 * i, 223, 16, 16);
                this.font.drawWordWrap(formattedText, n8, n4 + 16 + 19 * i, n10, n11);
                n11 = 8453920;
            }
            this.font.drawShadow(poseStack, string, (float)(n8 + 86 - this.font.width(string)), (float)(n4 + 16 + 19 * i + 7), n11);
        }
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        f = this.minecraft.getFrameTime();
        this.renderBackground(poseStack);
        super.render(poseStack, n, n2, f);
        this.renderTooltip(poseStack, n, n2);
        boolean bl = this.minecraft.player.abilities.instabuild;
        int n3 = ((EnchantmentMenu)this.menu).getGoldCount();
        for (int i = 0; i < 3; ++i) {
            int n4 = ((EnchantmentMenu)this.menu).costs[i];
            Enchantment enchantment = Enchantment.byId(((EnchantmentMenu)this.menu).enchantClue[i]);
            int n5 = ((EnchantmentMenu)this.menu).levelClue[i];
            int n6 = i + 1;
            if (!this.isHovering(60, 14 + 19 * i, 108, 17, n, n2) || n4 <= 0 || n5 < 0 || enchantment == null) continue;
            ArrayList arrayList = Lists.newArrayList();
            arrayList.add(new TranslatableComponent("container.enchant.clue", enchantment.getFullname(n5)).withStyle(ChatFormatting.WHITE));
            if (!bl) {
                arrayList.add(TextComponent.EMPTY);
                if (this.minecraft.player.experienceLevel < n4) {
                    arrayList.add(new TranslatableComponent("container.enchant.level.requirement", ((EnchantmentMenu)this.menu).costs[i]).withStyle(ChatFormatting.RED));
                } else {
                    TranslatableComponent translatableComponent = n6 == 1 ? new TranslatableComponent("container.enchant.lapis.one") : new TranslatableComponent("container.enchant.lapis.many", n6);
                    arrayList.add(translatableComponent.withStyle(n3 >= n6 ? ChatFormatting.GRAY : ChatFormatting.RED));
                    TranslatableComponent translatableComponent2 = n6 == 1 ? new TranslatableComponent("container.enchant.level.one") : new TranslatableComponent("container.enchant.level.many", n6);
                    arrayList.add(translatableComponent2.withStyle(ChatFormatting.GRAY));
                }
            }
            this.renderComponentTooltip(poseStack, arrayList, n, n2);
            break;
        }
    }

    public void tickBook() {
        ItemStack itemStack = ((EnchantmentMenu)this.menu).getSlot(0).getItem();
        if (!ItemStack.matches(itemStack, this.last)) {
            this.last = itemStack;
            do {
                this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.flip <= this.flipT + 1.0f && this.flip >= this.flipT - 1.0f);
        }
        ++this.time;
        this.oFlip = this.flip;
        this.oOpen = this.open;
        boolean bl = false;
        for (int i = 0; i < 3; ++i) {
            if (((EnchantmentMenu)this.menu).costs[i] == 0) continue;
            bl = true;
        }
        this.open = bl ? (this.open += 0.2f) : (this.open -= 0.2f);
        this.open = Mth.clamp(this.open, 0.0f, 1.0f);
        float f = (this.flipT - this.flip) * 0.4f;
        float f2 = 0.2f;
        f = Mth.clamp(f, -0.2f, 0.2f);
        this.flipA += (f - this.flipA) * 0.9f;
        this.flip += this.flipA;
    }
}

