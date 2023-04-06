/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;

public class VolumeSlider
extends AbstractOptionSliderButton {
    private final SoundSource source;

    public VolumeSlider(Minecraft minecraft, int n, int n2, SoundSource soundSource, int n3) {
        super(minecraft.options, n, n2, n3, 20, minecraft.options.getSoundSourceVolume(soundSource));
        this.source = soundSource;
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        Component component = (float)this.value == (float)this.getYImage(false) ? CommonComponents.OPTION_OFF : new TextComponent((int)(this.value * 100.0) + "%");
        this.setMessage(new TranslatableComponent("soundCategory." + this.source.getName()).append(": ").append(component));
    }

    @Override
    protected void applyValue() {
        this.options.setSoundCategoryVolume(this.source, (float)this.value);
        this.options.save();
    }
}

