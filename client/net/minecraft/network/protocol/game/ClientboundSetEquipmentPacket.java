/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ClientboundSetEquipmentPacket
implements Packet<ClientGamePacketListener> {
    private int entity;
    private final List<Pair<EquipmentSlot, ItemStack>> slots;

    public ClientboundSetEquipmentPacket() {
        this.slots = Lists.newArrayList();
    }

    public ClientboundSetEquipmentPacket(int n, List<Pair<EquipmentSlot, ItemStack>> list) {
        this.entity = n;
        this.slots = list;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        byte by;
        this.entity = friendlyByteBuf.readVarInt();
        EquipmentSlot[] arrequipmentSlot = EquipmentSlot.values();
        do {
            by = friendlyByteBuf.readByte();
            EquipmentSlot equipmentSlot = arrequipmentSlot[by & 0x7F];
            ItemStack itemStack = friendlyByteBuf.readItem();
            this.slots.add((Pair<EquipmentSlot, ItemStack>)Pair.of((Object)((Object)equipmentSlot), (Object)itemStack));
        } while ((by & 0xFFFFFF80) != 0);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.entity);
        int n = this.slots.size();
        for (int i = 0; i < n; ++i) {
            Pair<EquipmentSlot, ItemStack> pair = this.slots.get(i);
            EquipmentSlot equipmentSlot = (EquipmentSlot)((Object)pair.getFirst());
            boolean bl = i != n - 1;
            int n2 = equipmentSlot.ordinal();
            friendlyByteBuf.writeByte(bl ? n2 | 0xFFFFFF80 : n2);
            friendlyByteBuf.writeItem((ItemStack)pair.getSecond());
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleSetEquipment(this);
    }

    public int getEntity() {
        return this.entity;
    }

    public List<Pair<EquipmentSlot, ItemStack>> getSlots() {
        return this.slots;
    }
}

