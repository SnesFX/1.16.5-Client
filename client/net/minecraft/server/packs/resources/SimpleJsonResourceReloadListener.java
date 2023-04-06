/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.packs.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SimpleJsonResourceReloadListener
extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int PATH_SUFFIX_LENGTH = ".json".length();
    private final Gson gson;
    private final String directory;

    public SimpleJsonResourceReloadListener(Gson gson, String string) {
        this.gson = gson;
        this.directory = string;
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        HashMap hashMap = Maps.newHashMap();
        int n = this.directory.length() + 1;
        for (ResourceLocation resourceLocation : resourceManager.listResources(this.directory, string -> string.endsWith(".json"))) {
            String string2 = resourceLocation.getPath();
            ResourceLocation resourceLocation2 = new ResourceLocation(resourceLocation.getNamespace(), string2.substring(n, string2.length() - PATH_SUFFIX_LENGTH));
            try {
                Resource resource = resourceManager.getResource(resourceLocation);
                Throwable throwable = null;
                try {
                    InputStream inputStream = resource.getInputStream();
                    Throwable throwable2 = null;
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                        Throwable throwable3 = null;
                        try {
                            JsonElement jsonElement = GsonHelper.fromJson(this.gson, (Reader)bufferedReader, JsonElement.class);
                            if (jsonElement != null) {
                                JsonElement jsonElement2 = hashMap.put(resourceLocation2, jsonElement);
                                if (jsonElement2 == null) continue;
                                throw new IllegalStateException("Duplicate data file ignored with ID " + resourceLocation2);
                            }
                            LOGGER.error("Couldn't load data file {} from {} as it's null or empty", (Object)resourceLocation2, (Object)resourceLocation);
                        }
                        catch (Throwable throwable4) {
                            throwable3 = throwable4;
                            throw throwable4;
                        }
                        finally {
                            if (bufferedReader == null) continue;
                            if (throwable3 != null) {
                                try {
                                    ((Reader)bufferedReader).close();
                                }
                                catch (Throwable throwable5) {
                                    throwable3.addSuppressed(throwable5);
                                }
                                continue;
                            }
                            ((Reader)bufferedReader).close();
                        }
                    }
                    catch (Throwable throwable6) {
                        throwable2 = throwable6;
                        throw throwable6;
                    }
                    finally {
                        if (inputStream == null) continue;
                        if (throwable2 != null) {
                            try {
                                inputStream.close();
                            }
                            catch (Throwable throwable7) {
                                throwable2.addSuppressed(throwable7);
                            }
                            continue;
                        }
                        inputStream.close();
                    }
                }
                catch (Throwable throwable8) {
                    throwable = throwable8;
                    throw throwable8;
                }
                finally {
                    if (resource == null) continue;
                    if (throwable != null) {
                        try {
                            resource.close();
                        }
                        catch (Throwable throwable9) {
                            throwable.addSuppressed(throwable9);
                        }
                        continue;
                    }
                    resource.close();
                }
            }
            catch (JsonParseException | IOException | IllegalArgumentException throwable) {
                LOGGER.error("Couldn't parse data file {} from {}", (Object)resourceLocation2, (Object)resourceLocation, (Object)throwable);
            }
        }
        return hashMap;
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return this.prepare(resourceManager, profilerFiller);
    }
}

