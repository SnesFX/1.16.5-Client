/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldTemplate
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public String id = "";
    public String name = "";
    public String version = "";
    public String author = "";
    public String link = "";
    @Nullable
    public String image;
    public String trailer = "";
    public String recommendedPlayers = "";
    public WorldTemplateType type = WorldTemplateType.WORLD_TEMPLATE;

    public static WorldTemplate parse(JsonObject jsonObject) {
        WorldTemplate worldTemplate = new WorldTemplate();
        try {
            worldTemplate.id = JsonUtils.getStringOr("id", jsonObject, "");
            worldTemplate.name = JsonUtils.getStringOr("name", jsonObject, "");
            worldTemplate.version = JsonUtils.getStringOr("version", jsonObject, "");
            worldTemplate.author = JsonUtils.getStringOr("author", jsonObject, "");
            worldTemplate.link = JsonUtils.getStringOr("link", jsonObject, "");
            worldTemplate.image = JsonUtils.getStringOr("image", jsonObject, null);
            worldTemplate.trailer = JsonUtils.getStringOr("trailer", jsonObject, "");
            worldTemplate.recommendedPlayers = JsonUtils.getStringOr("recommendedPlayers", jsonObject, "");
            worldTemplate.type = WorldTemplateType.valueOf(JsonUtils.getStringOr("type", jsonObject, WorldTemplateType.WORLD_TEMPLATE.name()));
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse WorldTemplate: " + exception.getMessage());
        }
        return worldTemplate;
    }

    public static enum WorldTemplateType {
        WORLD_TEMPLATE,
        MINIGAME,
        ADVENTUREMAP,
        EXPERIENCE,
        INSPIRATION;
        
    }

}

