/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
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
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class SetWorldSpawnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setworldspawn").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).executes(commandContext -> SetWorldSpawnCommand.setSpawn((CommandSourceStack)commandContext.getSource(), new BlockPos(((CommandSourceStack)commandContext.getSource()).getPosition()), 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes(commandContext -> SetWorldSpawnCommand.setSpawn((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getOrLoadBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), 0.0f))).then(Commands.argument("angle", AngleArgument.angle()).executes(commandContext -> SetWorldSpawnCommand.setSpawn((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getOrLoadBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), AngleArgument.getAngle((CommandContext<CommandSourceStack>)commandContext, "angle"))))));
    }

    private static int setSpawn(CommandSourceStack commandSourceStack, BlockPos blockPos, float f) {
        commandSourceStack.getLevel().setDefaultSpawnPos(blockPos, f);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.setworldspawn.success", blockPos.getX(), blockPos.getY(), blockPos.getZ(), Float.valueOf(f)), true);
        return 1;
    }
}

