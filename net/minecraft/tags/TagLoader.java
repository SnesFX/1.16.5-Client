/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.tags;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagLoader<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final int PATH_SUFFIX_LENGTH = ".json".length();
    private final Function<ResourceLocation, Optional<T>> idToValue;
    private final String directory;
    private final String name;

    public TagLoader(Function<ResourceLocation, Optional<T>> function, String string, String string2) {
        this.idToValue = function;
        this.directory = string;
        this.name = string2;
    }

    public CompletableFuture<Map<ResourceLocation, Tag.Builder>> prepare(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            HashMap hashMap = Maps.newHashMap();
            for (ResourceLocation resourceLocation2 : resourceManager.listResources(this.directory, string -> string.endsWith(".json"))) {
                String string2 = resourceLocation2.getPath();
                ResourceLocation resourceLocation3 = new ResourceLocation(resourceLocation2.getNamespace(), string2.substring(this.directory.length() + 1, string2.length() - PATH_SUFFIX_LENGTH));
                try {
                    for (Resource resource : resourceManager.getResources(resourceLocation2)) {
                        try {
                            InputStream inputStream = resource.getInputStream();
                            Throwable throwable = null;
                            try {
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                                Throwable throwable2 = null;
                                try {
                                    JsonObject jsonObject = GsonHelper.fromJson(GSON, (Reader)bufferedReader, JsonObject.class);
                                    if (jsonObject == null) {
                                        LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it is empty or null", (Object)this.name, (Object)resourceLocation3, (Object)resourceLocation2, (Object)resource.getSourceName());
                                        continue;
                                    }
                                    hashMap.computeIfAbsent(resourceLocation3, resourceLocation -> Tag.Builder.tag()).addFromJson(jsonObject, resource.getSourceName());
                                }
                                catch (Throwable throwable3) {
                                    throwable2 = throwable3;
                                    throw throwable3;
                                }
                                finally {
                                    if (bufferedReader == null) continue;
                                    if (throwable2 != null) {
                                        try {
                                            ((Reader)bufferedReader).close();
                                        }
                                        catch (Throwable throwable4) {
                                            throwable2.addSuppressed(throwable4);
                                        }
                                        continue;
                                    }
                                    ((Reader)bufferedReader).close();
                                }
                            }
                            catch (Throwable throwable5) {
                                throwable = throwable5;
                                throw throwable5;
                            }
                            finally {
                                if (inputStream == null) continue;
                                if (throwable != null) {
                                    try {
                                        inputStream.close();
                                    }
                                    catch (Throwable throwable6) {
                                        throwable.addSuppressed(throwable6);
                                    }
                                    continue;
                                }
                                inputStream.close();
                            }
                        }
                        catch (IOException | RuntimeException exception) {
                            LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", (Object)this.name, (Object)resourceLocation3, (Object)resourceLocation2, (Object)resource.getSourceName(), (Object)exception);
                        }
                        finally {
                            IOUtils.closeQuietly((Closeable)resource);
                        }
                    }
                }
                catch (IOException iOException) {
                    LOGGER.error("Couldn't read {} tag list {} from {}", (Object)this.name, (Object)resourceLocation3, (Object)resourceLocation2, (Object)iOException);
                }
            }
            return hashMap;
        }, executor);
    }

    public TagCollection<T> load(Map<ResourceLocation, Tag.Builder> map) {
        HashMap hashMap = Maps.newHashMap();
        Function function = hashMap::get;
        Function<ResourceLocation, Object> function2 = resourceLocation -> this.idToValue.apply((ResourceLocation)resourceLocation).orElse(null);
        while (!map.isEmpty()) {
            boolean bl = false;
            Iterator<Map.Entry<ResourceLocation, Tag.Builder>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ResourceLocation, Tag.Builder> entry = iterator.next();
                Optional<Tag<Object>> optional = entry.getValue().build(function, function2);
                if (!optional.isPresent()) continue;
                hashMap.put(entry.getKey(), optional.get());
                iterator.remove();
                bl = true;
            }
            if (bl) continue;
            break;
        }
        map.forEach((resourceLocation, builder) -> LOGGER.error("Couldn't load {} tag {} as it is missing following references: {}", (Object)this.name, resourceLocation, (Object)builder.getUnresolvedEntries(function, function2).map(Objects::toString).collect(Collectors.joining(","))));
        return TagCollection.of(hashMap);
    }
}

