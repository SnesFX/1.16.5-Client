/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.client.gui.screens.controls;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.commons.lang3.ArrayUtils;

public class ControlList
extends ContainerObjectSelectionList<Entry> {
    private final ControlsScreen controlsScreen;
    private int maxNameWidth;

    public ControlList(ControlsScreen controlsScreen, Minecraft minecraft) {
        super(minecraft, controlsScreen.width + 45, controlsScreen.height, 43, controlsScreen.height - 32, 20);
        this.controlsScreen = controlsScreen;
        Object[] arrobject = (KeyMapping[])ArrayUtils.clone((Object[])minecraft.options.keyMappings);
        Arrays.sort(arrobject);
        String string = null;
        for (Object object : arrobject) {
            TranslatableComponent translatableComponent;
            int n;
            String string2 = ((KeyMapping)object).getCategory();
            if (!string2.equals(string)) {
                string = string2;
                this.addEntry(new CategoryEntry(new TranslatableComponent(string2)));
            }
            if ((n = minecraft.font.width(translatableComponent = new TranslatableComponent(((KeyMapping)object).getName()))) > this.maxNameWidth) {
                this.maxNameWidth = n;
            }
            this.addEntry(new KeyEntry((KeyMapping)object, translatableComponent));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    static /* synthetic */ Minecraft access$100(ControlList controlList) {
        return controlList.minecraft;
    }

    static /* synthetic */ Minecraft access$200(ControlList controlList) {
        return controlList.minecraft;
    }

    static /* synthetic */ Minecraft access$300(ControlList controlList) {
        return controlList.minecraft;
    }

    static /* synthetic */ Minecraft access$400(ControlList controlList) {
        return controlList.minecraft;
    }

    static /* synthetic */ ControlsScreen access$500(ControlList controlList) {
        return controlList.controlsScreen;
    }

    static /* synthetic */ Minecraft access$700(ControlList controlList) {
        return controlList.minecraft;
    }

    static /* synthetic */ Minecraft access$800(ControlList controlList) {
        return controlList.minecraft;
    }

    static /* synthetic */ Minecraft access$900(ControlList controlList) {
        return controlList.minecraft;
    }

    static /* synthetic */ Minecraft access$1000(ControlList controlList) {
        return controlList.minecraft;
    }

    public class KeyEntry
    extends Entry {
        private final KeyMapping key;
        private final Component name;
        private final Button changeButton;
        private final Button resetButton;

        private KeyEntry(final KeyMapping keyMapping, final Component component) {
            this.key = keyMapping;
            this.name = component;
            this.changeButton = new Button(0, 0, 75, 20, component, button -> {
                ControlList.access$500((ControlList)ControlList.this).selectedKey = keyMapping;
            }){

                @Override
                protected MutableComponent createNarrationMessage() {
                    if (keyMapping.isUnbound()) {
                        return new TranslatableComponent("narrator.controls.unbound", component);
                    }
                    return new TranslatableComponent("narrator.controls.bound", component, super.createNarrationMessage());
                }
            };
            this.resetButton = new Button(0, 0, 50, 20, new TranslatableComponent("controls.reset"), button -> {
                ControlList.access$1000((ControlList)ControlList.this).options.setKey(keyMapping, keyMapping.getDefaultKey());
                KeyMapping.resetMapping();
            }){

                @Override
                protected MutableComponent createNarrationMessage() {
                    return new TranslatableComponent("narrator.controls.reset", component);
                }
            };
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            boolean bl2 = ControlList.access$500((ControlList)ControlList.this).selectedKey == this.key;
            ControlList.access$700((ControlList)ControlList.this).font.getClass();
            ControlList.access$800((ControlList)ControlList.this).font.draw(poseStack, this.name, (float)(n3 + 90 - ControlList.this.maxNameWidth), (float)(n2 + n5 / 2 - 9 / 2), 16777215);
            this.resetButton.x = n3 + 190;
            this.resetButton.y = n2;
            this.resetButton.active = !this.key.isDefault();
            this.resetButton.render(poseStack, n6, n7, f);
            this.changeButton.x = n3 + 105;
            this.changeButton.y = n2;
            this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
            boolean bl3 = false;
            if (!this.key.isUnbound()) {
                for (KeyMapping keyMapping : ControlList.access$900((ControlList)ControlList.this).options.keyMappings) {
                    if (keyMapping == this.key || !this.key.same(keyMapping)) continue;
                    bl3 = true;
                    break;
                }
            }
            if (bl2) {
                this.changeButton.setMessage(new TextComponent("> ").append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.YELLOW)).append(" <").withStyle(ChatFormatting.YELLOW));
            } else if (bl3) {
                this.changeButton.setMessage(this.changeButton.getMessage().copy().withStyle(ChatFormatting.RED));
            }
            this.changeButton.render(poseStack, n6, n7, f);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of((Object)this.changeButton, (Object)this.resetButton);
        }

        @Override
        public boolean mouseClicked(double d, double d2, int n) {
            if (this.changeButton.mouseClicked(d, d2, n)) {
                return true;
            }
            return this.resetButton.mouseClicked(d, d2, n);
        }

        @Override
        public boolean mouseReleased(double d, double d2, int n) {
            return this.changeButton.mouseReleased(d, d2, n) || this.resetButton.mouseReleased(d, d2, n);
        }

    }

    public class CategoryEntry
    extends Entry {
        private final Component name;
        private final int width;

        public CategoryEntry(Component component) {
            this.name = component;
            this.width = ControlList.access$100((ControlList)ControlList.this).font.width(this.name);
        }

        @Override
        public void render(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl, float f) {
            ControlList.access$300((ControlList)ControlList.this).font.getClass();
            ControlList.access$400((ControlList)ControlList.this).font.draw(poseStack, this.name, (float)(ControlList.access$200((ControlList)ControlList.this).screen.width / 2 - this.width / 2), (float)(n2 + n5 - 9 - 1), 16777215);
        }

        @Override
        public boolean changeFocus(boolean bl) {
            return false;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }
    }

    public static abstract class Entry
    extends ContainerObjectSelectionList.Entry<Entry> {
    }

}

