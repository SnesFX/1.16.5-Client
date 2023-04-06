/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWErrorCallbackI
 */
package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;

public class RenderSystem {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConcurrentLinkedQueue<RenderCall> recordingQueue = Queues.newConcurrentLinkedQueue();
    private static final Tesselator RENDER_THREAD_TESSELATOR = new Tesselator();
    public static final float DEFAULTALPHACUTOFF = 0.1f;
    private static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
    private static boolean isReplayingQueue;
    private static Thread gameThread;
    private static Thread renderThread;
    private static int MAX_SUPPORTED_TEXTURE_SIZE;
    private static boolean isInInit;
    private static double lastDrawTime;

    public static void initRenderThread() {
        if (renderThread != null || gameThread == Thread.currentThread()) {
            throw new IllegalStateException("Could not initialize render thread");
        }
        renderThread = Thread.currentThread();
    }

    public static boolean isOnRenderThread() {
        return Thread.currentThread() == renderThread;
    }

    public static boolean isOnRenderThreadOrInit() {
        return isInInit || RenderSystem.isOnRenderThread();
    }

    public static void initGameThread(boolean bl) {
        boolean bl2;
        boolean bl3 = bl2 = renderThread == Thread.currentThread();
        if (gameThread != null || renderThread == null || bl2 == bl) {
            throw new IllegalStateException("Could not initialize tick thread");
        }
        gameThread = Thread.currentThread();
    }

    public static boolean isOnGameThread() {
        return true;
    }

    public static boolean isOnGameThreadOrInit() {
        return isInInit || RenderSystem.isOnGameThread();
    }

    public static void assertThread(Supplier<Boolean> supplier) {
        if (!supplier.get().booleanValue()) {
            throw new IllegalStateException("Rendersystem called from wrong thread");
        }
    }

    public static boolean isInInitPhase() {
        return true;
    }

    public static void recordRenderCall(RenderCall renderCall) {
        recordingQueue.add(renderCall);
    }

    public static void flipFrame(long l) {
        GLFW.glfwPollEvents();
        RenderSystem.replayQueue();
        Tesselator.getInstance().getBuilder().clear();
        GLFW.glfwSwapBuffers((long)l);
        GLFW.glfwPollEvents();
    }

    public static void replayQueue() {
        isReplayingQueue = true;
        while (!recordingQueue.isEmpty()) {
            RenderCall renderCall = recordingQueue.poll();
            renderCall.execute();
        }
        isReplayingQueue = false;
    }

    public static void limitDisplayFPS(int n) {
        double d = lastDrawTime + 1.0 / (double)n;
        double d2 = GLFW.glfwGetTime();
        while (d2 < d) {
            GLFW.glfwWaitEventsTimeout((double)(d - d2));
            d2 = GLFW.glfwGetTime();
        }
        lastDrawTime = d2;
    }

