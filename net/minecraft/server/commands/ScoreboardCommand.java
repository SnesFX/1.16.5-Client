/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
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
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType$Function
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
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
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.commands.arguments.OperationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.ScoreboardSlotArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardCommand {
    private static final SimpleCommandExceptionType ERROR_OBJECTIVE_ALREADY_EXISTS = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.scoreboard.objectives.add.duplicate"));
    private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_EMPTY = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.scoreboard.objectives.display.alreadyEmpty"));
    private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_SET = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.scoreboard.objectives.display.alreadySet"));
    private static final SimpleCommandExceptionType ERROR_TRIGGER_ALREADY_ENABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.scoreboard.players.enable.failed"));
    private static final SimpleCommandExceptionType ERROR_NOT_TRIGGER = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.scoreboard.players.enable.invalid"));
    private static final Dynamic2CommandExceptionType ERROR_NO_VALUE = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableComponent("commands.scoreboard.players.get.null", object, object2));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("scoreboard").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("objectives").then(Commands.literal("list").executes(commandContext -> ScoreboardCommand.listObjectives((CommandSourceStack)commandContext.getSource())))).then(Commands.literal("add").then(Commands.argument("objective", StringArgumentType.word()).then(((RequiredArgumentBuilder)Commands.argument("criteria", ObjectiveCriteriaArgument.criteria()).executes(commandContext -> ScoreboardCommand.addObjective((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"objective"), ObjectiveCriteriaArgument.getCriteria((CommandContext<CommandSourceStack>)commandContext, "criteria"), new TextComponent(StringArgumentType.getString((CommandContext)commandContext, (String)"objective"))))).then(Commands.argument("displayName", ComponentArgument.textComponent()).executes(commandContext -> ScoreboardCommand.addObjective((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"objective"), ObjectiveCriteriaArgument.getCriteria((CommandContext<CommandSourceStack>)commandContext, "criteria"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "displayName")))))))).then(Commands.literal("modify").then(((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.literal("displayname").then(Commands.argument("displayName", ComponentArgument.textComponent()).executes(commandContext -> ScoreboardCommand.setDisplayName((CommandSourceStack)commandContext.getSource(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "displayName")))))).then(ScoreboardCommand.createRenderTypeModify())))).then(Commands.literal("remove").then(Commands.argument("objective", ObjectiveArgument.objective()).executes(commandContext -> ScoreboardCommand.removeObjective((CommandSourceStack)commandContext.getSource(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective")))))).then(Commands.literal("setdisplay").then(((RequiredArgumentBuilder)Commands.argument("slot", ScoreboardSlotArgument.displaySlot()).executes(commandContext -> ScoreboardCommand.clearDisplaySlot((CommandSourceStack)commandContext.getSource(), ScoreboardSlotArgument.getDisplaySlot((CommandContext<CommandSourceStack>)commandContext, "slot")))).then(Commands.argument("objective", ObjectiveArgument.objective()).executes(commandContext -> ScoreboardCommand.setDisplaySlot((CommandSourceStack)commandContext.getSource(), ScoreboardSlotArgument.getDisplaySlot((CommandContext<CommandSourceStack>)commandContext, "slot"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective")))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("players").then(((LiteralArgumentBuilder)Commands.literal("list").executes(commandContext -> ScoreboardCommand.listTrackedPlayers((CommandSourceStack)commandContext.getSource()))).then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes(commandContext -> ScoreboardCommand.listTrackedPlayerScores((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getName((CommandContext<CommandSourceStack>)commandContext, "target")))))).then(Commands.literal("set").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer()).executes(commandContext -> ScoreboardCommand.setScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), ObjectiveArgument.getWritableObjective((CommandContext<CommandSourceStack>)commandContext, "objective"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"score")))))))).then(Commands.literal("get").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).executes(commandContext -> ScoreboardCommand.getScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getName((CommandContext<CommandSourceStack>)commandContext, "target"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective"))))))).then(Commands.literal("add").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer((int)0)).executes(commandContext -> ScoreboardCommand.addScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), ObjectiveArgument.getWritableObjective((CommandContext<CommandSourceStack>)commandContext, "objective"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"score")))))))).then(Commands.literal("remove").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer((int)0)).executes(commandContext -> ScoreboardCommand.removeScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), ObjectiveArgument.getWritableObjective((CommandContext<CommandSourceStack>)commandContext, "objective"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"score")))))))).then(Commands.literal("reset").then(((RequiredArgumentBuilder)Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes(commandContext -> ScoreboardCommand.resetScores((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets")))).then(Commands.argument("objective", ObjectiveArgument.objective()).executes(commandContext -> ScoreboardCommand.resetScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective"))))))).then(Commands.literal("enable").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).suggests((commandContext, suggestionsBuilder) -> ScoreboardCommand.suggestTriggers((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), suggestionsBuilder)).executes(commandContext -> ScoreboardCommand.enableTrigger((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective"))))))).then(Commands.literal("operation").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.argument("operation", OperationArgument.operation()).then(Commands.argument("source", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("sourceObjective", ObjectiveArgument.objective()).executes(commandContext -> ScoreboardCommand.performOperation((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "targets"), ObjectiveArgument.getWritableObjective((CommandContext<CommandSourceStack>)commandContext, "targetObjective"), OperationArgument.getOperation((CommandContext<CommandSourceStack>)commandContext, "operation"), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "source"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "sourceObjective")))))))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createRenderTypeModify() {
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal("rendertype");
        for (ObjectiveCriteria.RenderType renderType : ObjectiveCriteria.RenderType.values()) {
            literalArgumentBuilder.then(Commands.literal(renderType.getId()).executes(commandContext -> ScoreboardCommand.setRenderType((CommandSourceStack)commandContext.getSource(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective"), renderType)));
        }
        return literalArgumentBuilder;
    }

    private static CompletableFuture<Suggestions> suggestTriggers(CommandSourceStack commandSourceStack, Collection<String> collection, SuggestionsBuilder suggestionsBuilder) {
        ArrayList arrayList = Lists.newArrayList();
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        for (Objective objective : serverScoreboard.getObjectives()) {
            if (objective.getCriteria() != ObjectiveCriteria.TRIGGER) continue;
            boolean bl = false;
            for (String string : collection) {
                if (serverScoreboard.hasPlayerScore(string, objective) && !serverScoreboard.getOrCreatePlayerScore(string, objective).isLocked()) continue;
                bl = true;
                break;
            }
            if (!bl) continue;
            arrayList.add(objective.getName());
        }
        return SharedSuggestionProvider.suggest(arrayList, suggestionsBuilder);
    }

    private static int getScore(CommandSourceStack commandSourceStack, String string, Objective objective) throws CommandSyntaxException {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        if (!serverScoreboard.hasPlayerScore(string, objective)) {
            throw ERROR_NO_VALUE.create((Object)objective.getName(), (Object)string);
        }
        Score score = serverScoreboard.getOrCreatePlayerScore(string, objective);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.get.success", string, score.getScore(), objective.getFormattedDisplayName()), false);
        return score.getScore();
    }

    private static int performOperation(CommandSourceStack commandSourceStack, Collection<String> collection, Objective objective, OperationArgument.Operation operation, Collection<String> collection2, Objective objective2) throws CommandSyntaxException {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        int n = 0;
        for (String string : collection) {
            Score score = serverScoreboard.getOrCreatePlayerScore(string, objective);
            for (String string2 : collection2) {
                Score score2 = serverScoreboard.getOrCreatePlayerScore(string2, objective2);
                operation.apply(score, score2);
            }
            n += score.getScore();
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.operation.success.single", objective.getFormattedDisplayName(), collection.iterator().next(), n), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.operation.success.multiple", objective.getFormattedDisplayName(), collection.size()), true);
        }
        return n;
    }

    private static int enableTrigger(CommandSourceStack commandSourceStack, Collection<String> collection, Objective objective) throws CommandSyntaxException {
        if (objective.getCriteria() != ObjectiveCriteria.TRIGGER) {
            throw ERROR_NOT_TRIGGER.create();
        }
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        int n = 0;
        for (String string : collection) {
            Score score = serverScoreboard.getOrCreatePlayerScore(string, objective);
            if (!score.isLocked()) continue;
            score.setLocked(false);
            ++n;
        }
        if (n == 0) {
            throw ERROR_TRIGGER_ALREADY_ENABLED.create();
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.enable.success.single", objective.getFormattedDisplayName(), collection.iterator().next()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.enable.success.multiple", objective.getFormattedDisplayName(), collection.size()), true);
        }
        return n;
    }

    private static int resetScores(CommandSourceStack commandSourceStack, Collection<String> collection) {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        for (String string : collection) {
            serverScoreboard.resetPlayerScore(string, null);
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.all.single", collection.iterator().next()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.all.multiple", collection.size()), true);
        }
        return collection.size();
    }

    private static int resetScore(CommandSourceStack commandSourceStack, Collection<String> collection, Objective objective) {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        for (String string : collection) {
            serverScoreboard.resetPlayerScore(string, objective);
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.specific.single", objective.getFormattedDisplayName(), collection.iterator().next()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.specific.multiple", objective.getFormattedDisplayName(), collection.size()), true);
        }
        return collection.size();
    }

    private static int setScore(CommandSourceStack commandSourceStack, Collection<String> collection, Objective objective, int n) {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        for (String string : collection) {
            Score score = serverScoreboard.getOrCreatePlayerScore(string, objective);
            score.setScore(n);
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.set.success.single", objective.getFormattedDisplayName(), collection.iterator().next(), n), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.set.success.multiple", objective.getFormattedDisplayName(), collection.size(), n), true);
        }
        return n * collection.size();
    }

    private static int addScore(CommandSourceStack commandSourceStack, Collection<String> collection, Objective objective, int n) {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        int n2 = 0;
        for (String string : collection) {
            Score score = serverScoreboard.getOrCreatePlayerScore(string, objective);
            score.setScore(score.getScore() + n);
            n2 += score.getScore();
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.add.success.single", n, objective.getFormattedDisplayName(), collection.iterator().next(), n2), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.add.success.multiple", n, objective.getFormattedDisplayName(), collection.size()), true);
        }
        return n2;
    }

    private static int removeScore(CommandSourceStack commandSourceStack, Collection<String> collection, Objective objective, int n) {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        int n2 = 0;
        for (String string : collection) {
            Score score = serverScoreboard.getOrCreatePlayerScore(string, objective);
            score.setScore(score.getScore() - n);
            n2 += score.getScore();
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.remove.success.single", n, objective.getFormattedDisplayName(), collection.iterator().next(), n2), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.remove.success.multiple", n, objective.getFormattedDisplayName(), collection.size()), true);
        }
        return n2;
    }

    private static int listTrackedPlayers(CommandSourceStack commandSourceStack) {
        Collection<String> collection = commandSourceStack.getServer().getScoreboard().getTrackedPlayers();
        if (collection.isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.empty"), false);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.success", collection.size(), ComponentUtils.formatList(collection)), false);
        }
        return collection.size();
    }

    private static int listTrackedPlayerScores(CommandSourceStack commandSourceStack, String string) {
        Map<Objective, Score> map = commandSourceStack.getServer().getScoreboard().getPlayerScores(string);
        if (map.isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.empty", string), false);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.success", string, map.size()), false);
            for (Map.Entry<Objective, Score> entry : map.entrySet()) {
                commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.entry", entry.getKey().getFormattedDisplayName(), entry.getValue().getScore()), false);
            }
        }
        return map.size();
    }

    private static int clearDisplaySlot(CommandSourceStack commandSourceStack, int n) throws CommandSyntaxException {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        if (serverScoreboard.getDisplayObjective(n) == null) {
            throw ERROR_DISPLAY_SLOT_ALREADY_EMPTY.create();
        }
        ((Scoreboard)serverScoreboard).setDisplayObjective(n, null);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.display.cleared", Scoreboard.getDisplaySlotNames()[n]), true);
        return 0;
    }

    private static int setDisplaySlot(CommandSourceStack commandSourceStack, int n, Objective objective) throws CommandSyntaxException {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        if (serverScoreboard.getDisplayObjective(n) == objective) {
            throw ERROR_DISPLAY_SLOT_ALREADY_SET.create();
        }
        ((Scoreboard)serverScoreboard).setDisplayObjective(n, objective);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.display.set", Scoreboard.getDisplaySlotNames()[n], objective.getDisplayName()), true);
        return 0;
    }

    private static int setDisplayName(CommandSourceStack commandSourceStack, Objective objective, Component component) {
        if (!objective.getDisplayName().equals(component)) {
            objective.setDisplayName(component);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.modify.displayname", objective.getName(), objective.getFormattedDisplayName()), true);
        }
        return 0;
    }

    private static int setRenderType(CommandSourceStack commandSourceStack, Objective objective, ObjectiveCriteria.RenderType renderType) {
        if (objective.getRenderType() != renderType) {
            objective.setRenderType(renderType);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.modify.rendertype", objective.getFormattedDisplayName()), true);
        }
        return 0;
    }

    private static int removeObjective(CommandSourceStack commandSourceStack, Objective objective) {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        serverScoreboard.removeObjective(objective);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.remove.success", objective.getFormattedDisplayName()), true);
        return serverScoreboard.getObjectives().size();
    }

    private static int addObjective(CommandSourceStack commandSourceStack, String string, ObjectiveCriteria objectiveCriteria, Component component) throws CommandSyntaxException {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        if (serverScoreboard.getObjective(string) != null) {
            throw ERROR_OBJECTIVE_ALREADY_EXISTS.create();
        }
        if (string.length() > 16) {
            throw ObjectiveArgument.ERROR_OBJECTIVE_NAME_TOO_LONG.create((Object)16);
        }
        serverScoreboard.addObjective(string, objectiveCriteria, component, objectiveCriteria.getDefaultRenderType());
        Objective objective = serverScoreboard.getObjective(string);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.add.success", objective.getFormattedDisplayName()), true);
        return serverScoreboard.getObjectives().size();
    }

    private static int listObjectives(CommandSourceStack commandSourceStack) {
        Collection<Objective> collection = commandSourceStack.getServer().getScoreboard().getObjectives();
        if (collection.isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.list.empty"), false);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.list.success", collection.size(), ComponentUtils.formatList(collection, Objective::getFormattedDisplayName)), false);
        }
        return collection.size();
    }
}

