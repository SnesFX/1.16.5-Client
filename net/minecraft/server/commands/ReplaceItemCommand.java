/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType$Function
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ReplaceItemCommand {
    public static final SimpleCommandExceptionType ERROR_NOT_A_CONTAINER = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.replaceitem.block.failed"));
    public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_SLOT = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.replaceitem.slot.inapplicable", object));
    public static final Dynamic2CommandExceptionType ERROR_ENTITY_SLOT = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableComponent("commands.replaceitem.entity.failed", object, object2));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("replaceitem").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(Commands.argument("slot", SlotArgument.slot()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item()).executes(commandContext -> ReplaceItemCommand.setBlockItem((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)commandContext, "slot"), ItemArgument.getItem(commandContext, "item").createItemStack(1, false)))).then(Commands.argument("count", IntegerArgumentType.integer((int)1, (int)64)).executes(commandContext -> ReplaceItemCommand.setBlockItem((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)commandContext, "slot"), ItemArgument.getItem(commandContext, "item").createItemStack(IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), true))))))))).then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("slot", SlotArgument.slot()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item()).executes(commandContext -> ReplaceItemCommand.setEntityItem((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)commandContext, "slot"), ItemArgument.getItem(commandContext, "item").createItemStack(1, false)))).then(Commands.argument("count", IntegerArgumentType.integer((int)1, (int)64)).executes(commandContext -> ReplaceItemCommand.setEntityItem((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), SlotArgument.getSlot((CommandContext<CommandSourceStack>)commandContext, "slot"), ItemArgument.getItem(commandContext, "item").createItemStack(IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), true)))))))));
    }

    private static int setBlockItem(CommandSourceStack commandSourceStack, BlockPos blockPos, int n, ItemStack itemStack) throws CommandSyntaxException {
        BlockEntity blockEntity = commandSourceStack.getLevel().getBlockEntity(blockPos);
        if (!(blockEntity instanceof Container)) {
            throw ERROR_NOT_A_CONTAINER.create();
        }
        Container container = (Container)((Object)blockEntity);
        if (n < 0 || n >= container.getContainerSize()) {
            throw ERROR_INAPPLICABLE_SLOT.create((Object)n);
        }
        container.setItem(n, itemStack);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.replaceitem.block.success", blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack.getDisplayName()), true);
        return 1;
    }

    private static int setEntityItem(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, int n, ItemStack itemStack) throws CommandSyntaxException {
        ArrayList arrayList = Lists.newArrayListWithCapacity((int)collection.size());
        for (Entity entity : collection) {
            if (entity instanceof ServerPlayer) {
                ((ServerPlayer)entity).inventoryMenu.broadcastChanges();
            }
            if (!entity.setSlot(n, itemStack.copy())) continue;
            arrayList.add(entity);
            if (!(entity instanceof ServerPlayer)) continue;
            ((ServerPlayer)entity).inventoryMenu.broadcastChanges();
        }
        if (arrayList.isEmpty()) {
            throw ERROR_ENTITY_SLOT.create((Object)itemStack.getDisplayName(), (Object)n);
        }
        if (arrayList.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.replaceitem.entity.success.single", ((Entity)arrayList.iterator().next()).getDisplayName(), itemStack.getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.replaceitem.entity.success.multiple", arrayList.size(), itemStack.getDisplayName()), true);
        }
        return arrayList.size();
    }
}

