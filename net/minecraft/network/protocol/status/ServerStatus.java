/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.network.protocol.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class ServerStatus {
    private Component description;
    private Players players;
    private Version version;
    private String favicon;

    public Component getDescription() {
        return this.description;
    }

    public void setDescription(Component component) {
        this.description = component;
    }

    public Players getPlayers() {
        return this.players;
    }

    public void setPlayers(Players players) {
        this.players = players;
    }

    public Version getVersion() {
        return this.version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public void setFavicon(String string) {
        this.favicon = string;
    }

    public String getFavicon() {
        return this.favicon;
    }

    public static class Serializer
    implements JsonDeserializer<ServerStatus>,
    JsonSerializer<ServerStatus> {
        public ServerStatus deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "status");
            ServerStatus serverStatus = new ServerStatus();
            if (jsonObject.has("description")) {
                serverStatus.setDescription((Component)jsonDeserializationContext.deserialize(jsonObject.get("description"), Component.class));
            }
            if (jsonObject.has("players")) {
                serverStatus.setPlayers((Players)jsonDeserializationContext.deserialize(jsonObject.get("players"), Players.class));
            }
            if (jsonObject.has("version")) {
                serverStatus.setVersion((Version)jsonDeserializationContext.deserialize(jsonObject.get("version"), Version.class));
            }
            if (jsonObject.has("favicon")) {
                serverStatus.setFavicon(GsonHelper.getAsString(jsonObject, "favicon"));
            }
            return serverStatus;
        }

        public JsonElement serialize(ServerStatus serverStatus, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if (serverStatus.getDescription() != null) {
                jsonObject.add("description", jsonSerializationContext.serialize((Object)serverStatus.getDescription()));
            }
            if (serverStatus.getPlayers() != null) {
                jsonObject.add("players", jsonSerializationContext.serialize((Object)serverStatus.getPlayers()));
            }
            if (serverStatus.getVersion() != null) {
                jsonObject.add("version", jsonSerializationContext.serialize((Object)serverStatus.getVersion()));
            }
            if (serverStatus.getFavicon() != null) {
                jsonObject.addProperty("favicon", serverStatus.getFavicon());
            }
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((ServerStatus)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

    public static class Version {
        private final String name;
        private final int protocol;

        public Version(String string, int n) {
            this.name = string;
            this.protocol = n;
        }

        public String getName() {
            return this.name;
        }

        public int getProtocol() {
            return this.protocol;
        }

        public static class Serializer
        implements JsonDeserializer<Version>,
        JsonSerializer<Version> {
            public Version deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "version");
                return new Version(GsonHelper.getAsString(jsonObject, "name"), GsonHelper.getAsInt(jsonObject, "protocol"));
            }

            public JsonElement serialize(Version version, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", version.getName());
                jsonObject.addProperty("protocol", (Number)version.getProtocol());
                return jsonObject;
            }

            public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
                return this.serialize((Version)object, type, jsonSerializationContext);
            }

            public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return this.deserialize(jsonElement, type, jsonDeserializationContext);
            }
        }

    }

    public static class Players {
        private final int maxPlayers;
        private final int numPlayers;
        private GameProfile[] sample;

        public Players(int n, int n2) {
            this.maxPlayers = n;
            this.numPlayers = n2;
        }

        public int getMaxPlayers() {
            return this.maxPlayers;
        }

        public int getNumPlayers() {
            return this.numPlayers;
        }

        public GameProfile[] getSample() {
            return this.sample;
        }

        public void setSample(GameProfile[] arrgameProfile) {
            this.sample = arrgameProfile;
        }

        public static class Serializer
        implements JsonDeserializer<Players>,
        JsonSerializer<Players> {
            public Players deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                JsonArray jsonArray;
                JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "players");
                Players players = new Players(GsonHelper.getAsInt(jsonObject, "max"), GsonHelper.getAsInt(jsonObject, "online"));
                if (GsonHelper.isArrayNode(jsonObject, "sample") && (jsonArray = GsonHelper.getAsJsonArray(jsonObject, "sample")).size() > 0) {
                    GameProfile[] arrgameProfile = new GameProfile[jsonArray.size()];
                    for (int i = 0; i < arrgameProfile.length; ++i) {
                        JsonObject jsonObject2 = GsonHelper.convertToJsonObject(jsonArray.get(i), "player[" + i + "]");
                        String string = GsonHelper.getAsString(jsonObject2, "id");
                        arrgameProfile[i] = new GameProfile(UUID.fromString(string), GsonHelper.getAsString(jsonObject2, "name"));
                    }
                    players.setSample(arrgameProfile);
                }
                return players;
            }

            public JsonElement serialize(Players players, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("max", (Number)players.getMaxPlayers());
                jsonObject.addProperty("online", (Number)players.getNumPlayers());
                if (players.getSample() != null && players.getSample().length > 0) {
                    JsonArray jsonArray = new JsonArray();
                    for (int i = 0; i < players.getSample().length; ++i) {
                        JsonObject jsonObject2 = new JsonObject();
                        UUID uUID = players.getSample()[i].getId();
                        jsonObject2.addProperty("id", uUID == null ? "" : uUID.toString());
                        jsonObject2.addProperty("name", players.getSample()[i].getName());
                        jsonArray.add((JsonElement)jsonObject2);
                    }
                    jsonObject.add("sample", (JsonElement)jsonArray);
                }
                return jsonObject;
            }

            public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
                return this.serialize((Players)object, type, jsonSerializationContext);
            }

            public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return this.deserialize(jsonElement, type, jsonDeserializationContext);
            }
        }

    }

}

