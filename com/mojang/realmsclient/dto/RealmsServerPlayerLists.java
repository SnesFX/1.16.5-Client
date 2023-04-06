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
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerPlayerLists
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public List<RealmsServerPlayerList> servers;

    public static RealmsServerPlayerLists parse(String string) {
        RealmsServerPlayerLists realmsServerPlayerLists = new RealmsServerPlayerLists();
        realmsServerPlayerLists.servers = Lists.newArrayList();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
            if (jsonObject.get("lists").isJsonArray()) {
                JsonArray jsonArray = jsonObject.get("lists").getAsJsonArray();
                Iterator iterator = jsonArray.iterator();
                while (iterator.hasNext()) {
                    realmsServerPlayerLists.servers.add(RealmsServerPlayerList.parse(((JsonElement)iterator.next()).getAsJsonObject()));
                }
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsServerPlayerLists: " + exception.getMessage());
        }
        return realmsServerPlayerLists;
    }
}

