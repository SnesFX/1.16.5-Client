/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;

public class ServerItemCooldowns
extends ItemCooldowns {
    private final ServerPlayer player;

    public ServerItemCooldowns(ServerPlayer serverPlayer) {
        this.player = serverPlayer;
    }

    @Override
    protected void onCooldownStarted(Item item, int n) {
        super.onCooldownStarted(item, n);
        this.player.connection.send(new ClientboundCooldownPacket(item, n));
    }

    @Override
    protected void onCooldownEnded(Item item) {
        super.onCooldownEnded(item);
        this.player.connection.send(new ClientboundCooldownPacket(item, 0));
    }
}

