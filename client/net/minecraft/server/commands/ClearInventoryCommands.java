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
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
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
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

public class ClearInventoryCommands {
    private static final DynamicCommandExceptionType ERROR_SINGLE = new DynamicCommandExceptionType(object -> new TranslatableComponent("clear.failed.single", object));
    private static final DynamicCommandExceptionType ERROR_MULTIPLE = new DynamicCommandExceptionType(object -> new TranslatableComponent("clear.failed.multiple", object));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clear").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).executes(commandContext -> ClearInventoryCommands.clearInventory((CommandSourceStack)commandContext.getSource(), Collections.singleton(((CommandSourceStack)commandContext.getSource()).getPlayerOrException()), itemStack -> true, -1))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes(commandContext -> ClearInventoryCommands.clearInventory((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), itemStack -> true, -1))).then(((RequiredArgumentBuilder)Commands.argument("item", ItemPredicateArgument.itemPredicate()).executes(commandContext -> ClearInventoryCommands.clearInventory((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ItemPredicateArgument.getItemPredicate((CommandContext<CommandSourceStack>)commandContext, "item"), -1))).then(Commands.argument("maxCount", IntegerArgumentType.integer((int)0)).executes(commandContext -> ClearInventoryCommands.clearInventory((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ItemPredicateArgument.getItemPredicate((CommandContext<CommandSourceStack>)commandContext, "item"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"maxCount")))))));
    }

    private static int clearInventory(CommandSourceStack commandSourceStack, Collection<ServerPlayer> collection, Predicate<ItemStack> predicate, int n) throws CommandSyntaxException {
        int n2 = 0;
        for (ServerPlayer serverPlayer : collection) {
            n2 += serverPlayer.inventory.clearOrCountMatchingItems(predicate, n, serverPlayer.inventoryMenu.getCraftSlots());
            serverPlayer.containerMenu.broadcastChanges();
            serverPlayer.inventoryMenu.slotsChanged(serverPlayer.inventory);
            serverPlayer.broadcastCarriedItem();
        }
        if (n2 == 0) {
            if (collection.size() == 1) {
                throw ERROR_SINGLE.create((Object)collection.iterator().next().getName());
            }
            throw ERROR_MULTIPLE.create((Object)collection.size());
        }
        if (n == 0) {
            if (collection.size() == 1) {
                commandSourceStack.sendSuccess(new TranslatableComponent("commands.clear.test.single", n2, collection.iterator().next().getDisplayName()), true);
            } else {
                commandSourceStack.sendSuccess(new TranslatableComponent("commands.clear.test.multiple", n2, collection.size()), true);
            }
        } else if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.clear.success.single", n2, collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.clear.success.multiple", n2, collection.size()), true);
        }
        return n2;
    }
}

