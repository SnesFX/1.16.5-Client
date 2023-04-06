/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.server.packs;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import org.apache.commons.io.IOUtils;

public class FilePackResources
extends AbstractPackResources {
    public static final Splitter SPLITTER = Splitter.on((char)'/').omitEmptyStrings().limit(3);
    private ZipFile zipFile;

    public FilePackResources(File file) {
        super(file);
    }

    private ZipFile getOrCreateZipFile() throws IOException {
        if (this.zipFile == null) {
            this.zipFile = new ZipFile(this.file);
        }
        return this.zipFile;
    }

    @Override
    protected InputStream getResource(String string) throws IOException {
        ZipFile zipFile = this.getOrCreateZipFile();
        ZipEntry zipEntry = zipFile.getEntry(string);
        if (zipEntry == null) {
            throw new ResourcePackFileNotFoundException(this.file, string);
        }
        return zipFile.getInputStream(zipEntry);
    }

    @Override
    public boolean hasResource(String string) {
        try {
            return this.getOrCreateZipFile().getEntry(string) != null;
        }
        catch (IOException iOException) {
            return false;
        }
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        ZipFile zipFile;
        try {
            zipFile = this.getOrCreateZipFile();
        }
        catch (IOException iOException) {
            return Collections.emptySet();
        }
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        HashSet hashSet = Sets.newHashSet();
        while (enumeration.hasMoreElements()) {
            ArrayList arrayList;
            ZipEntry zipEntry = enumeration.nextElement();
            String string = zipEntry.getName();
            if (!string.startsWith(packType.getDirectory() + "/") || (arrayList = Lists.newArrayList((Iterable)SPLITTER.split((CharSequence)string))).size() <= 1) continue;
            String string2 = (String)arrayList.get(1);
            if (string2.equals(string2.toLowerCase(Locale.ROOT))) {
                hashSet.add(string2);
                continue;
            }
            this.logWarning(string2);
        }
        return hashSet;
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    @Override
    public void close() {
        if (this.zipFile != null) {
            IOUtils.closeQuietly((Closeable)this.zipFile);
            this.zipFile = null;
        }
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType packType, String string, String string2, int n, Predicate<String> predicate) {
        ZipFile zipFile;
        try {
            zipFile = this.getOrCreateZipFile();
        }
        catch (IOException iOException) {
            return Collections.emptySet();
        }
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        ArrayList arrayList = Lists.newArrayList();
        String string3 = packType.getDirectory() + "/" + string + "/";
        String string4 = string3 + string2 + "/";
        while (enumeration.hasMoreElements()) {
            String[] arrstring;
            String string5;
            String string6;
            ZipEntry zipEntry = enumeration.nextElement();
            if (zipEntry.isDirectory() || (string5 = zipEntry.getName()).endsWith(".mcmeta") || !string5.startsWith(string4) || (arrstring = (string6 = string5.substring(string3.length())).split("/")).length < n + 1 || !predicate.test(arrstring[arrstring.length - 1])) continue;
            arrayList.add(new ResourceLocation(string, string6));
        }
        return arrayList;
    }
}

