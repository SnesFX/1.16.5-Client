/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DSL;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType<T extends Entity> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final EntityType<AreaEffectCloud> AREA_EFFECT_CLOUD = EntityType.register("area_effect_cloud", Builder.of((arg_0, arg_1) -> AreaEffectCloud.new(arg_0, arg_1), MobCategory.MISC).fireImmune().sized(6.0f, 0.5f).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
    public static final EntityType<ArmorStand> ARMOR_STAND = EntityType.register("armor_stand", Builder.of((arg_0, arg_1) -> ArmorStand.new(arg_0, arg_1), MobCategory.MISC).sized(0.5f, 1.975f).clientTrackingRange(10));
    public static final EntityType<Arrow> ARROW = EntityType.register("arrow", Builder.of((arg_0, arg_1) -> Arrow.new(arg_0, arg_1), MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20));
    public static final EntityType<Bat> BAT = EntityType.register("bat", Builder.of((arg_0, arg_1) -> Bat.new(arg_0, arg_1), MobCategory.AMBIENT).sized(0.5f, 0.9f).clientTrackingRange(5));
    public static final EntityType<Bee> BEE = EntityType.register("bee", Builder.of((arg_0, arg_1) -> Bee.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.7f, 0.6f).clientTrackingRange(8));
    public static final EntityType<Blaze> BLAZE = EntityType.register("blaze", Builder.of((arg_0, arg_1) -> Blaze.new(arg_0, arg_1), MobCategory.MONSTER).fireImmune().sized(0.6f, 1.8f).clientTrackingRange(8));
    public static final EntityType<Boat> BOAT = EntityType.register("boat", Builder.of((arg_0, arg_1) -> Boat.new(arg_0, arg_1), MobCategory.MISC).sized(1.375f, 0.5625f).clientTrackingRange(10));
    public static final EntityType<Cat> CAT = EntityType.register("cat", Builder.of((arg_0, arg_1) -> Cat.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.6f, 0.7f).clientTrackingRange(8));
    public static final EntityType<CaveSpider> CAVE_SPIDER = EntityType.register("cave_spider", Builder.of((arg_0, arg_1) -> CaveSpider.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.7f, 0.5f).clientTrackingRange(8));
    public static final EntityType<Chicken> CHICKEN = EntityType.register("chicken", Builder.of((arg_0, arg_1) -> Chicken.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.4f, 0.7f).clientTrackingRange(10));
    public static final EntityType<Cod> COD = EntityType.register("cod", Builder.of((arg_0, arg_1) -> Cod.new(arg_0, arg_1), MobCategory.WATER_AMBIENT).sized(0.5f, 0.3f).clientTrackingRange(4));
    public static final EntityType<Cow> COW = EntityType.register("cow", Builder.of((arg_0, arg_1) -> Cow.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.9f, 1.4f).clientTrackingRange(10));
    public static final EntityType<Creeper> CREEPER = EntityType.register("creeper", Builder.of((arg_0, arg_1) -> Creeper.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 1.7f).clientTrackingRange(8));
    public static final EntityType<Dolphin> DOLPHIN = EntityType.register("dolphin", Builder.of((arg_0, arg_1) -> Dolphin.new(arg_0, arg_1), MobCategory.WATER_CREATURE).sized(0.9f, 0.6f));
    public static final EntityType<Donkey> DONKEY = EntityType.register("donkey", Builder.of((arg_0, arg_1) -> Donkey.new(arg_0, arg_1), MobCategory.CREATURE).sized(1.3964844f, 1.5f).clientTrackingRange(10));
    public static final EntityType<DragonFireball> DRAGON_FIREBALL = EntityType.register("dragon_fireball", Builder.of((arg_0, arg_1) -> DragonFireball.new(arg_0, arg_1), MobCategory.MISC).sized(1.0f, 1.0f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<Drowned> DROWNED = EntityType.register("drowned", Builder.of((arg_0, arg_1) -> Drowned.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<ElderGuardian> ELDER_GUARDIAN = EntityType.register("elder_guardian", Builder.of((arg_0, arg_1) -> ElderGuardian.new(arg_0, arg_1), MobCategory.MONSTER).sized(1.9975f, 1.9975f).clientTrackingRange(10));
    public static final EntityType<EndCrystal> END_CRYSTAL = EntityType.register("end_crystal", Builder.of((arg_0, arg_1) -> EndCrystal.new(arg_0, arg_1), MobCategory.MISC).sized(2.0f, 2.0f).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE));
    public static final EntityType<EnderDragon> ENDER_DRAGON = EntityType.register("ender_dragon", Builder.of((arg_0, arg_1) -> EnderDragon.new(arg_0, arg_1), MobCategory.MONSTER).fireImmune().sized(16.0f, 8.0f).clientTrackingRange(10));
    public static final EntityType<EnderMan> ENDERMAN = EntityType.register("enderman", Builder.of((arg_0, arg_1) -> EnderMan.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 2.9f).clientTrackingRange(8));
    public static final EntityType<Endermite> ENDERMITE = EntityType.register("endermite", Builder.of((arg_0, arg_1) -> Endermite.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.4f, 0.3f).clientTrackingRange(8));
    public static final EntityType<Evoker> EVOKER = EntityType.register("evoker", Builder.of((arg_0, arg_1) -> Evoker.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<EvokerFangs> EVOKER_FANGS = EntityType.register("evoker_fangs", Builder.of((arg_0, arg_1) -> EvokerFangs.new(arg_0, arg_1), MobCategory.MISC).sized(0.5f, 0.8f).clientTrackingRange(6).updateInterval(2));
    public static final EntityType<ExperienceOrb> EXPERIENCE_ORB = EntityType.register("experience_orb", Builder.of((arg_0, arg_1) -> ExperienceOrb.new(arg_0, arg_1), MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(6).updateInterval(20));
    public static final EntityType<EyeOfEnder> EYE_OF_ENDER = EntityType.register("eye_of_ender", Builder.of((arg_0, arg_1) -> EyeOfEnder.new(arg_0, arg_1), MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(4));
    public static final EntityType<FallingBlockEntity> FALLING_BLOCK = EntityType.register("falling_block", Builder.of((arg_0, arg_1) -> FallingBlockEntity.new(arg_0, arg_1), MobCategory.MISC).sized(0.98f, 0.98f).clientTrackingRange(10).updateInterval(20));
    public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET = EntityType.register("firework_rocket", Builder.of((arg_0, arg_1) -> FireworkRocketEntity.new(arg_0, arg_1), MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<Fox> FOX = EntityType.register("fox", Builder.of((arg_0, arg_1) -> Fox.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.6f, 0.7f).clientTrackingRange(8).immuneTo(Blocks.SWEET_BERRY_BUSH));
    public static final EntityType<Ghast> GHAST = EntityType.register("ghast", Builder.of((arg_0, arg_1) -> Ghast.new(arg_0, arg_1), MobCategory.MONSTER).fireImmune().sized(4.0f, 4.0f).clientTrackingRange(10));
    public static final EntityType<Giant> GIANT = EntityType.register("giant", Builder.of((arg_0, arg_1) -> Giant.new(arg_0, arg_1), MobCategory.MONSTER).sized(3.6f, 12.0f).clientTrackingRange(10));
    public static final EntityType<Guardian> GUARDIAN = EntityType.register("guardian", Builder.of((arg_0, arg_1) -> Guardian.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.85f, 0.85f).clientTrackingRange(8));
    public static final EntityType<Hoglin> HOGLIN = EntityType.register("hoglin", Builder.of((arg_0, arg_1) -> Hoglin.new(arg_0, arg_1), MobCategory.MONSTER).sized(1.3964844f, 1.4f).clientTrackingRange(8));
    public static final EntityType<Horse> HORSE = EntityType.register("horse", Builder.of((arg_0, arg_1) -> Horse.new(arg_0, arg_1), MobCategory.CREATURE).sized(1.3964844f, 1.6f).clientTrackingRange(10));
    public static final EntityType<Husk> HUSK = EntityType.register("husk", Builder.of((arg_0, arg_1) -> Husk.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<Illusioner> ILLUSIONER = EntityType.register("illusioner", Builder.of((arg_0, arg_1) -> Illusioner.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<IronGolem> IRON_GOLEM = EntityType.register("iron_golem", Builder.of((arg_0, arg_1) -> IronGolem.new(arg_0, arg_1), MobCategory.MISC).sized(1.4f, 2.7f).clientTrackingRange(10));
    public static final EntityType<ItemEntity> ITEM = EntityType.register("item", Builder.of((arg_0, arg_1) -> ItemEntity.new(arg_0, arg_1), MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(6).updateInterval(20));
    public static final EntityType<ItemFrame> ITEM_FRAME = EntityType.register("item_frame", Builder.of((arg_0, arg_1) -> ItemFrame.new(arg_0, arg_1), MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
    public static final EntityType<LargeFireball> FIREBALL = EntityType.register("fireball", Builder.of((arg_0, arg_1) -> LargeFireball.new(arg_0, arg_1), MobCategory.MISC).sized(1.0f, 1.0f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<LeashFenceKnotEntity> LEASH_KNOT = EntityType.register("leash_knot", Builder.of((arg_0, arg_1) -> LeashFenceKnotEntity.new(arg_0, arg_1), MobCategory.MISC).noSave().sized(0.5f, 0.5f).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
    public static final EntityType<LightningBolt> LIGHTNING_BOLT = EntityType.register("lightning_bolt", Builder.of((arg_0, arg_1) -> LightningBolt.new(arg_0, arg_1), MobCategory.MISC).noSave().sized(0.0f, 0.0f).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE));
    public static final EntityType<Llama> LLAMA = EntityType.register("llama", Builder.of((arg_0, arg_1) -> Llama.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.9f, 1.87f).clientTrackingRange(10));
    public static final EntityType<LlamaSpit> LLAMA_SPIT = EntityType.register("llama_spit", Builder.of((arg_0, arg_1) -> LlamaSpit.new(arg_0, arg_1), MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<MagmaCube> MAGMA_CUBE = EntityType.register("magma_cube", Builder.of((arg_0, arg_1) -> MagmaCube.new(arg_0, arg_1), MobCategory.MONSTER).fireImmune().sized(2.04f, 2.04f).clientTrackingRange(8));
    public static final EntityType<Minecart> MINECART = EntityType.register("minecart", Builder.of((arg_0, arg_1) -> Minecart.new(arg_0, arg_1), MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<MinecartChest> CHEST_MINECART = EntityType.register("chest_minecart", Builder.of((arg_0, arg_1) -> MinecartChest.new(arg_0, arg_1), MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<MinecartCommandBlock> COMMAND_BLOCK_MINECART = EntityType.register("command_block_minecart", Builder.of((arg_0, arg_1) -> MinecartCommandBlock.new(arg_0, arg_1), MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<MinecartFurnace> FURNACE_MINECART = EntityType.register("furnace_minecart", Builder.of((arg_0, arg_1) -> MinecartFurnace.new(arg_0, arg_1), MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<MinecartHopper> HOPPER_MINECART = EntityType.register("hopper_minecart", Builder.of((arg_0, arg_1) -> MinecartHopper.new(arg_0, arg_1), MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<MinecartSpawner> SPAWNER_MINECART = EntityType.register("spawner_minecart", Builder.of((arg_0, arg_1) -> MinecartSpawner.new(arg_0, arg_1), MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<MinecartTNT> TNT_MINECART = EntityType.register("tnt_minecart", Builder.of((arg_0, arg_1) -> MinecartTNT.new(arg_0, arg_1), MobCategory.MISC).sized(0.98f, 0.7f).clientTrackingRange(8));
    public static final EntityType<Mule> MULE = EntityType.register("mule", Builder.of((arg_0, arg_1) -> Mule.new(arg_0, arg_1), MobCategory.CREATURE).sized(1.3964844f, 1.6f).clientTrackingRange(8));
    public static final EntityType<MushroomCow> MOOSHROOM = EntityType.register("mooshroom", Builder.of((arg_0, arg_1) -> MushroomCow.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.9f, 1.4f).clientTrackingRange(10));
    public static final EntityType<Ocelot> OCELOT = EntityType.register("ocelot", Builder.of((arg_0, arg_1) -> Ocelot.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.6f, 0.7f).clientTrackingRange(10));
    public static final EntityType<Painting> PAINTING = EntityType.register("painting", Builder.of((arg_0, arg_1) -> Painting.new(arg_0, arg_1), MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
    public static final EntityType<Panda> PANDA = EntityType.register("panda", Builder.of((arg_0, arg_1) -> Panda.new(arg_0, arg_1), MobCategory.CREATURE).sized(1.3f, 1.25f).clientTrackingRange(10));
    public static final EntityType<Parrot> PARROT = EntityType.register("parrot", Builder.of((arg_0, arg_1) -> Parrot.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.5f, 0.9f).clientTrackingRange(8));
    public static final EntityType<Phantom> PHANTOM = EntityType.register("phantom", Builder.of((arg_0, arg_1) -> Phantom.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.9f, 0.5f).clientTrackingRange(8));
    public static final EntityType<Pig> PIG = EntityType.register("pig", Builder.of((arg_0, arg_1) -> Pig.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.9f, 0.9f).clientTrackingRange(10));
    public static final EntityType<Piglin> PIGLIN = EntityType.register("piglin", Builder.of((arg_0, arg_1) -> Piglin.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<PiglinBrute> PIGLIN_BRUTE = EntityType.register("piglin_brute", Builder.of((arg_0, arg_1) -> PiglinBrute.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<Pillager> PILLAGER = EntityType.register("pillager", Builder.of((arg_0, arg_1) -> Pillager.new(arg_0, arg_1), MobCategory.MONSTER).canSpawnFarFromPlayer().sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<PolarBear> POLAR_BEAR = EntityType.register("polar_bear", Builder.of((arg_0, arg_1) -> PolarBear.new(arg_0, arg_1), MobCategory.CREATURE).sized(1.4f, 1.4f).clientTrackingRange(10));
    public static final EntityType<PrimedTnt> TNT = EntityType.register("tnt", Builder.of((arg_0, arg_1) -> PrimedTnt.new(arg_0, arg_1), MobCategory.MISC).fireImmune().sized(0.98f, 0.98f).clientTrackingRange(10).updateInterval(10));
    public static final EntityType<Pufferfish> PUFFERFISH = EntityType.register("pufferfish", Builder.of((arg_0, arg_1) -> Pufferfish.new(arg_0, arg_1), MobCategory.WATER_AMBIENT).sized(0.7f, 0.7f).clientTrackingRange(4));
    public static final EntityType<Rabbit> RABBIT = EntityType.register("rabbit", Builder.of((arg_0, arg_1) -> Rabbit.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.4f, 0.5f).clientTrackingRange(8));
    public static final EntityType<Ravager> RAVAGER = EntityType.register("ravager", Builder.of((arg_0, arg_1) -> Ravager.new(arg_0, arg_1), MobCategory.MONSTER).sized(1.95f, 2.2f).clientTrackingRange(10));
    public static final EntityType<Salmon> SALMON = EntityType.register("salmon", Builder.of((arg_0, arg_1) -> Salmon.new(arg_0, arg_1), MobCategory.WATER_AMBIENT).sized(0.7f, 0.4f).clientTrackingRange(4));
    public static final EntityType<Sheep> SHEEP = EntityType.register("sheep", Builder.of((arg_0, arg_1) -> Sheep.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.9f, 1.3f).clientTrackingRange(10));
    public static final EntityType<Shulker> SHULKER = EntityType.register("shulker", Builder.of((arg_0, arg_1) -> Shulker.new(arg_0, arg_1), MobCategory.MONSTER).fireImmune().canSpawnFarFromPlayer().sized(1.0f, 1.0f).clientTrackingRange(10));
    public static final EntityType<ShulkerBullet> SHULKER_BULLET = EntityType.register("shulker_bullet", Builder.of((arg_0, arg_1) -> ShulkerBullet.new(arg_0, arg_1), MobCategory.MISC).sized(0.3125f, 0.3125f).clientTrackingRange(8));
    public static final EntityType<Silverfish> SILVERFISH = EntityType.register("silverfish", Builder.of((arg_0, arg_1) -> Silverfish.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.4f, 0.3f).clientTrackingRange(8));
    public static final EntityType<Skeleton> SKELETON = EntityType.register("skeleton", Builder.of((arg_0, arg_1) -> Skeleton.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 1.99f).clientTrackingRange(8));
    public static final EntityType<SkeletonHorse> SKELETON_HORSE = EntityType.register("skeleton_horse", Builder.of((arg_0, arg_1) -> SkeletonHorse.new(arg_0, arg_1), MobCategory.CREATURE).sized(1.3964844f, 1.6f).clientTrackingRange(10));
    public static final EntityType<Slime> SLIME = EntityType.register("slime", Builder.of((arg_0, arg_1) -> Slime.new(arg_0, arg_1), MobCategory.MONSTER).sized(2.04f, 2.04f).clientTrackingRange(10));
    public static final EntityType<SmallFireball> SMALL_FIREBALL = EntityType.register("small_fireball", Builder.of((arg_0, arg_1) -> SmallFireball.new(arg_0, arg_1), MobCategory.MISC).sized(0.3125f, 0.3125f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<SnowGolem> SNOW_GOLEM = EntityType.register("snow_golem", Builder.of((arg_0, arg_1) -> SnowGolem.new(arg_0, arg_1), MobCategory.MISC).sized(0.7f, 1.9f).clientTrackingRange(8));
    public static final EntityType<Snowball> SNOWBALL = EntityType.register("snowball", Builder.of((arg_0, arg_1) -> Snowball.new(arg_0, arg_1), MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<SpectralArrow> SPECTRAL_ARROW = EntityType.register("spectral_arrow", Builder.of((arg_0, arg_1) -> SpectralArrow.new(arg_0, arg_1), MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20));
    public static final EntityType<Spider> SPIDER = EntityType.register("spider", Builder.of((arg_0, arg_1) -> Spider.new(arg_0, arg_1), MobCategory.MONSTER).sized(1.4f, 0.9f).clientTrackingRange(8));
    public static final EntityType<Squid> SQUID = EntityType.register("squid", Builder.of((arg_0, arg_1) -> Squid.new(arg_0, arg_1), MobCategory.WATER_CREATURE).sized(0.8f, 0.8f).clientTrackingRange(8));
    public static final EntityType<Stray> STRAY = EntityType.register("stray", Builder.of((arg_0, arg_1) -> Stray.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 1.99f).clientTrackingRange(8));
    public static final EntityType<Strider> STRIDER = EntityType.register("strider", Builder.of((arg_0, arg_1) -> Strider.new(arg_0, arg_1), MobCategory.CREATURE).fireImmune().sized(0.9f, 1.7f).clientTrackingRange(10));
    public static final EntityType<ThrownEgg> EGG = EntityType.register("egg", Builder.of((arg_0, arg_1) -> ThrownEgg.new(arg_0, arg_1), MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<ThrownEnderpearl> ENDER_PEARL = EntityType.register("ender_pearl", Builder.of((arg_0, arg_1) -> ThrownEnderpearl.new(arg_0, arg_1), MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<ThrownExperienceBottle> EXPERIENCE_BOTTLE = EntityType.register("experience_bottle", Builder.of((arg_0, arg_1) -> ThrownExperienceBottle.new(arg_0, arg_1), MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<ThrownPotion> POTION = EntityType.register("potion", Builder.of((arg_0, arg_1) -> ThrownPotion.new(arg_0, arg_1), MobCategory.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<ThrownTrident> TRIDENT = EntityType.register("trident", Builder.of((arg_0, arg_1) -> ThrownTrident.new(arg_0, arg_1), MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20));
    public static final EntityType<TraderLlama> TRADER_LLAMA = EntityType.register("trader_llama", Builder.of((arg_0, arg_1) -> TraderLlama.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.9f, 1.87f).clientTrackingRange(10));
    public static final EntityType<TropicalFish> TROPICAL_FISH = EntityType.register("tropical_fish", Builder.of((arg_0, arg_1) -> TropicalFish.new(arg_0, arg_1), MobCategory.WATER_AMBIENT).sized(0.5f, 0.4f).clientTrackingRange(4));
    public static final EntityType<Turtle> TURTLE = EntityType.register("turtle", Builder.of((arg_0, arg_1) -> Turtle.new(arg_0, arg_1), MobCategory.CREATURE).sized(1.2f, 0.4f).clientTrackingRange(10));
    public static final EntityType<Vex> VEX = EntityType.register("vex", Builder.of((arg_0, arg_1) -> Vex.new(arg_0, arg_1), MobCategory.MONSTER).fireImmune().sized(0.4f, 0.8f).clientTrackingRange(8));
    public static final EntityType<Villager> VILLAGER = EntityType.register("villager", Builder.of((arg_0, arg_1) -> Villager.new(arg_0, arg_1), MobCategory.MISC).sized(0.6f, 1.95f).clientTrackingRange(10));
    public static final EntityType<Vindicator> VINDICATOR = EntityType.register("vindicator", Builder.of((arg_0, arg_1) -> Vindicator.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<WanderingTrader> WANDERING_TRADER = EntityType.register("wandering_trader", Builder.of((arg_0, arg_1) -> WanderingTrader.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.6f, 1.95f).clientTrackingRange(10));
    public static final EntityType<Witch> WITCH = EntityType.register("witch", Builder.of((arg_0, arg_1) -> Witch.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<WitherBoss> WITHER = EntityType.register("wither", Builder.of((arg_0, arg_1) -> WitherBoss.new(arg_0, arg_1), MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.9f, 3.5f).clientTrackingRange(10));
    public static final EntityType<WitherSkeleton> WITHER_SKELETON = EntityType.register("wither_skeleton", Builder.of((arg_0, arg_1) -> WitherSkeleton.new(arg_0, arg_1), MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.7f, 2.4f).clientTrackingRange(8));
    public static final EntityType<WitherSkull> WITHER_SKULL = EntityType.register("wither_skull", Builder.of((arg_0, arg_1) -> WitherSkull.new(arg_0, arg_1), MobCategory.MISC).sized(0.3125f, 0.3125f).clientTrackingRange(4).updateInterval(10));
    public static final EntityType<Wolf> WOLF = EntityType.register("wolf", Builder.of((arg_0, arg_1) -> Wolf.new(arg_0, arg_1), MobCategory.CREATURE).sized(0.6f, 0.85f).clientTrackingRange(10));
    public static final EntityType<Zoglin> ZOGLIN = EntityType.register("zoglin", Builder.of((arg_0, arg_1) -> Zoglin.new(arg_0, arg_1), MobCategory.MONSTER).fireImmune().sized(1.3964844f, 1.4f).clientTrackingRange(8));
    public static final EntityType<Zombie> ZOMBIE = EntityType.register("zombie", Builder.of((arg_0, arg_1) -> Zombie.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<ZombieHorse> ZOMBIE_HORSE = EntityType.register("zombie_horse", Builder.of((arg_0, arg_1) -> ZombieHorse.new(arg_0, arg_1), MobCategory.CREATURE).sized(1.3964844f, 1.6f).clientTrackingRange(10));
    public static final EntityType<ZombieVillager> ZOMBIE_VILLAGER = EntityType.register("zombie_villager", Builder.of((arg_0, arg_1) -> ZombieVillager.new(arg_0, arg_1), MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<ZombifiedPiglin> ZOMBIFIED_PIGLIN = EntityType.register("zombified_piglin", Builder.of((arg_0, arg_1) -> ZombifiedPiglin.new(arg_0, arg_1), MobCategory.MONSTER).fireImmune().sized(0.6f, 1.95f).clientTrackingRange(8));
    public static final EntityType<Player> PLAYER = EntityType.register("player", Builder.createNothing(MobCategory.MISC).noSave().noSummon().sized(0.6f, 1.8f).clientTrackingRange(32).updateInterval(2));
    public static final EntityType<FishingHook> FISHING_BOBBER = EntityType.register("fishing_bobber", Builder.createNothing(MobCategory.MISC).noSave().noSummon().sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(5));
    private final EntityFactory<T> factory;
    private final MobCategory category;
    private final ImmutableSet<Block> immuneTo;
    private final boolean serialize;
    private final boolean summon;
    private final boolean fireImmune;
    private final boolean canSpawnFarFromPlayer;
    private final int clientTrackingRange;
    private final int updateInterval;
    @Nullable
    private String descriptionId;
    @Nullable
    private Component description;
    @Nullable
    private ResourceLocation lootTable;
    private final EntityDimensions dimensions;

    private static <T extends Entity> EntityType<T> register(String string, Builder<T> builder) {
        return Registry.register(Registry.ENTITY_TYPE, string, builder.build(string));
    }

    public static ResourceLocation getKey(EntityType<?> entityType) {
        return Registry.ENTITY_TYPE.getKey(entityType);
    }

    public static Optional<EntityType<?>> byString(String string) {
        return Registry.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(string));
    }

    public EntityType(EntityFactory<T> entityFactory, MobCategory mobCategory, boolean bl, boolean bl2, boolean bl3, boolean bl4, ImmutableSet<Block> immutableSet, EntityDimensions entityDimensions, int n, int n2) {
        this.factory = entityFactory;
        this.category = mobCategory;
        this.canSpawnFarFromPlayer = bl4;
        this.serialize = bl;
        this.summon = bl2;
        this.fireImmune = bl3;
        this.immuneTo = immutableSet;
        this.dimensions = entityDimensions;
        this.clientTrackingRange = n;
        this.updateInterval = n2;
    }

    @Nullable
    public Entity spawn(ServerLevel serverLevel, @Nullable ItemStack itemStack, @Nullable Player player, BlockPos blockPos, MobSpawnType mobSpawnType, boolean bl, boolean bl2) {
        return this.spawn(serverLevel, itemStack == null ? null : itemStack.getTag(), itemStack != null && itemStack.hasCustomHoverName() ? itemStack.getHoverName() : null, player, blockPos, mobSpawnType, bl, bl2);
    }

    @Nullable
    public T spawn(ServerLevel serverLevel, @Nullable CompoundTag compoundTag, @Nullable Component component, @Nullable Player player, BlockPos blockPos, MobSpawnType mobSpawnType, boolean bl, boolean bl2) {
        T t = this.create(serverLevel, compoundTag, component, player, blockPos, mobSpawnType, bl, bl2);
        if (t != null) {
            serverLevel.addFreshEntityWithPassengers((Entity)t);
        }
        return t;
    }

    @Nullable
    public T create(ServerLevel serverLevel, @Nullable CompoundTag compoundTag, @Nullable Component component, @Nullable Player player, BlockPos blockPos, MobSpawnType mobSpawnType, boolean bl, boolean bl2) {
        double d;
        T t = this.create(serverLevel);
        if (t == null) {
            return null;
        }
        if (bl) {
            ((Entity)t).setPos((double)blockPos.getX() + 0.5, blockPos.getY() + 1, (double)blockPos.getZ() + 0.5);
            d = EntityType.getYOffset(serverLevel, blockPos, bl2, ((Entity)t).getBoundingBox());
        } else {
            d = 0.0;
        }
        ((Entity)t).moveTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + d, (double)blockPos.getZ() + 0.5, Mth.wrapDegrees(serverLevel.random.nextFloat() * 360.0f), 0.0f);
        if (t instanceof Mob) {
            Mob mob = (Mob)t;
            mob.yHeadRot = mob.yRot;
            mob.yBodyRot = mob.yRot;
            mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(mob.blockPosition()), mobSpawnType, null, compoundTag);
            mob.playAmbientSound();
        }
        if (component != null && t instanceof LivingEntity) {
            ((Entity)t).setCustomName(component);
        }
        EntityType.updateCustomEntityTag(serverLevel, player, t, compoundTag);
        return t;
    }

    protected static double getYOffset(LevelReader levelReader, BlockPos blockPos, boolean bl, AABB aABB) {
        AABB aABB2 = new AABB(blockPos);
        if (bl) {
            aABB2 = aABB2.expandTowards(0.0, -1.0, 0.0);
        }
        Stream<VoxelShape> stream = levelReader.getCollisions(null, aABB2, entity -> true);
        return 1.0 + Shapes.collide(Direction.Axis.Y, aABB, stream, bl ? -2.0 : -1.0);
    }

    public static void updateCustomEntityTag(Level level, @Nullable Player player, @Nullable Entity entity, @Nullable CompoundTag compoundTag) {
        if (compoundTag == null || !compoundTag.contains("EntityTag", 10)) {
            return;
        }
        MinecraftServer minecraftServer = level.getServer();
        if (minecraftServer == null || entity == null) {
            return;
        }
        if (!(level.isClientSide || !entity.onlyOpCanSetNbt() || player != null && minecraftServer.getPlayerList().isOp(player.getGameProfile()))) {
            return;
        }
        CompoundTag compoundTag2 = entity.saveWithoutId(new CompoundTag());
        UUID uUID = entity.getUUID();
        compoundTag2.merge(compoundTag.getCompound("EntityTag"));
        entity.setUUID(uUID);
        entity.load(compoundTag2);
    }

    public boolean canSerialize() {
        return this.serialize;
    }

    public boolean canSummon() {
        return this.summon;
    }

    public boolean fireImmune() {
        return this.fireImmune;
    }

    public boolean canSpawnFarFromPlayer() {
        return this.canSpawnFarFromPlayer;
    }

    public MobCategory getCategory() {
        return this.category;
    }

    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("entity", Registry.ENTITY_TYPE.getKey(this));
        }
        return this.descriptionId;
    }

    public Component getDescription() {
        if (this.description == null) {
            this.description = new TranslatableComponent(this.getDescriptionId());
        }
        return this.description;
    }

    public String toString() {
        return this.getDescriptionId();
    }

    public ResourceLocation getDefaultLootTable() {
        if (this.lootTable == null) {
            ResourceLocation resourceLocation = Registry.ENTITY_TYPE.getKey(this);
            this.lootTable = new ResourceLocation(resourceLocation.getNamespace(), "entities/" + resourceLocation.getPath());
        }
        return this.lootTable;
    }

    public float getWidth() {
        return this.dimensions.width;
    }

    public float getHeight() {
        return this.dimensions.height;
    }

    @Nullable
    public T create(Level level) {
        return this.factory.create(this, level);
    }

    @Nullable
    public static Entity create(int n, Level level) {
        return EntityType.create(level, Registry.ENTITY_TYPE.byId(n));
    }

    public static Optional<Entity> create(CompoundTag compoundTag, Level level) {
        return Util.ifElse(EntityType.by(compoundTag).map(entityType -> entityType.create(level)), entity -> entity.load(compoundTag), () -> LOGGER.warn("Skipping Entity with id {}", (Object)compoundTag.getString("id")));
    }

    @Nullable
    private static Entity create(Level level, @Nullable EntityType<?> entityType) {
        return entityType == null ? null : (Entity)entityType.create(level);
    }

    public AABB getAABB(double d, double d2, double d3) {
        float f = this.getWidth() / 2.0f;
        return new AABB(d - (double)f, d2, d3 - (double)f, d + (double)f, d2 + (double)this.getHeight(), d3 + (double)f);
    }

    public boolean isBlockDangerous(BlockState blockState) {
        if (this.immuneTo.contains((Object)blockState.getBlock())) {
            return false;
        }
        if (!this.fireImmune && (blockState.is(BlockTags.FIRE) || blockState.is(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(blockState) || blockState.is(Blocks.LAVA))) {
            return true;
        }
        return blockState.is(Blocks.WITHER_ROSE) || blockState.is(Blocks.SWEET_BERRY_BUSH) || blockState.is(Blocks.CACTUS);
    }

    public EntityDimensions getDimensions() {
        return this.dimensions;
    }

    public static Optional<EntityType<?>> by(CompoundTag compoundTag) {
        return Registry.ENTITY_TYPE.getOptional(new ResourceLocation(compoundTag.getString("id")));
    }

    @Nullable
    public static Entity loadEntityRecursive(CompoundTag compoundTag, Level level, Function<Entity, Entity> function) {
        return EntityType.loadStaticEntity(compoundTag, level).map(function).map(entity -> {
            if (compoundTag.contains("Passengers", 9)) {
                ListTag listTag = compoundTag.getList("Passengers", 10);
                for (int i = 0; i < listTag.size(); ++i) {
                    Entity entity2 = EntityType.loadEntityRecursive(listTag.getCompound(i), level, function);
                    if (entity2 == null) continue;
                    entity2.startRiding((Entity)entity, true);
                }
            }
            return entity;
        }).orElse(null);
    }

    private static Optional<Entity> loadStaticEntity(CompoundTag compoundTag, Level level) {
        try {
            return EntityType.create(compoundTag, level);
        }
        catch (RuntimeException runtimeException) {
            LOGGER.warn("Exception loading entity: ", (Throwable)runtimeException);
            return Optional.empty();
        }
    }

    public int clientTrackingRange() {
        return this.clientTrackingRange;
    }

    public int updateInterval() {
        return this.updateInterval;
    }

    public boolean trackDeltas() {
        return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
    }

    public boolean is(Tag<EntityType<?>> tag) {
        return tag.contains(this);
    }

    public static interface EntityFactory<T extends Entity> {
        public T create(EntityType<T> var1, Level var2);
    }

    public static class Builder<T extends Entity> {
        private final EntityFactory<T> factory;
        private final MobCategory category;
        private ImmutableSet<Block> immuneTo = ImmutableSet.of();
        private boolean serialize = true;
        private boolean summon = true;
        private boolean fireImmune;
        private boolean canSpawnFarFromPlayer;
        private int clientTrackingRange = 5;
        private int updateInterval = 3;
        private EntityDimensions dimensions = EntityDimensions.scalable(0.6f, 1.8f);

        private Builder(EntityFactory<T> entityFactory, MobCategory mobCategory) {
            this.factory = entityFactory;
            this.category = mobCategory;
            this.canSpawnFarFromPlayer = mobCategory == MobCategory.CREATURE || mobCategory == MobCategory.MISC;
        }

        public static <T extends Entity> Builder<T> of(EntityFactory<T> entityFactory, MobCategory mobCategory) {
            return new Builder<T>(entityFactory, mobCategory);
        }

        public static <T extends Entity> Builder<T> createNothing(MobCategory mobCategory) {
            return new Builder<Entity>((entityType, level) -> null, mobCategory);
        }

        public Builder<T> sized(float f, float f2) {
            this.dimensions = EntityDimensions.scalable(f, f2);
            return this;
        }

        public Builder<T> noSummon() {
            this.summon = false;
            return this;
        }

        public Builder<T> noSave() {
            this.serialize = false;
            return this;
        }

        public Builder<T> fireImmune() {
            this.fireImmune = true;
            return this;
        }

        public Builder<T> immuneTo(Block ... arrblock) {
            this.immuneTo = ImmutableSet.copyOf((Object[])arrblock);
            return this;
        }

        public Builder<T> canSpawnFarFromPlayer() {
            this.canSpawnFarFromPlayer = true;
            return this;
        }

        public Builder<T> clientTrackingRange(int n) {
            this.clientTrackingRange = n;
            return this;
        }

        public Builder<T> updateInterval(int n) {
            this.updateInterval = n;
            return this;
        }

        public EntityType<T> build(String string) {
            if (this.serialize) {
                Util.fetchChoiceType(References.ENTITY_TREE, string);
            }
            return new EntityType<T>(this.factory, this.category, this.serialize, this.summon, this.fireImmune, this.canSpawnFarFromPlayer, this.immuneTo, this.dimensions, this.clientTrackingRange, this.updateInterval);
        }
    }

}

