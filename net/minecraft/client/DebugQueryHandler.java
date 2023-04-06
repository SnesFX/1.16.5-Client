/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;

public class DebugQueryHandler {
    private final ClientPacketListener connection;
    private int transactionId = -1;
    @Nullable
    private Consumer<CompoundTag> callback;

    public DebugQueryHandler(ClientPacketListener clientPacketListener) {
        this.connection = clientPacketListener;
    }

    public boolean handleResponse(int n, @Nullable CompoundTag compoundTag) {
        if (this.transactionId == n && this.callback != null) {
            this.callback.accept(compoundTag);
            this.callback = null;
            return true;
        }
        return false;
    }

    private int startTransaction(Consumer<CompoundTag> consumer) {
        this.callback = consumer;
        return ++this.transactionId;
    }

    public void queryEntityTag(int n, Consumer<CompoundTag> consumer) {
        int n2 = this.startTransaction(consumer);
        this.connection.send(new ServerboundEntityTagQuery(n2, n));
    }

    public void queryBlockEntityTag(BlockPos blockPos, Consumer<CompoundTag> consumer) {
        int n = this.startTransaction(consumer);
        this.connection.send(new ServerboundBlockEntityTagQuery(n, blockPos));
    }
}

