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
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.phys.Vec3;

public class ParticleCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.particle.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("particle").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(((RequiredArgumentBuilder)Commands.argument("name", ParticleArgument.particle()).executes(commandContext -> ParticleCommand.sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle((CommandContext<CommandSourceStack>)commandContext, "name"), ((CommandSourceStack)commandContext.getSource()).getPosition(), Vec3.ZERO, 0.0f, 0, false, ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getPlayers()))).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes(commandContext -> ParticleCommand.sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle((CommandContext<CommandSourceStack>)commandContext, "name"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), Vec3.ZERO, 0.0f, 0, false, ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getPlayers()))).then(Commands.argument("delta", Vec3Argument.vec3(false)).then(Commands.argument("speed", FloatArgumentType.floatArg((float)0.0f)).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("count", IntegerArgumentType.integer((int)0)).executes(commandContext -> ParticleCommand.sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle((CommandContext<CommandSourceStack>)commandContext, "name"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "delta"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"speed"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), false, ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getPlayers()))).then(((LiteralArgumentBuilder)Commands.literal("force").executes(commandContext -> ParticleCommand.sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle((CommandContext<CommandSourceStack>)commandContext, "name"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "delta"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"speed"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), true, ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getPlayers()))).then(Commands.argument("viewers", EntityArgument.players()).executes(commandContext -> ParticleCommand.sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle((CommandContext<CommandSourceStack>)commandContext, "name"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "delta"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"speed"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), true, EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "viewers")))))).then(((LiteralArgumentBuilder)Commands.literal("normal").executes(commandContext -> ParticleCommand.sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle((CommandContext<CommandSourceStack>)commandContext, "name"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "delta"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"speed"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), false, ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getPlayers()))).then(Commands.argument("viewers", EntityArgument.players()).executes(commandContext -> ParticleCommand.sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle((CommandContext<CommandSourceStack>)commandContext, "name"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "delta"), FloatArgumentType.getFloat((CommandContext)commandContext, (String)"speed"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"count"), false, EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "viewers")))))))))));
    }

    private static int sendParticles(CommandSourceStack commandSourceStack, ParticleOptions particleOptions, Vec3 vec3, Vec3 vec32, float f, int n, boolean bl, Collection<ServerPlayer> collection) throws CommandSyntaxException {
        int n2 = 0;
        for (ServerPlayer serverPlayer : collection) {
            if (!commandSourceStack.getLevel().sendParticles(serverPlayer, particleOptions, bl, vec3.x, vec3.y, vec3.z, n, vec32.x, vec32.y, vec32.z, f)) continue;
            ++n2;
        }
        if (n2 == 0) {
            throw ERROR_FAILED.create();
        }
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.particle.success", Registry.PARTICLE_TYPE.getKey(particleOptions.getType()).toString()), true);
        return n2;
    }
}

