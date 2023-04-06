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
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerPlayerList
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final JsonParser JSON_PARSER = new JsonParser();
    public long serverId;
    public List<String> players;

    public static RealmsServerPlayerList parse(JsonObject jsonObject) {
        RealmsServerPlayerList realmsServerPlayerList = new RealmsServerPlayerList();
        try {
            JsonElement jsonElement;
            realmsServerPlayerList.serverId = JsonUtils.getLongOr("serverId", jsonObject, -1L);
            String string = JsonUtils.getStringOr("playerList", jsonObject, null);
            realmsServerPlayerList.players = string != null ? ((jsonElement = JSON_PARSER.parse(string)).isJsonArray() ? RealmsServerPlayerList.parsePlayers(jsonElement.getAsJsonArray()) : Lists.newArrayList()) : Lists.newArrayList();
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsServerPlayerList: " + exception.getMessage());
        }
        return realmsServerPlayerList;
    }

    private static List<String> parsePlayers(JsonArray jsonArray) {
        ArrayList arrayList = Lists.newArrayList();
        for (JsonElement jsonElement : jsonArray) {
            try {
                arrayList.add(jsonElement.getAsString());
            }
            catch (Exception exception) {}
        }
        return arrayList;
    }
}

