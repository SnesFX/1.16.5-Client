/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.models;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator generator;

    public ModelProvider(DataGenerator dataGenerator) {
        this.generator = dataGenerator;
    }

    @Override
    public void run(HashCache hashCache) {
        Path path = this.generator.getOutputFolder();
        HashMap hashMap = Maps.newHashMap();
        Consumer<BlockStateGenerator> consumer = blockStateGenerator -> {
            Block block = blockStateGenerator.getBlock();
            BlockStateGenerator blockStateGenerator2 = hashMap.put(block, blockStateGenerator);
            if (blockStateGenerator2 != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + block);
            }
        };
        HashMap hashMap2 = Maps.newHashMap();
        HashSet hashSet = Sets.newHashSet();
        BiConsumer<ResourceLocation, Supplier<JsonElement>> biConsumer = (resourceLocation, supplier) -> {
            Supplier supplier2 = hashMap2.put(resourceLocation, supplier);
            if (supplier2 != null) {
                throw new IllegalStateException("Duplicate model definition for " + resourceLocation);
            }
        };
        Consumer<Item> consumer2 = hashSet::add;
        new BlockModelGenerators(consumer, biConsumer, consumer2).run();
        new ItemModelGenerators(biConsumer).run();
        List list = Registry.BLOCK.stream().filter(block -> !hashMap.containsKey(block)).collect(Collectors.toList());
        if (!list.isEmpty()) {
            throw new IllegalStateException("Missing blockstate definitions for: " + list);
        }
        Registry.BLOCK.forEach(block -> {
            Item item = Item.BY_BLOCK.get(block);
            if (item != null) {
                if (hashSet.contains(item)) {
                    return;
                }
                ResourceLocation resourceLocation = ModelLocationUtils.getModelLocation(item);
                if (!hashMap2.containsKey(resourceLocation)) {
                    hashMap2.put(resourceLocation, new DelegatedModel(ModelLocationUtils.getModelLocation(block)));
                }
            }
        });
        this.saveCollection(hashCache, path, hashMap, (arg_0, arg_1) -> ModelProvider.createBlockStatePath(arg_0, arg_1));
        this.saveCollection(hashCache, path, hashMap2, (arg_0, arg_1) -> ModelProvider.createModelPath(arg_0, arg_1));
    }

    private <T> void saveCollection(HashCache hashCache, Path path, Map<T, ? extends Supplier<JsonElement>> map, BiFunction<Path, T, Path> biFunction) {
        map.forEach((object, supplier) -> {
            Path path2 = (Path)biFunction.apply(path, object);
            try {
                DataProvider.save(GSON, hashCache, (JsonElement)supplier.get(), path2);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't save {}", (Object)path2, (Object)exception);
            }
        });
    }

    private static Path createBlockStatePath(Path path, Block block) {
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(block);
        return path.resolve("assets/" + resourceLocation.getNamespace() + "/blockstates/" + resourceLocation.getPath() + ".json");
    }

    private static Path createModelPath(Path path, ResourceLocation resourceLocation) {
        return path.resolve("assets/" + resourceLocation.getNamespace() + "/models/" + resourceLocation.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Block State Definitions";
    }
}

