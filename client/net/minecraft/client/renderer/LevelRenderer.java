/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectCollection
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.Executor;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RunningTrimmedMean;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.BorderStatus;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelRenderer
implements ResourceManagerReloadListener,
AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation MOON_LOCATION = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation CLOUDS_LOCATION = new ResourceLocation("textures/environment/clouds.png");
    private static final ResourceLocation END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation FORCEFIELD_LOCATION = new ResourceLocation("textures/misc/forcefield.png");
    private static final ResourceLocation RAIN_LOCATION = new ResourceLocation("textures/environment/rain.png");
    private static final ResourceLocation SNOW_LOCATION = new ResourceLocation("textures/environment/snow.png");
    public static final Direction[] DIRECTIONS = Direction.values();
    private final Minecraft minecraft;
    private final TextureManager textureManager;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final RenderBuffers renderBuffers;
    private ClientLevel level;
    private Set<ChunkRenderDispatcher.RenderChunk> chunksToCompile = Sets.newLinkedHashSet();
    private final ObjectList<RenderChunkInfo> renderChunks = new ObjectArrayList(69696);
    private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
    private ViewArea viewArea;
    private final VertexFormat skyFormat = DefaultVertexFormat.POSITION;
    @Nullable
    private VertexBuffer starBuffer;
    @Nullable
    private VertexBuffer skyBuffer;
    @Nullable
    private VertexBuffer darkBuffer;
    private boolean generateClouds = true;
    @Nullable
    private VertexBuffer cloudBuffer;
    private final RunningTrimmedMean frameTimes = new RunningTrimmedMean(100);
    private int ticks;
    private final Int2ObjectMap<BlockDestructionProgress> destroyingBlocks = new Int2ObjectOpenHashMap();
    private final Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress = new Long2ObjectOpenHashMap();
    private final Map<BlockPos, SoundInstance> playingRecords = Maps.newHashMap();
    @Nullable
    private RenderTarget entityTarget;
    @Nullable
    private PostChain entityEffect;
    @Nullable
    private RenderTarget translucentTarget;
    @Nullable
    private RenderTarget itemEntityTarget;
    @Nullable
    private RenderTarget particlesTarget;
    @Nullable
    private RenderTarget weatherTarget;
    @Nullable
    private RenderTarget cloudsTarget;
    @Nullable
    private PostChain transparencyChain;
    private double lastCameraX = Double.MIN_VALUE;
    private double lastCameraY = Double.MIN_VALUE;
    private double lastCameraZ = Double.MIN_VALUE;
    private int lastCameraChunkX = Integer.MIN_VALUE;
    private int lastCameraChunkY = Integer.MIN_VALUE;
    private int lastCameraChunkZ = Integer.MIN_VALUE;
    private double prevCamX = Double.MIN_VALUE;
    private double prevCamY = Double.MIN_VALUE;
    private double prevCamZ = Double.MIN_VALUE;
    private double prevCamRotX = Double.MIN_VALUE;
    private double prevCamRotY = Double.MIN_VALUE;
    private int prevCloudX = Integer.MIN_VALUE;
    private int prevCloudY = Integer.MIN_VALUE;
    private int prevCloudZ = Integer.MIN_VALUE;
    private Vec3 prevCloudColor = Vec3.ZERO;
    private CloudStatus prevCloudsType;
    private ChunkRenderDispatcher chunkRenderDispatcher;
    private final VertexFormat format = DefaultVertexFormat.BLOCK;
    private int lastViewDistance = -1;
    private int renderedEntities;
    private int culledEntities;
    private boolean captureFrustum;
    @Nullable
    private Frustum capturedFrustum;
    private final Vector4f[] frustumPoints = new Vector4f[8];
    private final Vector3d frustumPos = new Vector3d(0.0, 0.0, 0.0);
    private double xTransparentOld;
    private double yTransparentOld;
    private double zTransparentOld;
    private boolean needsUpdate = true;
    private int frameId;
    private int rainSoundTime;
    private final float[] rainSizeX = new float[1024];
    private final float[] rainSizeZ = new float[1024];

    public LevelRenderer(Minecraft minecraft, RenderBuffers renderBuffers) {
        this.minecraft = minecraft;
        this.entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
        this.renderBuffers = renderBuffers;
        this.textureManager = minecraft.getTextureManager();
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 32; ++j) {
                float f = j - 16;
                float f2 = i - 16;
                float f3 = Mth.sqrt(f * f + f2 * f2);
                this.rainSizeX[i << 5 | j] = -f2 / f3;
                this.rainSizeZ[i << 5 | j] = f / f3;
            }
        }
        this.createStars();
        this.createLightSky();
        this.createDarkSky();
    }

    private void renderSnowAndRain(LightTexture lightTexture, float f, double d, double d2, double d3) {
        float f2 = this.minecraft.level.getRainLevel(f);
        if (f2 <= 0.0f) {
            return;
        }
        lightTexture.turnOnLightLayer();
        ClientLevel clientLevel = this.minecraft.level;
        int n = Mth.floor(d);
        int n2 = Mth.floor(d2);
        int n3 = Mth.floor(d3);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.enableAlphaTest();
        RenderSystem.disableCull();
        RenderSystem.normal3f(0.0f, 1.0f, 0.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableDepthTest();
        int n4 = 5;
        if (Minecraft.useFancyGraphics()) {
            n4 = 10;
        }
        RenderSystem.depthMask(Minecraft.useShaderTransparency());
        int n5 = -1;
        float f3 = (float)this.ticks + f;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = n3 - n4; i <= n3 + n4; ++i) {
            for (int j = n - n4; j <= n + n4; ++j) {
                float f4;
                int n6;
                float f5;
                int n7 = (i - n3 + 16) * 32 + j - n + 16;
                double d4 = (double)this.rainSizeX[n7] * 0.5;
                double d5 = (double)this.rainSizeZ[n7] * 0.5;
                mutableBlockPos.set(j, 0, i);
                Biome biome = clientLevel.getBiome(mutableBlockPos);
                if (biome.getPrecipitation() == Biome.Precipitation.NONE) continue;
                int n8 = clientLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, mutableBlockPos).getY();
                int n9 = n2 - n4;
                int n10 = n2 + n4;
                if (n9 < n8) {
                    n9 = n8;
                }
                if (n10 < n8) {
                    n10 = n8;
                }
                if ((n6 = n8) < n2) {
                    n6 = n2;
                }
                if (n9 == n10) continue;
                Random random = new Random(j * j * 3121 + j * 45238971 ^ i * i * 418711 + i * 13761);
                mutableBlockPos.set(j, n9, i);
                float f6 = biome.getTemperature(mutableBlockPos);
                if (f6 >= 0.15f) {
                    if (n5 != 0) {
                        if (n5 >= 0) {
                            tesselator.end();
                        }
                        n5 = 0;
                        this.minecraft.getTextureManager().bind(RAIN_LOCATION);
                        bufferBuilder.begin(7, DefaultVertexFormat.PARTICLE);
                    }
                    int n11 = this.ticks + j * j * 3121 + j * 45238971 + i * i * 418711 + i * 13761 & 0x1F;
                    f5 = -((float)n11 + f) / 32.0f * (3.0f + random.nextFloat());
                    double d6 = (double)((float)j + 0.5f) - d;
                    double d7 = (double)((float)i + 0.5f) - d3;
                    float f7 = Mth.sqrt(d6 * d6 + d7 * d7) / (float)n4;
                    f4 = ((1.0f - f7 * f7) * 0.5f + 0.5f) * f2;
                    mutableBlockPos.set(j, n6, i);
                    int n12 = LevelRenderer.getLightColor(clientLevel, mutableBlockPos);
                    bufferBuilder.vertex((double)j - d - d4 + 0.5, (double)n10 - d2, (double)i - d3 - d5 + 0.5).uv(0.0f, (float)n9 * 0.25f + f5).color(1.0f, 1.0f, 1.0f, f4).uv2(n12).endVertex();
                    bufferBuilder.vertex((double)j - d + d4 + 0.5, (double)n10 - d2, (double)i - d3 + d5 + 0.5).uv(1.0f, (float)n9 * 0.25f + f5).color(1.0f, 1.0f, 1.0f, f4).uv2(n12).endVertex();
                    bufferBuilder.vertex((double)j - d + d4 + 0.5, (double)n9 - d2, (double)i - d3 + d5 + 0.5).uv(1.0f, (float)n10 * 0.25f + f5).color(1.0f, 1.0f, 1.0f, f4).uv2(n12).endVertex();
                    bufferBuilder.vertex((double)j - d - d4 + 0.5, (double)n9 - d2, (double)i - d3 - d5 + 0.5).uv(0.0f, (float)n10 * 0.25f + f5).color(1.0f, 1.0f, 1.0f, f4).uv2(n12).endVertex();
                    continue;
                }
                if (n5 != 1) {
                    if (n5 >= 0) {
                        tesselator.end();
                    }
                    n5 = 1;
                    this.minecraft.getTextureManager().bind(SNOW_LOCATION);
                    bufferBuilder.begin(7, DefaultVertexFormat.PARTICLE);
                }
                float f8 = -((float)(this.ticks & 0x1FF) + f) / 512.0f;
                f5 = (float)(random.nextDouble() + (double)f3 * 0.01 * (double)((float)random.nextGaussian()));
                float f9 = (float)(random.nextDouble() + (double)(f3 * (float)random.nextGaussian()) * 0.001);
                double d8 = (double)((float)j + 0.5f) - d;
                double d9 = (double)((float)i + 0.5f) - d3;
                f4 = Mth.sqrt(d8 * d8 + d9 * d9) / (float)n4;
                float f10 = ((1.0f - f4 * f4) * 0.3f + 0.5f) * f2;
                mutableBlockPos.set(j, n6, i);
                int n13 = LevelRenderer.getLightColor(clientLevel, mutableBlockPos);
                int n14 = n13 >> 16 & 0xFFFF;
                int n15 = (n13 & 0xFFFF) * 3;
                int n16 = (n14 * 3 + 240) / 4;
                int n17 = (n15 * 3 + 240) / 4;
                bufferBuilder.vertex((double)j - d - d4 + 0.5, (double)n10 - d2, (double)i - d3 - d5 + 0.5).uv(0.0f + f5, (float)n9 * 0.25f + f8 + f9).color(1.0f, 1.0f, 1.0f, f10).uv2(n17, n16).endVertex();
                bufferBuilder.vertex((double)j - d + d4 + 0.5, (double)n10 - d2, (double)i - d3 + d5 + 0.5).uv(1.0f + f5, (float)n9 * 0.25f + f8 + f9).color(1.0f, 1.0f, 1.0f, f10).uv2(n17, n16).endVertex();
                bufferBuilder.vertex((double)j - d + d4 + 0.5, (double)n9 - d2, (double)i - d3 + d5 + 0.5).uv(1.0f + f5, (float)n10 * 0.25f + f8 + f9).color(1.0f, 1.0f, 1.0f, f10).uv2(n17, n16).endVertex();
                bufferBuilder.vertex((double)j - d - d4 + 0.5, (double)n9 - d2, (double)i - d3 - d5 + 0.5).uv(0.0f + f5, (float)n10 * 0.25f + f8 + f9).color(1.0f, 1.0f, 1.0f, f10).uv2(n17, n16).endVertex();
            }
        }
        if (n5 >= 0) {
            tesselator.end();
        }
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.disableAlphaTest();
        lightTexture.turnOffLightLayer();
    }

    public void tickRain(Camera camera) {
        float f = this.minecraft.level.getRainLevel(1.0f) / (Minecraft.useFancyGraphics() ? 1.0f : 2.0f);
        if (f <= 0.0f) {
            return;
        }
        Random random = new Random((long)this.ticks * 312987231L);
        ClientLevel clientLevel = this.minecraft.level;
        BlockPos blockPos = new BlockPos(camera.getPosition());
        Vec3i vec3i = null;
        int n = (int)(100.0f * f * f) / (this.minecraft.options.particles == ParticleStatus.DECREASED ? 2 : 1);
        for (int i = 0; i < n; ++i) {
            int n2 = random.nextInt(21) - 10;
            int n3 = random.nextInt(21) - 10;
            BlockPos blockPos2 = clientLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos.offset(n2, 0, n3)).below();
            Biome biome = clientLevel.getBiome(blockPos2);
            if (blockPos2.getY() <= 0 || blockPos2.getY() > blockPos.getY() + 10 || blockPos2.getY() < blockPos.getY() - 10 || biome.getPrecipitation() != Biome.Precipitation.RAIN || !(biome.getTemperature(blockPos2) >= 0.15f)) continue;
            vec3i = blockPos2;
            if (this.minecraft.options.particles == ParticleStatus.MINIMAL) break;
            double d = random.nextDouble();
            double d2 = random.nextDouble();
            BlockState blockState = clientLevel.getBlockState((BlockPos)vec3i);
            FluidState fluidState = clientLevel.getFluidState((BlockPos)vec3i);
            VoxelShape voxelShape = blockState.getCollisionShape(clientLevel, (BlockPos)vec3i);
            double d3 = voxelShape.max(Direction.Axis.Y, d, d2);
            double d4 = fluidState.getHeight(clientLevel, (BlockPos)vec3i);
            double d5 = Math.max(d3, d4);
            SimpleParticleType simpleParticleType = fluidState.is(FluidTags.LAVA) || blockState.is(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(blockState) ? ParticleTypes.SMOKE : ParticleTypes.RAIN;
            this.minecraft.level.addParticle(simpleParticleType, (double)vec3i.getX() + d, (double)vec3i.getY() + d5, (double)vec3i.getZ() + d2, 0.0, 0.0, 0.0);
        }
        if (vec3i != null && random.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            if (vec3i.getY() > blockPos.getY() + 1 && clientLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos).getY() > Mth.floor(blockPos.getY())) {
                this.minecraft.level.playLocalSound((BlockPos)vec3i, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1f, 0.5f, false);
            } else {
                this.minecraft.level.playLocalSound((BlockPos)vec3i, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2f, 1.0f, false);
            }
        }
    }

    @Override
    public void close() {
        if (this.entityEffect != null) {
            this.entityEffect.close();
        }
        if (this.transparencyChain != null) {
            this.transparencyChain.close();
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.textureManager.bind(FORCEFIELD_LOCATION);
        RenderSystem.texParameter(3553, 10242, 10497);
        RenderSystem.texParameter(3553, 10243, 10497);
        RenderSystem.bindTexture(0);
        this.initOutline();
        if (Minecraft.useShaderTransparency()) {
            this.initTransparency();
        }
    }

    public void initOutline() {
        if (this.entityEffect != null) {
            this.entityEffect.close();
        }
        ResourceLocation resourceLocation = new ResourceLocation("shaders/post/entity_outline.json");
        try {
            this.entityEffect = new PostChain(this.minecraft.getTextureManager(), this.minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), resourceLocation);
            this.entityEffect.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
            this.entityTarget = this.entityEffect.getTempTarget("final");
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to load shader: {}", (Object)resourceLocation, (Object)iOException);
            this.entityEffect = null;
            this.entityTarget = null;
        }
        catch (JsonSyntaxException jsonSyntaxException) {
            LOGGER.warn("Failed to parse shader: {}", (Object)resourceLocation, (Object)jsonSyntaxException);
            this.entityEffect = null;
            this.entityTarget = null;
        }
    }

    private void initTransparency() {
        this.deinitTransparency();
        ResourceLocation resourceLocation = new ResourceLocation("shaders/post/transparency.json");
        try {
            PostChain postChain = new PostChain(this.minecraft.getTextureManager(), this.minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), resourceLocation);
            postChain.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
            RenderTarget renderTarget = postChain.getTempTarget("translucent");
            RenderTarget renderTarget2 = postChain.getTempTarget("itemEntity");
            RenderTarget renderTarget3 = postChain.getTempTarget("particles");
            RenderTarget renderTarget4 = postChain.getTempTarget("weather");
            RenderTarget renderTarget5 = postChain.getTempTarget("clouds");
            this.transparencyChain = postChain;
            this.translucentTarget = renderTarget;
            this.itemEntityTarget = renderTarget2;
            this.particlesTarget = renderTarget3;
            this.weatherTarget = renderTarget4;
            this.cloudsTarget = renderTarget5;
        }
        catch (Exception exception) {
            String string = exception instanceof JsonSyntaxException ? "parse" : "load";
            String string2 = "Failed to " + string + " shader: " + resourceLocation;
            TransparencyShaderException transparencyShaderException = new TransparencyShaderException(string2, exception);
            if (this.minecraft.getResourcePackRepository().getSelectedIds().size() > 1) {
                TextComponent textComponent;
                try {
                    textComponent = new TextComponent(this.minecraft.getResourceManager().getResource(resourceLocation).getSourceName());
                }
                catch (IOException iOException) {
                    textComponent = null;
                }
                this.minecraft.options.graphicsMode = GraphicsStatus.FANCY;
                this.minecraft.clearResourcePacksOnError(transparencyShaderException, textComponent);
            }
            CrashReport crashReport = this.minecraft.fillReport(new CrashReport(string2, transparencyShaderException));
            this.minecraft.options.graphicsMode = GraphicsStatus.FANCY;
            this.minecraft.options.save();
            LOGGER.fatal(string2, (Throwable)transparencyShaderException);
            this.minecraft.emergencySave();
            Minecraft.crash(crashReport);
        }
    }

    private void deinitTransparency() {
        if (this.transparencyChain != null) {
            this.transparencyChain.close();
            this.translucentTarget.destroyBuffers();
            this.itemEntityTarget.destroyBuffers();
            this.particlesTarget.destroyBuffers();
            this.weatherTarget.destroyBuffers();
            this.cloudsTarget.destroyBuffers();
            this.transparencyChain = null;
            this.translucentTarget = null;
            this.itemEntityTarget = null;
            this.particlesTarget = null;
            this.weatherTarget = null;
            this.cloudsTarget = null;
        }
    }

    public void doEntityOutline() {
        if (this.shouldShowEntityOutlines()) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            this.entityTarget.blitToScreen(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), false);
            RenderSystem.disableBlend();
        }
    }

    protected boolean shouldShowEntityOutlines() {
        return this.entityTarget != null && this.entityEffect != null && this.minecraft.player != null;
    }

    private void createDarkSky() {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        if (this.darkBuffer != null) {
            this.darkBuffer.close();
        }
        this.darkBuffer = new VertexBuffer(this.skyFormat);
        this.drawSkyHemisphere(bufferBuilder, -16.0f, true);
        bufferBuilder.end();
        this.darkBuffer.upload(bufferBuilder);
    }

    private void createLightSky() {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        if (this.skyBuffer != null) {
            this.skyBuffer.close();
        }
        this.skyBuffer = new VertexBuffer(this.skyFormat);
        this.drawSkyHemisphere(bufferBuilder, 16.0f, false);
        bufferBuilder.end();
        this.skyBuffer.upload(bufferBuilder);
    }

    private void drawSkyHemisphere(BufferBuilder bufferBuilder, float f, boolean bl) {
        int n = 64;
        int n2 = 6;
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION);
        for (int i = -384; i <= 384; i += 64) {
            for (int j = -384; j <= 384; j += 64) {
                float f2 = i;
                float f3 = i + 64;
                if (bl) {
                    f3 = i;
                    f2 = i + 64;
                }
                bufferBuilder.vertex(f2, f, j).endVertex();
                bufferBuilder.vertex(f3, f, j).endVertex();
                bufferBuilder.vertex(f3, f, j + 64).endVertex();
                bufferBuilder.vertex(f2, f, j + 64).endVertex();
            }
        }
    }

    private void createStars() {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        if (this.starBuffer != null) {
            this.starBuffer.close();
        }
        this.starBuffer = new VertexBuffer(this.skyFormat);
        this.drawStars(bufferBuilder);
        bufferBuilder.end();
        this.starBuffer.upload(bufferBuilder);
    }

    private void drawStars(BufferBuilder bufferBuilder) {
        Random random = new Random(10842L);
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION);
        for (int i = 0; i < 1500; ++i) {
            double d = random.nextFloat() * 2.0f - 1.0f;
            double d2 = random.nextFloat() * 2.0f - 1.0f;
            double d3 = random.nextFloat() * 2.0f - 1.0f;
            double d4 = 0.15f + random.nextFloat() * 0.1f;
            double d5 = d * d + d2 * d2 + d3 * d3;
            if (!(d5 < 1.0) || !(d5 > 0.01)) continue;
            d5 = 1.0 / Math.sqrt(d5);
            double d6 = (d *= d5) * 100.0;
            double d7 = (d2 *= d5) * 100.0;
            double d8 = (d3 *= d5) * 100.0;
            double d9 = Math.atan2(d, d3);
            double d10 = Math.sin(d9);
            double d11 = Math.cos(d9);
            double d12 = Math.atan2(Math.sqrt(d * d + d3 * d3), d2);
            double d13 = Math.sin(d12);
            double d14 = Math.cos(d12);
            double d15 = random.nextDouble() * 3.141592653589793 * 2.0;
            double d16 = Math.sin(d15);
            double d17 = Math.cos(d15);
            for (int j = 0; j < 4; ++j) {
                double d18;
                double d19 = 0.0;
                double d20 = (double)((j & 2) - 1) * d4;
                double d21 = (double)((j + 1 & 2) - 1) * d4;
                double d22 = 0.0;
                double d23 = d20 * d17 - d21 * d16;
                double d24 = d18 = d21 * d17 + d20 * d16;
                double d25 = d23 * d13 + 0.0 * d14;
                double d26 = 0.0 * d13 - d23 * d14;
                double d27 = d26 * d10 - d24 * d11;
                double d28 = d25;
                double d29 = d24 * d10 + d26 * d11;
                bufferBuilder.vertex(d6 + d27, d7 + d28, d8 + d29).endVertex();
            }
        }
    }

    public void setLevel(@Nullable ClientLevel clientLevel) {
        this.lastCameraX = Double.MIN_VALUE;
        this.lastCameraY = Double.MIN_VALUE;
        this.lastCameraZ = Double.MIN_VALUE;
        this.lastCameraChunkX = Integer.MIN_VALUE;
        this.lastCameraChunkY = Integer.MIN_VALUE;
        this.lastCameraChunkZ = Integer.MIN_VALUE;
        this.entityRenderDispatcher.setLevel(clientLevel);
        this.level = clientLevel;
        if (clientLevel != null) {
            this.allChanged();
        } else {
            this.chunksToCompile.clear();
            this.renderChunks.clear();
            if (this.viewArea != null) {
                this.viewArea.releaseAllBuffers();
                this.viewArea = null;
            }
            if (this.chunkRenderDispatcher != null) {
                this.chunkRenderDispatcher.dispose();
            }
            this.chunkRenderDispatcher = null;
            this.globalBlockEntities.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void allChanged() {
        if (this.level == null) {
            return;
        }
        if (Minecraft.useShaderTransparency()) {
            this.initTransparency();
        } else {
            this.deinitTransparency();
        }
        this.level.clearTintCaches();
        if (this.chunkRenderDispatcher == null) {
            this.chunkRenderDispatcher = new ChunkRenderDispatcher(this.level, this, Util.backgroundExecutor(), this.minecraft.is64Bit(), this.renderBuffers.fixedBufferPack());
        } else {
            this.chunkRenderDispatcher.setLevel(this.level);
        }
        this.needsUpdate = true;
        this.generateClouds = true;
        ItemBlockRenderTypes.setFancy(Minecraft.useFancyGraphics());
        this.lastViewDistance = this.minecraft.options.renderDistance;
        if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
        }
        this.resetChunksToCompile();
        Object object = this.globalBlockEntities;
        synchronized (object) {
            this.globalBlockEntities.clear();
        }
        this.viewArea = new ViewArea(this.chunkRenderDispatcher, this.level, this.minecraft.options.renderDistance, this);
        if (this.level != null && (object = this.minecraft.getCameraEntity()) != null) {
            this.viewArea.repositionCamera(((Entity)object).getX(), ((Entity)object).getZ());
        }
    }

    protected void resetChunksToCompile() {
        this.chunksToCompile.clear();
        this.chunkRenderDispatcher.blockUntilClear();
    }

    public void resize(int n, int n2) {
        this.needsUpdate();
        if (this.entityEffect != null) {
            this.entityEffect.resize(n, n2);
        }
        if (this.transparencyChain != null) {
            this.transparencyChain.resize(n, n2);
        }
    }

    public String getChunkStatistics() {
        int n = this.viewArea.chunks.length;
        int n2 = this.countRenderedChunks();
        return String.format("C: %d/%d %sD: %d, %s", n2, n, this.minecraft.smartCull ? "(s) " : "", this.lastViewDistance, this.chunkRenderDispatcher == null ? "null" : this.chunkRenderDispatcher.getStats());
    }

    protected int countRenderedChunks() {
        int n = 0;
        for (RenderChunkInfo renderChunkInfo : this.renderChunks) {
            if (renderChunkInfo.chunk.getCompiledChunk().hasNoRenderableLayers()) continue;
            ++n;
        }
        return n;
    }

    public String getEntityStatistics() {
        return "E: " + this.renderedEntities + "/" + this.level.getEntityCount() + ", B: " + this.culledEntities;
    }

    private void setupRender(Camera camera, Frustum frustum, boolean bl, int n, boolean bl2) {
        Collection<ChunkRenderDispatcher.RenderChunk> collection;
        Vec3 vec3 = camera.getPosition();
        if (this.minecraft.options.renderDistance != this.lastViewDistance) {
            this.allChanged();
        }
        this.level.getProfiler().push("camera");
        double d = this.minecraft.player.getX() - this.lastCameraX;
        double d2 = this.minecraft.player.getY() - this.lastCameraY;
        double d3 = this.minecraft.player.getZ() - this.lastCameraZ;
        if (this.lastCameraChunkX != this.minecraft.player.xChunk || this.lastCameraChunkY != this.minecraft.player.yChunk || this.lastCameraChunkZ != this.minecraft.player.zChunk || d * d + d2 * d2 + d3 * d3 > 16.0) {
            this.lastCameraX = this.minecraft.player.getX();
            this.lastCameraY = this.minecraft.player.getY();
            this.lastCameraZ = this.minecraft.player.getZ();
            this.lastCameraChunkX = this.minecraft.player.xChunk;
            this.lastCameraChunkY = this.minecraft.player.yChunk;
            this.lastCameraChunkZ = this.minecraft.player.zChunk;
            this.viewArea.repositionCamera(this.minecraft.player.getX(), this.minecraft.player.getZ());
        }
        this.chunkRenderDispatcher.setCamera(vec3);
        this.level.getProfiler().popPush("cull");
        this.minecraft.getProfiler().popPush("culling");
        BlockPos blockPos = camera.getBlockPosition();
        ChunkRenderDispatcher.RenderChunk renderChunk = this.viewArea.getRenderChunkAt(blockPos);
        int n2 = 16;
        BlockPos blockPos2 = new BlockPos(Mth.floor(vec3.x / 16.0) * 16, Mth.floor(vec3.y / 16.0) * 16, Mth.floor(vec3.z / 16.0) * 16);
        float f = camera.getXRot();
        float f2 = camera.getYRot();
        this.needsUpdate = this.needsUpdate || !this.chunksToCompile.isEmpty() || vec3.x != this.prevCamX || vec3.y != this.prevCamY || vec3.z != this.prevCamZ || (double)f != this.prevCamRotX || (double)f2 != this.prevCamRotY;
        this.prevCamX = vec3.x;
        this.prevCamY = vec3.y;
        this.prevCamZ = vec3.z;
        this.prevCamRotX = f;
        this.prevCamRotY = f2;
        this.minecraft.getProfiler().popPush("update");
        if (!bl && this.needsUpdate) {
            this.needsUpdate = false;
            this.renderChunks.clear();
            collection = Queues.newArrayDeque();
            Entity.setViewScale(Mth.clamp((double)this.minecraft.options.renderDistance / 8.0, 1.0, 2.5) * (double)this.minecraft.options.entityDistanceScaling);
            boolean bl3 = this.minecraft.smartCull;
            if (renderChunk == null) {
                int n3 = blockPos.getY() > 0 ? 248 : 8;
                int n4 = Mth.floor(vec3.x / 16.0) * 16;
                int n5 = Mth.floor(vec3.z / 16.0) * 16;
                Direction[] arrdirection = Lists.newArrayList();
                for (int i = -this.lastViewDistance; i <= this.lastViewDistance; ++i) {
                    for (int j = -this.lastViewDistance; j <= this.lastViewDistance; ++j) {
                        ChunkRenderDispatcher.RenderChunk renderChunk2 = this.viewArea.getRenderChunkAt(new BlockPos(n4 + (i << 4) + 8, n3, n5 + (j << 4) + 8));
                        if (renderChunk2 == null || !frustum.isVisible(renderChunk2.bb)) continue;
                        renderChunk2.setFrame(n);
                        arrdirection.add(new RenderChunkInfo(renderChunk2, null, 0));
                    }
                }
                arrdirection.sort(Comparator.comparingDouble(renderChunkInfo -> blockPos.distSqr(renderChunkInfo.chunk.getOrigin().offset(8, 8, 8))));
                collection.addAll((Collection<ChunkRenderDispatcher.RenderChunk>)arrdirection);
            } else {
                if (bl2 && this.level.getBlockState(blockPos).isSolidRender(this.level, blockPos)) {
                    bl3 = false;
                }
                renderChunk.setFrame(n);
                collection.add((ChunkRenderDispatcher.RenderChunk)((Object)new RenderChunkInfo(renderChunk, null, 0)));
            }
            this.minecraft.getProfiler().push("iteration");
            while (!collection.isEmpty()) {
                RenderChunkInfo renderChunkInfo2 = (RenderChunkInfo)collection.poll();
                ChunkRenderDispatcher.RenderChunk renderChunk3 = renderChunkInfo2.chunk;
                Direction direction = renderChunkInfo2.sourceDirection;
                this.renderChunks.add((Object)renderChunkInfo2);
                for (Direction direction2 : DIRECTIONS) {
                    ChunkRenderDispatcher.RenderChunk renderChunk4 = this.getRelativeFrom(blockPos2, renderChunk3, direction2);
                    if (bl3 && renderChunkInfo2.hasDirection(direction2.getOpposite()) || bl3 && direction != null && !renderChunk3.getCompiledChunk().facesCanSeeEachother(direction.getOpposite(), direction2) || renderChunk4 == null || !renderChunk4.hasAllNeighbors() || !renderChunk4.setFrame(n) || !frustum.isVisible(renderChunk4.bb)) continue;
                    RenderChunkInfo renderChunkInfo3 = new RenderChunkInfo(renderChunk4, direction2, renderChunkInfo2.step + 1);
                    renderChunkInfo3.setDirections(renderChunkInfo2.directions, direction2);
                    collection.add((ChunkRenderDispatcher.RenderChunk)((Object)renderChunkInfo3));
                }
            }
            this.minecraft.getProfiler().pop();
        }
        this.minecraft.getProfiler().popPush("rebuildNear");
        collection = this.chunksToCompile;
        this.chunksToCompile = Sets.newLinkedHashSet();
        for (RenderChunkInfo renderChunkInfo4 : this.renderChunks) {
            boolean bl4;
            ChunkRenderDispatcher.RenderChunk renderChunk5 = renderChunkInfo4.chunk;
            if (!renderChunk5.isDirty() && !collection.contains(renderChunk5)) continue;
            this.needsUpdate = true;
            BlockPos blockPos3 = renderChunk5.getOrigin().offset(8, 8, 8);
            boolean bl5 = bl4 = blockPos3.distSqr(blockPos) < 768.0;
            if (renderChunk5.isDirtyFromPlayer() || bl4) {
                this.minecraft.getProfiler().push("build near");
                this.chunkRenderDispatcher.rebuildChunkSync(renderChunk5);
                renderChunk5.setNotDirty();
                this.minecraft.getProfiler().pop();
                continue;
            }
            this.chunksToCompile.add(renderChunk5);
        }
        this.chunksToCompile.addAll(collection);
        this.minecraft.getProfiler().pop();
    }

    @Nullable
    private ChunkRenderDispatcher.RenderChunk getRelativeFrom(BlockPos blockPos, ChunkRenderDispatcher.RenderChunk renderChunk, Direction direction) {
        BlockPos blockPos2 = renderChunk.getRelativeOrigin(direction);
        if (Mth.abs(blockPos.getX() - blockPos2.getX()) > this.lastViewDistance * 16) {
            return null;
        }
        if (blockPos2.getY() < 0 || blockPos2.getY() >= 256) {
            return null;
        }
        if (Mth.abs(blockPos.getZ() - blockPos2.getZ()) > this.lastViewDistance * 16) {
            return null;
        }
        return this.viewArea.getRenderChunkAt(blockPos2);
    }

    private void captureFrustum(Matrix4f matrix4f, Matrix4f matrix4f2, double d, double d2, double d3, Frustum frustum) {
        this.capturedFrustum = frustum;
        Matrix4f matrix4f3 = matrix4f2.copy();
        matrix4f3.multiply(matrix4f);
        matrix4f3.invert();
        this.frustumPos.x = d;
        this.frustumPos.y = d2;
        this.frustumPos.z = d3;
        this.frustumPoints[0] = new Vector4f(-1.0f, -1.0f, -1.0f, 1.0f);
        this.frustumPoints[1] = new Vector4f(1.0f, -1.0f, -1.0f, 1.0f);
        this.frustumPoints[2] = new Vector4f(1.0f, 1.0f, -1.0f, 1.0f);
        this.frustumPoints[3] = new Vector4f(-1.0f, 1.0f, -1.0f, 1.0f);
        this.frustumPoints[4] = new Vector4f(-1.0f, -1.0f, 1.0f, 1.0f);
        this.frustumPoints[5] = new Vector4f(1.0f, -1.0f, 1.0f, 1.0f);
        this.frustumPoints[6] = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.frustumPoints[7] = new Vector4f(-1.0f, 1.0f, 1.0f, 1.0f);
        for (int i = 0; i < 8; ++i) {
            this.frustumPoints[i].transform(matrix4f3);
            this.frustumPoints[i].perspectiveDivide();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void renderLevel(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f) {
        Frustum frustum;
        Object object6;
        boolean bl2;
        Object object2;
        int n;
        boolean bl3;
        Object object3;
        int n2;
        Object object4;
        int n3;
        BlockEntityRenderDispatcher.instance.prepare(this.level, this.minecraft.getTextureManager(), this.minecraft.font, camera, this.minecraft.hitResult);
        this.entityRenderDispatcher.prepare(this.level, camera, this.minecraft.crosshairPickEntity);
        ProfilerFiller profilerFiller = this.level.getProfiler();
        profilerFiller.popPush("light_updates");
        this.minecraft.level.getChunkSource().getLightEngine().runUpdates(Integer.MAX_VALUE, true, true);
        Vec3 vec3 = camera.getPosition();
        double d = vec3.x();
        double d2 = vec3.y();
        double d3 = vec3.z();
        Matrix4f matrix4f2 = poseStack.last().pose();
        profilerFiller.popPush("culling");
        boolean bl4 = bl2 = this.capturedFrustum != null;
        if (bl2) {
            frustum = this.capturedFrustum;
            frustum.prepare(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z);
        } else {
            frustum = new Frustum(matrix4f2, matrix4f);
            frustum.prepare(d, d2, d3);
        }
        this.minecraft.getProfiler().popPush("captureFrustum");
        if (this.captureFrustum) {
            this.captureFrustum(matrix4f2, matrix4f, vec3.x, vec3.y, vec3.z, bl2 ? new Frustum(matrix4f2, matrix4f) : frustum);
            this.captureFrustum = false;
        }
        profilerFiller.popPush("clear");
        FogRenderer.setupColor(camera, f, this.minecraft.level, this.minecraft.options.renderDistance, gameRenderer.getDarkenWorldAmount(f));
        RenderSystem.clear(16640, Minecraft.ON_OSX);
        float f2 = gameRenderer.getRenderDistance();
        boolean bl5 = bl3 = this.minecraft.level.effects().isFoggyAt(Mth.floor(d), Mth.floor(d2)) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
        if (this.minecraft.options.renderDistance >= 4) {
            FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_SKY, f2, bl3);
            profilerFiller.popPush("sky");
            this.renderSky(poseStack, f);
        }
        profilerFiller.popPush("fog");
        FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_TERRAIN, Math.max(f2 - 16.0f, 32.0f), bl3);
        profilerFiller.popPush("terrain_setup");
        this.setupRender(camera, frustum, bl2, this.frameId++, this.minecraft.player.isSpectator());
        profilerFiller.popPush("updatechunks");
        int n4 = 30;
        int n5 = this.minecraft.options.framerateLimit;
        long l2 = 33333333L;
        long l3 = (double)n5 == Option.FRAMERATE_LIMIT.getMaxValue() ? 0L : (long)(1000000000 / n5);
        long l4 = Util.getNanos() - l;
        long l5 = this.frameTimes.registerValueAndGetMean(l4);
        long l6 = l5 * 3L / 2L;
        long l7 = Mth.clamp(l6, l3, 33333333L);
        this.compileChunksUntil(l + l7);
        profilerFiller.popPush("terrain");
        this.renderChunkLayer(RenderType.solid(), poseStack, d, d2, d3);
        this.renderChunkLayer(RenderType.cutoutMipped(), poseStack, d, d2, d3);
        this.renderChunkLayer(RenderType.cutout(), poseStack, d, d2, d3);
        if (this.level.effects().constantAmbientLight()) {
            Lighting.setupNetherLevel(poseStack.last().pose());
        } else {
            Lighting.setupLevel(poseStack.last().pose());
        }
        profilerFiller.popPush("entities");
        this.renderedEntities = 0;
        this.culledEntities = 0;
        if (this.itemEntityTarget != null) {
            this.itemEntityTarget.clear(Minecraft.ON_OSX);
            this.itemEntityTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }
        if (this.weatherTarget != null) {
            this.weatherTarget.clear(Minecraft.ON_OSX);
        }
        if (this.shouldShowEntityOutlines()) {
            this.entityTarget.clear(Minecraft.ON_OSX);
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }
        boolean bl6 = false;
        MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
        for (Entity entity : this.level.entitiesForRendering()) {
            if (!this.entityRenderDispatcher.shouldRender(entity, frustum, d, d2, d3) && !entity.hasIndirectPassenger(this.minecraft.player) || entity == camera.getEntity() && !camera.isDetached() && (!(camera.getEntity() instanceof LivingEntity) || !((LivingEntity)camera.getEntity()).isSleeping()) || entity instanceof LocalPlayer && camera.getEntity() != entity) continue;
            ++this.renderedEntities;
            if (entity.tickCount == 0) {
                entity.xOld = entity.getX();
                entity.yOld = entity.getY();
                entity.zOld = entity.getZ();
            }
            if (this.shouldShowEntityOutlines() && this.minecraft.shouldEntityAppearGlowing(entity)) {
                bl6 = true;
                object6 = object2 = this.renderBuffers.outlineBufferSource();
                int n6 = entity.getTeamColor();
                n2 = 255;
                int n7 = n6 >> 16 & 0xFF;
                n3 = n6 >> 8 & 0xFF;
                n = n6 & 0xFF;
                ((OutlineBufferSource)object2).setColor(n7, n3, n, 255);
            } else {
                object6 = bufferSource;
            }
            this.renderEntity(entity, d, d2, d3, f, poseStack, (MultiBufferSource)object6);
        }
        this.checkPoseStack(poseStack);
        bufferSource.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
        bufferSource.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
        bufferSource.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
        bufferSource.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));
        profilerFiller.popPush("blockentities");
        for (RenderChunkInfo renderChunkInfo : this.renderChunks) {
            object6 = renderChunkInfo.chunk.getCompiledChunk().getRenderableBlockEntities();
            if (object6.isEmpty()) continue;
            object2 = object6.iterator();
            while (object2.hasNext()) {
                BlockEntity blockEntity = (BlockEntity)object2.next();
                BlockPos blockPos = blockEntity.getBlockPos();
                MultiBufferSource multiBufferSource = bufferSource;
                poseStack.pushPose();
                poseStack.translate((double)blockPos.getX() - d, (double)blockPos.getY() - d2, (double)blockPos.getZ() - d3);
                SortedSet sortedSet = (SortedSet)this.destructionProgress.get(blockPos.asLong());
                if (sortedSet != null && !sortedSet.isEmpty() && (n = ((BlockDestructionProgress)sortedSet.last()).getProgress()) >= 0) {
                    object4 = poseStack.last();
                    object3 = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(n)), ((PoseStack.Pose)object4).pose(), ((PoseStack.Pose)object4).normal());
                    multiBufferSource = arg_0 -> LevelRenderer.lambda$renderLevel$1(bufferSource, (VertexConsumer)object3, arg_0);
                }
                BlockEntityRenderDispatcher.instance.render(blockEntity, f, poseStack, multiBufferSource);
                poseStack.popPose();
            }
        }
        Object object5 = this.globalBlockEntities;
        synchronized (object5) {
            for (Object object6 : this.globalBlockEntities) {
                object2 = ((BlockEntity)object6).getBlockPos();
                poseStack.pushPose();
                poseStack.translate((double)((Vec3i)object2).getX() - d, (double)((Vec3i)object2).getY() - d2, (double)((Vec3i)object2).getZ() - d3);
                BlockEntityRenderDispatcher.instance.render(object6, f, poseStack, bufferSource);
                poseStack.popPose();
            }
        }
        this.checkPoseStack(poseStack);
        bufferSource.endBatch(RenderType.solid());
        bufferSource.endBatch(Sheets.solidBlockSheet());
        bufferSource.endBatch(Sheets.cutoutBlockSheet());
        bufferSource.endBatch(Sheets.bedSheet());
        bufferSource.endBatch(Sheets.shulkerBoxSheet());
        bufferSource.endBatch(Sheets.signSheet());
        bufferSource.endBatch(Sheets.chestSheet());
        this.renderBuffers.outlineBufferSource().endOutlineBatch();
        if (bl6) {
            this.entityEffect.process(f);
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }
        profilerFiller.popPush("destroyProgress");
        for (Long2ObjectMap.Entry entry : this.destructionProgress.long2ObjectEntrySet()) {
            object6 = BlockPos.of(entry.getLongKey());
            object2 = (double)((Vec3i)object6).getX() - d;
            if (object2 * object2 + (n2 = (int)((double)((Vec3i)object6).getY() - d2)) * n2 + (n3 = (int)((double)((Vec3i)object6).getZ() - d3)) * n3 > 1024.0 || (object4 = (SortedSet)entry.getValue()) == null || object4.isEmpty()) continue;
            object3 = ((BlockDestructionProgress)object4.last()).getProgress();
            poseStack.pushPose();
            poseStack.translate((double)((Vec3i)object6).getX() - d, (double)((Vec3i)object6).getY() - d2, (double)((Vec3i)object6).getZ() - d3);
            PoseStack.Pose pose = poseStack.last();
            SheetedDecalTextureGenerator sheetedDecalTextureGenerator = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get((int)object3)), pose.pose(), pose.normal());
            this.minecraft.getBlockRenderer().renderBreakingTexture(this.level.getBlockState((BlockPos)object6), (BlockPos)object6, this.level, poseStack, sheetedDecalTextureGenerator);
            poseStack.popPose();
        }
        this.checkPoseStack(poseStack);
        object5 = this.minecraft.hitResult;
        if (bl && object5 != null && ((HitResult)object5).getType() == HitResult.Type.BLOCK) {
            profilerFiller.popPush("outline");
            BlockPos blockPos = ((BlockHitResult)object5).getBlockPos();
            object6 = this.level.getBlockState(blockPos);
            if (!((BlockBehaviour.BlockStateBase)object6).isAir() && this.level.getWorldBorder().isWithinBounds(blockPos)) {
                VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
                this.renderHitOutline(poseStack, vertexConsumer, camera.getEntity(), d, d2, d3, blockPos, (BlockState)object6);
            }
        }
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(poseStack.last().pose());
        this.minecraft.debugRenderer.render(poseStack, bufferSource, d, d2, d3);
        RenderSystem.popMatrix();
        bufferSource.endBatch(Sheets.translucentCullBlockSheet());
        bufferSource.endBatch(Sheets.bannerSheet());
        bufferSource.endBatch(Sheets.shieldSheet());
        bufferSource.endBatch(RenderType.armorGlint());
        bufferSource.endBatch(RenderType.armorEntityGlint());
        bufferSource.endBatch(RenderType.glint());
        bufferSource.endBatch(RenderType.glintDirect());
        bufferSource.endBatch(RenderType.glintTranslucent());
        bufferSource.endBatch(RenderType.entityGlint());
        bufferSource.endBatch(RenderType.entityGlintDirect());
        bufferSource.endBatch(RenderType.waterMask());
        this.renderBuffers.crumblingBufferSource().endBatch();
        if (this.transparencyChain != null) {
            bufferSource.endBatch(RenderType.lines());
            bufferSource.endBatch();
            this.translucentTarget.clear(Minecraft.ON_OSX);
            this.translucentTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
            profilerFiller.popPush("translucent");
            this.renderChunkLayer(RenderType.translucent(), poseStack, d, d2, d3);
            profilerFiller.popPush("string");
            this.renderChunkLayer(RenderType.tripwire(), poseStack, d, d2, d3);
            this.particlesTarget.clear(Minecraft.ON_OSX);
            this.particlesTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
            RenderStateShard.PARTICLES_TARGET.setupRenderState();
            profilerFiller.popPush("particles");
            this.minecraft.particleEngine.render(poseStack, bufferSource, lightTexture, camera, f);
            RenderStateShard.PARTICLES_TARGET.clearRenderState();
        } else {
            profilerFiller.popPush("translucent");
            this.renderChunkLayer(RenderType.translucent(), poseStack, d, d2, d3);
            bufferSource.endBatch(RenderType.lines());
            bufferSource.endBatch();
            profilerFiller.popPush("string");
            this.renderChunkLayer(RenderType.tripwire(), poseStack, d, d2, d3);
            profilerFiller.popPush("particles");
            this.minecraft.particleEngine.render(poseStack, bufferSource, lightTexture, camera, f);
        }
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(poseStack.last().pose());
        if (this.minecraft.options.getCloudsType() != CloudStatus.OFF) {
            if (this.transparencyChain != null) {
                this.cloudsTarget.clear(Minecraft.ON_OSX);
                RenderStateShard.CLOUDS_TARGET.setupRenderState();
                profilerFiller.popPush("clouds");
                this.renderClouds(poseStack, f, d, d2, d3);
                RenderStateShard.CLOUDS_TARGET.clearRenderState();
            } else {
                profilerFiller.popPush("clouds");
                this.renderClouds(poseStack, f, d, d2, d3);
            }
        }
        if (this.transparencyChain != null) {
            RenderStateShard.WEATHER_TARGET.setupRenderState();
            profilerFiller.popPush("weather");
            this.renderSnowAndRain(lightTexture, f, d, d2, d3);
            this.renderWorldBounds(camera);
            RenderStateShard.WEATHER_TARGET.clearRenderState();
            this.transparencyChain.process(f);
            this.minecraft.getMainRenderTarget().bindWrite(false);
        } else {
            RenderSystem.depthMask(false);
            profilerFiller.popPush("weather");
            this.renderSnowAndRain(lightTexture, f, d, d2, d3);
            this.renderWorldBounds(camera);
            RenderSystem.depthMask(true);
        }
        this.renderDebug(camera);
        RenderSystem.shadeModel(7424);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        FogRenderer.setupNoFog();
    }

    private void checkPoseStack(PoseStack poseStack) {
        if (!poseStack.clear()) {
            throw new IllegalStateException("Pose stack not empty");
        }
    }

    private void renderEntity(Entity entity, double d, double d2, double d3, float f, PoseStack poseStack, MultiBufferSource multiBufferSource) {
        double d4 = Mth.lerp((double)f, entity.xOld, entity.getX());
        double d5 = Mth.lerp((double)f, entity.yOld, entity.getY());
        double d6 = Mth.lerp((double)f, entity.zOld, entity.getZ());
        float f2 = Mth.lerp(f, entity.yRotO, entity.yRot);
        this.entityRenderDispatcher.render(entity, d4 - d, d5 - d2, d6 - d3, f2, f, poseStack, multiBufferSource, this.entityRenderDispatcher.getPackedLightCoords(entity, f));
    }

    private void renderChunkLayer(RenderType renderType, PoseStack poseStack, double d, double d2, double d3) {
        renderType.setupRenderState();
        if (renderType == RenderType.translucent()) {
            this.minecraft.getProfiler().push("translucent_sort");
            double d4 = d - this.xTransparentOld;
            double d5 = d2 - this.yTransparentOld;
            double d6 = d3 - this.zTransparentOld;
            if (d4 * d4 + d5 * d5 + d6 * d6 > 1.0) {
                this.xTransparentOld = d;
                this.yTransparentOld = d2;
                this.zTransparentOld = d3;
                int n = 0;
                for (RenderChunkInfo renderChunkInfo : this.renderChunks) {
                    if (n >= 15 || !renderChunkInfo.chunk.resortTransparency(renderType, this.chunkRenderDispatcher)) continue;
                    ++n;
                }
            }
            this.minecraft.getProfiler().pop();
        }
        this.minecraft.getProfiler().push("filterempty");
        this.minecraft.getProfiler().popPush(() -> "render_" + renderType);
        boolean bl = renderType != RenderType.translucent();
        ObjectListIterator objectListIterator = this.renderChunks.listIterator(bl ? 0 : this.renderChunks.size());
        while (bl ? objectListIterator.hasNext() : objectListIterator.hasPrevious()) {
            RenderChunkInfo renderChunkInfo = bl ? (RenderChunkInfo)objectListIterator.next() : (RenderChunkInfo)objectListIterator.previous();
            ChunkRenderDispatcher.RenderChunk renderChunk = renderChunkInfo.chunk;
            if (renderChunk.getCompiledChunk().isEmpty(renderType)) continue;
            VertexBuffer vertexBuffer = renderChunk.getBuffer(renderType);
            poseStack.pushPose();
            BlockPos blockPos = renderChunk.getOrigin();
            poseStack.translate((double)blockPos.getX() - d, (double)blockPos.getY() - d2, (double)blockPos.getZ() - d3);
            vertexBuffer.bind();
            this.format.setupBufferState(0L);
            vertexBuffer.draw(poseStack.last().pose(), 7);
            poseStack.popPose();
        }
        VertexBuffer.unbind();
        RenderSystem.clearCurrentColor();
        this.format.clearBufferState();
        this.minecraft.getProfiler().pop();
        renderType.clearRenderState();
    }

    private void renderDebug(Camera camera) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        if (this.minecraft.chunkPath || this.minecraft.chunkVisibility) {
            double d = camera.getPosition().x();
            double d2 = camera.getPosition().y();
            double d3 = camera.getPosition().z();
            RenderSystem.depthMask(true);
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableTexture();
            for (RenderChunkInfo renderChunkInfo : this.renderChunks) {
                int n;
                ChunkRenderDispatcher.RenderChunk renderChunk = renderChunkInfo.chunk;
                RenderSystem.pushMatrix();
                BlockPos blockPos = renderChunk.getOrigin();
                RenderSystem.translated((double)blockPos.getX() - d, (double)blockPos.getY() - d2, (double)blockPos.getZ() - d3);
                if (this.minecraft.chunkPath) {
                    bufferBuilder.begin(1, DefaultVertexFormat.POSITION_COLOR);
                    RenderSystem.lineWidth(10.0f);
                    n = renderChunkInfo.step == 0 ? 0 : Mth.hsvToRgb((float)renderChunkInfo.step / 50.0f, 0.9f, 0.9f);
                    int n2 = n >> 16 & 0xFF;
                    int n3 = n >> 8 & 0xFF;
                    int n4 = n & 0xFF;
                    Direction direction = renderChunkInfo.sourceDirection;
                    if (direction != null) {
                        bufferBuilder.vertex(8.0, 8.0, 8.0).color(n2, n3, n4, 255).endVertex();
                        bufferBuilder.vertex(8 - 16 * direction.getStepX(), 8 - 16 * direction.getStepY(), 8 - 16 * direction.getStepZ()).color(n2, n3, n4, 255).endVertex();
                    }
                    tesselator.end();
                    RenderSystem.lineWidth(1.0f);
                }
                if (this.minecraft.chunkVisibility && !renderChunk.getCompiledChunk().hasNoRenderableLayers()) {
                    bufferBuilder.begin(1, DefaultVertexFormat.POSITION_COLOR);
                    RenderSystem.lineWidth(10.0f);
                    n = 0;
                    for (Direction direction : DIRECTIONS) {
                        for (Direction direction2 : DIRECTIONS) {
                            boolean bl = renderChunk.getCompiledChunk().facesCanSeeEachother(direction, direction2);
                            if (bl) continue;
                            ++n;
                            bufferBuilder.vertex(8 + 8 * direction.getStepX(), 8 + 8 * direction.getStepY(), 8 + 8 * direction.getStepZ()).color(1, 0, 0, 1).endVertex();
                            bufferBuilder.vertex(8 + 8 * direction2.getStepX(), 8 + 8 * direction2.getStepY(), 8 + 8 * direction2.getStepZ()).color(1, 0, 0, 1).endVertex();
                        }
                    }
                    tesselator.end();
                    RenderSystem.lineWidth(1.0f);
                    if (n > 0) {
                        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
                        float f = 0.5f;
                        float f2 = 0.2f;
                        bufferBuilder.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(0.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(15.5, 0.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(15.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(0.5, 15.5, 0.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(0.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(15.5, 15.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(15.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        bufferBuilder.vertex(0.5, 0.5, 15.5).color(0.9f, 0.9f, 0.0f, 0.2f).endVertex();
                        tesselator.end();
                    }
                }
                RenderSystem.popMatrix();
            }
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableTexture();
        }
        if (this.capturedFrustum != null) {
            RenderSystem.disableCull();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.lineWidth(10.0f);
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(this.frustumPos.x - camera.getPosition().x), (float)(this.frustumPos.y - camera.getPosition().y), (float)(this.frustumPos.z - camera.getPosition().z));
            RenderSystem.depthMask(true);
            bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
            this.addFrustumQuad(bufferBuilder, 0, 1, 2, 3, 0, 1, 1);
            this.addFrustumQuad(bufferBuilder, 4, 5, 6, 7, 1, 0, 0);
            this.addFrustumQuad(bufferBuilder, 0, 1, 5, 4, 1, 1, 0);
            this.addFrustumQuad(bufferBuilder, 2, 3, 7, 6, 0, 0, 1);
            this.addFrustumQuad(bufferBuilder, 0, 4, 7, 3, 0, 1, 0);
            this.addFrustumQuad(bufferBuilder, 1, 5, 6, 2, 1, 0, 1);
            tesselator.end();
            RenderSystem.depthMask(false);
            bufferBuilder.begin(1, DefaultVertexFormat.POSITION);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.addFrustumVertex(bufferBuilder, 0);
            this.addFrustumVertex(bufferBuilder, 1);
            this.addFrustumVertex(bufferBuilder, 1);
            this.addFrustumVertex(bufferBuilder, 2);
            this.addFrustumVertex(bufferBuilder, 2);
            this.addFrustumVertex(bufferBuilder, 3);
            this.addFrustumVertex(bufferBuilder, 3);
            this.addFrustumVertex(bufferBuilder, 0);
            this.addFrustumVertex(bufferBuilder, 4);
            this.addFrustumVertex(bufferBuilder, 5);
            this.addFrustumVertex(bufferBuilder, 5);
            this.addFrustumVertex(bufferBuilder, 6);
            this.addFrustumVertex(bufferBuilder, 6);
            this.addFrustumVertex(bufferBuilder, 7);
            this.addFrustumVertex(bufferBuilder, 7);
            this.addFrustumVertex(bufferBuilder, 4);
            this.addFrustumVertex(bufferBuilder, 0);
            this.addFrustumVertex(bufferBuilder, 4);
            this.addFrustumVertex(bufferBuilder, 1);
            this.addFrustumVertex(bufferBuilder, 5);
            this.addFrustumVertex(bufferBuilder, 2);
            this.addFrustumVertex(bufferBuilder, 6);
            this.addFrustumVertex(bufferBuilder, 3);
            this.addFrustumVertex(bufferBuilder, 7);
            tesselator.end();
            RenderSystem.popMatrix();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableTexture();
            RenderSystem.lineWidth(1.0f);
        }
    }

    private void addFrustumVertex(VertexConsumer vertexConsumer, int n) {
        vertexConsumer.vertex(this.frustumPoints[n].x(), this.frustumPoints[n].y(), this.frustumPoints[n].z()).endVertex();
    }

    private void addFrustumQuad(VertexConsumer vertexConsumer, int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        float f = 0.25f;
        vertexConsumer.vertex(this.frustumPoints[n].x(), this.frustumPoints[n].y(), this.frustumPoints[n].z()).color((float)n5, (float)n6, (float)n7, 0.25f).endVertex();
        vertexConsumer.vertex(this.frustumPoints[n2].x(), this.frustumPoints[n2].y(), this.frustumPoints[n2].z()).color((float)n5, (float)n6, (float)n7, 0.25f).endVertex();
        vertexConsumer.vertex(this.frustumPoints[n3].x(), this.frustumPoints[n3].y(), this.frustumPoints[n3].z()).color((float)n5, (float)n6, (float)n7, 0.25f).endVertex();
        vertexConsumer.vertex(this.frustumPoints[n4].x(), this.frustumPoints[n4].y(), this.frustumPoints[n4].z()).color((float)n5, (float)n6, (float)n7, 0.25f).endVertex();
    }

    public void tick() {
        ++this.ticks;
        if (this.ticks % 20 != 0) {
            return;
        }
        ObjectIterator objectIterator = this.destroyingBlocks.values().iterator();
        while (objectIterator.hasNext()) {
            BlockDestructionProgress blockDestructionProgress = (BlockDestructionProgress)objectIterator.next();
            int n = blockDestructionProgress.getUpdatedRenderTick();
            if (this.ticks - n <= 400) continue;
            objectIterator.remove();
            this.removeProgress(blockDestructionProgress);
        }
    }

    private void removeProgress(BlockDestructionProgress blockDestructionProgress) {
        long l = blockDestructionProgress.getPos().asLong();
        Set set = (Set)this.destructionProgress.get(l);
        set.remove(blockDestructionProgress);
        if (set.isEmpty()) {
            this.destructionProgress.remove(l);
        }
    }

    private void renderEndSky(PoseStack poseStack) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        this.textureManager.bind(END_SKY_LOCATION);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        for (int i = 0; i < 6; ++i) {
            poseStack.pushPose();
            if (i == 1) {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0f));
            }
            if (i == 2) {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0f));
            }
            if (i == 3) {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
            }
            if (i == 4) {
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0f));
            }
            if (i == 5) {
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0f));
            }
            Matrix4f matrix4f = poseStack.last().pose();
            bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, -100.0f).uv(0.0f, 0.0f).color(40, 40, 40, 255).endVertex();
            bufferBuilder.vertex(matrix4f, -100.0f, -100.0f, 100.0f).uv(0.0f, 16.0f).color(40, 40, 40, 255).endVertex();
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, 100.0f).uv(16.0f, 16.0f).color(40, 40, 40, 255).endVertex();
            bufferBuilder.vertex(matrix4f, 100.0f, -100.0f, -100.0f).uv(16.0f, 0.0f).color(40, 40, 40, 255).endVertex();
            tesselator.end();
            poseStack.popPose();
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    public void renderSky(PoseStack poseStack, float f) {
        float f2;
        float f3;
        float f4;
        float f5;
        int n;
        float f6;
        if (this.minecraft.level.effects().skyType() == DimensionSpecialEffects.SkyType.END) {
            this.renderEndSky(poseStack);
            return;
        }
        if (this.minecraft.level.effects().skyType() != DimensionSpecialEffects.SkyType.NORMAL) {
            return;
        }
        RenderSystem.disableTexture();
        Vec3 vec3 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getBlockPosition(), f);
        float f7 = (float)vec3.x;
        float f8 = (float)vec3.y;
        float f9 = (float)vec3.z;
        FogRenderer.levelFogColor();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.depthMask(false);
        RenderSystem.enableFog();
        RenderSystem.color3f(f7, f8, f9);
        this.skyBuffer.bind();
        this.skyFormat.setupBufferState(0L);
        this.skyBuffer.draw(poseStack.last().pose(), 7);
        VertexBuffer.unbind();
        this.skyFormat.clearBufferState();
        RenderSystem.disableFog();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float[] arrf = this.level.effects().getSunriseColor(this.level.getTimeOfDay(f), f);
        if (arrf != null) {
            RenderSystem.disableTexture();
            RenderSystem.shadeModel(7425);
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0f));
            f4 = Mth.sin(this.level.getSunAngle(f)) < 0.0f ? 180.0f : 0.0f;
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(f4));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0f));
            float f10 = arrf[0];
            f3 = arrf[1];
            float f11 = arrf[2];
            Matrix4f matrix4f = poseStack.last().pose();
            bufferBuilder.begin(6, DefaultVertexFormat.POSITION_COLOR);
            bufferBuilder.vertex(matrix4f, 0.0f, 100.0f, 0.0f).color(f10, f3, f11, arrf[3]).endVertex();
            n = 16;
            for (int i = 0; i <= 16; ++i) {
                f5 = (float)i * 6.2831855f / 16.0f;
                f2 = Mth.sin(f5);
                f6 = Mth.cos(f5);
                bufferBuilder.vertex(matrix4f, f2 * 120.0f, f6 * 120.0f, -f6 * 40.0f * arrf[3]).color(arrf[0], arrf[1], arrf[2], 0.0f).endVertex();
            }
            bufferBuilder.end();
            BufferUploader.end(bufferBuilder);
            poseStack.popPose();
            RenderSystem.shadeModel(7424);
        }
        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        poseStack.pushPose();
        f4 = 1.0f - this.level.getRainLevel(f);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, f4);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-90.0f));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(this.level.getTimeOfDay(f) * 360.0f));
        Matrix4f matrix4f = poseStack.last().pose();
        f3 = 30.0f;
        this.textureManager.bind(SUN_LOCATION);
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, -f3, 100.0f, -f3).uv(0.0f, 0.0f).endVertex();
        bufferBuilder.vertex(matrix4f, f3, 100.0f, -f3).uv(1.0f, 0.0f).endVertex();
        bufferBuilder.vertex(matrix4f, f3, 100.0f, f3).uv(1.0f, 1.0f).endVertex();
        bufferBuilder.vertex(matrix4f, -f3, 100.0f, f3).uv(0.0f, 1.0f).endVertex();
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        f3 = 20.0f;
        this.textureManager.bind(MOON_LOCATION);
        int n2 = this.level.getMoonPhase();
        int n3 = n2 % 4;
        n = n2 / 4 % 2;
        float f12 = (float)(n3 + 0) / 4.0f;
        f5 = (float)(n + 0) / 2.0f;
        f2 = (float)(n3 + 1) / 4.0f;
        f6 = (float)(n + 1) / 2.0f;
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, -f3, -100.0f, f3).uv(f2, f6).endVertex();
        bufferBuilder.vertex(matrix4f, f3, -100.0f, f3).uv(f12, f6).endVertex();
        bufferBuilder.vertex(matrix4f, f3, -100.0f, -f3).uv(f12, f5).endVertex();
        bufferBuilder.vertex(matrix4f, -f3, -100.0f, -f3).uv(f2, f5).endVertex();
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.disableTexture();
        float f13 = this.level.getStarBrightness(f) * f4;
        if (f13 > 0.0f) {
            RenderSystem.color4f(f13, f13, f13, f13);
            this.starBuffer.bind();
            this.skyFormat.setupBufferState(0L);
            this.starBuffer.draw(poseStack.last().pose(), 7);
            VertexBuffer.unbind();
            this.skyFormat.clearBufferState();
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableFog();
        poseStack.popPose();
        RenderSystem.disableTexture();
        RenderSystem.color3f(0.0f, 0.0f, 0.0f);
        double d = this.minecraft.player.getEyePosition((float)f).y - this.level.getLevelData().getHorizonHeight();
        if (d < 0.0) {
            poseStack.pushPose();
            poseStack.translate(0.0, 12.0, 0.0);
            this.darkBuffer.bind();
            this.skyFormat.setupBufferState(0L);
            this.darkBuffer.draw(poseStack.last().pose(), 7);
            VertexBuffer.unbind();
            this.skyFormat.clearBufferState();
            poseStack.popPose();
        }
        if (this.level.effects().hasGround()) {
            RenderSystem.color3f(f7 * 0.2f + 0.04f, f8 * 0.2f + 0.04f, f9 * 0.6f + 0.1f);
        } else {
            RenderSystem.color3f(f7, f8, f9);
        }
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.disableFog();
    }

    public void renderClouds(PoseStack poseStack, float f, double d, double d2, double d3) {
        float f2 = this.level.effects().getCloudHeight();
        if (Float.isNaN(f2)) {
            return;
        }
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableFog();
        RenderSystem.depthMask(true);
        float f3 = 12.0f;
        float f4 = 4.0f;
        double d4 = 2.0E-4;
        double d5 = ((float)this.ticks + f) * 0.03f;
        double d6 = (d + d5) / 12.0;
        double d7 = f2 - (float)d2 + 0.33f;
        double d8 = d3 / 12.0 + 0.33000001311302185;
        d6 -= (double)(Mth.floor(d6 / 2048.0) * 2048);
        d8 -= (double)(Mth.floor(d8 / 2048.0) * 2048);
        float f5 = (float)(d6 - (double)Mth.floor(d6));
        float f6 = (float)(d7 / 4.0 - (double)Mth.floor(d7 / 4.0)) * 4.0f;
        float f7 = (float)(d8 - (double)Mth.floor(d8));
        Vec3 vec3 = this.level.getCloudColor(f);
        int n = (int)Math.floor(d6);
        int n2 = (int)Math.floor(d7 / 4.0);
        int n3 = (int)Math.floor(d8);
        if (n != this.prevCloudX || n2 != this.prevCloudY || n3 != this.prevCloudZ || this.minecraft.options.getCloudsType() != this.prevCloudsType || this.prevCloudColor.distanceToSqr(vec3) > 2.0E-4) {
            this.prevCloudX = n;
            this.prevCloudY = n2;
            this.prevCloudZ = n3;
            this.prevCloudColor = vec3;
            this.prevCloudsType = this.minecraft.options.getCloudsType();
            this.generateClouds = true;
        }
        if (this.generateClouds) {
            this.generateClouds = false;
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
            if (this.cloudBuffer != null) {
                this.cloudBuffer.close();
            }
            this.cloudBuffer = new VertexBuffer(DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
            this.buildClouds(bufferBuilder, d6, d7, d8, vec3);
            bufferBuilder.end();
            this.cloudBuffer.upload(bufferBuilder);
        }
        this.textureManager.bind(CLOUDS_LOCATION);
        poseStack.pushPose();
        poseStack.scale(12.0f, 1.0f, 12.0f);
        poseStack.translate(-f5, f6, -f7);
        if (this.cloudBuffer != null) {
            int n4;
            this.cloudBuffer.bind();
            DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.setupBufferState(0L);
            for (int i = n4 = this.prevCloudsType == CloudStatus.FANCY ? 0 : 1; i < 2; ++i) {
                if (i == 0) {
                    RenderSystem.colorMask(false, false, false, false);
                } else {
                    RenderSystem.colorMask(true, true, true, true);
                }
                this.cloudBuffer.draw(poseStack.last().pose(), 7);
            }
            VertexBuffer.unbind();
            DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.clearBufferState();
        }
        poseStack.popPose();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableAlphaTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.disableFog();
    }

    private void buildClouds(BufferBuilder bufferBuilder, double d, double d2, double d3, Vec3 vec3) {
        float f = 4.0f;
        float f2 = 0.00390625f;
        int n = 8;
        int n2 = 4;
        float f3 = 9.765625E-4f;
        float f4 = (float)Mth.floor(d) * 0.00390625f;
        float f5 = (float)Mth.floor(d3) * 0.00390625f;
        float f6 = (float)vec3.x;
        float f7 = (float)vec3.y;
        float f8 = (float)vec3.z;
        float f9 = f6 * 0.9f;
        float f10 = f7 * 0.9f;
        float f11 = f8 * 0.9f;
        float f12 = f6 * 0.7f;
        float f13 = f7 * 0.7f;
        float f14 = f8 * 0.7f;
        float f15 = f6 * 0.8f;
        float f16 = f7 * 0.8f;
        float f17 = f8 * 0.8f;
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        float f18 = (float)Math.floor(d2 / 4.0) * 4.0f;
        if (this.prevCloudsType == CloudStatus.FANCY) {
            for (int i = -3; i <= 4; ++i) {
                for (int j = -3; j <= 4; ++j) {
                    int n3;
                    float f19 = i * 8;
                    float f20 = j * 8;
                    if (f18 > -5.0f) {
                        bufferBuilder.vertex(f19 + 0.0f, f18 + 0.0f, f20 + 8.0f).uv((f19 + 0.0f) * 0.00390625f + f4, (f20 + 8.0f) * 0.00390625f + f5).color(f12, f13, f14, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        bufferBuilder.vertex(f19 + 8.0f, f18 + 0.0f, f20 + 8.0f).uv((f19 + 8.0f) * 0.00390625f + f4, (f20 + 8.0f) * 0.00390625f + f5).color(f12, f13, f14, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        bufferBuilder.vertex(f19 + 8.0f, f18 + 0.0f, f20 + 0.0f).uv((f19 + 8.0f) * 0.00390625f + f4, (f20 + 0.0f) * 0.00390625f + f5).color(f12, f13, f14, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                        bufferBuilder.vertex(f19 + 0.0f, f18 + 0.0f, f20 + 0.0f).uv((f19 + 0.0f) * 0.00390625f + f4, (f20 + 0.0f) * 0.00390625f + f5).color(f12, f13, f14, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    }
                    if (f18 <= 5.0f) {
                        bufferBuilder.vertex(f19 + 0.0f, f18 + 4.0f - 9.765625E-4f, f20 + 8.0f).uv((f19 + 0.0f) * 0.00390625f + f4, (f20 + 8.0f) * 0.00390625f + f5).color(f6, f7, f8, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        bufferBuilder.vertex(f19 + 8.0f, f18 + 4.0f - 9.765625E-4f, f20 + 8.0f).uv((f19 + 8.0f) * 0.00390625f + f4, (f20 + 8.0f) * 0.00390625f + f5).color(f6, f7, f8, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        bufferBuilder.vertex(f19 + 8.0f, f18 + 4.0f - 9.765625E-4f, f20 + 0.0f).uv((f19 + 8.0f) * 0.00390625f + f4, (f20 + 0.0f) * 0.00390625f + f5).color(f6, f7, f8, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                        bufferBuilder.vertex(f19 + 0.0f, f18 + 4.0f - 9.765625E-4f, f20 + 0.0f).uv((f19 + 0.0f) * 0.00390625f + f4, (f20 + 0.0f) * 0.00390625f + f5).color(f6, f7, f8, 0.8f).normal(0.0f, 1.0f, 0.0f).endVertex();
                    }
                    if (i > -1) {
                        for (n3 = 0; n3 < 8; ++n3) {
                            bufferBuilder.vertex(f19 + (float)n3 + 0.0f, f18 + 0.0f, f20 + 8.0f).uv((f19 + (float)n3 + 0.5f) * 0.00390625f + f4, (f20 + 8.0f) * 0.00390625f + f5).color(f9, f10, f11, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            bufferBuilder.vertex(f19 + (float)n3 + 0.0f, f18 + 4.0f, f20 + 8.0f).uv((f19 + (float)n3 + 0.5f) * 0.00390625f + f4, (f20 + 8.0f) * 0.00390625f + f5).color(f9, f10, f11, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            bufferBuilder.vertex(f19 + (float)n3 + 0.0f, f18 + 4.0f, f20 + 0.0f).uv((f19 + (float)n3 + 0.5f) * 0.00390625f + f4, (f20 + 0.0f) * 0.00390625f + f5).color(f9, f10, f11, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                            bufferBuilder.vertex(f19 + (float)n3 + 0.0f, f18 + 0.0f, f20 + 0.0f).uv((f19 + (float)n3 + 0.5f) * 0.00390625f + f4, (f20 + 0.0f) * 0.00390625f + f5).color(f9, f10, f11, 0.8f).normal(-1.0f, 0.0f, 0.0f).endVertex();
                        }
                    }
                    if (i <= 1) {
                        for (n3 = 0; n3 < 8; ++n3) {
                            bufferBuilder.vertex(f19 + (float)n3 + 1.0f - 9.765625E-4f, f18 + 0.0f, f20 + 8.0f).uv((f19 + (float)n3 + 0.5f) * 0.00390625f + f4, (f20 + 8.0f) * 0.00390625f + f5).color(f9, f10, f11, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            bufferBuilder.vertex(f19 + (float)n3 + 1.0f - 9.765625E-4f, f18 + 4.0f, f20 + 8.0f).uv((f19 + (float)n3 + 0.5f) * 0.00390625f + f4, (f20 + 8.0f) * 0.00390625f + f5).color(f9, f10, f11, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            bufferBuilder.vertex(f19 + (float)n3 + 1.0f - 9.765625E-4f, f18 + 4.0f, f20 + 0.0f).uv((f19 + (float)n3 + 0.5f) * 0.00390625f + f4, (f20 + 0.0f) * 0.00390625f + f5).color(f9, f10, f11, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                            bufferBuilder.vertex(f19 + (float)n3 + 1.0f - 9.765625E-4f, f18 + 0.0f, f20 + 0.0f).uv((f19 + (float)n3 + 0.5f) * 0.00390625f + f4, (f20 + 0.0f) * 0.00390625f + f5).color(f9, f10, f11, 0.8f).normal(1.0f, 0.0f, 0.0f).endVertex();
                        }
                    }
                    if (j > -1) {
                        for (n3 = 0; n3 < 8; ++n3) {
                            bufferBuilder.vertex(f19 + 0.0f, f18 + 4.0f, f20 + (float)n3 + 0.0f).uv((f19 + 0.0f) * 0.00390625f + f4, (f20 + (float)n3 + 0.5f) * 0.00390625f + f5).color(f15, f16, f17, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            bufferBuilder.vertex(f19 + 8.0f, f18 + 4.0f, f20 + (float)n3 + 0.0f).uv((f19 + 8.0f) * 0.00390625f + f4, (f20 + (float)n3 + 0.5f) * 0.00390625f + f5).color(f15, f16, f17, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            bufferBuilder.vertex(f19 + 8.0f, f18 + 0.0f, f20 + (float)n3 + 0.0f).uv((f19 + 8.0f) * 0.00390625f + f4, (f20 + (float)n3 + 0.5f) * 0.00390625f + f5).color(f15, f16, f17, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                            bufferBuilder.vertex(f19 + 0.0f, f18 + 0.0f, f20 + (float)n3 + 0.0f).uv((f19 + 0.0f) * 0.00390625f + f4, (f20 + (float)n3 + 0.5f) * 0.00390625f + f5).color(f15, f16, f17, 0.8f).normal(0.0f, 0.0f, -1.0f).endVertex();
                        }
                    }
                    if (j > 1) continue;
                    for (n3 = 0; n3 < 8; ++n3) {
                        bufferBuilder.vertex(f19 + 0.0f, f18 + 4.0f, f20 + (float)n3 + 1.0f - 9.765625E-4f).uv((f19 + 0.0f) * 0.00390625f + f4, (f20 + (float)n3 + 0.5f) * 0.00390625f + f5).color(f15, f16, f17, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                        bufferBuilder.vertex(f19 + 8.0f, f18 + 4.0f, f20 + (float)n3 + 1.0f - 9.765625E-4f).uv((f19 + 8.0f) * 0.00390625f + f4, (f20 + (float)n3 + 0.5f) * 0.00390625f + f5).color(f15, f16, f17, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                        bufferBuilder.vertex(f19 + 8.0f, f18 + 0.0f, f20 + (float)n3 + 1.0f - 9.765625E-4f).uv((f19 + 8.0f) * 0.00390625f + f4, (f20 + (float)n3 + 0.5f) * 0.00390625f + f5).color(f15, f16, f17, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                        bufferBuilder.vertex(f19 + 0.0f, f18 + 0.0f, f20 + (float)n3 + 1.0f - 9.765625E-4f).uv((f19 + 0.0f) * 0.00390625f + f4, (f20 + (float)n3 + 0.5f) * 0.00390625f + f5).color(f15, f16, f17, 0.8f).normal(0.0f, 0.0f, 1.0f).endVertex();
                    }
                }
            }
        } else {
            boolean bl = true;
            int n4 = 32;
            for (int i = -32; i < 32; i += 32) {
                for (int j = -32; j < 32; j += 32) {
                    bufferBuilder.vertex(i + 0, f18, j + 32).uv((float)(i + 0) * 0.00390625f + f4, (float)(j + 32) * 0.00390625f + f5).color(f6, f7, f8, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    bufferBuilder.vertex(i + 32, f18, j + 32).uv((float)(i + 32) * 0.00390625f + f4, (float)(j + 32) * 0.00390625f + f5).color(f6, f7, f8, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    bufferBuilder.vertex(i + 32, f18, j + 0).uv((float)(i + 32) * 0.00390625f + f4, (float)(j + 0) * 0.00390625f + f5).color(f6, f7, f8, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                    bufferBuilder.vertex(i + 0, f18, j + 0).uv((float)(i + 0) * 0.00390625f + f4, (float)(j + 0) * 0.00390625f + f5).color(f6, f7, f8, 0.8f).normal(0.0f, -1.0f, 0.0f).endVertex();
                }
            }
        }
    }

    private void compileChunksUntil(long l) {
        this.needsUpdate |= this.chunkRenderDispatcher.uploadAllPendingUploads();
        long l2 = Util.getNanos();
        int n = 0;
        if (!this.chunksToCompile.isEmpty()) {
            Iterator<ChunkRenderDispatcher.RenderChunk> iterator = this.chunksToCompile.iterator();
            while (iterator.hasNext()) {
                long l3;
                long l4;
                ChunkRenderDispatcher.RenderChunk renderChunk = iterator.next();
                if (renderChunk.isDirtyFromPlayer()) {
                    this.chunkRenderDispatcher.rebuildChunkSync(renderChunk);
                } else {
                    renderChunk.rebuildChunkAsync(this.chunkRenderDispatcher);
                }
                renderChunk.setNotDirty();
                iterator.remove();
                long l5 = Util.getNanos();
                long l6 = l - l5;
                if (l6 >= (l3 = (l4 = l5 - l2) / (long)(++n))) continue;
                break;
            }
        }
    }

    private void renderWorldBounds(Camera camera) {
        double d;
        double d2;
        float f;
        float f2;
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        WorldBorder worldBorder = this.level.getWorldBorder();
        double d3 = this.minecraft.options.renderDistance * 16;
        if (camera.getPosition().x < worldBorder.getMaxX() - d3 && camera.getPosition().x > worldBorder.getMinX() + d3 && camera.getPosition().z < worldBorder.getMaxZ() - d3 && camera.getPosition().z > worldBorder.getMinZ() + d3) {
            return;
        }
        double d4 = 1.0 - worldBorder.getDistanceToBorder(camera.getPosition().x, camera.getPosition().z) / d3;
        d4 = Math.pow(d4, 4.0);
        double d5 = camera.getPosition().x;
        double d6 = camera.getPosition().y;
        double d7 = camera.getPosition().z;
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        this.textureManager.bind(FORCEFIELD_LOCATION);
        RenderSystem.depthMask(Minecraft.useShaderTransparency());
        RenderSystem.pushMatrix();
        int n = worldBorder.getStatus().getColor();
        float f3 = (float)(n >> 16 & 0xFF) / 255.0f;
        float f4 = (float)(n >> 8 & 0xFF) / 255.0f;
        float f5 = (float)(n & 0xFF) / 255.0f;
        RenderSystem.color4f(f3, f4, f5, (float)d4);
        RenderSystem.polygonOffset(-3.0f, -3.0f);
        RenderSystem.enablePolygonOffset();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableAlphaTest();
        RenderSystem.disableCull();
        float f6 = (float)(Util.getMillis() % 3000L) / 3000.0f;
        float f7 = 0.0f;
        float f8 = 0.0f;
        float f9 = 128.0f;
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX);
        double d8 = Math.max((double)Mth.floor(d7 - d3), worldBorder.getMinZ());
        double d9 = Math.min((double)Mth.ceil(d7 + d3), worldBorder.getMaxZ());
        if (d5 > worldBorder.getMaxX() - d3) {
            f2 = 0.0f;
            d = d8;
            while (d < d9) {
                d2 = Math.min(1.0, d9 - d);
                f = (float)d2 * 0.5f;
                this.vertex(bufferBuilder, d5, d6, d7, worldBorder.getMaxX(), 256, d, f6 + f2, f6 + 0.0f);
                this.vertex(bufferBuilder, d5, d6, d7, worldBorder.getMaxX(), 256, d + d2, f6 + f + f2, f6 + 0.0f);
                this.vertex(bufferBuilder, d5, d6, d7, worldBorder.getMaxX(), 0, d + d2, f6 + f + f2, f6 + 128.0f);
                this.vertex(bufferBuilder, d5, d6, d7, worldBorder.getMaxX(), 0, d, f6 + f2, f6 + 128.0f);
                d += 1.0;
                f2 += 0.5f;
            }
        }
        if (d5 < worldBorder.getMinX() + d3) {
            f2 = 0.0f;
            d = d8;
            while (d < d9) {
                d2 = Math.min(1.0, d9 - d);
                f = (float)d2 * 0.5f;
                this.vertex(bufferBuilder, d5, d6, d7, worldBorder.getMinX(), 256, d, f6 + f2, f6 + 0.0f);
                this.vertex(bufferBuilder, d5, d6, d7, worldBorder.getMinX(), 256, d + d2, f6 + f + f2, f6 + 0.0f);
                this.vertex(bufferBuilder, d5, d6, d7, worldBorder.getMinX(), 0, d + d2, f6 + f + f2, f6 + 128.0f);
                this.vertex(bufferBuilder, d5, d6, d7, worldBorder.getMinX(), 0, d, f6 + f2, f6 + 128.0f);
                d += 1.0;
                f2 += 0.5f;
            }
        }
        d8 = Math.max((double)Mth.floor(d5 - d3), worldBorder.getMinX());
        d9 = Math.min((double)Mth.ceil(d5 + d3), worldBorder.getMaxX());
        if (d7 > worldBorder.getMaxZ() - d3) {
            f2 = 0.0f;
            d = d8;
            while (d < d9) {
                d2 = Math.min(1.0, d9 - d);
                f = (float)d2 * 0.5f;
                this.vertex(bufferBuilder, d5, d6, d7, d, 256, worldBorder.getMaxZ(), f6 + f2, f6 + 0.0f);
                this.vertex(bufferBuilder, d5, d6, d7, d + d2, 256, worldBorder.getMaxZ(), f6 + f + f2, f6 + 0.0f);
                this.vertex(bufferBuilder, d5, d6, d7, d + d2, 0, worldBorder.getMaxZ(), f6 + f + f2, f6 + 128.0f);
                this.vertex(bufferBuilder, d5, d6, d7, d, 0, worldBorder.getMaxZ(), f6 + f2, f6 + 128.0f);
                d += 1.0;
                f2 += 0.5f;
            }
        }
        if (d7 < worldBorder.getMinZ() + d3) {
            f2 = 0.0f;
            d = d8;
            while (d < d9) {
                d2 = Math.min(1.0, d9 - d);
                f = (float)d2 * 0.5f;
                this.vertex(bufferBuilder, d5, d6, d7, d, 256, worldBorder.getMinZ(), f6 + f2, f6 + 0.0f);
                this.vertex(bufferBuilder, d5, d6, d7, d + d2, 256, worldBorder.getMinZ(), f6 + f + f2, f6 + 0.0f);
                this.vertex(bufferBuilder, d5, d6, d7, d + d2, 0, worldBorder.getMinZ(), f6 + f + f2, f6 + 128.0f);
                this.vertex(bufferBuilder, d5, d6, d7, d, 0, worldBorder.getMinZ(), f6 + f2, f6 + 128.0f);
                d += 1.0;
                f2 += 0.5f;
            }
        }
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.enableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.polygonOffset(0.0f, 0.0f);
        RenderSystem.disablePolygonOffset();
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        RenderSystem.depthMask(true);
    }

    private void vertex(BufferBuilder bufferBuilder, double d, double d2, double d3, double d4, int n, double d5, float f, float f2) {
        bufferBuilder.vertex(d4 - d, (double)n - d2, d5 - d3).uv(f, f2).endVertex();
    }

    private void renderHitOutline(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, double d, double d2, double d3, BlockPos blockPos, BlockState blockState) {
        LevelRenderer.renderShape(poseStack, vertexConsumer, blockState.getShape(this.level, blockPos, CollisionContext.of(entity)), (double)blockPos.getX() - d, (double)blockPos.getY() - d2, (double)blockPos.getZ() - d3, 0.0f, 0.0f, 0.0f, 0.4f);
    }

    public static void renderVoxelShape(PoseStack poseStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double d2, double d3, float f, float f2, float f3, float f4) {
        List<AABB> list = voxelShape.toAabbs();
        int n = Mth.ceil((double)list.size() / 3.0);
        for (int i = 0; i < list.size(); ++i) {
            AABB aABB = list.get(i);
            float f5 = ((float)i % (float)n + 1.0f) / (float)n;
            float f6 = i / n;
            float f7 = f5 * (float)(f6 == 0.0f);
            float f8 = f5 * (float)(f6 == 1.0f);
            float f9 = f5 * (float)(f6 == 2.0f);
            LevelRenderer.renderShape(poseStack, vertexConsumer, Shapes.create(aABB.move(0.0, 0.0, 0.0)), d, d2, d3, f7, f8, f9, 1.0f);
        }
    }

    private static void renderShape(PoseStack poseStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double d2, double d3, float f, float f2, float f3, float f4) {
        Matrix4f matrix4f = poseStack.last().pose();
        voxelShape.forAllEdges((d4, d5, d6, d7, d8, d9) -> {
            vertexConsumer.vertex(matrix4f, (float)(d4 + d), (float)(d5 + d2), (float)(d6 + d3)).color(f, f2, f3, f4).endVertex();
            vertexConsumer.vertex(matrix4f, (float)(d7 + d), (float)(d8 + d2), (float)(d9 + d3)).color(f, f2, f3, f4).endVertex();
        });
    }

    public static void renderLineBox(PoseStack poseStack, VertexConsumer vertexConsumer, AABB aABB, float f, float f2, float f3, float f4) {
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, aABB.minX, aABB.minY, aABB.minZ, aABB.maxX, aABB.maxY, aABB.maxZ, f, f2, f3, f4, f, f2, f3);
    }

    public static void renderLineBox(PoseStack poseStack, VertexConsumer vertexConsumer, double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4) {
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, d, d2, d3, d4, d5, d6, f, f2, f3, f4, f, f2, f3);
    }

    public static void renderLineBox(PoseStack poseStack, VertexConsumer vertexConsumer, double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        Matrix4f matrix4f = poseStack.last().pose();
        float f8 = (float)d;
        float f9 = (float)d2;
        float f10 = (float)d3;
        float f11 = (float)d4;
        float f12 = (float)d5;
        float f13 = (float)d6;
        vertexConsumer.vertex(matrix4f, f8, f9, f10).color(f, f6, f7, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f11, f9, f10).color(f, f6, f7, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f8, f9, f10).color(f5, f2, f7, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f8, f12, f10).color(f5, f2, f7, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f8, f9, f10).color(f5, f6, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f8, f9, f13).color(f5, f6, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f11, f9, f10).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f11, f12, f10).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f11, f12, f10).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f8, f12, f10).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f8, f12, f10).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f8, f12, f13).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f8, f12, f13).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f8, f9, f13).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f8, f9, f13).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f11, f9, f13).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f11, f9, f13).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f11, f9, f10).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f8, f12, f13).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f11, f12, f13).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f11, f9, f13).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f11, f12, f13).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f11, f12, f10).color(f, f2, f3, f4).endVertex();
        vertexConsumer.vertex(matrix4f, f11, f12, f13).color(f, f2, f3, f4).endVertex();
    }

    public static void addChainedFilledBoxVertices(BufferBuilder bufferBuilder, double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4) {
        bufferBuilder.vertex(d, d2, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d2, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d2, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d2, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d5, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d5, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d5, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d2, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d5, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d2, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d2, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d2, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d5, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d5, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d5, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d2, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d5, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d2, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d2, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d2, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d2, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d2, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d2, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d5, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d5, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d, d5, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d5, d3).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d5, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d5, d6).color(f, f2, f3, f4).endVertex();
        bufferBuilder.vertex(d4, d5, d6).color(f, f2, f3, f4).endVertex();
    }

    public void blockChanged(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, BlockState blockState2, int n) {
        this.setBlockDirty(blockPos, (n & 8) != 0);
    }

    private void setBlockDirty(BlockPos blockPos, boolean bl) {
        for (int i = blockPos.getZ() - 1; i <= blockPos.getZ() + 1; ++i) {
            for (int j = blockPos.getX() - 1; j <= blockPos.getX() + 1; ++j) {
                for (int k = blockPos.getY() - 1; k <= blockPos.getY() + 1; ++k) {
                    this.setSectionDirty(j >> 4, k >> 4, i >> 4, bl);
                }
            }
        }
    }

    public void setBlocksDirty(int n, int n2, int n3, int n4, int n5, int n6) {
        for (int i = n3 - 1; i <= n6 + 1; ++i) {
            for (int j = n - 1; j <= n4 + 1; ++j) {
                for (int k = n2 - 1; k <= n5 + 1; ++k) {
                    this.setSectionDirty(j >> 4, k >> 4, i >> 4);
                }
            }
        }
    }

    public void setBlockDirty(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
        if (this.minecraft.getModelManager().requiresRender(blockState, blockState2)) {
            this.setBlocksDirty(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
    }

    public void setSectionDirtyWithNeighbors(int n, int n2, int n3) {
        for (int i = n3 - 1; i <= n3 + 1; ++i) {
            for (int j = n - 1; j <= n + 1; ++j) {
                for (int k = n2 - 1; k <= n2 + 1; ++k) {
                    this.setSectionDirty(j, k, i);
                }
            }
        }
    }

    public void setSectionDirty(int n, int n2, int n3) {
        this.setSectionDirty(n, n2, n3, false);
    }

    private void setSectionDirty(int n, int n2, int n3, boolean bl) {
        this.viewArea.setDirty(n, n2, n3, bl);
    }

    public void playStreamingMusic(@Nullable SoundEvent soundEvent, BlockPos blockPos) {
        SoundInstance soundInstance = this.playingRecords.get(blockPos);
        if (soundInstance != null) {
            this.minecraft.getSoundManager().stop(soundInstance);
            this.playingRecords.remove(blockPos);
        }
        if (soundEvent != null) {
            RecordItem recordItem = RecordItem.getBySound(soundEvent);
            if (recordItem != null) {
                this.minecraft.gui.setNowPlaying(recordItem.getDisplayName());
            }
            soundInstance = SimpleSoundInstance.forRecord(soundEvent, blockPos.getX(), blockPos.getY(), blockPos.getZ());
            this.playingRecords.put(blockPos, soundInstance);
            this.minecraft.getSoundManager().play(soundInstance);
        }
        this.notifyNearbyEntities(this.level, blockPos, soundEvent != null);
    }

    private void notifyNearbyEntities(Level level, BlockPos blockPos, boolean bl) {
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(blockPos).inflate(3.0));
        for (LivingEntity livingEntity : list) {
            livingEntity.setRecordPlayingNearby(blockPos, bl);
        }
    }

    public void addParticle(ParticleOptions particleOptions, boolean bl, double d, double d2, double d3, double d4, double d5, double d6) {
        this.addParticle(particleOptions, bl, false, d, d2, d3, d4, d5, d6);
    }

    public void addParticle(ParticleOptions particleOptions, boolean bl, boolean bl2, double d, double d2, double d3, double d4, double d5, double d6) {
        try {
            this.addParticleInternal(particleOptions, bl, bl2, d, d2, d3, d4, d5, d6);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Exception while adding particle");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Particle being added");
            crashReportCategory.setDetail("ID", Registry.PARTICLE_TYPE.getKey(particleOptions.getType()));
            crashReportCategory.setDetail("Parameters", particleOptions.writeToString());
            crashReportCategory.setDetail("Position", () -> CrashReportCategory.formatLocation(d, d2, d3));
            throw new ReportedException(crashReport);
        }
    }

    private <T extends ParticleOptions> void addParticle(T t, double d, double d2, double d3, double d4, double d5, double d6) {
        this.addParticle(t, t.getType().getOverrideLimiter(), d, d2, d3, d4, d5, d6);
    }

    @Nullable
    private Particle addParticleInternal(ParticleOptions particleOptions, boolean bl, double d, double d2, double d3, double d4, double d5, double d6) {
        return this.addParticleInternal(particleOptions, bl, false, d, d2, d3, d4, d5, d6);
    }

    @Nullable
    private Particle addParticleInternal(ParticleOptions particleOptions, boolean bl, boolean bl2, double d, double d2, double d3, double d4, double d5, double d6) {
        Camera camera = this.minecraft.gameRenderer.getMainCamera();
        if (this.minecraft == null || !camera.isInitialized() || this.minecraft.particleEngine == null) {
            return null;
        }
        ParticleStatus particleStatus = this.calculateParticleLevel(bl2);
        if (bl) {
            return this.minecraft.particleEngine.createParticle(particleOptions, d, d2, d3, d4, d5, d6);
        }
        if (camera.getPosition().distanceToSqr(d, d2, d3) > 1024.0) {
            return null;
        }
        if (particleStatus == ParticleStatus.MINIMAL) {
            return null;
        }
        return this.minecraft.particleEngine.createParticle(particleOptions, d, d2, d3, d4, d5, d6);
    }

    private ParticleStatus calculateParticleLevel(boolean bl) {
        ParticleStatus particleStatus = this.minecraft.options.particles;
        if (bl && particleStatus == ParticleStatus.MINIMAL && this.level.random.nextInt(10) == 0) {
            particleStatus = ParticleStatus.DECREASED;
        }
        if (particleStatus == ParticleStatus.DECREASED && this.level.random.nextInt(3) == 0) {
            particleStatus = ParticleStatus.MINIMAL;
        }
        return particleStatus;
    }

    public void clear() {
    }

    public void globalLevelEvent(int n, BlockPos blockPos, int n2) {
        switch (n) {
            case 1023: 
            case 1028: 
            case 1038: {
                Camera camera = this.minecraft.gameRenderer.getMainCamera();
                if (!camera.isInitialized()) break;
                double d = (double)blockPos.getX() - camera.getPosition().x;
                double d2 = (double)blockPos.getY() - camera.getPosition().y;
                double d3 = (double)blockPos.getZ() - camera.getPosition().z;
                double d4 = Math.sqrt(d * d + d2 * d2 + d3 * d3);
                double d5 = camera.getPosition().x;
                double d6 = camera.getPosition().y;
                double d7 = camera.getPosition().z;
                if (d4 > 0.0) {
                    d5 += d / d4 * 2.0;
                    d6 += d2 / d4 * 2.0;
                    d7 += d3 / d4 * 2.0;
                }
                if (n == 1023) {
                    this.level.playLocalSound(d5, d6, d7, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                if (n == 1038) {
                    this.level.playLocalSound(d5, d6, d7, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.0f, 1.0f, false);
                    break;
                }
                this.level.playLocalSound(d5, d6, d7, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 5.0f, 1.0f, false);
            }
        }
    }

    public void levelEvent(Player player, int n, BlockPos blockPos, int n2) {
        Random random = this.level.random;
        switch (n) {
            case 1035: {
                this.level.playLocalSound(blockPos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1033: {
                this.level.playLocalSound(blockPos, SoundEvents.CHORUS_FLOWER_GROW, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1034: {
                this.level.playLocalSound(blockPos, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1032: {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRAVEL, random.nextFloat() * 0.4f + 0.8f, 0.25f));
                break;
            }
            case 1001: {
                this.level.playLocalSound(blockPos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 1000: {
                this.level.playLocalSound(blockPos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                break;
            }
            case 1003: {
                this.level.playLocalSound(blockPos, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1004: {
                this.level.playLocalSound(blockPos, SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.NEUTRAL, 1.0f, 1.2f, false);
                break;
            }
            case 1002: {
                this.level.playLocalSound(blockPos, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0f, 1.2f, false);
                break;
            }
            case 2000: {
                Direction direction = Direction.from3DDataValue(n2);
                int n3 = direction.getStepX();
                int n4 = direction.getStepY();
                int n5 = direction.getStepZ();
                double d = (double)blockPos.getX() + (double)n3 * 0.6 + 0.5;
                double d2 = (double)blockPos.getY() + (double)n4 * 0.6 + 0.5;
                double d3 = (double)blockPos.getZ() + (double)n5 * 0.6 + 0.5;
                for (int i = 0; i < 10; ++i) {
                    double d4 = random.nextDouble() * 0.2 + 0.01;
                    double d5 = d + (double)n3 * 0.01 + (random.nextDouble() - 0.5) * (double)n5 * 0.5;
                    double d6 = d2 + (double)n4 * 0.01 + (random.nextDouble() - 0.5) * (double)n4 * 0.5;
                    double d7 = d3 + (double)n5 * 0.01 + (random.nextDouble() - 0.5) * (double)n3 * 0.5;
                    double d8 = (double)n3 * d4 + random.nextGaussian() * 0.01;
                    double d9 = (double)n4 * d4 + random.nextGaussian() * 0.01;
                    double d10 = (double)n5 * d4 + random.nextGaussian() * 0.01;
                    this.addParticle(ParticleTypes.SMOKE, d5, d6, d7, d8, d9, d10);
                }
                break;
            }
            case 2003: {
                double d = (double)blockPos.getX() + 0.5;
                double d11 = blockPos.getY();
                double d12 = (double)blockPos.getZ() + 0.5;
                for (int i = 0; i < 8; ++i) {
                    this.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), d, d11, d12, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15);
                }
                for (double d13 = 0.0; d13 < 6.283185307179586; d13 += 0.15707963267948966) {
                    this.addParticle(ParticleTypes.PORTAL, d + Math.cos(d13) * 5.0, d11 - 0.4, d12 + Math.sin(d13) * 5.0, Math.cos(d13) * -5.0, 0.0, Math.sin(d13) * -5.0);
                    this.addParticle(ParticleTypes.PORTAL, d + Math.cos(d13) * 5.0, d11 - 0.4, d12 + Math.sin(d13) * 5.0, Math.cos(d13) * -7.0, 0.0, Math.sin(d13) * -7.0);
                }
                break;
            }
            case 2002: 
            case 2007: {
                Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
                for (int i = 0; i < 8; ++i) {
                    this.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), vec3.x, vec3.y, vec3.z, random.nextGaussian() * 0.15, random.nextDouble() * 0.2, random.nextGaussian() * 0.15);
                }
                float f = (float)(n2 >> 16 & 0xFF) / 255.0f;
                float f2 = (float)(n2 >> 8 & 0xFF) / 255.0f;
                float f3 = (float)(n2 >> 0 & 0xFF) / 255.0f;
                SimpleParticleType simpleParticleType = n == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;
                for (int i = 0; i < 100; ++i) {
                    double d = random.nextDouble() * 4.0;
                    double d14 = random.nextDouble() * 3.141592653589793 * 2.0;
                    double d15 = Math.cos(d14) * d;
                    double d16 = 0.01 + random.nextDouble() * 0.5;
                    double d17 = Math.sin(d14) * d;
                    Particle particle = this.addParticleInternal(simpleParticleType, simpleParticleType.getType().getOverrideLimiter(), vec3.x + d15 * 0.1, vec3.y + 0.3, vec3.z + d17 * 0.1, d15, d16, d17);
                    if (particle == null) continue;
                    float f4 = 0.75f + random.nextFloat() * 0.25f;
                    particle.setColor(f * f4, f2 * f4, f3 * f4);
                    particle.setPower((float)d);
                }
                this.level.playLocalSound(blockPos, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2001: {
                BlockState blockState = Block.stateById(n2);
                if (!blockState.isAir()) {
                    SoundType soundType = blockState.getSoundType();
                    this.level.playLocalSound(blockPos, soundType.getBreakSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0f) / 2.0f, soundType.getPitch() * 0.8f, false);
                }
                this.minecraft.particleEngine.destroy(blockPos, blockState);
                break;
            }
            case 2004: {
                for (int i = 0; i < 20; ++i) {
                    double d = (double)blockPos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
                    double d18 = (double)blockPos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
                    double d19 = (double)blockPos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
                    this.level.addParticle(ParticleTypes.SMOKE, d, d18, d19, 0.0, 0.0, 0.0);
                    this.level.addParticle(ParticleTypes.FLAME, d, d18, d19, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2005: {
                BoneMealItem.addGrowthParticles(this.level, blockPos, n2);
                break;
            }
            case 2008: {
                this.level.addParticle(ParticleTypes.EXPLOSION, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 0.0, 0.0, 0.0);
                break;
            }
            case 1500: {
                ComposterBlock.handleFill(this.level, blockPos, n2 > 0);
                break;
            }
            case 1501: {
                this.level.playLocalSound(blockPos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
                for (int i = 0; i < 8; ++i) {
                    this.level.addParticle(ParticleTypes.LARGE_SMOKE, (double)blockPos.getX() + random.nextDouble(), (double)blockPos.getY() + 1.2, (double)blockPos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1502: {
                this.level.playLocalSound(blockPos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
                for (int i = 0; i < 5; ++i) {
                    double d = (double)blockPos.getX() + random.nextDouble() * 0.6 + 0.2;
                    double d20 = (double)blockPos.getY() + random.nextDouble() * 0.6 + 0.2;
                    double d21 = (double)blockPos.getZ() + random.nextDouble() * 0.6 + 0.2;
                    this.level.addParticle(ParticleTypes.SMOKE, d, d20, d21, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1503: {
                this.level.playLocalSound(blockPos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0f, 1.0f, false);
                for (int i = 0; i < 16; ++i) {
                    double d = (double)blockPos.getX() + (5.0 + random.nextDouble() * 6.0) / 16.0;
                    double d22 = (double)blockPos.getY() + 0.8125;
                    double d23 = (double)blockPos.getZ() + (5.0 + random.nextDouble() * 6.0) / 16.0;
                    this.level.addParticle(ParticleTypes.SMOKE, d, d22, d23, 0.0, 0.0, 0.0);
                }
                break;
            }
            case 2006: {
                for (int i = 0; i < 200; ++i) {
                    float f = random.nextFloat() * 4.0f;
                    float f5 = random.nextFloat() * 6.2831855f;
                    double d = Mth.cos(f5) * f;
                    double d24 = 0.01 + random.nextDouble() * 0.5;
                    double d25 = Mth.sin(f5) * f;
                    Particle particle = this.addParticleInternal(ParticleTypes.DRAGON_BREATH, false, (double)blockPos.getX() + d * 0.1, (double)blockPos.getY() + 0.3, (double)blockPos.getZ() + d25 * 0.1, d, d24, d25);
                    if (particle == null) continue;
                    particle.setPower(f);
                }
                if (n2 != 1) break;
                this.level.playLocalSound(blockPos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 2009: {
                for (int i = 0; i < 8; ++i) {
                    this.level.addParticle(ParticleTypes.CLOUD, (double)blockPos.getX() + random.nextDouble(), (double)blockPos.getY() + 1.2, (double)blockPos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
                }
                break;
            }
            case 1012: {
                this.level.playLocalSound(blockPos, SoundEvents.WOODEN_DOOR_CLOSE, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1036: {
                this.level.playLocalSound(blockPos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1013: {
                this.level.playLocalSound(blockPos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1014: {
                this.level.playLocalSound(blockPos, SoundEvents.FENCE_GATE_CLOSE, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1011: {
                this.level.playLocalSound(blockPos, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1006: {
                this.level.playLocalSound(blockPos, SoundEvents.WOODEN_DOOR_OPEN, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1007: {
                this.level.playLocalSound(blockPos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1037: {
                this.level.playLocalSound(blockPos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1008: {
                this.level.playLocalSound(blockPos, SoundEvents.FENCE_GATE_OPEN, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1005: {
                this.level.playLocalSound(blockPos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1009: {
                this.level.playLocalSound(blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
                break;
            }
            case 1029: {
                this.level.playLocalSound(blockPos, SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1030: {
                this.level.playLocalSound(blockPos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1044: {
                this.level.playLocalSound(blockPos, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1031: {
                this.level.playLocalSound(blockPos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1039: {
                this.level.playLocalSound(blockPos, SoundEvents.PHANTOM_BITE, SoundSource.HOSTILE, 0.3f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1010: {
                if (Item.byId(n2) instanceof RecordItem) {
                    this.playStreamingMusic(((RecordItem)Item.byId(n2)).getSound(), blockPos);
                    break;
                }
                this.playStreamingMusic(null, blockPos);
                break;
            }
            case 1015: {
                this.level.playLocalSound(blockPos, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1017: {
                this.level.playLocalSound(blockPos, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1016: {
                this.level.playLocalSound(blockPos, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1019: {
                this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1022: {
                this.level.playLocalSound(blockPos, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1021: {
                this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1020: {
                this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1018: {
                this.level.playLocalSound(blockPos, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1024: {
                this.level.playLocalSound(blockPos, SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1026: {
                this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1027: {
                this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1040: {
                this.level.playLocalSound(blockPos, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1041: {
                this.level.playLocalSound(blockPos, SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.NEUTRAL, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1025: {
                this.level.playLocalSound(blockPos, SoundEvents.BAT_TAKEOFF, SoundSource.NEUTRAL, 0.05f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f, false);
                break;
            }
            case 1042: {
                this.level.playLocalSound(blockPos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 1043: {
                this.level.playLocalSound(blockPos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0f, this.level.random.nextFloat() * 0.1f + 0.9f, false);
                break;
            }
            case 3000: {
                this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 0.0, 0.0, 0.0);
                this.level.playLocalSound(blockPos, SoundEvents.END_GATEWAY_SPAWN, SoundSource.BLOCKS, 10.0f, (1.0f + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2f) * 0.7f, false);
                break;
            }
            case 3001: {
                this.level.playLocalSound(blockPos, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 64.0f, 0.8f + this.level.random.nextFloat() * 0.3f, false);
            }
        }
    }

    public void destroyBlockProgress(int n, BlockPos blockPos, int n2) {
        if (n2 < 0 || n2 >= 10) {
            BlockDestructionProgress blockDestructionProgress = (BlockDestructionProgress)this.destroyingBlocks.remove(n);
            if (blockDestructionProgress != null) {
                this.removeProgress(blockDestructionProgress);
            }
        } else {
            BlockDestructionProgress blockDestructionProgress = (BlockDestructionProgress)this.destroyingBlocks.get(n);
            if (blockDestructionProgress != null) {
                this.removeProgress(blockDestructionProgress);
            }
            if (blockDestructionProgress == null || blockDestructionProgress.getPos().getX() != blockPos.getX() || blockDestructionProgress.getPos().getY() != blockPos.getY() || blockDestructionProgress.getPos().getZ() != blockPos.getZ()) {
                blockDestructionProgress = new BlockDestructionProgress(n, blockPos);
                this.destroyingBlocks.put(n, (Object)blockDestructionProgress);
            }
            blockDestructionProgress.setProgress(n2);
            blockDestructionProgress.updateTick(this.ticks);
            ((SortedSet)this.destructionProgress.computeIfAbsent(blockDestructionProgress.getPos().asLong(), l -> Sets.newTreeSet())).add(blockDestructionProgress);
        }
    }

    public boolean hasRenderedAllChunks() {
        return this.chunksToCompile.isEmpty() && this.chunkRenderDispatcher.isQueueEmpty();
    }

    public void needsUpdate() {
        this.needsUpdate = true;
        this.generateClouds = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateGlobalBlockEntities(Collection<BlockEntity> collection, Collection<BlockEntity> collection2) {
        Set<BlockEntity> set = this.globalBlockEntities;
        synchronized (set) {
            this.globalBlockEntities.removeAll(collection);
            this.globalBlockEntities.addAll(collection2);
        }
    }

    public static int getLightColor(BlockAndTintGetter blockAndTintGetter, BlockPos blockPos) {
        return LevelRenderer.getLightColor(blockAndTintGetter, blockAndTintGetter.getBlockState(blockPos), blockPos);
    }

    public static int getLightColor(BlockAndTintGetter blockAndTintGetter, BlockState blockState, BlockPos blockPos) {
        int n;
        if (blockState.emissiveRendering(blockAndTintGetter, blockPos)) {
            return 15728880;
        }
        int n2 = blockAndTintGetter.getBrightness(LightLayer.SKY, blockPos);
        int n3 = blockAndTintGetter.getBrightness(LightLayer.BLOCK, blockPos);
        if (n3 < (n = blockState.getLightEmission())) {
            n3 = n;
        }
        return n2 << 20 | n3 << 4;
    }

    @Nullable
    public RenderTarget entityTarget() {
        return this.entityTarget;
    }

    @Nullable
    public RenderTarget getTranslucentTarget() {
        return this.translucentTarget;
    }

    @Nullable
    public RenderTarget getItemEntityTarget() {
        return this.itemEntityTarget;
    }

    @Nullable
    public RenderTarget getParticlesTarget() {
        return this.particlesTarget;
    }

    @Nullable
    public RenderTarget getWeatherTarget() {
        return this.weatherTarget;
    }

    @Nullable
    public RenderTarget getCloudsTarget() {
        return this.cloudsTarget;
    }

    private static /* synthetic */ VertexConsumer lambda$renderLevel$1(MultiBufferSource.BufferSource bufferSource, VertexConsumer vertexConsumer, RenderType renderType) {
        VertexConsumer vertexConsumer2 = bufferSource.getBuffer(renderType);
        if (renderType.affectsCrumbling()) {
            return VertexMultiConsumer.create(vertexConsumer, vertexConsumer2);
        }
        return vertexConsumer2;
    }

    public static class TransparencyShaderException
    extends RuntimeException {
        public TransparencyShaderException(String string, Throwable throwable) {
            super(string, throwable);
        }
    }

    class RenderChunkInfo {
        private final ChunkRenderDispatcher.RenderChunk chunk;
        private final Direction sourceDirection;
        private byte directions;
        private final int step;

        private RenderChunkInfo(ChunkRenderDispatcher.RenderChunk renderChunk, @Nullable Direction direction, int n) {
            this.chunk = renderChunk;
            this.sourceDirection = direction;
            this.step = n;
        }

        public void setDirections(byte by, Direction direction) {
            this.directions = (byte)(this.directions | (by | 1 << direction.ordinal()));
        }

        public boolean hasDirection(Direction direction) {
            return (this.directions & 1 << direction.ordinal()) > 0;
        }
    }

}

