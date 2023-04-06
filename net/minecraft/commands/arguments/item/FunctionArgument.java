/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.tags.Tag;

public class FunctionArgument
implements ArgumentType<Result> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType(object -> new TranslatableComponent("arguments.function.tag.unknown", object));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_FUNCTION = new DynamicCommandExceptionType(object -> new TranslatableComponent("arguments.function.unknown", object));

    public static FunctionArgument functions() {
        return new FunctionArgument();
    }

    public Result parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '#') {
            stringReader.skip();
            final ResourceLocation resourceLocation = ResourceLocation.read(stringReader);
            return new Result(){

                @Override
                public Collection<CommandFunction> create(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
                    Tag tag = FunctionArgument.getFunctionTag((CommandContext<CommandSourceStack>)commandContext, resourceLocation);
                    return tag.getValues();
                }

                @Override
                public Pair<ResourceLocation, Either<CommandFunction, Tag<CommandFunction>>> unwrap(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
                    return Pair.of((Object)resourceLocation, (Object)Either.right((Object)FunctionArgument.getFunctionTag((CommandContext<CommandSourceStack>)commandContext, resourceLocation)));
                }
            };
        }
        final ResourceLocation resourceLocation = ResourceLocation.read(stringReader);
        return new Result(){

            @Override
            public Collection<CommandFunction> create(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
                return Collections.singleton(FunctionArgument.getFunction((CommandContext<CommandSourceStack>)commandContext, resourceLocation));
            }

            @Override
            public Pair<ResourceLocation, Either<CommandFunction, Tag<CommandFunction>>> unwrap(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
                return Pair.of((Object)resourceLocation, (Object)Either.left((Object)FunctionArgument.getFunction((CommandContext<CommandSourceStack>)commandContext, resourceLocation)));
            }
        };
    }

    private static CommandFunction getFunction(CommandContext<CommandSourceStack> commandContext, ResourceLocation resourceLocation) throws CommandSyntaxException {
        return ((CommandSourceStack)commandContext.getSource()).getServer().getFunctions().get(resourceLocation).orElseThrow(() -> ERROR_UNKNOWN_FUNCTION.create((Object)resourceLocation.toString()));
    }

    private static Tag<CommandFunction> getFunctionTag(CommandContext<CommandSourceStack> commandContext, ResourceLocation resourceLocation) throws CommandSyntaxException {
        Tag<CommandFunction> tag = ((CommandSourceStack)commandContext.getSource()).getServer().getFunctions().getTag(resourceLocation);
        if (tag == null) {
            throw ERROR_UNKNOWN_TAG.create((Object)resourceLocation.toString());
        }
        return tag;
    }

    public static Collection<CommandFunction> getFunctions(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        return ((Result)commandContext.getArgument(string, Result.class)).create(commandContext);
    }

    public static Pair<ResourceLocation, Either<CommandFunction, Tag<CommandFunction>>> getFunctionOrTag(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        return ((Result)commandContext.getArgument(string, Result.class)).unwrap(commandContext);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static interface Result {
        public Collection<CommandFunction> create(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;

        public Pair<ResourceLocation, Either<CommandFunction, Tag<CommandFunction>>> unwrap(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;
    }

}

