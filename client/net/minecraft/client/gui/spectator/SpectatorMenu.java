/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.gui.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.RootSpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuListener;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class SpectatorMenu {
    private static final SpectatorMenuItem CLOSE_ITEM = new CloseSpectatorItem();
    private static final SpectatorMenuItem SCROLL_LEFT = new ScrollMenuItem(-1, true);
    private static final SpectatorMenuItem SCROLL_RIGHT_ENABLED = new ScrollMenuItem(1, true);
    private static final SpectatorMenuItem SCROLL_RIGHT_DISABLED = new ScrollMenuItem(1, false);
    private static final Component CLOSE_MENU_TEXT = new TranslatableComponent("spectatorMenu.close");
    private static final Component PREVIOUS_PAGE_TEXT = new TranslatableComponent("spectatorMenu.previous_page");
    private static final Component NEXT_PAGE_TEXT = new TranslatableComponent("spectatorMenu.next_page");
    public static final SpectatorMenuItem EMPTY_SLOT = new SpectatorMenuItem(){

        @Override
        public void selectItem(SpectatorMenu spectatorMenu) {
        }

        @Override
        public Component getName() {
            return TextComponent.EMPTY;
        }

        @Override
        public void renderIcon(PoseStack poseStack, float f, int n) {
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    };
    private final SpectatorMenuListener listener;
    private SpectatorMenuCategory category = new RootSpectatorMenuCategory();
    private int selectedSlot = -1;
    private int page;

    public SpectatorMenu(SpectatorMenuListener spectatorMenuListener) {
        this.listener = spectatorMenuListener;
    }

    public SpectatorMenuItem getItem(int n) {
        int n2 = n + this.page * 6;
        if (this.page > 0 && n == 0) {
            return SCROLL_LEFT;
        }
        if (n == 7) {
            if (n2 < this.category.getItems().size()) {
                return SCROLL_RIGHT_ENABLED;
            }
            return SCROLL_RIGHT_DISABLED;
        }
        if (n == 8) {
            return CLOSE_ITEM;
        }
        if (n2 < 0 || n2 >= this.category.getItems().size()) {
            return EMPTY_SLOT;
        }
        return (SpectatorMenuItem)MoreObjects.firstNonNull((Object)this.category.getItems().get(n2), (Object)EMPTY_SLOT);
    }

    public List<SpectatorMenuItem> getItems() {
        ArrayList arrayList = Lists.newArrayList();
        for (int i = 0; i <= 8; ++i) {
            arrayList.add(this.getItem(i));
        }
        return arrayList;
    }

    public SpectatorMenuItem getSelectedItem() {
        return this.getItem(this.selectedSlot);
    }

    public SpectatorMenuCategory getSelectedCategory() {
        return this.category;
    }

    public void selectSlot(int n) {
        SpectatorMenuItem spectatorMenuItem = this.getItem(n);
        if (spectatorMenuItem != EMPTY_SLOT) {
            if (this.selectedSlot == n && spectatorMenuItem.isEnabled()) {
                spectatorMenuItem.selectItem(this);
            } else {
                this.selectedSlot = n;
            }
        }
    }

    public void exit() {
        this.listener.onSpectatorMenuClosed(this);
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }

    public void selectCategory(SpectatorMenuCategory spectatorMenuCategory) {
        this.category = spectatorMenuCategory;
        this.selectedSlot = -1;
        this.page = 0;
    }

    public SpectatorPage getCurrentPage() {
        return new SpectatorPage(this.category, this.getItems(), this.selectedSlot);
    }

    static class ScrollMenuItem
    implements SpectatorMenuItem {
        private final int direction;
        private final boolean enabled;

        public ScrollMenuItem(int n, boolean bl) {
            this.direction = n;
            this.enabled = bl;
        }

        @Override
        public void selectItem(SpectatorMenu spectatorMenu) {
            spectatorMenu.page = spectatorMenu.page + this.direction;
        }

        @Override
        public Component getName() {
            return this.direction < 0 ? PREVIOUS_PAGE_TEXT : NEXT_PAGE_TEXT;
        }

        @Override
        public void renderIcon(PoseStack poseStack, float f, int n) {
            Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
            if (this.direction < 0) {
                GuiComponent.blit(poseStack, 0, 0, 144.0f, 0.0f, 16, 16, 256, 256);
            } else {
                GuiComponent.blit(poseStack, 0, 0, 160.0f, 0.0f, 16, 16, 256, 256);
            }
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }
    }

    static class CloseSpectatorItem
    implements SpectatorMenuItem {
        private CloseSpectatorItem() {
        }

        @Override
        public void selectItem(SpectatorMenu spectatorMenu) {
            spectatorMenu.exit();
        }

        @Override
        public Component getName() {
            return CLOSE_MENU_TEXT;
        }

        @Override
        public void renderIcon(PoseStack poseStack, float f, int n) {
            Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
            GuiComponent.blit(poseStack, 0, 0, 128.0f, 0.0f, 16, 16, 256, 256);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

}

