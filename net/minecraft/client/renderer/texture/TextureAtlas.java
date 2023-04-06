/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.PngInfo;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.StitcherException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSectionSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureAtlas
extends AbstractTexture
implements Tickable {
    private static final Logger LOGGER = LogManager.getLogger();
    @Deprecated
    public static final ResourceLocation LOCATION_BLOCKS = InventoryMenu.BLOCK_ATLAS;
    @Deprecated
    public static final ResourceLocation LOCATION_PARTICLES = new ResourceLocation("textures/atlas/particles.png");
    private final List<TextureAtlasSprite> animatedTextures = Lists.newArrayList();
    private final Set<ResourceLocation> sprites = Sets.newHashSet();
    private final Map<ResourceLocation, TextureAtlasSprite> texturesByName = Maps.newHashMap();
    private final ResourceLocation location;
    private final int maxSupportedTextureSize;

    public TextureAtlas(ResourceLocation resourceLocation) {
        this.location = resourceLocation;
        this.maxSupportedTextureSize = RenderSystem.maxSupportedTextureSize();
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
    }

    public void reload(Preparations preparations) {
        this.sprites.clear();
        this.sprites.addAll(preparations.sprites);
        LOGGER.info("Created: {}x{}x{} {}-atlas", (Object)preparations.width, (Object)preparations.height, (Object)preparations.mipLevel, (Object)this.location);
        TextureUtil.prepareImage(this.getId(), preparations.mipLevel, preparations.width, preparations.height);
        this.clearTextureData();
        for (TextureAtlasSprite textureAtlasSprite : preparations.regions) {
            this.texturesByName.put(textureAtlasSprite.getName(), textureAtlasSprite);
            try {
                textureAtlasSprite.uploadFirstFrame();
            }
            catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.forThrowable(throwable, "Stitching texture atlas");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Texture being stitched together");
                crashReportCategory.setDetail("Atlas path", this.location);
                crashReportCategory.setDetail("Sprite", textureAtlasSprite);
                throw new ReportedException(crashReport);
            }
            if (!textureAtlasSprite.isAnimation()) continue;
            this.animatedTextures.add(textureAtlasSprite);
        }
    }

    public Preparations prepareToStitch(ResourceManager resourceManager, Stream<ResourceLocation> stream, ProfilerFiller profilerFiller, int n) {
        int n2;
        profilerFiller.push("preparing");
        Set<ResourceLocation> set = stream.peek(resourceLocation -> {
            if (resourceLocation == null) {
                throw new IllegalArgumentException("Location cannot be null!");
            }
        }).collect(Collectors.toSet());
        int n3 = this.maxSupportedTextureSize;
        Stitcher stitcher = new Stitcher(n3, n3, n);
        int n4 = Integer.MAX_VALUE;
        int n5 = 1 << n;
        profilerFiller.popPush("extracting_frames");
        for (TextureAtlasSprite.Info info2 : this.getBasicSpriteInfos(resourceManager, set)) {
            n4 = Math.min(n4, Math.min(info2.width(), info2.height()));
            n2 = Math.min(Integer.lowestOneBit(info2.width()), Integer.lowestOneBit(info2.height()));
            if (n2 < n5) {
                LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", (Object)info2.name(), (Object)info2.width(), (Object)info2.height(), (Object)Mth.log2(n5), (Object)Mth.log2(n2));
                n5 = n2;
            }
            stitcher.registerSprite(info2);
        }
        int n6 = Math.min(n4, n5);
        int n7 = Mth.log2(n6);
        if (n7 < n) {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", (Object)this.location, (Object)n, (Object)n7, (Object)n6);
            n2 = n7;
        } else {
            n2 = n;
        }
        profilerFiller.popPush("register");
        stitcher.registerSprite(MissingTextureAtlasSprite.info());
        profilerFiller.popPush("stitching");
        try {
            stitcher.stitch();
        }
        catch (StitcherException stitcherException) {
            CrashReport crashReport = CrashReport.forThrowable(stitcherException, "Stitching");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Stitcher");
            crashReportCategory.setDetail("Sprites", stitcherException.getAllSprites().stream().map(info -> String.format("%s[%dx%d]", info.name(), info.width(), info.height())).collect(Collectors.joining(",")));
            crashReportCategory.setDetail("Max Texture Size", n3);
            throw new ReportedException(crashReport);
        }
        profilerFiller.popPush("loading");
        List<TextureAtlasSprite> list = this.getLoadedSprites(resourceManager, stitcher, n2);
        profilerFiller.pop();
        return new Preparations(set, stitcher.getWidth(), stitcher.getHeight(), n2, list);
    }

    private Collection<TextureAtlasSprite.Info> getBasicSpriteInfos(ResourceManager resourceManager, Set<ResourceLocation> set) {
        ArrayList arrayList = Lists.newArrayList();
        ConcurrentLinkedQueue<TextureAtlasSprite.Info> concurrentLinkedQueue = new ConcurrentLinkedQueue<TextureAtlasSprite.Info>();
        for (ResourceLocation resourceLocation : set) {
            if (MissingTextureAtlasSprite.getLocation().equals(resourceLocation)) continue;
            arrayList.add(CompletableFuture.runAsync(() -> {
                TextureAtlasSprite.Info info;
                ResourceLocation resourceLocation2 = this.getResourceLocation(resourceLocation);
                try {
                    try (Resource resource = resourceManager.getResource(resourceLocation2);){
                        PngInfo pngInfo = new PngInfo(resource.toString(), resource.getInputStream());
                        AnimationMetadataSection animationMetadataSection = resource.getMetadata(AnimationMetadataSection.SERIALIZER);
                        if (animationMetadataSection == null) {
                            animationMetadataSection = AnimationMetadataSection.EMPTY;
                        }
                        Pair<Integer, Integer> pair = animationMetadataSection.getFrameSize(pngInfo.width, pngInfo.height);
                        info = new TextureAtlasSprite.Info(resourceLocation, (Integer)pair.getFirst(), (Integer)pair.getSecond(), animationMetadataSection);
                    }
                }
                catch (RuntimeException runtimeException) {
                    LOGGER.error("Unable to parse metadata from {} : {}", (Object)resourceLocation2, (Object)runtimeException);
                    return;
                }
                catch (IOException iOException) {
                    LOGGER.error("Using missing texture, unable to load {} : {}", (Object)resourceLocation2, (Object)iOException);
                    return;
                }
                concurrentLinkedQueue.add(info);
            }, Util.backgroundExecutor()));
        }
        CompletableFuture.allOf(arrayList.toArray(new CompletableFuture[0])).join();
        return concurrentLinkedQueue;
    }

    private List<TextureAtlasSprite> getLoadedSprites(ResourceManager resourceManager, Stitcher stitcher, int n) {
        ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();
        ArrayList arrayList = Lists.newArrayList();
        stitcher.gatherSprites((info, n2, n3, n4, n5) -> {
            if (info == MissingTextureAtlasSprite.info()) {
                MissingTextureAtlasSprite missingTextureAtlasSprite = MissingTextureAtlasSprite.newInstance(this, n, n2, n3, n4, n5);
                concurrentLinkedQueue.add(missingTextureAtlasSprite);
            } else {
                arrayList.add(CompletableFuture.runAsync(() -> {
                    TextureAtlasSprite textureAtlasSprite = this.load(resourceManager, info, n2, n3, n, n4, n5);
                    if (textureAtlasSprite != null) {
                        concurrentLinkedQueue.add(textureAtlasSprite);
                    }
                }, Util.backgroundExecutor()));
            }
        });
        CompletableFuture.allOf(arrayList.toArray(new CompletableFuture[0])).join();
        return Lists.newArrayList(concurrentLinkedQueue);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private TextureAtlasSprite load(ResourceManager resourceManager, TextureAtlasSprite.Info info, int n, int n2, int n3, int n4, int n5) {
        ResourceLocation resourceLocation = this.getResourceLocation(info.name());
        try {
            try (Resource resource = resourceManager.getResource(resourceLocation);){
                NativeImage nativeImage = NativeImage.read(resource.getInputStream());
                TextureAtlasSprite textureAtlasSprite = new TextureAtlasSprite(this, info, n3, n, n2, n4, n5, nativeImage);
                return textureAtlasSprite;
            }
        }
        catch (RuntimeException runtimeException) {
            LOGGER.error("Unable to parse metadata from {}", (Object)resourceLocation, (Object)runtimeException);
            return null;
        }
        catch (IOException iOException) {
            LOGGER.error("Using missing texture, unable to load {}", (Object)resourceLocation, (Object)iOException);
            return null;
        }
    }

    private ResourceLocation getResourceLocation(ResourceLocation resourceLocation) {
        return new ResourceLocation(resourceLocation.getNamespace(), String.format("textures/%s%s", resourceLocation.getPath(), ".png"));
    }

    public void cycleAnimationFrames() {
        this.bind();
        for (TextureAtlasSprite textureAtlasSprite : this.animatedTextures) {
            textureAtlasSprite.cycleFrames();
        }
    }

    @Override
    public void tick() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::cycleAnimationFrames);
        } else {
            this.cycleAnimationFrames();
        }
    }

    public TextureAtlasSprite getSprite(ResourceLocation resourceLocation) {
        TextureAtlasSprite textureAtlasSprite = this.texturesByName.get(resourceLocation);
        if (textureAtlasSprite == null) {
            return this.texturesByName.get(MissingTextureAtlasSprite.getLocation());
        }
        return textureAtlasSprite;
    }

    public void clearTextureData() {
        for (TextureAtlasSprite textureAtlasSprite : this.texturesByName.values()) {
            textureAtlasSprite.close();
        }
        this.texturesByName.clear();
        this.animatedTextures.clear();
    }

    public ResourceLocation location() {
        return this.location;
    }

    public void updateFilter(Preparations preparations) {
        this.setFilter(false, preparations.mipLevel > 0);
    }

    public static class Preparations {
        final Set<ResourceLocation> sprites;
        final int width;
        final int height;
        final int mipLevel;
        final List<TextureAtlasSprite> regions;

        public Preparations(Set<ResourceLocation> set, int n, int n2, int n3, List<TextureAtlasSprite> list) {
            this.sprites = set;
            this.width = n;
            this.height = n2;
            this.mipLevel = n3;
            this.regions = list;
        }
    }

}

