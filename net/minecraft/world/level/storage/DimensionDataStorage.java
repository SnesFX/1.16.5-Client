/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFixer
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionDataStorage {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, SavedData> cache = Maps.newHashMap();
    private final DataFixer fixerUpper;
    private final File dataFolder;

    public DimensionDataStorage(File file, DataFixer dataFixer) {
        this.fixerUpper = dataFixer;
        this.dataFolder = file;
    }

    private File getDataFile(String string) {
        return new File(this.dataFolder, string + ".dat");
    }

    public <T extends SavedData> T computeIfAbsent(Supplier<T> supplier, String string) {
        T t = this.get(supplier, string);
        if (t != null) {
            return t;
        }
        SavedData savedData = (SavedData)supplier.get();
        this.set(savedData);
        return (T)savedData;
    }

    @Nullable
    public <T extends SavedData> T get(Supplier<T> supplier, String string) {
        SavedData savedData = this.cache.get(string);
        if (savedData == null && !this.cache.containsKey(string)) {
            savedData = this.readSavedData(supplier, string);
            this.cache.put(string, savedData);
        }
        return (T)savedData;
    }

    @Nullable
    private <T extends SavedData> T readSavedData(Supplier<T> supplier, String string) {
        try {
            File file = this.getDataFile(string);
            if (file.exists()) {
                SavedData savedData = (SavedData)supplier.get();
                CompoundTag compoundTag = this.readTagFromDisk(string, SharedConstants.getCurrentVersion().getWorldVersion());
                savedData.load(compoundTag.getCompound("data"));
                return (T)savedData;
            }
        }
        catch (Exception exception) {
            LOGGER.error("Error loading saved data: {}", (Object)string, (Object)exception);
        }
        return null;
    }

    public void set(SavedData savedData) {
        this.cache.put(savedData.getId(), savedData);
    }

    /*
     * Exception decompiling
     */
    public CompoundTag readTagFromDisk(String var1_1, int var2_2) throws IOException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 8[TRYBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:619)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:699)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:188)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:133)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        throw new IllegalStateException("Decompilation failed");
    }

    private boolean isGzip(PushbackInputStream pushbackInputStream) throws IOException {
        int n;
        byte[] arrby = new byte[2];
        boolean bl = false;
        int n2 = pushbackInputStream.read(arrby, 0, 2);
        if (n2 == 2 && (n = (arrby[1] & 0xFF) << 8 | arrby[0] & 0xFF) == 35615) {
            bl = true;
        }
        if (n2 != 0) {
            pushbackInputStream.unread(arrby, 0, n2);
        }
        return bl;
    }

    public void save() {
        for (SavedData savedData : this.cache.values()) {
            if (savedData == null) continue;
            savedData.save(this.getDataFile(savedData.getId()));
        }
    }
}

