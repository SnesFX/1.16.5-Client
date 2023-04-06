/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
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

import com.google.common.collect.Maps;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;

public class SlotArgument
implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = Arrays.asList("container.5", "12", "weapon");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_SLOT = new DynamicCommandExceptionType(object -> new TranslatableComponent("slot.unknown", object));
    private static final Map<String, Integer> SLOTS = Util.make(Maps.newHashMap(), hashMap -> {
        int n;
        for (n = 0; n < 54; ++n) {
            hashMap.put("container." + n, n);
        }
        for (n = 0; n < 9; ++n) {
            hashMap.put("hotbar." + n, n);
        }
        for (n = 0; n < 27; ++n) {
            hashMap.put("inventory." + n, 9 + n);
        }
        for (n = 0; n < 27; ++n) {
            hashMap.put("enderchest." + n, 200 + n);
        }
        for (n = 0; n < 8; ++n) {
            hashMap.put("villager." + n, 300 + n);
        }
        for (n = 0; n < 15; ++n) {
            hashMap.put("horse." + n, 500 + n);
        }
        hashMap.put("weapon", 98);
        hashMap.put("weapon.mainhand", 98);
        hashMap.put("weapon.offhand", 99);
        hashMap.put("armor.head", 100 + EquipmentSlot.HEAD.getIndex());
        hashMap.put("armor.chest", 100 + EquipmentSlot.CHEST.getIndex());
        hashMap.put("armor.legs", 100 + EquipmentSlot.LEGS.getIndex());
        hashMap.put("armor.feet", 100 + EquipmentSlot.FEET.getIndex());
        hashMap.put("horse.saddle", 400);
        hashMap.put("horse.armor", 401);
        hashMap.put("horse.chest", 499);
    });

    public static SlotArgument slot() {
        return new SlotArgument();
    }

    public static int getSlot(CommandContext<CommandSourceStack> commandContext, String string) {
        return (Integer)commandContext.getArgument(string, Integer.class);
    }

    public Integer parse(StringReader stringReader) throws CommandSyntaxException {
        String string = stringReader.readUnquotedString();
        if (!SLOTS.containsKey(string)) {
            throw ERROR_UNKNOWN_SLOT.create((Object)string);
        }
        return SLOTS.get(string);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(SLOTS.keySet(), suggestionsBuilder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

