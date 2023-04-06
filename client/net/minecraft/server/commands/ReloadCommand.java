/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.storage.WorldData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadCommand {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void reloadPacks(Collection<String> collection, CommandSourceStack commandSourceStack) {
        commandSourceStack.getServer().reloadResources(collection).exceptionally(throwable -> {
            LOGGER.warn("Failed to execute reload", throwable);
            commandSourceStack.sendFailure(new TranslatableComponent("commands.reload.failure"));
            return null;
        });
    }

    private static Collection<String> discoverNewPacks(PackRepository packRepository, WorldData worldData, Collection<String> collection) {
        packRepository.reload();
        ArrayList arrayList = Lists.newArrayList(collection);
        List<String> list = worldData.getDataPackConfig().getDisabled();
        for (String string : packRepository.getAvailableIds()) {
            if (list.contains(string) || arrayList.contains(string)) continue;
            arrayList.add(string);
        }
        return arrayList;
    }

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reload").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).executes(commandContext -> {
            CommandSourceStack commandSourceStack = (CommandSourceStack)commandContext.getSource();
            MinecraftServer minecraftServer = commandSourceStack.getServer();
            PackRepository packRepository = minecraftServer.getPackRepository();
            WorldData worldData = minecraftServer.getWorldData();
            Collection<String> collection = packRepository.getSelectedIds();
            Collection<String> collection2 = ReloadCommand.discoverNewPacks(packRepository, worldData, collection);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.reload.success"), true);
            ReloadCommand.reloadPacks(collection2, commandSourceStack);
            return 0;
        }));
    }
}

