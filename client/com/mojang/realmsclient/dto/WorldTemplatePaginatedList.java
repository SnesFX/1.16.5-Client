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
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldTemplatePaginatedList
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public List<WorldTemplate> templates;
    public int page;
    public int size;
    public int total;

    public WorldTemplatePaginatedList() {
    }

    public WorldTemplatePaginatedList(int n) {
        this.templates = Collections.emptyList();
        this.page = 0;
        this.size = n;
        this.total = -1;
    }

    public static WorldTemplatePaginatedList parse(String string) {
        WorldTemplatePaginatedList worldTemplatePaginatedList = new WorldTemplatePaginatedList();
        worldTemplatePaginatedList.templates = Lists.newArrayList();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
            if (jsonObject.get("templates").isJsonArray()) {
                Iterator iterator = jsonObject.get("templates").getAsJsonArray().iterator();
                while (iterator.hasNext()) {
                    worldTemplatePaginatedList.templates.add(WorldTemplate.parse(((JsonElement)iterator.next()).getAsJsonObject()));
                }
            }
            worldTemplatePaginatedList.page = JsonUtils.getIntOr("page", jsonObject, 0);
            worldTemplatePaginatedList.size = JsonUtils.getIntOr("size", jsonObject, 0);
            worldTemplatePaginatedList.total = JsonUtils.getIntOr("total", jsonObject, 0);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse WorldTemplatePaginatedList: " + exception.getMessage());
        }
        return worldTemplatePaginatedList;
    }
}

