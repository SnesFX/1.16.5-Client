/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.client.BooleanOption;
import net.minecraft.client.CycleOption;
import net.minecraft.client.FullscreenResolutionProgressOption;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.PopupScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

public class VideoSettingsScreen
extends OptionsSubScreen {
    private static final Component FABULOUS = new TranslatableComponent("options.graphics.fabulous").withStyle(ChatFormatting.ITALIC);
    private static final Component WARNING_MESSAGE = new TranslatableComponent("options.graphics.warning.message", FABULOUS, FABULOUS);
    private static final Component WARNING_TITLE = new TranslatableComponent("options.graphics.warning.title").withStyle(ChatFormatting.RED);
    private static final Component BUTTON_ACCEPT = new TranslatableComponent("options.graphics.warning.accept");
    private static final Component BUTTON_CANCEL = new TranslatableComponent("options.graphics.warning.cancel");
    private static final Component NEW_LINE = new TextComponent("\n");
    private static final Option[] OPTIONS = new Option[]{Option.GRAPHICS, Option.RENDER_DISTANCE, Option.AMBIENT_OCCLUSION, Option.FRAMERATE_LIMIT, Option.ENABLE_VSYNC, Option.VIEW_BOBBING, Option.GUI_SCALE, Option.ATTACK_INDICATOR, Option.GAMMA, Option.RENDER_CLOUDS, Option.USE_FULLSCREEN, Option.PARTICLES, Option.MIPMAP_LEVELS, Option.ENTITY_SHADOWS, Option.SCREEN_EFFECTS_SCALE, Option.ENTITY_DISTANCE_SCALING, Option.FOV_EFFECTS_SCALE};
    private OptionsList list;
    private final GpuWarnlistManager gpuWarnlistManager;
    private final int oldMipmaps;

    public VideoSettingsScreen(Screen screen, Options options) {
        super(screen, options, new TranslatableComponent("options.videoTitle"));
        this.gpuWarnlistManager = screen.minecraft.getGpuWarnlistManager();
        this.gpuWarnlistManager.resetWarnings();
        if (options.graphicsMode == GraphicsStatus.FABULOUS) {
            this.gpuWarnlistManager.dismissWarning();
        }
        this.oldMipmaps = options.mipmapLevels;
    }

    @Override
    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addBig(new FullscreenResolutionProgressOption(this.minecraft.getWindow()));
        this.list.addBig(Option.BIOME_BLEND_RADIUS);
        this.list.addSmall(OPTIONS);
        this.children.add(this.list);
        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, button -> {
            this.minecraft.options.save();
            this.minecraft.getWindow().changeFullscreenVideoMode();
            this.minecraft.setScreen(this.lastScreen);
        }));
    }

    @Override
    public void removed() {
        if (this.options.mipmapLevels != this.oldMipmaps) {
            this.minecraft.updateMaxMipLevel(this.options.mipmapLevels);
            this.minecraft.delayTextureReload();
        }
        super.removed();
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        int n2 = this.options.guiScale;
        if (super.mouseClicked(d, d2, n)) {
            if (this.options.guiScale != n2) {
                this.minecraft.resizeDisplay();
            }
            if (this.gpuWarnlistManager.isShowingWarning()) {
                String string;
                String string2;
                ArrayList arrayList = Lists.newArrayList((Object[])new FormattedText[]{WARNING_MESSAGE, NEW_LINE});
                String string3 = this.gpuWarnlistManager.getRendererWarnings();
                if (string3 != null) {
                    arrayList.add(NEW_LINE);
                    arrayList.add(new TranslatableComponent("options.graphics.warning.renderer", string3).withStyle(ChatFormatting.GRAY));
                }
                if ((string = this.gpuWarnlistManager.getVendorWarnings()) != null) {
                    arrayList.add(NEW_LINE);
                    arrayList.add(new TranslatableComponent("options.graphics.warning.vendor", string).withStyle(ChatFormatting.GRAY));
                }
                if ((string2 = this.gpuWarnlistManager.getVersionWarnings()) != null) {
                    arrayList.add(NEW_LINE);
                    arrayList.add(new TranslatableComponent("options.graphics.warning.version", string2).withStyle(ChatFormatting.GRAY));
                }
                this.minecraft.setScreen(new PopupScreen(WARNING_TITLE, arrayList, (ImmutableList<PopupScreen.ButtonOption>)ImmutableList.of((Object)new PopupScreen.ButtonOption(BUTTON_ACCEPT, button -> {
                    this.options.graphicsMode = GraphicsStatus.FABULOUS;
                    Minecraft.getInstance().levelRenderer.allChanged();
                    this.gpuWarnlistManager.dismissWarning();
                    this.minecraft.setScreen(this);
                }), (Object)new PopupScreen.ButtonOption(BUTTON_CANCEL, button -> {
                    this.gpuWarnlistManager.dismissWarningAndSkipFabulous();
                    this.minecraft.setScreen(this);
                }))));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double d, double d2, int n) {
        int n2 = this.options.guiScale;
        if (super.mouseReleased(d, d2, n)) {
            return true;
        }
        if (this.list.mouseReleased(d, d2, n)) {
            if (this.options.guiScale != n2) {
                this.minecraft.resizeDisplay();
            }
            return true;
        }
        return false;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.list.render(poseStack, n, n2, f);
        VideoSettingsScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 5, 16777215);
        super.render(poseStack, n, n2, f);
        List<FormattedCharSequence> list = VideoSettingsScreen.tooltipAt(this.list, n, n2);
        if (list != null) {
            this.renderTooltip(poseStack, list, n, n2);
        }
    }
}

