/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.BooleanOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.gui.components.VolumeSlider;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;

public class SoundOptionsScreen
extends OptionsSubScreen {
    public SoundOptionsScreen(Screen screen, Options options) {
        super(screen, options, new TranslatableComponent("options.sounds.title"));
    }

    @Override
    protected void init() {
        int n = 0;
        this.addButton(new VolumeSlider(this.minecraft, this.width / 2 - 155 + n % 2 * 160, this.height / 6 - 12 + 24 * (n >> 1), SoundSource.MASTER, 310));
        n += 2;
        for (SoundSource soundSource : SoundSource.values()) {
            if (soundSource == SoundSource.MASTER) continue;
            this.addButton(new VolumeSlider(this.minecraft, this.width / 2 - 155 + n % 2 * 160, this.height / 6 - 12 + 24 * (n >> 1), soundSource, 150));
            ++n;
        }
        this.addButton(new OptionButton(this.width / 2 - 75, this.height / 6 - 12 + 24 * (++n >> 1), 150, 20, Option.SHOW_SUBTITLES, Option.SHOW_SUBTITLES.getMessage(this.options), button -> {
            Option.SHOW_SUBTITLES.toggle(this.minecraft.options);
            button.setMessage(Option.SHOW_SUBTITLES.getMessage(this.minecraft.options));
            this.minecraft.options.save();
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        SoundOptionsScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 15, 16777215);
        super.render(poseStack, n, n2, f);
    }
}

