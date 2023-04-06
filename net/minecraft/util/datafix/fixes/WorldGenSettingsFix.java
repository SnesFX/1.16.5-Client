/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicLike
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.OptionalDynamic
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.OptionalDynamic;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class WorldGenSettingsFix
extends DataFix {
    private static final ImmutableMap<String, StructureFeatureConfiguration> DEFAULTS = ImmutableMap.builder().put((Object)"minecraft:village", (Object)new StructureFeatureConfiguration(32, 8, 10387312)).put((Object)"minecraft:desert_pyramid", (Object)new StructureFeatureConfiguration(32, 8, 14357617)).put((Object)"minecraft:igloo", (Object)new StructureFeatureConfiguration(32, 8, 14357618)).put((Object)"minecraft:jungle_pyramid", (Object)new StructureFeatureConfiguration(32, 8, 14357619)).put((Object)"minecraft:swamp_hut", (Object)new StructureFeatureConfiguration(32, 8, 14357620)).put((Object)"minecraft:pillager_outpost", (Object)new StructureFeatureConfiguration(32, 8, 165745296)).put((Object)"minecraft:monument", (Object)new StructureFeatureConfiguration(32, 5, 10387313)).put((Object)"minecraft:endcity", (Object)new StructureFeatureConfiguration(20, 11, 10387313)).put((Object)"minecraft:mansion", (Object)new StructureFeatureConfiguration(80, 20, 10387319)).build();

    public WorldGenSettingsFix(Schema schema) {
        super(schema, true);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("WorldGenSettings building", this.getInputSchema().getType(References.WORLD_GEN_SETTINGS), typed -> typed.update(DSL.remainderFinder(), WorldGenSettingsFix::fix));
    }

    private static <T> Dynamic<T> noise(long l, DynamicLike<T> dynamicLike, Dynamic<T> dynamic, Dynamic<T> dynamic2) {
        return dynamicLike.createMap((Map)ImmutableMap.of((Object)dynamicLike.createString("type"), (Object)dynamicLike.createString("minecraft:noise"), (Object)dynamicLike.createString("biome_source"), dynamic2, (Object)dynamicLike.createString("seed"), (Object)dynamicLike.createLong(l), (Object)dynamicLike.createString("settings"), dynamic));
    }

    private static <T> Dynamic<T> vanillaBiomeSource(Dynamic<T> dynamic, long l, boolean bl, boolean bl2) {
        ImmutableMap.Builder builder = ImmutableMap.builder().put((Object)dynamic.createString("type"), (Object)dynamic.createString("minecraft:vanilla_layered")).put((Object)dynamic.createString("seed"), (Object)dynamic.createLong(l)).put((Object)dynamic.createString("large_biomes"), (Object)dynamic.createBoolean(bl2));
        if (bl) {
            builder.put((Object)dynamic.createString("legacy_biome_init_layer"), (Object)dynamic.createBoolean(bl));
        }
        return dynamic.createMap((Map)builder.build());
    }

    private static <T> Dynamic<T> fix(Dynamic<T> dynamic2) {
        OptionalDynamic optionalDynamic;
        Dynamic<T> dynamic3;
        DynamicOps dynamicOps = dynamic2.getOps();
        long l = dynamic2.get("RandomSeed").asLong(0L);
        Optional optional = dynamic2.get("generatorName").asString().map(string -> string.toLowerCase(Locale.ROOT)).result();
        Optional optional2 = dynamic2.get("legacy_custom_options").asString().result().map(Optional::of).orElseGet(() -> {
            if (optional.equals(Optional.of("customized"))) {
                return dynamic2.get("generatorOptions").asString().result();
            }
            return Optional.empty();
        });
        boolean bl = false;
        if (optional.equals(Optional.of("customized"))) {
            dynamic3 = WorldGenSettingsFix.defaultOverworld(dynamic2, l);
        } else if (!optional.isPresent()) {
            dynamic3 = WorldGenSettingsFix.defaultOverworld(dynamic2, l);
        } else {
            switch ((String)optional.get()) {
                case "flat": {
                    optionalDynamic = dynamic2.get("generatorOptions");
                    Map<Dynamic<T>, Dynamic<T>> map = WorldGenSettingsFix.fixFlatStructures(dynamicOps, optionalDynamic);
                    dynamic3 = dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("type"), (Object)dynamic2.createString("minecraft:flat"), (Object)dynamic2.createString("settings"), (Object)dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("structures"), (Object)dynamic2.createMap(map), (Object)dynamic2.createString("layers"), (Object)optionalDynamic.get("layers").result().orElseGet(() -> dynamic2.createList(Stream.of(new Dynamic[]{dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("height"), (Object)dynamic2.createInt(1), (Object)dynamic2.createString("block"), (Object)dynamic2.createString("minecraft:bedrock"))), dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("height"), (Object)dynamic2.createInt(2), (Object)dynamic2.createString("block"), (Object)dynamic2.createString("minecraft:dirt"))), dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("height"), (Object)dynamic2.createInt(1), (Object)dynamic2.createString("block"), (Object)dynamic2.createString("minecraft:grass_block")))}))), (Object)dynamic2.createString("biome"), (Object)dynamic2.createString(optionalDynamic.get("biome").asString("minecraft:plains"))))));
                    break;
                }
                case "debug_all_block_states": {
                    dynamic3 = dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("type"), (Object)dynamic2.createString("minecraft:debug")));
                    break;
                }
                case "buffet": {
                    Dynamic dynamic4;
                    Dynamic dynamic5;
                    OptionalDynamic optionalDynamic2 = dynamic2.get("generatorOptions");
                    OptionalDynamic optionalDynamic3 = optionalDynamic2.get("chunk_generator");
                    Optional optional3 = optionalDynamic3.get("type").asString().result();
                    if (Objects.equals(optional3, Optional.of("minecraft:caves"))) {
                        dynamic4 = dynamic2.createString("minecraft:caves");
                        bl = true;
                    } else {
                        dynamic4 = Objects.equals(optional3, Optional.of("minecraft:floating_islands")) ? dynamic2.createString("minecraft:floating_islands") : dynamic2.createString("minecraft:overworld");
                    }
                    Dynamic dynamic6 = optionalDynamic2.get("biome_source").result().orElseGet(() -> dynamic2.createMap((Map)ImmutableMap.of((Object)dynamic2.createString("type"), (Object)dynamic2.createString("minecraft:fixed"))));
                    if (dynamic6.get("type").asString().result().equals(Optional.of("minecraft:fixed"))) {
                        String string2 = dynamic6.get("options").get("biomes").asStream().findFirst().flatMap(dynamic -> dynamic.asString().result()).orElse("minecraft:ocean");
                        dynamic5 = dynamic6.remove("options").set("biome", dynamic2.createString(string2));
                    } else {
                        dynamic5 = dynamic6;
                    }
                    dynamic3 = WorldGenSettingsFix.noise(l, dynamic2, dynamic4, dynamic5);
                    break;
                }
                default: {
                    boolean bl2 = ((String)optional.get()).equals("default");
                    boolean bl3 = ((String)optional.get()).equals("default_1_1") || bl2 && dynamic2.get("generatorVersion").asInt(0) == 0;
                    boolean bl4 = ((String)optional.get()).equals("amplified");
                    boolean bl5 = ((String)optional.get()).equals("largebiomes");
                    dynamic3 = WorldGenSettingsFix.noise(l, dynamic2, dynamic2.createString(bl4 ? "minecraft:amplified" : "minecraft:overworld"), WorldGenSettingsFix.vanillaBiomeSource(dynamic2, l, bl3, bl5));
                }
            }
        }
        boolean bl6 = dynamic2.get("MapFeatures").asBoolean(true);
        boolean bl7 = (dynamic2.get("BonusChest").asBoolean(false) ? 1 : 0) != 0;
        optionalDynamic = ImmutableMap.builder();
        optionalDynamic.put(dynamicOps.createString("seed"), dynamicOps.createLong(l));
        optionalDynamic.put(dynamicOps.createString("generate_features"), dynamicOps.createBoolean(bl6));
        optionalDynamic.put(dynamicOps.createString("bonus_chest"), dynamicOps.createBoolean(bl7));
        optionalDynamic.put(dynamicOps.createString("dimensions"), WorldGenSettingsFix.vanillaLevels(dynamic2, l, dynamic3, bl));
        optional2.ifPresent(arg_0 -> WorldGenSettingsFix.lambda$fix$6((ImmutableMap.Builder)optionalDynamic, dynamicOps, arg_0));
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)optionalDynamic.build()));
    }

    protected static <T> Dynamic<T> defaultOverworld(Dynamic<T> dynamic, long l) {
        return WorldGenSettingsFix.noise(l, dynamic, dynamic.createString("minecraft:overworld"), WorldGenSettingsFix.vanillaBiomeSource(dynamic, l, false, false));
    }

    protected static <T> T vanillaLevels(Dynamic<T> dynamic, long l, Dynamic<T> dynamic2, boolean bl) {
        DynamicOps dynamicOps = dynamic.getOps();
        return (T)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("minecraft:overworld"), (Object)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString("minecraft:overworld" + (bl ? "_caves" : "")), (Object)dynamicOps.createString("generator"), (Object)dynamic2.getValue())), (Object)dynamicOps.createString("minecraft:the_nether"), (Object)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString("minecraft:the_nether"), (Object)dynamicOps.createString("generator"), (Object)WorldGenSettingsFix.noise(l, dynamic, dynamic.createString("minecraft:nether"), dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("type"), (Object)dynamic.createString("minecraft:multi_noise"), (Object)dynamic.createString("seed"), (Object)dynamic.createLong(l), (Object)dynamic.createString("preset"), (Object)dynamic.createString("minecraft:nether")))).getValue())), (Object)dynamicOps.createString("minecraft:the_end"), (Object)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("type"), (Object)dynamicOps.createString("minecraft:the_end"), (Object)dynamicOps.createString("generator"), (Object)WorldGenSettingsFix.noise(l, dynamic, dynamic.createString("minecraft:end"), dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("type"), (Object)dynamic.createString("minecraft:the_end"), (Object)dynamic.createString("seed"), (Object)dynamic.createLong(l)))).getValue()))));
    }

    private static <T> Map<Dynamic<T>, Dynamic<T>> fixFlatStructures(DynamicOps<T> dynamicOps, OptionalDynamic<T> optionalDynamic) {
        MutableInt mutableInt = new MutableInt(32);
        MutableInt mutableInt2 = new MutableInt(3);
        MutableInt mutableInt3 = new MutableInt(128);
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        HashMap hashMap = Maps.newHashMap();
        if (!optionalDynamic.result().isPresent()) {
            mutableBoolean.setTrue();
            hashMap.put("minecraft:village", DEFAULTS.get((Object)"minecraft:village"));
        }
        optionalDynamic.get("structures").flatMap(Dynamic::getMapValues).result().ifPresent(map2 -> map2.forEach((dynamic, dynamic2) -> dynamic2.getMapValues().result().ifPresent(map2 -> map2.forEach((dynamic2, dynamic3) -> {
            String string = dynamic.asString("");
            String string2 = dynamic2.asString("");
            String string3 = dynamic3.asString("");
            if ("stronghold".equals(string)) {
                mutableBoolean.setTrue();
                switch (string2) {
                    case "distance": {
                        mutableInt.setValue(WorldGenSettingsFix.getInt(string3, mutableInt.getValue(), 1));
                        return;
                    }
                    case "spread": {
                        mutableInt2.setValue(WorldGenSettingsFix.getInt(string3, mutableInt2.getValue(), 1));
                        return;
                    }
                    case "count": {
                        mutableInt3.setValue(WorldGenSettingsFix.getInt(string3, mutableInt3.getValue(), 1));
                        return;
                    }
                }
                return;
            }
            switch (string2) {
                case "distance": {
                    switch (string) {
                        case "village": {
                            WorldGenSettingsFix.setSpacing(hashMap, "minecraft:village", string3, 9);
                            return;
                        }
                        case "biome_1": {
                            WorldGenSettingsFix.setSpacing(hashMap, "minecraft:desert_pyramid", string3, 9);
                            WorldGenSettingsFix.setSpacing(hashMap, "minecraft:igloo", string3, 9);
                            WorldGenSettingsFix.setSpacing(hashMap, "minecraft:jungle_pyramid", string3, 9);
                            WorldGenSettingsFix.setSpacing(hashMap, "minecraft:swamp_hut", string3, 9);
                            WorldGenSettingsFix.setSpacing(hashMap, "minecraft:pillager_outpost", string3, 9);
                            return;
                        }
                        case "endcity": {
                            WorldGenSettingsFix.setSpacing(hashMap, "minecraft:endcity", string3, 1);
                            return;
                        }
                        case "mansion": {
                            WorldGenSettingsFix.setSpacing(hashMap, "minecraft:mansion", string3, 1);
                            return;
                        }
                    }
                    return;
                }
                case "separation": {
                    if ("oceanmonument".equals(string)) {
                        StructureFeatureConfiguration structureFeatureConfiguration = (StructureFeatureConfiguration)hashMap.getOrDefault("minecraft:monument", DEFAULTS.get((Object)"minecraft:monument"));
                        int n = WorldGenSettingsFix.getInt(string3, structureFeatureConfiguration.separation, 1);
                        hashMap.put("minecraft:monument", new StructureFeatureConfiguration(n, structureFeatureConfiguration.separation, structureFeatureConfiguration.salt));
                    }
                    return;
                }
                case "spacing": {
                    if ("oceanmonument".equals(string)) {
                        WorldGenSettingsFix.setSpacing(hashMap, "minecraft:monument", string3, 1);
                    }
                    return;
                }
            }
        }))));
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)optionalDynamic.createString("structures"), (Object)optionalDynamic.createMap(hashMap.entrySet().stream().collect(Collectors.toMap(entry -> optionalDynamic.createString((String)entry.getKey()), entry -> ((StructureFeatureConfiguration)entry.getValue()).serialize(dynamicOps)))));
        if (mutableBoolean.isTrue()) {
            builder.put((Object)optionalDynamic.createString("stronghold"), (Object)optionalDynamic.createMap((Map)ImmutableMap.of((Object)optionalDynamic.createString("distance"), (Object)optionalDynamic.createInt(mutableInt.getValue().intValue()), (Object)optionalDynamic.createString("spread"), (Object)optionalDynamic.createInt(mutableInt2.getValue().intValue()), (Object)optionalDynamic.createString("count"), (Object)optionalDynamic.createInt(mutableInt3.getValue().intValue()))));
        }
        return builder.build();
    }

    private static int getInt(String string, int n) {
        return NumberUtils.toInt((String)string, (int)n);
    }

    private static int getInt(String string, int n, int n2) {
        return Math.max(n2, WorldGenSettingsFix.getInt(string, n));
    }

    private static void setSpacing(Map<String, StructureFeatureConfiguration> map, String string, String string2, int n) {
        StructureFeatureConfiguration structureFeatureConfiguration = map.getOrDefault(string, (StructureFeatureConfiguration)DEFAULTS.get((Object)string));
        int n2 = WorldGenSettingsFix.getInt(string2, structureFeatureConfiguration.spacing, n);
        map.put(string, new StructureFeatureConfiguration(n2, structureFeatureConfiguration.separation, structureFeatureConfiguration.salt));
    }

    private static /* synthetic */ void lambda$fix$6(ImmutableMap.Builder builder, DynamicOps dynamicOps, String string) {
        builder.put(dynamicOps.createString("legacy_custom_options"), dynamicOps.createString(string));
    }

    static final class StructureFeatureConfiguration {
        public static final Codec<StructureFeatureConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("spacing").forGetter(structureFeatureConfiguration -> structureFeatureConfiguration.spacing), (App)Codec.INT.fieldOf("separation").forGetter(structureFeatureConfiguration -> structureFeatureConfiguration.separation), (App)Codec.INT.fieldOf("salt").forGetter(structureFeatureConfiguration -> structureFeatureConfiguration.salt)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> StructureFeatureConfiguration.new(arg_0, arg_1, arg_2)));
        private final int spacing;
        private final int separation;
        private final int salt;

        public StructureFeatureConfiguration(int n, int n2, int n3) {
            this.spacing = n;
            this.separation = n2;
            this.salt = n3;
        }

        public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
            return new Dynamic(dynamicOps, CODEC.encodeStart(dynamicOps, (Object)this).result().orElse(dynamicOps.emptyMap()));
        }
    }

}

