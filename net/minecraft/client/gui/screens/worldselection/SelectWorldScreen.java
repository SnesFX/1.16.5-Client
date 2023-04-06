/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.worldselection;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

public class SelectWorldScreen
extends Screen {
    protected final Screen lastScreen;
    private List<FormattedCharSequence> toolTip;
    private Button deleteButton;
    private Button selectButton;
    private Button renameButton;
    private Button copyButton;
    protected EditBox searchBox;
    private WorldSelectionList list;

    public SelectWorldScreen(Screen screen) {
        super(new TranslatableComponent("selectWorld.title"));
        this.lastScreen = screen;
    }

    @Override
    public boolean mouseScrolled(double d, double d2, double d3) {
        return super.mouseScrolled(d, d2, d3);
    }

    @Override
    public void tick() {
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, new TranslatableComponent("selectWorld.search"));
        this.searchBox.setResponder(string -> this.list.refreshList(() -> string, false));
        this.list = new WorldSelectionList(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, () -> this.searchBox.getValue(), this.list);
        this.children.add(this.searchBox);
        this.children.add(this.list);
        this.selectButton = this.addButton(new Button(this.width / 2 - 154, this.height - 52, 150, 20, new TranslatableComponent("selectWorld.select"), button -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::joinWorld)));
        this.addButton(new Button(this.width / 2 + 4, this.height - 52, 150, 20, new TranslatableComponent("selectWorld.create"), button -> this.minecraft.setScreen(CreateWorldScreen.create(this))));
        this.renameButton = this.addButton(new Button(this.width / 2 - 154, this.height - 28, 72, 20, new TranslatableComponent("selectWorld.edit"), button -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::editWorld)));
        this.deleteButton = this.addButton(new Button(this.width / 2 - 76, this.height - 28, 72, 20, new TranslatableComponent("selectWorld.delete"), button -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::deleteWorld)));
        this.copyButton = this.addButton(new Button(this.width / 2 + 4, this.height - 28, 72, 20, new TranslatableComponent("selectWorld.recreate"), button -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::recreateWorld)));
        this.addButton(new Button(this.width / 2 + 82, this.height - 28, 72, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreen(this.lastScreen)));
        this.updateButtonStatus(false);
        this.setInitialFocus(this.searchBox);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (super.keyPressed(n, n2, n3)) {
            return true;
        }
        return this.searchBox.keyPressed(n, n2, n3);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public boolean charTyped(char c, int n) {
        return this.searchBox.charTyped(c, n);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.toolTip = null;
        this.list.render(poseStack, n, n2, f);
        this.searchBox.render(poseStack, n, n2, f);
        SelectWorldScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 8, 16777215);
        super.render(poseStack, n, n2, f);
        if (this.toolTip != null) {
            this.renderTooltip(poseStack, this.toolTip, n, n2);
        }
    }

    public void setToolTip(List<FormattedCharSequence> list) {
        this.toolTip = list;
    }

    public void updateButtonStatus(boolean bl) {
        this.selectButton.active = bl;
        this.deleteButton.active = bl;
        this.renameButton.active = bl;
        this.copyButton.active = bl;
    }

    @Override
    public void removed() {
        if (this.list != null) {
            this.list.children().forEach(WorldSelectionList.WorldListEntry::close);
        }
    }
}

