/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 */
package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.IOException;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;

public class ClientboundAwardStatsPacket
implements Packet<ClientGamePacketListener> {
    private Object2IntMap<Stat<?>> stats;

    public ClientboundAwardStatsPacket() {
    }

    public ClientboundAwardStatsPacket(Object2IntMap<Stat<?>> object2IntMap) {
        this.stats = object2IntMap;
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleAwardStats(this);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        int n = friendlyByteBuf.readVarInt();
        this.stats = new Object2IntOpenHashMap(n);
        for (int i = 0; i < n; ++i) {
            this.readStat((StatType)Registry.STAT_TYPE.byId(friendlyByteBuf.readVarInt()), friendlyByteBuf);
        }
    }

    private <T> void readStat(StatType<T> statType, FriendlyByteBuf friendlyByteBuf) {
        int n = friendlyByteBuf.readVarInt();
        int n2 = friendlyByteBuf.readVarInt();
        this.stats.put(statType.get(statType.getRegistry().byId(n)), n2);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.stats.size());
        for (Object2IntMap.Entry entry : this.stats.object2IntEntrySet()) {
            Stat stat = (Stat)entry.getKey();
            friendlyByteBuf.writeVarInt(Registry.STAT_TYPE.getId(stat.getType()));
            friendlyByteBuf.writeVarInt(this.getId(stat));
            friendlyByteBuf.writeVarInt(entry.getIntValue());
        }
    }

    private <T> int getId(Stat<T> stat) {
        return stat.getType().getRegistry().getId(stat.getValue());
    }

    public Map<Stat<?>, Integer> getStats() {
        return this.stats;
    }
}

