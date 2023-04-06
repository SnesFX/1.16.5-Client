/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.mojang.datafixers.util.Pair
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.PiglinBarterLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator generator;
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> subProviders = ImmutableList.of((Object)Pair.of((Supplier<Consumer>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Ljava/util/function/Consumer;)(), (Object)LootContextParamSets.FISHING), (Object)Pair.of((Supplier<Consumer>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Ljava/util/function/Consumer;)(), (Object)LootContextParamSets.CHEST), (Object)Pair.of((Supplier<Consumer>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Ljava/util/function/Consumer;)(), (Object)LootContextParamSets.ENTITY), (Object)Pair.of((Supplier<Consumer>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Ljava/util/function/Consumer;)(), (Object)LootContextParamSets.BLOCK), (Object)Pair.of(PiglinBarterLoot::new, (Object)LootContextParamSets.PIGLIN_BARTER), (Object)Pair.of((Supplier<Consumer>)LambdaMetafactory.metafactory(null, null, null, ()Ljava/lang/Object;, <init>(), ()Ljava/util/function/Consumer;)(), (Object)LootContextParamSets.GIFT));

    public LootTableProvider(DataGenerator dataGenerator) {
        this.generator = dataGenerator;
    }

    @Override
    public void run(HashCache hashCache) {
        Path path = this.generator.getOutputFolder();
        HashMap hashMap = Maps.newHashMap();
        this.subProviders.forEach(pair -> ((Consumer)((Supplier)pair.getFirst()).get()).accept((resourceLocation, builder) -> {
            if (hashMap.put(resourceLocation, builder.setParamSet((LootContextParamSet)pair.getSecond()).build()) != null) {
                throw new IllegalStateException("Duplicate loot table " + resourceLocation);
            }
        }));
        ValidationContext validationContext = new ValidationContext(LootContextParamSets.ALL_PARAMS, resourceLocation -> null, hashMap::get);
        Sets.SetView setView = Sets.difference(BuiltInLootTables.all(), hashMap.keySet());
        for (ResourceLocation resourceLocation2 : setView) {
            validationContext.reportProblem("Missing built-in table: " + resourceLocation2);
        }
        hashMap.forEach((resourceLocation, lootTable) -> LootTables.validate(validationContext, resourceLocation, lootTable));
        Iterator iterator = validationContext.getProblems();
        if (!iterator.isEmpty()) {
            iterator.forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        }
        hashMap.forEach((resourceLocation, lootTable) -> {
            Path path2 = LootTableProvider.createPath(path, resourceLocation);
            try {
                DataProvider.save(GSON, hashCache, LootTables.serialize(lootTable), path2);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't save loot table {}", (Object)path2, (Object)iOException);
            }
        });
    }

    private static Path createPath(Path path, ResourceLocation resourceLocation) {
        return path.resolve("data/" + resourceLocation.getNamespace() + "/loot_tables/" + resourceLocation.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "LootTables";
    }
}

