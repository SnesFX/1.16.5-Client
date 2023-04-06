/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public abstract class BaseComponent
implements MutableComponent {
    protected final List<Component> siblings = Lists.newArrayList();
    private FormattedCharSequence visualOrderText = FormattedCharSequence.EMPTY;
    @Nullable
    private Language decomposedWith;
    private Style style = Style.EMPTY;

    @Override
    public MutableComponent append(Component component) {
        this.siblings.add(component);
        return this;
    }

    @Override
    public String getContents() {
        return "";
    }

    @Override
    public List<Component> getSiblings() {
        return this.siblings;
    }

    @Override
    public MutableComponent setStyle(Style style) {
        this.style = style;
        return this;
    }

    @Override
    public Style getStyle() {
        return this.style;
    }

    @Override
    public abstract BaseComponent plainCopy();

    @Override
    public final MutableComponent copy() {
        BaseComponent baseComponent = this.plainCopy();
        baseComponent.siblings.addAll(this.siblings);
        baseComponent.setStyle(this.style);
        return baseComponent;
    }

    @Override
    public FormattedCharSequence getVisualOrderText() {
        Language language = Language.getInstance();
        if (this.decomposedWith != language) {
            this.visualOrderText = language.getVisualOrder(this);
            this.decomposedWith = language;
        }
        return this.visualOrderText;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof BaseComponent) {
            BaseComponent baseComponent = (BaseComponent)object;
            return this.siblings.equals(baseComponent.siblings) && Objects.equals(this.getStyle(), baseComponent.getStyle());
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.getStyle(), this.siblings);
    }

    public String toString() {
        return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
    }

    @Override
    public /* synthetic */ MutableComponent plainCopy() {
        return this.plainCopy();
    }
}

