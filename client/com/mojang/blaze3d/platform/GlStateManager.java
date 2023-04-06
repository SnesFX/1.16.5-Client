/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.lwjgl.opengl.ARBFramebufferObject
 *  org.lwjgl.opengl.EXTFramebufferBlit
 *  org.lwjgl.opengl.EXTFramebufferObject
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.DebugMemoryUntracker;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

public class GlStateManager {
    private static final FloatBuffer MATRIX_BUFFER = GLX.make(MemoryUtil.memAllocFloat((int)16), floatBuffer -> DebugMemoryUntracker.untrack(MemoryUtil.memAddress((FloatBuffer)floatBuffer)));
    private static final AlphaState ALPHA_TEST = new AlphaState();
    private static final BooleanState LIGHTING = new BooleanState(2896);
    private static final BooleanState[] LIGHT_ENABLE = (BooleanState[])IntStream.range(0, 8).mapToObj(n -> new BooleanState(16384 + n)).toArray(n -> new BooleanState[n]);
    private static final ColorMaterialState COLOR_MATERIAL = new ColorMaterialState();
    private static final BlendState BLEND = new BlendState();
    private static final DepthState DEPTH = new DepthState();
    private static final FogState FOG = new FogState();
    private static final CullState CULL = new CullState();
    private static final PolygonOffsetState POLY_OFFSET = new PolygonOffsetState();
    private static final ColorLogicState COLOR_LOGIC = new ColorLogicState();
    private static final TexGenState TEX_GEN = new TexGenState();
    private static final StencilState STENCIL = new StencilState();
    private static final ScissorState SCISSOR = new ScissorState();
    private static final FloatBuffer FLOAT_ARG_BUFFER = MemoryTracker.createFloatBuffer(4);
    private static int activeTexture;
    private static final TextureState[] TEXTURES;
    private static int shadeModel;
    private static final BooleanState RESCALE_NORMAL;
    private static final ColorMask COLOR_MASK;
    private static final Color COLOR;
    private static FboMode fboMode;
    private static FboBlitMode fboBlitMode;

