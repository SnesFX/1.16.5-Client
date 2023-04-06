/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.resources.AssetIndex;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class DirectAssetIndex
extends AssetIndex {
    private final File assetsDirectory;

    public DirectAssetIndex(File file) {
        this.assetsDirectory = file;
    }

    @Override
    public File getFile(ResourceLocation resourceLocation) {
        return new File(this.assetsDirectory, resourceLocation.toString().replace(':', '/'));
    }

    @Override
    public File getRootFile(String string) {
        return new File(this.assetsDirectory, string);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Collection<ResourceLocation> getFiles(String string, String string2, int n, Predicate<String> predicate) {
        Path path3 = this.assetsDirectory.toPath().resolve(string2);
        try {
            try (Stream<Path> stream = Files.walk(path3.resolve(string), n, new FileVisitOption[0]);){
                Collection collection = stream.filter(path -> Files.isRegularFile(path, new LinkOption[0])).filter(path -> !path.endsWith(".mcmeta")).filter(path -> predicate.test(path.getFileName().toString())).map(path2 -> new ResourceLocation(string2, path3.relativize((Path)path2).toString().replaceAll("\\\\", "/"))).collect(Collectors.toList());
                return collection;
            }
        }
        catch (NoSuchFileException noSuchFileException) {
            return Collections.emptyList();
        }
        catch (IOException iOException) {
            LOGGER.warn("Unable to getFiles on {}", (Object)string, (Object)iOException);
        }
        return Collections.emptyList();
    }
}

