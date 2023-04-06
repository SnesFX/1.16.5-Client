/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
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
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.PatrollingMonster;
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
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;

public class SpawnPlacements {
    private static final Map<EntityType<?>, Data> DATA_BY_TYPE = Maps.newHashMap();

    private static <T extends Mob> void register(EntityType<T> entityType, Type type, Heightmap.Types types, SpawnPredicate<T> spawnPredicate) {
        Data data = DATA_BY_TYPE.put(entityType, new Data(types, type, spawnPredicate));
        if (data != null) {
            throw new IllegalStateException("Duplicate registration for type " + Registry.ENTITY_TYPE.getKey(entityType));
        }
    }

    public static Type getPlacementType(EntityType<?> entityType) {
        Data data = DATA_BY_TYPE.get(entityType);
        return data == null ? Type.NO_RESTRICTIONS : data.placement;
    }

    public static Heightmap.Types getHeightmapType(@Nullable EntityType<?> entityType) {
        Data data = DATA_BY_TYPE.get(entityType);
        return data == null ? Heightmap.Types.MOTION_BLOCKING_NO_LEAVES : data.heightMap;
    }

    public static <T extends Entity> boolean checkSpawnRules(EntityType<T> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
        Data data = DATA_BY_TYPE.get(entityType);
        return data == null || data.predicate.test(entityType, serverLevelAccessor, mobSpawnType, blockPos, random);
    }

    static {
        SpawnPlacements.register(EntityType.COD, Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> AbstractFish.checkFishSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.DOLPHIN, Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Dolphin.checkDolphinSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.DROWNED, Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Drowned.checkDrownedSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.GUARDIAN, Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Guardian.checkGuardianSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.PUFFERFISH, Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> AbstractFish.checkFishSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.SALMON, Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> AbstractFish.checkFishSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.SQUID, Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Squid.checkSquidSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.TROPICAL_FISH, Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> AbstractFish.checkFishSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.BAT, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Bat.checkBatSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.BLAZE, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkAnyLightMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.CAVE_SPIDER, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.CHICKEN, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.COW, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.CREEPER, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.DONKEY, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.ENDERMAN, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.ENDERMITE, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Endermite.checkEndermiteSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.ENDER_DRAGON, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Mob.checkMobSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.GHAST, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Ghast.checkGhastSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.GIANT, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.HORSE, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.HUSK, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Husk.checkHuskSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.IRON_GOLEM, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Mob.checkMobSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.LLAMA, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.MAGMA_CUBE, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> MagmaCube.checkMagmaCubeSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.MOOSHROOM, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> MushroomCow.checkMushroomSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.MULE, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.OCELOT, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Ocelot.checkOcelotSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.PARROT, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Parrot.checkParrotSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.PIG, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.HOGLIN, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Hoglin.checkHoglinSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.PIGLIN, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Piglin.checkPiglinSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.PILLAGER, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> PatrollingMonster.checkPatrollingMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.POLAR_BEAR, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> PolarBear.checkPolarBearSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.RABBIT, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Rabbit.checkRabbitSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.SHEEP, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.SILVERFISH, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Silverfish.checkSliverfishSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.SKELETON, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.SKELETON_HORSE, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.SLIME, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Slime.checkSlimeSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.SNOW_GOLEM, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Mob.checkMobSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.SPIDER, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.STRAY, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Stray.checkStraySpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.STRIDER, Type.IN_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Strider.checkStriderSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.TURTLE, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Turtle.checkTurtleSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.VILLAGER, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Mob.checkMobSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.WITCH, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.WITHER, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.WITHER_SKELETON, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.WOLF, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.ZOMBIE, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.ZOMBIE_HORSE, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.ZOMBIFIED_PIGLIN, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> ZombifiedPiglin.checkZombifiedPiglinSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.ZOMBIE_VILLAGER, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.CAT, Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.ELDER_GUARDIAN, Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Guardian.checkGuardianSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.EVOKER, Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.FOX, Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.ILLUSIONER, Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.PANDA, Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.PHANTOM, Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Mob.checkMobSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.RAVAGER, Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.SHULKER, Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Mob.checkMobSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.TRADER_LLAMA, Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Animal.checkAnimalSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.VEX, Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.VINDICATOR, Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Monster.checkMonsterSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
        SpawnPlacements.register(EntityType.WANDERING_TRADER, Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) -> Mob.checkMobSpawnRules(arg_0, arg_1, arg_2, arg_3, arg_4));
    }

    public static enum Type {
        ON_GROUND,
        IN_WATER,
        NO_RESTRICTIONS,
        IN_LAVA;
        
    }

    static class Data {
        private final Heightmap.Types heightMap;
        private final Type placement;
        private final SpawnPredicate<?> predicate;

        public Data(Heightmap.Types types, Type type, SpawnPredicate<?> spawnPredicate) {
            this.heightMap = types;
            this.placement = type;
            this.predicate = spawnPredicate;
        }
    }

    @FunctionalInterface
    public static interface SpawnPredicate<T extends Entity> {
        public boolean test(EntityType<T> var1, ServerLevelAccessor var2, MobSpawnType var3, BlockPos var4, Random var5);
    }

}

