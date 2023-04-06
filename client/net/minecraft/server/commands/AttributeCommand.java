/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType$Function
 *  com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType$Function
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeCommand {
    private static final SuggestionProvider<CommandSourceStack> AVAILABLE_ATTRIBUTES = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggestResource(Registry.ATTRIBUTE.keySet(), suggestionsBuilder);
    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.attribute.failed.entity", object));
    private static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ATTRIBUTE = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableComponent("commands.attribute.failed.no_attribute", object, object2));
    private static final Dynamic3CommandExceptionType ERROR_NO_SUCH_MODIFIER = new Dynamic3CommandExceptionType((object, object2, object3) -> new TranslatableComponent("commands.attribute.failed.no_modifier", object2, object, object3));
    private static final Dynamic3CommandExceptionType ERROR_MODIFIER_ALREADY_PRESENT = new Dynamic3CommandExceptionType((object, object2, object3) -> new TranslatableComponent("commands.attribute.failed.modifier_already_present", object3, object2, object));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("attribute").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.argument("target", EntityArgument.entity()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("attribute", ResourceLocationArgument.id()).suggests(AVAILABLE_ATTRIBUTES).then(((LiteralArgumentBuilder)Commands.literal("get").executes(commandContext -> AttributeCommand.getAttributeValue((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "target"), ResourceLocationArgument.getAttribute((CommandContext<CommandSourceStack>)commandContext, "attribute"), 1.0))).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes(commandContext -> AttributeCommand.getAttributeValue((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "target"), ResourceLocationArgument.getAttribute((CommandContext<CommandSourceStack>)commandContext, "attribute"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale")))))).then(((LiteralArgumentBuilder)Commands.literal("base").then(Commands.literal("set").then(Commands.argument("value", DoubleArgumentType.doubleArg()).executes(commandContext -> AttributeCommand.setAttributeBase((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "target"), ResourceLocationArgument.getAttribute((CommandContext<CommandSourceStack>)commandContext, "attribute"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"value")))))).then(((LiteralArgumentBuilder)Commands.literal("get").executes(commandContext -> AttributeCommand.getAttributeBase((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "target"), ResourceLocationArgument.getAttribute((CommandContext<CommandSourceStack>)commandContext, "attribute"), 1.0))).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes(commandContext -> AttributeCommand.getAttributeBase((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "target"), ResourceLocationArgument.getAttribute((CommandContext<CommandSourceStack>)commandContext, "attribute"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale"))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("modifier").then(Commands.literal("add").then(Commands.argument("uuid", UuidArgument.uuid()).then(Commands.argument("name", StringArgumentType.string()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("value", DoubleArgumentType.doubleArg()).then(Commands.literal("add").executes(commandContext -> AttributeCommand.addModifier((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "target"), ResourceLocationArgument.getAttribute((CommandContext<CommandSourceStack>)commandContext, "attribute"), UuidArgument.getUuid((CommandContext<CommandSourceStack>)commandContext, "uuid"), StringArgumentType.getString((CommandContext)commandContext, (String)"name"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"value"), AttributeModifier.Operation.ADDITION)))).then(Commands.literal("multiply").executes(commandContext -> AttributeCommand.addModifier((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "target"), ResourceLocationArgument.getAttribute((CommandContext<CommandSourceStack>)commandContext, "attribute"), UuidArgument.getUuid((CommandContext<CommandSourceStack>)commandContext, "uuid"), StringArgumentType.getString((CommandContext)commandContext, (String)"name"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"value"), AttributeModifier.Operation.MULTIPLY_TOTAL)))).then(Commands.literal("multiply_base").executes(commandContext -> AttributeCommand.addModifier((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "target"), ResourceLocationArgument.getAttribute((CommandContext<CommandSourceStack>)commandContext, "attribute"), UuidArgument.getUuid((CommandContext<CommandSourceStack>)commandContext, "uuid"), StringArgumentType.getString((CommandContext)commandContext, (String)"name"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"value"), AttributeModifier.Operation.MULTIPLY_BASE)))))))).then(Commands.literal("remove").then(Commands.argument("uuid", UuidArgument.uuid()).executes(commandContext -> AttributeCommand.removeModifier((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "target"), ResourceLocationArgument.getAttribute((CommandContext<CommandSourceStack>)commandContext, "attribute"), UuidArgument.getUuid((CommandContext<CommandSourceStack>)commandContext, "uuid")))))).then(Commands.literal("value").then(Commands.literal("get").then(((RequiredArgumentBuilder)Commands.argument("uuid", UuidArgument.uuid()).executes(commandContext -> AttributeCommand.getAttributeModifier((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "target"), ResourceLocationArgument.getAttribute((CommandContext<CommandSourceStack>)commandContext, "attribute"), UuidArgument.getUuid((CommandContext<CommandSourceStack>)commandContext, "uuid"), 1.0))).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes(commandContext -> AttributeCommand.getAttributeModifier((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)commandContext, "target"), ResourceLocationArgument.getAttribute((CommandContext<CommandSourceStack>)commandContext, "attribute"), UuidArgument.getUuid((CommandContext<CommandSourceStack>)commandContext, "uuid"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale")))))))))));
    }

    private static AttributeInstance getAttributeInstance(Entity entity, Attribute attribute) throws CommandSyntaxException {
        AttributeInstance attributeInstance = AttributeCommand.getLivingEntity(entity).getAttributes().getInstance(attribute);
        if (attributeInstance == null) {
            throw ERROR_NO_SUCH_ATTRIBUTE.create((Object)entity.getName(), (Object)new TranslatableComponent(attribute.getDescriptionId()));
        }
        return attributeInstance;
    }

    private static LivingEntity getLivingEntity(Entity entity) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity)) {
            throw ERROR_NOT_LIVING_ENTITY.create((Object)entity.getName());
        }
        return (LivingEntity)entity;
    }

    private static LivingEntity getEntityWithAttribute(Entity entity, Attribute attribute) throws CommandSyntaxException {
        LivingEntity livingEntity = AttributeCommand.getLivingEntity(entity);
        if (!livingEntity.getAttributes().hasAttribute(attribute)) {
            throw ERROR_NO_SUCH_ATTRIBUTE.create((Object)entity.getName(), (Object)new TranslatableComponent(attribute.getDescriptionId()));
        }
        return livingEntity;
    }

    private static int getAttributeValue(CommandSourceStack commandSourceStack, Entity entity, Attribute attribute, double d) throws CommandSyntaxException {
        LivingEntity livingEntity = AttributeCommand.getEntityWithAttribute(entity, attribute);
        double d2 = livingEntity.getAttributeValue(attribute);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.attribute.value.get.success", new TranslatableComponent(attribute.getDescriptionId()), entity.getName(), d2), false);
        return (int)(d2 * d);
    }

    private static int getAttributeBase(CommandSourceStack commandSourceStack, Entity entity, Attribute attribute, double d) throws CommandSyntaxException {
        LivingEntity livingEntity = AttributeCommand.getEntityWithAttribute(entity, attribute);
        double d2 = livingEntity.getAttributeBaseValue(attribute);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.attribute.base_value.get.success", new TranslatableComponent(attribute.getDescriptionId()), entity.getName(), d2), false);
        return (int)(d2 * d);
    }

    private static int getAttributeModifier(CommandSourceStack commandSourceStack, Entity entity, Attribute attribute, UUID uUID, double d) throws CommandSyntaxException {
        LivingEntity livingEntity = AttributeCommand.getEntityWithAttribute(entity, attribute);
        AttributeMap attributeMap = livingEntity.getAttributes();
        if (!attributeMap.hasModifier(attribute, uUID)) {
            throw ERROR_NO_SUCH_MODIFIER.create((Object)entity.getName(), (Object)new TranslatableComponent(attribute.getDescriptionId()), (Object)uUID);
        }
        double d2 = attributeMap.getModifierValue(attribute, uUID);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.attribute.modifier.value.get.success", uUID, new TranslatableComponent(attribute.getDescriptionId()), entity.getName(), d2), false);
        return (int)(d2 * d);
    }

    private static int setAttributeBase(CommandSourceStack commandSourceStack, Entity entity, Attribute attribute, double d) throws CommandSyntaxException {
        AttributeCommand.getAttributeInstance(entity, attribute).setBaseValue(d);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.attribute.base_value.set.success", new TranslatableComponent(attribute.getDescriptionId()), entity.getName(), d), false);
        return 1;
    }

    private static int addModifier(CommandSourceStack commandSourceStack, Entity entity, Attribute attribute, UUID uUID, String string, double d, AttributeModifier.Operation operation) throws CommandSyntaxException {
        AttributeModifier attributeModifier;
        AttributeInstance attributeInstance = AttributeCommand.getAttributeInstance(entity, attribute);
        if (attributeInstance.hasModifier(attributeModifier = new AttributeModifier(uUID, string, d, operation))) {
            throw ERROR_MODIFIER_ALREADY_PRESENT.create((Object)entity.getName(), (Object)new TranslatableComponent(attribute.getDescriptionId()), (Object)uUID);
        }
        attributeInstance.addPermanentModifier(attributeModifier);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.attribute.modifier.add.success", uUID, new TranslatableComponent(attribute.getDescriptionId()), entity.getName()), false);
        return 1;
    }

    private static int removeModifier(CommandSourceStack commandSourceStack, Entity entity, Attribute attribute, UUID uUID) throws CommandSyntaxException {
        AttributeInstance attributeInstance = AttributeCommand.getAttributeInstance(entity, attribute);
        if (attributeInstance.removePermanentModifier(uUID)) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.attribute.modifier.remove.success", uUID, new TranslatableComponent(attribute.getDescriptionId()), entity.getName()), false);
            return 1;
        }
        throw ERROR_NO_SUCH_MODIFIER.create((Object)entity.getName(), (Object)new TranslatableComponent(attribute.getDescriptionId()), (Object)uUID);
    }
}

