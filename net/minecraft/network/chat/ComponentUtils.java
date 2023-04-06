/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ContextAwareComponent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;

public class ComponentUtils {
    public static MutableComponent mergeStyles(MutableComponent mutableComponent, Style style) {
        if (style.isEmpty()) {
            return mutableComponent;
        }
        Style style2 = mutableComponent.getStyle();
        if (style2.isEmpty()) {
            return mutableComponent.setStyle(style);
        }
        if (style2.equals(style)) {
            return mutableComponent;
        }
        return mutableComponent.setStyle(style2.applyTo(style));
    }

    public static MutableComponent updateForEntity(@Nullable CommandSourceStack commandSourceStack, Component component, @Nullable Entity entity, int n) throws CommandSyntaxException {
        if (n > 100) {
            return component.copy();
        }
        MutableComponent mutableComponent = component instanceof ContextAwareComponent ? ((ContextAwareComponent)((Object)component)).resolve(commandSourceStack, entity, n + 1) : component.plainCopy();
        for (Component component2 : component.getSiblings()) {
            mutableComponent.append(ComponentUtils.updateForEntity(commandSourceStack, component2, entity, n + 1));
        }
        return mutableComponent.withStyle(ComponentUtils.resolveStyle(commandSourceStack, component.getStyle(), entity, n));
    }

    private static Style resolveStyle(@Nullable CommandSourceStack commandSourceStack, Style style, @Nullable Entity entity, int n) throws CommandSyntaxException {
        Component component;
        HoverEvent hoverEvent = style.getHoverEvent();
        if (hoverEvent != null && (component = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT)) != null) {
            HoverEvent hoverEvent2 = new HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentUtils.updateForEntity(commandSourceStack, component, entity, n + 1));
            return style.withHoverEvent(hoverEvent2);
        }
        return style;
    }

    public static Component getDisplayName(GameProfile gameProfile) {
        if (gameProfile.getName() != null) {
            return new TextComponent(gameProfile.getName());
        }
        if (gameProfile.getId() != null) {
            return new TextComponent(gameProfile.getId().toString());
        }
        return new TextComponent("(unknown)");
    }

    public static Component formatList(Collection<String> collection) {
        return ComponentUtils.formatAndSortList(collection, string -> new TextComponent((String)string).withStyle(ChatFormatting.GREEN));
    }

    public static <T extends Comparable<T>> Component formatAndSortList(Collection<T> collection, Function<T, Component> function) {
        if (collection.isEmpty()) {
            return TextComponent.EMPTY;
        }
        if (collection.size() == 1) {
            return function.apply(collection.iterator().next());
        }
        ArrayList arrayList = Lists.newArrayList(collection);
        arrayList.sort(Comparable::compareTo);
        return ComponentUtils.formatList(arrayList, function);
    }

    public static <T> MutableComponent formatList(Collection<T> collection, Function<T, Component> function) {
        if (collection.isEmpty()) {
            return new TextComponent("");
        }
        if (collection.size() == 1) {
            return function.apply(collection.iterator().next()).copy();
        }
        TextComponent textComponent = new TextComponent("");
        boolean bl = true;
        for (T t : collection) {
            if (!bl) {
                textComponent.append(new TextComponent(", ").withStyle(ChatFormatting.GRAY));
            }
            textComponent.append(function.apply(t));
            bl = false;
        }
        return textComponent;
    }

    public static MutableComponent wrapInSquareBrackets(Component component) {
        return new TranslatableComponent("chat.square_brackets", component);
    }

    public static Component fromMessage(Message message) {
        if (message instanceof Component) {
            return (Component)message;
        }
        return new TextComponent(message.getString());
    }
}

