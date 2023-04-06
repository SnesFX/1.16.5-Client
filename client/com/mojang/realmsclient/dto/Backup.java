/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Backup
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public String backupId;
    public Date lastModifiedDate;
    public long size;
    private boolean uploadedVersion;
    public Map<String, String> metadata = Maps.newHashMap();
    public Map<String, String> changeList = Maps.newHashMap();

    public static Backup parse(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Backup backup = new Backup();
        try {
            backup.backupId = JsonUtils.getStringOr("backupId", jsonObject, "");
            backup.lastModifiedDate = JsonUtils.getDateOr("lastModifiedDate", jsonObject);
            backup.size = JsonUtils.getLongOr("size", jsonObject, 0L);
            if (jsonObject.has("metadata")) {
                JsonObject jsonObject2 = jsonObject.getAsJsonObject("metadata");
                Set set = jsonObject2.entrySet();
                for (Map.Entry entry : set) {
                    if (((JsonElement)entry.getValue()).isJsonNull()) continue;
                    backup.metadata.put(Backup.format((String)entry.getKey()), ((JsonElement)entry.getValue()).getAsString());
                }
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse Backup: " + exception.getMessage());
        }
        return backup;
    }

    private static String format(String string) {
        String[] arrstring = string.split("_");
        StringBuilder stringBuilder = new StringBuilder();
        for (String string2 : arrstring) {
            if (string2 == null || string2.length() < 1) continue;
            if ("of".equals(string2)) {
                stringBuilder.append(string2).append(" ");
                continue;
            }
            char c = Character.toUpperCase(string2.charAt(0));
            stringBuilder.append(c).append(string2.substring(1)).append(" ");
        }
        return stringBuilder.toString();
    }

    public boolean isUploadedVersion() {
        return this.uploadedVersion;
    }

    public void setUploadedVersion(boolean bl) {
        this.uploadedVersion = bl;
    }
}

