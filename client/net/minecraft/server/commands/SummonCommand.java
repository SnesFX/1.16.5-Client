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
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.LiteralCommandNode
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
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class SummonCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.summon.failed"));
    private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.summon.invalidPosition"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("summon").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(((RequiredArgumentBuilder)Commands.argument("entity", EntitySummonArgument.id()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(commandContext -> SummonCommand.spawnEntity((CommandSourceStack)commandContext.getSource(), EntitySummonArgument.getSummonableEntity((CommandContext<CommandSourceStack>)commandContext, "entity"), ((CommandSourceStack)commandContext.getSource()).getPosition(), new CompoundTag(), true))).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes(commandContext -> SummonCommand.spawnEntity((CommandSourceStack)commandContext.getSource(), EntitySummonArgument.getSummonableEntity((CommandContext<CommandSourceStack>)commandContext, "entity"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), new CompoundTag(), true))).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes(commandContext -> SummonCommand.spawnEntity((CommandSourceStack)commandContext.getSource(), EntitySummonArgument.getSummonableEntity((CommandContext<CommandSourceStack>)commandContext, "entity"), Vec3Argument.getVec3((CommandContext<CommandSourceStack>)commandContext, "pos"), CompoundTagArgument.getCompoundTag(commandContext, "nbt"), false))))));
    }

    private static int spawnEntity(CommandSourceStack commandSourceStack, ResourceLocation resourceLocation, Vec3 vec3, CompoundTag compoundTag, boolean bl) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(vec3);
        if (!Level.isInSpawnableBounds(blockPos)) {
            throw INVALID_POSITION.create();
        }
        CompoundTag compoundTag2 = compoundTag.copy();
        compoundTag2.putString("id", resourceLocation.toString());
        ServerLevel serverLevel = commandSourceStack.getLevel();
        Entity entity2 = EntityType.loadEntityRecursive(compoundTag2, serverLevel, entity -> {
            entity.moveTo(vec3.x, vec3.y, vec3.z, entity.yRot, entity.xRot);
            return entity;
        });
        if (entity2 == null) {
            throw ERROR_FAILED.create();
        }
        if (bl && entity2 instanceof Mob) {
            ((Mob)entity2).finalizeSpawn(commandSourceStack.getLevel(), commandSourceStack.getLevel().getCurrentDifficultyAt(entity2.blockPosition()), MobSpawnType.COMMAND, null, null);
        }
        if (!serverLevel.tryAddFreshEntityWithPassengers(entity2)) {
            throw ERROR_DUPLICATE_UUID.create();
        }
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.summon.success", entity2.getDisplayName()), true);
        return 1;
    }
}

