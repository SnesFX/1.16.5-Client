/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableFloat
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package net.minecraft.client;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.ComponentCollector;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

public class StringSplitter {
    private final WidthProvider widthProvider;

    public StringSplitter(WidthProvider widthProvider) {
        this.widthProvider = widthProvider;
    }

    public float stringWidth(@Nullable String string) {
        if (string == null) {
            return 0.0f;
        }
        MutableFloat mutableFloat = new MutableFloat();
        StringDecomposer.iterateFormatted(string, Style.EMPTY, (n, style, n2) -> {
            mutableFloat.add(this.widthProvider.getWidth(n2, style));
            return true;
        });
        return mutableFloat.floatValue();
    }

    public float stringWidth(FormattedText formattedText) {
        MutableFloat mutableFloat = new MutableFloat();
        StringDecomposer.iterateFormatted(formattedText, Style.EMPTY, (n, style, n2) -> {
            mutableFloat.add(this.widthProvider.getWidth(n2, style));
            return true;
        });
        return mutableFloat.floatValue();
    }

    public float stringWidth(FormattedCharSequence formattedCharSequence) {
        MutableFloat mutableFloat = new MutableFloat();
        formattedCharSequence.accept((n, style, n2) -> {
            mutableFloat.add(this.widthProvider.getWidth(n2, style));
            return true;
        });
        return mutableFloat.floatValue();
    }

    public int plainIndexAtWidth(String string, int n, Style style) {
        WidthLimitedCharSink widthLimitedCharSink = new WidthLimitedCharSink(n);
        StringDecomposer.iterate(string, style, widthLimitedCharSink);
        return widthLimitedCharSink.getPosition();
    }

    public String plainHeadByWidth(String string, int n, Style style) {
        return string.substring(0, this.plainIndexAtWidth(string, n, style));
    }

    public String plainTailByWidth(String string, int n, Style style2) {
        MutableFloat mutableFloat = new MutableFloat();
        MutableInt mutableInt = new MutableInt(string.length());
        StringDecomposer.iterateBackwards(string, style2, (n2, style, n3) -> {
            float f = mutableFloat.addAndGet(this.widthProvider.getWidth(n3, style));
            if (f > (float)n) {
                return false;
            }
            mutableInt.setValue(n2);
            return true;
        });
        return string.substring(mutableInt.intValue());
    }

    @Nullable
    public Style componentStyleAtWidth(FormattedText formattedText, int n) {
        WidthLimitedCharSink widthLimitedCharSink = new WidthLimitedCharSink(n);
        return formattedText.visit((style, string) -> StringDecomposer.iterateFormatted(string, style, (FormattedCharSink)widthLimitedCharSink) ? Optional.empty() : Optional.of(style), Style.EMPTY).orElse(null);
    }

    @Nullable
    public Style componentStyleAtWidth(FormattedCharSequence formattedCharSequence, int n3) {
        WidthLimitedCharSink widthLimitedCharSink = new WidthLimitedCharSink(n3);
        MutableObject mutableObject = new MutableObject();
        formattedCharSequence.accept((n, style, n2) -> {
            if (!widthLimitedCharSink.accept(n, style, n2)) {
                mutableObject.setValue((Object)style);
                return false;
            }
            return true;
        });
        return (Style)mutableObject.getValue();
    }

    public FormattedText headByWidth(FormattedText formattedText, int n, Style style) {
        final WidthLimitedCharSink widthLimitedCharSink = new WidthLimitedCharSink(n);
        return formattedText.visit(new FormattedText.StyledContentConsumer<FormattedText>(){
            private final ComponentCollector collector = new ComponentCollector();

            @Override
            public Optional<FormattedText> accept(Style style, String string) {
                widthLimitedCharSink.resetPosition();
                if (!StringDecomposer.iterateFormatted(string, style, (FormattedCharSink)widthLimitedCharSink)) {
                    String string2 = string.substring(0, widthLimitedCharSink.getPosition());
                    if (!string2.isEmpty()) {
                        this.collector.append(FormattedText.of(string2, style));
                    }
                    return Optional.of(this.collector.getResultOrEmpty());
                }
                if (!string.isEmpty()) {
                    this.collector.append(FormattedText.of(string, style));
                }
                return Optional.empty();
            }
        }, style).orElse(formattedText);
    }

