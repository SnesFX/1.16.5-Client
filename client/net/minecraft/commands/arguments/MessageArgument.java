/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandExceptionType
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;

public class MessageArgument
implements ArgumentType<Message> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

    public static MessageArgument message() {
        return new MessageArgument();
    }

    public static Component getMessage(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        return ((Message)commandContext.getArgument(string, Message.class)).toComponent((CommandSourceStack)commandContext.getSource(), ((CommandSourceStack)commandContext.getSource()).hasPermission(2));
    }

    public Message parse(StringReader stringReader) throws CommandSyntaxException {
        return Message.parseText(stringReader, true);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static class Part {
        private final int start;
        private final int end;
        private final EntitySelector selector;

        public Part(int n, int n2, EntitySelector entitySelector) {
            this.start = n;
            this.end = n2;
            this.selector = entitySelector;
        }

        public int getStart() {
            return this.start;
        }

        public int getEnd() {
            return this.end;
        }

        @Nullable
        public Component toComponent(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
            return EntitySelector.joinNames(this.selector.findEntities(commandSourceStack));
        }
    }

    public static class Message {
        private final String text;
        private final Part[] parts;

        public Message(String string, Part[] arrpart) {
            this.text = string;
            this.parts = arrpart;
        }

        public Component toComponent(CommandSourceStack commandSourceStack, boolean bl) throws CommandSyntaxException {
            if (this.parts.length == 0 || !bl) {
                return new TextComponent(this.text);
            }
            TextComponent textComponent = new TextComponent(this.text.substring(0, this.parts[0].getStart()));
            int n = this.parts[0].getStart();
            for (Part part : this.parts) {
                Component component = part.toComponent(commandSourceStack);
                if (n < part.getStart()) {
                    textComponent.append(this.text.substring(n, part.getStart()));
                }
                if (component != null) {
                    textComponent.append(component);
                }
                n = part.getEnd();
            }
            if (n < this.text.length()) {
                textComponent.append(this.text.substring(n, this.text.length()));
            }
            return textComponent;
        }

        public static Message parseText(StringReader stringReader, boolean bl) throws CommandSyntaxException {
            String string = stringReader.getString().substring(stringReader.getCursor(), stringReader.getTotalLength());
            if (!bl) {
                stringReader.setCursor(stringReader.getTotalLength());
                return new Message(string, new Part[0]);
            }
            ArrayList arrayList = Lists.newArrayList();
            int n = stringReader.getCursor();
            while (stringReader.canRead()) {
                if (stringReader.peek() == '@') {
                    EntitySelector entitySelector;
                    int n2 = stringReader.getCursor();
                    try {
                        EntitySelectorParser entitySelectorParser = new EntitySelectorParser(stringReader);
                        entitySelector = entitySelectorParser.parse();
                    }
                    catch (CommandSyntaxException commandSyntaxException) {
                        if (commandSyntaxException.getType() == EntitySelectorParser.ERROR_MISSING_SELECTOR_TYPE || commandSyntaxException.getType() == EntitySelectorParser.ERROR_UNKNOWN_SELECTOR_TYPE) {
                            stringReader.setCursor(n2 + 1);
                            continue;
                        }
                        throw commandSyntaxException;
                    }
                    arrayList.add(new Part(n2 - n, stringReader.getCursor() - n, entitySelector));
                    continue;
                }
                stringReader.skip();
            }
            return new Message(string, arrayList.toArray(new Part[arrayList.size()]));
        }
    }

}

