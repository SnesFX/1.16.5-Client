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
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.commands.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class ItemEnchantmentArgument
implements ArgumentType<Enchantment> {
    private static final Collection<String> EXAMPLES = Arrays.asList("unbreaking", "silk_touch");
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_ENCHANTMENT = new DynamicCommandExceptionType(object -> new TranslatableComponent("enchantment.unknown", object));

    public static ItemEnchantmentArgument enchantment() {
        return new ItemEnchantmentArgument();
    }

    public static Enchantment getEnchantment(CommandContext<CommandSourceStack> commandContext, String string) {
        return (Enchantment)commandContext.getArgument(string, Enchantment.class);
    }

    public Enchantment parse(StringReader stringReader) throws CommandSyntaxException {
        ResourceLocation resourceLocation = ResourceLocation.read(stringReader);
        return Registry.ENCHANTMENT.getOptional(resourceLocation).orElseThrow(() -> ERROR_UNKNOWN_ENCHANTMENT.create((Object)resourceLocation));
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggestResource(Registry.ENCHANTMENT.keySet(), suggestionsBuilder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

