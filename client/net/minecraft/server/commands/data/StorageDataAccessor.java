/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.server.commands.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.world.level.storage.CommandStorage;

public class StorageDataAccessor
implements DataAccessor {
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_STORAGE = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggestResource(StorageDataAccessor.getGlobalTags((CommandContext<CommandSourceStack>)commandContext).keys(), suggestionsBuilder);
    public static final Function<String, DataCommands.DataProvider> PROVIDER = string -> new DataCommands.DataProvider((String)string){
        final /* synthetic */ String val$arg;
        {
            this.val$arg = string;
        }

        @Override
        public DataAccessor access(CommandContext<CommandSourceStack> commandContext) {
            return new StorageDataAccessor(StorageDataAccessor.getGlobalTags((CommandContext<CommandSourceStack>)commandContext), ResourceLocationArgument.getId(commandContext, this.val$arg));
        }

        @Override
        public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> argumentBuilder, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> function) {
            return argumentBuilder.then(Commands.literal("storage").then(function.apply((ArgumentBuilder<CommandSourceStack, ?>)Commands.argument(this.val$arg, ResourceLocationArgument.id()).suggests(SUGGEST_STORAGE))));
        }
    };
    private final CommandStorage storage;
    private final ResourceLocation id;

    private static CommandStorage getGlobalTags(CommandContext<CommandSourceStack> commandContext) {
        return ((CommandSourceStack)commandContext.getSource()).getServer().getCommandStorage();
    }

    private StorageDataAccessor(CommandStorage commandStorage, ResourceLocation resourceLocation) {
        this.storage = commandStorage;
        this.id = resourceLocation;
    }

    @Override
    public void setData(CompoundTag compoundTag) {
        this.storage.set(this.id, compoundTag);
    }

    @Override
    public CompoundTag getData() {
        return this.storage.get(this.id);
    }

    @Override
    public Component getModifiedSuccess() {
        return new TranslatableComponent("commands.data.storage.modified", this.id);
    }

    @Override
    public Component getPrintSuccess(Tag tag) {
        return new TranslatableComponent("commands.data.storage.query", this.id, tag.getPrettyDisplay());
    }

    @Override
    public Component getPrintSuccess(NbtPathArgument.NbtPath nbtPath, double d, int n) {
        return new TranslatableComponent("commands.data.storage.get", nbtPath, this.id, String.format(Locale.ROOT, "%.2f", d), n);
    }

}

