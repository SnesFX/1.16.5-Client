/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BaseCommandBlock;

public abstract class AbstractCommandBlockEditScreen
extends Screen {
    private static final Component SET_COMMAND_LABEL = new TranslatableComponent("advMode.setCommand");
    private static final Component COMMAND_LABEL = new TranslatableComponent("advMode.command");
    private static final Component PREVIOUS_OUTPUT_LABEL = new TranslatableComponent("advMode.previousOutput");
    protected EditBox commandEdit;
    protected EditBox previousEdit;
    protected Button doneButton;
    protected Button cancelButton;
    protected Button outputButton;
    protected boolean trackOutput;
    private CommandSuggestions commandSuggestions;

    public AbstractCommandBlockEditScreen() {
        super(NarratorChatListener.NO_TITLE);
    }

    @Override
    public void tick() {
        this.commandEdit.tick();
    }

    abstract BaseCommandBlock getCommandBlock();

    abstract int getPreviousY();

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.doneButton = this.addButton(new Button(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, CommonComponents.GUI_DONE, button -> this.onDone()));
        this.cancelButton = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, CommonComponents.GUI_CANCEL, button -> this.onClose()));
        this.outputButton = this.addButton(new Button(this.width / 2 + 150 - 20, this.getPreviousY(), 20, 20, new TextComponent("O"), button -> {
            BaseCommandBlock baseCommandBlock;
            baseCommandBlock.setTrackOutput(!(baseCommandBlock = this.getCommandBlock()).isTrackOutput());
            this.updateCommandOutput();
        }));
        this.commandEdit = new EditBox(this.font, this.width / 2 - 150, 50, 300, 20, new TranslatableComponent("advMode.command")){

            @Override
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(AbstractCommandBlockEditScreen.this.commandSuggestions.getNarrationMessage());
            }
        };
        this.commandEdit.setMaxLength(32500);
        this.commandEdit.setResponder(this::onEdited);
        this.children.add(this.commandEdit);
        this.previousEdit = new EditBox(this.font, this.width / 2 - 150, this.getPreviousY(), 276, 20, new TranslatableComponent("advMode.previousOutput"));
        this.previousEdit.setMaxLength(32500);
        this.previousEdit.setEditable(false);
        this.previousEdit.setValue("-");
        this.children.add(this.previousEdit);
        this.setInitialFocus(this.commandEdit);
        this.commandEdit.setFocus(true);
        this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.commandEdit, this.font, true, true, 0, 7, false, Integer.MIN_VALUE);
        this.commandSuggestions.setAllowSuggestions(true);
        this.commandSuggestions.updateCommandInfo();
    }

    @Override
    public void resize(Minecraft minecraft, int n, int n2) {
        String string = this.commandEdit.getValue();
        this.init(minecraft, n, n2);
        this.commandEdit.setValue(string);
        this.commandSuggestions.updateCommandInfo();
    }

    protected void updateCommandOutput() {
        if (this.getCommandBlock().isTrackOutput()) {
            this.outputButton.setMessage(new TextComponent("O"));
            this.previousEdit.setValue(this.getCommandBlock().getLastOutput().getString());
        } else {
            this.outputButton.setMessage(new TextComponent("X"));
            this.previousEdit.setValue("-");
        }
    }

    protected void onDone() {
        BaseCommandBlock baseCommandBlock = this.getCommandBlock();
        this.populateAndSendPacket(baseCommandBlock);
        if (!baseCommandBlock.isTrackOutput()) {
            baseCommandBlock.setLastOutput(null);
        }
        this.minecraft.setScreen(null);
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    protected abstract void populateAndSendPacket(BaseCommandBlock var1);

    @Override
    public void onClose() {
        this.getCommandBlock().setTrackOutput(this.trackOutput);
        this.minecraft.setScreen(null);
    }

    private void onEdited(String string) {
        this.commandSuggestions.updateCommandInfo();
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (this.commandSuggestions.keyPressed(n, n2, n3)) {
            return true;
        }
        if (super.keyPressed(n, n2, n3)) {
            return true;
        }
        if (n == 257 || n == 335) {
            this.onDone();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double d, double d2, double d3) {
        if (this.commandSuggestions.mouseScrolled(d3)) {
            return true;
        }
        return super.mouseScrolled(d, d2, d3);
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (this.commandSuggestions.mouseClicked(d, d2, n)) {
            return true;
        }
        return super.mouseClicked(d, d2, n);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        AbstractCommandBlockEditScreen.drawCenteredString(poseStack, this.font, SET_COMMAND_LABEL, this.width / 2, 20, 16777215);
        AbstractCommandBlockEditScreen.drawString(poseStack, this.font, COMMAND_LABEL, this.width / 2 - 150, 40, 10526880);
        this.commandEdit.render(poseStack, n, n2, f);
        int n3 = 75;
        if (!this.previousEdit.getValue().isEmpty()) {
            this.font.getClass();
            AbstractCommandBlockEditScreen.drawString(poseStack, this.font, PREVIOUS_OUTPUT_LABEL, this.width / 2 - 150, (n3 += 5 * 9 + 1 + this.getPreviousY() - 135) + 4, 10526880);
            this.previousEdit.render(poseStack, n, n2, f);
        }
        super.render(poseStack, n, n2, f);
        this.commandSuggestions.render(poseStack, n, n2);
    }

}

