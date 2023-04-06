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
package com.mojang.realmsclient.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsError {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String errorMessage;
    private final int errorCode;

    private RealmsError(String string, int n) {
        this.errorMessage = string;
        this.errorCode = n;
    }

    public static RealmsError create(String string) {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
            String string2 = JsonUtils.getStringOr("errorMsg", jsonObject, "");
            int n = JsonUtils.getIntOr("errorCode", jsonObject, -1);
            return new RealmsError(string2, n);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsError: " + exception.getMessage());
            LOGGER.error("The error was: " + string);
            return new RealmsError("Failed to parse response from server", -1);
        }
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}

