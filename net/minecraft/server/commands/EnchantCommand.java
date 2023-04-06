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
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType$Function
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
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
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantCommand {
    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.enchant.failed.entity", object));
    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.enchant.failed.itemless", object));
    private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.enchant.failed.incompatible", object));
    private static final Dynamic2CommandExceptionType ERROR_LEVEL_TOO_HIGH = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableComponent("commands.enchant.failed.level", object, object2));
    private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.enchant.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("enchant").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("enchantment", ItemEnchantmentArgument.enchantment()).executes(commandContext -> EnchantCommand.enchant((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), ItemEnchantmentArgument.getEnchantment((CommandContext<CommandSourceStack>)commandContext, "enchantment"), 1))).then(Commands.argument("level", IntegerArgumentType.integer((int)0)).executes(commandContext -> EnchantCommand.enchant((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), ItemEnchantmentArgument.getEnchantment((CommandContext<CommandSourceStack>)commandContext, "enchantment"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"level")))))));
    }

    private static int enchant(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, Enchantment enchantment, int n) throws CommandSyntaxException {
        if (n > enchantment.getMaxLevel()) {
            throw ERROR_LEVEL_TOO_HIGH.create((Object)n, (Object)enchantment.getMaxLevel());
        }
        int n2 = 0;
        for (Entity entity : collection) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                ItemStack itemStack = livingEntity.getMainHandItem();
                if (!itemStack.isEmpty()) {
                    if (enchantment.canEnchant(itemStack) && EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantments(itemStack).keySet(), enchantment)) {
                        itemStack.enchant(enchantment, n);
                        ++n2;
                        continue;
                    }
                    if (collection.size() != 1) continue;
                    throw ERROR_INCOMPATIBLE.create((Object)itemStack.getItem().getName(itemStack).getString());
                }
                if (collection.size() != 1) continue;
                throw ERROR_NO_ITEM.create((Object)livingEntity.getName().getString());
            }
            if (collection.size() != 1) continue;
            throw ERROR_NOT_LIVING_ENTITY.create((Object)entity.getName().getString());
        }
        if (n2 == 0) {
            throw ERROR_NOTHING_HAPPENED.create();
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.enchant.success.single", enchantment.getFullname(n), collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.enchant.success.multiple", enchantment.getFullname(n), collection.size()), true);
        }
        return n2;
    }
}

