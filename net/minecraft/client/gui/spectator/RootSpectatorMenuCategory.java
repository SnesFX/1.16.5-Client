/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.gui.spectator;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.categories.TeleportToPlayerMenuCategory;
import net.minecraft.client.gui.spectator.categories.TeleportToTeamMenuCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class RootSpectatorMenuCategory
implements SpectatorMenuCategory {
    private static final Component PROMPT_TEXT = new TranslatableComponent("spectatorMenu.root.prompt");
    private final List<SpectatorMenuItem> items = Lists.newArrayList();

    public RootSpectatorMenuCategory() {
        this.items.add(new TeleportToPlayerMenuCategory());
        this.items.add(new TeleportToTeamMenuCategory());
    }

    @Override
    public List<SpectatorMenuItem> getItems() {
        return this.items;
    }

    @Override
    public Component getPrompt() {
        return PROMPT_TEXT;
    }
}

