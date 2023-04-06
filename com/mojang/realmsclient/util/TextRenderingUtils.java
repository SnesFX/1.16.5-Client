/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 */
package com.mojang.realmsclient.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TextRenderingUtils {
    @VisibleForTesting
    protected static List<String> lineBreak(String string) {
        return Arrays.asList(string.split("\\n"));
    }

    public static List<Line> decompose(String string, LineSegment ... arrlineSegment) {
        return TextRenderingUtils.decompose(string, Arrays.asList(arrlineSegment));
    }

    private static List<Line> decompose(String string, List<LineSegment> list) {
        List<String> list2 = TextRenderingUtils.lineBreak(string);
        return TextRenderingUtils.insertLinks(list2, list);
    }

    private static List<Line> insertLinks(List<String> list, List<LineSegment> list2) {
        int n = 0;
        ArrayList arrayList = Lists.newArrayList();
        for (String string : list) {
            ArrayList arrayList2 = Lists.newArrayList();
            List<String> list3 = TextRenderingUtils.split(string, "%link");
            for (String string2 : list3) {
                if ("%link".equals(string2)) {
                    arrayList2.add(list2.get(n++));
                    continue;
                }
                arrayList2.add(LineSegment.text(string2));
            }
            arrayList.add(new Line(arrayList2));
        }
        return arrayList;
    }

    public static List<String> split(String string, String string2) {
        int n;
        if (string2.isEmpty()) {
            throw new IllegalArgumentException("Delimiter cannot be the empty string");
        }
        ArrayList arrayList = Lists.newArrayList();
        int n2 = 0;
        while ((n = string.indexOf(string2, n2)) != -1) {
            if (n > n2) {
                arrayList.add(string.substring(n2, n));
            }
            arrayList.add(string2);
            n2 = n + string2.length();
        }
        if (n2 < string.length()) {
            arrayList.add(string.substring(n2));
        }
        return arrayList;
    }

    public static class LineSegment {
        private final String fullText;
        private final String linkTitle;
        private final String linkUrl;

        private LineSegment(String string) {
            this.fullText = string;
            this.linkTitle = null;
            this.linkUrl = null;
        }

        private LineSegment(String string, String string2, String string3) {
            this.fullText = string;
            this.linkTitle = string2;
            this.linkUrl = string3;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            LineSegment lineSegment = (LineSegment)object;
            return Objects.equals(this.fullText, lineSegment.fullText) && Objects.equals(this.linkTitle, lineSegment.linkTitle) && Objects.equals(this.linkUrl, lineSegment.linkUrl);
        }

        public int hashCode() {
            return Objects.hash(this.fullText, this.linkTitle, this.linkUrl);
        }

        public String toString() {
            return "Segment{fullText='" + this.fullText + '\'' + ", linkTitle='" + this.linkTitle + '\'' + ", linkUrl='" + this.linkUrl + '\'' + '}';
        }

        public String renderedText() {
            return this.isLink() ? this.linkTitle : this.fullText;
        }

        public boolean isLink() {
            return this.linkTitle != null;
        }

        public String getLinkUrl() {
            if (!this.isLink()) {
                throw new IllegalStateException("Not a link: " + this);
            }
            return this.linkUrl;
        }

        public static LineSegment link(String string, String string2) {
            return new LineSegment(null, string, string2);
        }

        @VisibleForTesting
        protected static LineSegment text(String string) {
            return new LineSegment(string);
        }
    }

    public static class Line {
        public final List<LineSegment> segments;

        Line(List<LineSegment> list) {
            this.segments = list;
        }

        public String toString() {
            return "Line{segments=" + this.segments + '}';
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            Line line = (Line)object;
            return Objects.equals(this.segments, line.segments);
        }

        public int hashCode() {
            return Objects.hash(this.segments);
        }
    }

}

