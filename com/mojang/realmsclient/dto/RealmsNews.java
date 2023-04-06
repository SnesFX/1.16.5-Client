/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsNews
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public String newsLink;

    public static RealmsNews parse(String string) {
        RealmsNews realmsNews = new RealmsNews();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
            realmsNews.newsLink = JsonUtils.getStringOr("newsLink", jsonObject, null);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsNews: " + exception.getMessage());
        }
        return realmsNews;
    }
}

