/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.chat;

import java.util.List;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class TextComponent
extends BaseComponent {
    public static final Component EMPTY = new TextComponent("");
    private final String text;

    public TextComponent(String string) {
        this.text = string;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String getContents() {
        return this.text;
    }

    @Override
    public TextComponent plainCopy() {
        return new TextComponent(this.text);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof TextComponent) {
            TextComponent textComponent = (TextComponent)object;
            return this.text.equals(textComponent.getText()) && super.equals(object);
        }
        return false;
    }

    @Override
    public String toString() {
        return "TextComponent{text='" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
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

