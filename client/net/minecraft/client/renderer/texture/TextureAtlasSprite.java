/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;

public class TextureAtlasSprite
implements AutoCloseable {
    private final TextureAtlas atlas;
    private final Info info;
    private final AnimationMetadataSection metadata;
    protected final NativeImage[] mainImage;
    private final int[] framesX;
    private final int[] framesY;
    @Nullable
    private final InterpolationData interpolationData;
    private final int x;
    private final int y;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;
    private int frame;
    private int subFrame;

    protected TextureAtlasSprite(TextureAtlas textureAtlas, Info info, int n, int n2, int n3, int n4, int n5, NativeImage nativeImage) {
        Object object;
        int n6;
        this.atlas = textureAtlas;
        AnimationMetadataSection animationMetadataSection = info.metadata;
        int n7 = info.width;
        int n8 = info.height;
        this.x = n4;
        this.y = n5;
        this.u0 = (float)n4 / (float)n2;
        this.u1 = (float)(n4 + n7) / (float)n2;
        this.v0 = (float)n5 / (float)n3;
        this.v1 = (float)(n5 + n8) / (float)n3;
        int n9 = nativeImage.getWidth() / animationMetadataSection.getFrameWidth(n7);
        int n10 = nativeImage.getHeight() / animationMetadataSection.getFrameHeight(n8);
        if (animationMetadataSection.getFrameCount() > 0) {
            int n11 = (Integer)animationMetadataSection.getUniqueFrameIndices().stream().max(Integer::compareTo).get() + 1;
            this.framesX = new int[n11];
            this.framesY = new int[n11];
            Arrays.fill(this.framesX, -1);
            Arrays.fill(this.framesY, -1);
            object = animationMetadataSection.getUniqueFrameIndices().iterator();
            while (object.hasNext()) {
                int n12;
                n6 = object.next();
                if (n6 >= n9 * n10) {
                    throw new RuntimeException("invalid frameindex " + n6);
                }
                int n13 = n6 / n9;
                this.framesX[n6] = n12 = n6 % n9;
                this.framesY[n6] = n13;
            }
        } else {
            ArrayList arrayList = Lists.newArrayList();
            int n14 = n9 * n10;
            this.framesX = new int[n14];
            this.framesY = new int[n14];
            for (n6 = 0; n6 < n10; ++n6) {
                int n15 = 0;
                while (n15 < n9) {
                    int n16 = n6 * n9 + n15;
                    this.framesX[n16] = n15++;
                    this.framesY[n16] = n6;
                    arrayList.add(new AnimationFrame(n16, -1));
                }
            }
            animationMetadataSection = new AnimationMetadataSection(arrayList, n7, n8, animationMetadataSection.getDefaultFrameTime(), animationMetadataSection.isInterpolatedFrames());
        }
        this.info = new Info(info.name, n7, n8, animationMetadataSection);
        this.metadata = animationMetadataSection;
        try {
            try {
                this.mainImage = MipmapGenerator.generateMipLevels(nativeImage, n);
            }
            catch (Throwable throwable) {
                object = CrashReport.forThrowable(throwable, "Generating mipmaps for frame");
                CrashReportCategory crashReportCategory = ((CrashReport)object).addCategory("Frame being iterated");
                crashReportCategory.setDetail("First frame", () -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(nativeImage.getWidth()).append("x").append(nativeImage.getHeight());
                    return stringBuilder.toString();
                });
                throw new ReportedException((CrashReport)object);
            }
        }
        catch (Throwable throwable) {
            object = CrashReport.forThrowable(throwable, "Applying mipmap");
            CrashReportCategory crashReportCategory = ((CrashReport)object).addCategory("Sprite being mipmapped");
            crashReportCategory.setDetail("Sprite name", () -> this.getName().toString());
            crashReportCategory.setDetail("Sprite size", () -> this.getWidth() + " x " + this.getHeight());
            crashReportCategory.setDetail("Sprite frames", () -> this.getFrameCount() + " frames");
            crashReportCategory.setDetail("Mipmap levels", n);
            throw new ReportedException((CrashReport)object);
        }
        this.interpolationData = animationMetadataSection.isInterpolatedFrames() ? new InterpolationData(info, n) : null;
    }

    private void upload(int n) {
        int n2 = this.framesX[n] * this.info.width;
        int n3 = this.framesY[n] * this.info.height;
        this.upload(n2, n3, this.mainImage);
    }

    private void upload(int n, int n2, NativeImage[] arrnativeImage) {
        for (int i = 0; i < this.mainImage.length; ++i) {
            arrnativeImage[i].upload(i, this.x >> i, this.y >> i, n >> i, n2 >> i, this.info.width >> i, this.info.height >> i, this.mainImage.length > 1, false);
        }
    }

    public int getWidth() {
        return this.info.width;
    }

    public int getHeight() {
        return this.info.height;
    }

    public float getU0() {
        return this.u0;
    }

    public float getU1() {
        return this.u1;
    }

    public float getU(double d) {
        float f = this.u1 - this.u0;
        return this.u0 + f * (float)d / 16.0f;
    }

    public float getV0() {
        return this.v0;
    }

    public float getV1() {
        return this.v1;
    }

    public float getV(double d) {
        float f = this.v1 - this.v0;
        return this.v0 + f * (float)d / 16.0f;
    }

    public ResourceLocation getName() {
        return this.info.name;
    }

    public TextureAtlas atlas() {
        return this.atlas;
    }

    public int getFrameCount() {
        return this.framesX.length;
    }

    @Override
    public void close() {
        for (NativeImage nativeImage : this.mainImage) {
            if (nativeImage == null) continue;
            nativeImage.close();
        }
        if (this.interpolationData != null) {
            this.interpolationData.close();
        }
    }

    public String toString() {
        int n = this.framesX.length;
        return "TextureAtlasSprite{name='" + this.info.name + '\'' + ", frameCount=" + n + ", x=" + this.x + ", y=" + this.y + ", height=" + this.info.height + ", width=" + this.info.width + ", u0=" + this.u0 + ", u1=" + this.u1 + ", v0=" + this.v0 + ", v1=" + this.v1 + '}';
    }

    public boolean isTransparent(int n, int n2, int n3) {
        return (this.mainImage[0].getPixelRGBA(n2 + this.framesX[n] * this.info.width, n3 + this.framesY[n] * this.info.height) >> 24 & 0xFF) == 0;
    }

    public void uploadFirstFrame() {
        this.upload(0);
    }

    private float atlasSize() {
        float f = (float)this.info.width / (this.u1 - this.u0);
        float f2 = (float)this.info.height / (this.v1 - this.v0);
        return Math.max(f2, f);
    }

    public float uvShrinkRatio() {
        return 4.0f / this.atlasSize();
    }

    public void cycleFrames() {
        ++this.subFrame;
        if (this.subFrame >= this.metadata.getFrameTime(this.frame)) {
            int n = this.metadata.getFrameIndex(this.frame);
            int n2 = this.metadata.getFrameCount() == 0 ? this.getFrameCount() : this.metadata.getFrameCount();
            this.frame = (this.frame + 1) % n2;
            this.subFrame = 0;
            int n3 = this.metadata.getFrameIndex(this.frame);
            if (n != n3 && n3 >= 0 && n3 < this.getFrameCount()) {
                this.upload(n3);
            }
        } else if (this.interpolationData != null) {
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> this.interpolationData.uploadInterpolatedFrame());
            } else {
                this.interpolationData.uploadInterpolatedFrame();
            }
        }
    }

    public boolean isAnimation() {
        return this.metadata.getFrameCount() > 1;
    }

    public VertexConsumer wrap(VertexConsumer vertexConsumer) {
        return new SpriteCoordinateExpander(vertexConsumer, this);
    }

    final class InterpolationData
    implements AutoCloseable {
        private final NativeImage[] activeFrame;

        private InterpolationData(Info info, int n) {
            this.activeFrame = new NativeImage[n + 1];
            for (int i = 0; i < this.activeFrame.length; ++i) {
                int n2 = info.width >> i;
                int n3 = info.height >> i;
                if (this.activeFrame[i] != null) continue;
                this.activeFrame[i] = new NativeImage(n2, n3, false);
            }
        }

        private void uploadInterpolatedFrame() {
            double d = 1.0 - (double)TextureAtlasSprite.this.subFrame / (double)TextureAtlasSprite.this.metadata.getFrameTime(TextureAtlasSprite.this.frame);
            int n = TextureAtlasSprite.this.metadata.getFrameIndex(TextureAtlasSprite.this.frame);
            int n2 = TextureAtlasSprite.this.metadata.getFrameCount() == 0 ? TextureAtlasSprite.this.getFrameCount() : TextureAtlasSprite.this.metadata.getFrameCount();
            int n3 = TextureAtlasSprite.this.metadata.getFrameIndex((TextureAtlasSprite.this.frame + 1) % n2);
            if (n != n3 && n3 >= 0 && n3 < TextureAtlasSprite.this.getFrameCount()) {
                for (int i = 0; i < this.activeFrame.length; ++i) {
                    int n4 = TextureAtlasSprite.this.info.width >> i;
                    int n5 = TextureAtlasSprite.this.info.height >> i;
                    for (int j = 0; j < n5; ++j) {
                        for (int k = 0; k < n4; ++k) {
                            int n6 = this.getPixel(n, i, k, j);
                            int n7 = this.getPixel(n3, i, k, j);
                            int n8 = this.mix(d, n6 >> 16 & 0xFF, n7 >> 16 & 0xFF);
                            int n9 = this.mix(d, n6 >> 8 & 0xFF, n7 >> 8 & 0xFF);
                            int n10 = this.mix(d, n6 & 0xFF, n7 & 0xFF);
                            this.activeFrame[i].setPixelRGBA(k, j, n6 & 0xFF000000 | n8 << 16 | n9 << 8 | n10);
                        }
                    }
                }
                TextureAtlasSprite.this.upload(0, 0, this.activeFrame);
            }
        }

        private int getPixel(int n, int n2, int n3, int n4) {
            return TextureAtlasSprite.this.mainImage[n2].getPixelRGBA(n3 + (TextureAtlasSprite.this.framesX[n] * TextureAtlasSprite.this.info.width >> n2), n4 + (TextureAtlasSprite.this.framesY[n] * TextureAtlasSprite.this.info.height >> n2));
        }

        private int mix(double d, int n, int n2) {
            return (int)(d * (double)n + (1.0 - d) * (double)n2);
        }

        @Override
        public void close() {
            for (NativeImage nativeImage : this.activeFrame) {
                if (nativeImage == null) continue;
                nativeImage.close();
            }
        }
    }

    public static final class Info {
        private final ResourceLocation name;
        private final int width;
        private final int height;
        private final AnimationMetadataSection metadata;

        public Info(ResourceLocation resourceLocation, int n, int n2, AnimationMetadataSection animationMetadataSection) {
            this.name = resourceLocation;
            this.width = n;
            this.height = n2;
            this.metadata = animationMetadataSection;
        }

        public ResourceLocation name() {
            return this.name;
        }

        public int width() {
            return this.width;
        }

        public int height() {
            return this.height;
        }
    }

}

