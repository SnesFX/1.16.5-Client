/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.packs;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaPackResources
implements PackResources {
    public static Path generatedDir;
    private static final Logger LOGGER;
    public static Class<?> clientObject;
    private static final Map<PackType, FileSystem> JAR_FILESYSTEM_BY_TYPE;
    public final Set<String> namespaces;

    public VanillaPackResources(String ... arrstring) {
        this.namespaces = ImmutableSet.copyOf((Object[])arrstring);
    }

    @Override
    public InputStream getRootResource(String string) throws IOException {
        Path path;
        if (string.contains("/") || string.contains("\\")) {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
        if (generatedDir != null && Files.exists(path = generatedDir.resolve(string), new LinkOption[0])) {
            return Files.newInputStream(path, new OpenOption[0]);
        }
        return this.getResourceAsStream(string);
    }

    @Override
    public InputStream getResource(PackType packType, ResourceLocation resourceLocation) throws IOException {
        InputStream inputStream = this.getResourceAsStream(packType, resourceLocation);
        if (inputStream != null) {
            return inputStream;
        }
        throw new FileNotFoundException(resourceLocation.getPath());
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType packType, String string, String string2, int n, Predicate<String> predicate) {
        Enumeration<URL> enumeration;
        URI uRI;
        HashSet hashSet = Sets.newHashSet();
        if (generatedDir != null) {
            try {
                VanillaPackResources.getResources(hashSet, n, string, generatedDir.resolve(packType.getDirectory()), string2, predicate);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            if (packType == PackType.CLIENT_RESOURCES) {
                enumeration = null;
                try {
                    enumeration = clientObject.getClassLoader().getResources(packType.getDirectory() + "/");
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                while (enumeration != null && enumeration.hasMoreElements()) {
                    try {
                        uRI = ((URL)enumeration.nextElement()).toURI();
                        if (!"file".equals(uRI.getScheme())) continue;
                        VanillaPackResources.getResources(hashSet, n, string, Paths.get(uRI), string2, predicate);
                    }
                    catch (IOException | URISyntaxException exception) {}
                }
            }
        }
        try {
            enumeration = VanillaPackResources.class.getResource("/" + packType.getDirectory() + "/.mcassetsroot");
            if (enumeration == null) {
                LOGGER.error("Couldn't find .mcassetsroot, cannot load vanilla resources");
                return hashSet;
            }
            uRI = ((URL)((Object)enumeration)).toURI();
            if ("file".equals(uRI.getScheme())) {
                URL uRL = new URL(((URL)((Object)enumeration)).toString().substring(0, ((URL)((Object)enumeration)).toString().length() - ".mcassetsroot".length()));
                Path path = Paths.get(uRL.toURI());
                VanillaPackResources.getResources(hashSet, n, string, path, string2, predicate);
            } else if ("jar".equals(uRI.getScheme())) {
                Path path = JAR_FILESYSTEM_BY_TYPE.get((Object)packType).getPath("/" + packType.getDirectory(), new String[0]);
                VanillaPackResources.getResources(hashSet, n, "minecraft", path, string2, predicate);
            } else {
                LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", (Object)uRI);
            }
        }
        catch (FileNotFoundException | NoSuchFileException iOException) {
        }
        catch (IOException | URISyntaxException exception) {
            LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)exception);
        }
        return hashSet;
    }

    private static void getResources(Collection<ResourceLocation> collection, int n, String string, Path path3, String string2, Predicate<String> predicate) throws IOException {
        Path path4 = path3.resolve(string);
        try (Stream<Path> stream = Files.walk(path4.resolve(string2), n, new FileVisitOption[0]);){
            stream.filter(path -> !path.endsWith(".mcmeta") && Files.isRegularFile(path, new LinkOption[0]) && predicate.test(path.getFileName().toString())).map(path2 -> new ResourceLocation(string, path4.relativize((Path)path2).toString().replaceAll("\\\\", "/"))).forEach(collection::add);
        }
    }

    @Nullable
    protected InputStream getResourceAsStream(PackType packType, ResourceLocation resourceLocation) {
        Object object;
        String string = VanillaPackResources.createPath(packType, resourceLocation);
        if (generatedDir != null && Files.exists((Path)(object = generatedDir.resolve(packType.getDirectory() + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath())), new LinkOption[0])) {
            try {
                return Files.newInputStream((Path)object, new OpenOption[0]);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        try {
            object = VanillaPackResources.class.getResource(string);
            if (VanillaPackResources.isResourceUrlValid(string, (URL)object)) {
                return ((URL)object).openStream();
            }
        }
        catch (IOException iOException) {
            return VanillaPackResources.class.getResourceAsStream(string);
        }
        return null;
    }

    private static String createPath(PackType packType, ResourceLocation resourceLocation) {
        return "/" + packType.getDirectory() + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath();
    }

    private static boolean isResourceUrlValid(String string, @Nullable URL uRL) throws IOException {
        return uRL != null && (uRL.getProtocol().equals("jar") || FolderPackResources.validatePath(new File(uRL.getFile()), string));
    }

    @Nullable
    protected InputStream getResourceAsStream(String string) {
        return VanillaPackResources.class.getResourceAsStream("/" + string);
    }

    @Override
    public boolean hasResource(PackType packType, ResourceLocation resourceLocation) {
        Object object;
        String string = VanillaPackResources.createPath(packType, resourceLocation);
        if (generatedDir != null && Files.exists((Path)(object = generatedDir.resolve(packType.getDirectory() + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath())), new LinkOption[0])) {
            return true;
        }
        try {
            object = VanillaPackResources.class.getResource(string);
            return VanillaPackResources.isResourceUrlValid(string, (URL)object);
        }
        catch (IOException iOException) {
            return false;
        }
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return this.namespaces;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> metadataSectionSerializer) throws IOException {
        try {
            try (InputStream inputStream = this.getRootResource("pack.mcmeta");){
                T t = AbstractPackResources.getMetadataFromStream(metadataSectionSerializer, inputStream);
                return t;
            }
        }
        catch (FileNotFoundException | RuntimeException exception) {
            return null;
        }
    }

    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public void close() {
    }

    static {
        LOGGER = LogManager.getLogger();
        JAR_FILESYSTEM_BY_TYPE = Util.make(Maps.newHashMap(), hashMap -> {
            Class<VanillaPackResources> class_ = VanillaPackResources.class;
            synchronized (VanillaPackResources.class) {
                for (PackType packType : PackType.values()) {
                    URL uRL = VanillaPackResources.class.getResource("/" + packType.getDirectory() + "/.mcassetsroot");
                    try {
                        FileSystem fileSystem;
                        URI uRI = uRL.toURI();
                        if (!"jar".equals(uRI.getScheme())) continue;
                        try {
                            fileSystem = FileSystems.getFileSystem(uRI);
                        }
                        catch (FileSystemNotFoundException fileSystemNotFoundException) {
                            fileSystem = FileSystems.newFileSystem(uRI, Collections.emptyMap());
                        }
                        hashMap.put(packType, fileSystem);
                    }
                    catch (IOException | URISyntaxException exception) {
                        LOGGER.error("Couldn't get a list of all vanilla resources", (Throwable)exception);
                    }
                }
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return;
            }
        });
    }
}

