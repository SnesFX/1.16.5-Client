/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package net.minecraft.data.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;

public class EntityLoot
implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
    private static final EntityPredicate.Builder ENTITY_ON_FIRE = EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true).build());
    private static final Set<EntityType<?>> SPECIAL_LOOT_TABLE_TYPES = ImmutableSet.of(EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER);
    private final Map<ResourceLocation, LootTable.Builder> map = Maps.newHashMap();

    private static LootTable.Builder createSheepTable(ItemLike itemLike) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(itemLike))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootTableReference.lootTableReference(EntityType.SHEEP.getDefaultLootTable())));
    }

    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
        this.add(EntityType.ARMOR_STAND, LootTable.lootTable());
        this.add(EntityType.BAT, LootTable.lootTable());
        this.add(EntityType.BEE, LootTable.lootTable());
        this.add(EntityType.BLAZE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.BLAZE_ROD).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).when(LootItemKilledByPlayerCondition.killedByPlayer())));
        this.add(EntityType.CAT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.STRING).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f))))));
        this.add(EntityType.CAVE_SPIDER, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.STRING).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.SPIDER_EYE).apply(SetItemCountFunction.setCount(RandomValueBounds.between(-1.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).when(LootItemKilledByPlayerCondition.killedByPlayer())));
        this.add(EntityType.CHICKEN, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.FEATHER).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.CHICKEN).apply((LootItemFunction.Builder)SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.COD, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.COD).apply((LootItemFunction.Builder)SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.BONE_MEAL)).when(LootItemRandomChanceCondition.randomChance(0.05f))));
        this.add(EntityType.COW, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.LEATHER).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.BEEF).apply(SetItemCountFunction.setCount(RandomValueBounds.between(1.0f, 3.0f)))).apply((LootItemFunction.Builder)SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.CREEPER, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.GUNPOWDER).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().add(TagEntry.expandTag(ItemTags.CREEPER_DROP_MUSIC_DISCS)).when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.KILLER, EntityPredicate.Builder.entity().of(EntityTypeTags.SKELETONS)))));
        this.add(EntityType.DOLPHIN, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.COD).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).apply((LootItemFunction.Builder)SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE))))));
        this.add(EntityType.DONKEY, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.LEATHER).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.DROWNED, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.GOLD_INGOT)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.05f, 0.01f))));
        this.add(EntityType.ELDER_GUARDIAN, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.PRISMARINE_SHARD).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.COD).setWeight(3)).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).apply((LootItemFunction.Builder)SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS).setWeight(2)).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).add(EmptyLootItem.emptyItem())).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Blocks.WET_SPONGE)).when(LootItemKilledByPlayerCondition.killedByPlayer())).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootTableReference.lootTableReference(BuiltInLootTables.FISHING_FISH)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.025f, 0.01f))));
        this.add(EntityType.ENDER_DRAGON, LootTable.lootTable());
        this.add(EntityType.ENDERMAN, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.ENDER_PEARL).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.ENDERMITE, LootTable.lootTable());
        this.add(EntityType.EVOKER, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.TOTEM_OF_UNDYING))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).when(LootItemKilledByPlayerCondition.killedByPlayer())));
        this.add(EntityType.FOX, LootTable.lootTable());
        this.add(EntityType.GHAST, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.GHAST_TEAR).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.GUNPOWDER).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.GIANT, LootTable.lootTable());
        this.add(EntityType.GUARDIAN, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.PRISMARINE_SHARD).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.COD).setWeight(2)).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).apply((LootItemFunction.Builder)SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS).setWeight(2)).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).add(EmptyLootItem.emptyItem())).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootTableReference.lootTableReference(BuiltInLootTables.FISHING_FISH)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.025f, 0.01f))));
        this.add(EntityType.HORSE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.LEATHER).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.HUSK, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.IRON_INGOT)).add(LootItem.lootTableItem(Items.CARROT)).add(LootItem.lootTableItem(Items.POTATO)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.025f, 0.01f))));
        this.add(EntityType.RAVAGER, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.SADDLE).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(1))))));
        this.add(EntityType.ILLUSIONER, LootTable.lootTable());
        this.add(EntityType.IRON_GOLEM, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Blocks.POPPY).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.IRON_INGOT).apply(SetItemCountFunction.setCount(RandomValueBounds.between(3.0f, 5.0f))))));
        this.add(EntityType.LLAMA, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.LEATHER).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.MAGMA_CUBE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.MAGMA_CREAM).apply(SetItemCountFunction.setCount(RandomValueBounds.between(-2.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.MULE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.LEATHER).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.MOOSHROOM, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.LEATHER).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.BEEF).apply(SetItemCountFunction.setCount(RandomValueBounds.between(1.0f, 3.0f)))).apply((LootItemFunction.Builder)SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.OCELOT, LootTable.lootTable());
        this.add(EntityType.PANDA, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Blocks.BAMBOO).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(1))))));
        this.add(EntityType.PARROT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.FEATHER).apply(SetItemCountFunction.setCount(RandomValueBounds.between(1.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.PHANTOM, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.PHANTOM_MEMBRANE).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).when(LootItemKilledByPlayerCondition.killedByPlayer())));
        this.add(EntityType.PIG, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.PORKCHOP).apply(SetItemCountFunction.setCount(RandomValueBounds.between(1.0f, 3.0f)))).apply((LootItemFunction.Builder)SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.PILLAGER, LootTable.lootTable());
        this.add(EntityType.PLAYER, LootTable.lootTable());
        this.add(EntityType.POLAR_BEAR, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.COD).setWeight(3)).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.SALMON).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.PUFFERFISH, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.PUFFERFISH).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(1))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.BONE_MEAL)).when(LootItemRandomChanceCondition.randomChance(0.05f))));
        this.add(EntityType.RABBIT, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.RABBIT_HIDE).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.RABBIT).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 1.0f)))).apply((LootItemFunction.Builder)SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.RABBIT_FOOT)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.1f, 0.03f))));
        this.add(EntityType.SALMON, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.SALMON).apply((LootItemFunction.Builder)SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.BONE_MEAL)).when(LootItemRandomChanceCondition.randomChance(0.05f))));
        this.add(EntityType.SHEEP, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.MUTTON).apply(SetItemCountFunction.setCount(RandomValueBounds.between(1.0f, 2.0f)))).apply((LootItemFunction.Builder)SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(BuiltInLootTables.SHEEP_BLACK, EntityLoot.createSheepTable(Blocks.BLACK_WOOL));
        this.add(BuiltInLootTables.SHEEP_BLUE, EntityLoot.createSheepTable(Blocks.BLUE_WOOL));
        this.add(BuiltInLootTables.SHEEP_BROWN, EntityLoot.createSheepTable(Blocks.BROWN_WOOL));
        this.add(BuiltInLootTables.SHEEP_CYAN, EntityLoot.createSheepTable(Blocks.CYAN_WOOL));
        this.add(BuiltInLootTables.SHEEP_GRAY, EntityLoot.createSheepTable(Blocks.GRAY_WOOL));
        this.add(BuiltInLootTables.SHEEP_GREEN, EntityLoot.createSheepTable(Blocks.GREEN_WOOL));
        this.add(BuiltInLootTables.SHEEP_LIGHT_BLUE, EntityLoot.createSheepTable(Blocks.LIGHT_BLUE_WOOL));
        this.add(BuiltInLootTables.SHEEP_LIGHT_GRAY, EntityLoot.createSheepTable(Blocks.LIGHT_GRAY_WOOL));
        this.add(BuiltInLootTables.SHEEP_LIME, EntityLoot.createSheepTable(Blocks.LIME_WOOL));
        this.add(BuiltInLootTables.SHEEP_MAGENTA, EntityLoot.createSheepTable(Blocks.MAGENTA_WOOL));
        this.add(BuiltInLootTables.SHEEP_ORANGE, EntityLoot.createSheepTable(Blocks.ORANGE_WOOL));
        this.add(BuiltInLootTables.SHEEP_PINK, EntityLoot.createSheepTable(Blocks.PINK_WOOL));
        this.add(BuiltInLootTables.SHEEP_PURPLE, EntityLoot.createSheepTable(Blocks.PURPLE_WOOL));
        this.add(BuiltInLootTables.SHEEP_RED, EntityLoot.createSheepTable(Blocks.RED_WOOL));
        this.add(BuiltInLootTables.SHEEP_WHITE, EntityLoot.createSheepTable(Blocks.WHITE_WOOL));
        this.add(BuiltInLootTables.SHEEP_YELLOW, EntityLoot.createSheepTable(Blocks.YELLOW_WOOL));
        this.add(EntityType.SHULKER, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.SHULKER_SHELL)).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.5f, 0.0625f))));
        this.add(EntityType.SILVERFISH, LootTable.lootTable());
        this.add(EntityType.SKELETON, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.BONE).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.SKELETON_HORSE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.BONE).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.SLIME, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.SLIME_BALL).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.SNOW_GOLEM, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.SNOWBALL).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 15.0f))))));
        this.add(EntityType.SPIDER, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.STRING).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.SPIDER_EYE).apply(SetItemCountFunction.setCount(RandomValueBounds.between(-1.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).when(LootItemKilledByPlayerCondition.killedByPlayer())));
        this.add(EntityType.SQUID, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.INK_SAC).apply(SetItemCountFunction.setCount(RandomValueBounds.between(1.0f, 3.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.STRAY, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.BONE).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.TIPPED_ARROW).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)).setLimit(1))).apply(SetNbtFunction.setTag(Util.make(new CompoundTag(), compoundTag -> compoundTag.putString("Potion", "minecraft:slowness"))))).when(LootItemKilledByPlayerCondition.killedByPlayer())));
        this.add(EntityType.STRIDER, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.STRING).apply(SetItemCountFunction.setCount(RandomValueBounds.between(2.0f, 5.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.TRADER_LLAMA, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.LEATHER).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.TROPICAL_FISH, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(Items.TROPICAL_FISH).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(1))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.BONE_MEAL)).when(LootItemRandomChanceCondition.randomChance(0.05f))));
        this.add(EntityType.TURTLE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Blocks.SEAGRASS).setWeight(3)).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.BOWL)).when(DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().isLightning(true)))));
        this.add(EntityType.VEX, LootTable.lootTable());
        this.add(EntityType.VILLAGER, LootTable.lootTable());
        this.add(EntityType.WANDERING_TRADER, LootTable.lootTable());
        this.add(EntityType.VINDICATOR, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).when(LootItemKilledByPlayerCondition.killedByPlayer())));
        this.add(EntityType.WITCH, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(RandomValueBounds.between(1.0f, 3.0f)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.GLOWSTONE_DUST).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.SUGAR).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.REDSTONE).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.SPIDER_EYE).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.GLASS_BOTTLE).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.GUNPOWDER).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f)))).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.STICK).setWeight(2)).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.WITHER, LootTable.lootTable());
        this.add(EntityType.WITHER_SKELETON, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.COAL).apply(SetItemCountFunction.setCount(RandomValueBounds.between(-1.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.BONE).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Blocks.WITHER_SKELETON_SKULL)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.025f, 0.01f))));
        this.add(EntityType.WOLF, LootTable.lootTable());
        this.add(EntityType.ZOGLIN, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(SetItemCountFunction.setCount(RandomValueBounds.between(1.0f, 3.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.ZOMBIE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.IRON_INGOT)).add(LootItem.lootTableItem(Items.CARROT)).add(LootItem.lootTableItem(Items.POTATO)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.025f, 0.01f))));
        this.add(EntityType.ZOMBIE_HORSE, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.ZOMBIFIED_PIGLIN, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.GOLD_NUGGET).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.GOLD_INGOT)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.025f, 0.01f))));
        this.add(EntityType.HOGLIN, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.PORKCHOP).apply(SetItemCountFunction.setCount(RandomValueBounds.between(2.0f, 4.0f)))).apply((LootItemFunction.Builder)SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.LEATHER).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 1.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))));
        this.add(EntityType.PIGLIN, LootTable.lootTable());
        this.add(EntityType.PIGLIN_BRUTE, LootTable.lootTable());
        this.add(EntityType.ZOMBIE_VILLAGER, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder<?>)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.ROTTEN_FLESH).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0f, 2.0f)))).apply(LootingEnchantFunction.lootingMultiplier(RandomValueBounds.between(0.0f, 1.0f))))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.IRON_INGOT)).add(LootItem.lootTableItem(Items.CARROT)).add(LootItem.lootTableItem(Items.POTATO)).when(LootItemKilledByPlayerCondition.killedByPlayer()).when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.025f, 0.01f))));
        HashSet hashSet = Sets.newHashSet();
        for (EntityType entityType : Registry.ENTITY_TYPE) {
            ResourceLocation resourceLocation = entityType.getDefaultLootTable();
            if (SPECIAL_LOOT_TABLE_TYPES.contains(entityType) || entityType.getCategory() != MobCategory.MISC) {
                if (resourceLocation == BuiltInLootTables.EMPTY || !hashSet.add(resourceLocation)) continue;
                LootTable.Builder builder = this.map.remove(resourceLocation);
                if (builder == null) {
                    throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourceLocation, Registry.ENTITY_TYPE.getKey(entityType)));
                }
                biConsumer.accept(resourceLocation, builder);
                continue;
            }
            if (resourceLocation == BuiltInLootTables.EMPTY || this.map.remove(resourceLocation) == null) continue;
            throw new IllegalStateException(String.format("Weird loottable '%s' for '%s', not a LivingEntity so should not have loot", resourceLocation, Registry.ENTITY_TYPE.getKey(entityType)));
        }
        this.map.forEach((arg_0, arg_1) -> biConsumer.accept(arg_0, arg_1));
    }

    private void add(EntityType<?> entityType, LootTable.Builder builder) {
        this.add(entityType.getDefaultLootTable(), builder);
    }

    private void add(ResourceLocation resourceLocation, LootTable.Builder builder) {
        this.map.put(resourceLocation, builder);
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((BiConsumer)object);
    }
}

