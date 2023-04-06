/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.packs.repository;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

public interface PackSource {
    public static final PackSource DEFAULT = PackSource.passThrough();
    public static final PackSource BUILT_IN = PackSource.decorating("pack.source.builtin");
    public static final PackSource WORLD = PackSource.decorating("pack.source.world");
    public static final PackSource SERVER = PackSource.decorating("pack.source.server");

    public Component decorate(Component var1);

    public static PackSource passThrough() {
        return component -> component;
    }

    public static PackSource decorating(String string) {
        TranslatableComponent translatableComponent = new TranslatableComponent(string);
        return component2 -> new TranslatableComponent("pack.nameAndSource", component2, translatableComponent).withStyle(ChatFormatting.GRAY);
    }
}

