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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SetSpawnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawnpoint").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).executes(commandContext -> SetSpawnCommand.setSpawn((CommandSourceStack)commandContext.getSource(), Collections.singleton(((CommandSourceStack)commandContext.getSource()).getPlayerOrException()), new BlockPos(((CommandSourceStack)commandContext.getSource()).getPosition()), 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes(commandContext -> SetSpawnCommand.setSpawn((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), new BlockPos(((CommandSourceStack)commandContext.getSource()).getPosition()), 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes(commandContext -> SetSpawnCommand.setSpawn((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), BlockPosArgument.getOrLoadBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), 0.0f))).then(Commands.argument("angle", AngleArgument.angle()).executes(commandContext -> SetSpawnCommand.setSpawn((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), BlockPosArgument.getOrLoadBlockPos((CommandContext<CommandSourceStack>)commandContext, "pos"), AngleArgument.getAngle((CommandContext<CommandSourceStack>)commandContext, "angle")))))));
    }

    private static int setSpawn(CommandSourceStack commandSourceStack, Collection<ServerPlayer> collection, BlockPos blockPos, float f) {
        ResourceKey<Level> resourceKey = commandSourceStack.getLevel().dimension();
        for (ServerPlayer serverPlayer : collection) {
            serverPlayer.setRespawnPosition(resourceKey, blockPos, f, true, false);
        }
        String string = resourceKey.location().toString();
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.spawnpoint.success.single", blockPos.getX(), blockPos.getY(), blockPos.getZ(), Float.valueOf(f), string, collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.spawnpoint.success.multiple", blockPos.getX(), blockPos.getY(), blockPos.getZ(), Float.valueOf(f), string, collection.size()), true);
        }
        return collection.size();
    }
}

