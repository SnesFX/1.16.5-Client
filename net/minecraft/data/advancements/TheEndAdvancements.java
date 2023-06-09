/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LevitationTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class TheEndAdvancements
implements Consumer<Consumer<Advancement>> {
    @Override
    public void accept(Consumer<Advancement> consumer) {
        Advancement advancement = Advancement.Builder.advancement().display(Blocks.END_STONE, (Component)new TranslatableComponent("advancements.end.root.title"), (Component)new TranslatableComponent("advancements.end.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/end.png"), FrameType.TASK, false, false, false).addCriterion("entered_end", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.END)).save(consumer, "end/root");
        Advancement advancement2 = Advancement.Builder.advancement().parent(advancement).display(Blocks.DRAGON_HEAD, (Component)new TranslatableComponent("advancements.end.kill_dragon.title"), (Component)new TranslatableComponent("advancements.end.kill_dragon.description"), null, FrameType.TASK, true, true, false).addCriterion("killed_dragon", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON))).save(consumer, "end/kill_dragon");
        Advancement advancement3 = Advancement.Builder.advancement().parent(advancement2).display(Items.ENDER_PEARL, (Component)new TranslatableComponent("advancements.end.enter_end_gateway.title"), (Component)new TranslatableComponent("advancements.end.enter_end_gateway.description"), null, FrameType.TASK, true, true, false).addCriterion("entered_end_gateway", EnterBlockTrigger.TriggerInstance.entersBlock(Blocks.END_GATEWAY)).save(consumer, "end/enter_end_gateway");
        Advancement.Builder.advancement().parent(advancement2).display(Items.END_CRYSTAL, (Component)new TranslatableComponent("advancements.end.respawn_dragon.title"), (Component)new TranslatableComponent("advancements.end.respawn_dragon.description"), null, FrameType.GOAL, true, true, false).addCriterion("summoned_dragon", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(EntityType.ENDER_DRAGON))).save(consumer, "end/respawn_dragon");
        Advancement advancement4 = Advancement.Builder.advancement().parent(advancement3).display(Blocks.PURPUR_BLOCK, (Component)new TranslatableComponent("advancements.end.find_end_city.title"), (Component)new TranslatableComponent("advancements.end.find_end_city.description"), null, FrameType.TASK, true, true, false).addCriterion("in_city", LocationTrigger.TriggerInstance.located(LocationPredicate.inFeature(StructureFeature.END_CITY))).save(consumer, "end/find_end_city");
        Advancement.Builder.advancement().parent(advancement2).display(Items.DRAGON_BREATH, (Component)new TranslatableComponent("advancements.end.dragon_breath.title"), (Component)new TranslatableComponent("advancements.end.dragon_breath.description"), null, FrameType.GOAL, true, true, false).addCriterion("dragon_breath", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DRAGON_BREATH)).save(consumer, "end/dragon_breath");
        Advancement.Builder.advancement().parent(advancement4).display(Items.SHULKER_SHELL, (Component)new TranslatableComponent("advancements.end.levitate.title"), (Component)new TranslatableComponent("advancements.end.levitate.description"), null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("levitated", LevitationTrigger.TriggerInstance.levitated(DistancePredicate.vertical(MinMaxBounds.Floats.atLeast(50.0f)))).save(consumer, "end/levitate");
        Advancement.Builder.advancement().parent(advancement4).display(Items.ELYTRA, (Component)new TranslatableComponent("advancements.end.elytra.title"), (Component)new TranslatableComponent("advancements.end.elytra.description"), null, FrameType.GOAL, true, true, false).addCriterion("elytra", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ELYTRA)).save(consumer, "end/elytra");
        Advancement.Builder.advancement().parent(advancement2).display(Blocks.DRAGON_EGG, (Component)new TranslatableComponent("advancements.end.dragon_egg.title"), (Component)new TranslatableComponent("advancements.end.dragon_egg.description"), null, FrameType.GOAL, true, true, false).addCriterion("dragon_egg", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.DRAGON_EGG)).save(consumer, "end/dragon_egg");
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((Consumer)object);
    }
}

