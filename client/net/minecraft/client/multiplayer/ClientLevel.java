/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.ObjectCollection
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagContainer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.EmptyTickList;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;
import org.apache.logging.log4j.Logger;

public class ClientLevel
extends Level {
    private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectOpenHashMap();
    private final ClientPacketListener connection;
    private final LevelRenderer levelRenderer;
    private final ClientLevelData clientLevelData;
    private final DimensionSpecialEffects effects;
    private final Minecraft minecraft = Minecraft.getInstance();
    private final List<AbstractClientPlayer> players = Lists.newArrayList();
    private Scoreboard scoreboard = new Scoreboard();
    private final Map<String, MapItemSavedData> mapData = Maps.newHashMap();
    private int skyFlashTime;
    private final Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches = Util.make(new Object2ObjectArrayMap(3), object2ObjectArrayMap -> {
        object2ObjectArrayMap.put((Object)BiomeColors.GRASS_COLOR_RESOLVER, (Object)new BlockTintCache());
        object2ObjectArrayMap.put((Object)BiomeColors.FOLIAGE_COLOR_RESOLVER, (Object)new BlockTintCache());
        object2ObjectArrayMap.put((Object)BiomeColors.WATER_COLOR_RESOLVER, (Object)new BlockTintCache());
    });
    private final ClientChunkCache chunkSource;

    public ClientLevel(ClientPacketListener clientPacketListener, ClientLevelData clientLevelData, ResourceKey<Level> resourceKey, DimensionType dimensionType, int n, Supplier<ProfilerFiller> supplier, LevelRenderer levelRenderer, boolean bl, long l) {
        super(clientLevelData, resourceKey, dimensionType, supplier, true, bl, l);
        this.connection = clientPacketListener;
        this.chunkSource = new ClientChunkCache(this, n);
        this.clientLevelData = clientLevelData;
        this.levelRenderer = levelRenderer;
        this.effects = DimensionSpecialEffects.forType(dimensionType);
        this.setDefaultSpawnPos(new BlockPos(8, 64, 8), 0.0f);
        this.updateSkyBrightness();
        this.prepareWeather();
    }

    public DimensionSpecialEffects effects() {
        return this.effects;
    }

    public void tick(BooleanSupplier booleanSupplier) {
        this.getWorldBorder().tick();
        this.tickTime();
        this.getProfiler().push("blocks");
        this.chunkSource.tick(booleanSupplier);
        this.getProfiler().pop();
    }

    private void tickTime() {
        this.setGameTime(this.levelData.getGameTime() + 1L);
        if (this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            this.setDayTime(this.levelData.getDayTime() + 1L);
        }
    }

    public void setGameTime(long l) {
        this.clientLevelData.setGameTime(l);
    }

    public void setDayTime(long l) {
        if (l < 0L) {
            l = -l;
            this.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, null);
        } else {
            this.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(true, null);
        }
        this.clientLevelData.setDayTime(l);
    }

    public Iterable<Entity> entitiesForRendering() {
        return this.entitiesById.values();
    }

    public void tickEntities() {
        ProfilerFiller profilerFiller = this.getProfiler();
        profilerFiller.push("entities");
        ObjectIterator objectIterator = this.entitiesById.int2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)objectIterator.next();
            Entity entity = (Entity)entry.getValue();
            if (entity.isPassenger()) continue;
            profilerFiller.push("tick");
            if (!entity.removed) {
                this.guardEntityTick(this::tickNonPassenger, entity);
            }
            profilerFiller.pop();
            profilerFiller.push("remove");
            if (entity.removed) {
                objectIterator.remove();
                this.onEntityRemoved(entity);
            }
            profilerFiller.pop();
        }
        this.tickBlockEntities();
        profilerFiller.pop();
    }

    public void tickNonPassenger(Entity entity) {
        if (!(entity instanceof Player) && !this.getChunkSource().isEntityTickingChunk(entity)) {
            this.updateChunkPos(entity);
            return;
        }
        entity.setPosAndOldPos(entity.getX(), entity.getY(), entity.getZ());
        entity.yRotO = entity.yRot;
        entity.xRotO = entity.xRot;
        if (entity.inChunk || entity.isSpectator()) {
            ++entity.tickCount;
            this.getProfiler().push(() -> Registry.ENTITY_TYPE.getKey(entity.getType()).toString());
            entity.tick();
            this.getProfiler().pop();
        }
        this.updateChunkPos(entity);
        if (entity.inChunk) {
            for (Entity entity2 : entity.getPassengers()) {
                this.tickPassenger(entity, entity2);
            }
        }
    }

    public void tickPassenger(Entity entity, Entity entity2) {
        if (entity2.removed || entity2.getVehicle() != entity) {
            entity2.stopRiding();
            return;
        }
        if (!(entity2 instanceof Player) && !this.getChunkSource().isEntityTickingChunk(entity2)) {
            return;
        }
        entity2.setPosAndOldPos(entity2.getX(), entity2.getY(), entity2.getZ());
        entity2.yRotO = entity2.yRot;
        entity2.xRotO = entity2.xRot;
        if (entity2.inChunk) {
            ++entity2.tickCount;
            entity2.rideTick();
        }
        this.updateChunkPos(entity2);
        if (entity2.inChunk) {
            for (Entity entity3 : entity2.getPassengers()) {
                this.tickPassenger(entity2, entity3);
            }
        }
    }

    private void updateChunkPos(Entity entity) {
        if (!entity.checkAndResetUpdateChunkPos()) {
            return;
        }
        this.getProfiler().push("chunkCheck");
        int n = Mth.floor(entity.getX() / 16.0);
        int n2 = Mth.floor(entity.getY() / 16.0);
        int n3 = Mth.floor(entity.getZ() / 16.0);
        if (!entity.inChunk || entity.xChunk != n || entity.yChunk != n2 || entity.zChunk != n3) {
            if (entity.inChunk && this.hasChunk(entity.xChunk, entity.zChunk)) {
                this.getChunk(entity.xChunk, entity.zChunk).removeEntity(entity, entity.yChunk);
            }
            if (entity.checkAndResetForcedChunkAdditionFlag() || this.hasChunk(n, n3)) {
                this.getChunk(n, n3).addEntity(entity);
            } else {
                if (entity.inChunk) {
                    LOGGER.warn("Entity {} left loaded chunk area", (Object)entity);
                }
                entity.inChunk = false;
            }
        }
        this.getProfiler().pop();
    }

    public void unload(LevelChunk levelChunk) {
        this.blockEntitiesToUnload.addAll(levelChunk.getBlockEntities().values());
        this.chunkSource.getLightEngine().enableLightSources(levelChunk.getPos(), false);
    }

    public void onChunkLoaded(int n, int n2) {
        this.tintCaches.forEach((colorResolver, blockTintCache) -> blockTintCache.invalidateForChunk(n, n2));
    }

    public void clearTintCaches() {
        this.tintCaches.forEach((colorResolver, blockTintCache) -> blockTintCache.invalidateAll());
    }

    @Override
    public boolean hasChunk(int n, int n2) {
        return true;
    }

    public int getEntityCount() {
        return this.entitiesById.size();
    }

    public void addPlayer(int n, AbstractClientPlayer abstractClientPlayer) {
        this.addEntity(n, abstractClientPlayer);
        this.players.add(abstractClientPlayer);
    }

    public void putNonPlayerEntity(int n, Entity entity) {
        this.addEntity(n, entity);
    }

    private void addEntity(int n, Entity entity) {
        this.removeEntity(n);
        this.entitiesById.put(n, (Object)entity);
        this.getChunkSource().getChunk(Mth.floor(entity.getX() / 16.0), Mth.floor(entity.getZ() / 16.0), ChunkStatus.FULL, true).addEntity(entity);
    }

    public void removeEntity(int n) {
        Entity entity = (Entity)this.entitiesById.remove(n);
        if (entity != null) {
            entity.remove();
            this.onEntityRemoved(entity);
        }
    }

    private void onEntityRemoved(Entity entity) {
        entity.unRide();
        if (entity.inChunk) {
            this.getChunk(entity.xChunk, entity.zChunk).removeEntity(entity);
        }
        this.players.remove(entity);
    }

    public void reAddEntitiesToChunk(LevelChunk levelChunk) {
        for (Int2ObjectMap.Entry entry : this.entitiesById.int2ObjectEntrySet()) {
            Entity entity = (Entity)entry.getValue();
            int n = Mth.floor(entity.getX() / 16.0);
            int n2 = Mth.floor(entity.getZ() / 16.0);
            if (n != levelChunk.getPos().x || n2 != levelChunk.getPos().z) continue;
            levelChunk.addEntity(entity);
        }
    }

    @Nullable
    @Override
    public Entity getEntity(int n) {
        return (Entity)this.entitiesById.get(n);
    }

    public void setKnownState(BlockPos blockPos, BlockState blockState) {
        this.setBlock(blockPos, blockState, 19);
    }

    @Override
    public void disconnect() {
        this.connection.getConnection().disconnect(new TranslatableComponent("multiplayer.status.quitting"));
    }

    public void animateTick(int n, int n2, int n3) {
        int n4 = 32;
        Random random = new Random();
        boolean bl = false;
        if (this.minecraft.gameMode.getPlayerMode() == GameType.CREATIVE) {
            for (ItemStack itemStack : this.minecraft.player.getHandSlots()) {
                if (itemStack.getItem() != Blocks.BARRIER.asItem()) continue;
                bl = true;
                break;
            }
        }
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 667; ++i) {
            this.doAnimateTick(n, n2, n3, 16, random, bl, mutableBlockPos);
            this.doAnimateTick(n, n2, n3, 32, random, bl, mutableBlockPos);
        }
    }

    public void doAnimateTick(int n, int n2, int n3, int n4, Random random, boolean bl, BlockPos.MutableBlockPos mutableBlockPos) {
        int n5 = n + this.random.nextInt(n4) - this.random.nextInt(n4);
        int n6 = n2 + this.random.nextInt(n4) - this.random.nextInt(n4);
        int n7 = n3 + this.random.nextInt(n4) - this.random.nextInt(n4);
        mutableBlockPos.set(n5, n6, n7);
        BlockState blockState = this.getBlockState(mutableBlockPos);
        blockState.getBlock().animateTick(blockState, this, mutableBlockPos, random);
        FluidState fluidState = this.getFluidState(mutableBlockPos);
        if (!fluidState.isEmpty()) {
            fluidState.animateTick(this, mutableBlockPos, random);
            ParticleOptions particleOptions = fluidState.getDripParticle();
            if (particleOptions != null && this.random.nextInt(10) == 0) {
                boolean bl2 = blockState.isFaceSturdy(this, mutableBlockPos, Direction.DOWN);
                Vec3i vec3i = mutableBlockPos.below();
                this.trySpawnDripParticles((BlockPos)vec3i, this.getBlockState((BlockPos)vec3i), particleOptions, bl2);
            }
        }
        if (bl && blockState.is(Blocks.BARRIER)) {
            this.addParticle(ParticleTypes.BARRIER, (double)n5 + 0.5, (double)n6 + 0.5, (double)n7 + 0.5, 0.0, 0.0, 0.0);
        }
        if (!blockState.isCollisionShapeFullBlock(this, mutableBlockPos)) {
            this.getBiome(mutableBlockPos).getAmbientParticle().ifPresent(ambientParticleSettings -> {
                if (ambientParticleSettings.canSpawn(this.random)) {
                    this.addParticle(ambientParticleSettings.getOptions(), (double)mutableBlockPos.getX() + this.random.nextDouble(), (double)mutableBlockPos.getY() + this.random.nextDouble(), (double)mutableBlockPos.getZ() + this.random.nextDouble(), 0.0, 0.0, 0.0);
                }
            });
        }
    }

    private void trySpawnDripParticles(BlockPos blockPos, BlockState blockState, ParticleOptions particleOptions, boolean bl) {
        if (!blockState.getFluidState().isEmpty()) {
            return;
        }
        VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos);
        double d = voxelShape.max(Direction.Axis.Y);
        if (d < 1.0) {
            if (bl) {
                this.spawnFluidParticle(blockPos.getX(), blockPos.getX() + 1, blockPos.getZ(), blockPos.getZ() + 1, (double)(blockPos.getY() + 1) - 0.05, particleOptions);
            }
        } else if (!blockState.is(BlockTags.IMPERMEABLE)) {
            double d2 = voxelShape.min(Direction.Axis.Y);
            if (d2 > 0.0) {
                this.spawnParticle(blockPos, particleOptions, voxelShape, (double)blockPos.getY() + d2 - 0.05);
            } else {
                BlockPos blockPos2 = blockPos.below();
                BlockState blockState2 = this.getBlockState(blockPos2);
                VoxelShape voxelShape2 = blockState2.getCollisionShape(this, blockPos2);
                double d3 = voxelShape2.max(Direction.Axis.Y);
                if (d3 < 1.0 && blockState2.getFluidState().isEmpty()) {
                    this.spawnParticle(blockPos, particleOptions, voxelShape, (double)blockPos.getY() - 0.05);
                }
            }
        }
    }

    private void spawnParticle(BlockPos blockPos, ParticleOptions particleOptions, VoxelShape voxelShape, double d) {
        this.spawnFluidParticle((double)blockPos.getX() + voxelShape.min(Direction.Axis.X), (double)blockPos.getX() + voxelShape.max(Direction.Axis.X), (double)blockPos.getZ() + voxelShape.min(Direction.Axis.Z), (double)blockPos.getZ() + voxelShape.max(Direction.Axis.Z), d, particleOptions);
    }

    private void spawnFluidParticle(double d, double d2, double d3, double d4, double d5, ParticleOptions particleOptions) {
        this.addParticle(particleOptions, Mth.lerp(this.random.nextDouble(), d, d2), d5, Mth.lerp(this.random.nextDouble(), d3, d4), 0.0, 0.0, 0.0);
    }

    public void removeAllPendingEntityRemovals() {
        ObjectIterator objectIterator = this.entitiesById.int2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)objectIterator.next();
            Entity entity = (Entity)entry.getValue();
            if (!entity.removed) continue;
            objectIterator.remove();
            this.onEntityRemoved(entity);
        }
    }

    @Override
    public CrashReportCategory fillReportDetails(CrashReport crashReport) {
        CrashReportCategory crashReportCategory = super.fillReportDetails(crashReport);
        crashReportCategory.setDetail("Server brand", () -> this.minecraft.player.getServerBrand());
        crashReportCategory.setDetail("Server type", () -> this.minecraft.getSingleplayerServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server");
        return crashReportCategory;
    }

    @Override
    public void playSound(@Nullable Player player, double d, double d2, double d3, SoundEvent soundEvent, SoundSource soundSource, float f, float f2) {
        if (player == this.minecraft.player) {
            this.playLocalSound(d, d2, d3, soundEvent, soundSource, f, f2, false);
        }
    }

    @Override
    public void playSound(@Nullable Player player, Entity entity, SoundEvent soundEvent, SoundSource soundSource, float f, float f2) {
        if (player == this.minecraft.player) {
            this.minecraft.getSoundManager().play(new EntityBoundSoundInstance(soundEvent, soundSource, entity));
        }
    }

    public void playLocalSound(BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float f, float f2, boolean bl) {
        this.playLocalSound((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, soundEvent, soundSource, f, f2, bl);
    }

    @Override
    public void playLocalSound(double d, double d2, double d3, SoundEvent soundEvent, SoundSource soundSource, float f, float f2, boolean bl) {
        double d4 = this.minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(d, d2, d3);
        SimpleSoundInstance simpleSoundInstance = new SimpleSoundInstance(soundEvent, soundSource, f, f2, d, d2, d3);
        if (bl && d4 > 100.0) {
            double d5 = Math.sqrt(d4) / 40.0;
            this.minecraft.getSoundManager().playDelayed(simpleSoundInstance, (int)(d5 * 20.0));
        } else {
            this.minecraft.getSoundManager().play(simpleSoundInstance);
        }
    }

    @Override
    public void createFireworks(double d, double d2, double d3, double d4, double d5, double d6, @Nullable CompoundTag compoundTag) {
        this.minecraft.particleEngine.add(new FireworkParticles.Starter(this, d, d2, d3, d4, d5, d6, this.minecraft.particleEngine, compoundTag));
    }

    @Override
    public void sendPacketToServer(Packet<?> packet) {
        this.connection.send(packet);
    }

    @Override
    public RecipeManager getRecipeManager() {
        return this.connection.getRecipeManager();
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public TickList<Block> getBlockTicks() {
        return EmptyTickList.empty();
    }

    @Override
    public TickList<Fluid> getLiquidTicks() {
        return EmptyTickList.empty();
    }

    @Override
    public ClientChunkCache getChunkSource() {
        return this.chunkSource;
    }

    @Nullable
    @Override
    public MapItemSavedData getMapData(String string) {
        return this.mapData.get(string);
    }

    @Override
    public void setMapData(MapItemSavedData mapItemSavedData) {
        this.mapData.put(mapItemSavedData.getId(), mapItemSavedData);
    }

    @Override
    public int getFreeMapId() {
        return 0;
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    @Override
    public TagContainer getTagManager() {
        return this.connection.getTags();
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.connection.registryAccess();
    }

    @Override
    public void sendBlockUpdated(BlockPos blockPos, BlockState blockState, BlockState blockState2, int n) {
        this.levelRenderer.blockChanged(this, blockPos, blockState, blockState2, n);
    }

    @Override
    public void setBlocksDirty(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
        this.levelRenderer.setBlockDirty(blockPos, blockState, blockState2);
    }

    public void setSectionDirtyWithNeighbors(int n, int n2, int n3) {
        this.levelRenderer.setSectionDirtyWithNeighbors(n, n2, n3);
    }

    @Override
    public void destroyBlockProgress(int n, BlockPos blockPos, int n2) {
        this.levelRenderer.destroyBlockProgress(n, blockPos, n2);
    }

    @Override
    public void globalLevelEvent(int n, BlockPos blockPos, int n2) {
        this.levelRenderer.globalLevelEvent(n, blockPos, n2);
    }

    @Override
    public void levelEvent(@Nullable Player player, int n, BlockPos blockPos, int n2) {
        try {
            this.levelRenderer.levelEvent(player, n, blockPos, n2);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Playing level event");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Level event being played");
            crashReportCategory.setDetail("Block coordinates", CrashReportCategory.formatLocation(blockPos));
            crashReportCategory.setDetail("Event source", player);
            crashReportCategory.setDetail("Event type", n);
            crashReportCategory.setDetail("Event data", n2);
            throw new ReportedException(crashReport);
        }
    }

    @Override
    public void addParticle(ParticleOptions particleOptions, double d, double d2, double d3, double d4, double d5, double d6) {
        this.levelRenderer.addParticle(particleOptions, particleOptions.getType().getOverrideLimiter(), d, d2, d3, d4, d5, d6);
    }

    @Override
    public void addParticle(ParticleOptions particleOptions, boolean bl, double d, double d2, double d3, double d4, double d5, double d6) {
        this.levelRenderer.addParticle(particleOptions, particleOptions.getType().getOverrideLimiter() || bl, d, d2, d3, d4, d5, d6);
    }

    @Override
    public void addAlwaysVisibleParticle(ParticleOptions particleOptions, double d, double d2, double d3, double d4, double d5, double d6) {
        this.levelRenderer.addParticle(particleOptions, false, true, d, d2, d3, d4, d5, d6);
    }

    @Override
    public void addAlwaysVisibleParticle(ParticleOptions particleOptions, boolean bl, double d, double d2, double d3, double d4, double d5, double d6) {
        this.levelRenderer.addParticle(particleOptions, particleOptions.getType().getOverrideLimiter() || bl, true, d, d2, d3, d4, d5, d6);
    }

    public List<AbstractClientPlayer> players() {
        return this.players;
    }

    @Override
    public Biome getUncachedNoiseBiome(int n, int n2, int n3) {
        return this.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.PLAINS);
    }

    public float getSkyDarken(float f) {
        float f2 = this.getTimeOfDay(f);
        float f3 = 1.0f - (Mth.cos(f2 * 6.2831855f) * 2.0f + 0.2f);
        f3 = Mth.clamp(f3, 0.0f, 1.0f);
        f3 = 1.0f - f3;
        f3 = (float)((double)f3 * (1.0 - (double)(this.getRainLevel(f) * 5.0f) / 16.0));
        f3 = (float)((double)f3 * (1.0 - (double)(this.getThunderLevel(f) * 5.0f) / 16.0));
        return f3 * 0.8f + 0.2f;
    }

    public Vec3 getSkyColor(BlockPos blockPos, float f) {
        float f2;
        float f3;
        float f4 = this.getTimeOfDay(f);
        float f5 = Mth.cos(f4 * 6.2831855f) * 2.0f + 0.5f;
        f5 = Mth.clamp(f5, 0.0f, 1.0f);
        Biome biome = this.getBiome(blockPos);
        int n = biome.getSkyColor();
        float f6 = (float)(n >> 16 & 0xFF) / 255.0f;
        float f7 = (float)(n >> 8 & 0xFF) / 255.0f;
        float f8 = (float)(n & 0xFF) / 255.0f;
        f6 *= f5;
        f7 *= f5;
        f8 *= f5;
        float f9 = this.getRainLevel(f);
        if (f9 > 0.0f) {
            f3 = (f6 * 0.3f + f7 * 0.59f + f8 * 0.11f) * 0.6f;
            f2 = 1.0f - f9 * 0.75f;
            f6 = f6 * f2 + f3 * (1.0f - f2);
            f7 = f7 * f2 + f3 * (1.0f - f2);
            f8 = f8 * f2 + f3 * (1.0f - f2);
        }
        if ((f3 = this.getThunderLevel(f)) > 0.0f) {
            f2 = (f6 * 0.3f + f7 * 0.59f + f8 * 0.11f) * 0.2f;
            float f10 = 1.0f - f3 * 0.75f;
            f6 = f6 * f10 + f2 * (1.0f - f10);
            f7 = f7 * f10 + f2 * (1.0f - f10);
            f8 = f8 * f10 + f2 * (1.0f - f10);
        }
        if (this.skyFlashTime > 0) {
            f2 = (float)this.skyFlashTime - f;
            if (f2 > 1.0f) {
                f2 = 1.0f;
            }
            f6 = f6 * (1.0f - (f2 *= 0.45f)) + 0.8f * f2;
            f7 = f7 * (1.0f - f2) + 0.8f * f2;
            f8 = f8 * (1.0f - f2) + 1.0f * f2;
        }
        return new Vec3(f6, f7, f8);
    }

    public Vec3 getCloudColor(float f) {
        float f2;
        float f3;
        float f4 = this.getTimeOfDay(f);
        float f5 = Mth.cos(f4 * 6.2831855f) * 2.0f + 0.5f;
        f5 = Mth.clamp(f5, 0.0f, 1.0f);
        float f6 = 1.0f;
        float f7 = 1.0f;
        float f8 = 1.0f;
        float f9 = this.getRainLevel(f);
        if (f9 > 0.0f) {
            f2 = (f6 * 0.3f + f7 * 0.59f + f8 * 0.11f) * 0.6f;
            f3 = 1.0f - f9 * 0.95f;
            f6 = f6 * f3 + f2 * (1.0f - f3);
            f7 = f7 * f3 + f2 * (1.0f - f3);
            f8 = f8 * f3 + f2 * (1.0f - f3);
        }
        f6 *= f5 * 0.9f + 0.1f;
        f7 *= f5 * 0.9f + 0.1f;
        f8 *= f5 * 0.85f + 0.15f;
        f2 = this.getThunderLevel(f);
        if (f2 > 0.0f) {
            f3 = (f6 * 0.3f + f7 * 0.59f + f8 * 0.11f) * 0.2f;
            float f10 = 1.0f - f2 * 0.95f;
            f6 = f6 * f10 + f3 * (1.0f - f10);
            f7 = f7 * f10 + f3 * (1.0f - f10);
            f8 = f8 * f10 + f3 * (1.0f - f10);
        }
        return new Vec3(f6, f7, f8);
    }

    public float getStarBrightness(float f) {
        float f2 = this.getTimeOfDay(f);
        float f3 = 1.0f - (Mth.cos(f2 * 6.2831855f) * 2.0f + 0.25f);
        f3 = Mth.clamp(f3, 0.0f, 1.0f);
        return f3 * f3 * 0.5f;
    }

    public int getSkyFlashTime() {
        return this.skyFlashTime;
    }

    @Override
    public void setSkyFlashTime(int n) {
        this.skyFlashTime = n;
    }

    @Override
    public float getShade(Direction direction, boolean bl) {
        boolean bl2 = this.effects().constantAmbientLight();
        if (!bl) {
            return bl2 ? 0.9f : 1.0f;
        }
        switch (direction) {
            case DOWN: {
                return bl2 ? 0.9f : 0.5f;
            }
            case UP: {
                return bl2 ? 0.9f : 1.0f;
            }
            case NORTH: 
            case SOUTH: {
                return 0.8f;
            }
            case WEST: 
            case EAST: {
                return 0.6f;
            }
        }
        return 1.0f;
    }

    @Override
    public int getBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        BlockTintCache blockTintCache = (BlockTintCache)this.tintCaches.get((Object)colorResolver);
        return blockTintCache.getColor(blockPos, () -> this.calculateBlockTint(blockPos, colorResolver));
    }

    public int calculateBlockTint(BlockPos blockPos, ColorResolver colorResolver) {
        int n = Minecraft.getInstance().options.biomeBlendRadius;
        if (n == 0) {
            return colorResolver.getColor(this.getBiome(blockPos), blockPos.getX(), blockPos.getZ());
        }
        int n2 = (n * 2 + 1) * (n * 2 + 1);
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        Cursor3D cursor3D = new Cursor3D(blockPos.getX() - n, blockPos.getY(), blockPos.getZ() - n, blockPos.getX() + n, blockPos.getY(), blockPos.getZ() + n);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        while (cursor3D.advance()) {
            mutableBlockPos.set(cursor3D.nextX(), cursor3D.nextY(), cursor3D.nextZ());
            int n6 = colorResolver.getColor(this.getBiome(mutableBlockPos), mutableBlockPos.getX(), mutableBlockPos.getZ());
            n3 += (n6 & 0xFF0000) >> 16;
            n4 += (n6 & 0xFF00) >> 8;
            n5 += n6 & 0xFF;
        }
        return (n3 / n2 & 0xFF) << 16 | (n4 / n2 & 0xFF) << 8 | n5 / n2 & 0xFF;
    }

    public BlockPos getSharedSpawnPos() {
        BlockPos blockPos = new BlockPos(this.levelData.getXSpawn(), this.levelData.getYSpawn(), this.levelData.getZSpawn());
        if (!this.getWorldBorder().isWithinBounds(blockPos)) {
            blockPos = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
        }
        return blockPos;
    }

    public float getSharedSpawnAngle() {
        return this.levelData.getSpawnAngle();
    }

    public void setDefaultSpawnPos(BlockPos blockPos, float f) {
        this.levelData.setSpawn(blockPos, f);
    }

    public String toString() {
        return "ClientLevel";
    }

    @Override
    public ClientLevelData getLevelData() {
        return this.clientLevelData;
    }

    @Override
    public /* synthetic */ LevelData getLevelData() {
        return this.getLevelData();
    }

    @Override
    public /* synthetic */ ChunkSource getChunkSource() {
        return this.getChunkSource();
    }

    public static class ClientLevelData
    implements WritableLevelData {
        private final boolean hardcore;
        private final GameRules gameRules;
        private final boolean isFlat;
        private int xSpawn;
        private int ySpawn;
        private int zSpawn;
        private float spawnAngle;
        private long gameTime;
        private long dayTime;
        private boolean raining;
        private Difficulty difficulty;
        private boolean difficultyLocked;

        public ClientLevelData(Difficulty difficulty, boolean bl, boolean bl2) {
            this.difficulty = difficulty;
            this.hardcore = bl;
            this.isFlat = bl2;
            this.gameRules = new GameRules();
        }

        @Override
        public int getXSpawn() {
            return this.xSpawn;
        }

        @Override
        public int getYSpawn() {
            return this.ySpawn;
        }

        @Override
        public int getZSpawn() {
            return this.zSpawn;
        }

        @Override
        public float getSpawnAngle() {
            return this.spawnAngle;
        }

        @Override
        public long getGameTime() {
            return this.gameTime;
        }

        @Override
        public long getDayTime() {
            return this.dayTime;
        }

        @Override
        public void setXSpawn(int n) {
            this.xSpawn = n;
        }

        @Override
        public void setYSpawn(int n) {
            this.ySpawn = n;
        }

        @Override
        public void setZSpawn(int n) {
            this.zSpawn = n;
        }

        @Override
        public void setSpawnAngle(float f) {
            this.spawnAngle = f;
        }

        public void setGameTime(long l) {
            this.gameTime = l;
        }

        public void setDayTime(long l) {
            this.dayTime = l;
        }

        @Override
        public void setSpawn(BlockPos blockPos, float f) {
            this.xSpawn = blockPos.getX();
            this.ySpawn = blockPos.getY();
            this.zSpawn = blockPos.getZ();
            this.spawnAngle = f;
        }

        @Override
        public boolean isThundering() {
            return false;
        }

        @Override
        public boolean isRaining() {
            return this.raining;
        }

        @Override
        public void setRaining(boolean bl) {
            this.raining = bl;
        }

        @Override
        public boolean isHardcore() {
            return this.hardcore;
        }

        @Override
        public GameRules getGameRules() {
            return this.gameRules;
        }

        @Override
        public Difficulty getDifficulty() {
            return this.difficulty;
        }

        @Override
        public boolean isDifficultyLocked() {
            return this.difficultyLocked;
        }

        @Override
        public void fillCrashReportCategory(CrashReportCategory crashReportCategory) {
            WritableLevelData.super.fillCrashReportCategory(crashReportCategory);
        }

        public void setDifficulty(Difficulty difficulty) {
            this.difficulty = difficulty;
        }

        public void setDifficultyLocked(boolean bl) {
            this.difficultyLocked = bl;
        }

        public double getHorizonHeight() {
            if (this.isFlat) {
                return 0.0;
            }
            return 63.0;
        }

        public double getClearColorScale() {
            if (this.isFlat) {
                return 1.0;
            }
            return 0.03125;
        }
    }

}

