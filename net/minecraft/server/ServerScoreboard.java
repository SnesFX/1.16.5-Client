/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class ServerScoreboard
extends Scoreboard {
    private final MinecraftServer server;
    private final Set<Objective> trackedObjectives = Sets.newHashSet();
    private Runnable[] dirtyListeners = new Runnable[0];

    public ServerScoreboard(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
    }

    @Override
    public void onScoreChanged(Score score) {
        super.onScoreChanged(score);
        if (this.trackedObjectives.contains(score.getObjective())) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(Method.CHANGE, score.getObjective().getName(), score.getOwner(), score.getScore()));
        }
        this.setDirty();
    }

    @Override
    public void onPlayerRemoved(String string) {
        super.onPlayerRemoved(string);
        this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(Method.REMOVE, null, string, 0));
        this.setDirty();
    }

    @Override
    public void onPlayerScoreRemoved(String string, Objective objective) {
        super.onPlayerScoreRemoved(string, objective);
        if (this.trackedObjectives.contains(objective)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(Method.REMOVE, objective.getName(), string, 0));
        }
        this.setDirty();
    }

    @Override
    public void setDisplayObjective(int n, @Nullable Objective objective) {
        Objective objective2 = this.getDisplayObjective(n);
        super.setDisplayObjective(n, objective);
        if (objective2 != objective && objective2 != null) {
            if (this.getObjectiveDisplaySlotCount(objective2) > 0) {
                this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket(n, objective));
            } else {
                this.stopTrackingObjective(objective2);
            }
        }
        if (objective != null) {
            if (this.trackedObjectives.contains(objective)) {
                this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket(n, objective));
            } else {
                this.startTrackingObjective(objective);
            }
        }
        this.setDirty();
    }

    @Override
    public boolean addPlayerToTeam(String string, PlayerTeam playerTeam) {
        if (super.addPlayerToTeam(string, playerTeam)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(playerTeam, Arrays.asList(string), 3));
            this.setDirty();
            return true;
        }
        return false;
    }

    @Override
    public void removePlayerFromTeam(String string, PlayerTeam playerTeam) {
        super.removePlayerFromTeam(string, playerTeam);
        this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(playerTeam, Arrays.asList(string), 4));
        this.setDirty();
    }

    @Override
    public void onObjectiveAdded(Objective objective) {
        super.onObjectiveAdded(objective);
        this.setDirty();
    }

    @Override
    public void onObjectiveChanged(Objective objective) {
        super.onObjectiveChanged(objective);
        if (this.trackedObjectives.contains(objective)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetObjectivePacket(objective, 2));
        }
        this.setDirty();
    }

    @Override
    public void onObjectiveRemoved(Objective objective) {
        super.onObjectiveRemoved(objective);
        if (this.trackedObjectives.contains(objective)) {
            this.stopTrackingObjective(objective);
        }
        this.setDirty();
    }

    @Override
    public void onTeamAdded(PlayerTeam playerTeam) {
        super.onTeamAdded(playerTeam);
        this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(playerTeam, 0));
        this.setDirty();
    }

    @Override
    public void onTeamChanged(PlayerTeam playerTeam) {
        super.onTeamChanged(playerTeam);
        this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(playerTeam, 2));
        this.setDirty();
    }

    @Override
    public void onTeamRemoved(PlayerTeam playerTeam) {
        super.onTeamRemoved(playerTeam);
        this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(playerTeam, 1));
        this.setDirty();
    }

    public void addDirtyListener(Runnable runnable) {
        this.dirtyListeners = Arrays.copyOf(this.dirtyListeners, this.dirtyListeners.length + 1);
        this.dirtyListeners[this.dirtyListeners.length - 1] = runnable;
    }

    protected void setDirty() {
        for (Runnable runnable : this.dirtyListeners) {
            runnable.run();
        }
    }

    public List<Packet<?>> getStartTrackingPackets(Objective objective) {
        ArrayList arrayList = Lists.newArrayList();
        arrayList.add(new ClientboundSetObjectivePacket(objective, 0));
        for (int i = 0; i < 19; ++i) {
            if (this.getDisplayObjective(i) != objective) continue;
            arrayList.add(new ClientboundSetDisplayObjectivePacket(i, objective));
        }
        for (Score score : this.getPlayerScores(objective)) {
            arrayList.add(new ClientboundSetScorePacket(Method.CHANGE, score.getObjective().getName(), score.getOwner(), score.getScore()));
        }
        return arrayList;
    }

    public void startTrackingObjective(Objective objective) {
        List<Packet<?>> list = this.getStartTrackingPackets(objective);
        for (ServerPlayer serverPlayer : this.server.getPlayerList().getPlayers()) {
            for (Packet<?> packet : list) {
                serverPlayer.connection.send(packet);
            }
        }
        this.trackedObjectives.add(objective);
    }

    public List<Packet<?>> getStopTrackingPackets(Objective objective) {
        ArrayList arrayList = Lists.newArrayList();
        arrayList.add(new ClientboundSetObjectivePacket(objective, 1));
        for (int i = 0; i < 19; ++i) {
            if (this.getDisplayObjective(i) != objective) continue;
            arrayList.add(new ClientboundSetDisplayObjectivePacket(i, objective));
        }
        return arrayList;
    }

    public void stopTrackingObjective(Objective objective) {
        List<Packet<?>> list = this.getStopTrackingPackets(objective);
        for (ServerPlayer serverPlayer : this.server.getPlayerList().getPlayers()) {
            for (Packet<?> packet : list) {
                serverPlayer.connection.send(packet);
            }
        }
        this.trackedObjectives.remove(objective);
    }

    public int getObjectiveDisplaySlotCount(Objective objective) {
        int n = 0;
        for (int i = 0; i < 19; ++i) {
            if (this.getDisplayObjective(i) != objective) continue;
            ++n;
        }
        return n;
    }

    public static enum Method {
        CHANGE,
        REMOVE;
        
    }

}

