/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.controls;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.BooleanOption;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.ControlList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class ControlsScreen
extends OptionsSubScreen {
    public KeyMapping selectedKey;
    public long lastKeySelection;
    private ControlList controlList;
    private Button resetButton;

    public ControlsScreen(Screen screen, Options options) {
        super(screen, options, new TranslatableComponent("controls.title"));
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 155, 18, 150, 20, new TranslatableComponent("options.mouse_settings"), button -> this.minecraft.setScreen(new MouseSettingsScreen(this, this.options))));
        this.addButton(Option.AUTO_JUMP.createButton(this.options, this.width / 2 - 155 + 160, 18, 150));
        this.controlList = new ControlList(this, this.minecraft);
        this.children.add(this.controlList);
        this.resetButton = this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, new TranslatableComponent("controls.resetAll"), button -> {
            for (KeyMapping keyMapping : this.options.keyMappings) {
                keyMapping.setKey(keyMapping.getDefaultKey());
            }
            KeyMapping.resetMapping();
        }));
        this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (this.selectedKey != null) {
            this.options.setKey(this.selectedKey, InputConstants.Type.MOUSE.getOrCreate(n));
            this.selectedKey = null;
            KeyMapping.resetMapping();
            return true;
        }
        return super.mouseClicked(d, d2, n);
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (this.selectedKey != null) {
            if (n == 256) {
                this.options.setKey(this.selectedKey, InputConstants.UNKNOWN);
            } else {
                this.options.setKey(this.selectedKey, InputConstants.getKey(n, n2));
            }
            this.selectedKey = null;
            this.lastKeySelection = Util.getMillis();
            KeyMapping.resetMapping();
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.controlList.render(poseStack, n, n2, f);
        ControlsScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 8, 16777215);
        boolean bl = false;
        for (KeyMapping keyMapping : this.options.keyMappings) {
            if (keyMapping.isDefault()) continue;
            bl = true;
            break;
        }
        this.resetButton.active = bl;
        super.render(poseStack, n, n2, f);
    }
}

