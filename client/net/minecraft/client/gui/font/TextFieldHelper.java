/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.font;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;

public class TextFieldHelper {
    private final Supplier<String> getMessageFn;
    private final Consumer<String> setMessageFn;
    private final Supplier<String> getClipboardFn;
    private final Consumer<String> setClipboardFn;
    private final Predicate<String> stringValidator;
    private int cursorPos;
    private int selectionPos;

    public TextFieldHelper(Supplier<String> supplier, Consumer<String> consumer, Supplier<String> supplier2, Consumer<String> consumer2, Predicate<String> predicate) {
        this.getMessageFn = supplier;
        this.setMessageFn = consumer;
        this.getClipboardFn = supplier2;
        this.setClipboardFn = consumer2;
        this.stringValidator = predicate;
        this.setCursorToEnd();
    }

    public static Supplier<String> createClipboardGetter(Minecraft minecraft) {
        return () -> TextFieldHelper.getClipboardContents(minecraft);
    }

    public static String getClipboardContents(Minecraft minecraft) {
        return ChatFormatting.stripFormatting(minecraft.keyboardHandler.getClipboard().replaceAll("\\r", ""));
    }

    public static Consumer<String> createClipboardSetter(Minecraft minecraft) {
        return string -> TextFieldHelper.setClipboardContents(minecraft, string);
    }

    public static void setClipboardContents(Minecraft minecraft, String string) {
        minecraft.keyboardHandler.setClipboard(string);
    }

    public boolean charTyped(char c) {
        if (SharedConstants.isAllowedChatCharacter(c)) {
            this.insertText(this.getMessageFn.get(), Character.toString(c));
        }
        return true;
    }

    public boolean keyPressed(int n) {
        if (Screen.isSelectAll(n)) {
            this.selectAll();
            return true;
        }
        if (Screen.isCopy(n)) {
            this.copy();
            return true;
        }
        if (Screen.isPaste(n)) {
            this.paste();
            return true;
        }
        if (Screen.isCut(n)) {
            this.cut();
            return true;
        }
        if (n == 259) {
            this.removeCharsFromCursor(-1);
            return true;
        }
        if (n == 261) {
            this.removeCharsFromCursor(1);
        } else {
            if (n == 263) {
                if (Screen.hasControlDown()) {
                    this.moveByWords(-1, Screen.hasShiftDown());
                } else {
                    this.moveByChars(-1, Screen.hasShiftDown());
                }
                return true;
            }
            if (n == 262) {
                if (Screen.hasControlDown()) {
                    this.moveByWords(1, Screen.hasShiftDown());
                } else {
                    this.moveByChars(1, Screen.hasShiftDown());
                }
                return true;
            }
            if (n == 268) {
                this.setCursorToStart(Screen.hasShiftDown());
                return true;
            }
            if (n == 269) {
                this.setCursorToEnd(Screen.hasShiftDown());
                return true;
            }
        }
        return false;
    }

    private int clampToMsgLength(int n) {
        return Mth.clamp(n, 0, this.getMessageFn.get().length());
    }

    private void insertText(String string, String string2) {
        if (this.selectionPos != this.cursorPos) {
            string = this.deleteSelection(string);
        }
        this.cursorPos = Mth.clamp(this.cursorPos, 0, string.length());
        String string3 = new StringBuilder(string).insert(this.cursorPos, string2).toString();
        if (this.stringValidator.test(string3)) {
            this.setMessageFn.accept(string3);
            this.selectionPos = this.cursorPos = Math.min(string3.length(), this.cursorPos + string2.length());
        }
    }

    public void insertText(String string) {
        this.insertText(this.getMessageFn.get(), string);
    }

    private void resetSelectionIfNeeded(boolean bl) {
        if (!bl) {
            this.selectionPos = this.cursorPos;
        }
    }

    public void moveByChars(int n, boolean bl) {
        this.cursorPos = Util.offsetByCodepoints(this.getMessageFn.get(), this.cursorPos, n);
        this.resetSelectionIfNeeded(bl);
    }

    public void moveByWords(int n, boolean bl) {
        this.cursorPos = StringSplitter.getWordPosition(this.getMessageFn.get(), n, this.cursorPos, true);
        this.resetSelectionIfNeeded(bl);
    }

    public void removeCharsFromCursor(int n) {
        String string = this.getMessageFn.get();
        if (!string.isEmpty()) {
            String string2;
            if (this.selectionPos != this.cursorPos) {
                string2 = this.deleteSelection(string);
            } else {
                int n2 = Util.offsetByCodepoints(string, this.cursorPos, n);
                int n3 = Math.min(n2, this.cursorPos);
                int n4 = Math.max(n2, this.cursorPos);
                string2 = new StringBuilder(string).delete(n3, n4).toString();
                if (n < 0) {
                    this.selectionPos = this.cursorPos = n3;
                }
            }
            this.setMessageFn.accept(string2);
        }
    }

    public void cut() {
        String string = this.getMessageFn.get();
        this.setClipboardFn.accept(this.getSelected(string));
        this.setMessageFn.accept(this.deleteSelection(string));
    }

    public void paste() {
        this.insertText(this.getMessageFn.get(), this.getClipboardFn.get());
        this.selectionPos = this.cursorPos;
    }

    public void copy() {
        this.setClipboardFn.accept(this.getSelected(this.getMessageFn.get()));
    }

    public void selectAll() {
        this.selectionPos = 0;
        this.cursorPos = this.getMessageFn.get().length();
    }

    private String getSelected(String string) {
        int n = Math.min(this.cursorPos, this.selectionPos);
        int n2 = Math.max(this.cursorPos, this.selectionPos);
        return string.substring(n, n2);
    }

    private String deleteSelection(String string) {
        if (this.selectionPos == this.cursorPos) {
            return string;
        }
        int n = Math.min(this.cursorPos, this.selectionPos);
        int n2 = Math.max(this.cursorPos, this.selectionPos);
        String string2 = string.substring(0, n) + string.substring(n2);
        this.selectionPos = this.cursorPos = n;
        return string2;
    }

    private void setCursorToStart(boolean bl) {
        this.cursorPos = 0;
        this.resetSelectionIfNeeded(bl);
    }

    public void setCursorToEnd() {
        this.setCursorToEnd(false);
    }

    private void setCursorToEnd(boolean bl) {
        this.cursorPos = this.getMessageFn.get().length();
        this.resetSelectionIfNeeded(bl);
    }

    public int getCursorPos() {
        return this.cursorPos;
    }

    public void setCursorPos(int n, boolean bl) {
        this.cursorPos = this.clampToMsgLength(n);
        this.resetSelectionIfNeeded(bl);
    }

    public int getSelectionPos() {
        return this.selectionPos;
    }

    public void setSelectionRange(int n, int n2) {
        int n3 = this.getMessageFn.get().length();
        this.cursorPos = Mth.clamp(n, 0, n3);
        this.selectionPos = Mth.clamp(n2, 0, n3);
    }

    public boolean isSelecting() {
        return this.cursorPos != this.selectionPos;
    }
}

