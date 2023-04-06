/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public abstract class AbstractTexture
implements AutoCloseable {
    protected int id = -1;
    protected boolean blur;
    protected boolean mipmap;

    public void setFilter(boolean bl, boolean bl2) {
        int n;
        int n2;
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        this.blur = bl;
        this.mipmap = bl2;
        if (bl) {
            n = bl2 ? 9987 : 9729;
            n2 = 9729;
        } else {
            n = bl2 ? 9986 : 9728;
            n2 = 9728;
        }
        GlStateManager._texParameter(3553, 10241, n);
        GlStateManager._texParameter(3553, 10240, n2);
    }

    public int getId() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (this.id == -1) {
            this.id = TextureUtil.generateTextureId();
        }
        return this.id;
    }

    public void releaseId() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                if (this.id != -1) {
                    TextureUtil.releaseTextureId(this.id);
                    this.id = -1;
                }
            });
        } else if (this.id != -1) {
            TextureUtil.releaseTextureId(this.id);
            this.id = -1;
        }
    }

    public abstract void load(ResourceManager var1) throws IOException;

    public void bind() {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> GlStateManager._bindTexture(this.getId()));
        } else {
            GlStateManager._bindTexture(this.getId());
        }
    }

    public void reset(TextureManager textureManager, ResourceManager resourceManager, ResourceLocation resourceLocation, Executor executor) {
        textureManager.register(resourceLocation, this);
    }

    @Override
    public void close() {
    }
}

