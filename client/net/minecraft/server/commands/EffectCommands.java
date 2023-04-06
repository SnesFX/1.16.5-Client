/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
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
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  javax.annotation.Nullable
 */
package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
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
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EffectCommands {
    private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.effect.give.failed"));
    private static final SimpleCommandExceptionType ERROR_CLEAR_EVERYTHING_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.effect.clear.everything.failed"));
    private static final SimpleCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.effect.clear.specific.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("effect").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(((LiteralArgumentBuilder)Commands.literal("clear").executes(commandContext -> EffectCommands.clearEffects((CommandSourceStack)commandContext.getSource(), (Collection<? extends Entity>)ImmutableList.of((Object)((CommandSourceStack)commandContext.getSource()).getEntityOrException())))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).executes(commandContext -> EffectCommands.clearEffects((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets")))).then(Commands.argument("effect", MobEffectArgument.effect()).executes(commandContext -> EffectCommands.clearEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), MobEffectArgument.getEffect((CommandContext<CommandSourceStack>)commandContext, "effect"))))))).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("effect", MobEffectArgument.effect()).executes(commandContext -> EffectCommands.giveEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), MobEffectArgument.getEffect((CommandContext<CommandSourceStack>)commandContext, "effect"), null, 0, true))).then(((RequiredArgumentBuilder)Commands.argument("seconds", IntegerArgumentType.integer((int)1, (int)1000000)).executes(commandContext -> EffectCommands.giveEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), MobEffectArgument.getEffect((CommandContext<CommandSourceStack>)commandContext, "effect"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"seconds"), 0, true))).then(((RequiredArgumentBuilder)Commands.argument("amplifier", IntegerArgumentType.integer((int)0, (int)255)).executes(commandContext -> EffectCommands.giveEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), MobEffectArgument.getEffect((CommandContext<CommandSourceStack>)commandContext, "effect"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"seconds"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amplifier"), true))).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes(commandContext -> EffectCommands.giveEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), MobEffectArgument.getEffect((CommandContext<CommandSourceStack>)commandContext, "effect"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"seconds"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"amplifier"), !BoolArgumentType.getBool((CommandContext)commandContext, (String)"hideParticles"))))))))));
    }

    private static int giveEffect(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, MobEffect mobEffect, @Nullable Integer n, int n2, boolean bl) throws CommandSyntaxException {
        int n3 = 0;
        int n4 = n != null ? (mobEffect.isInstantenous() ? n : n * 20) : (mobEffect.isInstantenous() ? 1 : 600);
        for (Entity entity : collection) {
            MobEffectInstance mobEffectInstance;
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).addEffect(mobEffectInstance = new MobEffectInstance(mobEffect, n4, n2, false, bl))) continue;
            ++n3;
        }
        if (n3 == 0) {
            throw ERROR_GIVE_FAILED.create();
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.effect.give.success.single", mobEffect.getDisplayName(), collection.iterator().next().getDisplayName(), n4 / 20), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.effect.give.success.multiple", mobEffect.getDisplayName(), collection.size(), n4 / 20), true);
        }
        return n3;
    }

    private static int clearEffects(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection) throws CommandSyntaxException {
        int n = 0;
        for (Entity entity : collection) {
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).removeAllEffects()) continue;
            ++n;
        }
        if (n == 0) {
            throw ERROR_CLEAR_EVERYTHING_FAILED.create();
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.effect.clear.everything.success.single", collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.effect.clear.everything.success.multiple", collection.size()), true);
        }
        return n;
    }

    private static int clearEffect(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, MobEffect mobEffect) throws CommandSyntaxException {
        int n = 0;
        for (Entity entity : collection) {
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).removeEffect(mobEffect)) continue;
            ++n;
        }
        if (n == 0) {
            throw ERROR_CLEAR_SPECIFIC_FAILED.create();
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.effect.clear.specific.success.single", mobEffect.getDisplayName(), collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.effect.clear.specific.success.multiple", mobEffect.getDisplayName(), collection.size()), true);
        }
        return n;
    }
}

