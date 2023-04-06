/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.AreaEffectCloudRenderer;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.CaveSpiderRenderer;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.CodRenderer;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.client.renderer.entity.DragonFireballRenderer;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EndermiteRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EvokerFangsRenderer;
import net.minecraft.client.renderer.entity.EvokerRenderer;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.FireworkEntityRenderer;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.client.renderer.entity.GiantMobRenderer;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.client.renderer.entity.HoglinRenderer;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.client.renderer.entity.IllusionerRenderer;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LeashKnotRenderer;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.client.renderer.entity.LlamaSpitRenderer;
import net.minecraft.client.renderer.entity.MagmaCubeRenderer;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.MushroomCowRenderer;
import net.minecraft.client.renderer.entity.OcelotRenderer;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.entity.PandaRenderer;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.PillagerRenderer;
import net.minecraft.client.renderer.entity.PolarBearRenderer;
import net.minecraft.client.renderer.entity.PufferfishRenderer;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.client.renderer.entity.RavagerRenderer;
import net.minecraft.client.renderer.entity.SalmonRenderer;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.renderer.entity.ShulkerBulletRenderer;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.SilverfishRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.minecraft.client.renderer.entity.SpectralArrowRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.SquidRenderer;
import net.minecraft.client.renderer.entity.StrayRenderer;
import net.minecraft.client.renderer.entity.StriderRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.client.renderer.entity.TurtleRenderer;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.VindicatorRenderer;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.client.renderer.entity.WitherBossRenderer;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.ZoglinRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.ZombieVillagerRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EntityRenderDispatcher {
    private static final RenderType SHADOW_RENDER_TYPE = RenderType.entityShadow(new ResourceLocation("textures/misc/shadow.png"));
    private final Map<EntityType<?>, EntityRenderer<?>> renderers = Maps.newHashMap();
    private final Map<String, PlayerRenderer> playerRenderers = Maps.newHashMap();
    private final PlayerRenderer defaultPlayerRenderer;
    private final Font font;
    public final TextureManager textureManager;
    private Level level;
    public Camera camera;
    private Quaternion cameraOrientation;
    public Entity crosshairPickEntity;
    public final Options options;
    private boolean shouldRenderShadow = true;
    private boolean renderHitBoxes;

    public <E extends Entity> int getPackedLightCoords(E e, float f) {
        return this.getRenderer(e).getPackedLightCoords(e, f);
    }

    private <T extends Entity> void register(EntityType<T> entityType, EntityRenderer<? super T> entityRenderer) {
        this.renderers.put(entityType, entityRenderer);
    }

    private void registerRenderers(ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager) {
        this.register(EntityType.AREA_EFFECT_CLOUD, new AreaEffectCloudRenderer(this));
        this.register(EntityType.ARMOR_STAND, new ArmorStandRenderer(this));
        this.register(EntityType.ARROW, new TippableArrowRenderer(this));
        this.register(EntityType.BAT, new BatRenderer(this));
        this.register(EntityType.BEE, new BeeRenderer(this));
        this.register(EntityType.BLAZE, new BlazeRenderer(this));
        this.register(EntityType.BOAT, new BoatRenderer(this));
        this.register(EntityType.CAT, new CatRenderer(this));
        this.register(EntityType.CAVE_SPIDER, new CaveSpiderRenderer(this));
        this.register(EntityType.CHEST_MINECART, new MinecartRenderer(this));
        this.register(EntityType.CHICKEN, new ChickenRenderer(this));
        this.register(EntityType.COD, new CodRenderer(this));
        this.register(EntityType.COMMAND_BLOCK_MINECART, new MinecartRenderer(this));
        this.register(EntityType.COW, new CowRenderer(this));
        this.register(EntityType.CREEPER, new CreeperRenderer(this));
        this.register(EntityType.DOLPHIN, new DolphinRenderer(this));
        this.register(EntityType.DONKEY, new ChestedHorseRenderer(this, 0.87f));
        this.register(EntityType.DRAGON_FIREBALL, new DragonFireballRenderer(this));
        this.register(EntityType.DROWNED, new DrownedRenderer(this));
        this.register(EntityType.EGG, new ThrownItemRenderer(this, itemRenderer));
        this.register(EntityType.ELDER_GUARDIAN, new ElderGuardianRenderer(this));
        this.register(EntityType.END_CRYSTAL, new EndCrystalRenderer(this));
        this.register(EntityType.ENDER_DRAGON, new EnderDragonRenderer(this));
        this.register(EntityType.ENDERMAN, new EndermanRenderer(this));
        this.register(EntityType.ENDERMITE, new EndermiteRenderer(this));
        this.register(EntityType.ENDER_PEARL, new ThrownItemRenderer(this, itemRenderer));
        this.register(EntityType.EVOKER_FANGS, new EvokerFangsRenderer(this));
        this.register(EntityType.EVOKER, new EvokerRenderer(this));
        this.register(EntityType.EXPERIENCE_BOTTLE, new ThrownItemRenderer(this, itemRenderer));
        this.register(EntityType.EXPERIENCE_ORB, new ExperienceOrbRenderer(this));
        this.register(EntityType.EYE_OF_ENDER, new ThrownItemRenderer(this, itemRenderer, 1.0f, true));
        this.register(EntityType.FALLING_BLOCK, new FallingBlockRenderer(this));
        this.register(EntityType.FIREBALL, new ThrownItemRenderer(this, itemRenderer, 3.0f, true));
        this.register(EntityType.FIREWORK_ROCKET, new FireworkEntityRenderer(this, itemRenderer));
        this.register(EntityType.FISHING_BOBBER, new FishingHookRenderer(this));
        this.register(EntityType.FOX, new FoxRenderer(this));
        this.register(EntityType.FURNACE_MINECART, new MinecartRenderer(this));
        this.register(EntityType.GHAST, new GhastRenderer(this));
        this.register(EntityType.GIANT, new GiantMobRenderer(this, 6.0f));
        this.register(EntityType.GUARDIAN, new GuardianRenderer(this));
        this.register(EntityType.HOGLIN, new HoglinRenderer(this));
        this.register(EntityType.HOPPER_MINECART, new MinecartRenderer(this));
        this.register(EntityType.HORSE, new HorseRenderer(this));
        this.register(EntityType.HUSK, new HuskRenderer(this));
        this.register(EntityType.ILLUSIONER, new IllusionerRenderer(this));
        this.register(EntityType.IRON_GOLEM, new IronGolemRenderer(this));
        this.register(EntityType.ITEM, new ItemEntityRenderer(this, itemRenderer));
        this.register(EntityType.ITEM_FRAME, new ItemFrameRenderer(this, itemRenderer));
        this.register(EntityType.LEASH_KNOT, new LeashKnotRenderer(this));
        this.register(EntityType.LIGHTNING_BOLT, new LightningBoltRenderer(this));
        this.register(EntityType.LLAMA, new LlamaRenderer(this));
        this.register(EntityType.LLAMA_SPIT, new LlamaSpitRenderer(this));
        this.register(EntityType.MAGMA_CUBE, new MagmaCubeRenderer(this));
        this.register(EntityType.MINECART, new MinecartRenderer(this));
        this.register(EntityType.MOOSHROOM, new MushroomCowRenderer(this));
        this.register(EntityType.MULE, new ChestedHorseRenderer(this, 0.92f));
        this.register(EntityType.OCELOT, new OcelotRenderer(this));
        this.register(EntityType.PAINTING, new PaintingRenderer(this));
        this.register(EntityType.PANDA, new PandaRenderer(this));
        this.register(EntityType.PARROT, new ParrotRenderer(this));
        this.register(EntityType.PHANTOM, new PhantomRenderer(this));
        this.register(EntityType.PIG, new PigRenderer(this));
        this.register(EntityType.PIGLIN, new PiglinRenderer(this, false));
        this.register(EntityType.PIGLIN_BRUTE, new PiglinRenderer(this, false));
        this.register(EntityType.PILLAGER, new PillagerRenderer(this));
        this.register(EntityType.POLAR_BEAR, new PolarBearRenderer(this));
        this.register(EntityType.POTION, new ThrownItemRenderer(this, itemRenderer));
        this.register(EntityType.PUFFERFISH, new PufferfishRenderer(this));
        this.register(EntityType.RABBIT, new RabbitRenderer(this));
        this.register(EntityType.RAVAGER, new RavagerRenderer(this));
        this.register(EntityType.SALMON, new SalmonRenderer(this));
        this.register(EntityType.SHEEP, new SheepRenderer(this));
        this.register(EntityType.SHULKER_BULLET, new ShulkerBulletRenderer(this));
        this.register(EntityType.SHULKER, new ShulkerRenderer(this));
        this.register(EntityType.SILVERFISH, new SilverfishRenderer(this));
        this.register(EntityType.SKELETON_HORSE, new UndeadHorseRenderer(this));
        this.register(EntityType.SKELETON, new SkeletonRenderer(this));
        this.register(EntityType.SLIME, new SlimeRenderer(this));
        this.register(EntityType.SMALL_FIREBALL, new ThrownItemRenderer(this, itemRenderer, 0.75f, true));
        this.register(EntityType.SNOWBALL, new ThrownItemRenderer(this, itemRenderer));
        this.register(EntityType.SNOW_GOLEM, new SnowGolemRenderer(this));
        this.register(EntityType.SPAWNER_MINECART, new MinecartRenderer(this));
        this.register(EntityType.SPECTRAL_ARROW, new SpectralArrowRenderer(this));
        this.register(EntityType.SPIDER, new SpiderRenderer(this));
        this.register(EntityType.SQUID, new SquidRenderer(this));
        this.register(EntityType.STRAY, new StrayRenderer(this));
        this.register(EntityType.TNT_MINECART, new TntMinecartRenderer(this));
        this.register(EntityType.TNT, new TntRenderer(this));
        this.register(EntityType.TRADER_LLAMA, new LlamaRenderer(this));
        this.register(EntityType.TRIDENT, new ThrownTridentRenderer(this));
        this.register(EntityType.TROPICAL_FISH, new TropicalFishRenderer(this));
        this.register(EntityType.TURTLE, new TurtleRenderer(this));
        this.register(EntityType.VEX, new VexRenderer(this));
        this.register(EntityType.VILLAGER, new VillagerRenderer(this, reloadableResourceManager));
        this.register(EntityType.VINDICATOR, new VindicatorRenderer(this));
        this.register(EntityType.WANDERING_TRADER, new WanderingTraderRenderer(this));
        this.register(EntityType.WITCH, new WitchRenderer(this));
        this.register(EntityType.WITHER, new WitherBossRenderer(this));
        this.register(EntityType.WITHER_SKELETON, new WitherSkeletonRenderer(this));
        this.register(EntityType.WITHER_SKULL, new WitherSkullRenderer(this));
        this.register(EntityType.WOLF, new WolfRenderer(this));
        this.register(EntityType.ZOGLIN, new ZoglinRenderer(this));
        this.register(EntityType.ZOMBIE_HORSE, new UndeadHorseRenderer(this));
        this.register(EntityType.ZOMBIE, new ZombieRenderer(this));
        this.register(EntityType.ZOMBIFIED_PIGLIN, new PiglinRenderer(this, true));
        this.register(EntityType.ZOMBIE_VILLAGER, new ZombieVillagerRenderer(this, reloadableResourceManager));
        this.register(EntityType.STRIDER, new StriderRenderer(this));
    }

    public EntityRenderDispatcher(TextureManager textureManager, ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager, Font font, Options options) {
        this.textureManager = textureManager;
        this.font = font;
        this.options = options;
        this.registerRenderers(itemRenderer, reloadableResourceManager);
        this.defaultPlayerRenderer = new PlayerRenderer(this);
        this.playerRenderers.put("default", this.defaultPlayerRenderer);
        this.playerRenderers.put("slim", new PlayerRenderer(this, true));
        for (EntityType entityType : Registry.ENTITY_TYPE) {
            if (entityType == EntityType.PLAYER || this.renderers.containsKey(entityType)) continue;
            throw new IllegalStateException("No renderer registered for " + Registry.ENTITY_TYPE.getKey(entityType));
        }
    }

    public <T extends Entity> EntityRenderer<? super T> getRenderer(T t) {
        if (t instanceof AbstractClientPlayer) {
            String string = ((AbstractClientPlayer)t).getModelName();
            PlayerRenderer playerRenderer = this.playerRenderers.get(string);
            if (playerRenderer != null) {
                return playerRenderer;
            }
            return this.defaultPlayerRenderer;
        }
        return this.renderers.get(((Entity)t).getType());
    }

    public void prepare(Level level, Camera camera, Entity entity) {
        this.level = level;
        this.camera = camera;
        this.cameraOrientation = camera.rotation();
        this.crosshairPickEntity = entity;
    }

    public void overrideCameraOrientation(Quaternion quaternion) {
        this.cameraOrientation = quaternion;
    }

    public void setRenderShadow(boolean bl) {
        this.shouldRenderShadow = bl;
    }

    public void setRenderHitBoxes(boolean bl) {
        this.renderHitBoxes = bl;
    }

    public boolean shouldRenderHitBoxes() {
        return this.renderHitBoxes;
    }

    public <E extends Entity> boolean shouldRender(E e, Frustum frustum, double d, double d2, double d3) {
        EntityRenderer<E> entityRenderer = this.getRenderer(e);
        return entityRenderer.shouldRender(e, frustum, d, d2, d3);
    }

    public <E extends Entity> void render(E e, double d, double d2, double d3, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        EntityRenderer<E> entityRenderer = this.getRenderer(e);
        try {
            float f3;
            double d4;
            Vec3 vec3 = entityRenderer.getRenderOffset(e, f2);
            double d5 = d + vec3.x();
            double d6 = d2 + vec3.y();
            double d7 = d3 + vec3.z();
            poseStack.pushPose();
            poseStack.translate(d5, d6, d7);
            entityRenderer.render(e, f, f2, poseStack, multiBufferSource, n);
            if (((Entity)e).displayFireAnimation()) {
                this.renderFlame(poseStack, multiBufferSource, e);
            }
            poseStack.translate(-vec3.x(), -vec3.y(), -vec3.z());
            if (this.options.entityShadows && this.shouldRenderShadow && entityRenderer.shadowRadius > 0.0f && !((Entity)e).isInvisible() && (f3 = (float)((1.0 - (d4 = this.distanceToSqr(((Entity)e).getX(), ((Entity)e).getY(), ((Entity)e).getZ())) / 256.0) * (double)entityRenderer.shadowStrength)) > 0.0f) {
                EntityRenderDispatcher.renderShadow(poseStack, multiBufferSource, e, f3, f2, this.level, entityRenderer.shadowRadius);
            }
            if (this.renderHitBoxes && !((Entity)e).isInvisible() && !Minecraft.getInstance().showOnlyReducedInfo()) {
                this.renderHitbox(poseStack, multiBufferSource.getBuffer(RenderType.lines()), e, f2);
            }
            poseStack.popPose();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Rendering entity in world");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Entity being rendered");
            ((Entity)e).fillCrashReportCategory(crashReportCategory);
            CrashReportCategory crashReportCategory2 = crashReport.addCategory("Renderer details");
            crashReportCategory2.setDetail("Assigned renderer", entityRenderer);
            crashReportCategory2.setDetail("Location", CrashReportCategory.formatLocation(d, d2, d3));
            crashReportCategory2.setDetail("Rotation", Float.valueOf(f));
            crashReportCategory2.setDetail("Delta", Float.valueOf(f2));
            throw new ReportedException(crashReport);
        }
    }

    private void renderHitbox(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, float f) {
        float f2 = entity.getBbWidth() / 2.0f;
        this.renderBox(poseStack, vertexConsumer, entity, 1.0f, 1.0f, 1.0f);
        if (entity instanceof EnderDragon) {
            double d = -Mth.lerp((double)f, entity.xOld, entity.getX());
            double d2 = -Mth.lerp((double)f, entity.yOld, entity.getY());
            double d3 = -Mth.lerp((double)f, entity.zOld, entity.getZ());
            for (EnderDragonPart enderDragonPart : ((EnderDragon)entity).getSubEntities()) {
                poseStack.pushPose();
                double d4 = d + Mth.lerp((double)f, enderDragonPart.xOld, enderDragonPart.getX());
                double d5 = d2 + Mth.lerp((double)f, enderDragonPart.yOld, enderDragonPart.getY());
                double d6 = d3 + Mth.lerp((double)f, enderDragonPart.zOld, enderDragonPart.getZ());
                poseStack.translate(d4, d5, d6);
                this.renderBox(poseStack, vertexConsumer, enderDragonPart, 0.25f, 1.0f, 0.0f);
                poseStack.popPose();
            }
        }
        if (entity instanceof LivingEntity) {
            float f3 = 0.01f;
            LevelRenderer.renderLineBox(poseStack, vertexConsumer, -f2, entity.getEyeHeight() - 0.01f, -f2, f2, entity.getEyeHeight() + 0.01f, f2, 1.0f, 0.0f, 0.0f, 1.0f);
        }
        Vec3 vec3 = entity.getViewVector(f);
        Matrix4f matrix4f = poseStack.last().pose();
        vertexConsumer.vertex(matrix4f, 0.0f, entity.getEyeHeight(), 0.0f).color(0, 0, 255, 255).endVertex();
        vertexConsumer.vertex(matrix4f, (float)(vec3.x * 2.0), (float)((double)entity.getEyeHeight() + vec3.y * 2.0), (float)(vec3.z * 2.0)).color(0, 0, 255, 255).endVertex();
    }

    private void renderBox(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, float f, float f2, float f3) {
        AABB aABB = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, aABB, f, f2, f3, 1.0f);
    }

    private void renderFlame(PoseStack poseStack, MultiBufferSource multiBufferSource, Entity entity) {
        TextureAtlasSprite textureAtlasSprite = ModelBakery.FIRE_0.sprite();
        TextureAtlasSprite textureAtlasSprite2 = ModelBakery.FIRE_1.sprite();
        poseStack.pushPose();
        float f = entity.getBbWidth() * 1.4f;
        poseStack.scale(f, f, f);
        float f2 = 0.5f;
        float f3 = 0.0f;
        float f4 = entity.getBbHeight() / f;
        float f5 = 0.0f;
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-this.camera.getYRot()));
        poseStack.translate(0.0, 0.0, -0.3f + (float)((int)f4) * 0.02f);
        float f6 = 0.0f;
        int n = 0;
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(Sheets.cutoutBlockSheet());
        PoseStack.Pose pose = poseStack.last();
        while (f4 > 0.0f) {
            TextureAtlasSprite textureAtlasSprite3 = n % 2 == 0 ? textureAtlasSprite : textureAtlasSprite2;
            float f7 = textureAtlasSprite3.getU0();
            float f8 = textureAtlasSprite3.getV0();
            float f9 = textureAtlasSprite3.getU1();
            float f10 = textureAtlasSprite3.getV1();
            if (n / 2 % 2 == 0) {
                float f11 = f9;
                f9 = f7;
                f7 = f11;
            }
            EntityRenderDispatcher.fireVertex(pose, vertexConsumer, f2 - 0.0f, 0.0f - f5, f6, f9, f10);
            EntityRenderDispatcher.fireVertex(pose, vertexConsumer, -f2 - 0.0f, 0.0f - f5, f6, f7, f10);
            EntityRenderDispatcher.fireVertex(pose, vertexConsumer, -f2 - 0.0f, 1.4f - f5, f6, f7, f8);
            EntityRenderDispatcher.fireVertex(pose, vertexConsumer, f2 - 0.0f, 1.4f - f5, f6, f9, f8);
            f4 -= 0.45f;
            f5 -= 0.45f;
            f2 *= 0.9f;
            f6 += 0.03f;
            ++n;
        }
        poseStack.popPose();
    }

    private static void fireVertex(PoseStack.Pose pose, VertexConsumer vertexConsumer, float f, float f2, float f3, float f4, float f5) {
        vertexConsumer.vertex(pose.pose(), f, f2, f3).color(255, 255, 255, 255).uv(f4, f5).overlayCoords(0, 10).uv2(240).normal(pose.normal(), 0.0f, 1.0f, 0.0f).endVertex();
    }

    private static void renderShadow(PoseStack poseStack, MultiBufferSource multiBufferSource, Entity entity, float f, float f2, LevelReader levelReader, float f3) {
        Mob mob;
        float f4 = f3;
        if (entity instanceof Mob && (mob = (Mob)entity).isBaby()) {
            f4 *= 0.5f;
        }
        double d = Mth.lerp((double)f2, entity.xOld, entity.getX());
        double d2 = Mth.lerp((double)f2, entity.yOld, entity.getY());
        double d3 = Mth.lerp((double)f2, entity.zOld, entity.getZ());
        int n = Mth.floor(d - (double)f4);
        int n2 = Mth.floor(d + (double)f4);
        int n3 = Mth.floor(d2 - (double)f4);
        int n4 = Mth.floor(d2);
        int n5 = Mth.floor(d3 - (double)f4);
        int n6 = Mth.floor(d3 + (double)f4);
        PoseStack.Pose pose = poseStack.last();
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(SHADOW_RENDER_TYPE);
        for (BlockPos blockPos : BlockPos.betweenClosed(new BlockPos(n, n3, n5), new BlockPos(n2, n4, n6))) {
            EntityRenderDispatcher.renderBlockShadow(pose, vertexConsumer, levelReader, blockPos, d, d2, d3, f4, f);
        }
    }

    private static void renderBlockShadow(PoseStack.Pose pose, VertexConsumer vertexConsumer, LevelReader levelReader, BlockPos blockPos, double d, double d2, double d3, float f, float f2) {
        BlockPos blockPos2 = blockPos.below();
        BlockState blockState = levelReader.getBlockState(blockPos2);
        if (blockState.getRenderShape() == RenderShape.INVISIBLE || levelReader.getMaxLocalRawBrightness(blockPos) <= 3) {
            return;
        }
        if (!blockState.isCollisionShapeFullBlock(levelReader, blockPos2)) {
            return;
        }
        VoxelShape voxelShape = blockState.getShape(levelReader, blockPos.below());
        if (voxelShape.isEmpty()) {
            return;
        }
        float f3 = (float)(((double)f2 - (d2 - (double)blockPos.getY()) / 2.0) * 0.5 * (double)levelReader.getBrightness(blockPos));
        if (f3 >= 0.0f) {
            if (f3 > 1.0f) {
                f3 = 1.0f;
            }
            AABB aABB = voxelShape.bounds();
            double d4 = (double)blockPos.getX() + aABB.minX;
            double d5 = (double)blockPos.getX() + aABB.maxX;
            double d6 = (double)blockPos.getY() + aABB.minY;
            double d7 = (double)blockPos.getZ() + aABB.minZ;
            double d8 = (double)blockPos.getZ() + aABB.maxZ;
            float f4 = (float)(d4 - d);
            float f5 = (float)(d5 - d);
            float f6 = (float)(d6 - d2);
            float f7 = (float)(d7 - d3);
            float f8 = (float)(d8 - d3);
            float f9 = -f4 / 2.0f / f + 0.5f;
            float f10 = -f5 / 2.0f / f + 0.5f;
            float f11 = -f7 / 2.0f / f + 0.5f;
            float f12 = -f8 / 2.0f / f + 0.5f;
            EntityRenderDispatcher.shadowVertex(pose, vertexConsumer, f3, f4, f6, f7, f9, f11);
            EntityRenderDispatcher.shadowVertex(pose, vertexConsumer, f3, f4, f6, f8, f9, f12);
            EntityRenderDispatcher.shadowVertex(pose, vertexConsumer, f3, f5, f6, f8, f10, f12);
            EntityRenderDispatcher.shadowVertex(pose, vertexConsumer, f3, f5, f6, f7, f10, f11);
        }
    }

    private static void shadowVertex(PoseStack.Pose pose, VertexConsumer vertexConsumer, float f, float f2, float f3, float f4, float f5, float f6) {
        vertexConsumer.vertex(pose.pose(), f2, f3, f4).color(1.0f, 1.0f, 1.0f, f).uv(f5, f6).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(pose.normal(), 0.0f, 1.0f, 0.0f).endVertex();
    }

    public void setLevel(@Nullable Level level) {
        this.level = level;
        if (level == null) {
            this.camera = null;
        }
    }

    public double distanceToSqr(Entity entity) {
        return this.camera.getPosition().distanceToSqr(entity.position());
    }

    public double distanceToSqr(double d, double d2, double d3) {
        return this.camera.getPosition().distanceToSqr(d, d2, d3);
    }

    public Quaternion cameraOrientation() {
        return this.cameraOrientation;
    }

    public Font getFont() {
        return this.font;
    }
}

