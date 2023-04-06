/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.OptionalDynamic
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  it.unimi.dsi.fastutil.objects.ObjectCollection
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.util.datafix.PackedBitStorage;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.References;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPalettedStorageFix
extends DataFix {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final BitSet VIRTUAL = new BitSet(256);
    private static final BitSet FIX = new BitSet(256);
    private static final Dynamic<?> PUMPKIN = BlockStateData.parse("{Name:'minecraft:pumpkin'}");
    private static final Dynamic<?> SNOWY_PODZOL = BlockStateData.parse("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
    private static final Dynamic<?> SNOWY_GRASS = BlockStateData.parse("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
    private static final Dynamic<?> SNOWY_MYCELIUM = BlockStateData.parse("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
    private static final Dynamic<?> UPPER_SUNFLOWER = BlockStateData.parse("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
    private static final Dynamic<?> UPPER_LILAC = BlockStateData.parse("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
    private static final Dynamic<?> UPPER_TALL_GRASS = BlockStateData.parse("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
    private static final Dynamic<?> UPPER_LARGE_FERN = BlockStateData.parse("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
    private static final Dynamic<?> UPPER_ROSE_BUSH = BlockStateData.parse("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
    private static final Dynamic<?> UPPER_PEONY = BlockStateData.parse("{Name:'minecraft:peony',Properties:{half:'upper'}}");
    private static final Map<String, Dynamic<?>> FLOWER_POT_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        hashMap.put("minecraft:air0", BlockStateData.parse("{Name:'minecraft:flower_pot'}"));
        hashMap.put("minecraft:red_flower0", BlockStateData.parse("{Name:'minecraft:potted_poppy'}"));
        hashMap.put("minecraft:red_flower1", BlockStateData.parse("{Name:'minecraft:potted_blue_orchid'}"));
        hashMap.put("minecraft:red_flower2", BlockStateData.parse("{Name:'minecraft:potted_allium'}"));
        hashMap.put("minecraft:red_flower3", BlockStateData.parse("{Name:'minecraft:potted_azure_bluet'}"));
        hashMap.put("minecraft:red_flower4", BlockStateData.parse("{Name:'minecraft:potted_red_tulip'}"));
        hashMap.put("minecraft:red_flower5", BlockStateData.parse("{Name:'minecraft:potted_orange_tulip'}"));
        hashMap.put("minecraft:red_flower6", BlockStateData.parse("{Name:'minecraft:potted_white_tulip'}"));
        hashMap.put("minecraft:red_flower7", BlockStateData.parse("{Name:'minecraft:potted_pink_tulip'}"));
        hashMap.put("minecraft:red_flower8", BlockStateData.parse("{Name:'minecraft:potted_oxeye_daisy'}"));
        hashMap.put("minecraft:yellow_flower0", BlockStateData.parse("{Name:'minecraft:potted_dandelion'}"));
        hashMap.put("minecraft:sapling0", BlockStateData.parse("{Name:'minecraft:potted_oak_sapling'}"));
        hashMap.put("minecraft:sapling1", BlockStateData.parse("{Name:'minecraft:potted_spruce_sapling'}"));
        hashMap.put("minecraft:sapling2", BlockStateData.parse("{Name:'minecraft:potted_birch_sapling'}"));
        hashMap.put("minecraft:sapling3", BlockStateData.parse("{Name:'minecraft:potted_jungle_sapling'}"));
        hashMap.put("minecraft:sapling4", BlockStateData.parse("{Name:'minecraft:potted_acacia_sapling'}"));
        hashMap.put("minecraft:sapling5", BlockStateData.parse("{Name:'minecraft:potted_dark_oak_sapling'}"));
        hashMap.put("minecraft:red_mushroom0", BlockStateData.parse("{Name:'minecraft:potted_red_mushroom'}"));
        hashMap.put("minecraft:brown_mushroom0", BlockStateData.parse("{Name:'minecraft:potted_brown_mushroom'}"));
        hashMap.put("minecraft:deadbush0", BlockStateData.parse("{Name:'minecraft:potted_dead_bush'}"));
        hashMap.put("minecraft:tallgrass2", BlockStateData.parse("{Name:'minecraft:potted_fern'}"));
        hashMap.put("minecraft:cactus0", BlockStateData.getTag(2240));
    });
    private static final Map<String, Dynamic<?>> SKULL_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        ChunkPalettedStorageFix.mapSkull(hashMap, 0, "skeleton", "skull");
        ChunkPalettedStorageFix.mapSkull(hashMap, 1, "wither_skeleton", "skull");
        ChunkPalettedStorageFix.mapSkull(hashMap, 2, "zombie", "head");
        ChunkPalettedStorageFix.mapSkull(hashMap, 3, "player", "head");
        ChunkPalettedStorageFix.mapSkull(hashMap, 4, "creeper", "head");
        ChunkPalettedStorageFix.mapSkull(hashMap, 5, "dragon", "head");
    });
    private static final Map<String, Dynamic<?>> DOOR_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        ChunkPalettedStorageFix.mapDoor(hashMap, "oak_door", 1024);
        ChunkPalettedStorageFix.mapDoor(hashMap, "iron_door", 1136);
        ChunkPalettedStorageFix.mapDoor(hashMap, "spruce_door", 3088);
        ChunkPalettedStorageFix.mapDoor(hashMap, "birch_door", 3104);
        ChunkPalettedStorageFix.mapDoor(hashMap, "jungle_door", 3120);
        ChunkPalettedStorageFix.mapDoor(hashMap, "acacia_door", 3136);
        ChunkPalettedStorageFix.mapDoor(hashMap, "dark_oak_door", 3152);
    });
    private static final Map<String, Dynamic<?>> NOTE_BLOCK_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        for (int i = 0; i < 26; ++i) {
            hashMap.put("true" + i, BlockStateData.parse("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
            hashMap.put("false" + i, BlockStateData.parse("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
        }
    });
    private static final Int2ObjectMap<String> DYE_COLOR_MAP = (Int2ObjectMap)DataFixUtils.make((Object)new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
        int2ObjectOpenHashMap.put(0, (Object)"white");
        int2ObjectOpenHashMap.put(1, (Object)"orange");
        int2ObjectOpenHashMap.put(2, (Object)"magenta");
        int2ObjectOpenHashMap.put(3, (Object)"light_blue");
        int2ObjectOpenHashMap.put(4, (Object)"yellow");
        int2ObjectOpenHashMap.put(5, (Object)"lime");
        int2ObjectOpenHashMap.put(6, (Object)"pink");
        int2ObjectOpenHashMap.put(7, (Object)"gray");
        int2ObjectOpenHashMap.put(8, (Object)"light_gray");
        int2ObjectOpenHashMap.put(9, (Object)"cyan");
        int2ObjectOpenHashMap.put(10, (Object)"purple");
        int2ObjectOpenHashMap.put(11, (Object)"blue");
        int2ObjectOpenHashMap.put(12, (Object)"brown");
        int2ObjectOpenHashMap.put(13, (Object)"green");
        int2ObjectOpenHashMap.put(14, (Object)"red");
        int2ObjectOpenHashMap.put(15, (Object)"black");
    });
    private static final Map<String, Dynamic<?>> BED_BLOCK_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        for (Int2ObjectMap.Entry entry : DYE_COLOR_MAP.int2ObjectEntrySet()) {
            if (Objects.equals(entry.getValue(), "red")) continue;
            ChunkPalettedStorageFix.addBeds(hashMap, entry.getIntKey(), (String)entry.getValue());
        }
    });
    private static final Map<String, Dynamic<?>> BANNER_BLOCK_MAP = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        for (Int2ObjectMap.Entry entry : DYE_COLOR_MAP.int2ObjectEntrySet()) {
            if (Objects.equals(entry.getValue(), "white")) continue;
            ChunkPalettedStorageFix.addBanners(hashMap, 15 - entry.getIntKey(), (String)entry.getValue());
        }
    });
    private static final Dynamic<?> AIR;

    public ChunkPalettedStorageFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    private static void mapSkull(Map<String, Dynamic<?>> map, int n, String string, String string2) {
        map.put(n + "north", BlockStateData.parse("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'north'}}"));
        map.put(n + "east", BlockStateData.parse("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'east'}}"));
        map.put(n + "south", BlockStateData.parse("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'south'}}"));
        map.put(n + "west", BlockStateData.parse("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'west'}}"));
        for (int i = 0; i < 16; ++i) {
            map.put(n + "" + i, BlockStateData.parse("{Name:'minecraft:" + string + "_" + string2 + "',Properties:{rotation:'" + i + "'}}"));
        }
    }

    private static void mapDoor(Map<String, Dynamic<?>> map, String string, int n) {
        map.put("minecraft:" + string + "eastlowerleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "eastlowerleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "eastlowerlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "eastlowerlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "eastlowerrightfalsefalse", BlockStateData.getTag(n));
        map.put("minecraft:" + string + "eastlowerrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "eastlowerrighttruefalse", BlockStateData.getTag(n + 4));
        map.put("minecraft:" + string + "eastlowerrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "eastupperleftfalsefalse", BlockStateData.getTag(n + 8));
        map.put("minecraft:" + string + "eastupperleftfalsetrue", BlockStateData.getTag(n + 10));
        map.put("minecraft:" + string + "eastupperlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "eastupperlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "eastupperrightfalsefalse", BlockStateData.getTag(n + 9));
        map.put("minecraft:" + string + "eastupperrightfalsetrue", BlockStateData.getTag(n + 11));
        map.put("minecraft:" + string + "eastupperrighttruefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "eastupperrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "northlowerleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "northlowerleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "northlowerlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "northlowerlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "northlowerrightfalsefalse", BlockStateData.getTag(n + 3));
        map.put("minecraft:" + string + "northlowerrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "northlowerrighttruefalse", BlockStateData.getTag(n + 7));
        map.put("minecraft:" + string + "northlowerrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "northupperleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "northupperleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "northupperlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "northupperlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "northupperrightfalsefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "northupperrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "northupperrighttruefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "northupperrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "southlowerleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "southlowerleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "southlowerlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "southlowerlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "southlowerrightfalsefalse", BlockStateData.getTag(n + 1));
        map.put("minecraft:" + string + "southlowerrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "southlowerrighttruefalse", BlockStateData.getTag(n + 5));
        map.put("minecraft:" + string + "southlowerrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "southupperleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "southupperleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "southupperlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "southupperlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "southupperrightfalsefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "southupperrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "southupperrighttruefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "southupperrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "westlowerleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "westlowerleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "westlowerlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "westlowerlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "westlowerrightfalsefalse", BlockStateData.getTag(n + 2));
        map.put("minecraft:" + string + "westlowerrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "westlowerrighttruefalse", BlockStateData.getTag(n + 6));
        map.put("minecraft:" + string + "westlowerrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "westupperleftfalsefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "westupperleftfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "westupperlefttruefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "westupperlefttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "westupperrightfalsefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "westupperrightfalsetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "westupperrighttruefalse", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "westupperrighttruetrue", BlockStateData.parse("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
    }

    private static void addBeds(Map<String, Dynamic<?>> map, int n, String string) {
        map.put("southfalsefoot" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}"));
        map.put("westfalsefoot" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
        map.put("northfalsefoot" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}"));
        map.put("eastfalsefoot" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
        map.put("southfalsehead" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}"));
        map.put("westfalsehead" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
        map.put("northfalsehead" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}"));
        map.put("eastfalsehead" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
        map.put("southtruehead" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
        map.put("westtruehead" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
        map.put("northtruehead" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
        map.put("easttruehead" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
    }

    private static void addBanners(Map<String, Dynamic<?>> map, int n, String string) {
        for (int i = 0; i < 16; ++i) {
            map.put("" + i + "_" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_banner',Properties:{rotation:'" + i + "'}}"));
        }
        map.put("north_" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'north'}}"));
        map.put("south_" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'south'}}"));
        map.put("west_" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'west'}}"));
        map.put("east_" + n, BlockStateData.parse("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'east'}}"));
    }

    public static String getName(Dynamic<?> dynamic) {
        return dynamic.get("Name").asString("");
    }

    public static String getProperty(Dynamic<?> dynamic, String string) {
        return dynamic.get("Properties").get(string).asString("");
    }

    public static int idFor(CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> crudeIncrementalIntIdentityHashBiMap, Dynamic<?> dynamic) {
        int n = crudeIncrementalIntIdentityHashBiMap.getId(dynamic);
        if (n == -1) {
            n = crudeIncrementalIntIdentityHashBiMap.add(dynamic);
        }
        return n;
    }

    private Dynamic<?> fix(Dynamic<?> dynamic) {
        Optional optional = dynamic.get("Level").result();
        if (optional.isPresent() && ((Dynamic)optional.get()).get("Sections").asStreamOpt().result().isPresent()) {
            return dynamic.set("Level", new UpgradeChunk((Dynamic)optional.get()).write());
        }
        return dynamic;
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(References.CHUNK);
        Type type2 = this.getOutputSchema().getType(References.CHUNK);
        return this.writeFixAndRead("ChunkPalettedStorageFix", type, type2, this::fix);
    }

    public static int getSideMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        int n = 0;
        if (bl3) {
            n = bl2 ? (n |= 2) : (bl ? (n |= 0x80) : (n |= 1));
        } else if (bl4) {
            n = bl ? (n |= 0x20) : (bl2 ? (n |= 8) : (n |= 0x10));
        } else if (bl2) {
            n |= 4;
        } else if (bl) {
            n |= 0x40;
        }
        return n;
    }

    static {
        FIX.set(2);
        FIX.set(3);
        FIX.set(110);
        FIX.set(140);
        FIX.set(144);
        FIX.set(25);
        FIX.set(86);
        FIX.set(26);
        FIX.set(176);
        FIX.set(177);
        FIX.set(175);
        FIX.set(64);
        FIX.set(71);
        FIX.set(193);
        FIX.set(194);
        FIX.set(195);
        FIX.set(196);
        FIX.set(197);
        VIRTUAL.set(54);
        VIRTUAL.set(146);
        VIRTUAL.set(25);
        VIRTUAL.set(26);
        VIRTUAL.set(51);
        VIRTUAL.set(53);
        VIRTUAL.set(67);
        VIRTUAL.set(108);
        VIRTUAL.set(109);
        VIRTUAL.set(114);
        VIRTUAL.set(128);
        VIRTUAL.set(134);
        VIRTUAL.set(135);
        VIRTUAL.set(136);
        VIRTUAL.set(156);
        VIRTUAL.set(163);
        VIRTUAL.set(164);
        VIRTUAL.set(180);
        VIRTUAL.set(203);
        VIRTUAL.set(55);
        VIRTUAL.set(85);
        VIRTUAL.set(113);
        VIRTUAL.set(188);
        VIRTUAL.set(189);
        VIRTUAL.set(190);
        VIRTUAL.set(191);
        VIRTUAL.set(192);
        VIRTUAL.set(93);
        VIRTUAL.set(94);
        VIRTUAL.set(101);
        VIRTUAL.set(102);
        VIRTUAL.set(160);
        VIRTUAL.set(106);
        VIRTUAL.set(107);
        VIRTUAL.set(183);
        VIRTUAL.set(184);
        VIRTUAL.set(185);
        VIRTUAL.set(186);
        VIRTUAL.set(187);
        VIRTUAL.set(132);
        VIRTUAL.set(139);
        VIRTUAL.set(199);
        AIR = BlockStateData.getTag(0);
    }

    public static enum Direction {
        DOWN(AxisDirection.NEGATIVE, Axis.Y),
        UP(AxisDirection.POSITIVE, Axis.Y),
        NORTH(AxisDirection.NEGATIVE, Axis.Z),
        SOUTH(AxisDirection.POSITIVE, Axis.Z),
        WEST(AxisDirection.NEGATIVE, Axis.X),
        EAST(AxisDirection.POSITIVE, Axis.X);
        
        private final Axis axis;
        private final AxisDirection axisDirection;

        private Direction(AxisDirection axisDirection, Axis axis) {
            this.axis = axis;
            this.axisDirection = axisDirection;
        }

        public AxisDirection getAxisDirection() {
            return this.axisDirection;
        }

        public Axis getAxis() {
            return this.axis;
        }

        public static enum AxisDirection {
            POSITIVE(1),
            NEGATIVE(-1);
            
            private final int step;

            private AxisDirection(int n2) {
                this.step = n2;
            }

            public int getStep() {
                return this.step;
            }
        }

        public static enum Axis {
            X,
            Y,
            Z;
            
        }

    }

    static class DataLayer {
        private final byte[] data;

        public DataLayer() {
            this.data = new byte[2048];
        }

        public DataLayer(byte[] arrby) {
            this.data = arrby;
            if (arrby.length != 2048) {
                throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + arrby.length);
            }
        }

        public int get(int n, int n2, int n3) {
            int n4 = this.getPosition(n2 << 8 | n3 << 4 | n);
            if (this.isFirst(n2 << 8 | n3 << 4 | n)) {
                return this.data[n4] & 0xF;
            }
            return this.data[n4] >> 4 & 0xF;
        }

        private boolean isFirst(int n) {
            return (n & 1) == 0;
        }

        private int getPosition(int n) {
            return n >> 1;
        }
    }

    static final class UpgradeChunk {
        private int sides;
        private final Section[] sections = new Section[16];
        private final Dynamic<?> level;
        private final int x;
        private final int z;
        private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap(16);

        public UpgradeChunk(Dynamic<?> dynamic) {
            this.level = dynamic;
            this.x = dynamic.get("xPos").asInt(0) << 4;
            this.z = dynamic.get("zPos").asInt(0) << 4;
            dynamic.get("TileEntities").asStreamOpt().result().ifPresent(stream -> stream.forEach(dynamic -> {
                int n;
                int n2 = dynamic.get("x").asInt(0) - this.x & 0xF;
                int n3 = dynamic.get("y").asInt(0);
                int n4 = n3 << 8 | (n = dynamic.get("z").asInt(0) - this.z & 0xF) << 4 | n2;
                if (this.blockEntities.put(n4, dynamic) != null) {
                    LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", (Object)this.x, (Object)this.z, (Object)n2, (Object)n3, (Object)n);
                }
            }));
            boolean bl = dynamic.get("convertedFromAlphaFormat").asBoolean(false);
            dynamic.get("Sections").asStreamOpt().result().ifPresent(stream -> stream.forEach(dynamic -> {
                Section section = new Section((Dynamic<?>)dynamic);
                this.sides = section.upgrade(this.sides);
                this.sections[section.y] = section;
            }));
            for (Section section : this.sections) {
                if (section == null) continue;
                block14 : for (Map.Entry entry : section.toFix.entrySet()) {
                    int n = section.y << 12;
                    switch ((Integer)entry.getKey()) {
                        case 2: {
                            int n2;
                            Object object;
                            Dynamic<?> dynamic2;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                n2 = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(n2 |= n);
                                if (!"minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(dynamic2)) || !"minecraft:snow".equals(object = ChunkPalettedStorageFix.getName(this.getBlock(UpgradeChunk.relative(n2, Direction.UP)))) && !"minecraft:snow_layer".equals(object)) continue;
                                this.setBlock(n2, SNOWY_GRASS);
                            }
                            continue block14;
                        }
                        case 3: {
                            int n2;
                            Object object;
                            Dynamic<?> dynamic2;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                n2 = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(n2 |= n);
                                if (!"minecraft:podzol".equals(ChunkPalettedStorageFix.getName(dynamic2)) || !"minecraft:snow".equals(object = ChunkPalettedStorageFix.getName(this.getBlock(UpgradeChunk.relative(n2, Direction.UP)))) && !"minecraft:snow_layer".equals(object)) continue;
                                this.setBlock(n2, SNOWY_PODZOL);
                            }
                            continue block14;
                        }
                        case 110: {
                            int n2;
                            Object object;
                            Dynamic<?> dynamic2;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                n2 = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(n2 |= n);
                                if (!"minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(dynamic2)) || !"minecraft:snow".equals(object = ChunkPalettedStorageFix.getName(this.getBlock(UpgradeChunk.relative(n2, Direction.UP)))) && !"minecraft:snow_layer".equals(object)) continue;
                                this.setBlock(n2, SNOWY_MYCELIUM);
                            }
                            continue block14;
                        }
                        case 25: {
                            int n2;
                            Object object;
                            Dynamic<?> dynamic2;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                n2 = (Integer)intListIterator.next();
                                dynamic2 = this.removeBlockEntity(n2 |= n);
                                if (dynamic2 == null) continue;
                                object = Boolean.toString(dynamic2.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic2.get("note").asInt(0), 0), 24);
                                this.setBlock(n2, (Dynamic)NOTE_BLOCK_MAP.getOrDefault(object, NOTE_BLOCK_MAP.get("false0")));
                            }
                            continue block14;
                        }
                        case 26: {
                            int n2;
                            Object object;
                            Object object2;
                            Object object3;
                            Dynamic<?> dynamic2;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                n2 = (Integer)intListIterator.next();
                                dynamic2 = this.getBlockEntity(n2 |= n);
                                object = this.getBlock(n2);
                                if (dynamic2 == null || (object2 = dynamic2.get("color").asInt(0)) == 14 || object2 < 0 || object2 >= 16) continue;
                                object3 = ChunkPalettedStorageFix.getProperty(object, "facing") + ChunkPalettedStorageFix.getProperty(object, "occupied") + ChunkPalettedStorageFix.getProperty(object, "part") + object2;
                                if (!BED_BLOCK_MAP.containsKey(object3)) continue;
                                this.setBlock(n2, (Dynamic)BED_BLOCK_MAP.get(object3));
                            }
                            continue block14;
                        }
                        case 176: 
                        case 177: {
                            int n2;
                            Object object;
                            Object object2;
                            Object object3;
                            Dynamic<?> dynamic2;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                n2 = (Integer)intListIterator.next();
                                dynamic2 = this.getBlockEntity(n2 |= n);
                                object = this.getBlock(n2);
                                if (dynamic2 == null || (object2 = dynamic2.get("Base").asInt(0)) == 15 || object2 < 0 || object2 >= 16) continue;
                                object3 = ChunkPalettedStorageFix.getProperty(object, (Integer)entry.getKey() == 176 ? "rotation" : "facing") + "_" + object2;
                                if (!BANNER_BLOCK_MAP.containsKey(object3)) continue;
                                this.setBlock(n2, (Dynamic)BANNER_BLOCK_MAP.get(object3));
                            }
                            continue block14;
                        }
                        case 86: {
                            int n2;
                            Object object;
                            Dynamic<?> dynamic2;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                n2 = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(n2 |= n);
                                if (!"minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(dynamic2)) || !"minecraft:grass_block".equals(object = ChunkPalettedStorageFix.getName(this.getBlock(UpgradeChunk.relative(n2, Direction.DOWN)))) && !"minecraft:dirt".equals(object)) continue;
                                this.setBlock(n2, PUMPKIN);
                            }
                            continue block14;
                        }
                        case 140: {
                            int n2;
                            Object object;
                            Dynamic<?> dynamic2;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                n2 = (Integer)intListIterator.next();
                                dynamic2 = this.removeBlockEntity(n2 |= n);
                                if (dynamic2 == null) continue;
                                object = dynamic2.get("Item").asString("") + dynamic2.get("Data").asInt(0);
                                this.setBlock(n2, (Dynamic)FLOWER_POT_MAP.getOrDefault(object, FLOWER_POT_MAP.get("minecraft:air0")));
                            }
                            continue block14;
                        }
                        case 144: {
                            int n2;
                            Object object;
                            Object object2;
                            Object object3;
                            Dynamic<?> dynamic2;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                n2 = (Integer)intListIterator.next();
                                dynamic2 = this.getBlockEntity(n2 |= n);
                                if (dynamic2 == null) continue;
                                object = String.valueOf(dynamic2.get("SkullType").asInt(0));
                                object2 = ChunkPalettedStorageFix.getProperty(this.getBlock(n2), "facing");
                                object3 = "up".equals(object2) || "down".equals(object2) ? object + String.valueOf(dynamic2.get("Rot").asInt(0)) : object + (String)object2;
                                dynamic2.remove("SkullType");
                                dynamic2.remove("facing");
                                dynamic2.remove("Rot");
                                this.setBlock(n2, (Dynamic)SKULL_MAP.getOrDefault(object3, SKULL_MAP.get("0north")));
                            }
                            continue block14;
                        }
                        case 64: 
                        case 71: 
                        case 193: 
                        case 194: 
                        case 195: 
                        case 196: 
                        case 197: {
                            int n2;
                            Object object;
                            Object object2;
                            Object object3;
                            Dynamic<?> dynamic2;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                n2 = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(n2 |= n);
                                if (!ChunkPalettedStorageFix.getName(dynamic2).endsWith("_door") || !"lower".equals(ChunkPalettedStorageFix.getProperty(object = this.getBlock(n2), "half"))) continue;
                                object2 = UpgradeChunk.relative(n2, Direction.UP);
                                object3 = this.getBlock((int)object2);
                                String string = ChunkPalettedStorageFix.getName(object);
                                if (!string.equals(ChunkPalettedStorageFix.getName(object3))) continue;
                                String string2 = ChunkPalettedStorageFix.getProperty(object, "facing");
                                String string3 = ChunkPalettedStorageFix.getProperty(object, "open");
                                String string4 = bl ? "left" : ChunkPalettedStorageFix.getProperty(object3, "hinge");
                                String string5 = bl ? "false" : ChunkPalettedStorageFix.getProperty(object3, "powered");
                                this.setBlock(n2, (Dynamic)DOOR_MAP.get(string + string2 + "lower" + string4 + string3 + string5));
                                this.setBlock((int)object2, (Dynamic<?>)((Dynamic)DOOR_MAP.get(string + string2 + "upper" + string4 + string3 + string5)));
                            }
                            continue block14;
                        }
                        case 175: {
                            int n2;
                            Object object;
                            Object object2;
                            Dynamic<?> dynamic2;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                n2 = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(n2 |= n);
                                if (!"upper".equals(ChunkPalettedStorageFix.getProperty(dynamic2, "half"))) continue;
                                object = this.getBlock(UpgradeChunk.relative(n2, Direction.DOWN));
                                object2 = ChunkPalettedStorageFix.getName(object);
                                if ("minecraft:sunflower".equals(object2)) {
                                    this.setBlock(n2, UPPER_SUNFLOWER);
                                    continue;
                                }
                                if ("minecraft:lilac".equals(object2)) {
                                    this.setBlock(n2, UPPER_LILAC);
                                    continue;
                                }
                                if ("minecraft:tall_grass".equals(object2)) {
                                    this.setBlock(n2, UPPER_TALL_GRASS);
                                    continue;
                                }
                                if ("minecraft:large_fern".equals(object2)) {
                                    this.setBlock(n2, UPPER_LARGE_FERN);
                                    continue;
                                }
                                if ("minecraft:rose_bush".equals(object2)) {
                                    this.setBlock(n2, UPPER_ROSE_BUSH);
                                    continue;
                                }
                                if (!"minecraft:peony".equals(object2)) continue;
                                this.setBlock(n2, UPPER_PEONY);
                            }
                            break;
                        }
                    }
                }
            }
        }

        @Nullable
        private Dynamic<?> getBlockEntity(int n) {
            return (Dynamic)this.blockEntities.get(n);
        }

        @Nullable
        private Dynamic<?> removeBlockEntity(int n) {
            return (Dynamic)this.blockEntities.remove(n);
        }

        public static int relative(int n, Direction direction) {
            switch (direction.getAxis()) {
                case X: {
                    int n2 = (n & 0xF) + direction.getAxisDirection().getStep();
                    return n2 < 0 || n2 > 15 ? -1 : n & 0xFFFFFFF0 | n2;
                }
                case Y: {
                    int n3 = (n >> 8) + direction.getAxisDirection().getStep();
                    return n3 < 0 || n3 > 255 ? -1 : n & 0xFF | n3 << 8;
                }
                case Z: {
                    int n4 = (n >> 4 & 0xF) + direction.getAxisDirection().getStep();
                    return n4 < 0 || n4 > 15 ? -1 : n & 0xFFFFFF0F | n4 << 4;
                }
            }
            return -1;
        }

        private void setBlock(int n, Dynamic<?> dynamic) {
            if (n < 0 || n > 65535) {
                return;
            }
            Section section = this.getSection(n);
            if (section == null) {
                return;
            }
            section.setBlock(n & 0xFFF, dynamic);
        }

        @Nullable
        private Section getSection(int n) {
            int n2 = n >> 12;
            return n2 < this.sections.length ? this.sections[n2] : null;
        }

        public Dynamic<?> getBlock(int n) {
            if (n < 0 || n > 65535) {
                return AIR;
            }
            Section section = this.getSection(n);
            if (section == null) {
                return AIR;
            }
            return section.getBlock(n & 0xFFF);
        }

        public Dynamic<?> write() {
            Dynamic dynamic = this.level;
            dynamic = this.blockEntities.isEmpty() ? dynamic.remove("TileEntities") : dynamic.set("TileEntities", dynamic.createList(this.blockEntities.values().stream()));
            Dynamic dynamic2 = dynamic.emptyMap();
            ArrayList arrayList = Lists.newArrayList();
            for (Section section : this.sections) {
                if (section == null) continue;
                arrayList.add(section.write());
                dynamic2 = dynamic2.set(String.valueOf(section.y), dynamic2.createIntList(Arrays.stream(section.update.toIntArray())));
            }
            Dynamic dynamic3 = dynamic.emptyMap();
            dynamic3 = dynamic3.set("Sides", dynamic3.createByte((byte)this.sides));
            dynamic3 = dynamic3.set("Indices", dynamic2);
            return dynamic.set("UpgradeData", dynamic3).set("Sections", dynamic3.createList(arrayList.stream()));
        }
    }

    static class Section {
        private final CrudeIncrementalIntIdentityHashBiMap<Dynamic<?>> palette = new CrudeIncrementalIntIdentityHashBiMap(32);
        private final List<Dynamic<?>> listTag;
        private final Dynamic<?> section;
        private final boolean hasData;
        private final Int2ObjectMap<IntList> toFix = new Int2ObjectLinkedOpenHashMap();
        private final IntList update = new IntArrayList();
        public final int y;
        private final Set<Dynamic<?>> seen = Sets.newIdentityHashSet();
        private final int[] buffer = new int[4096];

        public Section(Dynamic<?> dynamic) {
            this.listTag = Lists.newArrayList();
            this.section = dynamic;
            this.y = dynamic.get("Y").asInt(0);
            this.hasData = dynamic.get("Blocks").result().isPresent();
        }

        public Dynamic<?> getBlock(int n) {
            if (n < 0 || n > 4095) {
                return AIR;
            }
            Dynamic<?> dynamic = this.palette.byId(this.buffer[n]);
            return dynamic == null ? AIR : dynamic;
        }

        public void setBlock(int n, Dynamic<?> dynamic) {
            if (this.seen.add(dynamic)) {
                this.listTag.add("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(dynamic)) ? AIR : dynamic);
            }
            this.buffer[n] = ChunkPalettedStorageFix.idFor(this.palette, dynamic);
        }

        public int upgrade(int n) {
            if (!this.hasData) {
                return n;
            }
            ByteBuffer byteBuffer2 = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().result().get();
            DataLayer dataLayer = this.section.get("Data").asByteBufferOpt().map(byteBuffer -> new DataLayer(DataFixUtils.toArray((ByteBuffer)byteBuffer))).result().orElseGet(DataLayer::new);
            DataLayer dataLayer2 = this.section.get("Add").asByteBufferOpt().map(byteBuffer -> new DataLayer(DataFixUtils.toArray((ByteBuffer)byteBuffer))).result().orElseGet(DataLayer::new);
            this.seen.add(AIR);
            ChunkPalettedStorageFix.idFor(this.palette, AIR);
            this.listTag.add(AIR);
            for (int i = 0; i < 4096; ++i) {
                int n2 = i & 0xF;
                int n3 = i >> 8 & 0xF;
                int n4 = i >> 4 & 0xF;
                int n5 = dataLayer2.get(n2, n3, n4) << 12 | (byteBuffer2.get(i) & 0xFF) << 4 | dataLayer.get(n2, n3, n4);
                if (FIX.get(n5 >> 4)) {
                    this.addFix(n5 >> 4, i);
                }
                if (VIRTUAL.get(n5 >> 4)) {
                    int n6 = ChunkPalettedStorageFix.getSideMask(n2 == 0, n2 == 15, n4 == 0, n4 == 15);
                    if (n6 == 0) {
                        this.update.add(i);
                    } else {
                        n |= n6;
                    }
                }
                this.setBlock(i, BlockStateData.getTag(n5));
            }
            return n;
        }

        private void addFix(int n, int n2) {
            IntList intList = (IntList)this.toFix.get(n);
            if (intList == null) {
                intList = new IntArrayList();
                this.toFix.put(n, (Object)intList);
            }
            intList.add(n2);
        }

        public Dynamic<?> write() {
            Dynamic dynamic = this.section;
            if (!this.hasData) {
                return dynamic;
            }
            dynamic = dynamic.set("Palette", dynamic.createList(this.listTag.stream()));
            int n = Math.max(4, DataFixUtils.ceillog2((int)this.seen.size()));
            PackedBitStorage packedBitStorage = new PackedBitStorage(n, 4096);
            for (int i = 0; i < this.buffer.length; ++i) {
                packedBitStorage.set(i, this.buffer[i]);
            }
            dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(packedBitStorage.getRaw())));
            dynamic = dynamic.remove("Blocks");
            dynamic = dynamic.remove("Data");
            dynamic = dynamic.remove("Add");
            return dynamic;
        }
    }

}

