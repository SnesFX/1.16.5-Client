/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.longs.LongSets
 *  it.unimi.dsi.fastutil.longs.LongSets$EmptySet
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagContainer;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class DebugScreenOverlay
extends GuiComponent {
    private static final Map<Heightmap.Types, String> HEIGHTMAP_NAMES = Util.make(new EnumMap(Heightmap.Types.class), enumMap -> {
        enumMap.put(Heightmap.Types.WORLD_SURFACE_WG, "SW");
        enumMap.put(Heightmap.Types.WORLD_SURFACE, "S");
        enumMap.put(Heightmap.Types.OCEAN_FLOOR_WG, "OW");
        enumMap.put(Heightmap.Types.OCEAN_FLOOR, "O");
        enumMap.put(Heightmap.Types.MOTION_BLOCKING, "M");
        enumMap.put(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, "ML");
    });
    private final Minecraft minecraft;
    private final Font font;
    private HitResult block;
    private HitResult liquid;
    @Nullable
    private ChunkPos lastPos;
    @Nullable
    private LevelChunk clientChunk;
    @Nullable
    private CompletableFuture<LevelChunk> serverChunk;

    public DebugScreenOverlay(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.font = minecraft.font;
    }

    public void clearChunkCache() {
        this.serverChunk = null;
        this.clientChunk = null;
    }

    public void render(PoseStack poseStack) {
        this.minecraft.getProfiler().push("debug");
        RenderSystem.pushMatrix();
        Entity entity = this.minecraft.getCameraEntity();
        this.block = entity.pick(20.0, 0.0f, false);
        this.liquid = entity.pick(20.0, 0.0f, true);
        this.drawGameInformation(poseStack);
        this.drawSystemInformation(poseStack);
        RenderSystem.popMatrix();
        if (this.minecraft.options.renderFpsChart) {
            int n = this.minecraft.getWindow().getGuiScaledWidth();
            this.drawChart(poseStack, this.minecraft.getFrameTimer(), 0, n / 2, true);
            IntegratedServer integratedServer = this.minecraft.getSingleplayerServer();
            if (integratedServer != null) {
                this.drawChart(poseStack, integratedServer.getFrameTimer(), n - Math.min(n / 2, 240), n / 2, false);
            }
        }
        this.minecraft.getProfiler().pop();
    }

    protected void drawGameInformation(PoseStack poseStack) {
        List<String> list = this.getGameInformation();
        list.add("");
        boolean bl = this.minecraft.getSingleplayerServer() != null;
        list.add("Debug: Pie [shift]: " + (this.minecraft.options.renderDebugCharts ? "visible" : "hidden") + (bl ? " FPS + TPS" : " FPS") + " [alt]: " + (this.minecraft.options.renderFpsChart ? "visible" : "hidden"));
        list.add("For help: press F3 + Q");
        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);
            if (Strings.isNullOrEmpty((String)string)) continue;
            this.font.getClass();
            int n = 9;
            int n2 = this.font.width(string);
            int n3 = 2;
            int n4 = 2 + n * i;
            DebugScreenOverlay.fill(poseStack, 1, n4 - 1, 2 + n2 + 1, n4 + n - 1, -1873784752);
            this.font.draw(poseStack, string, 2.0f, (float)n4, 14737632);
        }
    }

    protected void drawSystemInformation(PoseStack poseStack) {
        List<String> list = this.getSystemInformation();
        for (int i = 0; i < list.size(); ++i) {
            String string = list.get(i);
            if (Strings.isNullOrEmpty((String)string)) continue;
            this.font.getClass();
            int n = 9;
            int n2 = this.font.width(string);
            int n3 = this.minecraft.getWindow().getGuiScaledWidth() - 2 - n2;
            int n4 = 2 + n * i;
            DebugScreenOverlay.fill(poseStack, n3 - 1, n4 - 1, n3 + n2 + 1, n4 + n - 1, -1873784752);
            this.font.draw(poseStack, string, (float)n3, (float)n4, 14737632);
        }
    }

    protected List<String> getGameInformation() {
        PostChain postChain;
        Level level;
        int n;
        BlockGetter blockGetter;
        String string;
        IntegratedServer integratedServer = this.minecraft.getSingleplayerServer();
        Connection connection = this.minecraft.getConnection().getConnection();
        float f = connection.getAverageSentPackets();
        float f2 = connection.getAverageReceivedPackets();
        String string2 = integratedServer != null ? String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", Float.valueOf(integratedServer.getAverageTickTime()), Float.valueOf(f), Float.valueOf(f2)) : String.format("\"%s\" server, %.0f tx, %.0f rx", this.minecraft.player.getServerBrand(), Float.valueOf(f), Float.valueOf(f2));
        BlockPos blockPos = this.minecraft.getCameraEntity().blockPosition();
        if (this.minecraft.showOnlyReducedInfo()) {
            return Lists.newArrayList((Object[])new String[]{"Minecraft " + SharedConstants.getCurrentVersion().getName() + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.minecraft.fpsString, string2, this.minecraft.levelRenderer.getChunkStatistics(), this.minecraft.levelRenderer.getEntityStatistics(), "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(), this.minecraft.level.gatherChunkSourceStats(), "", String.format("Chunk-relative: %d %d %d", blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF)});
        }
        Entity entity = this.minecraft.getCameraEntity();
        Direction direction = entity.getDirection();
        switch (direction) {
            case NORTH: {
                string = "Towards negative Z";
                break;
            }
            case SOUTH: {
                string = "Towards positive Z";
                break;
            }
            case WEST: {
                string = "Towards negative X";
                break;
            }
            case EAST: {
                string = "Towards positive X";
                break;
            }
            default: {
                string = "Invalid";
            }
        }
        ChunkPos chunkPos = new ChunkPos(blockPos);
        if (!Objects.equals(this.lastPos, chunkPos)) {
            this.lastPos = chunkPos;
            this.clearChunkCache();
        }
        LongSets.EmptySet emptySet = (level = this.getLevel()) instanceof ServerLevel ? ((ServerLevel)level).getForcedChunks() : LongSets.EMPTY_SET;
        ArrayList arrayList = Lists.newArrayList((Object[])new String[]{"Minecraft " + SharedConstants.getCurrentVersion().getName() + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType()) + ")", this.minecraft.fpsString, string2, this.minecraft.levelRenderer.getChunkStatistics(), this.minecraft.levelRenderer.getEntityStatistics(), "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(), this.minecraft.level.gatherChunkSourceStats()});
        String string3 = this.getServerChunkStats();
        if (string3 != null) {
            arrayList.add(string3);
        }
        arrayList.add(this.minecraft.level.dimension().location() + " FC: " + emptySet.size());
        arrayList.add("");
        arrayList.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.minecraft.getCameraEntity().getX(), this.minecraft.getCameraEntity().getY(), this.minecraft.getCameraEntity().getZ()));
        arrayList.add(String.format("Block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        arrayList.add(String.format("Chunk: %d %d %d in %d %d %d", blockPos.getX() & 0xF, blockPos.getY() & 0xF, blockPos.getZ() & 0xF, blockPos.getX() >> 4, blockPos.getY() >> 4, blockPos.getZ() >> 4));
        arrayList.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, string, Float.valueOf(Mth.wrapDegrees(entity.yRot)), Float.valueOf(Mth.wrapDegrees(entity.xRot))));
        if (this.minecraft.level != null) {
            if (this.minecraft.level.hasChunkAt(blockPos)) {
                blockGetter = this.getClientChunk();
                if (((LevelChunk)blockGetter).isEmpty()) {
                    arrayList.add("Waiting for chunk...");
                } else {
                    Object object;
                    int n2 = this.minecraft.level.getChunkSource().getLightEngine().getRawBrightness(blockPos, 0);
                    int n3 = this.minecraft.level.getBrightness(LightLayer.SKY, blockPos);
                    n = this.minecraft.level.getBrightness(LightLayer.BLOCK, blockPos);
                    arrayList.add("Client Light: " + n2 + " (" + n3 + " sky, " + n + " block)");
                    LevelChunk levelChunk = this.getServerChunk();
                    if (levelChunk != null) {
                        object = level.getChunkSource().getLightEngine();
                        arrayList.add("Server Light: (" + ((LevelLightEngine)object).getLayerListener(LightLayer.SKY).getLightValue(blockPos) + " sky, " + ((LevelLightEngine)object).getLayerListener(LightLayer.BLOCK).getLightValue(blockPos) + " block)");
                    } else {
                        arrayList.add("Server Light: (?? sky, ?? block)");
                    }
                    object = new StringBuilder("CH");
                    for (Heightmap.Types object2 : Heightmap.Types.values()) {
                        if (!object2.sendToClient()) continue;
                        ((StringBuilder)object).append(" ").append(HEIGHTMAP_NAMES.get(object2)).append(": ").append(((LevelChunk)blockGetter).getHeight(object2, blockPos.getX(), blockPos.getZ()));
                    }
                    arrayList.add(((StringBuilder)object).toString());
                    ((StringBuilder)object).setLength(0);
                    ((StringBuilder)object).append("SH");
                    for (Heightmap.Types types : Heightmap.Types.values()) {
                        if (!types.keepAfterWorldgen()) continue;
                        ((StringBuilder)object).append(" ").append(HEIGHTMAP_NAMES.get(types)).append(": ");
                        if (levelChunk != null) {
                            ((StringBuilder)object).append(levelChunk.getHeight(types, blockPos.getX(), blockPos.getZ()));
                            continue;
                        }
                        ((StringBuilder)object).append("??");
                    }
                    arrayList.add(((StringBuilder)object).toString());
                    if (blockPos.getY() >= 0 && blockPos.getY() < 256) {
                        arrayList.add("Biome: " + this.minecraft.level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(this.minecraft.level.getBiome(blockPos)));
                        long l = 0L;
                        float f3 = 0.0f;
                        if (levelChunk != null) {
                            f3 = level.getMoonBrightness();
                            l = levelChunk.getInhabitedTime();
                        }
                        DifficultyInstance difficultyInstance = new DifficultyInstance(level.getDifficulty(), level.getDayTime(), l, f3);
                        arrayList.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", Float.valueOf(difficultyInstance.getEffectiveDifficulty()), Float.valueOf(difficultyInstance.getSpecialMultiplier()), this.minecraft.level.getDayTime() / 24000L));
                    }
                }
            } else {
                arrayList.add("Outside of world...");
            }
        } else {
            arrayList.add("Outside of world...");
        }
        blockGetter = this.getServerLevel();
        if (blockGetter != null) {
            NaturalSpawner.SpawnState spawnState = ((ServerLevel)blockGetter).getChunkSource().getLastSpawnState();
            if (spawnState != null) {
                Object2IntMap<MobCategory> object2IntMap = spawnState.getMobCategoryCounts();
                n = spawnState.getSpawnableChunkCount();
                arrayList.add("SC: " + n + ", " + Stream.of(MobCategory.values()).map(mobCategory -> Character.toUpperCase(mobCategory.getName().charAt(0)) + ": " + object2IntMap.getInt(mobCategory)).collect(Collectors.joining(", ")));
            } else {
                arrayList.add("SC: N/A");
            }
        }
        if ((postChain = this.minecraft.gameRenderer.currentEffect()) != null) {
            arrayList.add("Shader: " + postChain.getName());
        }
        arrayList.add(this.minecraft.getSoundManager().getDebugString() + String.format(" (Mood %d%%)", Math.round(this.minecraft.player.getCurrentMood() * 100.0f)));
        return arrayList;
    }

    @Nullable
    private ServerLevel getServerLevel() {
        IntegratedServer integratedServer = this.minecraft.getSingleplayerServer();
        if (integratedServer != null) {
            return integratedServer.getLevel(this.minecraft.level.dimension());
        }
        return null;
    }

    @Nullable
    private String getServerChunkStats() {
        ServerLevel serverLevel = this.getServerLevel();
        if (serverLevel != null) {
            return serverLevel.gatherChunkSourceStats();
        }
        return null;
    }

    private Level getLevel() {
        return (Level)DataFixUtils.orElse(Optional.ofNullable(this.minecraft.getSingleplayerServer()).flatMap(integratedServer -> Optional.ofNullable(integratedServer.getLevel(this.minecraft.level.dimension()))), (Object)this.minecraft.level);
    }

    @Nullable
    private LevelChunk getServerChunk() {
        if (this.serverChunk == null) {
            ServerLevel serverLevel = this.getServerLevel();
            if (serverLevel != null) {
                this.serverChunk = serverLevel.getChunkSource().getChunkFuture(this.lastPos.x, this.lastPos.z, ChunkStatus.FULL, false).thenApply(either -> (LevelChunk)either.map(chunkAccess -> (LevelChunk)chunkAccess, chunkLoadingFailure -> null));
            }
            if (this.serverChunk == null) {
                this.serverChunk = CompletableFuture.completedFuture(this.getClientChunk());
            }
        }
        return this.serverChunk.getNow(null);
    }

    private LevelChunk getClientChunk() {
        if (this.clientChunk == null) {
            this.clientChunk = this.minecraft.level.getChunk(this.lastPos.x, this.lastPos.z);
        }
        return this.clientChunk;
    }

    protected List<String> getSystemInformation() {
        Object object;
        StateHolder stateHolder;
        long l = Runtime.getRuntime().maxMemory();
        long l2 = Runtime.getRuntime().totalMemory();
        long l3 = Runtime.getRuntime().freeMemory();
        long l4 = l2 - l3;
        ArrayList arrayList = Lists.newArrayList((Object[])new String[]{String.format("Java: %s %dbit", System.getProperty("java.version"), this.minecraft.is64Bit() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", l4 * 100L / l, DebugScreenOverlay.bytesToMegabytes(l4), DebugScreenOverlay.bytesToMegabytes(l)), String.format("Allocated: % 2d%% %03dMB", l2 * 100L / l, DebugScreenOverlay.bytesToMegabytes(l2)), "", String.format("CPU: %s", GlUtil.getCpuInfo()), "", String.format("Display: %dx%d (%s)", Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), GlUtil.getVendor()), GlUtil.getRenderer(), GlUtil.getOpenGLVersion()});
        if (this.minecraft.showOnlyReducedInfo()) {
            return arrayList;
        }
        if (this.block.getType() == HitResult.Type.BLOCK) {
            object = ((BlockHitResult)this.block).getBlockPos();
            stateHolder = this.minecraft.level.getBlockState((BlockPos)object);
            arrayList.add("");
            arrayList.add((Object)((Object)ChatFormatting.UNDERLINE) + "Targeted Block: " + ((Vec3i)object).getX() + ", " + ((Vec3i)object).getY() + ", " + ((Vec3i)object).getZ());
            arrayList.add(String.valueOf(Registry.BLOCK.getKey(((BlockBehaviour.BlockStateBase)stateHolder).getBlock())));
            for (Map.Entry object2 : stateHolder.getValues().entrySet()) {
                arrayList.add(this.getPropertyValueString(object2));
            }
            for (ResourceLocation resourceLocation : this.minecraft.getConnection().getTags().getBlocks().getMatchingTags(((BlockBehaviour.BlockStateBase)stateHolder).getBlock())) {
                arrayList.add("#" + resourceLocation);
            }
        }
        if (this.liquid.getType() == HitResult.Type.BLOCK) {
            object = ((BlockHitResult)this.liquid).getBlockPos();
            stateHolder = this.minecraft.level.getFluidState((BlockPos)object);
            arrayList.add("");
            arrayList.add((Object)((Object)ChatFormatting.UNDERLINE) + "Targeted Fluid: " + ((Vec3i)object).getX() + ", " + ((Vec3i)object).getY() + ", " + ((Vec3i)object).getZ());
            arrayList.add(String.valueOf(Registry.FLUID.getKey(((FluidState)stateHolder).getType())));
            for (Map.Entry entry : stateHolder.getValues().entrySet()) {
                arrayList.add(this.getPropertyValueString(entry));
            }
            for (ResourceLocation resourceLocation : this.minecraft.getConnection().getTags().getFluids().getMatchingTags(((FluidState)stateHolder).getType())) {
                arrayList.add("#" + resourceLocation);
            }
        }
        if ((object = this.minecraft.crosshairPickEntity) != null) {
            arrayList.add("");
            arrayList.add((Object)((Object)ChatFormatting.UNDERLINE) + "Targeted Entity");
            arrayList.add(String.valueOf(Registry.ENTITY_TYPE.getKey(((Entity)object).getType())));
        }
        return arrayList;
    }

    private String getPropertyValueString(Map.Entry<Property<?>, Comparable<?>> entry) {
        Property<?> property = entry.getKey();
        Comparable<?> comparable = entry.getValue();
        String string = Util.getPropertyName(property, comparable);
        if (Boolean.TRUE.equals(comparable)) {
            string = (Object)((Object)ChatFormatting.GREEN) + string;
        } else if (Boolean.FALSE.equals(comparable)) {
            string = (Object)((Object)ChatFormatting.RED) + string;
        }
        return property.getName() + ": " + string;
    }

    private void drawChart(PoseStack poseStack, FrameTimer frameTimer, int n, int n2, boolean bl) {
        int n3;
        RenderSystem.disableDepthTest();
        int n4 = frameTimer.getLogStart();
        int n5 = frameTimer.getLogEnd();
        long[] arrl = frameTimer.getLog();
        int n6 = n4;
        int n7 = n;
        int n8 = Math.max(0, arrl.length - n2);
        int n9 = arrl.length - n8;
        n6 = frameTimer.wrapIndex(n6 + n8);
        long l = 0L;
        int n10 = Integer.MAX_VALUE;
        int n11 = Integer.MIN_VALUE;
        for (n3 = 0; n3 < n9; ++n3) {
            int n12 = (int)(arrl[frameTimer.wrapIndex(n6 + n3)] / 1000000L);
            n10 = Math.min(n10, n12);
            n11 = Math.max(n11, n12);
            l += (long)n12;
        }
        n3 = this.minecraft.getWindow().getGuiScaledHeight();
        DebugScreenOverlay.fill(poseStack, n, n3 - 60, n + n9, n3, -1873784752);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix4f = Transformation.identity().getMatrix();
        while (n6 != n5) {
            int n13 = frameTimer.scaleSampleTo(arrl[n6], bl ? 30 : 60, bl ? 60 : 20);
            int n14 = bl ? 100 : 60;
            int n15 = this.getSampleColor(Mth.clamp(n13, 0, n14), 0, n14 / 2, n14);
            int n16 = n15 >> 24 & 0xFF;
            int n17 = n15 >> 16 & 0xFF;
            int n18 = n15 >> 8 & 0xFF;
            int n19 = n15 & 0xFF;
            bufferBuilder.vertex(matrix4f, n7 + 1, n3, 0.0f).color(n17, n18, n19, n16).endVertex();
            bufferBuilder.vertex(matrix4f, n7 + 1, n3 - n13 + 1, 0.0f).color(n17, n18, n19, n16).endVertex();
            bufferBuilder.vertex(matrix4f, n7, n3 - n13 + 1, 0.0f).color(n17, n18, n19, n16).endVertex();
            bufferBuilder.vertex(matrix4f, n7, n3, 0.0f).color(n17, n18, n19, n16).endVertex();
            ++n7;
            n6 = frameTimer.wrapIndex(n6 + 1);
        }
        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        if (bl) {
            DebugScreenOverlay.fill(poseStack, n + 1, n3 - 30 + 1, n + 14, n3 - 30 + 10, -1873784752);
            this.font.draw(poseStack, "60 FPS", (float)(n + 2), (float)(n3 - 30 + 2), 14737632);
            this.hLine(poseStack, n, n + n9 - 1, n3 - 30, -1);
            DebugScreenOverlay.fill(poseStack, n + 1, n3 - 60 + 1, n + 14, n3 - 60 + 10, -1873784752);
            this.font.draw(poseStack, "30 FPS", (float)(n + 2), (float)(n3 - 60 + 2), 14737632);
            this.hLine(poseStack, n, n + n9 - 1, n3 - 60, -1);
        } else {
            DebugScreenOverlay.fill(poseStack, n + 1, n3 - 60 + 1, n + 14, n3 - 60 + 10, -1873784752);
            this.font.draw(poseStack, "20 TPS", (float)(n + 2), (float)(n3 - 60 + 2), 14737632);
            this.hLine(poseStack, n, n + n9 - 1, n3 - 60, -1);
        }
        this.hLine(poseStack, n, n + n9 - 1, n3 - 1, -1);
        this.vLine(poseStack, n, n3 - 60, n3, -1);
        this.vLine(poseStack, n + n9 - 1, n3 - 60, n3, -1);
        if (bl && this.minecraft.options.framerateLimit > 0 && this.minecraft.options.framerateLimit <= 250) {
            this.hLine(poseStack, n, n + n9 - 1, n3 - 1 - (int)(1800.0 / (double)this.minecraft.options.framerateLimit), -16711681);
        }
        String string = n10 + " ms min";
        String string2 = l / (long)n9 + " ms avg";
        String string3 = n11 + " ms max";
        this.font.getClass();
        this.font.drawShadow(poseStack, string, (float)(n + 2), (float)(n3 - 60 - 9), 14737632);
        this.font.getClass();
        this.font.drawShadow(poseStack, string2, (float)(n + n9 / 2 - this.font.width(string2) / 2), (float)(n3 - 60 - 9), 14737632);
        this.font.getClass();
        this.font.drawShadow(poseStack, string3, (float)(n + n9 - this.font.width(string3)), (float)(n3 - 60 - 9), 14737632);
        RenderSystem.enableDepthTest();
    }

    private int getSampleColor(int n, int n2, int n3, int n4) {
        if (n < n3) {
            return this.colorLerp(-16711936, -256, (float)n / (float)n3);
        }
        return this.colorLerp(-256, -65536, (float)(n - n3) / (float)(n4 - n3));
    }

    private int colorLerp(int n, int n2, float f) {
        int n3 = n >> 24 & 0xFF;
        int n4 = n >> 16 & 0xFF;
        int n5 = n >> 8 & 0xFF;
        int n6 = n & 0xFF;
        int n7 = n2 >> 24 & 0xFF;
        int n8 = n2 >> 16 & 0xFF;
        int n9 = n2 >> 8 & 0xFF;
        int n10 = n2 & 0xFF;
        int n11 = Mth.clamp((int)Mth.lerp(f, n3, n7), 0, 255);
        int n12 = Mth.clamp((int)Mth.lerp(f, n4, n8), 0, 255);
        int n13 = Mth.clamp((int)Mth.lerp(f, n5, n9), 0, 255);
        int n14 = Mth.clamp((int)Mth.lerp(f, n6, n10), 0, 255);
        return n11 << 24 | n12 << 16 | n13 << 8 | n14;
    }

    private static long bytesToMegabytes(long l) {
        return l / 1024L / 1024L;
    }

}

