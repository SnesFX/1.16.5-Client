/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.AmbientOcclusionStatus;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.BooleanOption;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.CycleOption;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.LogaritmicProgressOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.Options;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;

public abstract class Option {
    public static final ProgressOption BIOME_BLEND_RADIUS = new ProgressOption("options.biomeBlendRadius", 0.0, 7.0, 1.0f, options -> options.biomeBlendRadius, (options, d) -> {
        options.biomeBlendRadius = Mth.clamp((int)d.doubleValue(), 0, 7);
        Minecraft.getInstance().levelRenderer.allChanged();
    }, (options, progressOption) -> {
        double d = progressOption.get((Options)options);
        int n = (int)d * 2 + 1;
        return progressOption.genericValueLabel(new TranslatableComponent("options.biomeBlendRadius." + n));
    });
    public static final ProgressOption CHAT_HEIGHT_FOCUSED = new ProgressOption("options.chat.height.focused", 0.0, 1.0, 0.0f, options -> options.chatHeightFocused, (options, d) -> {
        options.chatHeightFocused = d;
        Minecraft.getInstance().gui.getChat().rescaleChat();
    }, (options, progressOption) -> {
        double d = progressOption.toPct(progressOption.get((Options)options));
        return progressOption.pixelValueLabel(ChatComponent.getHeight(d));
    });
    public static final ProgressOption CHAT_HEIGHT_UNFOCUSED = new ProgressOption("options.chat.height.unfocused", 0.0, 1.0, 0.0f, options -> options.chatHeightUnfocused, (options, d) -> {
        options.chatHeightUnfocused = d;
        Minecraft.getInstance().gui.getChat().rescaleChat();
    }, (options, progressOption) -> {
        double d = progressOption.toPct(progressOption.get((Options)options));
        return progressOption.pixelValueLabel(ChatComponent.getHeight(d));
    });
    public static final ProgressOption CHAT_OPACITY = new ProgressOption("options.chat.opacity", 0.0, 1.0, 0.0f, options -> options.chatOpacity, (options, d) -> {
        options.chatOpacity = d;
        Minecraft.getInstance().gui.getChat().rescaleChat();
    }, (options, progressOption) -> {
        double d = progressOption.toPct(progressOption.get((Options)options));
        return progressOption.percentValueLabel(d * 0.9 + 0.1);
    });
    public static final ProgressOption CHAT_SCALE = new ProgressOption("options.chat.scale", 0.0, 1.0, 0.0f, options -> options.chatScale, (options, d) -> {
        options.chatScale = d;
        Minecraft.getInstance().gui.getChat().rescaleChat();
    }, (options, progressOption) -> {
        double d = progressOption.toPct(progressOption.get((Options)options));
        if (d == 0.0) {
            return CommonComponents.optionStatus(progressOption.getCaption(), false);
        }
        return progressOption.percentValueLabel(d);
    });
    public static final ProgressOption CHAT_WIDTH = new ProgressOption("options.chat.width", 0.0, 1.0, 0.0f, options -> options.chatWidth, (options, d) -> {
        options.chatWidth = d;
        Minecraft.getInstance().gui.getChat().rescaleChat();
    }, (options, progressOption) -> {
        double d = progressOption.toPct(progressOption.get((Options)options));
        return progressOption.pixelValueLabel(ChatComponent.getWidth(d));
    });
    public static final ProgressOption CHAT_LINE_SPACING = new ProgressOption("options.chat.line_spacing", 0.0, 1.0, 0.0f, options -> options.chatLineSpacing, (options, d) -> {
        options.chatLineSpacing = d;
    }, (options, progressOption) -> progressOption.percentValueLabel(progressOption.toPct(progressOption.get((Options)options))));
    public static final ProgressOption CHAT_DELAY = new ProgressOption("options.chat.delay_instant", 0.0, 6.0, 0.1f, options -> options.chatDelay, (options, d) -> {
        options.chatDelay = d;
    }, (options, progressOption) -> {
        double d = progressOption.get((Options)options);
        if (d <= 0.0) {
            return new TranslatableComponent("options.chat.delay_none");
        }
        return new TranslatableComponent("options.chat.delay", String.format("%.1f", d));
    });
    public static final ProgressOption FOV = new ProgressOption("options.fov", 30.0, 110.0, 1.0f, options -> options.fov, (options, d) -> {
        options.fov = d;
    }, (options, progressOption) -> {
        double d = progressOption.get((Options)options);
        if (d == 70.0) {
            return progressOption.genericValueLabel(new TranslatableComponent("options.fov.min"));
        }
        if (d == progressOption.getMaxValue()) {
            return progressOption.genericValueLabel(new TranslatableComponent("options.fov.max"));
        }
        return progressOption.genericValueLabel((int)d);
    });
    private static final Component ACCESSIBILITY_TOOLTIP_FOV_EFFECT = new TranslatableComponent("options.fovEffectScale.tooltip");
    public static final ProgressOption FOV_EFFECTS_SCALE = new ProgressOption("options.fovEffectScale", 0.0, 1.0, 0.0f, options -> Math.pow(options.fovEffectScale, 2.0), (options, d) -> {
        options.fovEffectScale = Mth.sqrt(d);
    }, (options, progressOption) -> {
        progressOption.setTooltip(Minecraft.getInstance().font.split(ACCESSIBILITY_TOOLTIP_FOV_EFFECT, 200));
        double d = progressOption.toPct(progressOption.get((Options)options));
        if (d == 0.0) {
            return progressOption.genericValueLabel(new TranslatableComponent("options.fovEffectScale.off"));
        }
        return progressOption.percentValueLabel(d);
    });
    private static final Component ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT = new TranslatableComponent("options.screenEffectScale.tooltip");
    public static final ProgressOption SCREEN_EFFECTS_SCALE = new ProgressOption("options.screenEffectScale", 0.0, 1.0, 0.0f, options -> options.screenEffectScale, (options, d) -> {
        options.screenEffectScale = d.floatValue();
    }, (options, progressOption) -> {
        progressOption.setTooltip(Minecraft.getInstance().font.split(ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT, 200));
        double d = progressOption.toPct(progressOption.get((Options)options));
        if (d == 0.0) {
            return progressOption.genericValueLabel(new TranslatableComponent("options.screenEffectScale.off"));
        }
        return progressOption.percentValueLabel(d);
    });
    public static final ProgressOption FRAMERATE_LIMIT = new ProgressOption("options.framerateLimit", 10.0, 260.0, 10.0f, options -> options.framerateLimit, (options, d) -> {
        options.framerateLimit = (int)d.doubleValue();
        Minecraft.getInstance().getWindow().setFramerateLimit(options.framerateLimit);
    }, (options, progressOption) -> {
        double d = progressOption.get((Options)options);
        if (d == progressOption.getMaxValue()) {
            return progressOption.genericValueLabel(new TranslatableComponent("options.framerateLimit.max"));
        }
        return progressOption.genericValueLabel(new TranslatableComponent("options.framerate", (int)d));
    });
    public static final ProgressOption GAMMA = new ProgressOption("options.gamma", 0.0, 1.0, 0.0f, options -> options.gamma, (options, d) -> {
        options.gamma = d;
    }, (options, progressOption) -> {
        double d = progressOption.toPct(progressOption.get((Options)options));
        if (d == 0.0) {
            return progressOption.genericValueLabel(new TranslatableComponent("options.gamma.min"));
        }
        if (d == 1.0) {
            return progressOption.genericValueLabel(new TranslatableComponent("options.gamma.max"));
        }
        return progressOption.percentAddValueLabel((int)(d * 100.0));
    });
    public static final ProgressOption MIPMAP_LEVELS = new ProgressOption("options.mipmapLevels", 0.0, 4.0, 1.0f, options -> options.mipmapLevels, (options, d) -> {
        options.mipmapLevels = (int)d.doubleValue();
    }, (options, progressOption) -> {
        double d = progressOption.get((Options)options);
        if (d == 0.0) {
            return CommonComponents.optionStatus(progressOption.getCaption(), false);
        }
        return progressOption.genericValueLabel((int)d);
    });
    public static final ProgressOption MOUSE_WHEEL_SENSITIVITY = new LogaritmicProgressOption("options.mouseWheelSensitivity", 0.01, 10.0, 0.01f, options -> options.mouseWheelSensitivity, (options, d) -> {
        options.mouseWheelSensitivity = d;
    }, (options, progressOption) -> {
        double d = progressOption.toPct(progressOption.get((Options)options));
        return progressOption.genericValueLabel(new TextComponent(String.format("%.2f", progressOption.toValue(d))));
    });
    public static final BooleanOption RAW_MOUSE_INPUT = new BooleanOption("options.rawMouseInput", options -> options.rawMouseInput, (options, bl) -> {
        options.rawMouseInput = bl;
        Window window = Minecraft.getInstance().getWindow();
        if (window != null) {
            window.updateRawMouseInput((boolean)bl);
        }
    });
    public static final ProgressOption RENDER_DISTANCE = new ProgressOption("options.renderDistance", 2.0, 16.0, 1.0f, options -> options.renderDistance, (options, d) -> {
        options.renderDistance = (int)d.doubleValue();
        Minecraft.getInstance().levelRenderer.needsUpdate();
    }, (options, progressOption) -> {
        double d = progressOption.get((Options)options);
        return progressOption.genericValueLabel(new TranslatableComponent("options.chunks", (int)d));
    });
    public static final ProgressOption ENTITY_DISTANCE_SCALING = new ProgressOption("options.entityDistanceScaling", 0.5, 5.0, 0.25f, options -> options.entityDistanceScaling, (options, d) -> {
        options.entityDistanceScaling = (float)d.doubleValue();
    }, (options, progressOption) -> {
        double d = progressOption.get((Options)options);
        return progressOption.percentValueLabel(d);
    });
    public static final ProgressOption SENSITIVITY = new ProgressOption("options.sensitivity", 0.0, 1.0, 0.0f, options -> options.sensitivity, (options, d) -> {
        options.sensitivity = d;
    }, (options, progressOption) -> {
        double d = progressOption.toPct(progressOption.get((Options)options));
        if (d == 0.0) {
            return progressOption.genericValueLabel(new TranslatableComponent("options.sensitivity.min"));
        }
        if (d == 1.0) {
            return progressOption.genericValueLabel(new TranslatableComponent("options.sensitivity.max"));
        }
        return progressOption.percentValueLabel(2.0 * d);
    });
    public static final ProgressOption TEXT_BACKGROUND_OPACITY = new ProgressOption("options.accessibility.text_background_opacity", 0.0, 1.0, 0.0f, options -> options.textBackgroundOpacity, (options, d) -> {
        options.textBackgroundOpacity = d;
        Minecraft.getInstance().gui.getChat().rescaleChat();
    }, (options, progressOption) -> progressOption.percentValueLabel(progressOption.toPct(progressOption.get((Options)options))));
    public static final CycleOption AMBIENT_OCCLUSION = new CycleOption("options.ao", (options, n) -> {
        options.ambientOcclusion = AmbientOcclusionStatus.byId(options.ambientOcclusion.getId() + n);
        Minecraft.getInstance().levelRenderer.allChanged();
    }, (options, cycleOption) -> cycleOption.genericValueLabel(new TranslatableComponent(options.ambientOcclusion.getKey())));
    public static final CycleOption ATTACK_INDICATOR = new CycleOption("options.attackIndicator", (options, n) -> {
        options.attackIndicator = AttackIndicatorStatus.byId(options.attackIndicator.getId() + n);
    }, (options, cycleOption) -> cycleOption.genericValueLabel(new TranslatableComponent(options.attackIndicator.getKey())));
    public static final CycleOption CHAT_VISIBILITY = new CycleOption("options.chat.visibility", (options, n) -> {
        options.chatVisibility = ChatVisiblity.byId((options.chatVisibility.getId() + n) % 3);
    }, (options, cycleOption) -> cycleOption.genericValueLabel(new TranslatableComponent(options.chatVisibility.getKey())));
    private static final Component GRAPHICS_TOOLTIP_FAST = new TranslatableComponent("options.graphics.fast.tooltip");
    private static final Component GRAPHICS_TOOLTIP_FABULOUS = new TranslatableComponent("options.graphics.fabulous.tooltip", new TranslatableComponent("options.graphics.fabulous").withStyle(ChatFormatting.ITALIC));
    private static final Component GRAPHICS_TOOLTIP_FANCY = new TranslatableComponent("options.graphics.fancy.tooltip");
    public static final CycleOption GRAPHICS = new CycleOption("options.graphics", (options, n) -> {
        Minecraft minecraft = Minecraft.getInstance();
        GpuWarnlistManager gpuWarnlistManager = minecraft.getGpuWarnlistManager();
        if (options.graphicsMode == GraphicsStatus.FANCY && gpuWarnlistManager.willShowWarning()) {
            gpuWarnlistManager.showWarning();
            return;
        }
        options.graphicsMode = options.graphicsMode.cycleNext();
        if (options.graphicsMode == GraphicsStatus.FABULOUS && (!GlStateManager.supportsFramebufferBlit() || gpuWarnlistManager.isSkippingFabulous())) {
            options.graphicsMode = GraphicsStatus.FAST;
        }
        minecraft.levelRenderer.allChanged();
    }, (options, cycleOption) -> {
        switch (options.graphicsMode) {
            case FAST: {
                cycleOption.setTooltip(Minecraft.getInstance().font.split(GRAPHICS_TOOLTIP_FAST, 200));
                break;
            }
            case FANCY: {
                cycleOption.setTooltip(Minecraft.getInstance().font.split(GRAPHICS_TOOLTIP_FANCY, 200));
                break;
            }
            case FABULOUS: {
                cycleOption.setTooltip(Minecraft.getInstance().font.split(GRAPHICS_TOOLTIP_FABULOUS, 200));
            }
        }
        TranslatableComponent translatableComponent = new TranslatableComponent(options.graphicsMode.getKey());
        if (options.graphicsMode == GraphicsStatus.FABULOUS) {
            return cycleOption.genericValueLabel(translatableComponent.withStyle(ChatFormatting.ITALIC));
        }
        return cycleOption.genericValueLabel(translatableComponent);
    });
    public static final CycleOption GUI_SCALE = new CycleOption("options.guiScale", (options, n) -> {
        options.guiScale = Integer.remainderUnsigned(options.guiScale + n, Minecraft.getInstance().getWindow().calculateScale(0, Minecraft.getInstance().isEnforceUnicode()) + 1);
    }, (options, cycleOption) -> {
        if (options.guiScale == 0) {
            return cycleOption.genericValueLabel(new TranslatableComponent("options.guiScale.auto"));
        }
        return cycleOption.genericValueLabel(options.guiScale);
    });
    public static final CycleOption MAIN_HAND = new CycleOption("options.mainHand", (options, n) -> {
        options.mainHand = options.mainHand.getOpposite();
    }, (options, cycleOption) -> cycleOption.genericValueLabel(options.mainHand.getName()));
    public static final CycleOption NARRATOR = new CycleOption("options.narrator", (options, n) -> {
        options.narratorStatus = NarratorChatListener.INSTANCE.isActive() ? NarratorStatus.byId(options.narratorStatus.getId() + n) : NarratorStatus.OFF;
        NarratorChatListener.INSTANCE.updateNarratorStatus(options.narratorStatus);
    }, (options, cycleOption) -> {
        if (NarratorChatListener.INSTANCE.isActive()) {
            return cycleOption.genericValueLabel(options.narratorStatus.getName());
        }
        return cycleOption.genericValueLabel(new TranslatableComponent("options.narrator.notavailable"));
    });
    public static final CycleOption PARTICLES = new CycleOption("options.particles", (options, n) -> {
        options.particles = ParticleStatus.byId(options.particles.getId() + n);
    }, (options, cycleOption) -> cycleOption.genericValueLabel(new TranslatableComponent(options.particles.getKey())));
    public static final CycleOption RENDER_CLOUDS = new CycleOption("options.renderClouds", (options, n) -> {
        RenderTarget renderTarget;
        options.renderClouds = CloudStatus.byId(options.renderClouds.getId() + n);
        if (Minecraft.useShaderTransparency() && (renderTarget = Minecraft.getInstance().levelRenderer.getCloudsTarget()) != null) {
            renderTarget.clear(Minecraft.ON_OSX);
        }
    }, (options, cycleOption) -> cycleOption.genericValueLabel(new TranslatableComponent(options.renderClouds.getKey())));
    public static final CycleOption TEXT_BACKGROUND = new CycleOption("options.accessibility.text_background", (options, n) -> {
        options.backgroundForChatOnly = !options.backgroundForChatOnly;
    }, (options, cycleOption) -> cycleOption.genericValueLabel(new TranslatableComponent(options.backgroundForChatOnly ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere")));
    private static final Component CHAT_TOOLTIP_HIDE_MATCHED_NAMES = new TranslatableComponent("options.hideMatchedNames.tooltip");
    public static final BooleanOption AUTO_JUMP = new BooleanOption("options.autoJump", options -> options.autoJump, (options, bl) -> {
        options.autoJump = bl;
    });
    public static final BooleanOption AUTO_SUGGESTIONS = new BooleanOption("options.autoSuggestCommands", options -> options.autoSuggestions, (options, bl) -> {
        options.autoSuggestions = bl;
    });
    public static final BooleanOption HIDE_MATCHED_NAMES = new BooleanOption("options.hideMatchedNames", CHAT_TOOLTIP_HIDE_MATCHED_NAMES, options -> options.hideMatchedNames, (options, bl) -> {
        options.hideMatchedNames = bl;
    });
    public static final BooleanOption CHAT_COLOR = new BooleanOption("options.chat.color", options -> options.chatColors, (options, bl) -> {
        options.chatColors = bl;
    });
    public static final BooleanOption CHAT_LINKS = new BooleanOption("options.chat.links", options -> options.chatLinks, (options, bl) -> {
        options.chatLinks = bl;
    });
    public static final BooleanOption CHAT_LINKS_PROMPT = new BooleanOption("options.chat.links.prompt", options -> options.chatLinksPrompt, (options, bl) -> {
        options.chatLinksPrompt = bl;
    });
    public static final BooleanOption DISCRETE_MOUSE_SCROLL = new BooleanOption("options.discrete_mouse_scroll", options -> options.discreteMouseScroll, (options, bl) -> {
        options.discreteMouseScroll = bl;
    });
    public static final BooleanOption ENABLE_VSYNC = new BooleanOption("options.vsync", options -> options.enableVsync, (options, bl) -> {
        options.enableVsync = bl;
        if (Minecraft.getInstance().getWindow() != null) {
            Minecraft.getInstance().getWindow().updateVsync(options.enableVsync);
        }
    });
    public static final BooleanOption ENTITY_SHADOWS = new BooleanOption("options.entityShadows", options -> options.entityShadows, (options, bl) -> {
        options.entityShadows = bl;
    });
    public static final BooleanOption FORCE_UNICODE_FONT = new BooleanOption("options.forceUnicodeFont", options -> options.forceUnicodeFont, (options, bl) -> {
        options.forceUnicodeFont = bl;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getWindow() != null) {
            minecraft.selectMainFont((boolean)bl);
        }
    });
    public static final BooleanOption INVERT_MOUSE = new BooleanOption("options.invertMouse", options -> options.invertYMouse, (options, bl) -> {
        options.invertYMouse = bl;
    });
    public static final BooleanOption REALMS_NOTIFICATIONS = new BooleanOption("options.realmsNotifications", options -> options.realmsNotifications, (options, bl) -> {
        options.realmsNotifications = bl;
    });
    public static final BooleanOption REDUCED_DEBUG_INFO = new BooleanOption("options.reducedDebugInfo", options -> options.reducedDebugInfo, (options, bl) -> {
        options.reducedDebugInfo = bl;
    });
    public static final BooleanOption SHOW_SUBTITLES = new BooleanOption("options.showSubtitles", options -> options.showSubtitles, (options, bl) -> {
        options.showSubtitles = bl;
    });
    public static final BooleanOption SNOOPER_ENABLED = new BooleanOption("options.snooper", options -> {
        if (options.snooperEnabled) {
            // empty if block
        }
        return false;
    }, (options, bl) -> {
        options.snooperEnabled = bl;
    });
    public static final CycleOption TOGGLE_CROUCH = new CycleOption("key.sneak", (options, n) -> {
        options.toggleCrouch = !options.toggleCrouch;
    }, (options, cycleOption) -> cycleOption.genericValueLabel(new TranslatableComponent(options.toggleCrouch ? "options.key.toggle" : "options.key.hold")));
    public static final CycleOption TOGGLE_SPRINT = new CycleOption("key.sprint", (options, n) -> {
        options.toggleSprint = !options.toggleSprint;
    }, (options, cycleOption) -> cycleOption.genericValueLabel(new TranslatableComponent(options.toggleSprint ? "options.key.toggle" : "options.key.hold")));
    public static final BooleanOption TOUCHSCREEN = new BooleanOption("options.touchscreen", options -> options.touchscreen, (options, bl) -> {
        options.touchscreen = bl;
    });
    public static final BooleanOption USE_FULLSCREEN = new BooleanOption("options.fullscreen", options -> options.fullscreen, (options, bl) -> {
        options.fullscreen = bl;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getWindow() != null && minecraft.getWindow().isFullscreen() != options.fullscreen) {
            minecraft.getWindow().toggleFullScreen();
            options.fullscreen = minecraft.getWindow().isFullscreen();
        }
    });
    public static final BooleanOption VIEW_BOBBING = new BooleanOption("options.viewBobbing", options -> options.bobView, (options, bl) -> {
        options.bobView = bl;
    });
    private final Component caption;
    private Optional<List<FormattedCharSequence>> toolTip = Optional.empty();

    public Option(String string) {
        this.caption = new TranslatableComponent(string);
    }

    public abstract AbstractWidget createButton(Options var1, int var2, int var3, int var4);

    protected Component getCaption() {
        return this.caption;
    }

    public void setTooltip(List<FormattedCharSequence> list) {
        this.toolTip = Optional.of(list);
    }

    public Optional<List<FormattedCharSequence>> getTooltip() {
        return this.toolTip;
    }

    protected Component pixelValueLabel(int n) {
        return new TranslatableComponent("options.pixel_value", this.getCaption(), n);
    }

    protected Component percentValueLabel(double d) {
        return new TranslatableComponent("options.percent_value", this.getCaption(), (int)(d * 100.0));
    }

    protected Component percentAddValueLabel(int n) {
        return new TranslatableComponent("options.percent_add_value", this.getCaption(), n);
    }

    protected Component genericValueLabel(Component component) {
        return new TranslatableComponent("options.generic_value", this.getCaption(), component);
    }

    protected Component genericValueLabel(int n) {
        return this.genericValueLabel(new TextComponent(Integer.toString(n)));
    }

}

