/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class TeamCommand {
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EXISTS = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.add.duplicate"));
    private static final DynamicCommandExceptionType ERROR_TEAM_NAME_TOO_LONG = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.team.add.longName", object));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EMPTY = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.empty.unchanged"));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_NAME = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.name.unchanged"));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_COLOR = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.color.unchanged"));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.friendlyfire.alreadyEnabled"));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.friendlyfire.alreadyDisabled"));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.seeFriendlyInvisibles.alreadyEnabled"));
    private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.seeFriendlyInvisibles.alreadyDisabled"));
    private static final SimpleCommandExceptionType ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.nametagVisibility.unchanged"));
    private static final SimpleCommandExceptionType ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.deathMessageVisibility.unchanged"));
    private static final SimpleCommandExceptionType ERROR_TEAM_COLLISION_UNCHANGED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.team.option.collisionRule.unchanged"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("team").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(((LiteralArgumentBuilder)Commands.literal("list").executes(commandContext -> TeamCommand.listTeams((CommandSourceStack)commandContext.getSource()))).then(Commands.argument("team", TeamArgument.team()).executes(commandContext -> TeamCommand.listMembers((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team")))))).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("team", StringArgumentType.word()).executes(commandContext -> TeamCommand.createTeam((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"team")))).then(Commands.argument("displayName", ComponentArgument.textComponent()).executes(commandContext -> TeamCommand.createTeam((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"team"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "displayName"))))))).then(Commands.literal("remove").then(Commands.argument("team", TeamArgument.team()).executes(commandContext -> TeamCommand.deleteTeam((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team")))))).then(Commands.literal("empty").then(Commands.argument("team", TeamArgument.team()).executes(commandContext -> TeamCommand.emptyTeam((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team")))))).then(Commands.literal("join").then(((RequiredArgumentBuilder)Commands.argument("team", TeamArgument.team()).executes(commandContext -> TeamCommand.joinTeam((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Collections.singleton(((CommandSourceStack)commandContext.getSource()).getEntityOrException().getScoreboardName())))).then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes(commandContext -> TeamCommand.joinTeam((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "members"))))))).then(Commands.literal("leave").then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes(commandContext -> TeamCommand.leaveTeam((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)commandContext, "members")))))).then(Commands.literal("modify").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("team", TeamArgument.team()).then(Commands.literal("displayName").then(Commands.argument("displayName", ComponentArgument.textComponent()).executes(commandContext -> TeamCommand.setDisplayName((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "displayName")))))).then(Commands.literal("color").then(Commands.argument("value", ColorArgument.color()).executes(commandContext -> TeamCommand.setColor((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), ColorArgument.getColor((CommandContext<CommandSourceStack>)commandContext, "value")))))).then(Commands.literal("friendlyFire").then(Commands.argument("allowed", BoolArgumentType.bool()).executes(commandContext -> TeamCommand.setFriendlyFire((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), BoolArgumentType.getBool((CommandContext)commandContext, (String)"allowed")))))).then(Commands.literal("seeFriendlyInvisibles").then(Commands.argument("allowed", BoolArgumentType.bool()).executes(commandContext -> TeamCommand.setFriendlySight((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), BoolArgumentType.getBool((CommandContext)commandContext, (String)"allowed")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("nametagVisibility").then(Commands.literal("never").executes(commandContext -> TeamCommand.setNametagVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.NEVER)))).then(Commands.literal("hideForOtherTeams").executes(commandContext -> TeamCommand.setNametagVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.HIDE_FOR_OTHER_TEAMS)))).then(Commands.literal("hideForOwnTeam").executes(commandContext -> TeamCommand.setNametagVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.HIDE_FOR_OWN_TEAM)))).then(Commands.literal("always").executes(commandContext -> TeamCommand.setNametagVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.ALWAYS))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("deathMessageVisibility").then(Commands.literal("never").executes(commandContext -> TeamCommand.setDeathMessageVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.NEVER)))).then(Commands.literal("hideForOtherTeams").executes(commandContext -> TeamCommand.setDeathMessageVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.HIDE_FOR_OTHER_TEAMS)))).then(Commands.literal("hideForOwnTeam").executes(commandContext -> TeamCommand.setDeathMessageVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.HIDE_FOR_OWN_TEAM)))).then(Commands.literal("always").executes(commandContext -> TeamCommand.setDeathMessageVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.Visibility.ALWAYS))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("collisionRule").then(Commands.literal("never").executes(commandContext -> TeamCommand.setCollision((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.CollisionRule.NEVER)))).then(Commands.literal("pushOwnTeam").executes(commandContext -> TeamCommand.setCollision((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.CollisionRule.PUSH_OWN_TEAM)))).then(Commands.literal("pushOtherTeams").executes(commandContext -> TeamCommand.setCollision((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.CollisionRule.PUSH_OTHER_TEAMS)))).then(Commands.literal("always").executes(commandContext -> TeamCommand.setCollision((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), Team.CollisionRule.ALWAYS))))).then(Commands.literal("prefix").then(Commands.argument("prefix", ComponentArgument.textComponent()).executes(commandContext -> TeamCommand.setPrefix((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "prefix")))))).then(Commands.literal("suffix").then(Commands.argument("suffix", ComponentArgument.textComponent()).executes(commandContext -> TeamCommand.setSuffix((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam((CommandContext<CommandSourceStack>)commandContext, "team"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "suffix"))))))));
    }

    private static int leaveTeam(CommandSourceStack commandSourceStack, Collection<String> collection) {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        for (String string : collection) {
            serverScoreboard.removePlayerFromTeam(string);
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.leave.success.single", collection.iterator().next()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.leave.success.multiple", collection.size()), true);
        }
        return collection.size();
    }

    private static int joinTeam(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Collection<String> collection) {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        for (String string : collection) {
            ((Scoreboard)serverScoreboard).addPlayerToTeam(string, playerTeam);
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.join.success.single", collection.iterator().next(), playerTeam.getFormattedDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.join.success.multiple", collection.size(), playerTeam.getFormattedDisplayName()), true);
        }
        return collection.size();
    }

    private static int setNametagVisibility(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Team.Visibility visibility) throws CommandSyntaxException {
        if (playerTeam.getNameTagVisibility() == visibility) {
            throw ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED.create();
        }
        playerTeam.setNameTagVisibility(visibility);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.nametagVisibility.success", playerTeam.getFormattedDisplayName(), visibility.getDisplayName()), true);
        return 0;
    }

    private static int setDeathMessageVisibility(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Team.Visibility visibility) throws CommandSyntaxException {
        if (playerTeam.getDeathMessageVisibility() == visibility) {
            throw ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED.create();
        }
        playerTeam.setDeathMessageVisibility(visibility);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.deathMessageVisibility.success", playerTeam.getFormattedDisplayName(), visibility.getDisplayName()), true);
        return 0;
    }

    private static int setCollision(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Team.CollisionRule collisionRule) throws CommandSyntaxException {
        if (playerTeam.getCollisionRule() == collisionRule) {
            throw ERROR_TEAM_COLLISION_UNCHANGED.create();
        }
        playerTeam.setCollisionRule(collisionRule);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.collisionRule.success", playerTeam.getFormattedDisplayName(), collisionRule.getDisplayName()), true);
        return 0;
    }

    private static int setFriendlySight(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, boolean bl) throws CommandSyntaxException {
        if (playerTeam.canSeeFriendlyInvisibles() == bl) {
            if (bl) {
                throw ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED.create();
            }
            throw ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED.create();
        }
        playerTeam.setSeeFriendlyInvisibles(bl);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.seeFriendlyInvisibles." + (bl ? "enabled" : "disabled"), playerTeam.getFormattedDisplayName()), true);
        return 0;
    }

    private static int setFriendlyFire(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, boolean bl) throws CommandSyntaxException {
        if (playerTeam.isAllowFriendlyFire() == bl) {
            if (bl) {
                throw ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED.create();
            }
            throw ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED.create();
        }
        playerTeam.setAllowFriendlyFire(bl);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.friendlyfire." + (bl ? "enabled" : "disabled"), playerTeam.getFormattedDisplayName()), true);
        return 0;
    }

    private static int setDisplayName(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Component component) throws CommandSyntaxException {
        if (playerTeam.getDisplayName().equals(component)) {
            throw ERROR_TEAM_ALREADY_NAME.create();
        }
        playerTeam.setDisplayName(component);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.name.success", playerTeam.getFormattedDisplayName()), true);
        return 0;
    }

    private static int setColor(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, ChatFormatting chatFormatting) throws CommandSyntaxException {
        if (playerTeam.getColor() == chatFormatting) {
            throw ERROR_TEAM_ALREADY_COLOR.create();
        }
        playerTeam.setColor(chatFormatting);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.color.success", playerTeam.getFormattedDisplayName(), chatFormatting.getName()), true);
        return 0;
    }

    private static int emptyTeam(CommandSourceStack commandSourceStack, PlayerTeam playerTeam) throws CommandSyntaxException {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        ArrayList arrayList = Lists.newArrayList(playerTeam.getPlayers());
        if (arrayList.isEmpty()) {
            throw ERROR_TEAM_ALREADY_EMPTY.create();
        }
        for (String string : arrayList) {
            ((Scoreboard)serverScoreboard).removePlayerFromTeam(string, playerTeam);
        }
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.empty.success", arrayList.size(), playerTeam.getFormattedDisplayName()), true);
        return arrayList.size();
    }

    private static int deleteTeam(CommandSourceStack commandSourceStack, PlayerTeam playerTeam) {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        serverScoreboard.removePlayerTeam(playerTeam);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.remove.success", playerTeam.getFormattedDisplayName()), true);
        return serverScoreboard.getPlayerTeams().size();
    }

    private static int createTeam(CommandSourceStack commandSourceStack, String string) throws CommandSyntaxException {
        return TeamCommand.createTeam(commandSourceStack, string, new TextComponent(string));
    }

    private static int createTeam(CommandSourceStack commandSourceStack, String string, Component component) throws CommandSyntaxException {
        ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
        if (serverScoreboard.getPlayerTeam(string) != null) {
            throw ERROR_TEAM_ALREADY_EXISTS.create();
        }
        if (string.length() > 16) {
            throw ERROR_TEAM_NAME_TOO_LONG.create((Object)16);
        }
        PlayerTeam playerTeam = serverScoreboard.addPlayerTeam(string);
        playerTeam.setDisplayName(component);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.add.success", playerTeam.getFormattedDisplayName()), true);
        return serverScoreboard.getPlayerTeams().size();
    }

    private static int listMembers(CommandSourceStack commandSourceStack, PlayerTeam playerTeam) {
        Collection<String> collection = playerTeam.getPlayers();
        if (collection.isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.list.members.empty", playerTeam.getFormattedDisplayName()), false);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.list.members.success", playerTeam.getFormattedDisplayName(), collection.size(), ComponentUtils.formatList(collection)), false);
        }
        return collection.size();
    }

    private static int listTeams(CommandSourceStack commandSourceStack) {
        Collection<PlayerTeam> collection = commandSourceStack.getServer().getScoreboard().getPlayerTeams();
        if (collection.isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.list.teams.empty"), false);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.list.teams.success", collection.size(), ComponentUtils.formatList(collection, PlayerTeam::getFormattedDisplayName)), false);
        }
        return collection.size();
    }

    private static int setPrefix(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Component component) {
        playerTeam.setPlayerPrefix(component);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.prefix.success", component), false);
        return 1;
    }

    private static int setSuffix(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Component component) {
        playerTeam.setPlayerSuffix(component);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.suffix.success", component), false);
        return 1;
    }
}

