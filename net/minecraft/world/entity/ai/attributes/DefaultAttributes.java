/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.AbstractFish;
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
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.AbstractSkeleton;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultAttributes {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<EntityType<? extends LivingEntity>, AttributeSupplier> SUPPLIERS = ImmutableMap.builder().put(EntityType.ARMOR_STAND, (Object)LivingEntity.createLivingAttributes().build()).put(EntityType.BAT, (Object)Bat.createAttributes().build()).put(EntityType.BEE, (Object)Bee.createAttributes().build()).put(EntityType.BLAZE, (Object)Blaze.createAttributes().build()).put(EntityType.CAT, (Object)Cat.createAttributes().build()).put(EntityType.CAVE_SPIDER, (Object)CaveSpider.createCaveSpider().build()).put(EntityType.CHICKEN, (Object)Chicken.createAttributes().build()).put(EntityType.COD, (Object)AbstractFish.createAttributes().build()).put(EntityType.COW, (Object)Cow.createAttributes().build()).put(EntityType.CREEPER, (Object)Creeper.createAttributes().build()).put(EntityType.DOLPHIN, (Object)Dolphin.createAttributes().build()).put(EntityType.DONKEY, (Object)AbstractChestedHorse.createBaseChestedHorseAttributes().build()).put(EntityType.DROWNED, (Object)Zombie.createAttributes().build()).put(EntityType.ELDER_GUARDIAN, (Object)ElderGuardian.createAttributes().build()).put(EntityType.ENDERMAN, (Object)EnderMan.createAttributes().build()).put(EntityType.ENDERMITE, (Object)Endermite.createAttributes().build()).put(EntityType.ENDER_DRAGON, (Object)EnderDragon.createAttributes().build()).put(EntityType.EVOKER, (Object)Evoker.createAttributes().build()).put(EntityType.FOX, (Object)Fox.createAttributes().build()).put(EntityType.GHAST, (Object)Ghast.createAttributes().build()).put(EntityType.GIANT, (Object)Giant.createAttributes().build()).put(EntityType.GUARDIAN, (Object)Guardian.createAttributes().build()).put(EntityType.HOGLIN, (Object)Hoglin.createAttributes().build()).put(EntityType.HORSE, (Object)AbstractHorse.createBaseHorseAttributes().build()).put(EntityType.HUSK, (Object)Zombie.createAttributes().build()).put(EntityType.ILLUSIONER, (Object)Illusioner.createAttributes().build()).put(EntityType.IRON_GOLEM, (Object)IronGolem.createAttributes().build()).put(EntityType.LLAMA, (Object)Llama.createAttributes().build()).put(EntityType.MAGMA_CUBE, (Object)MagmaCube.createAttributes().build()).put(EntityType.MOOSHROOM, (Object)Cow.createAttributes().build()).put(EntityType.MULE, (Object)AbstractChestedHorse.createBaseChestedHorseAttributes().build()).put(EntityType.OCELOT, (Object)Ocelot.createAttributes().build()).put(EntityType.PANDA, (Object)Panda.createAttributes().build()).put(EntityType.PARROT, (Object)Parrot.createAttributes().build()).put(EntityType.PHANTOM, (Object)Monster.createMonsterAttributes().build()).put(EntityType.PIG, (Object)Pig.createAttributes().build()).put(EntityType.PIGLIN, (Object)Piglin.createAttributes().build()).put(EntityType.PIGLIN_BRUTE, (Object)PiglinBrute.createAttributes().build()).put(EntityType.PILLAGER, (Object)Pillager.createAttributes().build()).put(EntityType.PLAYER, (Object)Player.createAttributes().build()).put(EntityType.POLAR_BEAR, (Object)PolarBear.createAttributes().build()).put(EntityType.PUFFERFISH, (Object)AbstractFish.createAttributes().build()).put(EntityType.RABBIT, (Object)Rabbit.createAttributes().build()).put(EntityType.RAVAGER, (Object)Ravager.createAttributes().build()).put(EntityType.SALMON, (Object)AbstractFish.createAttributes().build()).put(EntityType.SHEEP, (Object)Sheep.createAttributes().build()).put(EntityType.SHULKER, (Object)Shulker.createAttributes().build()).put(EntityType.SILVERFISH, (Object)Silverfish.createAttributes().build()).put(EntityType.SKELETON, (Object)AbstractSkeleton.createAttributes().build()).put(EntityType.SKELETON_HORSE, (Object)SkeletonHorse.createAttributes().build()).put(EntityType.SLIME, (Object)Monster.createMonsterAttributes().build()).put(EntityType.SNOW_GOLEM, (Object)SnowGolem.createAttributes().build()).put(EntityType.SPIDER, (Object)Spider.createAttributes().build()).put(EntityType.SQUID, (Object)Squid.createAttributes().build()).put(EntityType.STRAY, (Object)AbstractSkeleton.createAttributes().build()).put(EntityType.STRIDER, (Object)Strider.createAttributes().build()).put(EntityType.TRADER_LLAMA, (Object)Llama.createAttributes().build()).put(EntityType.TROPICAL_FISH, (Object)AbstractFish.createAttributes().build()).put(EntityType.TURTLE, (Object)Turtle.createAttributes().build()).put(EntityType.VEX, (Object)Vex.createAttributes().build()).put(EntityType.VILLAGER, (Object)Villager.createAttributes().build()).put(EntityType.VINDICATOR, (Object)Vindicator.createAttributes().build()).put(EntityType.WANDERING_TRADER, (Object)Mob.createMobAttributes().build()).put(EntityType.WITCH, (Object)Witch.createAttributes().build()).put(EntityType.WITHER, (Object)WitherBoss.createAttributes().build()).put(EntityType.WITHER_SKELETON, (Object)AbstractSkeleton.createAttributes().build()).put(EntityType.WOLF, (Object)Wolf.createAttributes().build()).put(EntityType.ZOGLIN, (Object)Zoglin.createAttributes().build()).put(EntityType.ZOMBIE, (Object)Zombie.createAttributes().build()).put(EntityType.ZOMBIE_HORSE, (Object)ZombieHorse.createAttributes().build()).put(EntityType.ZOMBIE_VILLAGER, (Object)Zombie.createAttributes().build()).put(EntityType.ZOMBIFIED_PIGLIN, (Object)ZombifiedPiglin.createAttributes().build()).build();

    public static AttributeSupplier getSupplier(EntityType<? extends LivingEntity> entityType) {
        return SUPPLIERS.get(entityType);
    }

    public static boolean hasSupplier(EntityType<?> entityType) {
        return SUPPLIERS.containsKey(entityType);
    }

    public static void validate() {
        Registry.ENTITY_TYPE.stream().filter(entityType -> entityType.getCategory() != MobCategory.MISC).filter(entityType -> !DefaultAttributes.hasSupplier(entityType)).map(Registry.ENTITY_TYPE::getKey).forEach(resourceLocation -> {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                throw new IllegalStateException("Entity " + resourceLocation + " has no attributes");
            }
            LOGGER.error("Entity {} has no attributes", resourceLocation);
        });
    }
}

