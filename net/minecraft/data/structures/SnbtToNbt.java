/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.HashFunction
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.structures;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SnbtToNbt
implements DataProvider {
    @Nullable
    private static final Path dumpSnbtTo = null;
    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator generator;
    private final List<Filter> filters = Lists.newArrayList();

    public SnbtToNbt(DataGenerator dataGenerator) {
        this.generator = dataGenerator;
    }

    public SnbtToNbt addFilter(Filter filter) {
        this.filters.add(filter);
        return this;
    }

    private CompoundTag applyFilters(String string, CompoundTag compoundTag) {
        CompoundTag compoundTag2 = compoundTag;
        for (Filter filter : this.filters) {
            compoundTag2 = filter.apply(string, compoundTag2);
        }
        return compoundTag2;
    }

    @Override
    public void run(HashCache hashCache) throws IOException {
        Path path3 = this.generator.getOutputFolder();
        ArrayList arrayList = Lists.newArrayList();
        for (Path path4 : this.generator.getInputFolders()) {
            Files.walk(path4, new FileVisitOption[0]).filter(path -> path.toString().endsWith(".snbt")).forEach(path2 -> arrayList.add(CompletableFuture.supplyAsync(() -> this.readStructure((Path)path2, this.getName(path4, (Path)path2)), Util.backgroundExecutor())));
        }
        Util.sequence(arrayList).join().stream().filter(Objects::nonNull).forEach(taskResult -> this.storeStructureIfChanged(hashCache, (TaskResult)taskResult, path3));
    }

    @Override
    public String getName() {
        return "SNBT -> NBT";
    }

    private String getName(Path path, Path path2) {
        String string = path.relativize(path2).toString().replaceAll("\\\\", "/");
        return string.substring(0, string.length() - ".snbt".length());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private TaskResult readStructure(Path path, String string) {
        try {
            try (BufferedReader bufferedReader = Files.newBufferedReader(path);){
                String string2 = IOUtils.toString((Reader)bufferedReader);
                CompoundTag compoundTag = this.applyFilters(string, TagParser.parseTag(string2));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                NbtIo.writeCompressed(compoundTag, byteArrayOutputStream);
                byte[] arrby = byteArrayOutputStream.toByteArray();
                String string3 = SHA1.hashBytes(arrby).toString();
                String string4 = dumpSnbtTo != null ? compoundTag.getPrettyDisplay("    ", 0).getString() + "\n" : null;
                TaskResult taskResult = new TaskResult(string, arrby, string4, string3);
                return taskResult;
            }
        }
        catch (CommandSyntaxException commandSyntaxException) {
            LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", (Object)string, (Object)path, (Object)commandSyntaxException);
            return null;
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", (Object)string, (Object)path, (Object)iOException);
        }
        return null;
    }

    private void storeStructureIfChanged(HashCache hashCache, TaskResult taskResult, Path path) {
        Path path2;
        if (taskResult.snbtPayload != null) {
            path2 = dumpSnbtTo.resolve(taskResult.name + ".snbt");
            try {
                FileUtils.write((File)path2.toFile(), (CharSequence)taskResult.snbtPayload, (Charset)StandardCharsets.UTF_8);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't write structure SNBT {} at {}", (Object)taskResult.name, (Object)path2, (Object)iOException);
            }
        }
        path2 = path.resolve(taskResult.name + ".nbt");
        try {
            if (!Objects.equals(hashCache.getHash(path2), taskResult.hash) || !Files.exists(path2, new LinkOption[0])) {
                Files.createDirectories(path2.getParent(), new FileAttribute[0]);
                try (OutputStream outputStream = Files.newOutputStream(path2, new OpenOption[0]);){
                    outputStream.write(taskResult.payload);
                }
            }
            hashCache.putNew(path2, taskResult.hash);
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't write structure {} at {}", (Object)taskResult.name, (Object)path2, (Object)iOException);
        }
    }

    @FunctionalInterface
    public static interface Filter {
        public CompoundTag apply(String var1, CompoundTag var2);
    }

    static class TaskResult {
        private final String name;
        private final byte[] payload;
        @Nullable
        private final String snbtPayload;
        private final String hash;

        public TaskResult(String string, byte[] arrby, @Nullable String string2, String string3) {
            this.name = string;
            this.payload = arrby;
            this.snbtPayload = string2;
            this.hash = string3;
        }
    }

}

