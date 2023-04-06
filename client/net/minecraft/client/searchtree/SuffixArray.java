/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.Arrays
 *  it.unimi.dsi.fastutil.Swapper
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntComparator
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.searchtree;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SuffixArray<T> {
    private static final boolean DEBUG_COMPARISONS = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
    private static final boolean DEBUG_ARRAY = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
    private static final Logger LOGGER = LogManager.getLogger();
    protected final List<T> list = Lists.newArrayList();
    private final IntList chars = new IntArrayList();
    private final IntList wordStarts = new IntArrayList();
    private IntList suffixToT = new IntArrayList();
    private IntList offsets = new IntArrayList();
    private int maxStringLength;

    public void add(T t, String string) {
        this.maxStringLength = Math.max(this.maxStringLength, string.length());
        int n = this.list.size();
        this.list.add(t);
        this.wordStarts.add(this.chars.size());
        for (int i = 0; i < string.length(); ++i) {
            this.suffixToT.add(n);
            this.offsets.add(i);
            this.chars.add((int)string.charAt(i));
        }
        this.suffixToT.add(n);
        this.offsets.add(string.length());
        this.chars.add(-1);
    }

    public void generate() {
        int n3;
        int n4 = this.chars.size();
        int[] arrn = new int[n4];
        final int[] arrn2 = new int[n4];
        final int[] arrn3 = new int[n4];
        int[] arrn4 = new int[n4];
        IntComparator intComparator = new IntComparator(){

            public int compare(int n, int n2) {
                if (arrn2[n] == arrn2[n2]) {
                    return Integer.compare(arrn3[n], arrn3[n2]);
                }
                return Integer.compare(arrn2[n], arrn2[n2]);
            }

            public int compare(Integer n, Integer n2) {
                return this.compare((int)n, (int)n2);
            }
        };
        Swapper swapper = (n, n2) -> {
            if (n != n2) {
                int n3 = arrn2[n];
                arrn[n] = arrn2[n2];
                arrn[n2] = n3;
                n3 = arrn3[n];
                arrn2[n] = arrn3[n2];
                arrn2[n2] = n3;
                n3 = arrn4[n];
                arrn3[n] = arrn4[n2];
                arrn3[n2] = n3;
            }
        };
        for (n3 = 0; n3 < n4; ++n3) {
            arrn[n3] = this.chars.getInt(n3);
        }
        n3 = 1;
        int n5 = Math.min(n4, this.maxStringLength);
        while (n3 * 2 < n5) {
            int n6;
            for (n6 = 0; n6 < n4; ++n6) {
                arrn2[n6] = arrn[n6];
                arrn3[n6] = n6 + n3 < n4 ? arrn[n6 + n3] : -2;
                arrn4[n6] = n6;
            }
            it.unimi.dsi.fastutil.Arrays.quickSort((int)0, (int)n4, (IntComparator)intComparator, (Swapper)swapper);
            for (n6 = 0; n6 < n4; ++n6) {
                arrn[arrn4[n6]] = n6 > 0 && arrn2[n6] == arrn2[n6 - 1] && arrn3[n6] == arrn3[n6 - 1] ? arrn[arrn4[n6 - 1]] : n6;
            }
            n3 *= 2;
        }
        IntList intList = this.suffixToT;
        IntList intList2 = this.offsets;
        this.suffixToT = new IntArrayList(intList.size());
        this.offsets = new IntArrayList(intList2.size());
        for (int i = 0; i < n4; ++i) {
            int n7 = arrn4[i];
            this.suffixToT.add(intList.getInt(n7));
            this.offsets.add(intList2.getInt(n7));
        }
        if (DEBUG_ARRAY) {
            this.print();
        }
    }

    private void print() {
        for (int i = 0; i < this.suffixToT.size(); ++i) {
            LOGGER.debug("{} {}", (Object)i, (Object)this.getString(i));
        }
        LOGGER.debug("");
    }

    private String getString(int n) {
        int n2 = this.offsets.getInt(n);
        int n3 = this.wordStarts.getInt(this.suffixToT.getInt(n));
        StringBuilder stringBuilder = new StringBuilder();
        int n4 = 0;
        while (n3 + n4 < this.chars.size()) {
            int n5;
            if (n4 == n2) {
                stringBuilder.append('^');
            }
            if ((n5 = this.chars.get(n3 + n4).intValue()) == -1) break;
            stringBuilder.append((char)n5);
            ++n4;
        }
        return stringBuilder.toString();
    }

    private int compare(String string, int n) {
        int n2 = this.wordStarts.getInt(this.suffixToT.getInt(n));
        int n3 = this.offsets.getInt(n);
        for (int i = 0; i < string.length(); ++i) {
            char c;
            int n4 = this.chars.getInt(n2 + n3 + i);
            if (n4 == -1) {
                return 1;
            }
            char c2 = string.charAt(i);
            if (c2 < (c = (char)n4)) {
                return -1;
            }
            if (c2 <= c) continue;
            return 1;
        }
        return 0;
    }

    public List<T> search(String string) {
        int n;
        int n2;
        int n3 = this.suffixToT.size();
        int n4 = 0;
        int n5 = n3;
        while (n4 < n5) {
            n2 = n4 + (n5 - n4) / 2;
            n = this.compare(string, n2);
            if (DEBUG_COMPARISONS) {
                LOGGER.debug("comparing lower \"{}\" with {} \"{}\": {}", (Object)string, (Object)n2, (Object)this.getString(n2), (Object)n);
            }
            if (n > 0) {
                n4 = n2 + 1;
                continue;
            }
            n5 = n2;
        }
        if (n4 < 0 || n4 >= n3) {
            return Collections.emptyList();
        }
        n2 = n4;
        n5 = n3;
        while (n4 < n5) {
            n = n4 + (n5 - n4) / 2;
            int n6 = this.compare(string, n);
            if (DEBUG_COMPARISONS) {
                LOGGER.debug("comparing upper \"{}\" with {} \"{}\": {}", (Object)string, (Object)n, (Object)this.getString(n), (Object)n6);
            }
            if (n6 >= 0) {
                n4 = n + 1;
                continue;
            }
            n5 = n;
        }
        n = n4;
        IntOpenHashSet intOpenHashSet = new IntOpenHashSet();
        for (int i = n2; i < n; ++i) {
            intOpenHashSet.add(this.suffixToT.getInt(i));
        }
        int[] arrn = intOpenHashSet.toIntArray();
        Arrays.sort(arrn);
        LinkedHashSet linkedHashSet = Sets.newLinkedHashSet();
        for (int n7 : arrn) {
            linkedHashSet.add(this.list.get(n7));
        }
        return Lists.newArrayList((Iterable)linkedHashSet);
    }

}

