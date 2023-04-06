/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

public class ClientboundSetPlayerTeamPacket
implements Packet<ClientGamePacketListener> {
    private String name = "";
    private Component displayName = TextComponent.EMPTY;
    private Component playerPrefix = TextComponent.EMPTY;
    private Component playerSuffix = TextComponent.EMPTY;
    private String nametagVisibility;
    private String collisionRule;
    private ChatFormatting color;
    private final Collection<String> players;
    private int method;
    private int options;

    public ClientboundSetPlayerTeamPacket() {
        this.nametagVisibility = Team.Visibility.ALWAYS.name;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = ChatFormatting.RESET;
        this.players = Lists.newArrayList();
    }

    public ClientboundSetPlayerTeamPacket(PlayerTeam playerTeam, int n) {
        this.nametagVisibility = Team.Visibility.ALWAYS.name;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = ChatFormatting.RESET;
        this.players = Lists.newArrayList();
        this.name = playerTeam.getName();
        this.method = n;
        if (n == 0 || n == 2) {
            this.displayName = playerTeam.getDisplayName();
            this.options = playerTeam.packOptions();
            this.nametagVisibility = playerTeam.getNameTagVisibility().name;
            this.collisionRule = playerTeam.getCollisionRule().name;
            this.color = playerTeam.getColor();
            this.playerPrefix = playerTeam.getPlayerPrefix();
            this.playerSuffix = playerTeam.getPlayerSuffix();
        }
        if (n == 0) {
            this.players.addAll(playerTeam.getPlayers());
        }
    }

    public ClientboundSetPlayerTeamPacket(PlayerTeam playerTeam, Collection<String> collection, int n) {
        this.nametagVisibility = Team.Visibility.ALWAYS.name;
        this.collisionRule = Team.CollisionRule.ALWAYS.name;
        this.color = ChatFormatting.RESET;
        this.players = Lists.newArrayList();
        if (n != 3 && n != 4) {
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        }
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("Players cannot be null/empty");
        }
        this.method = n;
        this.name = playerTeam.getName();
        this.players.addAll(collection);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.name = friendlyByteBuf.readUtf(16);
        this.method = friendlyByteBuf.readByte();
        if (this.method == 0 || this.method == 2) {
            this.displayName = friendlyByteBuf.readComponent();
            this.options = friendlyByteBuf.readByte();
            this.nametagVisibility = friendlyByteBuf.readUtf(40);
            this.collisionRule = friendlyByteBuf.readUtf(40);
            this.color = friendlyByteBuf.readEnum(ChatFormatting.class);
            this.playerPrefix = friendlyByteBuf.readComponent();
            this.playerSuffix = friendlyByteBuf.readComponent();
        }
        if (this.method == 0 || this.method == 3 || this.method == 4) {
            int n = friendlyByteBuf.readVarInt();
            for (int i = 0; i < n; ++i) {
                this.players.add(friendlyByteBuf.readUtf(40));
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeUtf(this.name);
        friendlyByteBuf.writeByte(this.method);
        if (this.method == 0 || this.method == 2) {
            friendlyByteBuf.writeComponent(this.displayName);
            friendlyByteBuf.writeByte(this.options);
            friendlyByteBuf.writeUtf(this.nametagVisibility);
            friendlyByteBuf.writeUtf(this.collisionRule);
            friendlyByteBuf.writeEnum(this.color);
            friendlyByteBuf.writeComponent(this.playerPrefix);
            friendlyByteBuf.writeComponent(this.playerSuffix);
        }
        if (this.method == 0 || this.method == 3 || this.method == 4) {
            friendlyByteBuf.writeVarInt(this.players.size());
            for (String string : this.players) {
                friendlyByteBuf.writeUtf(string);
            }
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleSetPlayerTeamPacket(this);
    }

    public String getName() {
        return this.name;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public Collection<String> getPlayers() {
        return this.players;
    }

    public int getMethod() {
        return this.method;
    }

    public int getOptions() {
        return this.options;
    }

    public ChatFormatting getColor() {
        return this.color;
    }

    public String getNametagVisibility() {
        return this.nametagVisibility;
    }

    public String getCollisionRule() {
        return this.collisionRule;
    }

    public Component getPlayerPrefix() {
        return this.playerPrefix;
    }

    public Component getPlayerSuffix() {
        return this.playerSuffix;
    }
}

