/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.advancements;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.advancements.AdventureAdvancements;
import net.minecraft.data.advancements.HusbandryAdvancements;
import net.minecraft.data.advancements.NetherAdvancements;
import net.minecraft.data.advancements.StoryAdvancements;
import net.minecraft.data.advancements.TheEndAdvancements;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;
    private final List<Consumer<Consumer<Advancement>>> tabs = ImmutableList.of((Object)new TheEndAdvancements(), (Object)new HusbandryAdvancements(), (Object)new AdventureAdvancements(), (Object)new NetherAdvancements(), (Object)new StoryAdvancements());

    public AdvancementProvider(DataGenerator dataGenerator) {
        this.generator = dataGenerator;
    }

    @Override
    public void run(HashCache hashCache) throws IOException {
        Path path = this.generator.getOutputFolder();
        HashSet hashSet = Sets.newHashSet();
        Consumer<Advancement> consumer = advancement -> {
            if (!hashSet.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            }
            Path path2 = AdvancementProvider.createPath(path, advancement);
            try {
                DataProvider.save(GSON, hashCache, (JsonElement)advancement.deconstruct().serializeToJson(), path2);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't save advancement {}", (Object)path2, (Object)iOException);
            }
        };
        for (Consumer<Consumer<Advancement>> consumer2 : this.tabs) {
            consumer2.accept(consumer);
        }
    }

    private static Path createPath(Path path, Advancement advancement) {
        return path.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Advancements";
    }
}

