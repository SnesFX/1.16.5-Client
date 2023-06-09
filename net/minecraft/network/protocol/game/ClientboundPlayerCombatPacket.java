/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.LivingEntity;

public class ClientboundPlayerCombatPacket
implements Packet<ClientGamePacketListener> {
    public Event event;
    public int playerId;
    public int killerId;
    public int duration;
    public Component message;

    public ClientboundPlayerCombatPacket() {
    }

    public ClientboundPlayerCombatPacket(CombatTracker combatTracker, Event event) {
        this(combatTracker, event, TextComponent.EMPTY);
    }

    public ClientboundPlayerCombatPacket(CombatTracker combatTracker, Event event, Component component) {
        this.event = event;
        LivingEntity livingEntity = combatTracker.getKiller();
        switch (event) {
            case END_COMBAT: {
                this.duration = combatTracker.getCombatDuration();
                this.killerId = livingEntity == null ? -1 : livingEntity.getId();
                break;
            }
            case ENTITY_DIED: {
                this.playerId = combatTracker.getMob().getId();
                this.killerId = livingEntity == null ? -1 : livingEntity.getId();
                this.message = component;
            }
        }
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.event = friendlyByteBuf.readEnum(Event.class);
        if (this.event == Event.END_COMBAT) {
            this.duration = friendlyByteBuf.readVarInt();
            this.killerId = friendlyByteBuf.readInt();
        } else if (this.event == Event.ENTITY_DIED) {
            this.playerId = friendlyByteBuf.readVarInt();
            this.killerId = friendlyByteBuf.readInt();
            this.message = friendlyByteBuf.readComponent();
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeEnum(this.event);
        if (this.event == Event.END_COMBAT) {
            friendlyByteBuf.writeVarInt(this.duration);
            friendlyByteBuf.writeInt(this.killerId);
        } else if (this.event == Event.ENTITY_DIED) {
            friendlyByteBuf.writeVarInt(this.playerId);
            friendlyByteBuf.writeInt(this.killerId);
            friendlyByteBuf.writeComponent(this.message);
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handlePlayerCombat(this);
    }

    @Override
    public boolean isSkippable() {
        return this.event == Event.ENTITY_DIED;
    }

    public static enum Event {
        ENTER_COMBAT,
        END_COMBAT,
        ENTITY_DIED;
        
    }

}

