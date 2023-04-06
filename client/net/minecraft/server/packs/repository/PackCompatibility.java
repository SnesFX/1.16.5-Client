/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.packs.repository;

import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

public enum PackCompatibility {
    TOO_OLD("old"),
    TOO_NEW("new"),
    COMPATIBLE("compatible");
    
    private final Component description;
    private final Component confirmation;

    private PackCompatibility(String string2) {
        this.description = new TranslatableComponent("pack.incompatible." + string2).withStyle(ChatFormatting.GRAY);
        this.confirmation = new TranslatableComponent("pack.incompatible.confirm." + string2);
    }

    public boolean isCompatible() {
        return this == COMPATIBLE;
    }

    public static PackCompatibility forFormat(int n) {
        if (n < SharedConstants.getCurrentVersion().getPackVersion()) {
            return TOO_OLD;
        }
        if (n > SharedConstants.getCurrentVersion().getPackVersion()) {
            return TOO_NEW;
        }
        return COMPATIBLE;
    }

    public Component getDescription() {
        return this.description;
    }

    public Component getConfirmation() {
        return this.confirmation;
    }
}

