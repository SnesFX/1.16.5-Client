/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
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

import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ObjectiveCriteriaArgument
implements ArgumentType<ObjectiveCriteria> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar.baz", "minecraft:foo");
    public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.criteria.invalid", object));

    private ObjectiveCriteriaArgument() {
    }

    public static ObjectiveCriteriaArgument criteria() {
        return new ObjectiveCriteriaArgument();
    }

    public static ObjectiveCriteria getCriteria(CommandContext<CommandSourceStack> commandContext, String string) {
        return (ObjectiveCriteria)commandContext.getArgument(string, ObjectiveCriteria.class);
    }

    public ObjectiveCriteria parse(StringReader stringReader) throws CommandSyntaxException {
        int n = stringReader.getCursor();
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            stringReader.skip();
        }
        String string = stringReader.getString().substring(n, stringReader.getCursor());
        return ObjectiveCriteria.byName(string).orElseThrow(() -> {
            stringReader.setCursor(n);
            return ERROR_INVALID_VALUE.create((Object)string);
        });
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        ArrayList arrayList = Lists.newArrayList(ObjectiveCriteria.CRITERIA_BY_NAME.keySet());
        for (StatType statType : Registry.STAT_TYPE) {
            for (Object e : statType.getRegistry()) {
                String string = this.getName(statType, e);
                arrayList.add(string);
            }
        }
        return SharedSuggestionProvider.suggest(arrayList, suggestionsBuilder);
    }

    public <T> String getName(StatType<T> statType, Object object) {
        return Stat.buildName(statType, object);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

