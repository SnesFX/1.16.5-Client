/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.EvictingQueue
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.client.particle;

import com.google.common.base.Charsets;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.AshParticle;
import net.minecraft.client.particle.AttackSweepParticle;
import net.minecraft.client.particle.BarrierParticle;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.client.particle.BubbleParticle;
import net.minecraft.client.particle.BubblePopParticle;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.CritParticle;
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.DustParticle;
import net.minecraft.client.particle.EnchantmentTableParticle;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.ExplodeParticle;
import net.minecraft.client.particle.FallingDustParticle;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.client.particle.HugeExplosionSeedParticle;
import net.minecraft.client.particle.LargeSmokeParticle;
import net.minecraft.client.particle.LavaParticle;
import net.minecraft.client.particle.MobAppearanceParticle;
import net.minecraft.client.particle.NoteParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.PlayerCloudParticle;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.ReversePortalParticle;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.particle.SoulParticle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.SpitParticle;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SquidInkParticle;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.client.particle.SuspendedTownParticle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.client.particle.TrackingEmitter;
import net.minecraft.client.particle.WakeParticle;
import net.minecraft.client.particle.WaterCurrentDownParticle;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.client.particle.WhiteAshParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ParticleEngine
implements PreparableReloadListener {
    private static final List<ParticleRenderType> RENDER_ORDER = ImmutableList.of((Object)ParticleRenderType.TERRAIN_SHEET, (Object)ParticleRenderType.PARTICLE_SHEET_OPAQUE, (Object)ParticleRenderType.PARTICLE_SHEET_LIT, (Object)ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT, (Object)ParticleRenderType.CUSTOM);
    protected ClientLevel level;
    private final Map<ParticleRenderType, Queue<Particle>> particles = Maps.newIdentityHashMap();
    private final Queue<TrackingEmitter> trackingEmitters = Queues.newArrayDeque();
    private final TextureManager textureManager;
    private final Random random = new Random();
    private final Int2ObjectMap<ParticleProvider<?>> providers = new Int2ObjectOpenHashMap();
    private final Queue<Particle> particlesToAdd = Queues.newArrayDeque();
    private final Map<ResourceLocation, MutableSpriteSet> spriteSets = Maps.newHashMap();
    private final TextureAtlas textureAtlas = new TextureAtlas(TextureAtlas.LOCATION_PARTICLES);

    public ParticleEngine(ClientLevel clientLevel, TextureManager textureManager) {
        textureManager.register(this.textureAtlas.location(), this.textureAtlas);
        this.level = clientLevel;
        this.textureManager = textureManager;
        this.registerProviders();
    }

    private void registerProviders() {
        this.register(ParticleTypes.AMBIENT_ENTITY_EFFECT, SpellParticle.AmbientMobProvider::new);
        this.register(ParticleTypes.ANGRY_VILLAGER, HeartParticle.AngryVillagerProvider::new);
        this.register(ParticleTypes.BARRIER, new BarrierParticle.Provider());
        this.register(ParticleTypes.BLOCK, new TerrainParticle.Provider());
        this.register(ParticleTypes.BUBBLE, BubbleParticle.Provider::new);
        this.register(ParticleTypes.BUBBLE_COLUMN_UP, BubbleColumnUpParticle.Provider::new);
        this.register(ParticleTypes.BUBBLE_POP, BubblePopParticle.Provider::new);
        this.register(ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireSmokeParticle.CosyProvider::new);
        this.register(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireSmokeParticle.SignalProvider::new);
        this.register(ParticleTypes.CLOUD, PlayerCloudParticle.Provider::new);
        this.register(ParticleTypes.COMPOSTER, SuspendedTownParticle.ComposterFillProvider::new);
        this.register(ParticleTypes.CRIT, CritParticle.Provider::new);
        this.register(ParticleTypes.CURRENT_DOWN, WaterCurrentDownParticle.Provider::new);
        this.register(ParticleTypes.DAMAGE_INDICATOR, CritParticle.DamageIndicatorProvider::new);
        this.register(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Provider::new);
        this.register(ParticleTypes.DOLPHIN, SuspendedTownParticle.DolphinSpeedProvider::new);
        this.register(ParticleTypes.DRIPPING_LAVA, DripParticle.LavaHangProvider::new);
        this.register(ParticleTypes.FALLING_LAVA, DripParticle.LavaFallProvider::new);
        this.register(ParticleTypes.LANDING_LAVA, DripParticle.LavaLandProvider::new);
        this.register(ParticleTypes.DRIPPING_WATER, DripParticle.WaterHangProvider::new);
        this.register(ParticleTypes.FALLING_WATER, DripParticle.WaterFallProvider::new);
        this.register(ParticleTypes.DUST, DustParticle.Provider::new);
        this.register(ParticleTypes.EFFECT, SpellParticle.Provider::new);
        this.register(ParticleTypes.ELDER_GUARDIAN, new MobAppearanceParticle.Provider());
        this.register(ParticleTypes.ENCHANTED_HIT, CritParticle.MagicProvider::new);
        this.register(ParticleTypes.ENCHANT, EnchantmentTableParticle.Provider::new);
        this.register(ParticleTypes.END_ROD, EndRodParticle.Provider::new);
        this.register(ParticleTypes.ENTITY_EFFECT, SpellParticle.MobProvider::new);
        this.register(ParticleTypes.EXPLOSION_EMITTER, new HugeExplosionSeedParticle.Provider());
        this.register(ParticleTypes.EXPLOSION, HugeExplosionParticle.Provider::new);
        this.register(ParticleTypes.FALLING_DUST, FallingDustParticle.Provider::new);
        this.register(ParticleTypes.FIREWORK, FireworkParticles.SparkProvider::new);
        this.register(ParticleTypes.FISHING, WakeParticle.Provider::new);
        this.register(ParticleTypes.FLAME, FlameParticle.Provider::new);
        this.register(ParticleTypes.SOUL, SoulParticle.Provider::new);
        this.register(ParticleTypes.SOUL_FIRE_FLAME, FlameParticle.Provider::new);
        this.register(ParticleTypes.FLASH, FireworkParticles.FlashProvider::new);
        this.register(ParticleTypes.HAPPY_VILLAGER, SuspendedTownParticle.HappyVillagerProvider::new);
        this.register(ParticleTypes.HEART, HeartParticle.Provider::new);
        this.register(ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantProvider::new);
        this.register(ParticleTypes.ITEM, new BreakingItemParticle.Provider());
        this.register(ParticleTypes.ITEM_SLIME, new BreakingItemParticle.SlimeProvider());
        this.register(ParticleTypes.ITEM_SNOWBALL, new BreakingItemParticle.SnowballProvider());
        this.register(ParticleTypes.LARGE_SMOKE, LargeSmokeParticle.Provider::new);
        this.register(ParticleTypes.LAVA, LavaParticle.Provider::new);
        this.register(ParticleTypes.MYCELIUM, SuspendedTownParticle.Provider::new);
        this.register(ParticleTypes.NAUTILUS, EnchantmentTableParticle.NautilusProvider::new);
        this.register(ParticleTypes.NOTE, NoteParticle.Provider::new);
        this.register(ParticleTypes.POOF, ExplodeParticle.Provider::new);
        this.register(ParticleTypes.PORTAL, PortalParticle.Provider::new);
        this.register(ParticleTypes.RAIN, WaterDropParticle.Provider::new);
        this.register(ParticleTypes.SMOKE, SmokeParticle.Provider::new);
        this.register(ParticleTypes.SNEEZE, PlayerCloudParticle.SneezeProvider::new);
        this.register(ParticleTypes.SPIT, SpitParticle.Provider::new);
        this.register(ParticleTypes.SWEEP_ATTACK, AttackSweepParticle.Provider::new);
        this.register(ParticleTypes.TOTEM_OF_UNDYING, TotemParticle.Provider::new);
        this.register(ParticleTypes.SQUID_INK, SquidInkParticle.Provider::new);
        this.register(ParticleTypes.UNDERWATER, SuspendedParticle.UnderwaterProvider::new);
        this.register(ParticleTypes.SPLASH, SplashParticle.Provider::new);
        this.register(ParticleTypes.WITCH, SpellParticle.WitchProvider::new);
        this.register(ParticleTypes.DRIPPING_HONEY, DripParticle.HoneyHangProvider::new);
        this.register(ParticleTypes.FALLING_HONEY, DripParticle.HoneyFallProvider::new);
        this.register(ParticleTypes.LANDING_HONEY, DripParticle.HoneyLandProvider::new);
        this.register(ParticleTypes.FALLING_NECTAR, DripParticle.NectarFallProvider::new);
        this.register(ParticleTypes.ASH, AshParticle.Provider::new);
        this.register(ParticleTypes.CRIMSON_SPORE, SuspendedParticle.CrimsonSporeProvider::new);
        this.register(ParticleTypes.WARPED_SPORE, SuspendedParticle.WarpedSporeProvider::new);
        this.register(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, DripParticle.ObsidianTearHangProvider::new);
        this.register(ParticleTypes.FALLING_OBSIDIAN_TEAR, DripParticle.ObsidianTearFallProvider::new);
        this.register(ParticleTypes.LANDING_OBSIDIAN_TEAR, DripParticle.ObsidianTearLandProvider::new);
        this.register(ParticleTypes.REVERSE_PORTAL, ReversePortalParticle.ReversePortalProvider::new);
        this.register(ParticleTypes.WHITE_ASH, WhiteAshParticle.Provider::new);
    }

    private <T extends ParticleOptions> void register(ParticleType<T> particleType, ParticleProvider<T> particleProvider) {
        this.providers.put(Registry.PARTICLE_TYPE.getId(particleType), particleProvider);
    }

    private <T extends ParticleOptions> void register(ParticleType<T> particleType, SpriteParticleRegistration<T> spriteParticleRegistration) {
        MutableSpriteSet mutableSpriteSet = new MutableSpriteSet();
        this.spriteSets.put(Registry.PARTICLE_TYPE.getKey(particleType), mutableSpriteSet);
        this.providers.put(Registry.PARTICLE_TYPE.getId(particleType), spriteParticleRegistration.create(mutableSpriteSet));
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
        ConcurrentMap concurrentMap = Maps.newConcurrentMap();
        CompletableFuture[] arrcompletableFuture = (CompletableFuture[])Registry.PARTICLE_TYPE.keySet().stream().map(resourceLocation -> CompletableFuture.runAsync(() -> this.loadParticleDescription(resourceManager, (ResourceLocation)resourceLocation, concurrentMap), executor)).toArray(n -> new CompletableFuture[n]);
        return ((CompletableFuture)((CompletableFuture)CompletableFuture.allOf(arrcompletableFuture).thenApplyAsync(void_ -> {
            profilerFiller.startTick();
            profilerFiller.push("stitching");
            TextureAtlas.Preparations preparations = this.textureAtlas.prepareToStitch(resourceManager, concurrentMap.values().stream().flatMap(Collection::stream), profilerFiller, 0);
            profilerFiller.pop();
            profilerFiller.endTick();
            return preparations;
        }, executor)).thenCompose(preparationBarrier::wait)).thenAcceptAsync(preparations -> {
            this.particles.clear();
            profilerFiller2.startTick();
            profilerFiller2.push("upload");
            this.textureAtlas.reload((TextureAtlas.Preparations)preparations);
            profilerFiller2.popPush("bindSpriteSets");
            TextureAtlasSprite textureAtlasSprite = this.textureAtlas.getSprite(MissingTextureAtlasSprite.getLocation());
            concurrentMap.forEach((resourceLocation, list) -> {
                ImmutableList immutableList = list.isEmpty() ? ImmutableList.of((Object)textureAtlasSprite) : (ImmutableList)list.stream().map(this.textureAtlas::getSprite).collect(ImmutableList.toImmutableList());
                this.spriteSets.get(resourceLocation).rebind((List<TextureAtlasSprite>)immutableList);
            });
            profilerFiller2.pop();
            profilerFiller2.endTick();
        }, executor2);
    }

    public void close() {
        this.textureAtlas.clearTextureData();
    }

    private void loadParticleDescription(ResourceManager resourceManager, ResourceLocation resourceLocation2, Map<ResourceLocation, List<ResourceLocation>> map) {
        ResourceLocation resourceLocation3 = new ResourceLocation(resourceLocation2.getNamespace(), "particles/" + resourceLocation2.getPath() + ".json");
        try {
            try (Resource resource = resourceManager.getResource(resourceLocation3);
                 InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);){
                ParticleDescription particleDescription = ParticleDescription.fromJson(GsonHelper.parse(inputStreamReader));
                List<ResourceLocation> list = particleDescription.getTextures();
                boolean bl = this.spriteSets.containsKey(resourceLocation2);
                if (list == null) {
                    if (bl) {
                        throw new IllegalStateException("Missing texture list for particle " + resourceLocation2);
                    }
                } else {
                    if (!bl) {
                        throw new IllegalStateException("Redundant texture list for particle " + resourceLocation2);
                    }
                    map.put(resourceLocation2, list.stream().map(resourceLocation -> new ResourceLocation(resourceLocation.getNamespace(), "particle/" + resourceLocation.getPath())).collect(Collectors.toList()));
                }
            }
        }
        catch (IOException iOException) {
            throw new IllegalStateException("Failed to load description for particle " + resourceLocation2, iOException);
        }
    }

    public void createTrackingEmitter(Entity entity, ParticleOptions particleOptions) {
        this.trackingEmitters.add(new TrackingEmitter(this.level, entity, particleOptions));
    }

    public void createTrackingEmitter(Entity entity, ParticleOptions particleOptions, int n) {
        this.trackingEmitters.add(new TrackingEmitter(this.level, entity, particleOptions, n));
    }

    @Nullable
    public Particle createParticle(ParticleOptions particleOptions, double d, double d2, double d3, double d4, double d5, double d6) {
        Particle particle = this.makeParticle(particleOptions, d, d2, d3, d4, d5, d6);
        if (particle != null) {
            this.add(particle);
            return particle;
        }
        return null;
    }

    @Nullable
    private <T extends ParticleOptions> Particle makeParticle(T t, double d, double d2, double d3, double d4, double d5, double d6) {
        ParticleProvider particleProvider = (ParticleProvider)this.providers.get(Registry.PARTICLE_TYPE.getId(t.getType()));
        if (particleProvider == null) {
            return null;
        }
        return particleProvider.createParticle(t, this.level, d, d2, d3, d4, d5, d6);
    }

    public void add(Particle particle) {
        this.particlesToAdd.add(particle);
    }

    public void tick() {
        Object object;
        this.particles.forEach((particleRenderType, queue) -> {
            this.level.getProfiler().push(particleRenderType.toString());
            this.tickParticleList((Collection<Particle>)queue);
            this.level.getProfiler().pop();
        });
        if (!this.trackingEmitters.isEmpty()) {
            object = Lists.newArrayList();
            for (TrackingEmitter trackingEmitter : this.trackingEmitters) {
                trackingEmitter.tick();
                if (trackingEmitter.isAlive()) continue;
                object.add(trackingEmitter);
            }
            this.trackingEmitters.removeAll((Collection<?>)object);
        }
        if (!this.particlesToAdd.isEmpty()) {
            while ((object = this.particlesToAdd.poll()) != null) {
                this.particles.computeIfAbsent(((Particle)object).getRenderType(), particleRenderType -> EvictingQueue.create((int)16384)).add(object);
            }
        }
    }

    private void tickParticleList(Collection<Particle> collection) {
        if (!collection.isEmpty()) {
            Iterator<Particle> iterator = collection.iterator();
            while (iterator.hasNext()) {
                Particle particle = iterator.next();
                this.tickParticle(particle);
                if (particle.isAlive()) continue;
                iterator.remove();
            }
        }
    }

    private void tickParticle(Particle particle) {
        try {
            particle.tick();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Ticking Particle");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Particle being ticked");
            crashReportCategory.setDetail("Particle", particle::toString);
            crashReportCategory.setDetail("Particle Type", particle.getRenderType()::toString);
            throw new ReportedException(crashReport);
        }
    }

    public void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LightTexture lightTexture, Camera camera, float f) {
        lightTexture.turnOnLightLayer();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.enableFog();
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(poseStack.last().pose());
        for (ParticleRenderType particleRenderType : RENDER_ORDER) {
            Iterable iterable = this.particles.get(particleRenderType);
            if (iterable == null) continue;
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            particleRenderType.begin(bufferBuilder, this.textureManager);
            for (Particle particle : iterable) {
                try {
                    particle.render(bufferBuilder, camera, f);
                }
                catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.forThrowable(throwable, "Rendering Particle");
                    CrashReportCategory crashReportCategory = crashReport.addCategory("Particle being rendered");
                    crashReportCategory.setDetail("Particle", particle::toString);
                    crashReportCategory.setDetail("Particle Type", particleRenderType::toString);
                    throw new ReportedException(crashReport);
                }
            }
            particleRenderType.end(tesselator);
        }
        RenderSystem.popMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
        RenderSystem.disableBlend();
        RenderSystem.defaultAlphaFunc();
        lightTexture.turnOffLightLayer();
        RenderSystem.disableFog();
    }

    public void setLevel(@Nullable ClientLevel clientLevel) {
        this.level = clientLevel;
        this.particles.clear();
        this.trackingEmitters.clear();
    }

    public void destroy(BlockPos blockPos, BlockState blockState) {
        if (blockState.isAir()) {
            return;
        }
        VoxelShape voxelShape = blockState.getShape(this.level, blockPos);
        double d7 = 0.25;
        voxelShape.forAllBoxes((d, d2, d3, d4, d5, d6) -> {
            double d7 = Math.min(1.0, d4 - d);
            double d8 = Math.min(1.0, d5 - d2);
            double d9 = Math.min(1.0, d6 - d3);
            int n = Math.max(2, Mth.ceil(d7 / 0.25));
            int n2 = Math.max(2, Mth.ceil(d8 / 0.25));
            int n3 = Math.max(2, Mth.ceil(d9 / 0.25));
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < n2; ++j) {
                    for (int k = 0; k < n3; ++k) {
                        double d10 = ((double)i + 0.5) / (double)n;
                        double d11 = ((double)j + 0.5) / (double)n2;
                        double d12 = ((double)k + 0.5) / (double)n3;
                        double d13 = d10 * d7 + d;
                        double d14 = d11 * d8 + d2;
                        double d15 = d12 * d9 + d3;
                        this.add(new TerrainParticle(this.level, (double)blockPos.getX() + d13, (double)blockPos.getY() + d14, (double)blockPos.getZ() + d15, d10 - 0.5, d11 - 0.5, d12 - 0.5, blockState).init(blockPos));
                    }
                }
            }
        });
    }

    public void crack(BlockPos blockPos, Direction direction) {
        BlockState blockState = this.level.getBlockState(blockPos);
        if (blockState.getRenderShape() == RenderShape.INVISIBLE) {
            return;
        }
        int n = blockPos.getX();
        int n2 = blockPos.getY();
        int n3 = blockPos.getZ();
        float f = 0.1f;
        AABB aABB = blockState.getShape(this.level, blockPos).bounds();
        double d = (double)n + this.random.nextDouble() * (aABB.maxX - aABB.minX - 0.20000000298023224) + 0.10000000149011612 + aABB.minX;
        double d2 = (double)n2 + this.random.nextDouble() * (aABB.maxY - aABB.minY - 0.20000000298023224) + 0.10000000149011612 + aABB.minY;
        double d3 = (double)n3 + this.random.nextDouble() * (aABB.maxZ - aABB.minZ - 0.20000000298023224) + 0.10000000149011612 + aABB.minZ;
        if (direction == Direction.DOWN) {
            d2 = (double)n2 + aABB.minY - 0.10000000149011612;
        }
        if (direction == Direction.UP) {
            d2 = (double)n2 + aABB.maxY + 0.10000000149011612;
        }
        if (direction == Direction.NORTH) {
            d3 = (double)n3 + aABB.minZ - 0.10000000149011612;
        }
        if (direction == Direction.SOUTH) {
            d3 = (double)n3 + aABB.maxZ + 0.10000000149011612;
        }
        if (direction == Direction.WEST) {
            d = (double)n + aABB.minX - 0.10000000149011612;
        }
        if (direction == Direction.EAST) {
            d = (double)n + aABB.maxX + 0.10000000149011612;
        }
        this.add(new TerrainParticle(this.level, d, d2, d3, 0.0, 0.0, 0.0, blockState).init(blockPos).setPower(0.2f).scale(0.6f));
    }

    public String countParticles() {
        return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
    }

    class MutableSpriteSet
    implements SpriteSet {
        private List<TextureAtlasSprite> sprites;

        private MutableSpriteSet() {
        }

        @Override
        public TextureAtlasSprite get(int n, int n2) {
            return this.sprites.get(n * (this.sprites.size() - 1) / n2);
        }

        @Override
        public TextureAtlasSprite get(Random random) {
            return this.sprites.get(random.nextInt(this.sprites.size()));
        }

        public void rebind(List<TextureAtlasSprite> list) {
            this.sprites = ImmutableList.copyOf(list);
        }
    }

    @FunctionalInterface
    static interface SpriteParticleRegistration<T extends ParticleOptions> {
        public ParticleProvider<T> create(SpriteSet var1);
    }

}

