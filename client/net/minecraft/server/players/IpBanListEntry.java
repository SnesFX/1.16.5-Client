/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.server.players;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.players.BanListEntry;

public class IpBanListEntry
extends BanListEntry<String> {
    public IpBanListEntry(String string) {
        this(string, null, null, null, null);
    }

    public IpBanListEntry(String string, @Nullable Date date, @Nullable String string2, @Nullable Date date2, @Nullable String string3) {
        super(string, date, string2, date2, string3);
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent((String)this.getUser());
    }

    public IpBanListEntry(JsonObject jsonObject) {
        super(IpBanListEntry.createIpInfo(jsonObject), jsonObject);
    }

    private static String createIpInfo(JsonObject jsonObject) {
        return jsonObject.has("ip") ? jsonObject.get("ip").getAsString() : null;
    }

    @Override
    protected void serialize(JsonObject jsonObject) {
        if (this.getUser() == null) {
            return;
        }
        jsonObject.addProperty("ip", (String)this.getUser());
        super.serialize(jsonObject);
    }
}

