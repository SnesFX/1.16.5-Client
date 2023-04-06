/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.client.resources.metadata.animation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSectionSerializer;

public class AnimationMetadataSection {
    public static final AnimationMetadataSectionSerializer SERIALIZER = new AnimationMetadataSectionSerializer();
    public static final AnimationMetadataSection EMPTY = new AnimationMetadataSection(Lists.newArrayList(), -1, -1, 1, false){

        @Override
        public Pair<Integer, Integer> getFrameSize(int n, int n2) {
            return Pair.of((Object)n, (Object)n2);
        }
    };
    private final List<AnimationFrame> frames;
    private final int frameWidth;
    private final int frameHeight;
    private final int defaultFrameTime;
    private final boolean interpolatedFrames;

    public AnimationMetadataSection(List<AnimationFrame> list, int n, int n2, int n3, boolean bl) {
        this.frames = list;
        this.frameWidth = n;
        this.frameHeight = n2;
        this.defaultFrameTime = n3;
        this.interpolatedFrames = bl;
    }

    private static boolean isDivisionInteger(int n, int n2) {
        return n / n2 * n2 == n;
    }

    public Pair<Integer, Integer> getFrameSize(int n, int n2) {
        Pair<Integer, Integer> pair = this.calculateFrameSize(n, n2);
        int n3 = (Integer)pair.getFirst();
        int n4 = (Integer)pair.getSecond();
        if (!AnimationMetadataSection.isDivisionInteger(n, n3) || !AnimationMetadataSection.isDivisionInteger(n2, n4)) {
            throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", n, n2, n3, n4));
        }
        return pair;
    }

    private Pair<Integer, Integer> calculateFrameSize(int n, int n2) {
        if (this.frameWidth != -1) {
            if (this.frameHeight != -1) {
                return Pair.of((Object)this.frameWidth, (Object)this.frameHeight);
            }
            return Pair.of((Object)this.frameWidth, (Object)n2);
        }
        if (this.frameHeight != -1) {
            return Pair.of((Object)n, (Object)this.frameHeight);
        }
        int n3 = Math.min(n, n2);
        return Pair.of((Object)n3, (Object)n3);
    }

    public int getFrameHeight(int n) {
        return this.frameHeight == -1 ? n : this.frameHeight;
    }

    public int getFrameWidth(int n) {
        return this.frameWidth == -1 ? n : this.frameWidth;
    }

    public int getFrameCount() {
        return this.frames.size();
    }

    public int getDefaultFrameTime() {
        return this.defaultFrameTime;
    }

    public boolean isInterpolatedFrames() {
        return this.interpolatedFrames;
    }

    private AnimationFrame getFrame(int n) {
        return this.frames.get(n);
    }

    public int getFrameTime(int n) {
        AnimationFrame animationFrame = this.getFrame(n);
        if (animationFrame.isTimeUnknown()) {
            return this.defaultFrameTime;
        }
        return animationFrame.getTime();
    }

    public int getFrameIndex(int n) {
        return this.frames.get(n).getIndex();
    }

    public Set<Integer> getUniqueFrameIndices() {
        HashSet hashSet = Sets.newHashSet();
        for (AnimationFrame animationFrame : this.frames) {
            hashSet.add(animationFrame.getIndex());
        }
        return hashSet;
    }

}

