/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 */
package net.minecraft.client.gui.screens.packs;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.util.FormattedCharSequence;

public class TransferableSelectionList
extends ObjectSelectionList<PackEntry> {
    private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/resource_packs.png");
    private static final Component INCOMPATIBLE_TITLE = new TranslatableComponent("pack.incompatible");
    private static final Component INCOMPATIBLE_CONFIRM_TITLE = new TranslatableComponent("pack.incompatible.confirm.title");
    private final Component title;

    public TransferableSelectionList(Minecraft minecraft, int n, int n2, Component component) {
        super(minecraft, n, n2, 32, n2 - 55 + 4, 36);
        this.title = component;
        this.centerListVertically = false;
        minecraft.font.getClass();
        this.setRenderHeader(true, (int)(9.0f * 1.5f));
    }

    @Override
    protected void renderHeader(PoseStack poseStack, int n, int n2, Tesselator tesselator) {
        MutableComponent mutableComponent = new TextComponent("").append(this.title).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
        this.minecraft.font.draw(poseStack, mutableComponent, (float)(n + this.width / 2 - this.minecraft.font.width(mutableComponent) / 2), (float)Math.min(this.y0 + 3, n2), 16777215);
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x1 - 6;
    }

    public static class PackEntry
    extends ObjectSelectionList.Entry<PackEntry> {
        private TransferableSelectionList parent;
        protected final Minecraft minecraft;
        protected final Screen screen;
        private final PackSelectionModel.Entry pack;
        private final FormattedCharSequence nameDisplayCache;
        private final MultiLineLabel descriptionDisplayCache;
        private final FormattedCharSequence incompatibleNameDisplayCache;
        private final MultiLineLabel incompatibleDescriptionDisplayCache;

        public PackEntry(Minecraft minecraft, TransferableSelectionList transferableSelectionList, Screen screen, PackSelectionModel.Entry entry) {
            this.minecraft = minecraft;
            this.screen = screen;
            this.pack = entry;
            this.parent = transferableSelectionList;
            this.nameDisplayCache = PackEntry.cacheName(minecraft, entry.getTitle());
            this.descriptionDisplayCache = PackEntry.cacheDescription(minecraft, entry.getExtendedDescription());
            this.incompatibleNameDisplayCache = PackEntry.cacheName(minecraft, INCOMPATIBLE_TITLE);
            this.incompatibleDescriptionDisplayCache = PackEntry.cacheDescription(minecraft, entry.getCompatibility().getDescription());
        }

        private static FormattedCharSequence cacheName(Minecraft minecraft, Component component) {
            int n = minecraft.font.width(component);
            if (n > 157) {
                FormattedText formattedText = FormattedText.composite(minecraft.font.substrByWidth(component, 157 - minecraft.font.width("...")), FormattedText.of("..."));
                return Language.getInstance().getVisualOrder(formattedText);
            }
            return component.getVisualOrderText();
        }

        private static MultiLineLabel cacheDescription(Minecraft minecraft, Component component) {
            return MultiLineLabel.create(minecraft.font, (FormattedText)component, 157, 2);
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            PackCompatibility packCompatibility = this.pack.getCompatibility();
            if (!packCompatibility.isCompatible()) {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                GuiComponent.fill(poseStack, n3 - 1, n2 - 1, n3 + n4 - 9, n2 + n5 + 1, -8978432);
            }
            this.minecraft.getTextureManager().bind(this.pack.getIconTexture());
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GuiComponent.blit(poseStack, n3, n2, 0.0f, 0.0f, 32, 32, 32, 32);
            FormattedCharSequence formattedCharSequence = this.nameDisplayCache;
            MultiLineLabel multiLineLabel = this.descriptionDisplayCache;
            if (this.showHoverOverlay() && (this.minecraft.options.touchscreen || bl)) {
                this.minecraft.getTextureManager().bind(ICON_OVERLAY_LOCATION);
                GuiComponent.fill(poseStack, n3, n2, n3 + 32, n2 + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                int n8 = n6 - n3;
                int n9 = n7 - n2;
                if (!this.pack.getCompatibility().isCompatible()) {
                    formattedCharSequence = this.incompatibleNameDisplayCache;
                    multiLineLabel = this.incompatibleDescriptionDisplayCache;
                }
                if (this.pack.canSelect()) {
                    if (n8 < 32) {
                        GuiComponent.blit(poseStack, n3, n2, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        GuiComponent.blit(poseStack, n3, n2, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                } else {
                    if (this.pack.canUnselect()) {
                        if (n8 < 16) {
                            GuiComponent.blit(poseStack, n3, n2, 32.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            GuiComponent.blit(poseStack, n3, n2, 32.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.pack.canMoveUp()) {
                        if (n8 < 32 && n8 > 16 && n9 < 16) {
                            GuiComponent.blit(poseStack, n3, n2, 96.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            GuiComponent.blit(poseStack, n3, n2, 96.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.pack.canMoveDown()) {
                        if (n8 < 32 && n8 > 16 && n9 > 16) {
                            GuiComponent.blit(poseStack, n3, n2, 64.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            GuiComponent.blit(poseStack, n3, n2, 64.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                }
            }
            this.minecraft.font.drawShadow(poseStack, formattedCharSequence, (float)(n3 + 32 + 2), (float)(n2 + 1), 16777215);
            multiLineLabel.renderLeftAligned(poseStack, n3 + 32 + 2, n2 + 12, 10, 8421504);
        }

        private boolean showHoverOverlay() {
            return !this.pack.isFixedPosition() || !this.pack.isRequired();
        }

        @Override
        public boolean mouseClicked(double d, double d2, int n) {
            double d3 = d - (double)this.parent.getRowLeft();
            double d4 = d2 - (double)this.parent.getRowTop(this.parent.children().indexOf(this));
            if (this.showHoverOverlay() && d3 <= 32.0) {
                if (this.pack.canSelect()) {
                    PackCompatibility packCompatibility = this.pack.getCompatibility();
                    if (packCompatibility.isCompatible()) {
                        this.pack.select();
                    } else {
                        Component component = packCompatibility.getConfirmation();
                        this.minecraft.setScreen(new ConfirmScreen(bl -> {
                            this.minecraft.setScreen(this.screen);
                            if (bl) {
                                this.pack.select();
                            }
                        }, INCOMPATIBLE_CONFIRM_TITLE, component));
                    }
                    return true;
                }
                if (d3 < 16.0 && this.pack.canUnselect()) {
                    this.pack.unselect();
                    return true;
                }
                if (d3 > 16.0 && d4 < 16.0 && this.pack.canMoveUp()) {
                    this.pack.moveUp();
                    return true;
                }
                if (d3 > 16.0 && d4 > 16.0 && this.pack.canMoveDown()) {
                    this.pack.moveDown();
                    return true;
                }
            }
            return false;
        }
    }

}

