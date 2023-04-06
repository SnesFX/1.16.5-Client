/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client;

import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class FullscreenResolutionProgressOption
extends ProgressOption {
    public FullscreenResolutionProgressOption(Window window) {
        this(window, window.findBestMonitor());
    }

    private FullscreenResolutionProgressOption(Window window, @Nullable Monitor monitor) {
        super("options.fullscreen.resolution", -1.0, monitor != null ? (double)(monitor.getModeCount() - 1) : -1.0, 1.0f, options -> {
            if (monitor == null) {
                return -1.0;
            }
            Optional<VideoMode> optional = window.getPreferredFullscreenVideoMode();
            return optional.map(videoMode -> monitor.getVideoModeIndex((VideoMode)videoMode)).orElse(-1.0);
        }, (options, d) -> {
            if (monitor == null) {
                return;
            }
            if (d == -1.0) {
                window.setPreferredFullscreenVideoMode(Optional.empty());
            } else {
                window.setPreferredFullscreenVideoMode(Optional.of(monitor.getMode(d.intValue())));
            }
        }, (options, progressOption) -> {
            if (monitor == null) {
                return new TranslatableComponent("options.fullscreen.unavailable");
            }
            double d = progressOption.get((Options)options);
            if (d == -1.0) {
                return progressOption.genericValueLabel(new TranslatableComponent("options.fullscreen.current"));
            }
            return progressOption.genericValueLabel(new TextComponent(monitor.getMode((int)d).toString()));
        });
    }
}

