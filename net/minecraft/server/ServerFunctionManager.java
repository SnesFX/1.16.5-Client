/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 */
package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.GameRules;

public class ServerFunctionManager {
    private static final ResourceLocation TICK_FUNCTION_TAG = new ResourceLocation("tick");
    private static final ResourceLocation LOAD_FUNCTION_TAG = new ResourceLocation("load");
    private final MinecraftServer server;
    private boolean isInFunction;
    private final ArrayDeque<QueuedCommand> commandQueue = new ArrayDeque();
    private final List<QueuedCommand> nestedCalls = Lists.newArrayList();
    private final List<CommandFunction> ticking = Lists.newArrayList();
    private boolean postReload;
    private ServerFunctionLibrary library;

    public ServerFunctionManager(MinecraftServer minecraftServer, ServerFunctionLibrary serverFunctionLibrary) {
        this.server = minecraftServer;
        this.library = serverFunctionLibrary;
        this.postReload(serverFunctionLibrary);
    }

    public int getCommandLimit() {
        return this.server.getGameRules().getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
    }

    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return this.server.getCommands().getDispatcher();
    }

    public void tick() {
        this.executeTagFunctions(this.ticking, TICK_FUNCTION_TAG);
        if (this.postReload) {
            this.postReload = false;
            List<CommandFunction> list = this.library.getTags().getTagOrEmpty(LOAD_FUNCTION_TAG).getValues();
            this.executeTagFunctions(list, LOAD_FUNCTION_TAG);
        }
    }

    private void executeTagFunctions(Collection<CommandFunction> collection, ResourceLocation resourceLocation) {
        this.server.getProfiler().push(resourceLocation::toString);
        for (CommandFunction commandFunction : collection) {
            this.execute(commandFunction, this.getGameLoopSender());
        }
        this.server.getProfiler().pop();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int execute(CommandFunction commandFunction, CommandSourceStack commandSourceStack) {
        int n = this.getCommandLimit();
        if (this.isInFunction) {
            if (this.commandQueue.size() + this.nestedCalls.size() < n) {
                this.nestedCalls.add(new QueuedCommand(this, commandSourceStack, new CommandFunction.FunctionEntry(commandFunction)));
            }
            return 0;
        }
        try {
            int n2;
            this.isInFunction = true;
            int n3 = 0;
            CommandFunction.Entry[] arrentry = commandFunction.getEntries();
            for (n2 = arrentry.length - 1; n2 >= 0; --n2) {
                this.commandQueue.push(new QueuedCommand(this, commandSourceStack, arrentry[n2]));
            }
            while (!this.commandQueue.isEmpty()) {
                try {
                    QueuedCommand queuedCommand = this.commandQueue.removeFirst();
                    this.server.getProfiler().push(queuedCommand::toString);
                    queuedCommand.execute(this.commandQueue, n);
                    if (!this.nestedCalls.isEmpty()) {
                        Lists.reverse(this.nestedCalls).forEach(this.commandQueue::addFirst);
                        this.nestedCalls.clear();
                    }
                }
                finally {
                    this.server.getProfiler().pop();
                }
                if (++n3 < n) continue;
                int n4 = n3;
                return n4;
            }
            n2 = n3;
            return n2;
        }
        finally {
            this.commandQueue.clear();
            this.nestedCalls.clear();
            this.isInFunction = false;
        }
    }

    public void replaceLibrary(ServerFunctionLibrary serverFunctionLibrary) {
        this.library = serverFunctionLibrary;
        this.postReload(serverFunctionLibrary);
    }

    private void postReload(ServerFunctionLibrary serverFunctionLibrary) {
        this.ticking.clear();
        this.ticking.addAll(serverFunctionLibrary.getTags().getTagOrEmpty(TICK_FUNCTION_TAG).getValues());
        this.postReload = true;
    }

    public CommandSourceStack getGameLoopSender() {
        return this.server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
    }

    public Optional<CommandFunction> get(ResourceLocation resourceLocation) {
        return this.library.getFunction(resourceLocation);
    }

    public Tag<CommandFunction> getTag(ResourceLocation resourceLocation) {
        return this.library.getTag(resourceLocation);
    }

    public Iterable<ResourceLocation> getFunctionNames() {
        return this.library.getFunctions().keySet();
    }

    public Iterable<ResourceLocation> getTagNames() {
        return this.library.getTags().getAvailableTags();
    }

    public static class QueuedCommand {
        private final ServerFunctionManager manager;
        private final CommandSourceStack sender;
        private final CommandFunction.Entry entry;

        public QueuedCommand(ServerFunctionManager serverFunctionManager, CommandSourceStack commandSourceStack, CommandFunction.Entry entry) {
            this.manager = serverFunctionManager;
            this.sender = commandSourceStack;
            this.entry = entry;
        }

        public void execute(ArrayDeque<QueuedCommand> arrayDeque, int n) {
            try {
                this.entry.execute(this.manager, this.sender, arrayDeque, n);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }

        public String toString() {
            return this.entry.toString();
        }
    }

}

