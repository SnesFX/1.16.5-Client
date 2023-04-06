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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ClientboundUpdateAttributesPacket
implements Packet<ClientGamePacketListener> {
    private int entityId;
    private final List<AttributeSnapshot> attributes = Lists.newArrayList();

    public ClientboundUpdateAttributesPacket() {
    }

    public ClientboundUpdateAttributesPacket(int n, Collection<AttributeInstance> collection) {
        this.entityId = n;
        for (AttributeInstance attributeInstance : collection) {
            this.attributes.add(new AttributeSnapshot(attributeInstance.getAttribute(), attributeInstance.getBaseValue(), attributeInstance.getModifiers()));
        }
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.entityId = friendlyByteBuf.readVarInt();
        int n = friendlyByteBuf.readInt();
        for (int i = 0; i < n; ++i) {
            ResourceLocation resourceLocation = friendlyByteBuf.readResourceLocation();
            Attribute attribute = Registry.ATTRIBUTE.get(resourceLocation);
            double d = friendlyByteBuf.readDouble();
            ArrayList arrayList = Lists.newArrayList();
            int n2 = friendlyByteBuf.readVarInt();
            for (int j = 0; j < n2; ++j) {
                UUID uUID = friendlyByteBuf.readUUID();
                arrayList.add(new AttributeModifier(uUID, "Unknown synced attribute modifier", friendlyByteBuf.readDouble(), AttributeModifier.Operation.fromValue(friendlyByteBuf.readByte())));
            }
            this.attributes.add(new AttributeSnapshot(attribute, d, arrayList));
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.entityId);
        friendlyByteBuf.writeInt(this.attributes.size());
        for (AttributeSnapshot attributeSnapshot : this.attributes) {
            friendlyByteBuf.writeResourceLocation(Registry.ATTRIBUTE.getKey(attributeSnapshot.getAttribute()));
            friendlyByteBuf.writeDouble(attributeSnapshot.getBase());
            friendlyByteBuf.writeVarInt(attributeSnapshot.getModifiers().size());
            for (AttributeModifier attributeModifier : attributeSnapshot.getModifiers()) {
                friendlyByteBuf.writeUUID(attributeModifier.getId());
                friendlyByteBuf.writeDouble(attributeModifier.getAmount());
                friendlyByteBuf.writeByte(attributeModifier.getOperation().toValue());
            }
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleUpdateAttributes(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public List<AttributeSnapshot> getValues() {
        return this.attributes;
    }

    public class AttributeSnapshot {
        private final Attribute attribute;
        private final double base;
        private final Collection<AttributeModifier> modifiers;

        public AttributeSnapshot(Attribute attribute, double d, Collection<AttributeModifier> collection) {
            this.attribute = attribute;
            this.base = d;
            this.modifiers = collection;
        }

        public Attribute getAttribute() {
            return this.attribute;
        }

        public double getBase() {
            return this.base;
        }

        public Collection<AttributeModifier> getModifiers() {
            return this.modifiers;
        }
    }

}

