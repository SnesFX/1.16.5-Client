/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.context.CommandContextBuilder
 *  com.mojang.brigadier.context.ParsedArgument
 *  com.mojang.brigadier.context.StringRange
 *  com.mojang.brigadier.context.SuggestionContext
 *  com.mojang.brigadier.exceptions.BuiltInExceptionProvider
 *  com.mojang.brigadier.exceptions.CommandExceptionType
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestion
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class CommandSuggestions {
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
    private static final Style UNPARSED_STYLE = Style.EMPTY.withColor(ChatFormatting.RED);
    private static final Style LITERAL_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
    private static final List<Style> ARGUMENT_STYLES = (List)Stream.of(new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.LIGHT_PURPLE, ChatFormatting.GOLD}).map(Style.EMPTY::withColor).collect(ImmutableList.toImmutableList());
    private final Minecraft minecraft;
    private final Screen screen;
    private final EditBox input;
    private final Font font;
    private final boolean commandsOnly;
    private final boolean onlyShowIfCursorPastError;
    private final int lineStartOffset;
    private final int suggestionLineLimit;
    private final boolean anchorToBottom;
    private final int fillColor;
    private final List<FormattedCharSequence> commandUsage = Lists.newArrayList();
    private int commandUsagePosition;
    private int commandUsageWidth;
    private ParseResults<SharedSuggestionProvider> currentParse;
    private CompletableFuture<Suggestions> pendingSuggestions;
    private SuggestionsList suggestions;
    private boolean allowSuggestions;
    private boolean keepSuggestions;

    public CommandSuggestions(Minecraft minecraft, Screen screen, EditBox editBox, Font font, boolean bl, boolean bl2, int n, int n2, boolean bl3, int n3) {
        this.minecraft = minecraft;
        this.screen = screen;
        this.input = editBox;
        this.font = font;
        this.commandsOnly = bl;
        this.onlyShowIfCursorPastError = bl2;
        this.lineStartOffset = n;
        this.suggestionLineLimit = n2;
        this.anchorToBottom = bl3;
        this.fillColor = n3;
        editBox.setFormatter((arg_0, arg_1) -> this.formatChat(arg_0, arg_1));
    }

    public void setAllowSuggestions(boolean bl) {
        this.allowSuggestions = bl;
        if (!bl) {
            this.suggestions = null;
        }
    }

    public boolean keyPressed(int n, int n2, int n3) {
        if (this.suggestions != null && this.suggestions.keyPressed(n, n2, n3)) {
            return true;
        }
        if (this.screen.getFocused() == this.input && n == 258) {
            this.showSuggestions(true);
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double d) {
        return this.suggestions != null && this.suggestions.mouseScrolled(Mth.clamp(d, -1.0, 1.0));
    }

    public boolean mouseClicked(double d, double d2, int n) {
        return this.suggestions != null && this.suggestions.mouseClicked((int)d, (int)d2, n);
    }

    public void showSuggestions(boolean bl) {
        Suggestions suggestions;
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone() && !(suggestions = this.pendingSuggestions.join()).isEmpty()) {
            int n = 0;
            for (Suggestion suggestion : suggestions.getList()) {
                n = Math.max(n, this.font.width(suggestion.getText()));
            }
            int n2 = Mth.clamp(this.input.getScreenX(suggestions.getRange().getStart()), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - n);
            int n3 = this.anchorToBottom ? this.screen.height - 12 : 72;
            this.suggestions = new SuggestionsList(n2, n3, n, this.sortSuggestions(suggestions), bl);
        }
    }

    private List<Suggestion> sortSuggestions(Suggestions suggestions) {
        String string = this.input.getValue().substring(0, this.input.getCursorPosition());
        int n = CommandSuggestions.getLastWordIndex(string);
        String string2 = string.substring(n).toLowerCase(Locale.ROOT);
        ArrayList arrayList = Lists.newArrayList();
        ArrayList arrayList2 = Lists.newArrayList();
        for (Suggestion suggestion : suggestions.getList()) {
            if (suggestion.getText().startsWith(string2) || suggestion.getText().startsWith("minecraft:" + string2)) {
                arrayList.add(suggestion);
                continue;
            }
            arrayList2.add(suggestion);
        }
        arrayList.addAll(arrayList2);
        return arrayList;
    }

    public void updateCommandInfo() {
        boolean bl;
        String string = this.input.getValue();
        if (this.currentParse != null && !this.currentParse.getReader().getString().equals(string)) {
            this.currentParse = null;
        }
        if (!this.keepSuggestions) {
            this.input.setSuggestion(null);
            this.suggestions = null;
        }
        this.commandUsage.clear();
        StringReader stringReader = new StringReader(string);
        boolean bl2 = bl = stringReader.canRead() && stringReader.peek() == '/';
        if (bl) {
            stringReader.skip();
        }
        boolean bl3 = this.commandsOnly || bl;
        int n = this.input.getCursorPosition();
        if (bl3) {
            int n2;
            CommandDispatcher<SharedSuggestionProvider> commandDispatcher = this.minecraft.player.connection.getCommands();
            if (this.currentParse == null) {
                this.currentParse = commandDispatcher.parse(stringReader, (Object)this.minecraft.player.connection.getSuggestionsProvider());
            }
            int n3 = n2 = this.onlyShowIfCursorPastError ? stringReader.getCursor() : 1;
            if (!(n < n2 || this.suggestions != null && this.keepSuggestions)) {
                this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.currentParse, n);
                this.pendingSuggestions.thenRun(() -> {
                    if (!this.pendingSuggestions.isDone()) {
                        return;
                    }
                    this.updateUsageInfo();
                });
            }
        } else {
            String string2 = string.substring(0, n);
            int n4 = CommandSuggestions.getLastWordIndex(string2);
            Collection<String> collection = this.minecraft.player.connection.getSuggestionsProvider().getOnlinePlayerNames();
            this.pendingSuggestions = SharedSuggestionProvider.suggest(collection, new SuggestionsBuilder(string2, n4));
        }
    }

    private static int getLastWordIndex(String string) {
        if (Strings.isNullOrEmpty((String)string)) {
            return 0;
        }
        int n = 0;
        Matcher matcher = WHITESPACE_PATTERN.matcher(string);
        while (matcher.find()) {
            n = matcher.end();
        }
        return n;
    }

    private static FormattedCharSequence getExceptionMessage(CommandSyntaxException commandSyntaxException) {
        Component component = ComponentUtils.fromMessage(commandSyntaxException.getRawMessage());
        String string = commandSyntaxException.getContext();
        if (string == null) {
            return component.getVisualOrderText();
        }
        return new TranslatableComponent("command.context.parse_error", component, commandSyntaxException.getCursor(), string).getVisualOrderText();
    }

    private void updateUsageInfo() {
        if (this.input.getCursorPosition() == this.input.getValue().length()) {
            if (this.pendingSuggestions.join().isEmpty() && !this.currentParse.getExceptions().isEmpty()) {
                int n = 0;
                for (Map.Entry entry : this.currentParse.getExceptions().entrySet()) {
                    CommandSyntaxException commandSyntaxException = (CommandSyntaxException)((Object)entry.getValue());
                    if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                        ++n;
                        continue;
                    }
                    this.commandUsage.add(CommandSuggestions.getExceptionMessage(commandSyntaxException));
                }
                if (n > 0) {
                    this.commandUsage.add(CommandSuggestions.getExceptionMessage(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
                }
            } else if (this.currentParse.getReader().canRead()) {
                this.commandUsage.add(CommandSuggestions.getExceptionMessage(Commands.getParseException(this.currentParse)));
            }
        }
        this.commandUsagePosition = 0;
        this.commandUsageWidth = this.screen.width;
        if (this.commandUsage.isEmpty()) {
            this.fillNodeUsage(ChatFormatting.GRAY);
        }
        this.suggestions = null;
        if (this.allowSuggestions && this.minecraft.options.autoSuggestions) {
            this.showSuggestions(false);
        }
    }

    private void fillNodeUsage(ChatFormatting chatFormatting) {
        CommandContextBuilder commandContextBuilder = this.currentParse.getContext();
        SuggestionContext suggestionContext = commandContextBuilder.findSuggestionContext(this.input.getCursorPosition());
        Map map = this.minecraft.player.connection.getCommands().getSmartUsage(suggestionContext.parent, (Object)this.minecraft.player.connection.getSuggestionsProvider());
        ArrayList arrayList = Lists.newArrayList();
        int n = 0;
        Style style = Style.EMPTY.withColor(chatFormatting);
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getKey() instanceof LiteralCommandNode) continue;
            arrayList.add(FormattedCharSequence.forward((String)entry.getValue(), style));
            n = Math.max(n, this.font.width((String)entry.getValue()));
        }
        if (!arrayList.isEmpty()) {
            this.commandUsage.addAll(arrayList);
            this.commandUsagePosition = Mth.clamp(this.input.getScreenX(suggestionContext.startPos), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - n);
            this.commandUsageWidth = n;
        }
    }

    private FormattedCharSequence formatChat(String string, int n) {
        if (this.currentParse != null) {
            return CommandSuggestions.formatText(this.currentParse, string, n);
        }
        return FormattedCharSequence.forward(string, Style.EMPTY);
    }

    @Nullable
    private static String calculateSuggestionSuffix(String string, String string2) {
        if (string2.startsWith(string)) {
            return string2.substring(string.length());
        }
        return null;
    }

    private static FormattedCharSequence formatText(ParseResults<SharedSuggestionProvider> parseResults, String string, int n) {
        int n2;
        ArrayList arrayList = Lists.newArrayList();
        int n3 = 0;
        int n4 = -1;
        CommandContextBuilder commandContextBuilder = parseResults.getContext().getLastChild();
        for (ParsedArgument parsedArgument : commandContextBuilder.getArguments().values()) {
            int n5;
            if (++n4 >= ARGUMENT_STYLES.size()) {
                n4 = 0;
            }
            if ((n5 = Math.max(parsedArgument.getRange().getStart() - n, 0)) >= string.length()) break;
            int n6 = Math.min(parsedArgument.getRange().getEnd() - n, string.length());
            if (n6 <= 0) continue;
            arrayList.add(FormattedCharSequence.forward(string.substring(n3, n5), LITERAL_STYLE));
            arrayList.add(FormattedCharSequence.forward(string.substring(n5, n6), ARGUMENT_STYLES.get(n4)));
            n3 = n6;
        }
        if (parseResults.getReader().canRead() && (n2 = Math.max(parseResults.getReader().getCursor() - n, 0)) < string.length()) {
            int n7 = Math.min(n2 + parseResults.getReader().getRemainingLength(), string.length());
            arrayList.add(FormattedCharSequence.forward(string.substring(n3, n2), LITERAL_STYLE));
            arrayList.add(FormattedCharSequence.forward(string.substring(n2, n7), UNPARSED_STYLE));
            n3 = n7;
        }
        arrayList.add(FormattedCharSequence.forward(string.substring(n3), LITERAL_STYLE));
        return FormattedCharSequence.composite(arrayList);
    }

    public void render(PoseStack poseStack, int n, int n2) {
        if (this.suggestions != null) {
            this.suggestions.render(poseStack, n, n2);
        } else {
            int n3 = 0;
            for (FormattedCharSequence formattedCharSequence : this.commandUsage) {
                int n4 = this.anchorToBottom ? this.screen.height - 14 - 13 - 12 * n3 : 72 + 12 * n3;
                GuiComponent.fill(poseStack, this.commandUsagePosition - 1, n4, this.commandUsagePosition + this.commandUsageWidth + 1, n4 + 12, this.fillColor);
                this.font.drawShadow(poseStack, formattedCharSequence, (float)this.commandUsagePosition, (float)(n4 + 2), -1);
                ++n3;
            }
        }
    }

    public String getNarrationMessage() {
        if (this.suggestions != null) {
            return "\n" + this.suggestions.getNarrationMessage();
        }
        return "";
    }

    public class SuggestionsList {
        private final Rect2i rect;
        private final String originalContents;
        private final List<Suggestion> suggestionList;
        private int offset;
        private int current;
        private Vec2 lastMouse = Vec2.ZERO;
        private boolean tabCycles;
        private int lastNarratedEntry;

        private SuggestionsList(int n, int n2, int n3, List<Suggestion> list, boolean bl) {
            int n4 = n - 1;
            int n5 = CommandSuggestions.this.anchorToBottom ? n2 - 3 - Math.min(list.size(), CommandSuggestions.this.suggestionLineLimit) * 12 : n2;
            this.rect = new Rect2i(n4, n5, n3 + 1, Math.min(list.size(), CommandSuggestions.this.suggestionLineLimit) * 12);
            this.originalContents = CommandSuggestions.this.input.getValue();
            this.lastNarratedEntry = bl ? -1 : 0;
            this.suggestionList = list;
            this.select(0);
        }

        public void render(PoseStack poseStack, int n, int n2) {
            Message message;
            int n3;
            boolean bl;
            int n4 = Math.min(this.suggestionList.size(), CommandSuggestions.this.suggestionLineLimit);
            int n5 = -5592406;
            boolean bl2 = this.offset > 0;
            boolean bl3 = this.suggestionList.size() > this.offset + n4;
            boolean bl4 = bl2 || bl3;
            boolean bl5 = bl = this.lastMouse.x != (float)n || this.lastMouse.y != (float)n2;
            if (bl) {
                this.lastMouse = new Vec2(n, n2);
            }
            if (bl4) {
                GuiComponent.fill(poseStack, this.rect.getX(), this.rect.getY() - 1, this.rect.getX() + this.rect.getWidth(), this.rect.getY(), CommandSuggestions.this.fillColor);
                GuiComponent.fill(poseStack, this.rect.getX(), this.rect.getY() + this.rect.getHeight(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight() + 1, CommandSuggestions.this.fillColor);
                if (bl2) {
                    for (n3 = 0; n3 < this.rect.getWidth(); ++n3) {
                        if (n3 % 2 != 0) continue;
                        GuiComponent.fill(poseStack, this.rect.getX() + n3, this.rect.getY() - 1, this.rect.getX() + n3 + 1, this.rect.getY(), -1);
                    }
                }
                if (bl3) {
                    for (n3 = 0; n3 < this.rect.getWidth(); ++n3) {
                        if (n3 % 2 != 0) continue;
                        GuiComponent.fill(poseStack, this.rect.getX() + n3, this.rect.getY() + this.rect.getHeight(), this.rect.getX() + n3 + 1, this.rect.getY() + this.rect.getHeight() + 1, -1);
                    }
                }
            }
            n3 = 0;
            for (int i = 0; i < n4; ++i) {
                Suggestion suggestion = this.suggestionList.get(i + this.offset);
                GuiComponent.fill(poseStack, this.rect.getX(), this.rect.getY() + 12 * i, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * i + 12, CommandSuggestions.this.fillColor);
                if (n > this.rect.getX() && n < this.rect.getX() + this.rect.getWidth() && n2 > this.rect.getY() + 12 * i && n2 < this.rect.getY() + 12 * i + 12) {
                    if (bl) {
                        this.select(i + this.offset);
                    }
                    n3 = 1;
                }
                CommandSuggestions.this.font.drawShadow(poseStack, suggestion.getText(), (float)(this.rect.getX() + 1), (float)(this.rect.getY() + 2 + 12 * i), i + this.offset == this.current ? -256 : -5592406);
            }
            if (n3 != 0 && (message = this.suggestionList.get(this.current).getTooltip()) != null) {
                CommandSuggestions.this.screen.renderTooltip(poseStack, ComponentUtils.fromMessage(message), n, n2);
            }
        }

        public boolean mouseClicked(int n, int n2, int n3) {
            if (!this.rect.contains(n, n2)) {
                return false;
            }
            int n4 = (n2 - this.rect.getY()) / 12 + this.offset;
            if (n4 >= 0 && n4 < this.suggestionList.size()) {
                this.select(n4);
                this.useSuggestion();
            }
            return true;
        }

        public boolean mouseScrolled(double d) {
            int n;
            int n2 = (int)(CommandSuggestions.access$800((CommandSuggestions)CommandSuggestions.this).mouseHandler.xpos() * (double)CommandSuggestions.this.minecraft.getWindow().getGuiScaledWidth() / (double)CommandSuggestions.this.minecraft.getWindow().getScreenWidth());
            if (this.rect.contains(n2, n = (int)(CommandSuggestions.access$800((CommandSuggestions)CommandSuggestions.this).mouseHandler.ypos() * (double)CommandSuggestions.this.minecraft.getWindow().getGuiScaledHeight() / (double)CommandSuggestions.this.minecraft.getWindow().getScreenHeight()))) {
                this.offset = Mth.clamp((int)((double)this.offset - d), 0, Math.max(this.suggestionList.size() - CommandSuggestions.this.suggestionLineLimit, 0));
                return true;
            }
            return false;
        }

        public boolean keyPressed(int n, int n2, int n3) {
            if (n == 265) {
                this.cycle(-1);
                this.tabCycles = false;
                return true;
            }
            if (n == 264) {
                this.cycle(1);
                this.tabCycles = false;
                return true;
            }
            if (n == 258) {
                if (this.tabCycles) {
                    this.cycle(Screen.hasShiftDown() ? -1 : 1);
                }
                this.useSuggestion();
                return true;
            }
            if (n == 256) {
                this.hide();
                return true;
            }
            return false;
        }

        public void cycle(int n) {
            this.select(this.current + n);
            int n2 = this.offset;
            int n3 = this.offset + CommandSuggestions.this.suggestionLineLimit - 1;
            if (this.current < n2) {
                this.offset = Mth.clamp(this.current, 0, Math.max(this.suggestionList.size() - CommandSuggestions.this.suggestionLineLimit, 0));
            } else if (this.current > n3) {
                this.offset = Mth.clamp(this.current + CommandSuggestions.this.lineStartOffset - CommandSuggestions.this.suggestionLineLimit, 0, Math.max(this.suggestionList.size() - CommandSuggestions.this.suggestionLineLimit, 0));
            }
        }

        public void select(int n) {
            this.current = n;
            if (this.current < 0) {
                this.current += this.suggestionList.size();
            }
            if (this.current >= this.suggestionList.size()) {
                this.current -= this.suggestionList.size();
            }
            Suggestion suggestion = this.suggestionList.get(this.current);
            CommandSuggestions.this.input.setSuggestion(CommandSuggestions.calculateSuggestionSuffix(CommandSuggestions.this.input.getValue(), suggestion.apply(this.originalContents)));
            if (NarratorChatListener.INSTANCE.isActive() && this.lastNarratedEntry != this.current) {
                NarratorChatListener.INSTANCE.sayNow(this.getNarrationMessage());
            }
        }

        public void useSuggestion() {
            Suggestion suggestion = this.suggestionList.get(this.current);
            CommandSuggestions.this.keepSuggestions = true;
            CommandSuggestions.this.input.setValue(suggestion.apply(this.originalContents));
            int n = suggestion.getRange().getStart() + suggestion.getText().length();
            CommandSuggestions.this.input.setCursorPosition(n);
            CommandSuggestions.this.input.setHighlightPos(n);
            this.select(this.current);
            CommandSuggestions.this.keepSuggestions = false;
            this.tabCycles = true;
        }

        private String getNarrationMessage() {
            this.lastNarratedEntry = this.current;
            Suggestion suggestion = this.suggestionList.get(this.current);
            Message message = suggestion.getTooltip();
            if (message != null) {
                return I18n.get("narration.suggestion.tooltip", this.current + 1, this.suggestionList.size(), suggestion.getText(), message.getString());
            }
            return I18n.get("narration.suggestion", this.current + 1, this.suggestionList.size(), suggestion.getText());
        }

        public void hide() {
            CommandSuggestions.this.suggestions = null;
        }
    }

}

