/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.BooleanOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class LanguageSelectScreen
extends OptionsSubScreen {
    private static final Component WARNING_LABEL = new TextComponent("(").append(new TranslatableComponent("options.languageWarning")).append(")").withStyle(ChatFormatting.GRAY);
    private LanguageSelectionList packSelectionList;
    private final LanguageManager languageManager;
    private OptionButton forceUnicodeButton;
    private Button doneButton;

    public LanguageSelectScreen(Screen screen, Options options, LanguageManager languageManager) {
        super(screen, options, new TranslatableComponent("options.language"));
        this.languageManager = languageManager;
    }

    @Override
    protected void init() {
        this.packSelectionList = new LanguageSelectionList(this.minecraft);
        this.children.add(this.packSelectionList);
        this.forceUnicodeButton = this.addButton(new OptionButton(this.width / 2 - 155, this.height - 38, 150, 20, Option.FORCE_UNICODE_FONT, Option.FORCE_UNICODE_FONT.getMessage(this.options), button -> {
            Option.FORCE_UNICODE_FONT.toggle(this.options);
            this.options.save();
            button.setMessage(Option.FORCE_UNICODE_FONT.getMessage(this.options));
            this.minecraft.resizeDisplay();
        }));
        this.doneButton = this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 38, 150, 20, CommonComponents.GUI_DONE, button -> {
            LanguageSelectionList.Entry entry = (LanguageSelectionList.Entry)this.packSelectionList.getSelected();
            if (entry != null && !entry.language.getCode().equals(this.languageManager.getSelected().getCode())) {
                this.languageManager.setSelected(entry.language);
                this.options.languageCode = entry.language.getCode();
                this.minecraft.reloadResourcePacks();
                this.doneButton.setMessage(CommonComponents.GUI_DONE);
                this.forceUnicodeButton.setMessage(Option.FORCE_UNICODE_FONT.getMessage(this.options));
                this.options.save();
            }
            this.minecraft.setScreen(this.lastScreen);
        }));
        super.init();
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.packSelectionList.render(poseStack, n, n2, f);
        LanguageSelectScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 16, 16777215);
        LanguageSelectScreen.drawCenteredString(poseStack, this.font, WARNING_LABEL, this.width / 2, this.height - 56, 8421504);
        super.render(poseStack, n, n2, f);
    }

    class LanguageSelectionList
    extends ObjectSelectionList<Entry> {
        public LanguageSelectionList(Minecraft minecraft) {
            super(minecraft, LanguageSelectScreen.this.width, LanguageSelectScreen.this.height, 32, LanguageSelectScreen.this.height - 65 + 4, 18);
            for (LanguageInfo languageInfo : LanguageSelectScreen.this.languageManager.getLanguages()) {
                Entry entry = new Entry(languageInfo);
                this.addEntry(entry);
                if (!LanguageSelectScreen.this.languageManager.getSelected().getCode().equals(languageInfo.getCode())) continue;
                this.setSelected(entry);
            }
            if (this.getSelected() != null) {
                this.centerScrollOn(this.getSelected());
            }
        }

        @Override
        protected int getScrollbarPosition() {
            return super.getScrollbarPosition() + 20;
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            if (entry != null) {
                NarratorChatListener.INSTANCE.sayNow(new TranslatableComponent("narrator.select", entry.language).getString());
            }
        }

        @Override
        protected void renderBackground(PoseStack poseStack) {
            LanguageSelectScreen.this.renderBackground(poseStack);
        }

        @Override
        protected boolean isFocused() {
            return LanguageSelectScreen.this.getFocused() == this;
        }

        public class Entry
        extends ObjectSelectionList.Entry<Entry> {
            private final LanguageInfo language;

            public Entry(LanguageInfo languageInfo) {
                this.language = languageInfo;
            }

            @Override
            public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
                String string = this.language.toString();
                LanguageSelectScreen.this.font.drawShadow(poseStack, string, LanguageSelectionList.this.width / 2 - LanguageSelectScreen.this.font.width(string) / 2, n2 + 1, 16777215, true);
            }

            @Override
            public boolean mouseClicked(double d, double d2, int n) {
                if (n == 0) {
                    this.select();
                    return true;
                }
                return false;
            }

            private void select() {
                LanguageSelectionList.this.setSelected(this);
            }
        }

    }

}

