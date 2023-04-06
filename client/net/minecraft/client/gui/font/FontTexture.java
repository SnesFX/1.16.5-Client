/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class FontTexture
extends AbstractTexture {
    private final ResourceLocation name;
    private final RenderType normalType;
    private final RenderType seeThroughType;
    private final boolean colored;
    private final Node root;

    public FontTexture(ResourceLocation resourceLocation, boolean bl) {
        this.name = resourceLocation;
        this.colored = bl;
        this.root = new Node(0, 0, 256, 256);
        TextureUtil.prepareImage(bl ? NativeImage.InternalGlFormat.RGBA : NativeImage.InternalGlFormat.INTENSITY, this.getId(), 256, 256);
        this.normalType = RenderType.text(resourceLocation);
        this.seeThroughType = RenderType.textSeeThrough(resourceLocation);
    }

    @Override
    public void load(ResourceManager resourceManager) {
    }

    @Override
    public void close() {
        this.releaseId();
    }

    @Nullable
    public BakedGlyph add(RawGlyph rawGlyph) {
        if (rawGlyph.isColored() != this.colored) {
            return null;
        }
        Node node = this.root.insert(rawGlyph);
        if (node != null) {
            this.bind();
            rawGlyph.upload(node.x, node.y);
            float f = 256.0f;
            float f2 = 256.0f;
            float f3 = 0.01f;
            return new BakedGlyph(this.normalType, this.seeThroughType, ((float)node.x + 0.01f) / 256.0f, ((float)node.x - 0.01f + (float)rawGlyph.getPixelWidth()) / 256.0f, ((float)node.y + 0.01f) / 256.0f, ((float)node.y - 0.01f + (float)rawGlyph.getPixelHeight()) / 256.0f, rawGlyph.getLeft(), rawGlyph.getRight(), rawGlyph.getUp(), rawGlyph.getDown());
        }
        return null;
    }

    public ResourceLocation getName() {
        return this.name;
    }

    static class Node {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private Node left;
        private Node right;
        private boolean occupied;

        private Node(int n, int n2, int n3, int n4) {
            this.x = n;
            this.y = n2;
            this.width = n3;
            this.height = n4;
        }

        @Nullable
        Node insert(RawGlyph rawGlyph) {
            if (this.left != null && this.right != null) {
                Node node = this.left.insert(rawGlyph);
                if (node == null) {
                    node = this.right.insert(rawGlyph);
                }
                return node;
            }
            if (this.occupied) {
                return null;
            }
            int n = rawGlyph.getPixelWidth();
            int n2 = rawGlyph.getPixelHeight();
            if (n > this.width || n2 > this.height) {
                return null;
            }
            if (n == this.width && n2 == this.height) {
                this.occupied = true;
                return this;
            }
            int n3 = this.width - n;
            int n4 = this.height - n2;
            if (n3 > n4) {
                this.left = new Node(this.x, this.y, n, this.height);
                this.right = new Node(this.x + n + 1, this.y, this.width - n - 1, this.height);
            } else {
                this.left = new Node(this.x, this.y, this.width, n2);
                this.right = new Node(this.x, this.y + n2 + 1, this.width, this.height - n2 - 1);
            }
            return this.left.insert(rawGlyph);
        }
    }

}

