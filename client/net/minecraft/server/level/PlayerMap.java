/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 */
package net.minecraft.server.level;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerMap {
    private final Object2BooleanMap<ServerPlayer> players = new Object2BooleanOpenHashMap();

    public Stream<ServerPlayer> getPlayers(long l) {
        return this.players.keySet().stream();
    }

    public void addPlayer(long l, ServerPlayer serverPlayer, boolean bl) {
        this.players.put((Object)serverPlayer, bl);
    }

    public void removePlayer(long l, ServerPlayer serverPlayer) {
        this.players.removeBoolean((Object)serverPlayer);
    }

    public void ignorePlayer(ServerPlayer serverPlayer) {
        this.players.replace((Object)serverPlayer, true);
    }

    public void unIgnorePlayer(ServerPlayer serverPlayer) {
        this.players.replace((Object)serverPlayer, false);
    }

    public boolean ignoredOrUnknown(ServerPlayer serverPlayer) {
        return this.players.getOrDefault((Object)serverPlayer, true);
    }

    public boolean ignored(ServerPlayer serverPlayer) {
        return this.players.getBoolean((Object)serverPlayer);
    }

    public void updatePlayer(long l, long l2, ServerPlayer serverPlayer) {
    }
}

