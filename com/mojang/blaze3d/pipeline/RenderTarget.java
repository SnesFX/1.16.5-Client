/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

public class RenderTarget {
    public int width;
    public int height;
    public int viewWidth;
    public int viewHeight;
    public final boolean useDepth;
    public int frameBufferId;
    private int colorTextureId;
    private int depthBufferId;
    public final float[] clearChannels;
    public int filterMode;

    public RenderTarget(int n, int n2, boolean bl, boolean bl2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.useDepth = bl;
        this.frameBufferId = -1;
        this.colorTextureId = -1;
        this.depthBufferId = -1;
        this.clearChannels = new float[4];
        this.clearChannels[0] = 1.0f;
        this.clearChannels[1] = 1.0f;
        this.clearChannels[2] = 1.0f;
        this.clearChannels[3] = 0.0f;
        this.resize(n, n2, bl2);
    }

    public void resize(int n, int n2, boolean bl) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this._resize(n, n2, bl));
        } else {
            this._resize(n, n2, bl);
        }
    }

    private void _resize(int n, int n2, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager._enableDepthTest();
        if (this.frameBufferId >= 0) {
            this.destroyBuffers();
        }
        this.createBuffers(n, n2, bl);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
    }

    public void destroyBuffers() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.unbindRead();
        this.unbindWrite();
        if (this.depthBufferId > -1) {
            TextureUtil.releaseTextureId(this.depthBufferId);
            this.depthBufferId = -1;
        }
        if (this.colorTextureId > -1) {
            TextureUtil.releaseTextureId(this.colorTextureId);
            this.colorTextureId = -1;
        }
        if (this.frameBufferId > -1) {
            GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
            GlStateManager._glDeleteFramebuffers(this.frameBufferId);
            this.frameBufferId = -1;
        }
    }

    public void copyDepthFrom(RenderTarget renderTarget) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (GlStateManager.supportsFramebufferBlit()) {
            GlStateManager._glBindFramebuffer(36008, renderTarget.frameBufferId);
            GlStateManager._glBindFramebuffer(36009, this.frameBufferId);
            GlStateManager._glBlitFrameBuffer(0, 0, renderTarget.width, renderTarget.height, 0, 0, this.width, this.height, 256, 9728);
        } else {
            GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, this.frameBufferId);
            int n = GlStateManager.getFramebufferDepthTexture();
            if (n != 0) {
                int n2 = GlStateManager.getActiveTextureName();
                GlStateManager._bindTexture(n);
                GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, renderTarget.frameBufferId);
                GlStateManager._glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, Math.min(this.width, renderTarget.width), Math.min(this.height, renderTarget.height));
                GlStateManager._bindTexture(n2);
            }
        }
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
    }

    public void createBuffers(int n, int n2, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.viewWidth = n;
        this.viewHeight = n2;
        this.width = n;
        this.height = n2;
        this.frameBufferId = GlStateManager.glGenFramebuffers();
        this.colorTextureId = TextureUtil.generateTextureId();
        if (this.useDepth) {
            this.depthBufferId = TextureUtil.generateTextureId();
            GlStateManager._bindTexture(this.depthBufferId);
            GlStateManager._texParameter(3553, 10241, 9728);
            GlStateManager._texParameter(3553, 10240, 9728);
            GlStateManager._texParameter(3553, 10242, 10496);
            GlStateManager._texParameter(3553, 10243, 10496);
            GlStateManager._texParameter(3553, 34892, 0);
            GlStateManager._texImage2D(3553, 0, 6402, this.width, this.height, 0, 6402, 5126, null);
        }
        this.setFilterMode(9728);
        GlStateManager._bindTexture(this.colorTextureId);
        GlStateManager._texImage2D(3553, 0, 32856, this.width, this.height, 0, 6408, 5121, null);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, this.frameBufferId);
        GlStateManager._glFramebufferTexture2D(GlConst.GL_FRAMEBUFFER, GlConst.GL_COLOR_ATTACHMENT0, 3553, this.colorTextureId, 0);
        if (this.useDepth) {
            GlStateManager._glFramebufferTexture2D(GlConst.GL_FRAMEBUFFER, GlConst.GL_DEPTH_ATTACHMENT, 3553, this.depthBufferId, 0);
        }
        this.checkStatus();
        this.clear(bl);
        this.unbindRead();
    }

    public void setFilterMode(int n) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.filterMode = n;
        GlStateManager._bindTexture(this.colorTextureId);
        GlStateManager._texParameter(3553, 10241, n);
        GlStateManager._texParameter(3553, 10240, n);
        GlStateManager._texParameter(3553, 10242, 10496);
        GlStateManager._texParameter(3553, 10243, 10496);
        GlStateManager._bindTexture(0);
    }

    public void checkStatus() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        int n = GlStateManager.glCheckFramebufferStatus(GlConst.GL_FRAMEBUFFER);
        if (n == GlConst.GL_FRAMEBUFFER_COMPLETE) {
            return;
        }
        if (n == GlConst.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
        }
        if (n == GlConst.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
        }
        if (n == GlConst.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
        }
        if (n == GlConst.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
        }
        throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + n);
    }

    public void bindRead() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager._bindTexture(this.colorTextureId);
    }

    public void unbindRead() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager._bindTexture(0);
    }

    public void bindWrite(boolean bl) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this._bindWrite(bl));
        } else {
            this._bindWrite(bl);
        }
    }

    private void _bindWrite(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, this.frameBufferId);
        if (bl) {
            GlStateManager._viewport(0, 0, this.viewWidth, this.viewHeight);
        }
    }

    public void unbindWrite() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0));
        } else {
            GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
        }
    }

    public void setClearColor(float f, float f2, float f3, float f4) {
        this.clearChannels[0] = f;
        this.clearChannels[1] = f2;
        this.clearChannels[2] = f3;
        this.clearChannels[3] = f4;
    }

    public void blitToScreen(int n, int n2) {
        this.blitToScreen(n, n2, true);
    }

    public void blitToScreen(int n, int n2, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
        if (!RenderSystem.isInInitPhase()) {
            RenderSystem.recordRenderCall(() -> this._blitToScreen(n, n2, bl));
        } else {
            this._blitToScreen(n, n2, bl);
        }
    }

    private void _blitToScreen(int n, int n2, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager._colorMask(true, true, true, false);
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);
        GlStateManager._matrixMode(5889);
        GlStateManager._loadIdentity();
        GlStateManager._ortho(0.0, n, n2, 0.0, 1000.0, 3000.0);
        GlStateManager._matrixMode(5888);
        GlStateManager._loadIdentity();
        GlStateManager._translatef(0.0f, 0.0f, -2000.0f);
        GlStateManager._viewport(0, 0, n, n2);
        GlStateManager._enableTexture();
        GlStateManager._disableLighting();
        GlStateManager._disableAlphaTest();
        if (bl) {
            GlStateManager._disableBlend();
            GlStateManager._enableColorMaterial();
        }
        GlStateManager._color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindRead();
        float f = n;
        float f2 = n2;
        float f3 = (float)this.viewWidth / (float)this.width;
        float f4 = (float)this.viewHeight / (float)this.height;
        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.vertex(0.0, f2, 0.0).uv(0.0f, 0.0f).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(f, f2, 0.0).uv(f3, 0.0f).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(f, 0.0, 0.0).uv(f3, f4).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(0.0, 0.0, 0.0).uv(0.0f, f4).color(255, 255, 255, 255).endVertex();
        tesselator.end();
        this.unbindRead();
        GlStateManager._depthMask(true);
        GlStateManager._colorMask(true, true, true, true);
    }

    public void clear(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.bindWrite(true);
        GlStateManager._clearColor(this.clearChannels[0], this.clearChannels[1], this.clearChannels[2], this.clearChannels[3]);
        int n = 16384;
        if (this.useDepth) {
            GlStateManager._clearDepth(1.0);
            n |= 0x100;
        }
        GlStateManager._clear(n, bl);
        this.unbindWrite();
    }

    public int getColorTextureId() {
        return this.colorTextureId;
    }

    public int getDepthTextureId() {
        return this.depthBufferId;
    }
}

