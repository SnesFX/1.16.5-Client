/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BackupList
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public List<Backup> backups;

    public static BackupList parse(String string) {
        JsonParser jsonParser = new JsonParser();
        BackupList backupList = new BackupList();
        backupList.backups = Lists.newArrayList();
        try {
            JsonElement jsonElement = jsonParser.parse(string).getAsJsonObject().get("backups");
            if (jsonElement.isJsonArray()) {
                Iterator iterator = jsonElement.getAsJsonArray().iterator();
                while (iterator.hasNext()) {
                    backupList.backups.add(Backup.parse((JsonElement)iterator.next()));
                }
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse BackupList: " + exception.getMessage());
        }
        return backupList;
    }
}

