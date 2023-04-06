/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  org.apache.commons.io.FileUtils
 */
package com.mojang.realmsclient.util;

import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.GuardedSerializer;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;

public class RealmsPersistence {
    private static final GuardedSerializer GSON = new GuardedSerializer();

    public static RealmsPersistenceData readFile() {
        File file = RealmsPersistence.getPathToData();
        try {
            return GSON.fromJson(FileUtils.readFileToString((File)file, (Charset)StandardCharsets.UTF_8), RealmsPersistenceData.class);
        }
        catch (IOException iOException) {
            return new RealmsPersistenceData();
        }
    }

    public static void writeFile(RealmsPersistenceData realmsPersistenceData) {
        File file = RealmsPersistence.getPathToData();
        try {
            FileUtils.writeStringToFile((File)file, (String)GSON.toJson(realmsPersistenceData), (Charset)StandardCharsets.UTF_8);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static File getPathToData() {
        return new File(Minecraft.getInstance().gameDirectory, "realms_persistence.json");
    }

    public static class RealmsPersistenceData
    implements ReflectionBasedSerialization {
        @SerializedName(value="newsLink")
        public String newsLink;
        @SerializedName(value="hasUnreadNews")
        public boolean hasUnreadNews;
    }

}

