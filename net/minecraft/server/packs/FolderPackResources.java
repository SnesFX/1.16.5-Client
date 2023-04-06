/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.commons.io.filefilter.DirectoryFileFilter
 *  org.apache.commons.io.filefilter.IOFileFilter
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.packs;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FolderPackResources
extends AbstractPackResources {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean ON_WINDOWS = Util.getPlatform() == Util.OS.WINDOWS;
    private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is((char)'\\');

    public FolderPackResources(File file) {
        super(file);
    }

    public static boolean validatePath(File file, String string) throws IOException {
        String string2 = file.getCanonicalPath();
        if (ON_WINDOWS) {
            string2 = BACKSLASH_MATCHER.replaceFrom((CharSequence)string2, '/');
        }
        return string2.endsWith(string);
    }

    @Override
    protected InputStream getResource(String string) throws IOException {
        File file = this.getFile(string);
        if (file == null) {
            throw new ResourcePackFileNotFoundException(this.file, string);
        }
        return new FileInputStream(file);
    }

    @Override
    protected boolean hasResource(String string) {
        return this.getFile(string) != null;
    }

    @Nullable
    private File getFile(String string) {
        try {
            File file = new File(this.file, string);
            if (file.isFile() && FolderPackResources.validatePath(file, string)) {
                return file;
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        HashSet hashSet = Sets.newHashSet();
        File file = new File(this.file, packType.getDirectory());
        File[] arrfile = file.listFiles((FileFilter)DirectoryFileFilter.DIRECTORY);
        if (arrfile != null) {
            for (File file2 : arrfile) {
                String string = FolderPackResources.getRelativePath(file, file2);
                if (string.equals(string.toLowerCase(Locale.ROOT))) {
                    hashSet.add(string.substring(0, string.length() - 1));
                    continue;
                }
                this.logWarning(string);
            }
        }
        return hashSet;
    }

    @Override
    public void close() {
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType packType, String string, String string2, int n, Predicate<String> predicate) {
        File file = new File(this.file, packType.getDirectory());
        ArrayList arrayList = Lists.newArrayList();
        this.listResources(new File(new File(file, string), string2), n, string, arrayList, string2 + "/", predicate);
        return arrayList;
    }

    private void listResources(File file, int n, String string, List<ResourceLocation> list, String string2, Predicate<String> predicate) {
        File[] arrfile = file.listFiles();
        if (arrfile != null) {
            for (File file2 : arrfile) {
                if (file2.isDirectory()) {
                    if (n <= 0) continue;
                    this.listResources(file2, n - 1, string, list, string2 + file2.getName() + "/", predicate);
                    continue;
                }
                if (file2.getName().endsWith(".mcmeta") || !predicate.test(file2.getName())) continue;
                try {
                    list.add(new ResourceLocation(string, string2 + file2.getName()));
                }
                catch (ResourceLocationException resourceLocationException) {
                    LOGGER.error(resourceLocationException.getMessage());
                }
            }
        }
    }
}

