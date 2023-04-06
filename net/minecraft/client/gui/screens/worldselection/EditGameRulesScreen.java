/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameRules;

public class EditGameRulesScreen
extends Screen {
    private final Consumer<Optional<GameRules>> exitCallback;
    private RuleList rules;
    private final Set<RuleEntry> invalidEntries = Sets.newHashSet();
    private Button doneButton;
    @Nullable
    private List<FormattedCharSequence> tooltip;
    private final GameRules gameRules;

    public EditGameRulesScreen(GameRules gameRules, Consumer<Optional<GameRules>> consumer) {
        super(new TranslatableComponent("editGamerule.title"));
        this.gameRules = gameRules;
        this.exitCallback = consumer;
    }

    @Override
    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        super.init();
        this.rules = new RuleList(this.gameRules);
        this.children.add(this.rules);
        this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, CommonComponents.GUI_CANCEL, button -> this.exitCallback.accept(Optional.empty())));
        this.doneButton = this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, CommonComponents.GUI_DONE, button -> this.exitCallback.accept(Optional.of(this.gameRules))));
    }

    @Override
    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void onClose() {
        this.exitCallback.accept(Optional.empty());
    }

    @Override
    public void render(PoseStack poseStack, int n, int n2, float f) {
        this.tooltip = null;
        this.rules.render(poseStack, n, n2, f);
        EditGameRulesScreen.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, 16777215);
        super.render(poseStack, n, n2, f);
        if (this.tooltip != null) {
            this.renderTooltip(poseStack, this.tooltip, n, n2);
        }
    }

    private void setTooltip(@Nullable List<FormattedCharSequence> list) {
        this.tooltip = list;
    }

    private void updateDoneButton() {
        this.doneButton.active = this.invalidEntries.isEmpty();
    }

    private void markInvalid(RuleEntry ruleEntry) {
        this.invalidEntries.add(ruleEntry);
        this.updateDoneButton();
    }

    private void clearInvalid(RuleEntry ruleEntry) {
        this.invalidEntries.remove(ruleEntry);
        this.updateDoneButton();
    }

    static /* synthetic */ Minecraft access$000(EditGameRulesScreen editGameRulesScreen) {
        return editGameRulesScreen.minecraft;
    }

    static /* synthetic */ Minecraft access$100(EditGameRulesScreen editGameRulesScreen) {
        return editGameRulesScreen.minecraft;
    }

    static /* synthetic */ Minecraft access$200(EditGameRulesScreen editGameRulesScreen) {
        return editGameRulesScreen.minecraft;
    }

    static /* synthetic */ Minecraft access$300(EditGameRulesScreen editGameRulesScreen) {
        return editGameRulesScreen.minecraft;
    }

    static /* synthetic */ Minecraft access$400(EditGameRulesScreen editGameRulesScreen) {
        return editGameRulesScreen.minecraft;
    }

    static /* synthetic */ Minecraft access$500(EditGameRulesScreen editGameRulesScreen) {
        return editGameRulesScreen.minecraft;
    }

    public class RuleList
    extends ContainerObjectSelectionList<RuleEntry> {
        public RuleList(final GameRules gameRules) {
            super(EditGameRulesScreen.this.minecraft, EditGameRulesScreen.this.width, EditGameRulesScreen.this.height, 43, EditGameRulesScreen.this.height - 32, 24);
            final HashMap hashMap = Maps.newHashMap();
            GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor(){

                @Override
                public void visitBoolean(GameRules.Key<GameRules.BooleanValue> key, GameRules.Type<GameRules.BooleanValue> type) {
                    this.addEntry(key, (component, list, string, booleanValue) -> new BooleanRuleEntry(component, list, string, (GameRules.BooleanValue)booleanValue));
                }

                @Override
                public void visitInteger(GameRules.Key<GameRules.IntegerValue> key, GameRules.Type<GameRules.IntegerValue> type) {
                    this.addEntry(key, (component, list, string, integerValue) -> new IntegerRuleEntry(component, list, string, (GameRules.IntegerValue)integerValue));
                }

                private <T extends GameRules.Value<T>> void addEntry(GameRules.Key<T> key, EntryFactory<T> entryFactory) {
                    String string;
                    ImmutableList immutableList;
                    TranslatableComponent translatableComponent = new TranslatableComponent(key.getDescriptionId());
                    MutableComponent mutableComponent = new TextComponent(key.getId()).withStyle(ChatFormatting.YELLOW);
                    T t = gameRules.getRule(key);
                    String string2 = ((GameRules.Value)t).serialize();
                    MutableComponent mutableComponent2 = new TranslatableComponent("editGamerule.default", new TextComponent(string2)).withStyle(ChatFormatting.GRAY);
                    String string3 = key.getDescriptionId() + ".description";
                    if (I18n.exists(string3)) {
                        ImmutableList.Builder builder = ImmutableList.builder().add((Object)mutableComponent.getVisualOrderText());
                        TranslatableComponent translatableComponent2 = new TranslatableComponent(string3);
                        EditGameRulesScreen.this.font.split(translatableComponent2, 150).forEach(((ImmutableList.Builder)builder)::add);
                        immutableList = builder.add((Object)mutableComponent2.getVisualOrderText()).build();
                        string = translatableComponent2.getString() + "\n" + mutableComponent2.getString();
                    } else {
                        immutableList = ImmutableList.of((Object)mutableComponent.getVisualOrderText(), (Object)mutableComponent2.getVisualOrderText());
                        string = mutableComponent2.getString();
                    }
                    hashMap.computeIfAbsent(key.getCategory(), category -> Maps.newHashMap()).put(key, entryFactory.create(translatableComponent, (List<FormattedCharSequence>)immutableList, string, t));
                }
            });
            hashMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry2 -> {
                this.addEntry(new CategoryRuleEntry(new TranslatableComponent(((GameRules.Category)((Object)((Object)entry2.getKey()))).getDescriptionId()).withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW)));
                ((Map)entry2.getValue()).entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(GameRules.Key::getId))).forEach(entry -> this.addEntry((AbstractSelectionList.Entry)entry.getValue()));
            });
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, float f) {
            RuleEntry ruleEntry;
            super.render(poseStack, n, n2, f);
            if (this.isMouseOver(n, n2) && (ruleEntry = (RuleEntry)this.getEntryAtPosition(n, n2)) != null) {
                EditGameRulesScreen.this.setTooltip(ruleEntry.tooltip);
            }
        }

    }

    public class IntegerRuleEntry
    extends GameRuleEntry {
        private final EditBox input;

        public IntegerRuleEntry(Component component, List<FormattedCharSequence> list, String string2, GameRules.IntegerValue integerValue) {
            super(list, component);
            this.input = new EditBox(EditGameRulesScreen.access$500((EditGameRulesScreen)EditGameRulesScreen.this).font, 10, 5, 42, 20, component.copy().append("\n").append(string2).append("\n"));
            this.input.setValue(Integer.toString(integerValue.get()));
            this.input.setResponder(string -> {
                if (integerValue.tryDeserialize((String)string)) {
                    this.input.setTextColor(14737632);
                    EditGameRulesScreen.this.clearInvalid(this);
                } else {
                    this.input.setTextColor(16711680);
                    EditGameRulesScreen.this.markInvalid(this);
                }
            });
            this.children.add(this.input);
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            this.renderLabel(poseStack, n2, n3);
            this.input.x = n3 + n4 - 44;
            this.input.y = n2;
            this.input.render(poseStack, n6, n7, f);
        }
    }

    public class BooleanRuleEntry
    extends GameRuleEntry {
        private final Button checkbox;

        public BooleanRuleEntry(final Component component, List<FormattedCharSequence> list, final String string, final GameRules.BooleanValue booleanValue) {
            super(list, component);
            this.checkbox = new Button(10, 5, 44, 20, CommonComponents.optionStatus(booleanValue.get()), button -> {
                boolean bl = !booleanValue.get();
                booleanValue.set(bl, null);
                button.setMessage(CommonComponents.optionStatus(booleanValue.get()));
            }){

                @Override
                protected MutableComponent createNarrationMessage() {
                    return CommonComponents.optionStatus(component, booleanValue.get()).append("\n").append(string);
                }
            };
            this.children.add(this.checkbox);
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            this.renderLabel(poseStack, n2, n3);
            this.checkbox.x = n3 + n4 - 45;
            this.checkbox.y = n2;
            this.checkbox.render(poseStack, n6, n7, f);
        }

    }

    public abstract class GameRuleEntry
    extends RuleEntry {
        private final List<FormattedCharSequence> label;
        protected final List<GuiEventListener> children;

        public GameRuleEntry(@Nullable List<FormattedCharSequence> list, Component component) {
            super(list);
            this.children = Lists.newArrayList();
            this.label = EditGameRulesScreen.access$100((EditGameRulesScreen)EditGameRulesScreen.this).font.split(component, 175);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        protected void renderLabel(PoseStack poseStack, int n, int n2) {
            if (this.label.size() == 1) {
                EditGameRulesScreen.access$200((EditGameRulesScreen)EditGameRulesScreen.this).font.draw(poseStack, this.label.get(0), (float)n2, (float)(n + 5), 16777215);
            } else if (this.label.size() >= 2) {
                EditGameRulesScreen.access$300((EditGameRulesScreen)EditGameRulesScreen.this).font.draw(poseStack, this.label.get(0), (float)n2, (float)n, 16777215);
                EditGameRulesScreen.access$400((EditGameRulesScreen)EditGameRulesScreen.this).font.draw(poseStack, this.label.get(1), (float)n2, (float)(n + 10), 16777215);
            }
        }
    }

    @FunctionalInterface
    static interface EntryFactory<T extends GameRules.Value<T>> {
        public RuleEntry create(Component var1, List<FormattedCharSequence> var2, String var3, T var4);
    }

    public class CategoryRuleEntry
    extends RuleEntry {
        private final Component label;

        public CategoryRuleEntry(Component component) {
            super(null);
            this.label = component;
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            GuiComponent.drawCenteredString(poseStack, EditGameRulesScreen.access$000((EditGameRulesScreen)EditGameRulesScreen.this).font, this.label, n3 + n4 / 2, n2 + 5, 16777215);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of();
        }
    }

    public abstract class RuleEntry
    extends ContainerObjectSelectionList.Entry<RuleEntry> {
        @Nullable
        private final List<FormattedCharSequence> tooltip;

        public RuleEntry(@Nullable List<FormattedCharSequence> list) {
            this.tooltip = list;
        }
    }

}

