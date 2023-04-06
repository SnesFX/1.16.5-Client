/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Locale;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec2;

public class WorldBorderCommand {
    private static final SimpleCommandExceptionType ERROR_SAME_CENTER = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.worldborder.center.failed"));
    private static final SimpleCommandExceptionType ERROR_SAME_SIZE = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.worldborder.set.failed.nochange"));
    private static final SimpleCommandExceptionType ERROR_TOO_SMALL = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.worldborder.set.failed.small."));
    private static final SimpleCommandExceptionType ERROR_TOO_BIG = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.worldborder.set.failed.big."));
    private static final SimpleCommandExceptionType ERROR_SAME_WARNING_TIME = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.worldborder.warning.time.failed"));
    private static final SimpleCommandExceptionType ERROR_SAME_WARNING_DISTANCE = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.worldborder.warning.distance.failed"));
    private static final SimpleCommandExceptionType ERROR_SAME_DAMAGE_BUFFER = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.worldborder.damage.buffer.failed"));
    private static final SimpleCommandExceptionType ERROR_SAME_DAMAGE_AMOUNT = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.worldborder.damage.amount.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("worldborder").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("distance", FloatArgumentType.floatArg((float)-6.0E7f, (float)6.0E7f)).executes(commandContext -> WorldBorderCommand.setSize((CommandSourceStack)commandContext.getSource(), ((CommandSourceStack)commandContext.getSource()).getLevel().getWorldBorder().getSize() + (double)FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"), 0L))).then(Commands.argument("time", IntegerArgumentType.integer((int)0)).executes(commandContext -> WorldBorderCommand.setSize((CommandSourceStack)commandContext.getSource(), ((CommandSourceStack)commandContext.getSource()).getLevel().getWorldBorder().getSize() + (double)FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"), ((CommandSourceStack)commandContext.getSource()).getLevel().getWorldBorder().getLerpRemainingTime() + (long)IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time") * 1000L)))))).then(Commands.literal("set").then(((RequiredArgumentBuilder)Commands.argument("distance", FloatArgumentType.floatArg((float)-6.0E7f, (float)6.0E7f)).executes(commandContext -> WorldBorderCommand.setSize((CommandSourceStack)commandContext.getSource(), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"), 0L))).then(Commands.argument("time", IntegerArgumentType.integer((int)0)).executes(commandContext -> WorldBorderCommand.setSize((CommandSourceStack)commandContext.getSource(), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"), (long)IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time") * 1000L)))))).then(Commands.literal("center").then(Commands.argument("pos", Vec2Argument.vec2()).executes(commandContext -> WorldBorderCommand.setCenter((CommandSourceStack)commandContext.getSource(), Vec2Argument.getVec2((CommandContext<CommandSourceStack>)commandContext, "pos")))))).then(((LiteralArgumentBuilder)Commands.literal("damage").then(Commands.literal("amount").then(Commands.argument("damagePerBlock", FloatArgumentType.floatArg((float)0.0f)).executes(commandContext -> WorldBorderCommand.setDamageAmount((CommandSourceStack)commandContext.getSource(), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"damagePerBlock")))))).then(Commands.literal("buffer").then(Commands.argument("distance", FloatArgumentType.floatArg((float)0.0f)).executes(commandContext -> WorldBorderCommand.setDamageBuffer((CommandSourceStack)commandContext.getSource(), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"distance"))))))).then(Commands.literal("get").executes(commandContext -> WorldBorderCommand.getSize((CommandSourceStack)commandContext.getSource())))).then(((LiteralArgumentBuilder)Commands.literal("warning").then(Commands.literal("distance").then(Commands.argument("distance", IntegerArgumentType.integer((int)0)).executes(commandContext -> WorldBorderCommand.setWarningDistance((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"distance")))))).then(Commands.literal("time").then(Commands.argument("time", IntegerArgumentType.integer((int)0)).executes(commandContext -> WorldBorderCommand.setWarningTime((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"time")))))));
    }

    private static int setDamageBuffer(CommandSourceStack commandSourceStack, float f) throws CommandSyntaxException {
        WorldBorder worldBorder = commandSourceStack.getLevel().getWorldBorder();
        if (worldBorder.getDamageSafeZone() == (double)f) {
            throw ERROR_SAME_DAMAGE_BUFFER.create();
        }
        worldBorder.setDamageSafeZone(f);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.damage.buffer.success", String.format(Locale.ROOT, "%.2f", Float.valueOf(f))), true);
        return (int)f;
    }

    private static int setDamageAmount(CommandSourceStack commandSourceStack, float f) throws CommandSyntaxException {
        WorldBorder worldBorder = commandSourceStack.getLevel().getWorldBorder();
        if (worldBorder.getDamagePerBlock() == (double)f) {
            throw ERROR_SAME_DAMAGE_AMOUNT.create();
        }
        worldBorder.setDamagePerBlock(f);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.damage.amount.success", String.format(Locale.ROOT, "%.2f", Float.valueOf(f))), true);
        return (int)f;
    }

    private static int setWarningTime(CommandSourceStack commandSourceStack, int n) throws CommandSyntaxException {
        WorldBorder worldBorder = commandSourceStack.getLevel().getWorldBorder();
        if (worldBorder.getWarningTime() == n) {
            throw ERROR_SAME_WARNING_TIME.create();
        }
        worldBorder.setWarningTime(n);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.warning.time.success", n), true);
        return n;
    }

    private static int setWarningDistance(CommandSourceStack commandSourceStack, int n) throws CommandSyntaxException {
        WorldBorder worldBorder = commandSourceStack.getLevel().getWorldBorder();
        if (worldBorder.getWarningBlocks() == n) {
            throw ERROR_SAME_WARNING_DISTANCE.create();
        }
        worldBorder.setWarningBlocks(n);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.warning.distance.success", n), true);
        return n;
    }

    private static int getSize(CommandSourceStack commandSourceStack) {
        double d = commandSourceStack.getLevel().getWorldBorder().getSize();
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.get", String.format(Locale.ROOT, "%.0f", d)), false);
        return Mth.floor(d + 0.5);
    }

    private static int setCenter(CommandSourceStack commandSourceStack, Vec2 vec2) throws CommandSyntaxException {
        WorldBorder worldBorder = commandSourceStack.getLevel().getWorldBorder();
        if (worldBorder.getCenterX() == (double)vec2.x && worldBorder.getCenterZ() == (double)vec2.y) {
            throw ERROR_SAME_CENTER.create();
        }
        worldBorder.setCenter(vec2.x, vec2.y);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.center.success", String.format(Locale.ROOT, "%.2f", Float.valueOf(vec2.x)), String.format("%.2f", Float.valueOf(vec2.y))), true);
        return 0;
    }

    private static int setSize(CommandSourceStack commandSourceStack, double d, long l) throws CommandSyntaxException {
        WorldBorder worldBorder = commandSourceStack.getLevel().getWorldBorder();
        double d2 = worldBorder.getSize();
        if (d2 == d) {
            throw ERROR_SAME_SIZE.create();
        }
        if (d < 1.0) {
            throw ERROR_TOO_SMALL.create();
        }
        if (d > 6.0E7) {
            throw ERROR_TOO_BIG.create();
        }
        if (l > 0L) {
            worldBorder.lerpSizeBetween(d2, d, l);
            if (d > d2) {
                commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.set.grow", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true);
            } else {
                commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.set.shrink", String.format(Locale.ROOT, "%.1f", d), Long.toString(l / 1000L)), true);
            }
        } else {
            worldBorder.setSize(d);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.set.immediate", String.format(Locale.ROOT, "%.1f", d)), true);
        }
        return (int)(d - d2);
    }
}

