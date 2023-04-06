/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagTypes;

public class NbtIo {
    public static CompoundTag readCompressed(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file);){
            CompoundTag compoundTag = NbtIo.readCompressed(fileInputStream);
            return compoundTag;
        }
    }

    public static CompoundTag readCompressed(InputStream inputStream) throws IOException {
        try (DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputStream)));){
            CompoundTag compoundTag = NbtIo.read(dataInputStream, NbtAccounter.UNLIMITED);
            return compoundTag;
        }
    }

    public static void writeCompressed(CompoundTag compoundTag, File file) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);){
            NbtIo.writeCompressed(compoundTag, fileOutputStream);
        }
    }

    public static void writeCompressed(CompoundTag compoundTag, OutputStream outputStream) throws IOException {
        try (DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)));){
            NbtIo.write(compoundTag, dataOutputStream);
        }
    }

    public static void write(CompoundTag compoundTag, File file) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);){
            NbtIo.write(compoundTag, dataOutputStream);
        }
    }

    /*
     * Exception decompiling
     */
    @Nullable
    public static CompoundTag read(File var0) throws IOException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 4[TRYBLOCK]
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

    public static CompoundTag read(DataInput dataInput) throws IOException {
        return NbtIo.read(dataInput, NbtAccounter.UNLIMITED);
    }

    public static CompoundTag read(DataInput dataInput, NbtAccounter nbtAccounter) throws IOException {
        Tag tag = NbtIo.readUnnamedTag(dataInput, 0, nbtAccounter);
        if (tag instanceof CompoundTag) {
            return (CompoundTag)tag;
        }
        throw new IOException("Root tag must be a named compound tag");
    }

    public static void write(CompoundTag compoundTag, DataOutput dataOutput) throws IOException {
        NbtIo.writeUnnamedTag(compoundTag, dataOutput);
    }

    private static void writeUnnamedTag(Tag tag, DataOutput dataOutput) throws IOException {
        dataOutput.writeByte(tag.getId());
        if (tag.getId() == 0) {
            return;
        }
        dataOutput.writeUTF("");
        tag.write(dataOutput);
    }

    private static Tag readUnnamedTag(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
        byte by = dataInput.readByte();
        if (by == 0) {
            return EndTag.INSTANCE;
        }
        dataInput.readUTF();
        try {
            return TagTypes.getType(by).load(dataInput, n, nbtAccounter);
        }
        catch (IOException iOException) {
            CrashReport crashReport = CrashReport.forThrowable(iOException, "Loading NBT data");
            CrashReportCategory crashReportCategory = crashReport.addCategory("NBT Tag");
            crashReportCategory.setDetail("Tag type", by);
            throw new ReportedException(crashReport);
        }
    }
}

