/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  javax.annotation.Nullable
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class TeleportCommand {
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.teleport.invalidPosition"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        LiteralCommandNode literalCommandNode = commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("teleport").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("location", Vec3Argument.vec3()).executes(commandContext -> TeleportCommand.teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)commandContext, "location"), null, null))).then(Commands.argument("rotation", RotationArgument.rotation()).executes(commandContext -> TeleportCommand.teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)commandContext, "location"), RotationArgument.getRotation((CommandContext<CommandSourceStack>)commandContext, "rotation"), null)))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("facingEntity", EntityArgument.entity()).executes(commandContext -> TeleportCommand.teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)commandContext, "location"), null, new LookAt(EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "facingEntity"), EntityAnchorArgument.Anchor.FEET)))).then(Commands.argument("facingAnchor", EntityAnchorArgument.anchor()).executes(commandContext -> TeleportCommand.teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)commandContext, "location"), null, new LookAt(EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "facingEntity"), EntityAnchorArgument.getAnchor((CommandContext<CommandSourceStack>)commandContext, "facingAnchor")))))))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes(commandContext -> TeleportCommand.teleportToPos((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)commandContext, "location"), null, new LookAt(Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "facingLocation")))))))).then(Commands.argument("destination", EntityArgument.entity()).executes(commandContext -> TeleportCommand.teleportToEntity((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "destination")))))).then(Commands.argument("location", Vec3Argument.vec3()).executes(commandContext -> TeleportCommand.teleportToPos((CommandSourceStack)commandContext.getSource(), Collections.singleton(((CommandSourceStack)commandContext.getSource()).getEntityOrException()), ((CommandSourceStack)commandContext.getSource()).getLevel(), Vec3Argument.getCoordinates((CommandContext<CommandSourceStack>)commandContext, "location"), WorldCoordinates.current(), null)))).then(Commands.argument("destination", EntityArgument.entity()).executes(commandContext -> TeleportCommand.teleportToEntity((CommandSourceStack)commandContext.getSource(), Collections.singleton(((CommandSourceStack)commandContext.getSource()).getEntityOrException()), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "destination")))));
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tp").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).redirect((CommandNode)literalCommandNode));
    }

    private static int teleportToEntity(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, Entity entity) throws CommandSyntaxException {
        for (Entity entity2 : collection) {
            TeleportCommand.performTeleport(commandSourceStack, entity2, (ServerLevel)entity.level, entity.getX(), entity.getY(), entity.getZ(), EnumSet.noneOf(ClientboundPlayerPositionPacket.RelativeArgument.class), entity.yRot, entity.xRot, null);
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.teleport.success.entity.single", collection.iterator().next().getDisplayName(), entity.getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.teleport.success.entity.multiple", collection.size(), entity.getDisplayName()), true);
        }
        return collection.size();
    }

    private static int teleportToPos(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, ServerLevel serverLevel, Coordinates coordinates, @Nullable Coordinates coordinates2, @Nullable LookAt lookAt) throws CommandSyntaxException {
        Vec3 vec3 = coordinates.getPosition(commandSourceStack);
        Vec2 vec2 = coordinates2 == null ? null : coordinates2.getRotation(commandSourceStack);
        EnumSet<ClientboundPlayerPositionPacket.RelativeArgument> enumSet = EnumSet.noneOf(ClientboundPlayerPositionPacket.RelativeArgument.class);
        if (coordinates.isXRelative()) {
            enumSet.add(ClientboundPlayerPositionPacket.RelativeArgument.X);
        }
        if (coordinates.isYRelative()) {
            enumSet.add(ClientboundPlayerPositionPacket.RelativeArgument.Y);
        }
        if (coordinates.isZRelative()) {
            enumSet.add(ClientboundPlayerPositionPacket.RelativeArgument.Z);
        }
        if (coordinates2 == null) {
            enumSet.add(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT);
            enumSet.add(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT);
        } else {
            if (coordinates2.isXRelative()) {
                enumSet.add(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT);
            }
            if (coordinates2.isYRelative()) {
                enumSet.add(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT);
            }
        }
        for (Entity entity : collection) {
            if (coordinates2 == null) {
                TeleportCommand.performTeleport(commandSourceStack, entity, serverLevel, vec3.x, vec3.y, vec3.z, enumSet, entity.yRot, entity.xRot, lookAt);
                continue;
            }
            TeleportCommand.performTeleport(commandSourceStack, entity, serverLevel, vec3.x, vec3.y, vec3.z, enumSet, vec2.y, vec2.x, lookAt);
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.teleport.success.location.single", collection.iterator().next().getDisplayName(), vec3.x, vec3.y, vec3.z), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.teleport.success.location.multiple", collection.size(), vec3.x, vec3.y, vec3.z), true);
        }
        return collection.size();
    }

    private static void performTeleport(CommandSourceStack commandSourceStack, Entity entity, ServerLevel serverLevel, double d, double d2, double d3, Set<ClientboundPlayerPositionPacket.RelativeArgument> set, float f, float f2, @Nullable LookAt lookAt) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(d, d2, d3);
        if (!Level.isInSpawnableBounds(blockPos)) {
            throw INVALID_POSITION.create();
        }
        if (entity instanceof ServerPlayer) {
            ChunkPos chunkPos = new ChunkPos(new BlockPos(d, d2, d3));
            serverLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, entity.getId());
            entity.stopRiding();
            if (((ServerPlayer)entity).isSleeping()) {
                ((ServerPlayer)entity).stopSleepInBed(true, true);
            }
            if (serverLevel == entity.level) {
                ((ServerPlayer)entity).connection.teleport(d, d2, d3, f, f2, set);
            } else {
                ((ServerPlayer)entity).teleportTo(serverLevel, d, d2, d3, f, f2);
            }
            entity.setYHeadRot(f);
        } else {
            float f3 = Mth.wrapDegrees(f);
            float f4 = Mth.wrapDegrees(f2);
            f4 = Mth.clamp(f4, -90.0f, 90.0f);
            if (serverLevel == entity.level) {
                entity.moveTo(d, d2, d3, f3, f4);
                entity.setYHeadRot(f3);
            } else {
                entity.unRide();
                Entity entity2 = entity;
                entity = entity2.getType().create(serverLevel);
                if (entity != null) {
                    entity.restoreFrom(entity2);
                    entity.moveTo(d, d2, d3, f3, f4);
                    entity.setYHeadRot(f3);
                    serverLevel.addFromAnotherDimension(entity);
                    entity2.removed = true;
                } else {
                    return;
                }
            }
        }
        if (lookAt != null) {
            lookAt.perform(commandSourceStack, entity);
        }
        if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).isFallFlying()) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            entity.setOnGround(true);
        }
        if (entity instanceof PathfinderMob) {
            ((PathfinderMob)entity).getNavigation().stop();
        }
    }

    static class LookAt {
        private final Vec3 position;
        private final Entity entity;
        private final EntityAnchorArgument.Anchor anchor;

        public LookAt(Entity entity, EntityAnchorArgument.Anchor anchor) {
            this.entity = entity;
            this.anchor = anchor;
            this.position = anchor.apply(entity);
        }

        public LookAt(Vec3 vec3) {
            this.entity = null;
            this.position = vec3;
            this.anchor = null;
        }

        public void perform(CommandSourceStack commandSourceStack, Entity entity) {
            if (this.entity != null) {
                if (entity instanceof ServerPlayer) {
                    ((ServerPlayer)entity).lookAt(commandSourceStack.getAnchor(), this.entity, this.anchor);
                } else {
                    entity.lookAt(commandSourceStack.getAnchor(), this.position);
                }
            } else {
                entity.lookAt(commandSourceStack.getAnchor(), this.position);
            }
        }
    }

}

