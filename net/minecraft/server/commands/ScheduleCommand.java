/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.timers.FunctionCallback;
import net.minecraft.world.level.timers.FunctionTagCallback;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;

public class ScheduleCommand {
    private static final SimpleCommandExceptionType ERROR_SAME_TICK = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.schedule.same_tick"));
    private static final DynamicCommandExceptionType ERROR_CANT_REMOVE = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.schedule.cleared.failure", object));
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_SCHEDULE = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(((CommandSourceStack)commandContext.getSource()).getServer().getWorldData().overworldData().getScheduledEvents().getEventsIds(), suggestionsBuilder);

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("schedule").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.literal("function").then(Commands.argument("function", FunctionArgument.functions()).suggests(FunctionCommand.SUGGEST_FUNCTION).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("time", TimeArgument.time()).executes(commandContext -> ScheduleCommand.schedule((CommandSourceStack)commandContext.getSource(), FunctionArgument.getFunctionOrTag((CommandContext<CommandSourceStack>)commandContext, "function"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time"), true))).then(Commands.literal("append").executes(commandContext -> ScheduleCommand.schedule((CommandSourceStack)commandContext.getSource(), FunctionArgument.getFunctionOrTag((CommandContext<CommandSourceStack>)commandContext, "function"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time"), false)))).then(Commands.literal("replace").executes(commandContext -> ScheduleCommand.schedule((CommandSourceStack)commandContext.getSource(), FunctionArgument.getFunctionOrTag((CommandContext<CommandSourceStack>)commandContext, "function"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time"), true))))))).then(Commands.literal("clear").then(Commands.argument("function", StringArgumentType.greedyString()).suggests(SUGGEST_SCHEDULE).executes(commandContext -> ScheduleCommand.remove((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"function"))))));
    }

    private static int schedule(CommandSourceStack commandSourceStack, Pair<ResourceLocation, Either<CommandFunction, Tag<CommandFunction>>> pair, int n, boolean bl) throws CommandSyntaxException {
        if (n == 0) {
            throw ERROR_SAME_TICK.create();
        }
        long l = commandSourceStack.getLevel().getGameTime() + (long)n;
        ResourceLocation resourceLocation = (ResourceLocation)pair.getFirst();
        TimerQueue<MinecraftServer> timerQueue = commandSourceStack.getServer().getWorldData().overworldData().getScheduledEvents();
        ((Either)pair.getSecond()).ifLeft(commandFunction -> {
            String string = resourceLocation.toString();
            if (bl) {
                timerQueue.remove(string);
            }
            timerQueue.schedule(string, l, new FunctionCallback(resourceLocation));
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.schedule.created.function", resourceLocation, n, l), true);
        }).ifRight(tag -> {
            String string = "#" + resourceLocation.toString();
            if (bl) {
                timerQueue.remove(string);
            }
            timerQueue.schedule(string, l, new FunctionTagCallback(resourceLocation));
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.schedule.created.tag", resourceLocation, n, l), true);
        });
        return (int)Math.floorMod(l, Integer.MAX_VALUE);
    }

    private static int remove(CommandSourceStack commandSourceStack, String string) throws CommandSyntaxException {
        int n = commandSourceStack.getServer().getWorldData().overworldData().getScheduledEvents().remove(string);
        if (n == 0) {
            throw ERROR_CANT_REMOVE.create((Object)string);
        }
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.schedule.cleared.success", n, string), true);
        return n;
    }
}

