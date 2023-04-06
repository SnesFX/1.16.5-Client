/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class AdvancementTab
extends GuiComponent {
    private final Minecraft minecraft;
    private final AdvancementsScreen screen;
    private final AdvancementTabType type;
    private final int index;
    private final Advancement advancement;
    private final DisplayInfo display;
    private final ItemStack icon;
    private final Component title;
    private final AdvancementWidget root;
    private final Map<Advancement, AdvancementWidget> widgets = Maps.newLinkedHashMap();
    private double scrollX;
    private double scrollY;
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;
    private float fade;
    private boolean centered;

    public AdvancementTab(Minecraft minecraft, AdvancementsScreen advancementsScreen, AdvancementTabType advancementTabType, int n, Advancement advancement, DisplayInfo displayInfo) {
        this.minecraft = minecraft;
        this.screen = advancementsScreen;
        this.type = advancementTabType;
        this.index = n;
        this.advancement = advancement;
        this.display = displayInfo;
        this.icon = displayInfo.getIcon();
        this.title = displayInfo.getTitle();
        this.root = new AdvancementWidget(this, minecraft, advancement, displayInfo);
        this.addWidget(this.root, advancement);
    }

    public Advancement getAdvancement() {
        return this.advancement;
    }

    public Component getTitle() {
        return this.title;
    }

    public void drawTab(PoseStack poseStack, int n, int n2, boolean bl) {
        this.type.draw(poseStack, this, n, n2, bl, this.index);
    }

    public void drawIcon(int n, int n2, ItemRenderer itemRenderer) {
        this.type.drawIcon(n, n2, this.index, itemRenderer, this.icon);
    }

    public void drawContents(PoseStack poseStack) {
        if (!this.centered) {
            this.scrollX = 117 - (this.maxX + this.minX) / 2;
            this.scrollY = 56 - (this.maxY + this.minY) / 2;
            this.centered = true;
        }
        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.translatef(0.0f, 0.0f, 950.0f);
        RenderSystem.colorMask(false, false, false, false);
        AdvancementTab.fill(poseStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0f, 0.0f, -950.0f);
        RenderSystem.depthFunc(518);
        AdvancementTab.fill(poseStack, 234, 113, 0, 0, -16777216);
        RenderSystem.depthFunc(515);
        ResourceLocation resourceLocation = this.display.getBackground();
        if (resourceLocation != null) {
            this.minecraft.getTextureManager().bind(resourceLocation);
        } else {
            this.minecraft.getTextureManager().bind(TextureManager.INTENTIONAL_MISSING_TEXTURE);
        }
        int n = Mth.floor(this.scrollX);
        int n2 = Mth.floor(this.scrollY);
        int n3 = n % 16;
        int n4 = n2 % 16;
        for (int i = -1; i <= 15; ++i) {
            for (int j = -1; j <= 8; ++j) {
                AdvancementTab.blit(poseStack, n3 + 16 * i, n4 + 16 * j, 0.0f, 0.0f, 16, 16, 16, 16);
            }
        }
        this.root.drawConnectivity(poseStack, n, n2, true);
        this.root.drawConnectivity(poseStack, n, n2, false);
        this.root.draw(poseStack, n, n2);
        RenderSystem.depthFunc(518);
        RenderSystem.translatef(0.0f, 0.0f, -950.0f);
        RenderSystem.colorMask(false, false, false, false);
        AdvancementTab.fill(poseStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0f, 0.0f, 950.0f);
        RenderSystem.depthFunc(515);
        RenderSystem.popMatrix();
    }

    public void drawTooltips(PoseStack poseStack, int n, int n2, int n3, int n4) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.0f, 200.0f);
        AdvancementTab.fill(poseStack, 0, 0, 234, 113, Mth.floor(this.fade * 255.0f) << 24);
        boolean bl = false;
        int n5 = Mth.floor(this.scrollX);
        int n6 = Mth.floor(this.scrollY);
        if (n > 0 && n < 234 && n2 > 0 && n2 < 113) {
            for (AdvancementWidget advancementWidget : this.widgets.values()) {
                if (!advancementWidget.isMouseOver(n5, n6, n, n2)) continue;
                bl = true;
                advancementWidget.drawHover(poseStack, n5, n6, this.fade, n3, n4);
                break;
            }
        }
        RenderSystem.popMatrix();
        this.fade = bl ? Mth.clamp(this.fade + 0.02f, 0.0f, 0.3f) : Mth.clamp(this.fade - 0.04f, 0.0f, 1.0f);
    }

    public boolean isMouseOver(int n, int n2, double d, double d2) {
        return this.type.isMouseOver(n, n2, this.index, d, d2);
    }

    @Nullable
    public static AdvancementTab create(Minecraft minecraft, AdvancementsScreen advancementsScreen, int n, Advancement advancement) {
        if (advancement.getDisplay() == null) {
            return null;
        }
        for (AdvancementTabType advancementTabType : AdvancementTabType.values()) {
            if (n >= advancementTabType.getMax()) {
                n -= advancementTabType.getMax();
                continue;
            }
            return new AdvancementTab(minecraft, advancementsScreen, advancementTabType, n, advancement, advancement.getDisplay());
        }
        return null;
    }

    public void scroll(double d, double d2) {
        if (this.maxX - this.minX > 234) {
            this.scrollX = Mth.clamp(this.scrollX + d, (double)(-(this.maxX - 234)), 0.0);
        }
        if (this.maxY - this.minY > 113) {
            this.scrollY = Mth.clamp(this.scrollY + d2, (double)(-(this.maxY - 113)), 0.0);
        }
    }

    public void addAdvancement(Advancement advancement) {
        if (advancement.getDisplay() == null) {
            return;
        }
        AdvancementWidget advancementWidget = new AdvancementWidget(this, this.minecraft, advancement, advancement.getDisplay());
        this.addWidget(advancementWidget, advancement);
    }

    private void addWidget(AdvancementWidget advancementWidget, Advancement advancement) {
        this.widgets.put(advancement, advancementWidget);
        int n = advancementWidget.getX();
        int n2 = n + 28;
        int n3 = advancementWidget.getY();
        int n4 = n3 + 27;
        this.minX = Math.min(this.minX, n);
        this.maxX = Math.max(this.maxX, n2);
        this.minY = Math.min(this.minY, n3);
        this.maxY = Math.max(this.maxY, n4);
        for (AdvancementWidget advancementWidget2 : this.widgets.values()) {
            advancementWidget2.attachToParent();
        }
    }

    @Nullable
    public AdvancementWidget getWidget(Advancement advancement) {
        return this.widgets.get(advancement);
    }

    public AdvancementsScreen getScreen() {
        return this.screen;
    }
}

