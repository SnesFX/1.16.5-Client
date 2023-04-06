/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.info;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;

public class RegistryDumpReport
implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;

    public RegistryDumpReport(DataGenerator dataGenerator) {
        this.generator = dataGenerator;
    }

    @Override
    public void run(HashCache hashCache) throws IOException {
        JsonObject jsonObject = new JsonObject();
        Registry.REGISTRY.keySet().forEach(resourceLocation -> jsonObject.add(resourceLocation.toString(), RegistryDumpReport.dumpRegistry(Registry.REGISTRY.get((ResourceLocation)resourceLocation))));
        Path path = this.generator.getOutputFolder().resolve("reports/registries.json");
        DataProvider.save(GSON, hashCache, (JsonElement)jsonObject, path);
    }

    private static <T> JsonElement dumpRegistry(Registry<T> registry) {
        JsonObject jsonObject = new JsonObject();
        if (registry instanceof DefaultedRegistry) {
            ResourceLocation resourceLocation = ((DefaultedRegistry)registry).getDefaultKey();
            jsonObject.addProperty("default", resourceLocation.toString());
        }
        int n = Registry.REGISTRY.getId(registry);
        jsonObject.addProperty("protocol_id", (Number)n);
        JsonObject jsonObject2 = new JsonObject();
        for (ResourceLocation resourceLocation : registry.keySet()) {
            T t = registry.get(resourceLocation);
            int n2 = registry.getId(t);
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("protocol_id", (Number)n2);
            jsonObject2.add(resourceLocation.toString(), (JsonElement)jsonObject3);
        }
        jsonObject.add("entries", (JsonElement)jsonObject2);
        return jsonObject;
    }

    @Override
    public String getName() {
        return "Registry Dump";
    }
}

