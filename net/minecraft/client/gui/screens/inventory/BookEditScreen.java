/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class BookEditScreen
extends Screen {
    private static final Component EDIT_TITLE_LABEL = new TranslatableComponent("book.editTitle");
    private static final Component FINALIZE_WARNING_LABEL = new TranslatableComponent("book.finalizeWarning");
    private static final FormattedCharSequence BLACK_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.BLACK));
    private static final FormattedCharSequence GRAY_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.GRAY));
    private final Player owner;
    private final ItemStack book;
    private boolean isModified;
    private boolean isSigning;
    private int frameTick;
    private int currentPage;
    private final List<String> pages = Lists.newArrayList();
    private String title = "";
    private final TextFieldHelper pageEdit = new TextFieldHelper(this::getCurrentPageText, this::setCurrentPageText, this::getClipboard, this::setClipboard, string -> string.length() < 1024 && this.font.wordWrapHeight((String)string, 114) <= 128);
    private final TextFieldHelper titleEdit = new TextFieldHelper(() -> this.title, string -> {
        this.title = string;
    }, this::getClipboard, this::setClipboard, string -> string.length() < 16);
    private long lastClickTime;
    private int lastIndex = -1;
    private PageButton forwardButton;
    private PageButton backButton;
    private Button doneButton;
    private Button signButton;
    private Button finalizeButton;
    private Button cancelButton;
    private final InteractionHand hand;
    @Nullable
    private DisplayCache displayCache = DisplayCache.access$000();
    private Component pageMsg = TextComponent.EMPTY;
    private final Component ownerText;

    public BookEditScreen(Player player, ItemStack itemStack, InteractionHand interactionHand) {
        super(NarratorChatListener.NO_TITLE);
        this.owner = player;
        this.book = itemStack;
        this.hand = interactionHand;
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null) {
            ListTag listTag = compoundTag.getList("pages", 8).copy();
            for (int i = 0; i < listTag.size(); ++i) {
                this.pages.add(listTag.getString(i));
            }
        }
        if (this.pages.isEmpty()) {
            this.pages.add("");
        }
        this.ownerText = new TranslatableComponent("book.byAuthor", player.getName()).withStyle(ChatFormatting.DARK_GRAY);
    }

    private void setClipboard(String string) {
        if (this.minecraft != null) {
            TextFieldHelper.setClipboardContents(this.minecraft, string);
        }
    }

    private String getClipboard() {
        return this.minecraft != null ? TextFieldHelper.getClipboardContents(this.minecraft) : "";
    }

    private int getNumPages() {
        return this.pages.size();
    }

    @Override
    public void tick() {
        super.tick();
        ++this.frameTick;
    }

    @Override
    protected void init() {
        this.clearDisplayCache();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.signButton = this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, new TranslatableComponent("book.signButton"), button -> {
            this.isSigning = true;
            this.updateButtonVisibility();
        }));
        this.doneButton = this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, CommonComponents.GUI_DONE, button -> {
            this.minecraft.setScreen(null);
            this.saveChanges(false);
        }));
        this.finalizeButton = this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, new TranslatableComponent("book.finalizeButton"), button -> {
            if (this.isSigning) {
                this.saveChanges(true);
                this.minecraft.setScreen(null);
            }
        }));
        this.cancelButton = this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, CommonComponents.GUI_CANCEL, button -> {
            if (this.isSigning) {
                this.isSigning = false;
            }
            this.updateButtonVisibility();
        }));
        int n = (this.width - 192) / 2;
        int n2 = 2;
        this.forwardButton = this.addButton(new PageButton(n + 116, 159, true, button -> this.pageForward(), true));
        this.backButton = this.addButton(new PageButton(n + 43, 159, false, button -> this.pageBack(), true));
        this.updateButtonVisibility();
    }

    private void pageBack() {
        if (this.currentPage > 0) {
            --this.currentPage;
        }
        this.updateButtonVisibility();
        this.clearDisplayCacheAfterPageChange();
    }

    private void pageForward() {
        if (this.currentPage < this.getNumPages() - 1) {
            ++this.currentPage;
        } else {
            this.appendPageToBook();
            if (this.currentPage < this.getNumPages() - 1) {
                ++this.currentPage;
            }
        }
        this.updateButtonVisibility();
        this.clearDisplayCacheAfterPageChange();
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    private void updateButtonVisibility() {
        this.backButton.visible = !this.isSigning && this.currentPage > 0;
        this.forwardButton.visible = !this.isSigning;
        this.doneButton.visible = !this.isSigning;
        this.signButton.visible = !this.isSigning;
        this.cancelButton.visible = this.isSigning;
        this.finalizeButton.visible = this.isSigning;
        this.finalizeButton.active = !this.title.trim().isEmpty();
    }

    private void eraseEmptyTrailingPages() {
        ListIterator<String> listIterator = this.pages.listIterator(this.pages.size());
        while (listIterator.hasPrevious() && listIterator.previous().isEmpty()) {
            listIterator.remove();
        }
    }

    private void saveChanges(boolean bl) {
        if (!this.isModified) {
            return;
        }
        this.eraseEmptyTrailingPages();
        ListTag listTag = new ListTag();
        this.pages.stream().map(StringTag::valueOf).forEach(listTag::add);
        if (!this.pages.isEmpty()) {
            this.book.addTagElement("pages", listTag);
        }
        if (bl) {
            this.book.addTagElement("author", StringTag.valueOf(this.owner.getGameProfile().getName()));
            this.book.addTagElement("title", StringTag.valueOf(this.title.trim()));
        }
        int n = this.hand == InteractionHand.MAIN_HAND ? this.owner.inventory.selected : 40;
        this.minecraft.getConnection().send(new ServerboundEditBookPacket(this.book, bl, n));
    }

    private void appendPageToBook() {
        if (this.getNumPages() >= 100) {
            return;
        }
        this.pages.add("");
        this.isModified = true;
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (super.keyPressed(n, n2, n3)) {
            return true;
        }
        if (this.isSigning) {
            return this.titleKeyPressed(n, n2, n3);
        }
        boolean bl = this.bookKeyPressed(n, n2, n3);
        if (bl) {
            this.clearDisplayCache();
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char c, int n) {
        if (super.charTyped(c, n)) {
            return true;
        }
        if (this.isSigning) {
            boolean bl = this.titleEdit.charTyped(c);
            if (bl) {
                this.updateButtonVisibility();
                this.isModified = true;
                return true;
            }
            return false;
        }
        if (SharedConstants.isAllowedChatCharacter(c)) {
            this.pageEdit.insertText(Character.toString(c));
            this.clearDisplayCache();
            return true;
        }
        return false;
    }

    private boolean bookKeyPressed(int n, int n2, int n3) {
        if (Screen.isSelectAll(n)) {
            this.pageEdit.selectAll();
            return true;
        }
        if (Screen.isCopy(n)) {
            this.pageEdit.copy();
            return true;
        }
        if (Screen.isPaste(n)) {
            this.pageEdit.paste();
            return true;
        }
        if (Screen.isCut(n)) {
            this.pageEdit.cut();
            return true;
        }
        switch (n) {
            case 259: {
                this.pageEdit.removeCharsFromCursor(-1);
                return true;
            }
            case 261: {
                this.pageEdit.removeCharsFromCursor(1);
                return true;
            }
            case 257: 
            case 335: {
                this.pageEdit.insertText("\n");
                return true;
            }
            case 263: {
                this.pageEdit.moveByChars(-1, Screen.hasShiftDown());
                return true;
            }
            case 262: {
                this.pageEdit.moveByChars(1, Screen.hasShiftDown());
                return true;
            }
            case 265: {
                this.keyUp();
                return true;
            }
            case 264: {
                this.keyDown();
                return true;
            }
            case 266: {
                this.backButton.onPress();
                return true;
            }
            case 267: {
                this.forwardButton.onPress();
                return true;
            }
            case 268: {
                this.keyHome();
                return true;
            }
            case 269: {
                this.keyEnd();
                return true;
            }
        }
        return false;
    }

    private void keyUp() {
        this.changeLine(-1);
    }

    private void keyDown() {
        this.changeLine(1);
    }

    private void changeLine(int n) {
        int n2 = this.pageEdit.getCursorPos();
        int n3 = this.getDisplayCache().changeLine(n2, n);
        this.pageEdit.setCursorPos(n3, Screen.hasShiftDown());
    }

    private void keyHome() {
        int n = this.pageEdit.getCursorPos();
        int n2 = this.getDisplayCache().findLineStart(n);
        this.pageEdit.setCursorPos(n2, Screen.hasShiftDown());
    }

    private void keyEnd() {
        DisplayCache displayCache = this.getDisplayCache();
        int n = this.pageEdit.getCursorPos();
        int n2 = displayCache.findLineEnd(n);
        this.pageEdit.setCursorPos(n2, Screen.hasShiftDown());
    }

    private boolean titleKeyPressed(int n, int n2, int n3) {
        switch (n) {
            case 259: {
                this.titleEdit.removeCharsFromCursor(-1);
                this.updateButtonVisibility();
                this.isModified = true;
                return true;
            }
            case 257: 
            case 335: {
                if (!this.title.isEmpty()) {
                    this.saveChanges(true);
                    this.minecraft.setScreen(null);
                }
                return true;
            }
        }
        return false;
    }

    private String getCurrentPageText() {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            return this.pages.get(this.currentPage);
        }
        return "";
    }

    private void setCurrentPageText(String string) {
        if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
            this.pages.set(this.currentPage, string);
            this.isModified = true;
            this.clearDisplayCache();
        }
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.renderBackground(poseStack);
        this.setFocused(null);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(BookViewScreen.BOOK_LOCATION);
        int n3 = (this.width - 192) / 2;
        int n4 = 2;
        this.blit(poseStack, n3, 2, 0, 0, 192, 192);
        if (this.isSigning) {
            boolean bl = this.frameTick / 6 % 2 == 0;
            FormattedCharSequence formattedCharSequence = FormattedCharSequence.composite(FormattedCharSequence.forward(this.title, Style.EMPTY), bl ? BLACK_CURSOR : GRAY_CURSOR);
            int n5 = this.font.width(EDIT_TITLE_LABEL);
            this.font.draw(poseStack, EDIT_TITLE_LABEL, (float)(n3 + 36 + (114 - n5) / 2), 34.0f, 0);
            int n6 = this.font.width(formattedCharSequence);
            this.font.draw(poseStack, formattedCharSequence, (float)(n3 + 36 + (114 - n6) / 2), 50.0f, 0);
            int n7 = this.font.width(this.ownerText);
            this.font.draw(poseStack, this.ownerText, (float)(n3 + 36 + (114 - n7) / 2), 60.0f, 0);
            this.font.drawWordWrap(FINALIZE_WARNING_LABEL, n3 + 36, 82, 114, 0);
        } else {
            int n8 = this.font.width(this.pageMsg);
            this.font.draw(poseStack, this.pageMsg, (float)(n3 - n8 + 192 - 44), 18.0f, 0);
            DisplayCache displayCache = this.getDisplayCache();
            for (LineInfo lineInfo : displayCache.lines) {
                this.font.draw(poseStack, lineInfo.asComponent, (float)lineInfo.x, (float)lineInfo.y, -16777216);
            }
            this.renderHighlight(displayCache.selection);
            this.renderCursor(poseStack, displayCache.cursor, displayCache.cursorAtEnd);
        }
        super.render(poseStack, n, n2, f);
    }

    private void renderCursor(PoseStack poseStack, Pos2i pos2i, boolean bl) {
        if (this.frameTick / 6 % 2 == 0) {
            pos2i = this.convertLocalToScreen(pos2i);
            if (!bl) {
                this.font.getClass();
                GuiComponent.fill(poseStack, pos2i.x, pos2i.y - 1, pos2i.x + 1, pos2i.y + 9, -16777216);
            } else {
                this.font.draw(poseStack, "_", (float)pos2i.x, (float)pos2i.y, 0);
            }
        }
    }

    private void renderHighlight(Rect2i[] arrrect2i) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferBuilder.begin(7, DefaultVertexFormat.POSITION);
        for (Rect2i rect2i : arrrect2i) {
            int n = rect2i.getX();
            int n2 = rect2i.getY();
            int n3 = n + rect2i.getWidth();
            int n4 = n2 + rect2i.getHeight();
            bufferBuilder.vertex(n, n4, 0.0).endVertex();
            bufferBuilder.vertex(n3, n4, 0.0).endVertex();
            bufferBuilder.vertex(n3, n2, 0.0).endVertex();
            bufferBuilder.vertex(n, n2, 0.0).endVertex();
        }
        tesselator.end();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    private Pos2i convertScreenToLocal(Pos2i pos2i) {
        return new Pos2i(pos2i.x - (this.width - 192) / 2 - 36, pos2i.y - 32);
    }

    private Pos2i convertLocalToScreen(Pos2i pos2i) {
        return new Pos2i(pos2i.x + (this.width - 192) / 2 + 36, pos2i.y + 32);
    }

    @Override
    public boolean mouseClicked(double d, double d2, int n) {
        if (super.mouseClicked(d, d2, n)) {
            return true;
        }
        if (n == 0) {
            long l = Util.getMillis();
            DisplayCache displayCache = this.getDisplayCache();
            int n2 = displayCache.getIndexAtPosition(this.font, this.convertScreenToLocal(new Pos2i((int)d, (int)d2)));
            if (n2 >= 0) {
                if (n2 == this.lastIndex && l - this.lastClickTime < 250L) {
                    if (!this.pageEdit.isSelecting()) {
                        this.selectWord(n2);
                    } else {
                        this.pageEdit.selectAll();
                    }
                } else {
                    this.pageEdit.setCursorPos(n2, Screen.hasShiftDown());
                }
                this.clearDisplayCache();
            }
            this.lastIndex = n2;
            this.lastClickTime = l;
        }
        return true;
    }

    private void selectWord(int n) {
        String string = this.getCurrentPageText();
        this.pageEdit.setSelectionRange(StringSplitter.getWordPosition(string, -1, n, false), StringSplitter.getWordPosition(string, 1, n, false));
    }

    @Override
    public boolean mouseDragged(double d, double d2, int n, double d3, double d4) {
        if (super.mouseDragged(d, d2, n, d3, d4)) {
            return true;
        }
        if (n == 0) {
            DisplayCache displayCache = this.getDisplayCache();
            int n2 = displayCache.getIndexAtPosition(this.font, this.convertScreenToLocal(new Pos2i((int)d, (int)d2)));
            this.pageEdit.setCursorPos(n2, true);
            this.clearDisplayCache();
        }
        return true;
    }

    private DisplayCache getDisplayCache() {
        if (this.displayCache == null) {
            this.displayCache = this.rebuildDisplayCache();
            this.pageMsg = new TranslatableComponent("book.pageIndicator", this.currentPage + 1, this.getNumPages());
        }
        return this.displayCache;
    }

    private void clearDisplayCache() {
        this.displayCache = null;
    }

    private void clearDisplayCacheAfterPageChange() {
        this.pageEdit.setCursorToEnd();
        this.clearDisplayCache();
    }

    private DisplayCache rebuildDisplayCache() {
        boolean bl;
        Pos2i pos2i;
        int n;
        String string = this.getCurrentPageText();
        if (string.isEmpty()) {
            return DisplayCache.EMPTY;
        }
        int n2 = this.pageEdit.getCursorPos();
        int n3 = this.pageEdit.getSelectionPos();
        IntArrayList intArrayList = new IntArrayList();
        ArrayList arrayList = Lists.newArrayList();
        MutableInt mutableInt = new MutableInt();
        MutableBoolean mutableBoolean = new MutableBoolean();
        StringSplitter stringSplitter = this.font.getSplitter();
        stringSplitter.splitLines(string, 114, Style.EMPTY, true, (arg_0, arg_1, arg_2) -> this.lambda$rebuildDisplayCache$10(mutableInt, string, mutableBoolean, (IntList)intArrayList, arrayList, arg_0, arg_1, arg_2));
        int[] arrn = intArrayList.toIntArray();
        boolean bl2 = bl = n2 == string.length();
        if (bl && mutableBoolean.isTrue()) {
            this.font.getClass();
            pos2i = new Pos2i(0, arrayList.size() * 9);
        } else {
            int n4 = BookEditScreen.findLineFromPos(arrn, n2);
            n = this.font.width(string.substring(arrn[n4], n2));
            this.font.getClass();
            pos2i = new Pos2i(n, n4 * 9);
        }
        ArrayList arrayList2 = Lists.newArrayList();
        if (n2 != n3) {
            int n5;
            n = Math.min(n2, n3);
            int n6 = Math.max(n2, n3);
            int n7 = BookEditScreen.findLineFromPos(arrn, n);
            if (n7 == (n5 = BookEditScreen.findLineFromPos(arrn, n6))) {
                this.font.getClass();
                int n8 = n7 * 9;
                int n9 = arrn[n7];
                arrayList2.add(this.createPartialLineSelection(string, stringSplitter, n, n6, n8, n9));
            } else {
                int n10 = n7 + 1 > arrn.length ? string.length() : arrn[n7 + 1];
                this.font.getClass();
                arrayList2.add(this.createPartialLineSelection(string, stringSplitter, n, n10, n7 * 9, arrn[n7]));
                for (int i = n7 + 1; i < n5; ++i) {
                    this.font.getClass();
                    int n11 = i * 9;
                    String string2 = string.substring(arrn[i], arrn[i + 1]);
                    int n12 = (int)stringSplitter.stringWidth(string2);
                    this.font.getClass();
                    arrayList2.add(this.createSelection(new Pos2i(0, n11), new Pos2i(n12, n11 + 9)));
                }
                this.font.getClass();
                arrayList2.add(this.createPartialLineSelection(string, stringSplitter, arrn[n5], n6, n5 * 9, arrn[n5]));
            }
        }
        return new DisplayCache(string, pos2i, bl, arrn, arrayList.toArray(new LineInfo[0]), arrayList2.toArray(new Rect2i[0]));
    }

    private static int findLineFromPos(int[] arrn, int n) {
        int n2 = Arrays.binarySearch(arrn, n);
        if (n2 < 0) {
            return -(n2 + 2);
        }
        return n2;
    }

    private Rect2i createPartialLineSelection(String string, StringSplitter stringSplitter, int n, int n2, int n3, int n4) {
        String string2 = string.substring(n4, n);
        String string3 = string.substring(n4, n2);
        Pos2i pos2i = new Pos2i((int)stringSplitter.stringWidth(string2), n3);
        this.font.getClass();
        Pos2i pos2i2 = new Pos2i((int)stringSplitter.stringWidth(string3), n3 + 9);
        return this.createSelection(pos2i, pos2i2);
    }

    private Rect2i createSelection(Pos2i pos2i, Pos2i pos2i2) {
        Pos2i pos2i3 = this.convertLocalToScreen(pos2i);
        Pos2i pos2i4 = this.convertLocalToScreen(pos2i2);
        int n = Math.min(pos2i3.x, pos2i4.x);
        int n2 = Math.max(pos2i3.x, pos2i4.x);
        int n3 = Math.min(pos2i3.y, pos2i4.y);
        int n4 = Math.max(pos2i3.y, pos2i4.y);
        return new Rect2i(n, n3, n2 - n, n4 - n3);
    }

    private /* synthetic */ void lambda$rebuildDisplayCache$10(MutableInt mutableInt, String string, MutableBoolean mutableBoolean, IntList intList, List list, Style style, int n, int n2) {
        int n3 = mutableInt.getAndIncrement();
        String string2 = string.substring(n, n2);
        mutableBoolean.setValue(string2.endsWith("\n"));
        String string3 = StringUtils.stripEnd((String)string2, (String)" \n");
        this.font.getClass();
        int n4 = n3 * 9;
        Pos2i pos2i = this.convertLocalToScreen(new Pos2i(0, n4));
        intList.add(n);
        list.add(new LineInfo(style, string3, pos2i.x, pos2i.y));
    }

    static class DisplayCache {
        private static final DisplayCache EMPTY = new DisplayCache("", new Pos2i(0, 0), true, new int[]{0}, new LineInfo[]{new LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
        private final String fullText;
        private final Pos2i cursor;
        private final boolean cursorAtEnd;
        private final int[] lineStarts;
        private final LineInfo[] lines;
        private final Rect2i[] selection;

        public DisplayCache(String string, Pos2i pos2i, boolean bl, int[] arrn, LineInfo[] arrlineInfo, Rect2i[] arrrect2i) {
            this.fullText = string;
            this.cursor = pos2i;
            this.cursorAtEnd = bl;
            this.lineStarts = arrn;
            this.lines = arrlineInfo;
            this.selection = arrrect2i;
        }

        public int getIndexAtPosition(Font font, Pos2i pos2i) {
            font.getClass();
            int n = pos2i.y / 9;
            if (n < 0) {
                return 0;
            }
            if (n >= this.lines.length) {
                return this.fullText.length();
            }
            LineInfo lineInfo = this.lines[n];
            return this.lineStarts[n] + font.getSplitter().plainIndexAtWidth(lineInfo.contents, pos2i.x, lineInfo.style);
        }

        public int changeLine(int n, int n2) {
            int n3;
            int n4 = BookEditScreen.findLineFromPos(this.lineStarts, n);
            int n5 = n4 + n2;
            if (0 <= n5 && n5 < this.lineStarts.length) {
                int n6 = n - this.lineStarts[n4];
                int n7 = this.lines[n5].contents.length();
                n3 = this.lineStarts[n5] + Math.min(n6, n7);
            } else {
                n3 = n;
            }
            return n3;
        }

        public int findLineStart(int n) {
            int n2 = BookEditScreen.findLineFromPos(this.lineStarts, n);
            return this.lineStarts[n2];
        }

        public int findLineEnd(int n) {
            int n2 = BookEditScreen.findLineFromPos(this.lineStarts, n);
            return this.lineStarts[n2] + this.lines[n2].contents.length();
        }
    }

    static class LineInfo {
        private final Style style;
        private final String contents;
        private final Component asComponent;
        private final int x;
        private final int y;

        public LineInfo(Style style, String string, int n, int n2) {
            this.style = style;
            this.contents = string;
            this.x = n;
            this.y = n2;
            this.asComponent = new TextComponent(string).setStyle(style);
        }
    }

    static class Pos2i {
        public final int x;
        public final int y;

        Pos2i(int n, int n2) {
            this.x = n;
            this.y = n2;
        }
    }

}