    @Deprecated
    public static void _pushLightingAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushAttrib((int)8256);
    }

    @Deprecated
    public static void _pushTextureAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushAttrib((int)270336);
    }

    @Deprecated
    public static void _popAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPopAttrib();
    }

    @Deprecated
    public static void _disableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.ALPHA_TEST.mode.disable();
    }

    @Deprecated
    public static void _enableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.ALPHA_TEST.mode.enable();
    }

    @Deprecated
    public static void _alphaFunc(int n, float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (n != GlStateManager.ALPHA_TEST.func || f != GlStateManager.ALPHA_TEST.reference) {
            GlStateManager.ALPHA_TEST.func = n;
            GlStateManager.ALPHA_TEST.reference = f;
            GL11.glAlphaFunc((int)n, (float)f);
        }
    }

    @Deprecated
    public static void _enableLighting() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHTING.enable();
    }

    @Deprecated
    public static void _disableLighting() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHTING.disable();
    }

    @Deprecated
    public static void _enableLight(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHT_ENABLE[n].enable();
    }

    @Deprecated
    public static void _enableColorMaterial() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_MATERIAL.enable.enable();
    }

    @Deprecated
    public static void _disableColorMaterial() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_MATERIAL.enable.disable();
    }

    @Deprecated
    public static void _colorMaterial(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (n != GlStateManager.COLOR_MATERIAL.face || n2 != GlStateManager.COLOR_MATERIAL.mode) {
            GlStateManager.COLOR_MATERIAL.face = n;
            GlStateManager.COLOR_MATERIAL.mode = n2;
            GL11.glColorMaterial((int)n, (int)n2);
        }
    }

    @Deprecated
    public static void _light(int n, int n2, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLightfv((int)n, (int)n2, (FloatBuffer)floatBuffer);
    }

    @Deprecated
    public static void _lightModel(int n, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLightModelfv((int)n, (FloatBuffer)floatBuffer);
    }

    @Deprecated
    public static void _normal3f(float f, float f2, float f3) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glNormal3f((float)f, (float)f2, (float)f3);
    }

    public static void _disableScissorTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.SCISSOR.mode.disable();
    }

    public static void _enableScissorTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.SCISSOR.mode.enable();
    }

    public static void _scissorBox(int n, int n2, int n3, int n4) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL20.glScissor((int)n, (int)n2, (int)n3, (int)n4);
    }

    public static void _disableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.DEPTH.mode.disable();
    }

    public static void _enableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.DEPTH.mode.enable();
    }

    public static void _depthFunc(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (n != GlStateManager.DEPTH.func) {
            GlStateManager.DEPTH.func = n;
            GL11.glDepthFunc((int)n);
        }
    }

    public static void _depthMask(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (bl != GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = bl;
            GL11.glDepthMask((boolean)bl);
        }
    }

    public static void _disableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.BLEND.mode.disable();
    }

    public static void _enableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.BLEND.mode.enable();
    }

    public static void _blendFunc(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (n != GlStateManager.BLEND.srcRgb || n2 != GlStateManager.BLEND.dstRgb) {
            GlStateManager.BLEND.srcRgb = n;
            GlStateManager.BLEND.dstRgb = n2;
            GL11.glBlendFunc((int)n, (int)n2);
        }
    }

    public static void _blendFuncSeparate(int n, int n2, int n3, int n4) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (n != GlStateManager.BLEND.srcRgb || n2 != GlStateManager.BLEND.dstRgb || n3 != GlStateManager.BLEND.srcAlpha || n4 != GlStateManager.BLEND.dstAlpha) {
            GlStateManager.BLEND.srcRgb = n;
            GlStateManager.BLEND.dstRgb = n2;
            GlStateManager.BLEND.srcAlpha = n3;
            GlStateManager.BLEND.dstAlpha = n4;
            GlStateManager.glBlendFuncSeparate(n, n2, n3, n4);
        }
    }

    public static void _blendColor(float f, float f2, float f3, float f4) {
        GL14.glBlendColor((float)f, (float)f2, (float)f3, (float)f4);
    }

    public static void _blendEquation(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL14.glBlendEquation((int)n);
    }

    public static String _init_fbo(GLCapabilities gLCapabilities) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        fboBlitMode = gLCapabilities.OpenGL30 ? FboBlitMode.BASE : (gLCapabilities.GL_EXT_framebuffer_blit ? FboBlitMode.EXT : FboBlitMode.NONE);
        if (gLCapabilities.OpenGL30) {
            fboMode = FboMode.BASE;
            GlConst.GL_FRAMEBUFFER = 36160;
            GlConst.GL_RENDERBUFFER = 36161;
            GlConst.GL_COLOR_ATTACHMENT0 = 36064;
            GlConst.GL_DEPTH_ATTACHMENT = 36096;
            GlConst.GL_FRAMEBUFFER_COMPLETE = 36053;
            GlConst.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            GlConst.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            GlConst.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            GlConst.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "OpenGL 3.0";
        }
        if (gLCapabilities.GL_ARB_framebuffer_object) {
            fboMode = FboMode.ARB;
            GlConst.GL_FRAMEBUFFER = 36160;
            GlConst.GL_RENDERBUFFER = 36161;
            GlConst.GL_COLOR_ATTACHMENT0 = 36064;
            GlConst.GL_DEPTH_ATTACHMENT = 36096;
            GlConst.GL_FRAMEBUFFER_COMPLETE = 36053;
            GlConst.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            GlConst.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            GlConst.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            GlConst.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "ARB_framebuffer_object extension";
        }
        if (gLCapabilities.GL_EXT_framebuffer_object) {
            fboMode = FboMode.EXT;
            GlConst.GL_FRAMEBUFFER = 36160;
            GlConst.GL_RENDERBUFFER = 36161;
            GlConst.GL_COLOR_ATTACHMENT0 = 36064;
            GlConst.GL_DEPTH_ATTACHMENT = 36096;
            GlConst.GL_FRAMEBUFFER_COMPLETE = 36053;
            GlConst.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            GlConst.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
            GlConst.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            GlConst.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "EXT_framebuffer_object extension";
        }
        throw new IllegalStateException("Could not initialize framebuffer support.");
    }

    public static int glGetProgrami(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetProgrami((int)n, (int)n2);
    }

    public static void glAttachShader(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glAttachShader((int)n, (int)n2);
    }

    public static void glDeleteShader(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glDeleteShader((int)n);
    }

    public static int glCreateShader(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glCreateShader((int)n);
    }

    public static void glShaderSource(int n, CharSequence charSequence) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glShaderSource((int)n, (CharSequence)charSequence);
    }

    public static void glCompileShader(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glCompileShader((int)n);
    }

    public static int glGetShaderi(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetShaderi((int)n, (int)n2);
    }

    public static void _glUseProgram(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUseProgram((int)n);
    }

    public static int glCreateProgram() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glCreateProgram();
    }

    public static void glDeleteProgram(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glDeleteProgram((int)n);
    }

    public static void glLinkProgram(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glLinkProgram((int)n);
    }

    public static int _glGetUniformLocation(int n, CharSequence charSequence) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetUniformLocation((int)n, (CharSequence)charSequence);
    }

    public static void _glUniform1(int n, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1iv((int)n, (IntBuffer)intBuffer);
    }

    public static void _glUniform1i(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1i((int)n, (int)n2);
    }

    public static void _glUniform1(int n, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1fv((int)n, (FloatBuffer)floatBuffer);
    }

    public static void _glUniform2(int n, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform2iv((int)n, (IntBuffer)intBuffer);
    }

    public static void _glUniform2(int n, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform2fv((int)n, (FloatBuffer)floatBuffer);
    }

    public static void _glUniform3(int n, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform3iv((int)n, (IntBuffer)intBuffer);
    }

    public static void _glUniform3(int n, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform3fv((int)n, (FloatBuffer)floatBuffer);
    }

    public static void _glUniform4(int n, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform4iv((int)n, (IntBuffer)intBuffer);
    }

    public static void _glUniform4(int n, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform4fv((int)n, (FloatBuffer)floatBuffer);
    }

    public static void _glUniformMatrix2(int n, boolean bl, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix2fv((int)n, (boolean)bl, (FloatBuffer)floatBuffer);
    }

    public static void _glUniformMatrix3(int n, boolean bl, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix3fv((int)n, (boolean)bl, (FloatBuffer)floatBuffer);
    }

    public static void _glUniformMatrix4(int n, boolean bl, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix4fv((int)n, (boolean)bl, (FloatBuffer)floatBuffer);
    }

    public static int _glGetAttribLocation(int n, CharSequence charSequence) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetAttribLocation((int)n, (CharSequence)charSequence);
    }

    public static int _glGenBuffers() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL15.glGenBuffers();
    }

    public static void _glBindBuffer(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glBindBuffer((int)n, (int)n2);
    }

    public static void _glBufferData(int n, ByteBuffer byteBuffer, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glBufferData((int)n, (ByteBuffer)byteBuffer, (int)n2);
    }

    public static void _glDeleteBuffers(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL15.glDeleteBuffers((int)n);
    }

    public static void _glCopyTexSubImage2D(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL20.glCopyTexSubImage2D((int)n, (int)n2, (int)n3, (int)n4, (int)n5, (int)n6, (int)n7, (int)n8);
    }

    public static void _glBindFramebuffer(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glBindFramebuffer((int)n, (int)n2);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glBindFramebuffer((int)n, (int)n2);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glBindFramebufferEXT((int)n, (int)n2);
            }
        }
    }

    public static int getFramebufferDepthTexture() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                if (GL30.glGetFramebufferAttachmentParameteri((int)36160, (int)36096, (int)36048) != 5890) break;
                return GL30.glGetFramebufferAttachmentParameteri((int)36160, (int)36096, (int)36049);
            }
            case ARB: {
                if (ARBFramebufferObject.glGetFramebufferAttachmentParameteri((int)36160, (int)36096, (int)36048) != 5890) break;
                return ARBFramebufferObject.glGetFramebufferAttachmentParameteri((int)36160, (int)36096, (int)36049);
            }
            case EXT: {
                if (EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT((int)36160, (int)36096, (int)36048) != 5890) break;
                return EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT((int)36160, (int)36096, (int)36049);
            }
        }
        return 0;
    }

    public static void _glBlitFrameBuffer(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboBlitMode) {
            case BASE: {
                GL30.glBlitFramebuffer((int)n, (int)n2, (int)n3, (int)n4, (int)n5, (int)n6, (int)n7, (int)n8, (int)n9, (int)n10);
                break;
            }
            case EXT: {
                EXTFramebufferBlit.glBlitFramebufferEXT((int)n, (int)n2, (int)n3, (int)n4, (int)n5, (int)n6, (int)n7, (int)n8, (int)n9, (int)n10);
                break;
            }
        }
    }

    public static void _glDeleteFramebuffers(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glDeleteFramebuffers((int)n);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glDeleteFramebuffers((int)n);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glDeleteFramebuffersEXT((int)n);
            }
        }
    }

    public static int glGenFramebuffers() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                return GL30.glGenFramebuffers();
            }
            case ARB: {
                return ARBFramebufferObject.glGenFramebuffers();
            }
            case EXT: {
                return EXTFramebufferObject.glGenFramebuffersEXT();
            }
        }
        return -1;
    }

    public static int glCheckFramebufferStatus(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                return GL30.glCheckFramebufferStatus((int)n);
            }
            case ARB: {
                return ARBFramebufferObject.glCheckFramebufferStatus((int)n);
            }
            case EXT: {
                return EXTFramebufferObject.glCheckFramebufferStatusEXT((int)n);
            }
        }
        return -1;
    }

    public static void _glFramebufferTexture2D(int n, int n2, int n3, int n4, int n5) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glFramebufferTexture2D((int)n, (int)n2, (int)n3, (int)n4, (int)n5);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glFramebufferTexture2D((int)n, (int)n2, (int)n3, (int)n4, (int)n5);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glFramebufferTexture2DEXT((int)n, (int)n2, (int)n3, (int)n4, (int)n5);
            }
        }
    }

    @Deprecated
    public static int getActiveTextureName() {
        return GlStateManager.TEXTURES[GlStateManager.activeTexture].binding;
    }

    public static void glActiveTexture(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glActiveTexture((int)n);
    }

    @Deprecated
    public static void _glClientActiveTexture(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glClientActiveTexture((int)n);
    }

    @Deprecated
    public static void _glMultiTexCoord2f(int n, float f, float f2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glMultiTexCoord2f((int)n, (float)f, (float)f2);
    }

    public static void glBlendFuncSeparate(int n, int n2, int n3, int n4) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL14.glBlendFuncSeparate((int)n, (int)n2, (int)n3, (int)n4);
    }

    public static String glGetShaderInfoLog(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetShaderInfoLog((int)n, (int)n2);
    }

    public static String glGetProgramInfoLog(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetProgramInfoLog((int)n, (int)n2);
    }

    public static void setupOutline() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager._texEnv(8960, 8704, 34160);
        GlStateManager.color1arg(7681, 34168);
    }

    public static void teardownOutline() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager._texEnv(8960, 8704, 8448);
        GlStateManager.color3arg(8448, 5890, 34168, 34166);
    }

    public static void setupOverlayColor(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager._activeTexture(33985);
        GlStateManager._enableTexture();
        GlStateManager._matrixMode(5890);
        GlStateManager._loadIdentity();
        float f = 1.0f / (float)(n2 - 1);
        GlStateManager._scalef(f, f, f);
        GlStateManager._matrixMode(5888);
        GlStateManager._bindTexture(n);
        GlStateManager._texParameter(3553, 10241, 9728);
        GlStateManager._texParameter(3553, 10240, 9728);
        GlStateManager._texParameter(3553, 10242, 10496);
        GlStateManager._texParameter(3553, 10243, 10496);
        GlStateManager._texEnv(8960, 8704, 34160);
        GlStateManager.color3arg(34165, 34168, 5890, 5890);
        GlStateManager.alpha1arg(7681, 34168);
        GlStateManager._activeTexture(33984);
    }

    public static void teardownOverlayColor() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager._activeTexture(33985);
        GlStateManager._disableTexture();
        GlStateManager._activeTexture(33984);
    }

    private static void color1arg(int n, int n2) {
        GlStateManager._texEnv(8960, 34161, n);
        GlStateManager._texEnv(8960, 34176, n2);
        GlStateManager._texEnv(8960, 34192, 768);
    }

    private static void color3arg(int n, int n2, int n3, int n4) {
        GlStateManager._texEnv(8960, 34161, n);
        GlStateManager._texEnv(8960, 34176, n2);
        GlStateManager._texEnv(8960, 34192, 768);
        GlStateManager._texEnv(8960, 34177, n3);
        GlStateManager._texEnv(8960, 34193, 768);
        GlStateManager._texEnv(8960, 34178, n4);
        GlStateManager._texEnv(8960, 34194, 770);
    }

    private static void alpha1arg(int n, int n2) {
        GlStateManager._texEnv(8960, 34162, n);
        GlStateManager._texEnv(8960, 34184, n2);
        GlStateManager._texEnv(8960, 34200, 770);
    }

    public static void setupLevelDiffuseLighting(Vector3f vector3f, Vector3f vector3f2, Matrix4f matrix4f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager._pushMatrix();
        GlStateManager._loadIdentity();
        GlStateManager._enableLight(0);
        GlStateManager._enableLight(1);
        Vector4f vector4f = new Vector4f(vector3f);
        vector4f.transform(matrix4f);
        GlStateManager._light(16384, 4611, GlStateManager.getBuffer(vector4f.x(), vector4f.y(), vector4f.z(), 0.0f));
        float f = 0.6f;
        GlStateManager._light(16384, 4609, GlStateManager.getBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        GlStateManager._light(16384, 4608, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager._light(16384, 4610, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        Vector4f vector4f2 = new Vector4f(vector3f2);
        vector4f2.transform(matrix4f);
        GlStateManager._light(16385, 4611, GlStateManager.getBuffer(vector4f2.x(), vector4f2.y(), vector4f2.z(), 0.0f));
        GlStateManager._light(16385, 4609, GlStateManager.getBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        GlStateManager._light(16385, 4608, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager._light(16385, 4610, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager._shadeModel(7424);
        float f2 = 0.4f;
        GlStateManager._lightModel(2899, GlStateManager.getBuffer(0.4f, 0.4f, 0.4f, 1.0f));
        GlStateManager._popMatrix();
    }

    public static void setupGuiFlatDiffuseLighting(Vector3f vector3f, Vector3f vector3f2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        matrix4f.multiply(Matrix4f.createScaleMatrix(1.0f, -1.0f, 1.0f));
        matrix4f.multiply(Vector3f.YP.rotationDegrees(-22.5f));
        matrix4f.multiply(Vector3f.XP.rotationDegrees(135.0f));
        GlStateManager.setupLevelDiffuseLighting(vector3f, vector3f2, matrix4f);
    }

    public static void setupGui3DDiffuseLighting(Vector3f vector3f, Vector3f vector3f2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        matrix4f.multiply(Vector3f.YP.rotationDegrees(62.0f));
        matrix4f.multiply(Vector3f.XP.rotationDegrees(185.5f));
        matrix4f.multiply(Matrix4f.createScaleMatrix(1.0f, -1.0f, 1.0f));
        matrix4f.multiply(Vector3f.YP.rotationDegrees(-22.5f));
        matrix4f.multiply(Vector3f.XP.rotationDegrees(135.0f));
        GlStateManager.setupLevelDiffuseLighting(vector3f, vector3f2, matrix4f);
    }

    private static FloatBuffer getBuffer(float f, float f2, float f3, float f4) {
        FLOAT_ARG_BUFFER.clear();
        FLOAT_ARG_BUFFER.put(f).put(f2).put(f3).put(f4);
        FLOAT_ARG_BUFFER.flip();
        return FLOAT_ARG_BUFFER;
    }

    public static void setupEndPortalTexGen() {
        GlStateManager._texGenMode(TexGen.S, 9216);
        GlStateManager._texGenMode(TexGen.T, 9216);
        GlStateManager._texGenMode(TexGen.R, 9216);
        GlStateManager._texGenParam(TexGen.S, 9474, GlStateManager.getBuffer(1.0f, 0.0f, 0.0f, 0.0f));
        GlStateManager._texGenParam(TexGen.T, 9474, GlStateManager.getBuffer(0.0f, 1.0f, 0.0f, 0.0f));
        GlStateManager._texGenParam(TexGen.R, 9474, GlStateManager.getBuffer(0.0f, 0.0f, 1.0f, 0.0f));
        GlStateManager._enableTexGen(TexGen.S);
        GlStateManager._enableTexGen(TexGen.T);
        GlStateManager._enableTexGen(TexGen.R);
    }

    public static void clearTexGen() {
        GlStateManager._disableTexGen(TexGen.S);
        GlStateManager._disableTexGen(TexGen.T);
        GlStateManager._disableTexGen(TexGen.R);
    }

    public static void mulTextureByProjModelView() {
        GlStateManager._getMatrix(2983, MATRIX_BUFFER);
        GlStateManager._multMatrix(MATRIX_BUFFER);
        GlStateManager._getMatrix(2982, MATRIX_BUFFER);
        GlStateManager._multMatrix(MATRIX_BUFFER);
    }

    @Deprecated
    public static void _enableFog() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.FOG.enable.enable();
    }

    @Deprecated
    public static void _disableFog() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.FOG.enable.disable();
    }

    @Deprecated
    public static void _fogMode(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (n != GlStateManager.FOG.mode) {
            GlStateManager.FOG.mode = n;
            GlStateManager._fogi(2917, n);
        }
    }

    @Deprecated
    public static void _fogDensity(float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (f != GlStateManager.FOG.density) {
            GlStateManager.FOG.density = f;
            GL11.glFogf((int)2914, (float)f);
        }
    }

    @Deprecated
    public static void _fogStart(float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (f != GlStateManager.FOG.start) {
            GlStateManager.FOG.start = f;
            GL11.glFogf((int)2915, (float)f);
        }
    }

    @Deprecated
    public static void _fogEnd(float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (f != GlStateManager.FOG.end) {
            GlStateManager.FOG.end = f;
            GL11.glFogf((int)2916, (float)f);
        }
    }

    @Deprecated
    public static void _fog(int n, float[] arrf) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glFogfv((int)n, (float[])arrf);
    }

    @Deprecated
    public static void _fogi(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glFogi((int)n, (int)n2);
    }

    public static void _enableCull() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.CULL.enable.enable();
    }

    public static void _disableCull() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.CULL.enable.disable();
    }

    public static void _polygonMode(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPolygonMode((int)n, (int)n2);
    }

    public static void _enablePolygonOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.fill.enable();
    }

    public static void _disablePolygonOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.fill.disable();
    }

    public static void _enableLineOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.line.enable();
    }

    public static void _disableLineOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.line.disable();
    }

    public static void _polygonOffset(float f, float f2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (f != GlStateManager.POLY_OFFSET.factor || f2 != GlStateManager.POLY_OFFSET.units) {
            GlStateManager.POLY_OFFSET.factor = f;
            GlStateManager.POLY_OFFSET.units = f2;
            GL11.glPolygonOffset((float)f, (float)f2);
        }
    }

    public static void _enableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_LOGIC.enable.enable();
    }

    public static void _disableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_LOGIC.enable.disable();
    }

    public static void _logicOp(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (n != GlStateManager.COLOR_LOGIC.op) {
            GlStateManager.COLOR_LOGIC.op = n;
            GL11.glLogicOp((int)n);
        }
    }

    @Deprecated
    public static void _enableTexGen(TexGen texGen) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.getTexGen((TexGen)texGen).enable.enable();
    }

    @Deprecated
    public static void _disableTexGen(TexGen texGen) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.getTexGen((TexGen)texGen).enable.disable();
    }

    @Deprecated
    public static void _texGenMode(TexGen texGen, int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        TexGenCoord texGenCoord = GlStateManager.getTexGen(texGen);
        if (n != texGenCoord.mode) {
            texGenCoord.mode = n;
            GL11.glTexGeni((int)texGenCoord.coord, (int)9472, (int)n);
        }
    }

    @Deprecated
    public static void _texGenParam(TexGen texGen, int n, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexGenfv((int)GlStateManager.getTexGen((TexGen)texGen).coord, (int)n, (FloatBuffer)floatBuffer);
    }

    @Deprecated
    private static TexGenCoord getTexGen(TexGen texGen) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        switch (texGen) {
            case S: {
                return GlStateManager.TEX_GEN.s;
            }
            case T: {
                return GlStateManager.TEX_GEN.t;
            }
            case R: {
                return GlStateManager.TEX_GEN.r;
            }
            case Q: {
                return GlStateManager.TEX_GEN.q;
            }
        }
        return GlStateManager.TEX_GEN.s;
    }

    public static void _activeTexture(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (activeTexture != n - 33984) {
            activeTexture = n - 33984;
            GlStateManager.glActiveTexture(n);
        }
    }

    public static void _enableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.TEXTURES[GlStateManager.activeTexture].enable.enable();
    }

    public static void _disableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.TEXTURES[GlStateManager.activeTexture].enable.disable();
    }

    @Deprecated
    public static void _texEnv(int n, int n2, int n3) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexEnvi((int)n, (int)n2, (int)n3);
    }

    public static void _texParameter(int n, int n2, float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexParameterf((int)n, (int)n2, (float)f);
    }

    public static void _texParameter(int n, int n2, int n3) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexParameteri((int)n, (int)n2, (int)n3);
    }

    public static int _getTexLevelParameter(int n, int n2, int n3) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return GL11.glGetTexLevelParameteri((int)n, (int)n2, (int)n3);
    }

    public static int _genTexture() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL11.glGenTextures();
    }

    public static void _genTextures(int[] arrn) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glGenTextures((int[])arrn);
    }

    public static void _deleteTexture(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glDeleteTextures((int)n);
        for (TextureState textureState : TEXTURES) {
            if (textureState.binding != n) continue;
            textureState.binding = -1;
        }
    }

    public static void _deleteTextures(int[] arrn) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        for (TextureState textureState : TEXTURES) {
            for (int n : arrn) {
                if (textureState.binding != n) continue;
                textureState.binding = -1;
            }
        }
        GL11.glDeleteTextures((int[])arrn);
    }

    public static void _bindTexture(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (n != GlStateManager.TEXTURES[GlStateManager.activeTexture].binding) {
            GlStateManager.TEXTURES[GlStateManager.activeTexture].binding = n;
            GL11.glBindTexture((int)3553, (int)n);
        }
    }

    public static void _texImage2D(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, @Nullable IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexImage2D((int)n, (int)n2, (int)n3, (int)n4, (int)n5, (int)n6, (int)n7, (int)n8, (IntBuffer)intBuffer);
    }

    public static void _texSubImage2D(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexSubImage2D((int)n, (int)n2, (int)n3, (int)n4, (int)n5, (int)n6, (int)n7, (int)n8, (long)l);
    }

    public static void _getTexImage(int n, int n2, int n3, int n4, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glGetTexImage((int)n, (int)n2, (int)n3, (int)n4, (long)l);
    }

    @Deprecated
    public static void _shadeModel(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (n != shadeModel) {
            shadeModel = n;
            GL11.glShadeModel((int)n);
        }
    }

    @Deprecated
    public static void _enableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        RESCALE_NORMAL.enable();
    }

    @Deprecated
    public static void _disableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        RESCALE_NORMAL.disable();
    }

    public static void _viewport(int n, int n2, int n3, int n4) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        Viewport.INSTANCE.x = n;
        Viewport.INSTANCE.y = n2;
        Viewport.INSTANCE.width = n3;
        Viewport.INSTANCE.height = n4;
        GL11.glViewport((int)n, (int)n2, (int)n3, (int)n4);
    }

    public static void _colorMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (bl != GlStateManager.COLOR_MASK.red || bl2 != GlStateManager.COLOR_MASK.green || bl3 != GlStateManager.COLOR_MASK.blue || bl4 != GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = bl;
            GlStateManager.COLOR_MASK.green = bl2;
            GlStateManager.COLOR_MASK.blue = bl3;
            GlStateManager.COLOR_MASK.alpha = bl4;
            GL11.glColorMask((boolean)bl, (boolean)bl2, (boolean)bl3, (boolean)bl4);
        }
    }

    public static void _stencilFunc(int n, int n2, int n3) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (n != GlStateManager.STENCIL.func.func || n != GlStateManager.STENCIL.func.ref || n != GlStateManager.STENCIL.func.mask) {
            GlStateManager.STENCIL.func.func = n;
            GlStateManager.STENCIL.func.ref = n2;
            GlStateManager.STENCIL.func.mask = n3;
            GL11.glStencilFunc((int)n, (int)n2, (int)n3);
        }
    }

    public static void _stencilMask(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (n != GlStateManager.STENCIL.mask) {
            GlStateManager.STENCIL.mask = n;
            GL11.glStencilMask((int)n);
        }
    }

    public static void _stencilOp(int n, int n2, int n3) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (n != GlStateManager.STENCIL.fail || n2 != GlStateManager.STENCIL.zfail || n3 != GlStateManager.STENCIL.zpass) {
            GlStateManager.STENCIL.fail = n;
            GlStateManager.STENCIL.zfail = n2;
            GlStateManager.STENCIL.zpass = n3;
            GL11.glStencilOp((int)n, (int)n2, (int)n3);
        }
    }

    public static void _clearDepth(double d) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClearDepth((double)d);
    }

    public static void _clearColor(float f, float f2, float f3, float f4) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClearColor((float)f, (float)f2, (float)f3, (float)f4);
    }

    public static void _clearStencil(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glClearStencil((int)n);
    }

    public static void _clear(int n, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClear((int)n);
        if (bl) {
            GlStateManager._getError();
        }
    }

    @Deprecated
    public static void _matrixMode(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glMatrixMode((int)n);
    }

    @Deprecated
    public static void _loadIdentity() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glLoadIdentity();
    }

    @Deprecated
    public static void _pushMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushMatrix();
    }

    @Deprecated
    public static void _popMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPopMatrix();
    }

    @Deprecated
    public static void _getMatrix(int n, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glGetFloatv((int)n, (FloatBuffer)floatBuffer);
    }

    @Deprecated
    public static void _ortho(double d, double d2, double d3, double d4, double d5, double d6) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glOrtho((double)d, (double)d2, (double)d3, (double)d4, (double)d5, (double)d6);
    }

    @Deprecated
    public static void _rotatef(float f, float f2, float f3, float f4) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glRotatef((float)f, (float)f2, (float)f3, (float)f4);
    }

    @Deprecated
    public static void _scalef(float f, float f2, float f3) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glScalef((float)f, (float)f2, (float)f3);
    }

    @Deprecated
    public static void _scaled(double d, double d2, double d3) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glScaled((double)d, (double)d2, (double)d3);
    }

    @Deprecated
    public static void _translatef(float f, float f2, float f3) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTranslatef((float)f, (float)f2, (float)f3);
    }

    @Deprecated
    public static void _translated(double d, double d2, double d3) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTranslated((double)d, (double)d2, (double)d3);
    }

    @Deprecated
    public static void _multMatrix(FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glMultMatrixf((FloatBuffer)floatBuffer);
    }

    @Deprecated
    public static void _multMatrix(Matrix4f matrix4f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        matrix4f.store(MATRIX_BUFFER);
        MATRIX_BUFFER.rewind();
        GlStateManager._multMatrix(MATRIX_BUFFER);
    }

    @Deprecated
    public static void _color4f(float f, float f2, float f3, float f4) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (f != GlStateManager.COLOR.r || f2 != GlStateManager.COLOR.g || f3 != GlStateManager.COLOR.b || f4 != GlStateManager.COLOR.a) {
            GlStateManager.COLOR.r = f;
            GlStateManager.COLOR.g = f2;
            GlStateManager.COLOR.b = f3;
            GlStateManager.COLOR.a = f4;
            GL11.glColor4f((float)f, (float)f2, (float)f3, (float)f4);
        }
    }

    @Deprecated
    public static void _clearCurrentColor() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR.r = -1.0f;
        GlStateManager.COLOR.g = -1.0f;
        GlStateManager.COLOR.b = -1.0f;
        GlStateManager.COLOR.a = -1.0f;
    }

    @Deprecated
    public static void _normalPointer(int n, int n2, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glNormalPointer((int)n, (int)n2, (long)l);
    }

    @Deprecated
    public static void _texCoordPointer(int n, int n2, int n3, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexCoordPointer((int)n, (int)n2, (int)n3, (long)l);
    }

    @Deprecated
    public static void _vertexPointer(int n, int n2, int n3, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glVertexPointer((int)n, (int)n2, (int)n3, (long)l);
    }

    @Deprecated
    public static void _colorPointer(int n, int n2, int n3, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glColorPointer((int)n, (int)n2, (int)n3, (long)l);
    }

    public static void _vertexAttribPointer(int n, int n2, int n3, boolean bl, int n4, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glVertexAttribPointer((int)n, (int)n2, (int)n3, (boolean)bl, (int)n4, (long)l);
    }

    @Deprecated
    public static void _enableClientState(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glEnableClientState((int)n);
    }

    @Deprecated
    public static void _disableClientState(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glDisableClientState((int)n);
    }

    public static void _enableVertexAttribArray(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glEnableVertexAttribArray((int)n);
    }

    public static void _disableVertexAttribArray(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glEnableVertexAttribArray((int)n);
    }

    public static void _drawArrays(int n, int n2, int n3) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glDrawArrays((int)n, (int)n2, (int)n3);
    }

    public static void _lineWidth(float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLineWidth((float)f);
    }

    public static void _pixelStore(int n, int n2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glPixelStorei((int)n, (int)n2);
    }

    public static void _pixelTransfer(int n, float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPixelTransferf((int)n, (float)f);
    }

    public static void _readPixels(int n, int n2, int n3, int n4, int n5, int n6, ByteBuffer byteBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glReadPixels((int)n, (int)n2, (int)n3, (int)n4, (int)n5, (int)n6, (ByteBuffer)byteBuffer);
    }

    public static int _getError() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL11.glGetError();
    }

    public static String _getString(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL11.glGetString((int)n);
    }

    public static int _getInteger(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL11.glGetInteger((int)n);
    }

    public static boolean supportsFramebufferBlit() {
        return fboBlitMode != FboBlitMode.NONE;
    }

    static {
        TEXTURES = (TextureState[])IntStream.range(0, 12).mapToObj(n -> new TextureState()).toArray(n -> new TextureState[n]);
        shadeModel = 7425;
        RESCALE_NORMAL = new BooleanState(32826);
        COLOR_MASK = new ColorMask();
        COLOR = new Color();
    }

    public static enum DestFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);
        
        public final int value;

        private DestFactor(int n2) {
            this.value = n2;
        }
    }

    public static enum SourceFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);
        
        public final int value;

        private SourceFactor(int n2) {
            this.value = n2;
        }
    }

    static class BooleanState {
        private final int state;
        private boolean enabled;

        public BooleanState(int n) {
            this.state = n;
        }

        public void disable() {
            this.setEnabled(false);
        }

        public void enable() {
            this.setEnabled(true);
        }

        public void setEnabled(boolean bl) {
            RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
            if (bl != this.enabled) {
                this.enabled = bl;
                if (bl) {
                    GL11.glEnable((int)this.state);
                } else {
                    GL11.glDisable((int)this.state);
                }
            }
        }
    }

    @Deprecated
    static class Color {
        public float r = 1.0f;
        public float g = 1.0f;
        public float b = 1.0f;
        public float a = 1.0f;

        public Color() {
            this(1.0f, 1.0f, 1.0f, 1.0f);
        }

        public Color(float f, float f2, float f3, float f4) {
            this.r = f;
            this.g = f2;
            this.b = f3;
            this.a = f4;
        }
    }

    static class ColorMask {
        public boolean red = true;
        public boolean green = true;
        public boolean blue = true;
        public boolean alpha = true;

        private ColorMask() {
        }
    }

    @Deprecated
    public static enum TexGen {
        S,
        T,
        R,
        Q;
        
    }

    @Deprecated
    static class TexGenCoord {
        public final BooleanState enable;
        public final int coord;
        public int mode = -1;

        public TexGenCoord(int n, int n2) {
            this.coord = n;
            this.enable = new BooleanState(n2);
        }
    }

    @Deprecated
    static class TexGenState {
        public final TexGenCoord s = new TexGenCoord(8192, 3168);
        public final TexGenCoord t = new TexGenCoord(8193, 3169);
        public final TexGenCoord r = new TexGenCoord(8194, 3170);
        public final TexGenCoord q = new TexGenCoord(8195, 3171);

        private TexGenState() {
        }
    }

    static class ScissorState {
        public final BooleanState mode = new BooleanState(3089);

        private ScissorState() {
        }
    }

    static class StencilState {
        public final StencilFunc func = new StencilFunc();
        public int mask = -1;
        public int fail = 7680;
        public int zfail = 7680;
        public int zpass = 7680;

        private StencilState() {
        }
    }

    static class StencilFunc {
        public int func = 519;
        public int ref;
        public int mask = -1;

        private StencilFunc() {
        }
    }

    static class ColorLogicState {
        public final BooleanState enable = new BooleanState(3058);
        public int op = 5379;

        private ColorLogicState() {
        }
    }

    static class PolygonOffsetState {
        public final BooleanState fill = new BooleanState(32823);
        public final BooleanState line = new BooleanState(10754);
        public float factor;
        public float units;

        private PolygonOffsetState() {
        }
    }

    static class CullState {
        public final BooleanState enable = new BooleanState(2884);
        public int mode = 1029;

        private CullState() {
        }
    }

    @Deprecated
    static class FogState {
        public final BooleanState enable = new BooleanState(2912);
        public int mode = 2048;
        public float density = 1.0f;
        public float start;
        public float end = 1.0f;

        private FogState() {
        }
    }

    static class DepthState {
        public final BooleanState mode = new BooleanState(2929);
        public boolean mask = true;
        public int func = 513;

        private DepthState() {
        }
    }

    static class BlendState {
        public final BooleanState mode = new BooleanState(3042);
        public int srcRgb = 1;
        public int dstRgb = 0;
        public int srcAlpha = 1;
        public int dstAlpha = 0;

        private BlendState() {
        }
    }

    @Deprecated
    static class ColorMaterialState {
        public final BooleanState enable = new BooleanState(2903);
        public int face = 1032;
        public int mode = 5634;

        private ColorMaterialState() {
        }
    }

    @Deprecated
    static class AlphaState {
        public final BooleanState mode = new BooleanState(3008);
        public int func = 519;
        public float reference = -1.0f;

        private AlphaState() {
        }
    }

    static class TextureState {
        public final BooleanState enable = new BooleanState(3553);
        public int binding;

        private TextureState() {
        }
    }

    public static enum FboBlitMode {
        BASE,
        EXT,
        NONE;
        
    }

    public static enum FboMode {
        BASE,
        ARB,
        EXT;
        
    }

    public static enum Viewport {
        INSTANCE;
        
        protected int x;
        protected int y;
        protected int width;
        protected int height;
    }

    public static enum LogicOp {
        AND(5377),
        AND_INVERTED(5380),
        AND_REVERSE(5378),
        CLEAR(5376),
        COPY(5379),
        COPY_INVERTED(5388),
        EQUIV(5385),
        INVERT(5386),
        NAND(5390),
        NOOP(5381),
        NOR(5384),
        OR(5383),
        OR_INVERTED(5389),
        OR_REVERSE(5387),
        SET(5391),
        XOR(5382);
        
        public final int value;

        private LogicOp(int n2) {
            this.value = n2;
        }
    }

    @Deprecated
    public static enum FogMode {
        LINEAR(9729),
        EXP(2048),
        EXP2(2049);
        
        public final int value;

        private FogMode(int n2) {
            this.value = n2;
        }
    }

}

