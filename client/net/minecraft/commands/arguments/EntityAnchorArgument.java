/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.ImmutableStringReader;
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
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityAnchorArgument
implements ArgumentType<Anchor> {
    private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
    private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.anchor.invalid", object));

    public static Anchor getAnchor(CommandContext<CommandSourceStack> commandContext, String string) {
        return (Anchor)((Object)commandContext.getArgument(string, Anchor.class));
    }

    public static EntityAnchorArgument anchor() {
        return new EntityAnchorArgument();
    }

    public Anchor parse(StringReader stringReader) throws CommandSyntaxException {
        int n = stringReader.getCursor();
        String string = stringReader.readUnquotedString();
        Anchor anchor = Anchor.getByName(string);
        if (anchor == null) {
            stringReader.setCursor(n);
            throw ERROR_INVALID.createWithContext((ImmutableStringReader)stringReader, (Object)string);
        }
        return anchor;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(Anchor.BY_NAME.keySet(), suggestionsBuilder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static enum Anchor {
        FEET("feet", (vec3, entity) -> vec3),
        EYES("eyes", (vec3, entity) -> new Vec3(vec3.x, vec3.y + (double)entity.getEyeHeight(), vec3.z));
        
        private static final Map<String, Anchor> BY_NAME;
        private final String name;
        private final BiFunction<Vec3, Entity, Vec3> transform;

        private Anchor(String string2, BiFunction<Vec3, Entity, Vec3> biFunction) {
            this.name = string2;
            this.transform = biFunction;
        }

        @Nullable
        public static Anchor getByName(String string) {
            return BY_NAME.get(string);
        }

        public Vec3 apply(Entity entity) {
            return this.transform.apply(entity.position(), entity);
        }

        public Vec3 apply(CommandSourceStack commandSourceStack) {
            Entity entity = commandSourceStack.getEntity();
            if (entity == null) {
                return commandSourceStack.getPosition();
            }
            return this.transform.apply(commandSourceStack.getPosition(), entity);
        }

        static {
            BY_NAME = Util.make(Maps.newHashMap(), hashMap -> {
                for (Anchor anchor : Anchor.values()) {
                    hashMap.put(anchor.name, anchor);
                }
            });
        }
    }

}

