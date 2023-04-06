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

public class Subscription
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public long startDate;
    public int daysLeft;
    public SubscriptionType type = SubscriptionType.NORMAL;

    public static Subscription parse(String string) {
        Subscription subscription = new Subscription();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
            subscription.startDate = JsonUtils.getLongOr("startDate", jsonObject, 0L);
            subscription.daysLeft = JsonUtils.getIntOr("daysLeft", jsonObject, 0);
            subscription.type = Subscription.typeFrom(JsonUtils.getStringOr("subscriptionType", jsonObject, SubscriptionType.NORMAL.name()));
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse Subscription: " + exception.getMessage());
        }
        return subscription;
    }

    private static SubscriptionType typeFrom(String string) {
        try {
            return SubscriptionType.valueOf(string);
        }
        catch (Exception exception) {
            return SubscriptionType.NORMAL;
        }
    }

    public static enum SubscriptionType {
        NORMAL,
        RECURRING;
        
    }

}

