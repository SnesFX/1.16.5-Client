/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.data.advancements;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.BeeNestDestroyedTrigger;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnBlockTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class HusbandryAdvancements
implements Consumer<Consumer<Advancement>> {
    private static final EntityType<?>[] BREEDABLE_ANIMALS = new EntityType[]{EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.CAT, EntityType.PANDA, EntityType.FOX, EntityType.BEE, EntityType.HOGLIN, EntityType.STRIDER};
    private static final Item[] FISH = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
    private static final Item[] FISH_BUCKETS = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
    private static final Item[] EDIBLE_ITEMS = new Item[]{Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES, Items.HONEY_BOTTLE};

    @Override
    public void accept(Consumer<Advancement> consumer) {
        Advancement advancement = Advancement.Builder.advancement().display(Blocks.HAY_BLOCK, (Component)new TranslatableComponent("advancements.husbandry.root.title"), (Component)new TranslatableComponent("advancements.husbandry.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/husbandry.png"), FrameType.TASK, false, false, false).addCriterion("consumed_item", ConsumeItemTrigger.TriggerInstance.usedItem()).save(consumer, "husbandry/root");
        Advancement advancement2 = Advancement.Builder.advancement().parent(advancement).display(Items.WHEAT, (Component)new TranslatableComponent("advancements.husbandry.plant_seed.title"), (Component)new TranslatableComponent("advancements.husbandry.plant_seed.description"), null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("wheat", PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.WHEAT)).addCriterion("pumpkin_stem", PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.PUMPKIN_STEM)).addCriterion("melon_stem", PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.MELON_STEM)).addCriterion("beetroots", PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.BEETROOTS)).addCriterion("nether_wart", PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.NETHER_WART)).save(consumer, "husbandry/plant_seed");
        Advancement advancement3 = Advancement.Builder.advancement().parent(advancement).display(Items.WHEAT, (Component)new TranslatableComponent("advancements.husbandry.breed_an_animal.title"), (Component)new TranslatableComponent("advancements.husbandry.breed_an_animal.description"), null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("bred", BredAnimalsTrigger.TriggerInstance.bredAnimals()).save(consumer, "husbandry/breed_an_animal");
        this.addFood(Advancement.Builder.advancement()).parent(advancement2).display(Items.APPLE, (Component)new TranslatableComponent("advancements.husbandry.balanced_diet.title"), (Component)new TranslatableComponent("advancements.husbandry.balanced_diet.description"), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(consumer, "husbandry/balanced_diet");
        Advancement.Builder.advancement().parent(advancement2).display(Items.NETHERITE_HOE, (Component)new TranslatableComponent("advancements.husbandry.netherite_hoe.title"), (Component)new TranslatableComponent("advancements.husbandry.netherite_hoe.description"), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("netherite_hoe", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_HOE)).save(consumer, "husbandry/obtain_netherite_hoe");
        Advancement advancement4 = Advancement.Builder.advancement().parent(advancement).display(Items.LEAD, (Component)new TranslatableComponent("advancements.husbandry.tame_an_animal.title"), (Component)new TranslatableComponent("advancements.husbandry.tame_an_animal.description"), null, FrameType.TASK, true, true, false).addCriterion("tamed_animal", TameAnimalTrigger.TriggerInstance.tamedAnimal()).save(consumer, "husbandry/tame_an_animal");
        this.addBreedable(Advancement.Builder.advancement()).parent(advancement3).display(Items.GOLDEN_CARROT, (Component)new TranslatableComponent("advancements.husbandry.breed_all_animals.title"), (Component)new TranslatableComponent("advancements.husbandry.breed_all_animals.description"), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(consumer, "husbandry/bred_all_animals");
        Advancement advancement5 = this.addFish(Advancement.Builder.advancement()).parent(advancement).requirements(RequirementsStrategy.OR).display(Items.FISHING_ROD, (Component)new TranslatableComponent("advancements.husbandry.fishy_business.title"), (Component)new TranslatableComponent("advancements.husbandry.fishy_business.description"), null, FrameType.TASK, true, true, false).save(consumer, "husbandry/fishy_business");
        this.addFishBuckets(Advancement.Builder.advancement()).parent(advancement5).requirements(RequirementsStrategy.OR).display(Items.PUFFERFISH_BUCKET, (Component)new TranslatableComponent("advancements.husbandry.tactical_fishing.title"), (Component)new TranslatableComponent("advancements.husbandry.tactical_fishing.description"), null, FrameType.TASK, true, true, false).save(consumer, "husbandry/tactical_fishing");
        this.addCatVariants(Advancement.Builder.advancement()).parent(advancement4).display(Items.COD, (Component)new TranslatableComponent("advancements.husbandry.complete_catalogue.title"), (Component)new TranslatableComponent("advancements.husbandry.complete_catalogue.description"), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).save(consumer, "husbandry/complete_catalogue");
        Advancement.Builder.advancement().parent(advancement).addCriterion("safely_harvest_honey", ItemUsedOnBlockTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockTags.BEEHIVES).build()).setSmokey(true), ItemPredicate.Builder.item().of(Items.GLASS_BOTTLE))).display(Items.HONEY_BOTTLE, (Component)new TranslatableComponent("advancements.husbandry.safely_harvest_honey.title"), (Component)new TranslatableComponent("advancements.husbandry.safely_harvest_honey.description"), null, FrameType.TASK, true, true, false).save(consumer, "husbandry/safely_harvest_honey");
        Advancement.Builder.advancement().parent(advancement).addCriterion("silk_touch_nest", BeeNestDestroyedTrigger.TriggerInstance.destroyedBeeNest(Blocks.BEE_NEST, ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))), MinMaxBounds.Ints.exactly(3))).display(Blocks.BEE_NEST, (Component)new TranslatableComponent("advancements.husbandry.silk_touch_nest.title"), (Component)new TranslatableComponent("advancements.husbandry.silk_touch_nest.description"), null, FrameType.TASK, true, true, false).save(consumer, "husbandry/silk_touch_nest");
    }

    private Advancement.Builder addFood(Advancement.Builder builder) {
        for (Item item : EDIBLE_ITEMS) {
            builder.addCriterion(Registry.ITEM.getKey(item).getPath(), ConsumeItemTrigger.TriggerInstance.usedItem(item));
        }
        return builder;
    }

    private Advancement.Builder addBreedable(Advancement.Builder builder) {
        for (EntityType<?> entityType : BREEDABLE_ANIMALS) {
            builder.addCriterion(EntityType.getKey(entityType).toString(), BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(entityType)));
        }
        builder.addCriterion(EntityType.getKey(EntityType.TURTLE).toString(), BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(EntityType.TURTLE).build(), EntityPredicate.Builder.entity().of(EntityType.TURTLE).build(), EntityPredicate.ANY));
        return builder;
    }

    private Advancement.Builder addFishBuckets(Advancement.Builder builder) {
        for (Item item : FISH_BUCKETS) {
            builder.addCriterion(Registry.ITEM.getKey(item).getPath(), FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(item).build()));
        }
        return builder;
    }

    private Advancement.Builder addFish(Advancement.Builder builder) {
        for (Item item : FISH) {
            builder.addCriterion(Registry.ITEM.getKey(item).getPath(), FishingRodHookedTrigger.TriggerInstance.fishedItem(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.item().of(item).build()));
        }
        return builder;
    }

    private Advancement.Builder addCatVariants(Advancement.Builder builder) {
        Cat.TEXTURE_BY_TYPE.forEach((n, resourceLocation) -> builder.addCriterion(resourceLocation.getPath(), TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of((ResourceLocation)resourceLocation).build())));
        return builder;
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((Consumer)object);
    }
}

