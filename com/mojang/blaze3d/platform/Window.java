/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.glfw.Callbacks
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallback
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 *  org.lwjgl.glfw.GLFWImage
 *  org.lwjgl.glfw.GLFWImage$Buffer
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.stb.STBImage
 *  org.lwjgl.system.CustomBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.main.SilentInitException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.CustomBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public final class Window
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final GLFWErrorCallback defaultErrorCallback = GLFWErrorCallback.create((arg_0, arg_1) -> this.defaultErrorCallback(arg_0, arg_1));
    private final WindowEventHandler eventHandler;
    private final ScreenManager screenManager;
    private final long window;
    private int windowedX;
    private int windowedY;
    private int windowedWidth;
    private int windowedHeight;
    private Optional<VideoMode> preferredFullscreenVideoMode;
    private boolean fullscreen;
    private boolean actuallyFullscreen;
    private int x;
    private int y;
    private int width;
    private int height;
    private int framebufferWidth;
    private int framebufferHeight;
    private int guiScaledWidth;
    private int guiScaledHeight;
    private double guiScale;
    private String errorSection = "";
    private boolean dirty;
    private int framerateLimit;
    private boolean vsync;

    public Window(WindowEventHandler windowEventHandler, ScreenManager screenManager, DisplayData displayData, @Nullable String string, String string2) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        this.screenManager = screenManager;
        this.setBootErrorCallback();
        this.setErrorSection("Pre startup");
        this.eventHandler = windowEventHandler;
        Optional<VideoMode> optional = VideoMode.read(string);
        this.preferredFullscreenVideoMode = optional.isPresent() ? optional : (displayData.fullscreenWidth.isPresent() && displayData.fullscreenHeight.isPresent() ? Optional.of(new VideoMode(displayData.fullscreenWidth.getAsInt(), displayData.fullscreenHeight.getAsInt(), 8, 8, 8, 60)) : Optional.empty());
        this.actuallyFullscreen = this.fullscreen = displayData.isFullscreen;
        Monitor monitor = screenManager.getMonitor(GLFW.glfwGetPrimaryMonitor());
        this.width = displayData.width > 0 ? displayData.width : 1;
        this.windowedWidth = this.width;
        this.height = displayData.height > 0 ? displayData.height : 1;
        this.windowedHeight = this.height;
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint((int)139265, (int)196609);
        GLFW.glfwWindowHint((int)139275, (int)221185);
        GLFW.glfwWindowHint((int)139266, (int)2);
        GLFW.glfwWindowHint((int)139267, (int)0);
        GLFW.glfwWindowHint((int)139272, (int)0);
        this.window = GLFW.glfwCreateWindow((int)this.width, (int)this.height, (CharSequence)string2, (long)(this.fullscreen && monitor != null ? monitor.getMonitor() : 0L), (long)0L);
        if (monitor != null) {
            VideoMode videoMode = monitor.getPreferredVidMode(this.fullscreen ? this.preferredFullscreenVideoMode : Optional.empty());
            this.windowedX = this.x = monitor.getX() + videoMode.getWidth() / 2 - this.width / 2;
            this.windowedY = this.y = monitor.getY() + videoMode.getHeight() / 2 - this.height / 2;
        } else {
            int[] arrn = new int[1];
            int[] arrn2 = new int[1];
            GLFW.glfwGetWindowPos((long)this.window, (int[])arrn, (int[])arrn2);
            this.windowedX = this.x = arrn[0];
            this.windowedY = this.y = arrn2[0];
        }
        GLFW.glfwMakeContextCurrent((long)this.window);
        GL.createCapabilities();
        this.setMode();
        this.refreshFramebufferSize();
        GLFW.glfwSetFramebufferSizeCallback((long)this.window, (arg_0, arg_1, arg_2) -> this.onFramebufferResize(arg_0, arg_1, arg_2));
        GLFW.glfwSetWindowPosCallback((long)this.window, (arg_0, arg_1, arg_2) -> this.onMove(arg_0, arg_1, arg_2));
        GLFW.glfwSetWindowSizeCallback((long)this.window, (arg_0, arg_1, arg_2) -> this.onResize(arg_0, arg_1, arg_2));
        GLFW.glfwSetWindowFocusCallback((long)this.window, (arg_0, arg_1) -> this.onFocus(arg_0, arg_1));
        GLFW.glfwSetCursorEnterCallback((long)this.window, (arg_0, arg_1) -> this.onEnter(arg_0, arg_1));
    }

    public int getRefreshRate() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GLX._getRefreshRate(this);
    }

    public boolean shouldClose() {
        return GLX._shouldClose(this);
    }

    public static void checkGlfwError(BiConsumer<Integer, String> biConsumer) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
            int n = GLFW.glfwGetError((PointerBuffer)pointerBuffer);
            if (n != 0) {
                long l = pointerBuffer.get();
                String string = l == 0L ? "" : MemoryUtil.memUTF8((long)l);
                biConsumer.accept(n, string);
            }
        }
    }

    public void setIcon(InputStream inputStream, InputStream inputStream2) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        try {
            try (MemoryStack memoryStack = MemoryStack.stackPush();){
                if (inputStream == null) {
                    throw new FileNotFoundException("icons/icon_16x16.png");
                }
                if (inputStream2 == null) {
                    throw new FileNotFoundException("icons/icon_32x32.png");
                }
                IntBuffer intBuffer = memoryStack.mallocInt(1);
                IntBuffer intBuffer2 = memoryStack.mallocInt(1);
                IntBuffer intBuffer3 = memoryStack.mallocInt(1);
                GLFWImage.Buffer buffer = GLFWImage.mallocStack((int)2, (MemoryStack)memoryStack);
                ByteBuffer byteBuffer = this.readIconPixels(inputStream, intBuffer, intBuffer2, intBuffer3);
                if (byteBuffer == null) {
                    throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
                }
                buffer.position(0);
                buffer.width(intBuffer.get(0));
                buffer.height(intBuffer2.get(0));
                buffer.pixels(byteBuffer);
                ByteBuffer byteBuffer2 = this.readIconPixels(inputStream2, intBuffer, intBuffer2, intBuffer3);
                if (byteBuffer2 == null) {
                    throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
                }
                buffer.position(1);
                buffer.width(intBuffer.get(0));
                buffer.height(intBuffer2.get(0));
                buffer.pixels(byteBuffer2);
                buffer.position(0);
                GLFW.glfwSetWindowIcon((long)this.window, (GLFWImage.Buffer)buffer);
                STBImage.stbi_image_free((ByteBuffer)byteBuffer);
                STBImage.stbi_image_free((ByteBuffer)byteBuffer2);
            }
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't set icon", (Throwable)iOException);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private ByteBuffer readIconPixels(InputStream inputStream, IntBuffer intBuffer, IntBuffer intBuffer2, IntBuffer intBuffer3) throws IOException {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        ByteBuffer byteBuffer = null;
        try {
            byteBuffer = TextureUtil.readResource(inputStream);
            byteBuffer.rewind();
            ByteBuffer byteBuffer2 = STBImage.stbi_load_from_memory((ByteBuffer)byteBuffer, (IntBuffer)intBuffer, (IntBuffer)intBuffer2, (IntBuffer)intBuffer3, (int)0);
            return byteBuffer2;
        }
        finally {
            if (byteBuffer != null) {
                MemoryUtil.memFree((Buffer)byteBuffer);
            }
        }
    }

    public void setErrorSection(String string) {
        this.errorSection = string;
    }

    private void setBootErrorCallback() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GLFW.glfwSetErrorCallback((arg_0, arg_1) -> Window.bootCrash(arg_0, arg_1));
    }

    private static void bootCrash(int n, long l) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        String string = "GLFW error " + n + ": " + MemoryUtil.memUTF8((long)l);
        TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)(string + ".\n\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions)."), (CharSequence)"ok", (CharSequence)"error", (boolean)false);
        throw new WindowInitFailed(string);
    }

    public void defaultErrorCallback(int n, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        String string = MemoryUtil.memUTF8((long)l);
        LOGGER.error("########## GL ERROR ##########");
        LOGGER.error("@ {}", (Object)this.errorSection);
        LOGGER.error("{}: {}", (Object)n, (Object)string);
    }

    public void setDefaultErrorCallback() {
        GLFWErrorCallback gLFWErrorCallback = GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)this.defaultErrorCallback);
        if (gLFWErrorCallback != null) {
            gLFWErrorCallback.free();
        }
    }

    public void updateVsync(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.vsync = bl;
        GLFW.glfwSwapInterval((int)(bl ? 1 : 0));
    }

    @Override
    public void close() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        Callbacks.glfwFreeCallbacks((long)this.window);
        this.defaultErrorCallback.close();
        GLFW.glfwDestroyWindow((long)this.window);
        GLFW.glfwTerminate();
    }

    private void onMove(long l, int n, int n2) {
        this.x = n;
        this.y = n2;
    }

    private void onFramebufferResize(long l, int n, int n2) {
        if (l != this.window) {
            return;
        }
        int n3 = this.getWidth();
        int n4 = this.getHeight();
        if (n == 0 || n2 == 0) {
            return;
        }
        this.framebufferWidth = n;
        this.framebufferHeight = n2;
        if (this.getWidth() != n3 || this.getHeight() != n4) {
            this.eventHandler.resizeDisplay();
        }
    }

    private void refreshFramebufferSize() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        int[] arrn = new int[1];
        int[] arrn2 = new int[1];
        GLFW.glfwGetFramebufferSize((long)this.window, (int[])arrn, (int[])arrn2);
        this.framebufferWidth = arrn[0];
        this.framebufferHeight = arrn2[0];
    }

    private void onResize(long l, int n, int n2) {
        this.width = n;
        this.height = n2;
    }

    private void onFocus(long l, boolean bl) {
        if (l == this.window) {
            this.eventHandler.setWindowActive(bl);
        }
    }

    private void onEnter(long l, boolean bl) {
        if (bl) {
            this.eventHandler.cursorEntered();
        }
    }

    public void setFramerateLimit(int n) {
        this.framerateLimit = n;
    }

    public int getFramerateLimit() {
        return this.framerateLimit;
    }

    public void updateDisplay() {
        RenderSystem.flipFrame(this.window);
        if (this.fullscreen != this.actuallyFullscreen) {
            this.actuallyFullscreen = this.fullscreen;
            this.updateFullscreen(this.vsync);
        }
    }

    public Optional<VideoMode> getPreferredFullscreenVideoMode() {
        return this.preferredFullscreenVideoMode;
    }

    public void setPreferredFullscreenVideoMode(Optional<VideoMode> optional) {
        boolean bl = !optional.equals(this.preferredFullscreenVideoMode);
        this.preferredFullscreenVideoMode = optional;
        if (bl) {
            this.dirty = true;
        }
    }

    public void changeFullscreenVideoMode() {
        if (this.fullscreen && this.dirty) {
            this.dirty = false;
            this.setMode();
            this.eventHandler.resizeDisplay();
        }
    }

    private void setMode() {
        boolean bl;
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        boolean bl2 = bl = GLFW.glfwGetWindowMonitor((long)this.window) != 0L;
        if (this.fullscreen) {
            Monitor monitor = this.screenManager.findBestMonitor(this);
            if (monitor == null) {
                LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
                this.fullscreen = false;
            } else {
                VideoMode videoMode = monitor.getPreferredVidMode(this.preferredFullscreenVideoMode);
                if (!bl) {
                    this.windowedX = this.x;
                    this.windowedY = this.y;
                    this.windowedWidth = this.width;
                    this.windowedHeight = this.height;
                }
                this.x = 0;
                this.y = 0;
                this.width = videoMode.getWidth();
                this.height = videoMode.getHeight();
                GLFW.glfwSetWindowMonitor((long)this.window, (long)monitor.getMonitor(), (int)this.x, (int)this.y, (int)this.width, (int)this.height, (int)videoMode.getRefreshRate());
            }
        } else {
            this.x = this.windowedX;
            this.y = this.windowedY;
            this.width = this.windowedWidth;
            this.height = this.windowedHeight;
            GLFW.glfwSetWindowMonitor((long)this.window, (long)0L, (int)this.x, (int)this.y, (int)this.width, (int)this.height, (int)-1);
        }
    }

    public void toggleFullScreen() {
        this.fullscreen = !this.fullscreen;
    }

    private void updateFullscreen(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        try {
            this.setMode();
            this.eventHandler.resizeDisplay();
            this.updateVsync(bl);
            this.updateDisplay();
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't toggle fullscreen", (Throwable)exception);
        }
    }

    public int calculateScale(int n, boolean bl) {
        int n2;
        for (n2 = 1; n2 != n && n2 < this.framebufferWidth && n2 < this.framebufferHeight && this.framebufferWidth / (n2 + 1) >= 320 && this.framebufferHeight / (n2 + 1) >= 240; ++n2) {
        }
        if (bl && n2 % 2 != 0) {
            ++n2;
        }
        return n2;
    }

    public void setGuiScale(double d) {
        this.guiScale = d;
        int n = (int)((double)this.framebufferWidth / d);
        this.guiScaledWidth = (double)this.framebufferWidth / d > (double)n ? n + 1 : n;
        int n2 = (int)((double)this.framebufferHeight / d);
        this.guiScaledHeight = (double)this.framebufferHeight / d > (double)n2 ? n2 + 1 : n2;
    }

    public void setTitle(String string) {
        GLFW.glfwSetWindowTitle((long)this.window, (CharSequence)string);
    }

    public long getWindow() {
        return this.window;
    }

    public boolean isFullscreen() {
        return this.fullscreen;
    }

    public int getWidth() {
        return this.framebufferWidth;
    }

    public int getHeight() {
        return this.framebufferHeight;
    }

    public int getScreenWidth() {
        return this.width;
    }

    public int getScreenHeight() {
        return this.height;
    }

    public int getGuiScaledWidth() {
        return this.guiScaledWidth;
    }

    public int getGuiScaledHeight() {
        return this.guiScaledHeight;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public double getGuiScale() {
        return this.guiScale;
    }

    @Nullable
    public Monitor findBestMonitor() {
        return this.screenManager.findBestMonitor(this);
    }

    public void updateRawMouseInput(boolean bl) {
        InputConstants.updateRawMouseInput(this.window, bl);
    }

    public static class WindowInitFailed
    extends SilentInitException {
        private WindowInitFailed(String string) {
            super(string);
        }
    }

}

