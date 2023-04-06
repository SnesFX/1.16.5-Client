/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class DemoIntroScreen
extends Screen {
    private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");
    private MultiLineLabel movementMessage = MultiLineLabel.EMPTY;
    private MultiLineLabel durationMessage = MultiLineLabel.EMPTY;

    public DemoIntroScreen() {
        super(new TranslatableComponent("demo.help.title"));
    }

    @Override
    protected void init() {
        int n = -16;
        this.addButton(new Button(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, new TranslatableComponent("demo.help.buy"), button -> {
            button.active = false;
            Util.getPlatform().openUri("http://www.minecraft.net/store?source=demo");
        }));
        this.addButton(new Button(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, new TranslatableComponent("demo.help.later"), button -> {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
        }));
        Options options = this.minecraft.options;
        this.movementMessage = MultiLineLabel.create(this.font, new TranslatableComponent("demo.help.movementShort", options.keyUp.getTranslatedKeyMessage(), options.keyLeft.getTranslatedKeyMessage(), options.keyDown.getTranslatedKeyMessage(), options.keyRight.getTranslatedKeyMessage()), new TranslatableComponent("demo.help.movementMouse"), new TranslatableComponent("demo.help.jump", options.keyJump.getTranslatedKeyMessage()), new TranslatableComponent("demo.help.inventory", options.keyInventory.getTranslatedKeyMessage()));
        this.durationMessage = MultiLineLabel.create(this.font, (FormattedText)new TranslatableComponent("demo.help.fullWrapped"), 218);
    }

    @Override
    public void renderBackground(PoseStack poseStack) {
        super.renderBackground(poseStack);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(DEMO_BACKGROUND_LOCATION);
        int n = (this.width - 248) / 2;
        int n2 = (this.height - 166) / 2;
        this.blit(poseStack, n, n2, 0, 0, 248, 166);
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        int n3 = (this.width - 248) / 2 + 10;
        int n4 = (this.height - 166) / 2 + 8;
        this.font.draw(poseStack, this.title, (float)n3, (float)n4, 2039583);
        n4 = this.movementMessage.renderLeftAlignedNoShadow(poseStack, n3, n4 + 12, 12, 5197647);
        this.font.getClass();
        this.durationMessage.renderLeftAlignedNoShadow(poseStack, n3, n4 + 20, 9, 2039583);
        super.render(poseStack, n, n2, f);
    }
}

