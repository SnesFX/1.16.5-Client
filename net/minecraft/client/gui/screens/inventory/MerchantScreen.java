/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class MerchantScreen
extends AbstractContainerScreen<MerchantMenu> {
    private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
    private static final Component TRADES_LABEL = new TranslatableComponent("merchant.trades");
    private static final Component LEVEL_SEPARATOR = new TextComponent(" - ");
    private static final Component DEPRECATED_TOOLTIP = new TranslatableComponent("merchant.deprecated");
    private int shopItem;
    private final TradeOfferButton[] tradeOfferButtons = new TradeOfferButton[7];
    private int scrollOff;
    private boolean isDragging;

    public MerchantScreen(MerchantMenu merchantMenu, Inventory inventory, Component component) {
        super(merchantMenu, inventory, component);
        this.imageWidth = 276;
        this.inventoryLabelX = 107;
    }

    private void postButtonClick() {
        ((MerchantMenu)this.menu).setSelectionHint(this.shopItem);
        ((MerchantMenu)this.menu).tryMoveItems(this.shopItem);
        this.minecraft.getConnection().send(new ServerboundSelectTradePacket(this.shopItem));
    }

    @Override
    protected void init() {
        super.init();
        int n = (this.width - this.imageWidth) / 2;
        int n2 = (this.height - this.imageHeight) / 2;
        int n3 = n2 + 16 + 2;
        for (int i = 0; i < 7; ++i) {
            this.tradeOfferButtons[i] = this.addButton(new TradeOfferButton(n + 5, n3, i, button -> {
                if (button instanceof TradeOfferButton) {
                    this.shopItem = ((TradeOfferButton)button).getIndex() + this.scrollOff;
                    this.postButtonClick();
                }
            }));
            n3 += 20;
        }
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int n, int n2) {
        int n3 = ((MerchantMenu)this.menu).getTraderLevel();
        if (n3 > 0 && n3 <= 5 && ((MerchantMenu)this.menu).showProgressBar()) {
            MutableComponent mutableComponent = this.title.copy().append(LEVEL_SEPARATOR).append(new TranslatableComponent("merchant.level." + n3));
            int n4 = this.font.width(mutableComponent);
            int n5 = 49 + this.imageWidth / 2 - n4 / 2;
            this.font.draw(poseStack, mutableComponent, (float)n5, 6.0f, 4210752);
        } else {
            this.font.draw(poseStack, this.title, (float)(49 + this.imageWidth / 2 - this.font.width(this.title) / 2), 6.0f, 4210752);
        }
        this.font.draw(poseStack, this.inventory.getDisplayName(), (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
        int n6 = this.font.width(TRADES_LABEL);
        this.font.draw(poseStack, TRADES_LABEL, (float)(5 - n6 / 2 + 48), 6.0f, 4210752);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int n, int n2) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
        int n3 = (this.width - this.imageWidth) / 2;
        int n4 = (this.height - this.imageHeight) / 2;
        MerchantScreen.blit(poseStack, n3, n4, this.getBlitOffset(), 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 512);
        MerchantOffers merchantOffers = ((MerchantMenu)this.menu).getOffers();
        if (!merchantOffers.isEmpty()) {
            int n5 = this.shopItem;
            if (n5 < 0 || n5 >= merchantOffers.size()) {
                return;
            }
            MerchantOffer merchantOffer = (MerchantOffer)merchantOffers.get(n5);
            if (merchantOffer.isOutOfStock()) {
                this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                MerchantScreen.blit(poseStack, this.leftPos + 83 + 99, this.topPos + 35, this.getBlitOffset(), 311.0f, 0.0f, 28, 21, 256, 512);
            }
        }
    }

    private void renderProgressBar(PoseStack poseStack, int n, int n2, MerchantOffer merchantOffer) {
        this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
        int n3 = ((MerchantMenu)this.menu).getTraderLevel();
        int n4 = ((MerchantMenu)this.menu).getTraderXp();
        if (n3 >= 5) {
            return;
        }
        MerchantScreen.blit(poseStack, n + 136, n2 + 16, this.getBlitOffset(), 0.0f, 186.0f, 102, 5, 256, 512);
        int n5 = VillagerData.getMinXpPerLevel(n3);
        if (n4 < n5 || !VillagerData.canLevelUp(n3)) {
            return;
        }
        int n6 = 100;
        float f = 100.0f / (float)(VillagerData.getMaxXpPerLevel(n3) - n5);
        int n7 = Math.min(Mth.floor(f * (float)(n4 - n5)), 100);
        MerchantScreen.blit(poseStack, n + 136, n2 + 16, this.getBlitOffset(), 0.0f, 191.0f, n7 + 1, 5, 256, 512);
        int n8 = ((MerchantMenu)this.menu).getFutureTraderXp();
        if (n8 > 0) {
            int n9 = Math.min(Mth.floor((float)n8 * f), 100 - n7);
            MerchantScreen.blit(poseStack, n + 136 + n7 + 1, n2 + 16 + 1, this.getBlitOffset(), 2.0f, 182.0f, n9, 3, 256, 512);
        }
    }

    private void renderScroller(PoseStack poseStack, int n, int n2, MerchantOffers merchantOffers) {
        int n3 = merchantOffers.size() + 1 - 7;
        if (n3 > 1) {
            int n4 = 139 - (27 + (n3 - 1) * 139 / n3);
            int n5 = 1 + n4 / n3 + 139 / n3;
            int n6 = 113;
            int n7 = Math.min(113, this.scrollOff * n5);
            if (this.scrollOff == n3 - 1) {
                n7 = 113;
            }
            MerchantScreen.blit(poseStack, n + 94, n2 + 18 + n7, this.getBlitOffset(), 0.0f, 199.0f, 6, 27, 256, 512);
        } else {
            MerchantScreen.blit(poseStack, n + 94, n2 + 18, this.getBlitOffset(), 6.0f, 199.0f, 6, 27, 256, 512);
        }
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        super.render(poseStack, n, n2, f);
        MerchantOffers merchantOffers = ((MerchantMenu)this.menu).getOffers();
        if (!merchantOffers.isEmpty()) {
            MerchantOffer merchantOffer22;
            int n3 = (this.width - this.imageWidth) / 2;
            int n4 = (this.height - this.imageHeight) / 2;
            int n5 = n4 + 16 + 1;
            int n6 = n3 + 5 + 5;
            RenderSystem.pushMatrix();
            RenderSystem.enableRescaleNormal();
            this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
            this.renderScroller(poseStack, n3, n4, merchantOffers);
            int n7 = 0;
            for (MerchantOffer merchantOffer22 : merchantOffers) {
                if (this.canScroll(merchantOffers.size()) && (n7 < this.scrollOff || n7 >= 7 + this.scrollOff)) {
                    ++n7;
                    continue;
                }
                TradeOfferButton[] arrtradeOfferButton = merchantOffer22.getBaseCostA();
                ItemStack itemStack = merchantOffer22.getCostA();
                ItemStack itemStack2 = merchantOffer22.getCostB();
                ItemStack object = merchantOffer22.getResult();
                this.itemRenderer.blitOffset = 100.0f;
                int n8 = n5 + 2;
                this.renderAndDecorateCostA(poseStack, itemStack, (ItemStack)arrtradeOfferButton, n6, n8);
                if (!itemStack2.isEmpty()) {
                    this.itemRenderer.renderAndDecorateFakeItem(itemStack2, n3 + 5 + 35, n8);
                    this.itemRenderer.renderGuiItemDecorations(this.font, itemStack2, n3 + 5 + 35, n8);
                }
                this.renderButtonArrows(poseStack, merchantOffer22, n3, n8);
                this.itemRenderer.renderAndDecorateFakeItem(object, n3 + 5 + 68, n8);
                this.itemRenderer.renderGuiItemDecorations(this.font, object, n3 + 5 + 68, n8);
                this.itemRenderer.blitOffset = 0.0f;
                n5 += 20;
                ++n7;
            }
            int n9 = this.shopItem;
            merchantOffer22 = (MerchantOffer)merchantOffers.get(n9);
            if (((MerchantMenu)this.menu).showProgressBar()) {
                this.renderProgressBar(poseStack, n3, n4, merchantOffer22);
            }
            if (merchantOffer22.isOutOfStock() && this.isHovering(186, 35, 22, 21, n, n2) && ((MerchantMenu)this.menu).canRestock()) {
                this.renderTooltip(poseStack, DEPRECATED_TOOLTIP, n, n2);
            }
            for (TradeOfferButton tradeOfferButton : this.tradeOfferButtons) {
                if (tradeOfferButton.isHovered()) {
                    tradeOfferButton.renderToolTip(poseStack, n, n2);
                }
                tradeOfferButton.visible = tradeOfferButton.index < ((MerchantMenu)this.menu).getOffers().size();
            }
            RenderSystem.popMatrix();
            RenderSystem.enableDepthTest();
        }
        this.renderTooltip(poseStack, n, n2);
    }

    private void renderButtonArrows(PoseStack poseStack, MerchantOffer merchantOffer, int n, int n2) {
        RenderSystem.enableBlend();
        this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
        if (merchantOffer.isOutOfStock()) {
            MerchantScreen.blit(poseStack, n + 5 + 35 + 20, n2 + 3, this.getBlitOffset(), 25.0f, 171.0f, 10, 9, 256, 512);
        } else {
            MerchantScreen.blit(poseStack, n + 5 + 35 + 20, n2 + 3, this.getBlitOffset(), 15.0f, 171.0f, 10, 9, 256, 512);
        }
    }

    private void renderAndDecorateCostA(PoseStack poseStack, ItemStack itemStack, ItemStack itemStack2, int n, int n2) {
        this.itemRenderer.renderAndDecorateFakeItem(itemStack, n, n2);
        if (itemStack2.getCount() == itemStack.getCount()) {
            this.itemRenderer.renderGuiItemDecorations(this.font, itemStack, n, n2);
        } else {
            this.itemRenderer.renderGuiItemDecorations(this.font, itemStack2, n, n2, itemStack2.getCount() == 1 ? "1" : null);
            this.itemRenderer.renderGuiItemDecorations(this.font, itemStack, n + 14, n2, itemStack.getCount() == 1 ? "1" : null);
            this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
            this.setBlitOffset(this.getBlitOffset() + 300);
            MerchantScreen.blit(poseStack, n + 7, n2 + 12, this.getBlitOffset(), 0.0f, 176.0f, 9, 2, 256, 512);
            this.setBlitOffset(this.getBlitOffset() - 300);
        }
    }

    private boolean canScroll(int n) {
        return n > 7;
    }

    @Override
    public boolean mouseScrolled(double d, double d2, double d3) {
        int n = ((MerchantMenu)this.menu).getOffers().size();
        if (this.canScroll(n)) {
            int n2 = n - 7;
            this.scrollOff = (int)((double)this.scrollOff - d3);
            this.scrollOff = Mth.clamp(this.scrollOff, 0, n2);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double d, double d2, int n, double d3, double d4) {
        int n2 = ((MerchantMenu)this.menu).getOffers().size();
        if (this.isDragging) {
            int n3 = this.topPos + 18;
            int n4 = n3 + 139;
            int n5 = n2 - 7;
            float f = ((float)d2 - (float)n3 - 13.5f) / ((float)(n4 - n3) - 27.0f);
            f = f * (float)n5 + 0.5f;
            this.scrollOff = Mth.clamp((int)f, 0, n5);
            return true;
        }
        return super.mouseDragged(d, d2, n, d3, d4);
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        this.isDragging = false;
        int n2 = (this.width - this.imageWidth) / 2;
        int n3 = (this.height - this.imageHeight) / 2;
        if (this.canScroll(((MerchantMenu)this.menu).getOffers().size()) && d > (double)(n2 + 94) && d < (double)(n2 + 94 + 6) && d2 > (double)(n3 + 18) && d2 <= (double)(n3 + 18 + 139 + 1)) {
            this.isDragging = true;
        }
        return super.mouseClicked(d, d2, n);
    }

    class TradeOfferButton
    extends Button {
        final int index;

        public TradeOfferButton(int n, int n2, int n3, Button.OnPress onPress) {
            super(n, n2, 89, 20, TextComponent.EMPTY, onPress);
            this.index = n3;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public void renderToolTip(PoseStack poseStack, int n, int n2) {
            if (this.isHovered && ((MerchantMenu)MerchantScreen.this.menu).getOffers().size() > this.index + MerchantScreen.this.scrollOff) {
                if (n < this.x + 20) {
                    ItemStack itemStack = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getCostA();
                    MerchantScreen.this.renderTooltip(poseStack, itemStack, n, n2);
                } else if (n < this.x + 50 && n > this.x + 30) {
                    ItemStack itemStack = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getCostB();
                    if (!itemStack.isEmpty()) {
                        MerchantScreen.this.renderTooltip(poseStack, itemStack, n, n2);
                    }
                } else if (n > this.x + 65) {
                    ItemStack itemStack = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getResult();
                    MerchantScreen.this.renderTooltip(poseStack, itemStack, n, n2);
                }
            }
        }
    }

}

