/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class ExperienceCommand {
    private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.experience.set.points.invalid"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        LiteralCommandNode literalCommandNode = commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("experience").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer()).executes(commandContext -> ExperienceCommand.addExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Type.POINTS))).then(Commands.literal("points").executes(commandContext -> ExperienceCommand.addExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Type.POINTS)))).then(Commands.literal("levels").executes(commandContext -> ExperienceCommand.addExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Type.LEVELS))))))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer((int)0)).executes(commandContext -> ExperienceCommand.setExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Type.POINTS))).then(Commands.literal("points").executes(commandContext -> ExperienceCommand.setExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Type.POINTS)))).then(Commands.literal("levels").executes(commandContext -> ExperienceCommand.setExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amount"), Type.LEVELS))))))).then(Commands.literal("query").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.player()).then(Commands.literal("points").executes(commandContext -> ExperienceCommand.queryExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayer((CommandContext<CommandSourceStack>)commandContext, "targets"), Type.POINTS)))).then(Commands.literal("levels").executes(commandContext -> ExperienceCommand.queryExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayer((CommandContext<CommandSourceStack>)commandContext, "targets"), Type.LEVELS))))));
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("xp").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).redirect((CommandNode)literalCommandNode));
    }

    private static int queryExperience(CommandSourceStack commandSourceStack, ServerPlayer serverPlayer, Type type) {
        int n = type.query.applyAsInt(serverPlayer);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.experience.query." + type.name, serverPlayer.getDisplayName(), n), false);
        return n;
    }

    private static int addExperience(CommandSourceStack commandSourceStack, Collection<? extends ServerPlayer> collection, int n, Type type) {
        for (ServerPlayer serverPlayer : collection) {
            type.add.accept(serverPlayer, n);
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.experience.add." + type.name + ".success.single", n, collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.experience.add." + type.name + ".success.multiple", n, collection.size()), true);
        }
        return collection.size();
    }

    private static int setExperience(CommandSourceStack commandSourceStack, Collection<? extends ServerPlayer> collection, int n, Type type) throws CommandSyntaxException {
        int n2 = 0;
        for (ServerPlayer serverPlayer : collection) {
            if (!type.set.test(serverPlayer, n)) continue;
            ++n2;
        }
        if (n2 == 0) {
            throw ERROR_SET_POINTS_INVALID.create();
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.experience.set." + type.name + ".success.single", n, collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.experience.set." + type.name + ".success.multiple", n, collection.size()), true);
        }
        return collection.size();
    }

    static enum Type {
        POINTS("points", Player::giveExperiencePoints, (serverPlayer, n) -> {
            if (n >= serverPlayer.getXpNeededForNextLevel()) {
                return false;
            }
            serverPlayer.setExperiencePoints((int)n);
            return true;
        }, serverPlayer -> Mth.floor(serverPlayer.experienceProgress * (float)serverPlayer.getXpNeededForNextLevel())),
        LEVELS("levels", ServerPlayer::giveExperienceLevels, (serverPlayer, n) -> {
            serverPlayer.setExperienceLevels((int)n);
            return true;
        }, serverPlayer -> serverPlayer.experienceLevel);
        
        public final BiConsumer<ServerPlayer, Integer> add;
        public final BiPredicate<ServerPlayer, Integer> set;
        public final String name;
        private final ToIntFunction<ServerPlayer> query;

        private Type(String string2, BiConsumer<ServerPlayer, Integer> biConsumer, BiPredicate<ServerPlayer, Integer> biPredicate, ToIntFunction<ServerPlayer> toIntFunction) {
            this.add = biConsumer;
            this.name = string2;
            this.set = biPredicate;
            this.query = toIntFunction;
        }
    }

}

