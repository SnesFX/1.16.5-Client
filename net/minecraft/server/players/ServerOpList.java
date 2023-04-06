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
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;

public class ServerOpList
extends StoredUserList<GameProfile, ServerOpListEntry> {
    public ServerOpList(File file) {
        super(file);
    }

    @Override
    protected StoredUserEntry<GameProfile> createEntry(JsonObject jsonObject) {
        return new ServerOpListEntry(jsonObject);
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

    public boolean canBypassPlayerLimit(GameProfile gameProfile) {
        ServerOpListEntry serverOpListEntry = (ServerOpListEntry)this.get(gameProfile);
        if (serverOpListEntry != null) {
            return serverOpListEntry.getBypassesPlayerLimit();
        }
        return false;
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

