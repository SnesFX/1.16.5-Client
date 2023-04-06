/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;
import net.minecraft.server.players.UserBanListEntry;

public class UserBanList
extends StoredUserList<GameProfile, UserBanListEntry> {
    public UserBanList(File file) {
        super(file);
    }

    @Override
    protected StoredUserEntry<GameProfile> createEntry(JsonObject jsonObject) {
        return new UserBanListEntry(jsonObject);
    }

    public boolean isBanned(GameProfile gameProfile) {
        return this.contains(gameProfile);
    }

    @Override
    public String[] getUserList() {
        String[] arrstring = new String[this.getEntries().size()];
        int n = 0;
        for (StoredUserEntry storedUserEntry : this.getEntries()) {
            arrstring[n++] = ((GameProfile)storedUserEntry.getUser()).getName();
        }
        return arrstring;
    }

    @Override
    protected String getKeyForUser(GameProfile gameProfile) {
        return gameProfile.getId().toString();
    }

    @Override
    protected /* synthetic */ String getKeyForUser(Object object) {
        return this.getKeyForUser((GameProfile)object);
    }
}

