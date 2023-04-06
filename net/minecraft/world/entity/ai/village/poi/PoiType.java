/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 */
package net.minecraft.world.entity.ai.village.poi;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class PoiType {
    private static final Supplier<Set<PoiType>> ALL_JOB_POI_TYPES = Suppliers.memoize(() -> Registry.VILLAGER_PROFESSION.stream().map(VillagerProfession::getJobPoiType).collect(Collectors.toSet()));
    public static final Predicate<PoiType> ALL_JOBS = poiType -> ALL_JOB_POI_TYPES.get().contains(poiType);
    public static final Predicate<PoiType> ALL = poiType -> true;
    private static final Set<BlockState> BEDS = (Set)ImmutableList.of((Object)Blocks.RED_BED, (Object)Blocks.BLACK_BED, (Object)Blocks.BLUE_BED, (Object)Blocks.BROWN_BED, (Object)Blocks.CYAN_BED, (Object)Blocks.GRAY_BED, (Object)Blocks.GREEN_BED, (Object)Blocks.LIGHT_BLUE_BED, (Object)Blocks.LIGHT_GRAY_BED, (Object)Blocks.LIME_BED, (Object)Blocks.MAGENTA_BED, (Object)Blocks.ORANGE_BED, (Object[])new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap(block -> block.getStateDefinition().getPossibleStates().stream()).filter(blockState -> blockState.getValue(BedBlock.PART) == BedPart.HEAD).collect(ImmutableSet.toImmutableSet());
    private static final Map<BlockState, PoiType> TYPE_BY_STATE = Maps.newHashMap();
    public static final PoiType UNEMPLOYED = PoiType.register("unemployed", (Set<BlockState>)ImmutableSet.of(), 1, ALL_JOBS, 1);
    public static final PoiType ARMORER = PoiType.register("armorer", PoiType.getBlockStates(Blocks.BLAST_FURNACE), 1, 1);
    public static final PoiType BUTCHER = PoiType.register("butcher", PoiType.getBlockStates(Blocks.SMOKER), 1, 1);
    public static final PoiType CARTOGRAPHER = PoiType.register("cartographer", PoiType.getBlockStates(Blocks.CARTOGRAPHY_TABLE), 1, 1);
    public static final PoiType CLERIC = PoiType.register("cleric", PoiType.getBlockStates(Blocks.BREWING_STAND), 1, 1);
    public static final PoiType FARMER = PoiType.register("farmer", PoiType.getBlockStates(Blocks.COMPOSTER), 1, 1);
    public static final PoiType FISHERMAN = PoiType.register("fisherman", PoiType.getBlockStates(Blocks.BARREL), 1, 1);
    public static final PoiType FLETCHER = PoiType.register("fletcher", PoiType.getBlockStates(Blocks.FLETCHING_TABLE), 1, 1);
    public static final PoiType LEATHERWORKER = PoiType.register("leatherworker", PoiType.getBlockStates(Blocks.CAULDRON), 1, 1);
    public static final PoiType LIBRARIAN = PoiType.register("librarian", PoiType.getBlockStates(Blocks.LECTERN), 1, 1);
    public static final PoiType MASON = PoiType.register("mason", PoiType.getBlockStates(Blocks.STONECUTTER), 1, 1);
    public static final PoiType NITWIT = PoiType.register("nitwit", (Set<BlockState>)ImmutableSet.of(), 1, 1);
    public static final PoiType SHEPHERD = PoiType.register("shepherd", PoiType.getBlockStates(Blocks.LOOM), 1, 1);
    public static final PoiType TOOLSMITH = PoiType.register("toolsmith", PoiType.getBlockStates(Blocks.SMITHING_TABLE), 1, 1);
    public static final PoiType WEAPONSMITH = PoiType.register("weaponsmith", PoiType.getBlockStates(Blocks.GRINDSTONE), 1, 1);
    public static final PoiType HOME = PoiType.register("home", BEDS, 1, 1);
    public static final PoiType MEETING = PoiType.register("meeting", PoiType.getBlockStates(Blocks.BELL), 32, 6);
    public static final PoiType BEEHIVE = PoiType.register("beehive", PoiType.getBlockStates(Blocks.BEEHIVE), 0, 1);
    public static final PoiType BEE_NEST = PoiType.register("bee_nest", PoiType.getBlockStates(Blocks.BEE_NEST), 0, 1);
    public static final PoiType NETHER_PORTAL = PoiType.register("nether_portal", PoiType.getBlockStates(Blocks.NETHER_PORTAL), 0, 1);
    public static final PoiType LODESTONE = PoiType.register("lodestone", PoiType.getBlockStates(Blocks.LODESTONE), 0, 1);
    protected static final Set<BlockState> ALL_STATES = new ObjectOpenHashSet(TYPE_BY_STATE.keySet());
    private final String name;
    private final Set<BlockState> matchingStates;
    private final int maxTickets;
    private final Predicate<PoiType> predicate;
    private final int validRange;

    private static Set<BlockState> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    private PoiType(String string, Set<BlockState> set, int n, Predicate<PoiType> predicate, int n2) {
        this.name = string;
        this.matchingStates = ImmutableSet.copyOf(set);
        this.maxTickets = n;
        this.predicate = predicate;
        this.validRange = n2;
    }

    private PoiType(String string, Set<BlockState> set, int n, int n2) {
        this.name = string;
        this.matchingStates = ImmutableSet.copyOf(set);
        this.maxTickets = n;
        this.predicate = poiType -> poiType == this;
        this.validRange = n2;
    }

    public int getMaxTickets() {
        return this.maxTickets;
    }

    public Predicate<PoiType> getPredicate() {
        return this.predicate;
    }

    public int getValidRange() {
        return this.validRange;
    }

    public String toString() {
        return this.name;
    }

    private static PoiType register(String string, Set<BlockState> set, int n, int n2) {
        return PoiType.registerBlockStates(Registry.register(Registry.POINT_OF_INTEREST_TYPE, new ResourceLocation(string), new PoiType(string, set, n, n2)));
    }

    private static PoiType register(String string, Set<BlockState> set, int n, Predicate<PoiType> predicate, int n2) {
        return PoiType.registerBlockStates(Registry.register(Registry.POINT_OF_INTEREST_TYPE, new ResourceLocation(string), new PoiType(string, set, n, predicate, n2)));
    }

    private static PoiType registerBlockStates(PoiType poiType) {
        poiType.matchingStates.forEach(blockState -> {
            PoiType poiType2 = TYPE_BY_STATE.put((BlockState)blockState, poiType);
            if (poiType2 != null) {
                throw Util.pauseInIde(new IllegalStateException(String.format("%s is defined in too many tags", blockState)));
            }
        });
        return poiType;
    }

    public static Optional<PoiType> forState(BlockState blockState) {
        return Optional.ofNullable(TYPE_BY_STATE.get(blockState));
    }
}

