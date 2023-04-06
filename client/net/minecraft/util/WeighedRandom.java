/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.util;

import java.util.List;
import java.util.Random;
import net.minecraft.Util;

public class WeighedRandom {
    public static int getTotalWeight(List<? extends WeighedRandomItem> list) {
        int n = 0;
        int n2 = list.size();
        for (int i = 0; i < n2; ++i) {
            WeighedRandomItem weighedRandomItem = list.get(i);
            n += weighedRandomItem.weight;
        }
        return n;
    }

    public static <T extends WeighedRandomItem> T getRandomItem(Random random, List<T> list, int n) {
        if (n <= 0) {
            throw Util.pauseInIde(new IllegalArgumentException());
        }
        int n2 = random.nextInt(n);
        return WeighedRandom.getWeightedItem(list, n2);
    }

    public static <T extends WeighedRandomItem> T getWeightedItem(List<T> list, int n) {
        int n2 = list.size();
        for (int i = 0; i < n2; ++i) {
            WeighedRandomItem weighedRandomItem = (WeighedRandomItem)list.get(i);
            if ((n -= weighedRandomItem.weight) >= 0) continue;
            return (T)weighedRandomItem;
        }
        return null;
    }

    public static <T extends WeighedRandomItem> T getRandomItem(Random random, List<T> list) {
        return WeighedRandom.getRandomItem(random, list, WeighedRandom.getTotalWeight(list));
    }

    public static class WeighedRandomItem {
        protected final int weight;

        public WeighedRandomItem(int n) {
            this.weight = n;
        }
    }

}

