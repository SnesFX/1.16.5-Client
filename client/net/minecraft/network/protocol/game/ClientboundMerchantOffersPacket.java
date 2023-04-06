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
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.trading.MerchantOffers;

public class ClientboundMerchantOffersPacket
implements Packet<ClientGamePacketListener> {
    private int containerId;
    private MerchantOffers offers;
    private int villagerLevel;
    private int villagerXp;
    private boolean showProgress;
    private boolean canRestock;

    public ClientboundMerchantOffersPacket() {
    }

    public ClientboundMerchantOffersPacket(int n, MerchantOffers merchantOffers, int n2, int n3, boolean bl, boolean bl2) {
        this.containerId = n;
        this.offers = merchantOffers;
        this.villagerLevel = n2;
        this.villagerXp = n3;
        this.showProgress = bl;
        this.canRestock = bl2;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.containerId = friendlyByteBuf.readVarInt();
        this.offers = MerchantOffers.createFromStream(friendlyByteBuf);
        this.villagerLevel = friendlyByteBuf.readVarInt();
        this.villagerXp = friendlyByteBuf.readVarInt();
        this.showProgress = friendlyByteBuf.readBoolean();
        this.canRestock = friendlyByteBuf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.containerId);
        this.offers.writeToStream(friendlyByteBuf);
        friendlyByteBuf.writeVarInt(this.villagerLevel);
        friendlyByteBuf.writeVarInt(this.villagerXp);
        friendlyByteBuf.writeBoolean(this.showProgress);
        friendlyByteBuf.writeBoolean(this.canRestock);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleMerchantOffers(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public MerchantOffers getOffers() {
        return this.offers;
    }

    public int getVillagerLevel() {
        return this.villagerLevel;
    }

    public int getVillagerXp() {
        return this.villagerXp;
    }

    public boolean showProgress() {
        return this.showProgress;
    }

    public boolean canRestock() {
        return this.canRestock;
    }
}

