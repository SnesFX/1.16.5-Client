/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType$Function
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.server.commands;

import com.google.common.base.Joiner;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class ForceLoadCommand {
    private static final Dynamic2CommandExceptionType ERROR_TOO_MANY_CHUNKS = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableComponent("commands.forceload.toobig", object, object2));
    private static final Dynamic2CommandExceptionType ERROR_NOT_TICKING = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableComponent("commands.forceload.query.failure", object, object2));
    private static final SimpleCommandExceptionType ERROR_ALL_ADDED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.forceload.added.failure"));
    private static final SimpleCommandExceptionType ERROR_NONE_REMOVED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.forceload.removed.failure"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("forceload").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("from", ColumnPosArgument.columnPos()).executes(commandContext -> ForceLoadCommand.changeForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "from"), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "from"), true))).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes(commandContext -> ForceLoadCommand.changeForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "from"), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "to"), true)))))).then(((LiteralArgumentBuilder)Commands.literal("remove").then(((RequiredArgumentBuilder)Commands.argument("from", ColumnPosArgument.columnPos()).executes(commandContext -> ForceLoadCommand.changeForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "from"), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "from"), false))).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes(commandContext -> ForceLoadCommand.changeForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "from"), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "to"), false))))).then(Commands.literal("all").executes(commandContext -> ForceLoadCommand.removeAll((CommandSourceStack)commandContext.getSource()))))).then(((LiteralArgumentBuilder)Commands.literal("query").executes(commandContext -> ForceLoadCommand.listForceLoad((CommandSourceStack)commandContext.getSource()))).then(Commands.argument("pos", ColumnPosArgument.columnPos()).executes(commandContext -> ForceLoadCommand.queryForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos((CommandContext<CommandSourceStack>)commandContext, "pos"))))));
    }

    private static int queryForceLoad(CommandSourceStack commandSourceStack, ColumnPos columnPos) throws CommandSyntaxException {
        ChunkPos chunkPos = new ChunkPos(columnPos.x >> 4, columnPos.z >> 4);
        ServerLevel serverLevel = commandSourceStack.getLevel();
        ResourceKey<Level> resourceKey = serverLevel.dimension();
        boolean bl = serverLevel.getForcedChunks().contains(chunkPos.toLong());
        if (bl) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.forceload.query.success", chunkPos, resourceKey.location()), false);
            return 1;
        }
        throw ERROR_NOT_TICKING.create((Object)chunkPos, (Object)resourceKey.location());
    }

    private static int listForceLoad(CommandSourceStack commandSourceStack) {
        ServerLevel serverLevel = commandSourceStack.getLevel();
        ResourceKey<Level> resourceKey = serverLevel.dimension();
        LongSet longSet = serverLevel.getForcedChunks();
        int n = longSet.size();
        if (n > 0) {
            String string = Joiner.on((String)", ").join(longSet.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
            if (n == 1) {
                commandSourceStack.sendSuccess(new TranslatableComponent("commands.forceload.list.single", resourceKey.location(), string), false);
            } else {
                commandSourceStack.sendSuccess(new TranslatableComponent("commands.forceload.list.multiple", n, resourceKey.location(), string), false);
            }
        } else {
            commandSourceStack.sendFailure(new TranslatableComponent("commands.forceload.added.none", resourceKey.location()));
        }
        return n;
    }

    private static int removeAll(CommandSourceStack commandSourceStack) {
        ServerLevel serverLevel = commandSourceStack.getLevel();
        ResourceKey<Level> resourceKey = serverLevel.dimension();
        LongSet longSet = serverLevel.getForcedChunks();
        longSet.forEach(l -> serverLevel.setChunkForced(ChunkPos.getX(l), ChunkPos.getZ(l), false));
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.forceload.removed.all", resourceKey.location()), true);
        return 0;
    }

    private static int changeForceLoad(CommandSourceStack commandSourceStack, ColumnPos columnPos, ColumnPos columnPos2, boolean bl) throws CommandSyntaxException {
        int n = Math.min(columnPos.x, columnPos2.x);
        int n2 = Math.min(columnPos.z, columnPos2.z);
        int n3 = Math.max(columnPos.x, columnPos2.x);
        int n4 = Math.max(columnPos.z, columnPos2.z);
        if (n < -30000000 || n2 < -30000000 || n3 >= 30000000 || n4 >= 30000000) {
            throw BlockPosArgument.ERROR_OUT_OF_WORLD.create();
        }
        int n5 = n3 >> 4;
        int n6 = n >> 4;
        int n7 = n4 >> 4;
        int n8 = n2 >> 4;
        long l = ((long)(n5 - n6) + 1L) * ((long)(n7 - n8) + 1L);
        if (l > 256L) {
            throw ERROR_TOO_MANY_CHUNKS.create((Object)256, (Object)l);
        }
        ServerLevel serverLevel = commandSourceStack.getLevel();
        ResourceKey<Level> resourceKey = serverLevel.dimension();
        ChunkPos chunkPos = null;
        int n9 = 0;
        for (int i = n6; i <= n5; ++i) {
            for (int j = n8; j <= n7; ++j) {
                boolean bl2 = serverLevel.setChunkForced(i, j, bl);
                if (!bl2) continue;
                ++n9;
                if (chunkPos != null) continue;
                chunkPos = new ChunkPos(i, j);
            }
        }
        if (n9 == 0) {
            throw (bl ? ERROR_ALL_ADDED : ERROR_NONE_REMOVED).create();
        }
        if (n9 == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.forceload." + (bl ? "added" : "removed") + ".single", chunkPos, resourceKey.location()), true);
        } else {
            ChunkPos chunkPos2 = new ChunkPos(n6, n8);
            ChunkPos chunkPos3 = new ChunkPos(n5, n7);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.forceload." + (bl ? "added" : "removed") + ".multiple", n9, resourceKey.location(), chunkPos2, chunkPos3), true);
        }
        return n9;
    }
}

