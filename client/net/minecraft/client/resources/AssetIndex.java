/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AssetIndex {
    protected static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, File> rootFiles = Maps.newHashMap();
    private final Map<ResourceLocation, File> namespacedFiles = Maps.newHashMap();

    protected AssetIndex() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AssetIndex(File file, String string) {
        File file2 = new File(file, "objects");
        File file3 = new File(file, "indexes/" + string + ".json");
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = Files.newReader((File)file3, (Charset)StandardCharsets.UTF_8);
            JsonObject jsonObject = GsonHelper.parse(bufferedReader);
            JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "objects", null);
            if (jsonObject2 != null) {
                for (Map.Entry entry : jsonObject2.entrySet()) {
                    JsonObject jsonObject3 = (JsonObject)entry.getValue();
                    String string2 = (String)entry.getKey();
                    String[] arrstring = string2.split("/", 2);
                    String string3 = GsonHelper.getAsString(jsonObject3, "hash");
                    File file4 = new File(file2, string3.substring(0, 2) + "/" + string3);
                    if (arrstring.length == 1) {
                        this.rootFiles.put(arrstring[0], file4);
                        continue;
                    }
                    this.namespacedFiles.put(new ResourceLocation(arrstring[0], arrstring[1]), file4);
                }
            }
        }
        catch (JsonParseException jsonParseException) {
            LOGGER.error("Unable to parse resource index file: {}", (Object)file3);
        }
        catch (FileNotFoundException fileNotFoundException) {
            LOGGER.error("Can't find the resource index file: {}", (Object)file3);
        }
        finally {
            IOUtils.closeQuietly((Reader)bufferedReader);
        }
    }

    @Nullable
    public File getFile(ResourceLocation resourceLocation) {
        return this.namespacedFiles.get(resourceLocation);
    }

    @Nullable
    public File getRootFile(String string) {
        return this.rootFiles.get(string);
    }

    public Collection<ResourceLocation> getFiles(String string, String string2, int n, Predicate<String> predicate) {
        return this.namespacedFiles.keySet().stream().filter(resourceLocation -> {
            String string3 = resourceLocation.getPath();
            return resourceLocation.getNamespace().equals(string2) && !string3.endsWith(".mcmeta") && string3.startsWith(string + "/") && predicate.test(string3);
        }).collect(Collectors.toList());
    }
}

