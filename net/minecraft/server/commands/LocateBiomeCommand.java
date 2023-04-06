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
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
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
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

public class LocateBiomeCommand {
    public static final DynamicCommandExceptionType ERROR_INVALID_BIOME = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.locatebiome.invalid", object));
    private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.locatebiome.notFound", object));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("locatebiome").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.argument("biome", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_BIOMES).executes(commandContext -> LocateBiomeCommand.locateBiome((CommandSourceStack)commandContext.getSource(), (ResourceLocation)commandContext.getArgument("biome", ResourceLocation.class)))));
    }

    private static int locateBiome(CommandSourceStack commandSourceStack, ResourceLocation resourceLocation) throws CommandSyntaxException {
        Biome biome = (Biome)commandSourceStack.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOptional(resourceLocation).orElseThrow(() -> ERROR_INVALID_BIOME.create((Object)resourceLocation));
        BlockPos blockPos = new BlockPos(commandSourceStack.getPosition());
        BlockPos blockPos2 = commandSourceStack.getLevel().findNearestBiome(biome, blockPos, 6400, 8);
        String string = resourceLocation.toString();
        if (blockPos2 == null) {
            throw ERROR_BIOME_NOT_FOUND.create((Object)string);
        }
        return LocateCommand.showLocateResult(commandSourceStack, string, blockPos, blockPos2, "commands.locatebiome.success");
    }
}

