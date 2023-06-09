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
 */
package net.minecraft.commands.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class EntitySummonArgument
implements ArgumentType<ResourceLocation> {
    private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:pig", "cow");
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_ENTITY = new DynamicCommandExceptionType(object -> new TranslatableComponent("entity.notFound", object));

    public static EntitySummonArgument id() {
        return new EntitySummonArgument();
    }

    public static ResourceLocation getSummonableEntity(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        return EntitySummonArgument.verifyCanSummon((ResourceLocation)commandContext.getArgument(string, ResourceLocation.class));
    }

    private static ResourceLocation verifyCanSummon(ResourceLocation resourceLocation) throws CommandSyntaxException {
        Registry.ENTITY_TYPE.getOptional(resourceLocation).filter(EntityType::canSummon).orElseThrow(() -> ERROR_UNKNOWN_ENTITY.create((Object)resourceLocation));
        return resourceLocation;
    }

    public ResourceLocation parse(StringReader stringReader) throws CommandSyntaxException {
        return EntitySummonArgument.verifyCanSummon(ResourceLocation.read(stringReader));
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

