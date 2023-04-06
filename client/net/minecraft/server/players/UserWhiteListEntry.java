/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.server.players;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.players.StoredUserEntry;

public class UserWhiteListEntry
extends StoredUserEntry<GameProfile> {
    public UserWhiteListEntry(GameProfile gameProfile) {
        super(gameProfile);
    }

    public UserWhiteListEntry(JsonObject jsonObject) {
        super(UserWhiteListEntry.createGameProfile(jsonObject));
    }

    @Override
    protected void serialize(JsonObject jsonObject) {
        if (this.getUser() == null) {
            return;
        }
        jsonObject.addProperty("uuid", ((GameProfile)this.getUser()).getId() == null ? "" : ((GameProfile)this.getUser()).getId().toString());
        jsonObject.addProperty("name", ((GameProfile)this.getUser()).getName());
    }

    private static GameProfile createGameProfile(JsonObject jsonObject) {
        UUID uUID;
        if (!jsonObject.has("uuid") || !jsonObject.has("name")) {
            return null;
        }
        String string = jsonObject.get("uuid").getAsString();
        try {
            uUID = UUID.fromString(string);
        }
        catch (Throwable throwable) {
            return null;
        }
        return new GameProfile(uUID, jsonObject.get("name").getAsString());
    }
}

