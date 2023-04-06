/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components.spectator;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuListener;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SpectatorGui
extends GuiComponent
implements SpectatorMenuListener {
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    public static final ResourceLocation SPECTATOR_LOCATION = new ResourceLocation("textures/gui/spectator_widgets.png");
    private final Minecraft minecraft;
    private long lastSelectionTime;
    private SpectatorMenu menu;

    public SpectatorGui(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void onHotbarSelected(int n) {
        this.lastSelectionTime = Util.getMillis();
        if (this.menu != null) {
            this.menu.selectSlot(n);
        } else {
            this.menu = new SpectatorMenu(this);
        }
    }

    private float getHotbarAlpha() {
        long l = this.lastSelectionTime - Util.getMillis() + 5000L;
        return Mth.clamp((float)l / 2000.0f, 0.0f, 1.0f);
    }

    public void renderHotbar(PoseStack poseStack, float f) {
        if (this.menu == null) {
            return;
        }
        float f2 = this.getHotbarAlpha();
        if (f2 <= 0.0f) {
            this.menu.exit();
            return;
        }
        int n = this.minecraft.getWindow().getGuiScaledWidth() / 2;
        int n2 = this.getBlitOffset();
        this.setBlitOffset(-90);
        int n3 = Mth.floor((float)this.minecraft.getWindow().getGuiScaledHeight() - 22.0f * f2);
        SpectatorPage spectatorPage = this.menu.getCurrentPage();
        this.renderPage(poseStack, f2, n, n3, spectatorPage);
        this.setBlitOffset(n2);
    }

    protected void renderPage(PoseStack poseStack, float f, int n, int n2, SpectatorPage spectatorPage) {
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, f);
        this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        this.blit(poseStack, n - 91, n2, 0, 0, 182, 22);
        if (spectatorPage.getSelectedSlot() >= 0) {
            this.blit(poseStack, n - 91 - 1 + spectatorPage.getSelectedSlot() * 20, n2 - 1, 0, 22, 24, 22);
        }
        for (int i = 0; i < 9; ++i) {
            this.renderSlot(poseStack, i, this.minecraft.getWindow().getGuiScaledWidth() / 2 - 90 + i * 20 + 2, n2 + 3, f, spectatorPage.getItem(i));
        }
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
    }

    private void renderSlot(PoseStack poseStack, int n, int n2, float f, float f2, SpectatorMenuItem spectatorMenuItem) {
        this.minecraft.getTextureManager().bind(SPECTATOR_LOCATION);
        if (spectatorMenuItem != SpectatorMenu.EMPTY_SLOT) {
            int n3 = (int)(f2 * 255.0f);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(n2, f, 0.0f);
            float f3 = spectatorMenuItem.isEnabled() ? 1.0f : 0.25f;
            RenderSystem.color4f(f3, f3, f3, f2);
            spectatorMenuItem.renderIcon(poseStack, f3, n3);
            RenderSystem.popMatrix();
            if (n3 > 3 && spectatorMenuItem.isEnabled()) {
                Component component = this.minecraft.options.keyHotbarSlots[n].getTranslatedKeyMessage();
                this.minecraft.font.drawShadow(poseStack, component, (float)(n2 + 19 - 2 - this.minecraft.font.width(component)), f + 6.0f + 3.0f, 16777215 + (n3 << 24));
            }
        }
    }

    public void renderTooltip(PoseStack poseStack) {
        int n = (int)(this.getHotbarAlpha() * 255.0f);
        if (n > 3 && this.menu != null) {
            Component component;
            SpectatorMenuItem spectatorMenuItem = this.menu.getSelectedItem();
            Component component2 = component = spectatorMenuItem == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt() : spectatorMenuItem.getName();
            if (component != null) {
                int n2 = (this.minecraft.getWindow().getGuiScaledWidth() - this.minecraft.font.width(component)) / 2;
                int n3 = this.minecraft.getWindow().getGuiScaledHeight() - 35;
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                this.minecraft.font.drawShadow(poseStack, component, (float)n2, (float)n3, 16777215 + (n << 24));
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            }
        }
    }

    @Override
    public void onSpectatorMenuClosed(SpectatorMenu spectatorMenu) {
        this.menu = null;
        this.lastSelectionTime = 0L;
    }

    public boolean isMenuActive() {
        return this.menu != null;
    }

    public void onMouseScrolled(double d) {
        int n = this.menu.getSelectedSlot() + (int)d;
        while (!(n < 0 || n > 8 || this.menu.getItem(n) != SpectatorMenu.EMPTY_SLOT && this.menu.getItem(n).isEnabled())) {
            n = (int)((double)n + d);
        }
        if (n >= 0 && n <= 8) {
            this.menu.selectSlot(n);
            this.lastSelectionTime = Util.getMillis();
        }
    }

    public void onMouseMiddleClick() {
        this.lastSelectionTime = Util.getMillis();
        if (this.isMenuActive()) {
            int n = this.menu.getSelectedSlot();
            if (n != -1) {
                this.menu.selectSlot(n);
            }
        } else {
            this.menu = new SpectatorMenu(this);
        }
    }
}