    @Deprecated
    public static void pushLightingAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._pushLightingAttributes();
    }

    @Deprecated
    public static void pushTextureAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._pushTextureAttributes();
    }

    @Deprecated
    public static void popAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._popAttributes();
    }

    @Deprecated
    public static void disableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._disableAlphaTest();
    }

    @Deprecated
    public static void enableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._enableAlphaTest();
    }

    @Deprecated
    public static void alphaFunc(int n, float f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._alphaFunc(n, f);
    }

    @Deprecated
    public static void enableLighting() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._enableLighting();
    }

    @Deprecated
    public static void disableLighting() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._disableLighting();
    }

    @Deprecated
    public static void enableColorMaterial() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._enableColorMaterial();
    }

    @Deprecated
    public static void disableColorMaterial() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._disableColorMaterial();
    }

    @Deprecated
    public static void colorMaterial(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._colorMaterial(n, n2);
    }

    @Deprecated
    public static void normal3f(float f, float f2, float f3) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._normal3f(f, f2, f3);
    }

    public static void disableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._disableDepthTest();
    }

    public static void enableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager._enableDepthTest();
    }

    public static void enableScissor(int n, int n2, int n3, int n4) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager._enableScissorTest();
        GlStateManager._scissorBox(n, n2, n3, n4);
    }

    public static void disableScissor() {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager._disableScissorTest();
    }

    public static void depthFunc(int n) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._depthFunc(n);
    }

    public static void depthMask(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._depthMask(bl);
    }

    public static void enableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._enableBlend();
    }

    public static void disableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._disableBlend();
    }

    public static void blendFunc(GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._blendFunc(sourceFactor.value, destFactor.value);
    }

    public static void blendFunc(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._blendFunc(n, n2);
    }

    public static void blendFuncSeparate(GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor, GlStateManager.SourceFactor sourceFactor2, GlStateManager.DestFactor destFactor2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._blendFuncSeparate(sourceFactor.value, destFactor.value, sourceFactor2.value, destFactor2.value);
    }

    public static void blendFuncSeparate(int n, int n2, int n3, int n4) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._blendFuncSeparate(n, n2, n3, n4);
    }

    public static void blendEquation(int n) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._blendEquation(n);
    }

    public static void blendColor(float f, float f2, float f3, float f4) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._blendColor(f, f2, f3, f4);
    }

    @Deprecated
    public static void enableFog() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._enableFog();
    }

    @Deprecated
    public static void disableFog() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._disableFog();
    }

    @Deprecated
    public static void fogMode(GlStateManager.FogMode fogMode) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._fogMode(fogMode.value);
    }

    @Deprecated
    public static void fogMode(int n) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._fogMode(n);
    }

    @Deprecated
    public static void fogDensity(float f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._fogDensity(f);
    }

    @Deprecated
    public static void fogStart(float f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._fogStart(f);
    }

    @Deprecated
    public static void fogEnd(float f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._fogEnd(f);
    }

    @Deprecated
    public static void fog(int n, float f, float f2, float f3, float f4) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._fog(n, new float[]{f, f2, f3, f4});
    }

    @Deprecated
    public static void fogi(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._fogi(n, n2);
    }

    public static void enableCull() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._enableCull();
    }

    public static void disableCull() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._disableCull();
    }

    public static void polygonMode(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._polygonMode(n, n2);
    }

    public static void enablePolygonOffset() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._enablePolygonOffset();
    }

    public static void disablePolygonOffset() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._disablePolygonOffset();
    }

    public static void enableLineOffset() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._enableLineOffset();
    }

    public static void disableLineOffset() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._disableLineOffset();
    }

    public static void polygonOffset(float f, float f2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._polygonOffset(f, f2);
    }

    public static void enableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._enableColorLogicOp();
    }

    public static void disableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._disableColorLogicOp();
    }

    public static void logicOp(GlStateManager.LogicOp logicOp) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._logicOp(logicOp.value);
    }

    public static void activeTexture(int n) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._activeTexture(n);
    }

    public static void enableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._enableTexture();
    }

    public static void disableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._disableTexture();
    }

    public static void texParameter(int n, int n2, int n3) {
        GlStateManager._texParameter(n, n2, n3);
    }

    public static void deleteTexture(int n) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager._deleteTexture(n);
    }

    public static void bindTexture(int n) {
        GlStateManager._bindTexture(n);
    }

    @Deprecated
    public static void shadeModel(int n) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._shadeModel(n);
    }

    @Deprecated
    public static void enableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._enableRescaleNormal();
    }

    @Deprecated
    public static void disableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._disableRescaleNormal();
    }

    public static void viewport(int n, int n2, int n3, int n4) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager._viewport(n, n2, n3, n4);
    }

    public static void colorMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._colorMask(bl, bl2, bl3, bl4);
    }

    public static void stencilFunc(int n, int n2, int n3) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._stencilFunc(n, n2, n3);
    }

    public static void stencilMask(int n) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._stencilMask(n);
    }

    public static void stencilOp(int n, int n2, int n3) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._stencilOp(n, n2, n3);
    }

    public static void clearDepth(double d) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager._clearDepth(d);
    }

    public static void clearColor(float f, float f2, float f3, float f4) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager._clearColor(f, f2, f3, f4);
    }

    public static void clearStencil(int n) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._clearStencil(n);
    }

    public static void clear(int n, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager._clear(n, bl);
    }

    @Deprecated
    public static void matrixMode(int n) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._matrixMode(n);
    }

    @Deprecated
    public static void loadIdentity() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._loadIdentity();
    }

    @Deprecated
    public static void pushMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._pushMatrix();
    }

    @Deprecated
    public static void popMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._popMatrix();
    }

    @Deprecated
    public static void ortho(double d, double d2, double d3, double d4, double d5, double d6) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._ortho(d, d2, d3, d4, d5, d6);
    }

    @Deprecated
    public static void rotatef(float f, float f2, float f3, float f4) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._rotatef(f, f2, f3, f4);
    }

    @Deprecated
    public static void scalef(float f, float f2, float f3) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._scalef(f, f2, f3);
    }

    @Deprecated
    public static void scaled(double d, double d2, double d3) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._scaled(d, d2, d3);
    }

    @Deprecated
    public static void translatef(float f, float f2, float f3) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._translatef(f, f2, f3);
    }

    @Deprecated
    public static void translated(double d, double d2, double d3) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._translated(d, d2, d3);
    }

    @Deprecated
    public static void multMatrix(Matrix4f matrix4f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._multMatrix(matrix4f);
    }

    @Deprecated
    public static void color4f(float f, float f2, float f3, float f4) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._color4f(f, f2, f3, f4);
    }

    @Deprecated
    public static void color3f(float f, float f2, float f3) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._color4f(f, f2, f3, 1.0f);
    }

    @Deprecated
    public static void clearCurrentColor() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._clearCurrentColor();
    }

    public static void drawArrays(int n, int n2, int n3) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._drawArrays(n, n2, n3);
    }

    public static void lineWidth(float f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._lineWidth(f);
    }

    public static void pixelStore(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        GlStateManager._pixelStore(n, n2);
    }

    public static void pixelTransfer(int n, float f) {
        GlStateManager._pixelTransfer(n, f);
    }

    public static void readPixels(int n, int n2, int n3, int n4, int n5, int n6, ByteBuffer byteBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._readPixels(n, n2, n3, n4, n5, n6, byteBuffer);
    }

    public static void getString(int n, Consumer<String> consumer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        consumer.accept(GlStateManager._getString(n));
    }

    public static String getBackendDescription() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return String.format("LWJGL version %s", GLX._getLWJGLVersion());
    }

    public static String getApiDescription() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return GLX.getOpenGLVersionString();
    }

    public static LongSupplier initBackendSystem() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return GLX._initGlfw();
    }

    public static void initRenderer(int n, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GLX._init(n, bl);
    }

    public static void setErrorCallback(GLFWErrorCallbackI gLFWErrorCallbackI) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GLX._setGlfwErrorCallback(gLFWErrorCallbackI);
    }

    public static void renderCrosshair(int n) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GLX._renderCrosshair(n, true, true, true);
    }

    public static void setupNvFogDistance() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GLX._setupNvFogDistance();
    }

    @Deprecated
    public static void glMultiTexCoord2f(int n, float f, float f2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glMultiTexCoord2f(n, f, f2);
    }

    public static String getCapsString() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        return GLX._getCapsString();
    }

    public static void setupDefaultState(int n, int n2, int n3, int n4) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        GlStateManager._enableTexture();
        GlStateManager._shadeModel(7425);
        GlStateManager._clearDepth(1.0);
        GlStateManager._enableDepthTest();
        GlStateManager._depthFunc(515);
        GlStateManager._enableAlphaTest();
        GlStateManager._alphaFunc(516, 0.1f);
        GlStateManager._matrixMode(5889);
        GlStateManager._loadIdentity();
        GlStateManager._matrixMode(5888);
        GlStateManager._viewport(n, n2, n3, n4);
    }

    public static int maxSupportedTextureSize() {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
            int n = GlStateManager._getInteger(3379);
            for (int i = Math.max((int)32768, (int)n); i >= 1024; i >>= 1) {
                GlStateManager._texImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, null);
                int n2 = GlStateManager._getTexLevelParameter(32868, 0, 4096);
                if (n2 == 0) continue;
                MAX_SUPPORTED_TEXTURE_SIZE = i;
                return i;
            }
            MAX_SUPPORTED_TEXTURE_SIZE = Math.max(n, 1024);
            LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", (Object)MAX_SUPPORTED_TEXTURE_SIZE);
        }
        return MAX_SUPPORTED_TEXTURE_SIZE;
    }

    public static void glBindBuffer(int n, Supplier<Integer> supplier) {
        GlStateManager._glBindBuffer(n, supplier.get());
    }

    public static void glBufferData(int n, ByteBuffer byteBuffer, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager._glBufferData(n, byteBuffer, n2);
    }

    public static void glDeleteBuffers(int n) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glDeleteBuffers(n);
    }

    public static void glUniform1i(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glUniform1i(n, n2);
    }

    public static void glUniform1(int n, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glUniform1(n, intBuffer);
    }

    public static void glUniform2(int n, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glUniform2(n, intBuffer);
    }

    public static void glUniform3(int n, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glUniform3(n, intBuffer);
    }

    public static void glUniform4(int n, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glUniform4(n, intBuffer);
    }

    public static void glUniform1(int n, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glUniform1(n, floatBuffer);
    }

    public static void glUniform2(int n, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glUniform2(n, floatBuffer);
    }

    public static void glUniform3(int n, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glUniform3(n, floatBuffer);
    }

    public static void glUniform4(int n, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glUniform4(n, floatBuffer);
    }

    public static void glUniformMatrix2(int n, boolean bl, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glUniformMatrix2(n, bl, floatBuffer);
    }

    public static void glUniformMatrix3(int n, boolean bl, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glUniformMatrix3(n, bl, floatBuffer);
    }

    public static void glUniformMatrix4(int n, boolean bl, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager._glUniformMatrix4(n, bl, floatBuffer);
    }

    public static void setupOutline() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupOutline();
    }

    public static void teardownOutline() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.teardownOutline();
    }

    public static void setupOverlayColor(IntSupplier intSupplier, int n) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupOverlayColor(intSupplier.getAsInt(), n);
    }

    public static void teardownOverlayColor() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.teardownOverlayColor();
    }

    public static void setupLevelDiffuseLighting(Vector3f vector3f, Vector3f vector3f2, Matrix4f matrix4f) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupLevelDiffuseLighting(vector3f, vector3f2, matrix4f);
    }

    public static void setupGuiFlatDiffuseLighting(Vector3f vector3f, Vector3f vector3f2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupGuiFlatDiffuseLighting(vector3f, vector3f2);
    }

    public static void setupGui3DDiffuseLighting(Vector3f vector3f, Vector3f vector3f2) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupGui3DDiffuseLighting(vector3f, vector3f2);
    }

    public static void mulTextureByProjModelView() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.mulTextureByProjModelView();
    }

    public static void setupEndPortalTexGen() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.setupEndPortalTexGen();
    }

    public static void clearTexGen() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlStateManager.clearTexGen();
    }

    public static void beginInitialization() {
        isInInit = true;
    }

    public static void finishInitialization() {
        isInInit = false;
        if (!recordingQueue.isEmpty()) {
            RenderSystem.replayQueue();
        }
        if (!recordingQueue.isEmpty()) {
            throw new IllegalStateException("Recorded to render queue during initialization");
        }
    }

    public static void glGenBuffers(Consumer<Integer> consumer) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> consumer.accept(GlStateManager._glGenBuffers()));
        } else {
            consumer.accept(GlStateManager._glGenBuffers());
        }
    }

    public static Tesselator renderThreadTesselator() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return RENDER_THREAD_TESSELATOR;
    }

    public static void defaultBlendFunc() {
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }

    public static void defaultAlphaFunc() {
        RenderSystem.alphaFunc(516, 0.1f);
    }

    @Deprecated
    public static void runAsFancy(Runnable runnable) {
        boolean bl = Minecraft.useShaderTransparency();
        if (!bl) {
            runnable.run();
            return;
        }
        Options options = Minecraft.getInstance().options;
        GraphicsStatus graphicsStatus = options.graphicsMode;
        options.graphicsMode = GraphicsStatus.FANCY;
        runnable.run();
        options.graphicsMode = graphicsStatus;
    }

    private static /* synthetic */ void lambda$setupGui3DDiffuseLighting$71(Vector3f vector3f, Vector3f vector3f2) {
        GlStateManager.setupGui3DDiffuseLighting(vector3f, vector3f2);
    }

    private static /* synthetic */ void lambda$setupGuiFlatDiffuseLighting$70(Vector3f vector3f, Vector3f vector3f2) {
        GlStateManager.setupGuiFlatDiffuseLighting(vector3f, vector3f2);
    }

    private static /* synthetic */ void lambda$setupLevelDiffuseLighting$69(Vector3f vector3f, Vector3f vector3f2, Matrix4f matrix4f) {
        GlStateManager.setupLevelDiffuseLighting(vector3f, vector3f2, matrix4f);
    }

    private static /* synthetic */ void lambda$setupOverlayColor$68(IntSupplier intSupplier, int n) {
        GlStateManager.setupOverlayColor(intSupplier.getAsInt(), n);
    }

    private static /* synthetic */ void lambda$glUniformMatrix4$67(int n, boolean bl, FloatBuffer floatBuffer) {
        GlStateManager._glUniformMatrix4(n, bl, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniformMatrix3$66(int n, boolean bl, FloatBuffer floatBuffer) {
        GlStateManager._glUniformMatrix3(n, bl, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniformMatrix2$65(int n, boolean bl, FloatBuffer floatBuffer) {
        GlStateManager._glUniformMatrix2(n, bl, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform4$64(int n, FloatBuffer floatBuffer) {
        GlStateManager._glUniform4(n, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform3$63(int n, FloatBuffer floatBuffer) {
        GlStateManager._glUniform3(n, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform2$62(int n, FloatBuffer floatBuffer) {
        GlStateManager._glUniform2(n, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform1$61(int n, FloatBuffer floatBuffer) {
        GlStateManager._glUniform1(n, floatBuffer);
    }

    private static /* synthetic */ void lambda$glUniform4$60(int n, IntBuffer intBuffer) {
        GlStateManager._glUniform4(n, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform3$59(int n, IntBuffer intBuffer) {
        GlStateManager._glUniform3(n, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform2$58(int n, IntBuffer intBuffer) {
        GlStateManager._glUniform2(n, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform1$57(int n, IntBuffer intBuffer) {
        GlStateManager._glUniform1(n, intBuffer);
    }

    private static /* synthetic */ void lambda$glUniform1i$56(int n, int n2) {
        GlStateManager._glUniform1i(n, n2);
    }

    private static /* synthetic */ void lambda$glDeleteBuffers$55(int n) {
        GlStateManager._glDeleteBuffers(n);
    }

    private static /* synthetic */ void lambda$glBindBuffer$54(int n, Supplier supplier) {
        GlStateManager._glBindBuffer(n, (Integer)supplier.get());
    }

    private static /* synthetic */ void lambda$glMultiTexCoord2f$53(int n, float f, float f2) {
        GlStateManager._glMultiTexCoord2f(n, f, f2);
    }

    private static /* synthetic */ void lambda$renderCrosshair$52(int n) {
        GLX._renderCrosshair(n, true, true, true);
    }

    private static /* synthetic */ void lambda$getString$51(int n, Consumer consumer) {
        String string = GlStateManager._getString(n);
        consumer.accept(string);
    }

    private static /* synthetic */ void lambda$readPixels$50(int n, int n2, int n3, int n4, int n5, int n6, ByteBuffer byteBuffer) {
        GlStateManager._readPixels(n, n2, n3, n4, n5, n6, byteBuffer);
    }

    private static /* synthetic */ void lambda$pixelTransfer$49(int n, float f) {
        GlStateManager._pixelTransfer(n, f);
    }

    private static /* synthetic */ void lambda$pixelStore$48(int n, int n2) {
        GlStateManager._pixelStore(n, n2);
    }

    private static /* synthetic */ void lambda$lineWidth$47(float f) {
        GlStateManager._lineWidth(f);
    }

    private static /* synthetic */ void lambda$drawArrays$46(int n, int n2, int n3) {
        GlStateManager._drawArrays(n, n2, n3);
    }

    private static /* synthetic */ void lambda$color3f$45(float f, float f2, float f3) {
        GlStateManager._color4f(f, f2, f3, 1.0f);
    }

    private static /* synthetic */ void lambda$color4f$44(float f, float f2, float f3, float f4) {
        GlStateManager._color4f(f, f2, f3, f4);
    }

    private static /* synthetic */ void lambda$multMatrix$43(Matrix4f matrix4f) {
        GlStateManager._multMatrix(matrix4f);
    }

    private static /* synthetic */ void lambda$translated$42(double d, double d2, double d3) {
        GlStateManager._translated(d, d2, d3);
    }

    private static /* synthetic */ void lambda$translatef$41(float f, float f2, float f3) {
        GlStateManager._translatef(f, f2, f3);
    }

    private static /* synthetic */ void lambda$scaled$40(double d, double d2, double d3) {
        GlStateManager._scaled(d, d2, d3);
    }

    private static /* synthetic */ void lambda$scalef$39(float f, float f2, float f3) {
        GlStateManager._scalef(f, f2, f3);
    }

    private static /* synthetic */ void lambda$rotatef$38(float f, float f2, float f3, float f4) {
        GlStateManager._rotatef(f, f2, f3, f4);
    }

    private static /* synthetic */ void lambda$ortho$37(double d, double d2, double d3, double d4, double d5, double d6) {
        GlStateManager._ortho(d, d2, d3, d4, d5, d6);
    }

    private static /* synthetic */ void lambda$matrixMode$36(int n) {
        GlStateManager._matrixMode(n);
    }

    private static /* synthetic */ void lambda$clear$35(int n, boolean bl) {
        GlStateManager._clear(n, bl);
    }

    private static /* synthetic */ void lambda$clearStencil$34(int n) {
        GlStateManager._clearStencil(n);
    }

    private static /* synthetic */ void lambda$clearColor$33(float f, float f2, float f3, float f4) {
        GlStateManager._clearColor(f, f2, f3, f4);
    }

    private static /* synthetic */ void lambda$clearDepth$32(double d) {
        GlStateManager._clearDepth(d);
    }

    private static /* synthetic */ void lambda$stencilOp$31(int n, int n2, int n3) {
        GlStateManager._stencilOp(n, n2, n3);
    }

    private static /* synthetic */ void lambda$stencilMask$30(int n) {
        GlStateManager._stencilMask(n);
    }

    private static /* synthetic */ void lambda$stencilFunc$29(int n, int n2, int n3) {
        GlStateManager._stencilFunc(n, n2, n3);
    }

    private static /* synthetic */ void lambda$colorMask$28(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        GlStateManager._colorMask(bl, bl2, bl3, bl4);
    }

    private static /* synthetic */ void lambda$viewport$27(int n, int n2, int n3, int n4) {
        GlStateManager._viewport(n, n2, n3, n4);
    }

    private static /* synthetic */ void lambda$shadeModel$26(int n) {
        GlStateManager._shadeModel(n);
    }

    private static /* synthetic */ void lambda$bindTexture$25(int n) {
        GlStateManager._bindTexture(n);
    }

    private static /* synthetic */ void lambda$deleteTexture$24(int n) {
        GlStateManager._deleteTexture(n);
    }

    private static /* synthetic */ void lambda$texParameter$23(int n, int n2, int n3) {
        GlStateManager._texParameter(n, n2, n3);
    }

    private static /* synthetic */ void lambda$activeTexture$22(int n) {
        GlStateManager._activeTexture(n);
    }

    private static /* synthetic */ void lambda$logicOp$21(GlStateManager.LogicOp logicOp) {
        GlStateManager._logicOp(logicOp.value);
    }

    private static /* synthetic */ void lambda$polygonOffset$20(float f, float f2) {
        GlStateManager._polygonOffset(f, f2);
    }

    private static /* synthetic */ void lambda$polygonMode$19(int n, int n2) {
        GlStateManager._polygonMode(n, n2);
    }

    private static /* synthetic */ void lambda$fogi$18(int n, int n2) {
        GlStateManager._fogi(n, n2);
    }

    private static /* synthetic */ void lambda$fog$17(int n, float f, float f2, float f3, float f4) {
        GlStateManager._fog(n, new float[]{f, f2, f3, f4});
    }

    private static /* synthetic */ void lambda$fogEnd$16(float f) {
        GlStateManager._fogEnd(f);
    }

    private static /* synthetic */ void lambda$fogStart$15(float f) {
        GlStateManager._fogStart(f);
    }

    private static /* synthetic */ void lambda$fogDensity$14(float f) {
        GlStateManager._fogDensity(f);
    }

    private static /* synthetic */ void lambda$fogMode$13(int n) {
        GlStateManager._fogMode(n);
    }

    private static /* synthetic */ void lambda$fogMode$12(GlStateManager.FogMode fogMode) {
        GlStateManager._fogMode(fogMode.value);
    }

    private static /* synthetic */ void lambda$blendColor$11(float f, float f2, float f3, float f4) {
        GlStateManager._blendColor(f, f2, f3, f4);
    }

    private static /* synthetic */ void lambda$blendEquation$10(int n) {
        GlStateManager._blendEquation(n);
    }

    private static /* synthetic */ void lambda$blendFuncSeparate$9(int n, int n2, int n3, int n4) {
        GlStateManager._blendFuncSeparate(n, n2, n3, n4);
    }

    private static /* synthetic */ void lambda$blendFuncSeparate$8(GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor, GlStateManager.SourceFactor sourceFactor2, GlStateManager.DestFactor destFactor2) {
        GlStateManager._blendFuncSeparate(sourceFactor.value, destFactor.value, sourceFactor2.value, destFactor2.value);
    }

    private static /* synthetic */ void lambda$blendFunc$7(int n, int n2) {
        GlStateManager._blendFunc(n, n2);
    }

    private static /* synthetic */ void lambda$blendFunc$6(GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor) {
        GlStateManager._blendFunc(sourceFactor.value, destFactor.value);
    }

    private static /* synthetic */ void lambda$depthMask$5(boolean bl) {
        GlStateManager._depthMask(bl);
    }

    private static /* synthetic */ void lambda$depthFunc$4(int n) {
        GlStateManager._depthFunc(n);
    }

    private static /* synthetic */ void lambda$enableScissor$3(int n, int n2, int n3, int n4) {
        GlStateManager._enableScissorTest();
        GlStateManager._scissorBox(n, n2, n3, n4);
    }

    private static /* synthetic */ void lambda$normal3f$2(float f, float f2, float f3) {
        GlStateManager._normal3f(f, f2, f3);
    }

    private static /* synthetic */ void lambda$colorMaterial$1(int n, int n2) {
        GlStateManager._colorMaterial(n, n2);
    }

    private static /* synthetic */ void lambda$alphaFunc$0(int n, float f) {
        GlStateManager._alphaFunc(n, f);
    }

    static {
        MAX_SUPPORTED_TEXTURE_SIZE = -1;
        lastDrawTime = Double.MIN_VALUE;
    }
}

