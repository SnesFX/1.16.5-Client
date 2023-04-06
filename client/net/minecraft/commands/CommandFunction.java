/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.context.CommandContextBuilder
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;

public class CommandFunction {
    private final Entry[] entries;
    private final ResourceLocation id;

    public CommandFunction(ResourceLocation resourceLocation, Entry[] arrentry) {
        this.id = resourceLocation;
        this.entries = arrentry;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Entry[] getEntries() {
        return this.entries;
    }

    public static CommandFunction fromLines(ResourceLocation resourceLocation, CommandDispatcher<CommandSourceStack> commandDispatcher, CommandSourceStack commandSourceStack, List<String> list) {
        ArrayList arrayList = Lists.newArrayListWithCapacity((int)list.size());
        for (int i = 0; i < list.size(); ++i) {
            Object object;
            int n = i + 1;
            String string = list.get(i).trim();
            StringReader stringReader = new StringReader(string);
            if (!stringReader.canRead() || stringReader.peek() == '#') continue;
            if (stringReader.peek() == '/') {
                stringReader.skip();
                if (stringReader.peek() == '/') {
                    throw new IllegalArgumentException("Unknown or invalid command '" + string + "' on line " + n + " (if you intended to make a comment, use '#' not '//')");
                }
                object = stringReader.readUnquotedString();
                throw new IllegalArgumentException("Unknown or invalid command '" + string + "' on line " + n + " (did you mean '" + (String)object + "'? Do not use a preceding forwards slash.)");
            }
            try {
                object = commandDispatcher.parse(stringReader, (Object)commandSourceStack);
                if (object.getReader().canRead()) {
                    throw Commands.getParseException(object);
                }
                arrayList.add(new CommandEntry((ParseResults<CommandSourceStack>)object));
                continue;
            }
            catch (CommandSyntaxException commandSyntaxException) {
                throw new IllegalArgumentException("Whilst parsing command on line " + n + ": " + commandSyntaxException.getMessage());
            }
        }
        return new CommandFunction(resourceLocation, arrayList.toArray(new Entry[0]));
    }

    public static class CacheableFunction {
        public static final CacheableFunction NONE = new CacheableFunction((ResourceLocation)null);
        @Nullable
        private final ResourceLocation id;
        private boolean resolved;
        private Optional<CommandFunction> function = Optional.empty();

        public CacheableFunction(@Nullable ResourceLocation resourceLocation) {
            this.id = resourceLocation;
        }

        public CacheableFunction(CommandFunction commandFunction) {
            this.resolved = true;
            this.id = null;
            this.function = Optional.of(commandFunction);
        }

        public Optional<CommandFunction> get(ServerFunctionManager serverFunctionManager) {
            if (!this.resolved) {
                if (this.id != null) {
                    this.function = serverFunctionManager.get(this.id);
                }
                this.resolved = true;
            }
            return this.function;
        }

        @Nullable
        public ResourceLocation getId() {
            return this.function.map(commandFunction -> commandFunction.id).orElse(this.id);
        }
    }

    public static class FunctionEntry
    implements Entry {
        private final CacheableFunction function;

        public FunctionEntry(CommandFunction commandFunction) {
            this.function = new CacheableFunction(commandFunction);
        }

        @Override
        public void execute(ServerFunctionManager serverFunctionManager, CommandSourceStack commandSourceStack, ArrayDeque<ServerFunctionManager.QueuedCommand> arrayDeque, int n) {
            this.function.get(serverFunctionManager).ifPresent(commandFunction -> {
                Entry[] arrentry = commandFunction.getEntries();
                int n2 = n - arrayDeque.size();
                int n3 = Math.min(arrentry.length, n2);
                for (int i = n3 - 1; i >= 0; --i) {
                    arrayDeque.addFirst(new ServerFunctionManager.QueuedCommand(serverFunctionManager, commandSourceStack, arrentry[i]));
                }
            });
        }

        public String toString() {
            return "function " + this.function.getId();
        }
    }

    public static class CommandEntry
    implements Entry {
        private final ParseResults<CommandSourceStack> parse;

        public CommandEntry(ParseResults<CommandSourceStack> parseResults) {
            this.parse = parseResults;
        }

        @Override
        public void execute(ServerFunctionManager serverFunctionManager, CommandSourceStack commandSourceStack, ArrayDeque<ServerFunctionManager.QueuedCommand> arrayDeque, int n) throws CommandSyntaxException {
            serverFunctionManager.getDispatcher().execute(new ParseResults(this.parse.getContext().withSource((Object)commandSourceStack), this.parse.getReader(), this.parse.getExceptions()));
        }

        public String toString() {
            return this.parse.getReader().getString();
        }
    }

    public static interface Entry {
        public void execute(ServerFunctionManager var1, CommandSourceStack var2, ArrayDeque<ServerFunctionManager.QueuedCommand> var3, int var4) throws CommandSyntaxException;
    }

}

