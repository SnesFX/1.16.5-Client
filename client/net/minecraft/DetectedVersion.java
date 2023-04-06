/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.bridge.game.GameVersion
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.bridge.game.GameVersion;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DetectedVersion
implements GameVersion {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final GameVersion BUILT_IN = new DetectedVersion();
    private final String id;
    private final String name;
    private final boolean stable;
    private final int worldVersion;
    private final int protocolVersion;
    private final int packVersion;
    private final Date buildTime;
    private final String releaseTarget;

    private DetectedVersion() {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.name = "1.16.5";
        this.stable = true;
        this.worldVersion = 2586;
        this.protocolVersion = SharedConstants.getProtocolVersion();
        this.packVersion = 6;
        this.buildTime = new Date();
        this.releaseTarget = "1.16.5";
    }

    private DetectedVersion(JsonObject jsonObject) {
        this.id = GsonHelper.getAsString(jsonObject, "id");
        this.name = GsonHelper.getAsString(jsonObject, "name");
        this.releaseTarget = GsonHelper.getAsString(jsonObject, "release_target");
        this.stable = GsonHelper.getAsBoolean(jsonObject, "stable");
        this.worldVersion = GsonHelper.getAsInt(jsonObject, "world_version");
        this.protocolVersion = GsonHelper.getAsInt(jsonObject, "protocol_version");
        this.packVersion = GsonHelper.getAsInt(jsonObject, "pack_version");
        this.buildTime = Date.from(ZonedDateTime.parse(GsonHelper.getAsString(jsonObject, "build_time")).toInstant());
    }

    /*
     * Exception decompiling
     */
    public static GameVersion tryDetectVersion() {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 7[TRYBLOCK]
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

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getReleaseTarget() {
        return this.releaseTarget;
    }

    public int getWorldVersion() {
        return this.worldVersion;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public int getPackVersion() {
        return this.packVersion;
    }

    public Date getBuildTime() {
        return this.buildTime;
    }

    public boolean isStable() {
        return this.stable;
    }
}

