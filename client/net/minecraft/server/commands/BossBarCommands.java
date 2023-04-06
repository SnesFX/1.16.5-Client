/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
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
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.player.Player;

public class BossBarCommands {
    private static final DynamicCommandExceptionType ERROR_ALREADY_EXISTS = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.bossbar.create.failed", object));
    private static final DynamicCommandExceptionType ERROR_DOESNT_EXIST = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.bossbar.unknown", object));
    private static final SimpleCommandExceptionType ERROR_NO_PLAYER_CHANGE = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.bossbar.set.players.unchanged"));
    private static final SimpleCommandExceptionType ERROR_NO_NAME_CHANGE = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.bossbar.set.name.unchanged"));
    private static final SimpleCommandExceptionType ERROR_NO_COLOR_CHANGE = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.bossbar.set.color.unchanged"));
    private static final SimpleCommandExceptionType ERROR_NO_STYLE_CHANGE = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.bossbar.set.style.unchanged"));
    private static final SimpleCommandExceptionType ERROR_NO_VALUE_CHANGE = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.bossbar.set.value.unchanged"));
    private static final SimpleCommandExceptionType ERROR_NO_MAX_CHANGE = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.bossbar.set.max.unchanged"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_HIDDEN = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.bossbar.set.visibility.unchanged.hidden"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_VISIBLE = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.bossbar.set.visibility.unchanged.visible"));
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_BOSS_BAR = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggestResource(((CommandSourceStack)commandContext.getSource()).getServer().getCustomBossEvents().getIds(), suggestionsBuilder);

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("bossbar").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.literal("add").then(Commands.argument("id", ResourceLocationArgument.id()).then(Commands.argument("name", ComponentArgument.textComponent()).executes(commandContext -> BossBarCommands.createBar((CommandSourceStack)commandContext.getSource(), ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)commandContext, "id"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "name"))))))).then(Commands.literal("remove").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).executes(commandContext -> BossBarCommands.removeBar((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext)))))).then(Commands.literal("list").executes(commandContext -> BossBarCommands.listBars((CommandSourceStack)commandContext.getSource())))).then(Commands.literal("set").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).then(Commands.literal("name").then(Commands.argument("name", ComponentArgument.textComponent()).executes(commandContext -> BossBarCommands.setName((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "name")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("color").then(Commands.literal("pink").executes(commandContext -> BossBarCommands.setColor((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), BossEvent.BossBarColor.PINK)))).then(Commands.literal("blue").executes(commandContext -> BossBarCommands.setColor((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), BossEvent.BossBarColor.BLUE)))).then(Commands.literal("red").executes(commandContext -> BossBarCommands.setColor((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), BossEvent.BossBarColor.RED)))).then(Commands.literal("green").executes(commandContext -> BossBarCommands.setColor((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), BossEvent.BossBarColor.GREEN)))).then(Commands.literal("yellow").executes(commandContext -> BossBarCommands.setColor((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), BossEvent.BossBarColor.YELLOW)))).then(Commands.literal("purple").executes(commandContext -> BossBarCommands.setColor((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), BossEvent.BossBarColor.PURPLE)))).then(Commands.literal("white").executes(commandContext -> BossBarCommands.setColor((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), BossEvent.BossBarColor.WHITE))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("style").then(Commands.literal("progress").executes(commandContext -> BossBarCommands.setStyle((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), BossEvent.BossBarOverlay.PROGRESS)))).then(Commands.literal("notched_6").executes(commandContext -> BossBarCommands.setStyle((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), BossEvent.BossBarOverlay.NOTCHED_6)))).then(Commands.literal("notched_10").executes(commandContext -> BossBarCommands.setStyle((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), BossEvent.BossBarOverlay.NOTCHED_10)))).then(Commands.literal("notched_12").executes(commandContext -> BossBarCommands.setStyle((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), BossEvent.BossBarOverlay.NOTCHED_12)))).then(Commands.literal("notched_20").executes(commandContext -> BossBarCommands.setStyle((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), BossEvent.BossBarOverlay.NOTCHED_20))))).then(Commands.literal("value").then(Commands.argument("value", IntegerArgumentType.integer((int)0)).executes(commandContext -> BossBarCommands.setValue((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"value")))))).then(Commands.literal("max").then(Commands.argument("max", IntegerArgumentType.integer((int)1)).executes(commandContext -> BossBarCommands.setMax((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"max")))))).then(Commands.literal("visible").then(Commands.argument("visible", BoolArgumentType.bool()).executes(commandContext -> BossBarCommands.setVisible((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), BoolArgumentType.getBool((CommandContext)commandContext, (String)"visible")))))).then(((LiteralArgumentBuilder)Commands.literal("players").executes(commandContext -> BossBarCommands.setPlayers((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), Collections.emptyList()))).then(Commands.argument("targets", EntityArgument.players()).executes(commandContext -> BossBarCommands.setPlayers((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext), EntityArgument.getOptionalPlayers((CommandContext<CommandSourceStack>)commandContext, "targets")))))))).then(Commands.literal("get").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).then(Commands.literal("value").executes(commandContext -> BossBarCommands.getValue((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext))))).then(Commands.literal("max").executes(commandContext -> BossBarCommands.getMax((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext))))).then(Commands.literal("visible").executes(commandContext -> BossBarCommands.getVisible((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext))))).then(Commands.literal("players").executes(commandContext -> BossBarCommands.getPlayers((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)commandContext)))))));
    }

    private static int getValue(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent) {
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.get.value", customBossEvent.getDisplayName(), customBossEvent.getValue()), true);
        return customBossEvent.getValue();
    }

    private static int getMax(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent) {
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.get.max", customBossEvent.getDisplayName(), customBossEvent.getMax()), true);
        return customBossEvent.getMax();
    }

    private static int getVisible(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent) {
        if (customBossEvent.isVisible()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.get.visible.visible", customBossEvent.getDisplayName()), true);
            return 1;
        }
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.get.visible.hidden", customBossEvent.getDisplayName()), true);
        return 0;
    }

    private static int getPlayers(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent) {
        if (customBossEvent.getPlayers().isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.get.players.none", customBossEvent.getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.get.players.some", customBossEvent.getDisplayName(), customBossEvent.getPlayers().size(), ComponentUtils.formatList(customBossEvent.getPlayers(), Player::getDisplayName)), true);
        }
        return customBossEvent.getPlayers().size();
    }

    private static int setVisible(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, boolean bl) throws CommandSyntaxException {
        if (customBossEvent.isVisible() == bl) {
            if (bl) {
                throw ERROR_ALREADY_VISIBLE.create();
            }
            throw ERROR_ALREADY_HIDDEN.create();
        }
        customBossEvent.setVisible(bl);
        if (bl) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.visible.success.visible", customBossEvent.getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.visible.success.hidden", customBossEvent.getDisplayName()), true);
        }
        return 0;
    }

    private static int setValue(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, int n) throws CommandSyntaxException {
        if (customBossEvent.getValue() == n) {
            throw ERROR_NO_VALUE_CHANGE.create();
        }
        customBossEvent.setValue(n);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.value.success", customBossEvent.getDisplayName(), n), true);
        return n;
    }

    private static int setMax(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, int n) throws CommandSyntaxException {
        if (customBossEvent.getMax() == n) {
            throw ERROR_NO_MAX_CHANGE.create();
        }
        customBossEvent.setMax(n);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.max.success", customBossEvent.getDisplayName(), n), true);
        return n;
    }

    private static int setColor(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, BossEvent.BossBarColor bossBarColor) throws CommandSyntaxException {
        if (customBossEvent.getColor().equals((Object)bossBarColor)) {
            throw ERROR_NO_COLOR_CHANGE.create();
        }
        customBossEvent.setColor(bossBarColor);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.color.success", customBossEvent.getDisplayName()), true);
        return 0;
    }

    private static int setStyle(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, BossEvent.BossBarOverlay bossBarOverlay) throws CommandSyntaxException {
        if (customBossEvent.getOverlay().equals((Object)bossBarOverlay)) {
            throw ERROR_NO_STYLE_CHANGE.create();
        }
        customBossEvent.setOverlay(bossBarOverlay);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.style.success", customBossEvent.getDisplayName()), true);
        return 0;
    }

    private static int setName(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, Component component) throws CommandSyntaxException {
        MutableComponent mutableComponent = ComponentUtils.updateForEntity(commandSourceStack, component, null, 0);
        if (customBossEvent.getName().equals(mutableComponent)) {
            throw ERROR_NO_NAME_CHANGE.create();
        }
        customBossEvent.setName(mutableComponent);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.name.success", customBossEvent.getDisplayName()), true);
        return 0;
    }

    private static int setPlayers(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, Collection<ServerPlayer> collection) throws CommandSyntaxException {
        boolean bl = customBossEvent.setPlayers(collection);
        if (!bl) {
            throw ERROR_NO_PLAYER_CHANGE.create();
        }
        if (customBossEvent.getPlayers().isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.players.success.none", customBossEvent.getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.players.success.some", customBossEvent.getDisplayName(), collection.size(), ComponentUtils.formatList(collection, Player::getDisplayName)), true);
        }
        return customBossEvent.getPlayers().size();
    }

    private static int listBars(CommandSourceStack commandSourceStack) {
        Collection<CustomBossEvent> collection = commandSourceStack.getServer().getCustomBossEvents().getEvents();
        if (collection.isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.list.bars.none"), false);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.list.bars.some", collection.size(), ComponentUtils.formatList(collection, CustomBossEvent::getDisplayName)), false);
        }
        return collection.size();
    }

    private static int createBar(CommandSourceStack commandSourceStack, ResourceLocation resourceLocation, Component component) throws CommandSyntaxException {
        CustomBossEvents customBossEvents = commandSourceStack.getServer().getCustomBossEvents();
        if (customBossEvents.get(resourceLocation) != null) {
            throw ERROR_ALREADY_EXISTS.create((Object)resourceLocation.toString());
        }
        CustomBossEvent customBossEvent = customBossEvents.create(resourceLocation, ComponentUtils.updateForEntity(commandSourceStack, component, null, 0));
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.create.success", customBossEvent.getDisplayName()), true);
        return customBossEvents.getEvents().size();
    }

    private static int removeBar(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent) {
        CustomBossEvents customBossEvents = commandSourceStack.getServer().getCustomBossEvents();
        customBossEvent.removeAllPlayers();
        customBossEvents.remove(customBossEvent);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.remove.success", customBossEvent.getDisplayName()), true);
        return customBossEvents.getEvents().size();
    }

    public static CustomBossEvent getBossBar(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        ResourceLocation resourceLocation = ResourceLocationArgument.getId(commandContext, "id");
        CustomBossEvent customBossEvent = ((CommandSourceStack)commandContext.getSource()).getServer().getCustomBossEvents().get(resourceLocation);
        if (customBossEvent == null) {
            throw ERROR_DOESNT_EXIST.create((Object)resourceLocation.toString());
        }
        return customBossEvent;
    }
}

