/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
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
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GiveCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("give").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item()).executes(commandContext -> GiveCommand.giveItem((CommandSourceStack)commandContext.getSource(), ItemArgument.getItem(commandContext, "item"), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), 1))).then(Commands.argument("count", IntegerArgumentType.integer((int)1)).executes(commandContext -> GiveCommand.giveItem((CommandSourceStack)commandContext.getSource(), ItemArgument.getItem(commandContext, "item"), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count")))))));
    }

    private static int giveItem(CommandSourceStack commandSourceStack, ItemInput itemInput, Collection<ServerPlayer> collection, int n) throws CommandSyntaxException {
        for (ServerPlayer serverPlayer : collection) {
            int n2 = n;
            while (n2 > 0) {
                ItemEntity itemEntity;
                int n3 = Math.min(itemInput.getItem().getMaxStackSize(), n2);
                n2 -= n3;
                ItemStack itemStack = itemInput.createItemStack(n3, false);
                boolean bl = serverPlayer.inventory.add(itemStack);
                if (!bl || !itemStack.isEmpty()) {
                    itemEntity = serverPlayer.drop(itemStack, false);
                    if (itemEntity == null) continue;
                    itemEntity.setNoPickUpDelay();
                    itemEntity.setOwner(serverPlayer.getUUID());
                    continue;
                }
                itemStack.setCount(1);
                itemEntity = serverPlayer.drop(itemStack, false);
                if (itemEntity != null) {
                    itemEntity.makeFakeItem();
                }
                serverPlayer.level.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                serverPlayer.inventoryMenu.broadcastChanges();
            }
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.give.success.single", n, itemInput.createItemStack(n, false).getDisplayName(), collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.give.success.single", n, itemInput.createItemStack(n, false).getDisplayName(), collection.size()), true);
        }
        return collection.size();
    }
}

