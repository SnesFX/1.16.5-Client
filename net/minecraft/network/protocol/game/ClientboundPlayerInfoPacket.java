/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.MoreObjects$ToStringHelper
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  io.netty.buffer.ByteBuf
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.GameType;

public class ClientboundPlayerInfoPacket
implements Packet<ClientGamePacketListener> {
    private Action action;
    private final List<PlayerUpdate> entries = Lists.newArrayList();

    public ClientboundPlayerInfoPacket() {
    }

    public ClientboundPlayerInfoPacket(Action action, ServerPlayer ... arrserverPlayer) {
        this.action = action;
        for (ServerPlayer serverPlayer : arrserverPlayer) {
            this.entries.add(new PlayerUpdate(serverPlayer.getGameProfile(), serverPlayer.latency, serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer.getTabListDisplayName()));
        }
    }

    public ClientboundPlayerInfoPacket(Action action, Iterable<ServerPlayer> iterable) {
        this.action = action;
        for (ServerPlayer serverPlayer : iterable) {
            this.entries.add(new PlayerUpdate(serverPlayer.getGameProfile(), serverPlayer.latency, serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer.getTabListDisplayName()));
        }
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.action = friendlyByteBuf.readEnum(Action.class);
        int n = friendlyByteBuf.readVarInt();
        for (int i = 0; i < n; ++i) {
            GameProfile gameProfile = null;
            int n2 = 0;
            GameType gameType = null;
            Component component = null;
            switch (this.action) {
                case ADD_PLAYER: {
                    gameProfile = new GameProfile(friendlyByteBuf.readUUID(), friendlyByteBuf.readUtf(16));
                    int n3 = friendlyByteBuf.readVarInt();
                    for (int j = 0; j < n3; ++j) {
                        String string = friendlyByteBuf.readUtf(32767);
                        String string2 = friendlyByteBuf.readUtf(32767);
                        if (friendlyByteBuf.readBoolean()) {
                            gameProfile.getProperties().put((Object)string, (Object)new Property(string, string2, friendlyByteBuf.readUtf(32767)));
                            continue;
                        }
                        gameProfile.getProperties().put((Object)string, (Object)new Property(string, string2));
                    }
                    gameType = GameType.byId(friendlyByteBuf.readVarInt());
                    n2 = friendlyByteBuf.readVarInt();
                    if (!friendlyByteBuf.readBoolean()) break;
                    component = friendlyByteBuf.readComponent();
                    break;
                }
                case UPDATE_GAME_MODE: {
                    gameProfile = new GameProfile(friendlyByteBuf.readUUID(), null);
                    gameType = GameType.byId(friendlyByteBuf.readVarInt());
                    break;
                }
                case UPDATE_LATENCY: {
                    gameProfile = new GameProfile(friendlyByteBuf.readUUID(), null);
                    n2 = friendlyByteBuf.readVarInt();
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    gameProfile = new GameProfile(friendlyByteBuf.readUUID(), null);
                    if (!friendlyByteBuf.readBoolean()) break;
                    component = friendlyByteBuf.readComponent();
                    break;
                }
                case REMOVE_PLAYER: {
                    gameProfile = new GameProfile(friendlyByteBuf.readUUID(), null);
                }
            }
            this.entries.add(new PlayerUpdate(gameProfile, n2, gameType, component));
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeEnum(this.action);
        friendlyByteBuf.writeVarInt(this.entries.size());
        for (PlayerUpdate playerUpdate : this.entries) {
            switch (this.action) {
                case ADD_PLAYER: {
                    friendlyByteBuf.writeUUID(playerUpdate.getProfile().getId());
                    friendlyByteBuf.writeUtf(playerUpdate.getProfile().getName());
                    friendlyByteBuf.writeVarInt(playerUpdate.getProfile().getProperties().size());
                    for (Property property : playerUpdate.getProfile().getProperties().values()) {
                        friendlyByteBuf.writeUtf(property.getName());
                        friendlyByteBuf.writeUtf(property.getValue());
                        if (property.hasSignature()) {
                            friendlyByteBuf.writeBoolean(true);
                            friendlyByteBuf.writeUtf(property.getSignature());
                            continue;
                        }
                        friendlyByteBuf.writeBoolean(false);
                    }
                    friendlyByteBuf.writeVarInt(playerUpdate.getGameMode().getId());
                    friendlyByteBuf.writeVarInt(playerUpdate.getLatency());
                    if (playerUpdate.getDisplayName() == null) {
                        friendlyByteBuf.writeBoolean(false);
                        break;
                    }
                    friendlyByteBuf.writeBoolean(true);
                    friendlyByteBuf.writeComponent(playerUpdate.getDisplayName());
                    break;
                }
                case UPDATE_GAME_MODE: {
                    friendlyByteBuf.writeUUID(playerUpdate.getProfile().getId());
                    friendlyByteBuf.writeVarInt(playerUpdate.getGameMode().getId());
                    break;
                }
                case UPDATE_LATENCY: {
                    friendlyByteBuf.writeUUID(playerUpdate.getProfile().getId());
                    friendlyByteBuf.writeVarInt(playerUpdate.getLatency());
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    friendlyByteBuf.writeUUID(playerUpdate.getProfile().getId());
                    if (playerUpdate.getDisplayName() == null) {
                        friendlyByteBuf.writeBoolean(false);
                        break;
                    }
                    friendlyByteBuf.writeBoolean(true);
                    friendlyByteBuf.writeComponent(playerUpdate.getDisplayName());
                    break;
                }
                case REMOVE_PLAYER: {
                    friendlyByteBuf.writeUUID(playerUpdate.getProfile().getId());
                }
            }
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handlePlayerInfo(this);
    }

    public List<PlayerUpdate> getEntries() {
        return this.entries;
    }

    public Action getAction() {
        return this.action;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("action", (Object)this.action).add("entries", this.entries).toString();
    }

    public class PlayerUpdate {
        private final int latency;
        private final GameType gameMode;
        private final GameProfile profile;
        private final Component displayName;

        public PlayerUpdate(GameProfile gameProfile, int n, @Nullable GameType gameType, @Nullable Component component) {
            this.profile = gameProfile;
            this.latency = n;
            this.gameMode = gameType;
            this.displayName = component;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public int getLatency() {
            return this.latency;
        }

        public GameType getGameMode() {
            return this.gameMode;
        }

        @Nullable
        public Component getDisplayName() {
            return this.displayName;
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("latency", this.latency).add("gameMode", (Object)this.gameMode).add("profile", (Object)this.profile).add("displayName", this.displayName == null ? null : Component.Serializer.toJson(this.displayName)).toString();
        }
    }

    public static enum Action {
        ADD_PLAYER,
        UPDATE_GAME_MODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER;
        
    }

}

