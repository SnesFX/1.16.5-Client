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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ParticleArgument
implements ArgumentType<ParticleOptions> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType(object -> new TranslatableComponent("particle.notFound", object));

    public static ParticleArgument particle() {
        return new ParticleArgument();
    }

    public static ParticleOptions getParticle(CommandContext<CommandSourceStack> commandContext, String string) {
        return (ParticleOptions)commandContext.getArgument(string, ParticleOptions.class);
    }

    public ParticleOptions parse(StringReader stringReader) throws CommandSyntaxException {
        return ParticleArgument.readParticle(stringReader);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static ParticleOptions readParticle(StringReader stringReader) throws CommandSyntaxException {
        ResourceLocation resourceLocation = ResourceLocation.read(stringReader);
        ParticleType<?> particleType = Registry.PARTICLE_TYPE.getOptional(resourceLocation).orElseThrow(() -> ERROR_UNKNOWN_PARTICLE.create((Object)resourceLocation));
        return ParticleArgument.readParticle(stringReader, particleType);
    }

    private static <T extends ParticleOptions> T readParticle(StringReader stringReader, ParticleType<T> particleType) throws CommandSyntaxException {
        return particleType.getDeserializer().fromCommand(particleType, stringReader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggestResource(Registry.PARTICLE_TYPE.keySet(), suggestionsBuilder);
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

