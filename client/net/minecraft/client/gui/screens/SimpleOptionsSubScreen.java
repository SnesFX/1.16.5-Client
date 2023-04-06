/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.CycleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public abstract class SimpleOptionsSubScreen
extends OptionsSubScreen {
    private final Option[] smallOptions;
    @Nullable
    private AbstractWidget narratorButton;
    private OptionsList list;

    public SimpleOptionsSubScreen(Screen screen, Options options, Component component, Option[] arroption) {
        super(screen, options, component);
        this.smallOptions = arroption;
    }

    @Override
    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addSmall(this.smallOptions);
        this.children.add(this.list);
        this.createFooter();
        this.narratorButton = this.list.findOption(Option.NARRATOR);
        if (this.narratorButton != null) {
            this.narratorButton.active = NarratorChatListener.INSTANCE.isActive();
        }
    }

    protected void createFooter() {
        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.list.render(poseStack, n, n2, f);
        SimpleOptionsSubScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, 16777215);
        super.render(poseStack, n, n2, f);
        List<FormattedCharSequence> list = SimpleOptionsSubScreen.tooltipAt(this.list, n, n2);
        if (list != null) {
            this.renderTooltip(poseStack, list, n, n2);
        }
    }

    public void updateNarratorButton() {
        if (this.narratorButton != null) {
            this.narratorButton.setMessage(Option.NARRATOR.getMessage(this.options));
        }
    }
}

