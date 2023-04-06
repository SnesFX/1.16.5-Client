/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public interface MultiLineLabel {
    public static final MultiLineLabel EMPTY = new MultiLineLabel(){

        @Override
        public int renderCentered(PoseStack poseStack, int n, int n2) {
            return n2;
        }

        @Override
        public int renderCentered(PoseStack poseStack, int n, int n2, int n3, int n4) {
            return n2;
        }

        @Override
        public int renderLeftAligned(PoseStack poseStack, int n, int n2, int n3, int n4) {
            return n2;
        }

        @Override
        public int renderLeftAlignedNoShadow(PoseStack poseStack, int n, int n2, int n3, int n4) {
            return n2;
        }

        @Override
        public int getLineCount() {
            return 0;
        }
    };

    public static MultiLineLabel create(Font font, FormattedText formattedText, int n) {
        return MultiLineLabel.createFixed(font, (List)font.split(formattedText, n).stream().map(formattedCharSequence -> new TextWithWidth((FormattedCharSequence)formattedCharSequence, font.width((FormattedCharSequence)formattedCharSequence))).collect(ImmutableList.toImmutableList()));
    }

    public static MultiLineLabel create(Font font, FormattedText formattedText, int n, int n2) {
        return MultiLineLabel.createFixed(font, (List)font.split(formattedText, n).stream().limit(n2).map(formattedCharSequence -> new TextWithWidth((FormattedCharSequence)formattedCharSequence, font.width((FormattedCharSequence)formattedCharSequence))).collect(ImmutableList.toImmutableList()));
    }

    public static MultiLineLabel create(Font font, Component ... arrcomponent) {
        return MultiLineLabel.createFixed(font, (List)Arrays.stream(arrcomponent).map(Component::getVisualOrderText).map(formattedCharSequence -> new TextWithWidth((FormattedCharSequence)formattedCharSequence, font.width((FormattedCharSequence)formattedCharSequence))).collect(ImmutableList.toImmutableList()));
    }

    public static MultiLineLabel createFixed(final Font font, final List<TextWithWidth> list) {
        if (list.isEmpty()) {
            return EMPTY;
        }
        return new MultiLineLabel(){

            @Override
            public int renderCentered(PoseStack poseStack, int n, int n2) {
                font.getClass();
                return this.renderCentered(poseStack, n, n2, 9, 16777215);
            }

            @Override
            public int renderCentered(PoseStack poseStack, int n, int n2, int n3, int n4) {
                int n5 = n2;
                for (TextWithWidth textWithWidth : list) {
                    font.drawShadow(poseStack, textWithWidth.text, (float)(n - textWithWidth.width / 2), (float)n5, n4);
                    n5 += n3;
                }
                return n5;
            }

            @Override
            public int renderLeftAligned(PoseStack poseStack, int n, int n2, int n3, int n4) {
                int n5 = n2;
                for (TextWithWidth textWithWidth : list) {
                    font.drawShadow(poseStack, textWithWidth.text, (float)n, (float)n5, n4);
                    n5 += n3;
                }
                return n5;
            }

            @Override
            public int renderLeftAlignedNoShadow(PoseStack poseStack, int n, int n2, int n3, int n4) {
                int n5 = n2;
                for (TextWithWidth textWithWidth : list) {
                    font.draw(poseStack, textWithWidth.text, (float)n, (float)n5, n4);
                    n5 += n3;
                }
                return n5;
            }

            @Override
            public int getLineCount() {
                return list.size();
            }
        };
    }

    public int renderCentered(PoseStack var1, int var2, int var3);

    public int renderCentered(PoseStack var1, int var2, int var3, int var4, int var5);

    public int renderLeftAligned(PoseStack var1, int var2, int var3, int var4, int var5);

    public int renderLeftAlignedNoShadow(PoseStack var1, int var2, int var3, int var4, int var5);

    public int getLineCount();

    public static class TextWithWidth {
        private final FormattedCharSequence text;
        private final int width;

        private TextWithWidth(FormattedCharSequence formattedCharSequence, int n) {
            this.text = formattedCharSequence;
            this.width = n;
        }
    }

}

