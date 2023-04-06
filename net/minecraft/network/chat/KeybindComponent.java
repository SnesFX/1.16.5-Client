/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.chat;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class KeybindComponent
extends BaseComponent {
    private static Function<String, Supplier<Component>> keyResolver = string -> () -> new TextComponent((String)string);
    private final String name;
    private Supplier<Component> nameResolver;

    public KeybindComponent(String string) {
        this.name = string;
    }

    public static void setKeyResolver(Function<String, Supplier<Component>> function) {
        keyResolver = function;
    }

    private Component getNestedComponent() {
        if (this.nameResolver == null) {
            this.nameResolver = keyResolver.apply(this.name);
        }
        return this.nameResolver.get();
    }

    @Override
    public <T> Optional<T> visitSelf(FormattedText.ContentConsumer<T> contentConsumer) {
        return this.getNestedComponent().visit(contentConsumer);
    }

    @Override
    public <T> Optional<T> visitSelf(FormattedText.StyledContentConsumer<T> styledContentConsumer, Style style) {
        return this.getNestedComponent().visit(styledContentConsumer, style);
    }

    @Override
    public KeybindComponent plainCopy() {
        return new KeybindComponent(this.name);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof KeybindComponent) {
            KeybindComponent keybindComponent = (KeybindComponent)object;
            return this.name.equals(keybindComponent.name) && super.equals(object);
        }
        return false;
    }

    @Override
    public String toString() {
        return "KeybindComponent{keybind='" + this.name + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    public String getName() {
        return this.name;
    }

    @Override
    public /* synthetic */ BaseComponent plainCopy() {
        return this.plainCopy();
    }

    @Override
    public /* synthetic */ MutableComponent plainCopy() {
        return this.plainCopy();
    }
}

