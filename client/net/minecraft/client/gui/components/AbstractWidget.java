/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public abstract class AbstractWidget
extends GuiComponent
implements Widget,
GuiEventListener {
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    protected int width;
    protected int height;
    public int x;
    public int y;
    private Component message;
    private boolean wasHovered;
    protected boolean isHovered;
    public boolean active = true;
    public boolean visible = true;
    protected float alpha = 1.0f;
    protected long nextNarration = Long.MAX_VALUE;
    private boolean focused;

    public AbstractWidget(int n, int n2, int n3, int n4, Component component) {
        this.x = n;
        this.y = n2;
        this.width = n3;
        this.height = n4;
        this.message = component;
    }

    public int getHeight() {
        return this.height;
    }

    protected int getYImage(boolean bl) {
        int n = 1;
        if (!this.active) {
            n = 0;
        } else if (bl) {
            n = 2;
        }
        return n;
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        if (!this.visible) {
            return;
        }
        boolean bl = this.isHovered = n >= this.x && n2 >= this.y && n < this.x + this.width && n2 < this.y + this.height;
        if (this.wasHovered != this.isHovered()) {
            if (this.isHovered()) {
                if (this.focused) {
                    this.queueNarration(200);
                } else {
                    this.queueNarration(750);
                }
            } else {
                this.nextNarration = Long.MAX_VALUE;
            }
        }
        if (this.visible) {
            this.renderButton(poseStack, n, n2, f);
        }
        this.narrate();
        this.wasHovered = this.isHovered();
    }

    protected void narrate() {
        String string;
        if (this.active && this.isHovered() && Util.getMillis() > this.nextNarration && !(string = this.createNarrationMessage().getString()).isEmpty()) {
            NarratorChatListener.INSTANCE.sayNow(string);
            this.nextNarration = Long.MAX_VALUE;
        }
    }

    protected MutableComponent createNarrationMessage() {
        return new TranslatableComponent("gui.narrate.button", this.getMessage());
    }

    public void renderButton(PoseStack poseStack, int n, int n2, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, this.alpha);
        int n3 = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, this.x, this.y, 0, 46 + n3 * 20, this.width / 2, this.height);
        this.blit(poseStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + n3 * 20, this.width / 2, this.height);
        this.renderBg(poseStack, minecraft, n, n2);
        int n4 = this.active ? 16777215 : 10526880;
        AbstractWidget.drawCenteredString(poseStack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, n4 | Mth.ceil(this.alpha * 255.0f) << 24);
    }

    protected void renderBg(PoseStack poseStack, Minecraft minecraft, int n, int n2) {
    }

    public void onClick(double d, double d2) {
    }

    public void onRelease(double d, double d2) {
    }

    protected void onDrag(double d, double d2, double d3, double d4) {
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        boolean bl;
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isValidClickButton(n) && (bl = this.clicked(d, d2))) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onClick(d, d2);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double d, double d2, int n) {
        if (this.isValidClickButton(n)) {
            this.onRelease(d, d2);
            return true;
        }
        return false;
    }

    protected boolean isValidClickButton(int n) {
        return n == 0;
    }

    @Override
    public boolean mouseDragged(double d, double d2, int n, double d3, double d4) {
        if (this.isValidClickButton(n)) {
            this.onDrag(d, d2, d3, d4);
            return true;
        }
        return false;
    }

    protected boolean clicked(double d, double d2) {
        return this.active && this.visible && d >= (double)this.x && d2 >= (double)this.y && d < (double)(this.x + this.width) && d2 < (double)(this.y + this.height);
    }

    public boolean isHovered() {
        return this.isHovered || this.focused;
    }

    @Override
    public boolean changeFocus(boolean bl) {
        if (!this.active || !this.visible) {
            return false;
        }
        this.focused = !this.focused;
        this.onFocusedChanged(this.focused);
        return this.focused;
    }

    protected void onFocusedChanged(boolean bl) {
    }

    @Override
    public boolean isMouseOver(double d, double d2) {
        return this.active && this.visible && d >= (double)this.x && d2 >= (double)this.y && d < (double)(this.x + this.width) && d2 < (double)(this.y + this.height);
    }

    public void renderToolTip(PoseStack poseStack, int n, int n2) {
    }

    public void playDownSound(SoundManager soundManager) {
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int n) {
        this.width = n;
    }

    public void setAlpha(float f) {
        this.alpha = f;
    }

    public void setMessage(Component component) {
        if (!Objects.equals(component.getString(), this.message.getString())) {
            this.queueNarration(250);
        }
        this.message = component;
    }

    public void queueNarration(int n) {
        this.nextNarration = Util.getMillis() + (long)n;
    }

    public Component getMessage() {
        return this.message;
    }

    public boolean isFocused() {
        return this.focused;
    }

    protected void setFocused(boolean bl) {
        this.focused = bl;
    }
}

