/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.ToDoubleFunction;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class AdvancementWidget
extends GuiComponent {
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    private final AdvancementTab tab;
    private final Advancement advancement;
    private final DisplayInfo display;
    private final FormattedCharSequence title;
    private final int width;
    private final List<FormattedCharSequence> description;
    private final Minecraft minecraft;
    private AdvancementWidget parent;
    private final List<AdvancementWidget> children = Lists.newArrayList();
    private AdvancementProgress progress;
    private final int x;
    private final int y;

    public AdvancementWidget(AdvancementTab advancementTab, Minecraft minecraft, Advancement advancement, DisplayInfo displayInfo) {
        this.tab = advancementTab;
        this.advancement = advancement;
        this.display = displayInfo;
        this.minecraft = minecraft;
        this.title = Language.getInstance().getVisualOrder(minecraft.font.substrByWidth(displayInfo.getTitle(), 163));
        this.x = Mth.floor(displayInfo.getX() * 28.0f);
        this.y = Mth.floor(displayInfo.getY() * 27.0f);
        int n = advancement.getMaxCriteraRequired();
        int n2 = String.valueOf(n).length();
        int n3 = n > 1 ? minecraft.font.width("  ") + minecraft.font.width("0") * n2 * 2 + minecraft.font.width("/") : 0;
        int n4 = 29 + minecraft.font.width(this.title) + n3;
        this.description = Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(displayInfo.getDescription().copy(), Style.EMPTY.withColor(displayInfo.getFrame().getChatColor())), n4));
        for (FormattedCharSequence formattedCharSequence : this.description) {
            n4 = Math.max(n4, minecraft.font.width(formattedCharSequence));
        }
        this.width = n4 + 3 + 5;
    }

    private static float getMaxWidth(StringSplitter stringSplitter, List<FormattedText> list) {
        return (float)list.stream().mapToDouble(stringSplitter::stringWidth).max().orElse(0.0);
    }

    private List<FormattedText> findOptimalLines(Component component, int n) {
        StringSplitter stringSplitter = this.minecraft.font.getSplitter();
        List<FormattedText> list = null;
        float f = Float.MAX_VALUE;
        for (int n2 : TEST_SPLIT_OFFSETS) {
            List<FormattedText> list2 = stringSplitter.splitLines(component, n - n2, Style.EMPTY);
            float f2 = Math.abs(AdvancementWidget.getMaxWidth(stringSplitter, list2) - (float)n);
            if (f2 <= 10.0f) {
                return list2;
            }
            if (!(f2 < f)) continue;
            f = f2;
            list = list2;
        }
        return list;
    }

    @Nullable
    private AdvancementWidget getFirstVisibleParent(Advancement advancement) {
        while ((advancement = advancement.getParent()) != null && advancement.getDisplay() == null) {
        }
        if (advancement == null || advancement.getDisplay() == null) {
            return null;
        }
        return this.tab.getWidget(advancement);
    }

    public void drawConnectivity(PoseStack poseStack, int n, int n2, boolean bl) {
        if (this.parent != null) {
            int n3;
            int n4 = n + this.parent.x + 13;
            int n5 = n + this.parent.x + 26 + 4;
            int n6 = n2 + this.parent.y + 13;
            int n7 = n + this.x + 13;
            int n8 = n2 + this.y + 13;
            int n9 = n3 = bl ? -16777216 : -1;
            if (bl) {
                this.hLine(poseStack, n5, n4, n6 - 1, n3);
                this.hLine(poseStack, n5 + 1, n4, n6, n3);
                this.hLine(poseStack, n5, n4, n6 + 1, n3);
                this.hLine(poseStack, n7, n5 - 1, n8 - 1, n3);
                this.hLine(poseStack, n7, n5 - 1, n8, n3);
                this.hLine(poseStack, n7, n5 - 1, n8 + 1, n3);
                this.vLine(poseStack, n5 - 1, n8, n6, n3);
                this.vLine(poseStack, n5 + 1, n8, n6, n3);
            } else {
                this.hLine(poseStack, n5, n4, n6, n3);
                this.hLine(poseStack, n7, n5, n8, n3);
                this.vLine(poseStack, n5, n8, n6, n3);
            }
        }
        for (AdvancementWidget advancementWidget : this.children) {
            advancementWidget.drawConnectivity(poseStack, n, n2, bl);
        }
    }

    /*
     * WARNING - void declaration
     */
    public void draw(PoseStack poseStack, int n, int n2) {
        if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
            void var5_8;
            float f;
            float f2 = f = this.progress == null ? 0.0f : this.progress.getPercent();
            if (f >= 1.0f) {
                AdvancementWidgetType object = AdvancementWidgetType.OBTAINED;
            } else {
                AdvancementWidgetType advancementWidgetType = AdvancementWidgetType.UNOBTAINED;
            }
            this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
            this.blit(poseStack, n + this.x + 3, n2 + this.y, this.display.getFrame().getTexture(), 128 + var5_8.getIndex() * 26, 26, 26);
            this.minecraft.getItemRenderer().renderAndDecorateFakeItem(this.display.getIcon(), n + this.x + 8, n2 + this.y + 5);
        }
        for (AdvancementWidget advancementWidget : this.children) {
            advancementWidget.draw(poseStack, n, n2);
        }
    }

    public void setProgress(AdvancementProgress advancementProgress) {
        this.progress = advancementProgress;
    }

    public void addChild(AdvancementWidget advancementWidget) {
        this.children.add(advancementWidget);
    }

    public void drawHover(PoseStack poseStack, int n, int n2, float f, int n3, int n4) {
        AdvancementWidgetType advancementWidgetType;
        AdvancementWidgetType advancementWidgetType2;
        AdvancementWidgetType advancementWidgetType3;
        boolean bl = n3 + n + this.x + this.width + 26 >= this.tab.getScreen().width;
        String string = this.progress == null ? null : this.progress.getProgressText();
        int n5 = string == null ? 0 : this.minecraft.font.width(string);
        this.minecraft.font.getClass();
        boolean bl2 = 113 - n2 - this.y - 26 <= 6 + this.description.size() * 9;
        float f2 = this.progress == null ? 0.0f : this.progress.getPercent();
        int n6 = Mth.floor(f2 * (float)this.width);
        if (f2 >= 1.0f) {
            n6 = this.width / 2;
            advancementWidgetType3 = AdvancementWidgetType.OBTAINED;
            advancementWidgetType2 = AdvancementWidgetType.OBTAINED;
            advancementWidgetType = AdvancementWidgetType.OBTAINED;
        } else if (n6 < 2) {
            n6 = this.width / 2;
            advancementWidgetType3 = AdvancementWidgetType.UNOBTAINED;
            advancementWidgetType2 = AdvancementWidgetType.UNOBTAINED;
            advancementWidgetType = AdvancementWidgetType.UNOBTAINED;
        } else if (n6 > this.width - 2) {
            n6 = this.width / 2;
            advancementWidgetType3 = AdvancementWidgetType.OBTAINED;
            advancementWidgetType2 = AdvancementWidgetType.OBTAINED;
            advancementWidgetType = AdvancementWidgetType.UNOBTAINED;
        } else {
            advancementWidgetType3 = AdvancementWidgetType.OBTAINED;
            advancementWidgetType2 = AdvancementWidgetType.UNOBTAINED;
            advancementWidgetType = AdvancementWidgetType.UNOBTAINED;
        }
        int n7 = this.width - n6;
        this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        int n8 = n2 + this.y;
        int n9 = bl ? n + this.x - this.width + 26 + 6 : n + this.x;
        this.minecraft.font.getClass();
        int n10 = 32 + this.description.size() * 9;
        if (!this.description.isEmpty()) {
            if (bl2) {
                this.render9Sprite(poseStack, n9, n8 + 26 - n10, this.width, n10, 10, 200, 26, 0, 52);
            } else {
                this.render9Sprite(poseStack, n9, n8, this.width, n10, 10, 200, 26, 0, 52);
            }
        }
        this.blit(poseStack, n9, n8, 0, advancementWidgetType3.getIndex() * 26, n6, 26);
        this.blit(poseStack, n9 + n6, n8, 200 - n7, advancementWidgetType2.getIndex() * 26, n7, 26);
        this.blit(poseStack, n + this.x + 3, n2 + this.y, this.display.getFrame().getTexture(), 128 + advancementWidgetType.getIndex() * 26, 26, 26);
        if (bl) {
            this.minecraft.font.drawShadow(poseStack, this.title, (float)(n9 + 5), (float)(n2 + this.y + 9), -1);
            if (string != null) {
                this.minecraft.font.drawShadow(poseStack, string, (float)(n + this.x - n5), (float)(n2 + this.y + 9), -1);
            }
        } else {
            this.minecraft.font.drawShadow(poseStack, this.title, (float)(n + this.x + 32), (float)(n2 + this.y + 9), -1);
            if (string != null) {
                this.minecraft.font.drawShadow(poseStack, string, (float)(n + this.x + this.width - n5 - 5), (float)(n2 + this.y + 9), -1);
            }
        }
        if (bl2) {
            for (int i = 0; i < this.description.size(); ++i) {
                this.minecraft.font.getClass();
                this.minecraft.font.draw(poseStack, this.description.get(i), (float)(n9 + 5), (float)(n8 + 26 - n10 + 7 + i * 9), -5592406);
            }
        } else {
            for (int i = 0; i < this.description.size(); ++i) {
                this.minecraft.font.getClass();
                this.minecraft.font.draw(poseStack, this.description.get(i), (float)(n9 + 5), (float)(n2 + this.y + 9 + 17 + i * 9), -5592406);
            }
        }
        this.minecraft.getItemRenderer().renderAndDecorateFakeItem(this.display.getIcon(), n + this.x + 8, n2 + this.y + 5);
    }

    protected void render9Sprite(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9) {
        this.blit(poseStack, n, n2, n8, n9, n5, n5);
        this.renderRepeating(poseStack, n + n5, n2, n3 - n5 - n5, n5, n8 + n5, n9, n6 - n5 - n5, n7);
        this.blit(poseStack, n + n3 - n5, n2, n8 + n6 - n5, n9, n5, n5);
        this.blit(poseStack, n, n2 + n4 - n5, n8, n9 + n7 - n5, n5, n5);
        this.renderRepeating(poseStack, n + n5, n2 + n4 - n5, n3 - n5 - n5, n5, n8 + n5, n9 + n7 - n5, n6 - n5 - n5, n7);
        this.blit(poseStack, n + n3 - n5, n2 + n4 - n5, n8 + n6 - n5, n9 + n7 - n5, n5, n5);
        this.renderRepeating(poseStack, n, n2 + n5, n5, n4 - n5 - n5, n8, n9 + n5, n6, n7 - n5 - n5);
        this.renderRepeating(poseStack, n + n5, n2 + n5, n3 - n5 - n5, n4 - n5 - n5, n8 + n5, n9 + n5, n6 - n5 - n5, n7 - n5 - n5);
        this.renderRepeating(poseStack, n + n3 - n5, n2 + n5, n5, n4 - n5 - n5, n8 + n6 - n5, n9 + n5, n6, n7 - n5 - n5);
    }

    protected void renderRepeating(PoseStack poseStack, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        for (int i = 0; i < n3; i += n7) {
            int n9 = n + i;
            int n10 = Math.min(n7, n3 - i);
            for (int j = 0; j < n4; j += n8) {
                int n11 = n2 + j;
                int n12 = Math.min(n8, n4 - j);
                this.blit(poseStack, n9, n11, n5, n6, n10, n12);
            }
        }
    }

    public boolean isMouseOver(int n, int n2, int n3, int n4) {
        if (this.display.isHidden() && (this.progress == null || !this.progress.isDone())) {
            return false;
        }
        int n5 = n + this.x;
        int n6 = n5 + 26;
        int n7 = n2 + this.y;
        int n8 = n7 + 26;
        return n3 >= n5 && n3 <= n6 && n4 >= n7 && n4 <= n8;
    }

    public void attachToParent() {
        if (this.parent == null && this.advancement.getParent() != null) {
            this.parent = this.getFirstVisibleParent(this.advancement);
            if (this.parent != null) {
                this.parent.addChild(this);
            }
        }
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }
}

