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
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;

public final class MissingTextureAtlasSprite
extends TextureAtlasSprite {
    private static final ResourceLocation MISSING_TEXTURE_LOCATION = new ResourceLocation("missingno");
    @Nullable
    private static DynamicTexture missingTexture;
    private static final LazyLoadedValue<NativeImage> MISSING_IMAGE_DATA;
    private static final TextureAtlasSprite.Info INFO;

    private MissingTextureAtlasSprite(TextureAtlas textureAtlas, int n, int n2, int n3, int n4, int n5) {
        super(textureAtlas, INFO, n, n2, n3, n4, n5, MISSING_IMAGE_DATA.get());
    }

    public static MissingTextureAtlasSprite newInstance(TextureAtlas textureAtlas, int n, int n2, int n3, int n4, int n5) {
        return new MissingTextureAtlasSprite(textureAtlas, n, n2, n3, n4, n5);
    }

    public static ResourceLocation getLocation() {
        return MISSING_TEXTURE_LOCATION;
    }

    public static TextureAtlasSprite.Info info() {
        return INFO;
    }

    @Override
    public void close() {
        for (int i = 1; i < this.mainImage.length; ++i) {
            this.mainImage[i].close();
        }
    }

    public static DynamicTexture getTexture() {
        if (missingTexture == null) {
            missingTexture = new DynamicTexture(MISSING_IMAGE_DATA.get());
            Minecraft.getInstance().getTextureManager().register(MISSING_TEXTURE_LOCATION, (AbstractTexture)missingTexture);
        }
        return missingTexture;
    }

    static {
        MISSING_IMAGE_DATA = new LazyLoadedValue<NativeImage>(() -> {
            NativeImage nativeImage = new NativeImage(16, 16, false);
            int n = -16777216;
            int n2 = -524040;
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (i < 8 ^ j < 8) {
                        nativeImage.setPixelRGBA(j, i, -524040);
                        continue;
                    }
                    nativeImage.setPixelRGBA(j, i, -16777216);
                }
            }
            nativeImage.untrack();
            return nativeImage;
        });
        INFO = new TextureAtlasSprite.Info(MISSING_TEXTURE_LOCATION, 16, 16, new AnimationMetadataSection(Lists.newArrayList((Object[])new AnimationFrame[]{new AnimationFrame(0, -1)}), 16, 16, 1, false));
    }
}

