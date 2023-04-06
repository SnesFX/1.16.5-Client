/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import net.minecraft.client.CycleOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.PlayerModelPart;

public class SkinCustomizationScreen
extends OptionsSubScreen {
    public SkinCustomizationScreen(Screen screen, Options options) {
        super(screen, options, new TranslatableComponent("options.skinCustomisation.title"));
    }

    @Override
    protected void init() {
        int n = 0;
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            this.addButton(new Button(this.width / 2 - 155 + n % 2 * 160, this.height / 6 + 24 * (n >> 1), 150, 20, this.getMessage(playerModelPart), button -> {
                this.options.toggleModelPart(playerModelPart);
                button.setMessage(this.getMessage(playerModelPart));
            }));
            ++n;
        }
        this.addButton(new OptionButton(this.width / 2 - 155 + n % 2 * 160, this.height / 6 + 24 * (n >> 1), 150, 20, Option.MAIN_HAND, Option.MAIN_HAND.getMessage(this.options), button -> {
            Option.MAIN_HAND.toggle(this.options, 1);
            this.options.save();
            button.setMessage(Option.MAIN_HAND.getMessage(this.options));
            this.options.broadcastOptions();
        }));
        if (++n % 2 == 1) {
            ++n;
        }
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (n >> 1), 200, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        SkinCustomizationScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, 16777215);
        super.render(poseStack, n, n2, f);
    }

    private Component getMessage(PlayerModelPart playerModelPart) {
        return CommonComponents.optionStatus(playerModelPart.getName(), this.options.getModelParts().contains((Object)playerModelPart));
    }
}

