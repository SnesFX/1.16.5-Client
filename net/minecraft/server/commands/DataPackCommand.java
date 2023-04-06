/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;

public class DataPackCommand {
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_PACK = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.datapack.unknown", object));
    private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_ENABLED = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.datapack.enable.failed", object));
    private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_DISABLED = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.datapack.disable.failed", object));
    private static final SuggestionProvider<CommandSourceStack> SELECTED_PACKS = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(((CommandSourceStack)commandContext.getSource()).getServer().getPackRepository().getSelectedIds().stream().map(StringArgumentType::escapeIfRequired), suggestionsBuilder);
    private static final SuggestionProvider<CommandSourceStack> UNSELECTED_PACKS = (commandContext, suggestionsBuilder) -> {
        PackRepository packRepository = ((CommandSourceStack)commandContext.getSource()).getServer().getPackRepository();
        Collection<String> collection = packRepository.getSelectedIds();
        return SharedSuggestionProvider.suggest(packRepository.getAvailableIds().stream().filter(string -> !collection.contains(string)).map(StringArgumentType::escapeIfRequired), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("datapack").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.literal("enable").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("name", StringArgumentType.string()).suggests(UNSELECTED_PACKS).executes(commandContext -> DataPackCommand.enablePack((CommandSourceStack)commandContext.getSource(), DataPackCommand.getPack((CommandContext<CommandSourceStack>)commandContext, "name", true), (list, pack2) -> pack2.getDefaultPosition().insert(list, pack2, pack -> pack, false)))).then(Commands.literal("after").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes(commandContext -> DataPackCommand.enablePack((CommandSourceStack)commandContext.getSource(), DataPackCommand.getPack((CommandContext<CommandSourceStack>)commandContext, "name", true), (list, pack) -> list.add(list.indexOf(DataPackCommand.getPack((CommandContext<CommandSourceStack>)commandContext, "existing", false)) + 1, pack)))))).then(Commands.literal("before").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes(commandContext -> DataPackCommand.enablePack((CommandSourceStack)commandContext.getSource(), DataPackCommand.getPack((CommandContext<CommandSourceStack>)commandContext, "name", true), (list, pack) -> list.add(list.indexOf(DataPackCommand.getPack((CommandContext<CommandSourceStack>)commandContext, "existing", false)), pack)))))).then(Commands.literal("last").executes(commandContext -> DataPackCommand.enablePack((CommandSourceStack)commandContext.getSource(), DataPackCommand.getPack((CommandContext<CommandSourceStack>)commandContext, "name", true), List::add)))).then(Commands.literal("first").executes(commandContext -> DataPackCommand.enablePack((CommandSourceStack)commandContext.getSource(), DataPackCommand.getPack((CommandContext<CommandSourceStack>)commandContext, "name", true), (list, pack) -> list.add(0, pack))))))).then(Commands.literal("disable").then(Commands.argument("name", StringArgumentType.string()).suggests(SELECTED_PACKS).executes(commandContext -> DataPackCommand.disablePack((CommandSourceStack)commandContext.getSource(), DataPackCommand.getPack((CommandContext<CommandSourceStack>)commandContext, "name", false)))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").executes(commandContext -> DataPackCommand.listPacks((CommandSourceStack)commandContext.getSource()))).then(Commands.literal("available").executes(commandContext -> DataPackCommand.listAvailablePacks((CommandSourceStack)commandContext.getSource())))).then(Commands.literal("enabled").executes(commandContext -> DataPackCommand.listEnabledPacks((CommandSourceStack)commandContext.getSource())))));
    }

    private static int enablePack(CommandSourceStack commandSourceStack, Pack pack, Inserter inserter) throws CommandSyntaxException {
        PackRepository packRepository = commandSourceStack.getServer().getPackRepository();
        ArrayList arrayList = Lists.newArrayList(packRepository.getSelectedPacks());
        inserter.apply(arrayList, pack);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.datapack.modify.enable", pack.getChatLink(true)), true);
        ReloadCommand.reloadPacks(arrayList.stream().map(Pack::getId).collect(Collectors.toList()), commandSourceStack);
        return arrayList.size();
    }

    private static int disablePack(CommandSourceStack commandSourceStack, Pack pack) {
        PackRepository packRepository = commandSourceStack.getServer().getPackRepository();
        ArrayList arrayList = Lists.newArrayList(packRepository.getSelectedPacks());
        arrayList.remove(pack);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.datapack.modify.disable", pack.getChatLink(true)), true);
        ReloadCommand.reloadPacks(arrayList.stream().map(Pack::getId).collect(Collectors.toList()), commandSourceStack);
        return arrayList.size();
    }

    private static int listPacks(CommandSourceStack commandSourceStack) {
        return DataPackCommand.listEnabledPacks(commandSourceStack) + DataPackCommand.listAvailablePacks(commandSourceStack);
    }

    private static int listAvailablePacks(CommandSourceStack commandSourceStack) {
        PackRepository packRepository = commandSourceStack.getServer().getPackRepository();
        packRepository.reload();
        Collection<Pack> collection = packRepository.getSelectedPacks();
        Collection<Pack> collection2 = packRepository.getAvailablePacks();
        List list = collection2.stream().filter(pack -> !collection.contains(pack)).collect(Collectors.toList());
        if (list.isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.datapack.list.available.none"), false);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.datapack.list.available.success", list.size(), ComponentUtils.formatList(list, pack -> pack.getChatLink(false))), false);
        }
        return list.size();
    }

    private static int listEnabledPacks(CommandSourceStack commandSourceStack) {
        PackRepository packRepository = commandSourceStack.getServer().getPackRepository();
        packRepository.reload();
        Collection<Pack> collection = packRepository.getSelectedPacks();
        if (collection.isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.datapack.list.enabled.none"), false);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.datapack.list.enabled.success", collection.size(), ComponentUtils.formatList(collection, pack -> pack.getChatLink(true))), false);
        }
        return collection.size();
    }

    private static Pack getPack(CommandContext<CommandSourceStack> commandContext, String string, boolean bl) throws CommandSyntaxException {
        String string2 = StringArgumentType.getString(commandContext, (String)string);
        PackRepository packRepository = ((CommandSourceStack)commandContext.getSource()).getServer().getPackRepository();
        Pack pack = packRepository.getPack(string2);
        if (pack == null) {
            throw ERROR_UNKNOWN_PACK.create((Object)string2);
        }
        boolean bl2 = packRepository.getSelectedPacks().contains(pack);
        if (bl && bl2) {
            throw ERROR_PACK_ALREADY_ENABLED.create((Object)string2);
        }
        if (!bl && !bl2) {
            throw ERROR_PACK_ALREADY_DISABLED.create((Object)string2);
        }
        return pack;
    }

    static interface Inserter {
        public void apply(List<Pack> var1, Pack var2) throws CommandSyntaxException;
    }

}