    public static int getWordPosition(String string, int n, int n2, boolean bl) {
        int n3 = n2;
        boolean bl2 = n < 0;
        int n4 = Math.abs(n);
        for (int i = 0; i < n4; ++i) {
            if (bl2) {
                while (bl && n3 > 0 && (string.charAt(n3 - 1) == ' ' || string.charAt(n3 - 1) == '\n')) {
                    --n3;
                }
                while (n3 > 0 && string.charAt(n3 - 1) != ' ' && string.charAt(n3 - 1) != '\n') {
                    --n3;
                }
                continue;
            }
            int n5 = string.length();
            int n6 = string.indexOf(32, n3);
            int n7 = string.indexOf(10, n3);
            n3 = n6 == -1 && n7 == -1 ? -1 : (n6 != -1 && n7 != -1 ? Math.min(n6, n7) : (n6 != -1 ? n6 : n7));
            if (n3 == -1) {
                n3 = n5;
                continue;
            }
            while (bl && n3 < n5 && (string.charAt(n3) == ' ' || string.charAt(n3) == '\n')) {
                ++n3;
            }
        }
        return n3;
    }

    public void splitLines(String string, int n, Style style, boolean bl, LinePosConsumer linePosConsumer) {
        int n2 = 0;
        int n3 = string.length();
        Style style2 = style;
        while (n2 < n3) {
            LineBreakFinder lineBreakFinder = new LineBreakFinder(n);
            boolean bl2 = StringDecomposer.iterateFormatted(string, n2, style2, style, lineBreakFinder);
            if (bl2) {
                linePosConsumer.accept(style2, n2, n3);
                break;
            }
            int n4 = lineBreakFinder.getSplitPosition();
            char c = string.charAt(n4);
            int n5 = c == '\n' || c == ' ' ? n4 + 1 : n4;
            linePosConsumer.accept(style2, n2, bl ? n5 : n4);
            n2 = n5;
            style2 = lineBreakFinder.getSplitStyle();
        }
    }

    public List<FormattedText> splitLines(String string, int n3, Style style2) {
        ArrayList arrayList = Lists.newArrayList();
        this.splitLines(string, n3, style2, false, (style, n, n2) -> arrayList.add(FormattedText.of(string.substring(n, n2), style)));
        return arrayList;
    }

    public List<FormattedText> splitLines(FormattedText formattedText2, int n, Style style) {
        ArrayList arrayList = Lists.newArrayList();
        this.splitLines(formattedText2, n, style, (formattedText, bl) -> arrayList.add(formattedText));
        return arrayList;
    }

    public void splitLines(FormattedText formattedText, int n, Style style2, BiConsumer<FormattedText, Boolean> biConsumer) {
        Object object;
        ArrayList arrayList = Lists.newArrayList();
        formattedText.visit((style, string) -> {
            if (!string.isEmpty()) {
                arrayList.add(new LineComponent(string, style));
            }
            return Optional.empty();
        }, style2);
        FlatComponents flatComponents = new FlatComponents(arrayList);
        boolean bl = true;
        boolean bl2 = false;
        boolean bl3 = false;
        block0 : while (bl) {
            bl = false;
            object = new LineBreakFinder(n);
            for (LineComponent lineComponent : flatComponents.parts) {
                boolean bl4 = StringDecomposer.iterateFormatted(lineComponent.contents, 0, lineComponent.style, style2, (FormattedCharSink)object);
                if (!bl4) {
                    int n2 = ((LineBreakFinder)object).getSplitPosition();
                    Style style3 = ((LineBreakFinder)object).getSplitStyle();
                    char c = flatComponents.charAt(n2);
                    boolean bl5 = c == '\n';
                    boolean bl6 = bl5 || c == ' ';
                    bl2 = bl5;
                    FormattedText formattedText2 = flatComponents.splitAt(n2, bl6 ? 1 : 0, style3);
                    biConsumer.accept(formattedText2, bl3);
                    bl3 = !bl5;
                    bl = true;
                    continue block0;
                }
                ((LineBreakFinder)object).addToOffset(lineComponent.contents.length());
            }
        }
        object = flatComponents.getRemainder();
        if (object != null) {
            biConsumer.accept((FormattedText)object, bl3);
        } else if (bl2) {
            biConsumer.accept(FormattedText.EMPTY, false);
        }
    }

    static class FlatComponents {
        private final List<LineComponent> parts;
        private String flatParts;

        public FlatComponents(List<LineComponent> list) {
            this.parts = list;
            this.flatParts = list.stream().map(lineComponent -> lineComponent.contents).collect(Collectors.joining());
        }

        public char charAt(int n) {
            return this.flatParts.charAt(n);
        }

