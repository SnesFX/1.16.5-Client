/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.UnmodifiableIterator
 *  com.google.common.io.Files
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.OptionalDynamic
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.OptionalDynamic;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.DirectoryLock;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.McRegionUpgrader;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelStorageSource {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
    private static final ImmutableList<String> OLD_SETTINGS_KEYS = ImmutableList.of((Object)"RandomSeed", (Object)"generatorName", (Object)"generatorOptions", (Object)"generatorVersion", (Object)"legacy_custom_options", (Object)"MapFeatures", (Object)"BonusChest");
    private final Path baseDir;
    private final Path backupDir;
    private final DataFixer fixerUpper;

    public LevelStorageSource(Path path, Path path2, DataFixer dataFixer) {
        this.fixerUpper = dataFixer;
        try {
            Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath(new LinkOption[0]) : path, new FileAttribute[0]);
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
        this.baseDir = path;
        this.backupDir = path2;
    }

    public static LevelStorageSource createDefault(Path path) {
        return new LevelStorageSource(path, path.resolve("../backups"), DataFixers.getDataFixer());
    }

    private static <T> Pair<WorldGenSettings, Lifecycle> readWorldGenSettings(Dynamic<T> dynamic, DataFixer dataFixer, int n) {
        String string2;
        Dynamic dynamic2 = dynamic.get("WorldGenSettings").orElseEmptyMap();
        for (String string2 : OLD_SETTINGS_KEYS) {
            Optional optional = dynamic.get(string2).result();
            if (!optional.isPresent()) continue;
            dynamic2 = dynamic2.set(string2, (Dynamic)optional.get());
        }
        UnmodifiableIterator unmodifiableIterator = dataFixer.update(References.WORLD_GEN_SETTINGS, dynamic2, n, SharedConstants.getCurrentVersion().getWorldVersion());
        string2 = WorldGenSettings.CODEC.parse((Dynamic)unmodifiableIterator);
        return Pair.of((Object)string2.resultOrPartial(Util.prefix("WorldGenSettings: ", ((Logger)LOGGER)::error)).orElseGet(() -> LevelStorageSource.lambda$readWorldGenSettings$3((Dynamic)unmodifiableIterator)), (Object)string2.lifecycle());
    }

    private static DataPackConfig readDataPackConfig(Dynamic<?> dynamic) {
        return DataPackConfig.CODEC.parse(dynamic).resultOrPartial(((Logger)LOGGER)::error).orElse(DataPackConfig.DEFAULT);
    }

    public List<LevelSummary> getLevelList() throws LevelStorageException {
        File[] arrfile;
        if (!Files.isDirectory(this.baseDir, new LinkOption[0])) {
            throw new LevelStorageException(new TranslatableComponent("selectWorld.load_folder_access").getString());
        }
        ArrayList arrayList = Lists.newArrayList();
        for (File file : arrfile = this.baseDir.toFile().listFiles()) {
            boolean bl;
            if (!file.isDirectory()) continue;
            try {
                bl = DirectoryLock.isLocked(file.toPath());
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to read {} lock", (Object)file, (Object)exception);
                continue;
            }
            LevelSummary levelSummary = this.readLevelData(file, this.levelSummaryReader(file, bl));
            if (levelSummary == null) continue;
            arrayList.add(levelSummary);
        }
        return arrayList;
    }

    private int getStorageVersion() {
        return 19133;
    }

    @Nullable
    private <T> T readLevelData(File file, BiFunction<File, DataFixer, T> biFunction) {
        T t;
        if (!file.exists()) {
            return null;
        }
        File file2 = new File(file, "level.dat");
        if (file2.exists() && (t = biFunction.apply(file2, this.fixerUpper)) != null) {
            return t;
        }
        file2 = new File(file, "level.dat_old");
        if (file2.exists()) {
            return biFunction.apply(file2, this.fixerUpper);
        }
        return null;
    }

    @Nullable
    private static DataPackConfig getDataPacks(File file, DataFixer dataFixer) {
        try {
            CompoundTag compoundTag = NbtIo.readCompressed(file);
            CompoundTag compoundTag2 = compoundTag.getCompound("Data");
            compoundTag2.remove("Player");
            int n = compoundTag2.contains("DataVersion", 99) ? compoundTag2.getInt("DataVersion") : -1;
            Dynamic dynamic = dataFixer.update(DataFixTypes.LEVEL.getType(), new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)compoundTag2), n, SharedConstants.getCurrentVersion().getWorldVersion());
            return dynamic.get("DataPacks").result().map(LevelStorageSource::readDataPackConfig).orElse(DataPackConfig.DEFAULT);
        }
        catch (Exception exception) {
            LOGGER.error("Exception reading {}", (Object)file, (Object)exception);
            return null;
        }
    }

    private static BiFunction<File, DataFixer, PrimaryLevelData> getLevelData(DynamicOps<Tag> dynamicOps, DataPackConfig dataPackConfig) {
        return (file, dataFixer) -> {
            try {
                CompoundTag compoundTag = NbtIo.readCompressed(file);
                CompoundTag compoundTag2 = compoundTag.getCompound("Data");
                CompoundTag compoundTag3 = compoundTag2.contains("Player", 10) ? compoundTag2.getCompound("Player") : null;
                compoundTag2.remove("Player");
                int n = compoundTag2.contains("DataVersion", 99) ? compoundTag2.getInt("DataVersion") : -1;
                Dynamic dynamic = dataFixer.update(DataFixTypes.LEVEL.getType(), new Dynamic(dynamicOps, (Object)compoundTag2), n, SharedConstants.getCurrentVersion().getWorldVersion());
                Pair<WorldGenSettings, Lifecycle> pair = LevelStorageSource.readWorldGenSettings(dynamic, dataFixer, n);
                LevelVersion levelVersion = LevelVersion.parse(dynamic);
                LevelSettings levelSettings = LevelSettings.parse(dynamic, dataPackConfig);
                return PrimaryLevelData.parse((Dynamic<Tag>)dynamic, dataFixer, n, compoundTag3, levelSettings, levelVersion, (WorldGenSettings)pair.getFirst(), (Lifecycle)pair.getSecond());
            }
            catch (Exception exception) {
                LOGGER.error("Exception reading {}", file, (Object)exception);
                return null;
            }
        };
    }

    private BiFunction<File, DataFixer, LevelSummary> levelSummaryReader(File file, boolean bl) {
        return (file2, dataFixer) -> {
            try {
                CompoundTag compoundTag = NbtIo.readCompressed(file2);
                CompoundTag compoundTag2 = compoundTag.getCompound("Data");
                compoundTag2.remove("Player");
                int n = compoundTag2.contains("DataVersion", 99) ? compoundTag2.getInt("DataVersion") : -1;
                Dynamic dynamic = dataFixer.update(DataFixTypes.LEVEL.getType(), new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)compoundTag2), n, SharedConstants.getCurrentVersion().getWorldVersion());
                LevelVersion levelVersion = LevelVersion.parse(dynamic);
                int n2 = levelVersion.levelDataVersion();
                if (n2 == 19132 || n2 == 19133) {
                    boolean bl2 = n2 != this.getStorageVersion();
                    File file3 = new File(file, "icon.png");
                    DataPackConfig dataPackConfig = dynamic.get("DataPacks").result().map(LevelStorageSource::readDataPackConfig).orElse(DataPackConfig.DEFAULT);
                    LevelSettings levelSettings = LevelSettings.parse(dynamic, dataPackConfig);
                    return new LevelSummary(levelSettings, levelVersion, file.getName(), bl2, bl, file3);
                }
                return null;
            }
            catch (Exception exception) {
                LOGGER.error("Exception reading {}", file2, (Object)exception);
                return null;
            }
        };
    }

    public boolean isNewLevelIdAcceptable(String string) {
        try {
            Path path = this.baseDir.resolve(string);
            Files.createDirectory(path, new FileAttribute[0]);
            Files.deleteIfExists(path);
            return true;
        }
        catch (IOException iOException) {
            return false;
        }
    }

    public boolean levelExists(String string) {
        return Files.isDirectory(this.baseDir.resolve(string), new LinkOption[0]);
    }

    public Path getBaseDir() {
        return this.baseDir;
    }

    public Path getBackupPath() {
        return this.backupDir;
    }

    public LevelStorageAccess createAccess(String string) throws IOException {
        return new LevelStorageAccess(string);
    }

    private static /* synthetic */ WorldGenSettings lambda$readWorldGenSettings$3(Dynamic dynamic) {
        Registry registry = (Registry)RegistryLookupCodec.create(Registry.DIMENSION_TYPE_REGISTRY).codec().parse(dynamic).resultOrPartial(Util.prefix("Dimension type registry: ", ((Logger)LOGGER)::error)).orElseThrow(() -> new IllegalStateException("Failed to get dimension registry"));
        Registry registry2 = (Registry)RegistryLookupCodec.create(Registry.BIOME_REGISTRY).codec().parse(dynamic).resultOrPartial(Util.prefix("Biome registry: ", ((Logger)LOGGER)::error)).orElseThrow(() -> new IllegalStateException("Failed to get biome registry"));
        Registry registry3 = (Registry)RegistryLookupCodec.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).codec().parse(dynamic).resultOrPartial(Util.prefix("Noise settings registry: ", ((Logger)LOGGER)::error)).orElseThrow(() -> new IllegalStateException("Failed to get noise settings registry"));
        return WorldGenSettings.makeDefault(registry, registry2, registry3);
    }

    public class LevelStorageAccess
    implements AutoCloseable {
        private final DirectoryLock lock;
        private final Path levelPath;
        private final String levelId;
        private final Map<LevelResource, Path> resources = Maps.newHashMap();

        public LevelStorageAccess(String string) throws IOException {
            this.levelId = string;
            this.levelPath = LevelStorageSource.this.baseDir.resolve(string);
            this.lock = DirectoryLock.create(this.levelPath);
        }

        public String getLevelId() {
            return this.levelId;
        }

        public Path getLevelPath(LevelResource levelResource2) {
            return this.resources.computeIfAbsent(levelResource2, levelResource -> this.levelPath.resolve(levelResource.getId()));
        }

        public File getDimensionPath(ResourceKey<Level> resourceKey) {
            return DimensionType.getStorageFolder(resourceKey, this.levelPath.toFile());
        }

        private void checkLock() {
            if (!this.lock.isValid()) {
                throw new IllegalStateException("Lock is no longer valid");
            }
        }

        public PlayerDataStorage createPlayerStorage() {
            this.checkLock();
            return new PlayerDataStorage(this, LevelStorageSource.this.fixerUpper);
        }

        public boolean requiresConversion() {
            LevelSummary levelSummary = this.getSummary();
            return levelSummary != null && levelSummary.levelVersion().levelDataVersion() != LevelStorageSource.this.getStorageVersion();
        }

        public boolean convertLevel(ProgressListener progressListener) {
            this.checkLock();
            return McRegionUpgrader.convertLevel(this, progressListener);
        }

        @Nullable
        public LevelSummary getSummary() {
            this.checkLock();
            return (LevelSummary)LevelStorageSource.this.readLevelData(this.levelPath.toFile(), LevelStorageSource.this.levelSummaryReader(this.levelPath.toFile(), false));
        }

        @Nullable
        public WorldData getDataTag(DynamicOps<Tag> dynamicOps, DataPackConfig dataPackConfig) {
            this.checkLock();
            return (WorldData)LevelStorageSource.this.readLevelData(this.levelPath.toFile(), LevelStorageSource.getLevelData((DynamicOps<Tag>)dynamicOps, dataPackConfig));
        }

        @Nullable
        public DataPackConfig getDataPacks() {
            this.checkLock();
            return (DataPackConfig)LevelStorageSource.this.readLevelData(this.levelPath.toFile(), (file, dataFixer) -> LevelStorageSource.getDataPacks(file, dataFixer));
        }

        public void saveDataTag(RegistryAccess registryAccess, WorldData worldData) {
            this.saveDataTag(registryAccess, worldData, null);
        }

        public void saveDataTag(RegistryAccess registryAccess, WorldData worldData, @Nullable CompoundTag compoundTag) {
            File file = this.levelPath.toFile();
            CompoundTag compoundTag2 = worldData.createTag(registryAccess, compoundTag);
            CompoundTag compoundTag3 = new CompoundTag();
            compoundTag3.put("Data", compoundTag2);
            try {
                File file2 = File.createTempFile("level", ".dat", file);
                NbtIo.writeCompressed(compoundTag3, file2);
                File file3 = new File(file, "level.dat_old");
                File file4 = new File(file, "level.dat");
                Util.safeReplaceFile(file4, file2, file3);
            }
            catch (Exception exception) {
                LOGGER.error("Failed to save level {}", (Object)file, (Object)exception);
            }
        }

        public File getIconFile() {
            this.checkLock();
            return this.levelPath.resolve("icon.png").toFile();
        }

        public void deleteLevel() throws IOException {
            this.checkLock();
            final Path path = this.levelPath.resolve("session.lock");
            for (int i = 1; i <= 5; ++i) {
                LOGGER.info("Attempt {}...", (Object)i);
                try {
                    Files.walkFileTree(this.levelPath, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                        @Override
                        public FileVisitResult visitFile(Path path2, BasicFileAttributes basicFileAttributes) throws IOException {
                            if (!path2.equals(path)) {
                                LOGGER.debug("Deleting {}", (Object)path2);
                                Files.delete(path2);
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path path2, IOException iOException) throws IOException {
                            if (iOException != null) {
                                throw iOException;
                            }
                            if (path2.equals(LevelStorageAccess.this.levelPath)) {
                                LevelStorageAccess.this.lock.close();
                                Files.deleteIfExists(path);
                            }
                            Files.delete(path2);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public /* synthetic */ FileVisitResult postVisitDirectory(Object object, IOException iOException) throws IOException {
                            return this.postVisitDirectory((Path)object, iOException);
                        }

                        @Override
                        public /* synthetic */ FileVisitResult visitFile(Object object, BasicFileAttributes basicFileAttributes) throws IOException {
                            return this.visitFile((Path)object, basicFileAttributes);
                        }
                    });
                    break;
                }
                catch (IOException iOException) {
                    if (i < 5) {
                        LOGGER.warn("Failed to delete {}", (Object)this.levelPath, (Object)iOException);
                        try {
                            Thread.sleep(500L);
                        }
                        catch (InterruptedException interruptedException) {}
                        continue;
                    }
                    throw iOException;
                }
            }
        }

        public void renameLevel(String string) throws IOException {
            this.checkLock();
            File file = new File(LevelStorageSource.this.baseDir.toFile(), this.levelId);
            if (!file.exists()) {
                return;
            }
            File file2 = new File(file, "level.dat");
            if (file2.exists()) {
                CompoundTag compoundTag = NbtIo.readCompressed(file2);
                CompoundTag compoundTag2 = compoundTag.getCompound("Data");
                compoundTag2.putString("LevelName", string);
                NbtIo.writeCompressed(compoundTag, file2);
            }
        }

        public long makeWorldBackup() throws IOException {
            this.checkLock();
            String string = LocalDateTime.now().format(FORMATTER) + "_" + this.levelId;
            Path path = LevelStorageSource.this.getBackupPath();
            try {
                Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath(new LinkOption[0]) : path, new FileAttribute[0]);
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            Path path2 = path.resolve(FileUtil.findAvailableName(path, string, ".zip"));
            try (final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path2, new OpenOption[0])));){
                final Path path3 = Paths.get(this.levelId, new String[0]);
                Files.walkFileTree(this.levelPath, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                        if (path.endsWith("session.lock")) {
                            return FileVisitResult.CONTINUE;
                        }
                        String string = path3.resolve(LevelStorageAccess.this.levelPath.relativize(path)).toString().replace('\\', '/');
                        ZipEntry zipEntry = new ZipEntry(string);
                        zipOutputStream.putNextEntry(zipEntry);
                        com.google.common.io.Files.asByteSource((File)path.toFile()).copyTo((OutputStream)zipOutputStream);
                        zipOutputStream.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public /* synthetic */ FileVisitResult visitFile(Object object, BasicFileAttributes basicFileAttributes) throws IOException {
                        return this.visitFile((Path)object, basicFileAttributes);
                    }
                });
            }
            return Files.size(path2);
        }

        @Override
        public void close() throws IOException {
            this.lock.close();
        }

    }

}

