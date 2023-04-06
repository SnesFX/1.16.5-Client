/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.bridge.game.GameSession
 */
package net.minecraft.client;

import com.mojang.bridge.game.GameSession;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;

public class Session
implements GameSession {
    private final int players;
    private final boolean isRemoteServer;
    private final String difficulty;
    private final String gameMode;
    private final UUID id;

    public Session(ClientLevel clientLevel, LocalPlayer localPlayer, ClientPacketListener clientPacketListener) {
        this.players = clientPacketListener.getOnlinePlayers().size();
        this.isRemoteServer = !clientPacketListener.getConnection().isMemoryConnection();
        this.difficulty = clientLevel.getDifficulty().getKey();
        PlayerInfo playerInfo = clientPacketListener.getPlayerInfo(localPlayer.getUUID());
        this.gameMode = playerInfo != null ? playerInfo.getGameMode().getName() : "unknown";
        this.id = clientPacketListener.getId();
    }

    public int getPlayerCount() {
        return this.players;
    }

    public boolean isRemoteServer() {
        return this.isRemoteServer;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public String getGameMode() {
        return this.gameMode;
    }

    public UUID getSessionId() {
        return this.id;
    }
}