        public FormattedText splitAt(int n, int n2, Style style) {
            ComponentCollector componentCollector = new ComponentCollector();
            ListIterator<LineComponent> listIterator = this.parts.listIterator();
            int n3 = n;
            boolean bl = false;
            while (listIterator.hasNext()) {
                String string;
                LineComponent lineComponent = listIterator.next();
                String string2 = lineComponent.contents;
                int n4 = string2.length();
                if (!bl) {
                    if (n3 > n4) {
                        componentCollector.append(lineComponent);
                        listIterator.remove();
                        n3 -= n4;
                    } else {
                        string = string2.substring(0, n3);
                        if (!string.isEmpty()) {
                            componentCollector.append(FormattedText.of(string, lineComponent.style));
                        }
                        n3 += n2;
                        bl = true;
                    }
                }
                if (!bl) continue;
                if (n3 > n4) {
                    listIterator.remove();
                    n3 -= n4;
                    continue;
                }
                string = string2.substring(n3);
                if (string.isEmpty()) {
                    listIterator.remove();
                    break;
                }
                listIterator.set(new LineComponent(string, style));
                break;
            }
            this.flatParts = this.flatParts.substring(n + n2);
            return componentCollector.getResultOrEmpty();
        }

        @Nullable
        public FormattedText getRemainder() {
            ComponentCollector componentCollector = new ComponentCollector();
            this.parts.forEach(componentCollector::append);
            this.parts.clear();
            return componentCollector.getResult();
        }
    }

    static class LineComponent
    implements FormattedText {
        private final String contents;
        private final Style style;

        public LineComponent(String string, Style style) {
            this.contents = string;
            this.style = style;
        }

        @Override
        public <T> Optional<T> visit(FormattedText.ContentConsumer<T> contentConsumer) {
            return contentConsumer.accept(this.contents);
        }

        @Override
        public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> styledContentConsumer, Style style) {
            return styledContentConsumer.accept(this.style.applyTo(style), this.contents);
        }
    }

    @FunctionalInterface
    public static interface LinePosConsumer {
        public void accept(Style var1, int var2, int var3);
    }

    class LineBreakFinder
    implements FormattedCharSink {
        private final float maxWidth;
        private int lineBreak = -1;
        private Style lineBreakStyle = Style.EMPTY;
        private boolean hadNonZeroWidthChar;
        private float width;
        private int lastSpace = -1;
        private Style lastSpaceStyle = Style.EMPTY;
        private int nextChar;
        private int offset;

        public LineBreakFinder(float f) {
            this.maxWidth = Math.max(f, 1.0f);
        }

        @Override
        public boolean accept(int n, Style style, int n2) {
            int n3 = n + this.offset;
            switch (n2) {
                case 10: {
                    return this.finishIteration(n3, style);
                }
                case 32: {
                    this.lastSpace = n3;
                    this.lastSpaceStyle = style;
                }
            }
            float f = StringSplitter.this.widthProvider.getWidth(n2, style);
            this.width += f;
            if (this.hadNonZeroWidthChar && this.width > this.maxWidth) {
                if (this.lastSpace != -1) {
                    return this.finishIteration(this.lastSpace, this.lastSpaceStyle);
                }
                return this.finishIteration(n3, style);
            }
            this.hadNonZeroWidthChar |= f != 0.0f;
            this.nextChar = n3 + Character.charCount(n2);
            return true;
        }

        private boolean finishIteration(int n, Style style) {
            this.lineBreak = n;
            this.lineBreakStyle = style;
            return false;
        }

        private boolean lineBreakFound() {
            return this.lineBreak != -1;
        }

        public int getSplitPosition() {
            return this.lineBreakFound() ? this.lineBreak : this.nextChar;
        }

        public Style getSplitStyle() {
            return this.lineBreakStyle;
        }

        public void addToOffset(int n) {
            this.offset += n;
        }
    }

    class WidthLimitedCharSink
    implements FormattedCharSink {
        private float maxWidth;
        private int position;

        public WidthLimitedCharSink(float f) {
            this.maxWidth = f;
        }

        @Override
        public boolean accept(int n, Style style, int n2) {
            this.maxWidth -= StringSplitter.this.widthProvider.getWidth(n2, style);
            if (this.maxWidth >= 0.0f) {
                this.position = n + Character.charCount(n2);
                return true;
            }
            return false;
        }

        public int getPosition() {
            return this.position;
        }

        public void resetPosition() {
            this.position = 0;
        }
    }

    @FunctionalInterface
    public static interface WidthProvider {
        public float getWidth(int var1, Style var2);
    }

}

