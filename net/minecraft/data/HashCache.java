/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HashCache {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path path;
    private final Path cachePath;
    private int hits;
    private final Map<Path, String> oldCache = Maps.newHashMap();
    private final Map<Path, String> newCache = Maps.newHashMap();
    private final Set<Path> keep = Sets.newHashSet();

    public HashCache(Path path2, String string2) throws IOException {
        this.path = path2;
        Path path3 = path2.resolve(".cache");
        Files.createDirectories(path3, new FileAttribute[0]);
        this.cachePath = path3.resolve(string2);
        this.walkOutputFiles().forEach(path -> this.oldCache.put((Path)path, ""));
        if (Files.isReadable(this.cachePath)) {
            IOUtils.readLines((InputStream)Files.newInputStream(this.cachePath, new OpenOption[0]), (Charset)Charsets.UTF_8).forEach(string -> {
                int n = string.indexOf(32);
                this.oldCache.put(path2.resolve(string.substring(n + 1)), string.substring(0, n));
            });
        }
    }

    public void purgeStaleAndWrite() throws IOException {
        BufferedWriter bufferedWriter;
        this.removeStale();
        try {
            bufferedWriter = Files.newBufferedWriter(this.cachePath, new OpenOption[0]);
        }
        catch (IOException iOException) {
            LOGGER.warn("Unable write cachefile {}: {}", (Object)this.cachePath, (Object)iOException.toString());
            return;
        }
        IOUtils.writeLines((Collection)this.newCache.entrySet().stream().map(entry -> (String)entry.getValue() + ' ' + this.path.relativize((Path)entry.getKey())).collect(Collectors.toList()), (String)System.lineSeparator(), (Writer)bufferedWriter);
        ((Writer)bufferedWriter).close();
        LOGGER.debug("Caching: cache hits: {}, created: {} removed: {}", (Object)this.hits, (Object)(this.newCache.size() - this.hits), (Object)this.oldCache.size());
    }

    @Nullable
    public String getHash(Path path) {
        return this.oldCache.get(path);
    }

    public void putNew(Path path, String string) {
        this.newCache.put(path, string);
        if (Objects.equals(this.oldCache.remove(path), string)) {
            ++this.hits;
        }
    }

    public boolean had(Path path) {
        return this.oldCache.containsKey(path);
    }

    public void keep(Path path) {
        this.keep.add(path);
    }

    private void removeStale() throws IOException {
        this.walkOutputFiles().forEach(path -> {
            if (this.had((Path)path) && !this.keep.contains(path)) {
                try {
                    Files.delete(path);
                }
                catch (IOException iOException) {
                    LOGGER.debug("Unable to delete: {} ({})", path, (Object)iOException.toString());
                }
            }
        });
    }

    private Stream<Path> walkOutputFiles() throws IOException {
        return Files.walk(this.path, new FileVisitOption[0]).filter(path -> !Objects.equals(this.cachePath, path) && !Files.isDirectory(path, new LinkOption[0]));
    }
}

