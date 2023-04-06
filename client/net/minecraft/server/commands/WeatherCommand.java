/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;

public class WeatherCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("weather").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(((LiteralArgumentBuilder)Commands.literal("clear").executes(commandContext -> WeatherCommand.setClear((CommandSourceStack)commandContext.getSource(), 6000))).then(Commands.argument("duration", IntegerArgumentType.integer((int)0, (int)1000000)).executes(commandContext -> WeatherCommand.setClear((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"duration") * 20))))).then(((LiteralArgumentBuilder)Commands.literal("rain").executes(commandContext -> WeatherCommand.setRain((CommandSourceStack)commandContext.getSource(), 6000))).then(Commands.argument("duration", IntegerArgumentType.integer((int)0, (int)1000000)).executes(commandContext -> WeatherCommand.setRain((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"duration") * 20))))).then(((LiteralArgumentBuilder)Commands.literal("thunder").executes(commandContext -> WeatherCommand.setThunder((CommandSourceStack)commandContext.getSource(), 6000))).then(Commands.argument("duration", IntegerArgumentType.integer((int)0, (int)1000000)).executes(commandContext -> WeatherCommand.setThunder((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"duration") * 20)))));
    }

    private static int setClear(CommandSourceStack commandSourceStack, int n) {
        commandSourceStack.getLevel().setWeatherParameters(n, 0, false, false);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.weather.set.clear"), true);
        return n;
    }

    private static int setRain(CommandSourceStack commandSourceStack, int n) {
        commandSourceStack.getLevel().setWeatherParameters(0, n, true, false);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.weather.set.rain"), true);
        return n;
    }

    private static int setThunder(CommandSourceStack commandSourceStack, int n) {
        commandSourceStack.getLevel().setWeatherParameters(0, n, true, true);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.weather.set.thunder"), true);
        return n;
    }
}

