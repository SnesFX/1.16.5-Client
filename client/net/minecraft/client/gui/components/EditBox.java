/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class EditBox
extends AbstractWidget
implements Widget,
GuiEventListener {
    private final Font font;
    private String value = "";
    private int maxLength = 32;
    private int frame;
    private boolean bordered = true;
    private boolean canLoseFocus = true;
    private boolean isEditable = true;
    private boolean shiftPressed;
    private int displayPos;
    private int cursorPos;
    private int highlightPos;
    private int textColor = 14737632;
    private int textColorUneditable = 7368816;
    private String suggestion;
    private Consumer<String> responder;
    private Predicate<String> filter = Objects::nonNull;
    private BiFunction<String, Integer, FormattedCharSequence> formatter = (string, n) -> FormattedCharSequence.forward(string, Style.EMPTY);

    public EditBox(Font font, int n, int n2, int n3, int n4, Component component) {
        this(font, n, n2, n3, n4, null, component);
    }

    public EditBox(Font font, int n2, int n3, int n4, int n5, @Nullable EditBox editBox, Component component) {
        super(n2, n3, n4, n5, component);
        this.font = font;
        if (editBox != null) {
            this.setValue(editBox.getValue());
        }
    }

    public void setResponder(Consumer<String> consumer) {
        this.responder = consumer;
    }

    public void setFormatter(BiFunction<String, Integer, FormattedCharSequence> biFunction) {
        this.formatter = biFunction;
    }

    public void tick() {
        ++this.frame;
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        Component component = this.getMessage();
        return new TranslatableComponent("gui.narrate.editBox", component, this.value);
    }

    public void setValue(String string) {
        if (!this.filter.test(string)) {
            return;
        }
        this.value = string.length() > this.maxLength ? string.substring(0, this.maxLength) : string;
        this.moveCursorToEnd();
        this.setHighlightPos(this.cursorPos);
        this.onValueChange(string);
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int n = this.cursorPos < this.highlightPos ? this.cursorPos : this.highlightPos;
        int n2 = this.cursorPos < this.highlightPos ? this.highlightPos : this.cursorPos;
        return this.value.substring(n, n2);
    }

    public void setFilter(Predicate<String> predicate) {
        this.filter = predicate;
    }

    public void insertText(String string) {
        int n;
        String string2;
        String string3;
        int n2 = this.cursorPos < this.highlightPos ? this.cursorPos : this.highlightPos;
        int n3 = this.cursorPos < this.highlightPos ? this.highlightPos : this.cursorPos;
        int n4 = this.maxLength - this.value.length() - (n2 - n3);
        if (n4 < (n = (string3 = SharedConstants.filterText(string)).length())) {
            string3 = string3.substring(0, n4);
            n = n4;
        }
        if (!this.filter.test(string2 = new StringBuilder(this.value).replace(n2, n3, string3).toString())) {
            return;
        }
        this.value = string2;
        this.setCursorPosition(n2 + n);
        this.setHighlightPos(this.cursorPos);
        this.onValueChange(this.value);
    }

    private void onValueChange(String string) {
        if (this.responder != null) {
            this.responder.accept(string);
        }
        this.nextNarration = Util.getMillis() + 500L;
    }

    private void deleteText(int n) {
        if (Screen.hasControlDown()) {
            this.deleteWords(n);
        } else {
            this.deleteChars(n);
        }
    }

    public void deleteWords(int n) {
        if (this.value.isEmpty()) {
            return;
        }
        if (this.highlightPos != this.cursorPos) {
            this.insertText("");
            return;
        }
        this.deleteChars(this.getWordPosition(n) - this.cursorPos);
    }

    public void deleteChars(int n) {
        int n2;
        if (this.value.isEmpty()) {
            return;
        }
        if (this.highlightPos != this.cursorPos) {
            this.insertText("");
            return;
        }
        int n3 = this.getCursorPos(n);
        int n4 = Math.min(n3, this.cursorPos);
        if (n4 == (n2 = Math.max(n3, this.cursorPos))) {
            return;
        }
        String string = new StringBuilder(this.value).delete(n4, n2).toString();
        if (!this.filter.test(string)) {
            return;
        }
        this.value = string;
        this.moveCursorTo(n4);
    }

    public int getWordPosition(int n) {
        return this.getWordPosition(n, this.getCursorPosition());
    }

    private int getWordPosition(int n, int n2) {
        return this.getWordPosition(n, n2, true);
    }

    private int getWordPosition(int n, int n2, boolean bl) {
        int n3 = n2;
        boolean bl2 = n < 0;
        int n4 = Math.abs(n);
        for (int i = 0; i < n4; ++i) {
            if (bl2) {
                while (bl && n3 > 0 && this.value.charAt(n3 - 1) == ' ') {
                    --n3;
                }
                while (n3 > 0 && this.value.charAt(n3 - 1) != ' ') {
                    --n3;
                }
                continue;
            }
            int n5 = this.value.length();
            if ((n3 = this.value.indexOf(32, n3)) == -1) {
                n3 = n5;
                continue;
            }
            while (bl && n3 < n5 && this.value.charAt(n3) == ' ') {
                ++n3;
            }
        }
        return n3;
    }

    public void moveCursor(int n) {
        this.moveCursorTo(this.getCursorPos(n));
    }

    private int getCursorPos(int n) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, n);
    }

    public void moveCursorTo(int n) {
        this.setCursorPosition(n);
        if (!this.shiftPressed) {
            this.setHighlightPos(this.cursorPos);
        }
        this.onValueChange(this.value);
    }

    public void setCursorPosition(int n) {
        this.cursorPos = Mth.clamp(n, 0, this.value.length());
    }

    public void moveCursorToStart() {
        this.moveCursorTo(0);
    }

    public void moveCursorToEnd() {
        this.moveCursorTo(this.value.length());
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (!this.canConsumeInput()) {
            return false;
        }
        this.shiftPressed = Screen.hasShiftDown();
        if (Screen.isSelectAll(n)) {
            this.moveCursorToEnd();
            this.setHighlightPos(0);
            return true;
        }
        if (Screen.isCopy(n)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            return true;
        }
        if (Screen.isPaste(n)) {
            if (this.isEditable) {
                this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            }
            return true;
        }
        if (Screen.isCut(n)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            if (this.isEditable) {
                this.insertText("");
            }
            return true;
        }
        switch (n) {
            case 263: {
                if (Screen.hasControlDown()) {
                    this.moveCursorTo(this.getWordPosition(-1));
                } else {
                    this.moveCursor(-1);
                }
                return true;
            }
            case 262: {
                if (Screen.hasControlDown()) {
                    this.moveCursorTo(this.getWordPosition(1));
                } else {
                    this.moveCursor(1);
                }
                return true;
            }
            case 259: {
                if (this.isEditable) {
                    this.shiftPressed = false;
                    this.deleteText(-1);
                    this.shiftPressed = Screen.hasShiftDown();
                }
                return true;
            }
            case 261: {
                if (this.isEditable) {
                    this.shiftPressed = false;
                    this.deleteText(1);
                    this.shiftPressed = Screen.hasShiftDown();
                }
                return true;
            }
            case 268: {
                this.moveCursorToStart();
                return true;
            }
            case 269: {
                this.moveCursorToEnd();
                return true;
            }
        }
        return false;
    }

    public boolean canConsumeInput() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    @Override
    public boolean charTyped(char c, int n) {
        if (!this.canConsumeInput()) {
            return false;
        }
        if (SharedConstants.isAllowedChatCharacter(c)) {
            if (this.isEditable) {
                this.insertText(Character.toString(c));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        boolean bl;
        if (!this.isVisible()) {
            return false;
        }
        boolean bl2 = bl = d >= (double)this.x && d < (double)(this.x + this.width) && d2 >= (double)this.y && d2 < (double)(this.y + this.height);
        if (this.canLoseFocus) {
            this.setFocus(bl);
        }
        if (this.isFocused() && bl && n == 0) {
            int n2 = Mth.floor(d) - this.x;
            if (this.bordered) {
                n2 -= 4;
            }
            String string = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            this.moveCursorTo(this.font.plainSubstrByWidth(string, n2).length() + this.displayPos);
            return true;
        }
        return false;
    }

    public void setFocus(boolean bl) {
        super.setFocused(bl);
    }

    @Override
    public void renderButton(PoseStack poseStack, int n, int n2, float f) {
        int n3;
        if (!this.isVisible()) {
            return;
        }
        if (this.isBordered()) {
            n3 = this.isFocused() ? -1 : -6250336;
            EditBox.fill(poseStack, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, n3);
            EditBox.fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
        }
        n3 = this.isEditable ? this.textColor : this.textColorUneditable;
        int n4 = this.cursorPos - this.displayPos;
        int n5 = this.highlightPos - this.displayPos;
        String string = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        boolean bl = n4 >= 0 && n4 <= string.length();
        boolean bl2 = this.isFocused() && this.frame / 6 % 2 == 0 && bl;
        int n6 = this.bordered ? this.x + 4 : this.x;
        int n7 = this.bordered ? this.y + (this.height - 8) / 2 : this.y;
        int n8 = n6;
        if (n5 > string.length()) {
            n5 = string.length();
        }
        if (!string.isEmpty()) {
            String string2 = bl ? string.substring(0, n4) : string;
            n8 = this.font.drawShadow(poseStack, this.formatter.apply(string2, this.displayPos), (float)n8, (float)n7, n3);
        }
        boolean bl3 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
        int n9 = n8;
        if (!bl) {
            n9 = n4 > 0 ? n6 + this.width : n6;
        } else if (bl3) {
            --n9;
            --n8;
        }
        if (!string.isEmpty() && bl && n4 < string.length()) {
            this.font.drawShadow(poseStack, this.formatter.apply(string.substring(n4), this.cursorPos), (float)n8, (float)n7, n3);
        }
        if (!bl3 && this.suggestion != null) {
            this.font.drawShadow(poseStack, this.suggestion, (float)(n9 - 1), (float)n7, -8355712);
        }
        if (bl2) {
            if (bl3) {
                this.font.getClass();
                GuiComponent.fill(poseStack, n9, n7 - 1, n9 + 1, n7 + 1 + 9, -3092272);
            } else {
                this.font.drawShadow(poseStack, "_", (float)n9, (float)n7, n3);
            }
        }
        if (n5 != n4) {
            int n10 = n6 + this.font.width(string.substring(0, n5));
            this.font.getClass();
            this.renderHighlight(n9, n7 - 1, n10 - 1, n7 + 1 + 9);
        }
    }

    private void renderHighlight(int n, int n2, int n3, int n4) {
        int n5;
        if (n < n3) {
            n5 = n;
            n = n3;
            n3 = n5;
        }
        if (n2 < n4) {
            n5 = n2;
            n2 = n4;
            n4 = n5;
        }
        if (n3 > this.x + this.width) {
            n3 = this.x + this.width;
        }
        if (n > this.x + this.width) {
            n = this.x + this.width;
        }
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION);
        bufferBuilder.vertex(n, n4, 0.0).endVertex();
        bufferBuilder.vertex(n3, n4, 0.0).endVertex();
        bufferBuilder.vertex(n3, n2, 0.0).endVertex();
        bufferBuilder.vertex(n, n2, 0.0).endVertex();
        tesselator.end();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    public void setMaxLength(int n) {
        this.maxLength = n;
        if (this.value.length() > n) {
            this.value = this.value.substring(0, n);
            this.onValueChange(this.value);
        }
    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    private boolean isBordered() {
        return this.bordered;
    }

    public void setBordered(boolean bl) {
        this.bordered = bl;
    }

    public void setTextColor(int n) {
        this.textColor = n;
    }

    public void setTextColorUneditable(int n) {
        this.textColorUneditable = n;
    }

    @Override
    public boolean changeFocus(boolean bl) {
        if (!this.visible || !this.isEditable) {
            return false;
        }
        return super.changeFocus(bl);
    }

    @Override
    public boolean isMouseOver(double d, double d2) {
        return this.visible && d >= (double)this.x && d < (double)(this.x + this.width) && d2 >= (double)this.y && d2 < (double)(this.y + this.height);
    }

    @Override
    protected void onFocusedChanged(boolean bl) {
        if (bl) {
            this.frame = 0;
        }
    }

    private boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean bl) {
        this.isEditable = bl;
    }

    public int getInnerWidth() {
        return this.isBordered() ? this.width - 8 : this.width;
    }

    public void setHighlightPos(int n) {
        int n2 = this.value.length();
        this.highlightPos = Mth.clamp(n, 0, n2);
        if (this.font != null) {
            if (this.displayPos > n2) {
                this.displayPos = n2;
            }
            int n3 = this.getInnerWidth();
            String string = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), n3);
            int n4 = string.length() + this.displayPos;
            if (this.highlightPos == this.displayPos) {
                this.displayPos -= this.font.plainSubstrByWidth(this.value, n3, true).length();
            }
            if (this.highlightPos > n4) {
                this.displayPos += this.highlightPos - n4;
            } else if (this.highlightPos <= this.displayPos) {
                this.displayPos -= this.displayPos - this.highlightPos;
            }
            this.displayPos = Mth.clamp(this.displayPos, 0, n2);
        }
    }

    public void setCanLoseFocus(boolean bl) {
        this.canLoseFocus = bl;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean bl) {
        this.visible = bl;
    }

    public void setSuggestion(@Nullable String string) {
        this.suggestion = string;
    }

    public int getScreenX(int n) {
        if (n > this.value.length()) {
            return this.x;
        }
        return this.x + this.font.width(this.value.substring(0, n));
    }

    public void setX(int n) {
        this.x = n;
    }
}

