/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Nullable;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class VillagerProfession {
    public static final VillagerProfession NONE = VillagerProfession.register("none", PoiType.UNEMPLOYED, null);
    public static final VillagerProfession ARMORER = VillagerProfession.register("armorer", PoiType.ARMORER, SoundEvents.VILLAGER_WORK_ARMORER);
    public static final VillagerProfession BUTCHER = VillagerProfession.register("butcher", PoiType.BUTCHER, SoundEvents.VILLAGER_WORK_BUTCHER);
    public static final VillagerProfession CARTOGRAPHER = VillagerProfession.register("cartographer", PoiType.CARTOGRAPHER, SoundEvents.VILLAGER_WORK_CARTOGRAPHER);
    public static final VillagerProfession CLERIC = VillagerProfession.register("cleric", PoiType.CLERIC, SoundEvents.VILLAGER_WORK_CLERIC);
    public static final VillagerProfession FARMER = VillagerProfession.register("farmer", PoiType.FARMER, (ImmutableSet<Item>)ImmutableSet.of((Object)Items.WHEAT, (Object)Items.WHEAT_SEEDS, (Object)Items.BEETROOT_SEEDS, (Object)Items.BONE_MEAL), (ImmutableSet<Block>)ImmutableSet.of((Object)Blocks.FARMLAND), SoundEvents.VILLAGER_WORK_FARMER);
    public static final VillagerProfession FISHERMAN = VillagerProfession.register("fisherman", PoiType.FISHERMAN, SoundEvents.VILLAGER_WORK_FISHERMAN);
    public static final VillagerProfession FLETCHER = VillagerProfession.register("fletcher", PoiType.FLETCHER, SoundEvents.VILLAGER_WORK_FLETCHER);
    public static final VillagerProfession LEATHERWORKER = VillagerProfession.register("leatherworker", PoiType.LEATHERWORKER, SoundEvents.VILLAGER_WORK_LEATHERWORKER);
    public static final VillagerProfession LIBRARIAN = VillagerProfession.register("librarian", PoiType.LIBRARIAN, SoundEvents.VILLAGER_WORK_LIBRARIAN);
    public static final VillagerProfession MASON = VillagerProfession.register("mason", PoiType.MASON, SoundEvents.VILLAGER_WORK_MASON);
    public static final VillagerProfession NITWIT = VillagerProfession.register("nitwit", PoiType.NITWIT, null);
    public static final VillagerProfession SHEPHERD = VillagerProfession.register("shepherd", PoiType.SHEPHERD, SoundEvents.VILLAGER_WORK_SHEPHERD);
    public static final VillagerProfession TOOLSMITH = VillagerProfession.register("toolsmith", PoiType.TOOLSMITH, SoundEvents.VILLAGER_WORK_TOOLSMITH);
    public static final VillagerProfession WEAPONSMITH = VillagerProfession.register("weaponsmith", PoiType.WEAPONSMITH, SoundEvents.VILLAGER_WORK_WEAPONSMITH);
    private final String name;
    private final PoiType jobPoiType;
    private final ImmutableSet<Item> requestedItems;
    private final ImmutableSet<Block> secondaryPoi;
    @Nullable
    private final SoundEvent workSound;

    private VillagerProfession(String string, PoiType poiType, ImmutableSet<Item> immutableSet, ImmutableSet<Block> immutableSet2, @Nullable SoundEvent soundEvent) {
        this.name = string;
        this.jobPoiType = poiType;
        this.requestedItems = immutableSet;
        this.secondaryPoi = immutableSet2;
        this.workSound = soundEvent;
    }

    public PoiType getJobPoiType() {
        return this.jobPoiType;
    }

    public ImmutableSet<Item> getRequestedItems() {
        return this.requestedItems;
    }

    public ImmutableSet<Block> getSecondaryPoi() {
        return this.secondaryPoi;
    }

    @Nullable
    public SoundEvent getWorkSound() {
        return this.workSound;
    }

    public String toString() {
        return this.name;
    }

    static VillagerProfession register(String string, PoiType poiType, @Nullable SoundEvent soundEvent) {
        return VillagerProfession.register(string, poiType, (ImmutableSet<Item>)ImmutableSet.of(), (ImmutableSet<Block>)ImmutableSet.of(), soundEvent);
    }

    static VillagerProfession register(String string, PoiType poiType, ImmutableSet<Item> immutableSet, ImmutableSet<Block> immutableSet2, @Nullable SoundEvent soundEvent) {
        return Registry.register(Registry.VILLAGER_PROFESSION, new ResourceLocation(string), new VillagerProfession(string, poiType, immutableSet, immutableSet2, soundEvent));
    }
}

