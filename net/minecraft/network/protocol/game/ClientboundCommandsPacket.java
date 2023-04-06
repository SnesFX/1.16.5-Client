/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ClientboundCommandsPacket
implements Packet<ClientGamePacketListener> {
    private RootCommandNode<SharedSuggestionProvider> root;

    public ClientboundCommandsPacket() {
    }

    public ClientboundCommandsPacket(RootCommandNode<SharedSuggestionProvider> rootCommandNode) {
        this.root = rootCommandNode;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        Entry[] arrentry = new Entry[friendlyByteBuf.readVarInt()];
        for (int i = 0; i < arrentry.length; ++i) {
            arrentry[i] = ClientboundCommandsPacket.readNode(friendlyByteBuf);
        }
        ClientboundCommandsPacket.resolveEntries(arrentry);
        this.root = (RootCommandNode)arrentry[friendlyByteBuf.readVarInt()].node;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        Object2IntMap<CommandNode<SharedSuggestionProvider>> object2IntMap = ClientboundCommandsPacket.enumerateNodes(this.root);
        CommandNode<SharedSuggestionProvider>[] arrcommandNode = ClientboundCommandsPacket.getNodesInIdOrder(object2IntMap);
        friendlyByteBuf.writeVarInt(arrcommandNode.length);
        for (CommandNode<SharedSuggestionProvider> commandNode : arrcommandNode) {
            ClientboundCommandsPacket.writeNode(friendlyByteBuf, commandNode, object2IntMap);
        }
        friendlyByteBuf.writeVarInt(object2IntMap.get(this.root));
    }

    private static void resolveEntries(Entry[] arrentry) {
        ArrayList arrayList = Lists.newArrayList((Object[])arrentry);
        while (!arrayList.isEmpty()) {
            boolean bl = arrayList.removeIf(entry -> entry.build(arrentry));
            if (bl) continue;
            throw new IllegalStateException("Server sent an impossible command tree");
        }
    }

    private static Object2IntMap<CommandNode<SharedSuggestionProvider>> enumerateNodes(RootCommandNode<SharedSuggestionProvider> rootCommandNode) {
        CommandNode commandNode;
        Object2IntOpenHashMap object2IntOpenHashMap = new Object2IntOpenHashMap();
        ArrayDeque arrayDeque = Queues.newArrayDeque();
        arrayDeque.add(rootCommandNode);
        while ((commandNode = (CommandNode)arrayDeque.poll()) != null) {
            if (object2IntOpenHashMap.containsKey((Object)commandNode)) continue;
            int n = object2IntOpenHashMap.size();
            object2IntOpenHashMap.put((Object)commandNode, n);
            arrayDeque.addAll(commandNode.getChildren());
            if (commandNode.getRedirect() == null) continue;
            arrayDeque.add(commandNode.getRedirect());
        }
        return object2IntOpenHashMap;
    }

    private static CommandNode<SharedSuggestionProvider>[] getNodesInIdOrder(Object2IntMap<CommandNode<SharedSuggestionProvider>> object2IntMap) {
        CommandNode[] arrcommandNode = new CommandNode[object2IntMap.size()];
        for (Object2IntMap.Entry entry : Object2IntMaps.fastIterable(object2IntMap)) {
            arrcommandNode[entry.getIntValue()] = (CommandNode)entry.getKey();
        }
        return arrcommandNode;
    }

    private static Entry readNode(FriendlyByteBuf friendlyByteBuf) {
        byte by = friendlyByteBuf.readByte();
        int[] arrn = friendlyByteBuf.readVarIntArray();
        int n = (by & 8) != 0 ? friendlyByteBuf.readVarInt() : 0;
        ArgumentBuilder<SharedSuggestionProvider, ?> argumentBuilder = ClientboundCommandsPacket.createBuilder(friendlyByteBuf, by);
        return new Entry(argumentBuilder, by, n, arrn);
    }

    @Nullable
    private static ArgumentBuilder<SharedSuggestionProvider, ?> createBuilder(FriendlyByteBuf friendlyByteBuf, byte by) {
        int n = by & 3;
        if (n == 2) {
            String string = friendlyByteBuf.readUtf(32767);
            ArgumentType<?> argumentType = ArgumentTypes.deserialize(friendlyByteBuf);
            if (argumentType == null) {
                return null;
            }
            RequiredArgumentBuilder requiredArgumentBuilder = RequiredArgumentBuilder.argument((String)string, argumentType);
            if ((by & 0x10) != 0) {
                requiredArgumentBuilder.suggests(SuggestionProviders.getProvider(friendlyByteBuf.readResourceLocation()));
            }
            return requiredArgumentBuilder;
        }
        if (n == 1) {
            return LiteralArgumentBuilder.literal((String)friendlyByteBuf.readUtf(32767));
        }
        return null;
    }

    private static void writeNode(FriendlyByteBuf friendlyByteBuf, CommandNode<SharedSuggestionProvider> commandNode, Map<CommandNode<SharedSuggestionProvider>, Integer> map) {
        int n = 0;
        if (commandNode.getRedirect() != null) {
            n = (byte)(n | 8);
        }
        if (commandNode.getCommand() != null) {
            n = (byte)(n | 4);
        }
        if (commandNode instanceof RootCommandNode) {
            n = (byte)(n | 0);
        } else if (commandNode instanceof ArgumentCommandNode) {
            n = (byte)(n | 2);
            if (((ArgumentCommandNode)commandNode).getCustomSuggestions() != null) {
                n = (byte)(n | 0x10);
            }
        } else if (commandNode instanceof LiteralCommandNode) {
            n = (byte)(n | 1);
        } else {
            throw new UnsupportedOperationException("Unknown node type " + commandNode);
        }
        friendlyByteBuf.writeByte(n);
        friendlyByteBuf.writeVarInt(commandNode.getChildren().size());
        for (CommandNode commandNode2 : commandNode.getChildren()) {
            friendlyByteBuf.writeVarInt(map.get((Object)commandNode2));
        }
        if (commandNode.getRedirect() != null) {
            friendlyByteBuf.writeVarInt(map.get((Object)commandNode.getRedirect()));
        }
        if (commandNode instanceof ArgumentCommandNode) {
            ArgumentCommandNode argumentCommandNode = (ArgumentCommandNode)commandNode;
            friendlyByteBuf.writeUtf(argumentCommandNode.getName());
            ArgumentTypes.serialize(friendlyByteBuf, argumentCommandNode.getType());
            if (argumentCommandNode.getCustomSuggestions() != null) {
                friendlyByteBuf.writeResourceLocation(SuggestionProviders.getName((SuggestionProvider<SharedSuggestionProvider>)argumentCommandNode.getCustomSuggestions()));
            }
        } else if (commandNode instanceof LiteralCommandNode) {
            friendlyByteBuf.writeUtf(((LiteralCommandNode)commandNode).getLiteral());
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleCommands(this);
    }

    public RootCommandNode<SharedSuggestionProvider> getRoot() {
        return this.root;
    }

    static class Entry {
        @Nullable
        private final ArgumentBuilder<SharedSuggestionProvider, ?> builder;
        private final byte flags;
        private final int redirect;
        private final int[] children;
        @Nullable
        private CommandNode<SharedSuggestionProvider> node;

        private Entry(@Nullable ArgumentBuilder<SharedSuggestionProvider, ?> argumentBuilder, byte by, int n, int[] arrn) {
            this.builder = argumentBuilder;
            this.flags = by;
            this.redirect = n;
            this.children = arrn;
        }

        public boolean build(Entry[] arrentry) {
            if (this.node == null) {
                if (this.builder == null) {
                    this.node = new RootCommandNode();
                } else {
                    if ((this.flags & 8) != 0) {
                        if (arrentry[this.redirect].node == null) {
                            return false;
                        }
                        this.builder.redirect(arrentry[this.redirect].node);
                    }
                    if ((this.flags & 4) != 0) {
                        this.builder.executes(commandContext -> 0);
                    }
                    this.node = this.builder.build();
                }
            }
            for (int n : this.children) {
                if (arrentry[n].node != null) continue;
                return false;
            }
            for (int n : this.children) {
                CommandNode<SharedSuggestionProvider> commandNode = arrentry[n].node;
                if (commandNode instanceof RootCommandNode) continue;
                this.node.addChild(commandNode);
            }
            return true;
        }
    }

}

