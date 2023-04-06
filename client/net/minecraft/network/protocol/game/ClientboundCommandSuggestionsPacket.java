/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.context.StringRange
 *  com.mojang.brigadier.suggestion.Suggestion
 *  com.mojang.brigadier.suggestion.Suggestions
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundCommandSuggestionsPacket
implements Packet<ClientGamePacketListener> {
    private int id;
    private Suggestions suggestions;

    public ClientboundCommandSuggestionsPacket() {
    }

    public ClientboundCommandSuggestionsPacket(int n, Suggestions suggestions) {
        this.id = n;
        this.suggestions = suggestions;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.id = friendlyByteBuf.readVarInt();
        int n = friendlyByteBuf.readVarInt();
        int n2 = friendlyByteBuf.readVarInt();
        StringRange stringRange = StringRange.between((int)n, (int)(n + n2));
        int n3 = friendlyByteBuf.readVarInt();
        ArrayList arrayList = Lists.newArrayListWithCapacity((int)n3);
        for (int i = 0; i < n3; ++i) {
            String string = friendlyByteBuf.readUtf(32767);
            Component component = friendlyByteBuf.readBoolean() ? friendlyByteBuf.readComponent() : null;
            arrayList.add(new Suggestion(stringRange, string, (Message)component));
        }
        this.suggestions = new Suggestions(stringRange, (List)arrayList);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.id);
        friendlyByteBuf.writeVarInt(this.suggestions.getRange().getStart());
        friendlyByteBuf.writeVarInt(this.suggestions.getRange().getLength());
        friendlyByteBuf.writeVarInt(this.suggestions.getList().size());
        for (Suggestion suggestion : this.suggestions.getList()) {
            friendlyByteBuf.writeUtf(suggestion.getText());
            friendlyByteBuf.writeBoolean(suggestion.getTooltip() != null);
            if (suggestion.getTooltip() == null) continue;
            friendlyByteBuf.writeComponent(ComponentUtils.fromMessage(suggestion.getTooltip()));
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleCommandSuggestions(this);
    }

    public int getId() {
        return this.id;
    }

    public Suggestions getSuggestions() {
        return this.suggestions;
    }
}

