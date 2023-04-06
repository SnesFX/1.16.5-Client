/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;

public class RegionFileVersion {
    private static final Int2ObjectMap<RegionFileVersion> VERSIONS = new Int2ObjectOpenHashMap();
    public static final RegionFileVersion VERSION_GZIP = RegionFileVersion.register(new RegionFileVersion(1, GZIPInputStream::new, GZIPOutputStream::new));
    public static final RegionFileVersion VERSION_DEFLATE = RegionFileVersion.register(new RegionFileVersion(2, InflaterInputStream::new, DeflaterOutputStream::new));
    public static final RegionFileVersion VERSION_NONE = RegionFileVersion.register(new RegionFileVersion(3, inputStream -> inputStream, outputStream -> outputStream));
    private final int id;
    private final StreamWrapper<InputStream> inputWrapper;
    private final StreamWrapper<OutputStream> outputWrapper;

    private RegionFileVersion(int n, StreamWrapper<InputStream> streamWrapper, StreamWrapper<OutputStream> streamWrapper2) {
        this.id = n;
        this.inputWrapper = streamWrapper;
        this.outputWrapper = streamWrapper2;
    }

    private static RegionFileVersion register(RegionFileVersion regionFileVersion) {
        VERSIONS.put(regionFileVersion.id, (Object)regionFileVersion);
        return regionFileVersion;
    }

    @Nullable
    public static RegionFileVersion fromId(int n) {
        return (RegionFileVersion)VERSIONS.get(n);
    }

    public static boolean isValidVersion(int n) {
        return VERSIONS.containsKey(n);
    }

    public int getId() {
        return this.id;
    }

    public OutputStream wrap(OutputStream outputStream) throws IOException {
        return this.outputWrapper.wrap(outputStream);
    }

    public InputStream wrap(InputStream inputStream) throws IOException {
        return this.inputWrapper.wrap(inputStream);
    }

    @FunctionalInterface
    static interface StreamWrapper<O> {
        public O wrap(O var1) throws IOException;
    }

}

