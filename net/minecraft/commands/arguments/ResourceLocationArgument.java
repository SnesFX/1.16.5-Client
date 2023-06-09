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
import java.util.function.Supplier;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ResourceLocationArgument
implements ArgumentType<ResourceLocation> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ADVANCEMENT = new DynamicCommandExceptionType(object -> new TranslatableComponent("advancement.advancementNotFound", object));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType(object -> new TranslatableComponent("recipe.notFound", object));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_PREDICATE = new DynamicCommandExceptionType(object -> new TranslatableComponent("predicate.unknown", object));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ATTRIBUTE = new DynamicCommandExceptionType(object -> new TranslatableComponent("attribute.unknown", object));

    public static ResourceLocationArgument id() {
        return new ResourceLocationArgument();
    }

    public static Advancement getAdvancement(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        ResourceLocation resourceLocation = (ResourceLocation)commandContext.getArgument(string, ResourceLocation.class);
        Advancement advancement = ((CommandSourceStack)commandContext.getSource()).getServer().getAdvancements().getAdvancement(resourceLocation);
        if (advancement == null) {
            throw ERROR_UNKNOWN_ADVANCEMENT.create((Object)resourceLocation);
        }
        return advancement;
    }

    public static Recipe<?> getRecipe(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        RecipeManager recipeManager = ((CommandSourceStack)commandContext.getSource()).getServer().getRecipeManager();
        ResourceLocation resourceLocation = (ResourceLocation)commandContext.getArgument(string, ResourceLocation.class);
        return recipeManager.byKey(resourceLocation).orElseThrow(() -> ERROR_UNKNOWN_RECIPE.create((Object)resourceLocation));
    }

    public static LootItemCondition getPredicate(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        ResourceLocation resourceLocation = (ResourceLocation)commandContext.getArgument(string, ResourceLocation.class);
        PredicateManager predicateManager = ((CommandSourceStack)commandContext.getSource()).getServer().getPredicateManager();
        LootItemCondition lootItemCondition = predicateManager.get(resourceLocation);
        if (lootItemCondition == null) {
            throw ERROR_UNKNOWN_PREDICATE.create((Object)resourceLocation);
        }
        return lootItemCondition;
    }

    public static Attribute getAttribute(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        ResourceLocation resourceLocation = (ResourceLocation)commandContext.getArgument(string, ResourceLocation.class);
        return Registry.ATTRIBUTE.getOptional(resourceLocation).orElseThrow(() -> ERROR_UNKNOWN_ATTRIBUTE.create((Object)resourceLocation));
    }

    public static ResourceLocation getId(CommandContext<CommandSourceStack> commandContext, String string) {
        return (ResourceLocation)commandContext.getArgument(string, ResourceLocation.class);
    }

    public ResourceLocation parse(StringReader stringReader) throws CommandSyntaxException {
        return ResourceLocation.read(stringReader);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

