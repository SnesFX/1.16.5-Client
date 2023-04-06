/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Screenshot {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    public static void grab(File file, int n, int n2, RenderTarget renderTarget, Consumer<Component> consumer) {
        Screenshot.grab(file, null, n, n2, renderTarget, consumer);
    }

    public static void grab(File file, @Nullable String string, int n, int n2, RenderTarget renderTarget, Consumer<Component> consumer) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> Screenshot._grab(file, string, n, n2, renderTarget, consumer));
        } else {
            Screenshot._grab(file, string, n, n2, renderTarget, consumer);
        }
    }

    private static void _grab(File file, @Nullable String string, int n, int n2, RenderTarget renderTarget, Consumer<Component> consumer) {
        NativeImage nativeImage = Screenshot.takeScreenshot(n, n2, renderTarget);
        File file2 = new File(file, "screenshots");
        file2.mkdir();
        File file3 = string == null ? Screenshot.getFile(file2) : new File(file2, string);
        Util.ioPool().execute(() -> {
            try {
                nativeImage.writeToFile(file3);
                MutableComponent mutableComponent = new TextComponent(file3.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file3.getAbsolutePath())));
                consumer.accept(new TranslatableComponent("screenshot.success", mutableComponent));
            }
            catch (Exception exception) {
                LOGGER.warn("Couldn't save screenshot", (Throwable)exception);
                consumer.accept(new TranslatableComponent("screenshot.failure", exception.getMessage()));
            }
            finally {
                nativeImage.close();
            }
        });
    }

    public static NativeImage takeScreenshot(int n, int n2, RenderTarget renderTarget) {
        n = renderTarget.width;
        n2 = renderTarget.height;
        NativeImage nativeImage = new NativeImage(n, n2, false);
        RenderSystem.bindTexture(renderTarget.getColorTextureId());
        nativeImage.downloadTexture(0, true);
        nativeImage.flipY();
        return nativeImage;
    }

    private static File getFile(File file) {
        String string = DATE_FORMAT.format(new Date());
        int n = 1;
        File file2;
        while ((file2 = new File(file, string + (n == 1 ? "" : "_" + n) + ".png")).exists()) {
            ++n;
        }
        return file2;
    }
}

