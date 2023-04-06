/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongList
 *  it.unimi.dsi.fastutil.longs.LongSet
 */
package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;
import net.minecraft.util.Mth;

public abstract class DynamicGraphMinFixedPoint {
    private final int levelCount;
    private final LongLinkedOpenHashSet[] queues;
    private final Long2ByteMap computedLevels;
    private int firstQueuedLevel;
    private volatile boolean hasWork;

    protected DynamicGraphMinFixedPoint(int n, final int n2, final int n3) {
        if (n >= 254) {
            throw new IllegalArgumentException("Level count must be < 254.");
        }
        this.levelCount = n;
        this.queues = new LongLinkedOpenHashSet[n];
        for (int i = 0; i < n; ++i) {
            this.queues[i] = new LongLinkedOpenHashSet(n2, 0.5f){

                protected void rehash(int n) {
                    if (n > n2) {
                        super.rehash(n);
                    }
                }
            };
        }
        this.computedLevels = new Long2ByteOpenHashMap(n3, 0.5f){

            protected void rehash(int n) {
                if (n > n3) {
                    super.rehash(n);
                }
            }
        };
        this.computedLevels.defaultReturnValue((byte)-1);
        this.firstQueuedLevel = n;
    }

    private int getKey(int n, int n2) {
        int n3 = n;
        if (n3 > n2) {
            n3 = n2;
        }
        if (n3 > this.levelCount - 1) {
            n3 = this.levelCount - 1;
        }
        return n3;
    }

    private void checkFirstQueuedLevel(int n) {
        int n2 = this.firstQueuedLevel;
        this.firstQueuedLevel = n;
        for (int i = n2 + 1; i < n; ++i) {
            if (this.queues[i].isEmpty()) continue;
            this.firstQueuedLevel = i;
            break;
        }
    }

    protected void removeFromQueue(long l) {
        int n = this.computedLevels.get(l) & 0xFF;
        if (n == 255) {
            return;
        }
        int n2 = this.getLevel(l);
        int n3 = this.getKey(n2, n);
        this.dequeue(l, n3, this.levelCount, true);
        this.hasWork = this.firstQueuedLevel < this.levelCount;
    }

    public void removeIf(LongPredicate longPredicate) {
        LongArrayList longArrayList = new LongArrayList();
        this.computedLevels.keySet().forEach(arg_0 -> DynamicGraphMinFixedPoint.lambda$removeIf$0(longPredicate, (LongList)longArrayList, arg_0));
        longArrayList.forEach(this::removeFromQueue);
    }

    private void dequeue(long l, int n, int n2, boolean bl) {
        if (bl) {
            this.computedLevels.remove(l);
        }
        this.queues[n].remove(l);
        if (this.queues[n].isEmpty() && this.firstQueuedLevel == n) {
            this.checkFirstQueuedLevel(n2);
        }
    }

    private void enqueue(long l, int n, int n2) {
        this.computedLevels.put(l, (byte)n);
        this.queues[n2].add(l);
        if (this.firstQueuedLevel > n2) {
            this.firstQueuedLevel = n2;
        }
    }

    protected void checkNode(long l) {
        this.checkEdge(l, l, this.levelCount - 1, false);
    }

    protected void checkEdge(long l, long l2, int n, boolean bl) {
        this.checkEdge(l, l2, n, this.getLevel(l2), this.computedLevels.get(l2) & 0xFF, bl);
        this.hasWork = this.firstQueuedLevel < this.levelCount;
    }

    private void checkEdge(long l, long l2, int n, int n2, int n3, boolean bl) {
        boolean bl2;
        if (this.isSource(l2)) {
            return;
        }
        n = Mth.clamp(n, 0, this.levelCount - 1);
        n2 = Mth.clamp(n2, 0, this.levelCount - 1);
        if (n3 == 255) {
            bl2 = true;
            n3 = n2;
        } else {
            bl2 = false;
        }
        int n4 = bl ? Math.min(n3, n) : Mth.clamp(this.getComputedLevel(l2, l, n), 0, this.levelCount - 1);
        int n5 = this.getKey(n2, n3);
        if (n2 != n4) {
            int n6 = this.getKey(n2, n4);
            if (n5 != n6 && !bl2) {
                this.dequeue(l2, n5, n6, false);
            }
            this.enqueue(l2, n4, n6);
        } else if (!bl2) {
            this.dequeue(l2, n5, this.levelCount, true);
        }
    }

    protected final void checkNeighbor(long l, long l2, int n, boolean bl) {
        int n2 = this.computedLevels.get(l2) & 0xFF;
        int n3 = Mth.clamp(this.computeLevelFromNeighbor(l, l2, n), 0, this.levelCount - 1);
        if (bl) {
            this.checkEdge(l, l2, n3, this.getLevel(l2), n2, true);
        } else {
            int n4;
            boolean bl2;
            if (n2 == 255) {
                bl2 = true;
                n4 = Mth.clamp(this.getLevel(l2), 0, this.levelCount - 1);
            } else {
                n4 = n2;
                bl2 = false;
            }
            if (n3 == n4) {
                this.checkEdge(l, l2, this.levelCount - 1, bl2 ? n4 : this.getLevel(l2), n2, false);
            }
        }
    }

    protected final boolean hasWork() {
        return this.hasWork;
    }

    protected final int runUpdates(int n) {
        if (this.firstQueuedLevel >= this.levelCount) {
            return n;
        }
        while (this.firstQueuedLevel < this.levelCount && n > 0) {
            int n2;
            --n;
            LongLinkedOpenHashSet longLinkedOpenHashSet = this.queues[this.firstQueuedLevel];
            long l = longLinkedOpenHashSet.removeFirstLong();
            int n3 = Mth.clamp(this.getLevel(l), 0, this.levelCount - 1);
            if (longLinkedOpenHashSet.isEmpty()) {
                this.checkFirstQueuedLevel(this.levelCount);
            }
            if ((n2 = this.computedLevels.remove(l) & 0xFF) < n3) {
                this.setLevel(l, n2);
                this.checkNeighborsAfterUpdate(l, n2, true);
                continue;
            }
            if (n2 <= n3) continue;
            this.enqueue(l, n2, this.getKey(this.levelCount - 1, n2));
            this.setLevel(l, this.levelCount - 1);
            this.checkNeighborsAfterUpdate(l, n3, false);
        }
        this.hasWork = this.firstQueuedLevel < this.levelCount;
        return n;
    }

    public int getQueueSize() {
        return this.computedLevels.size();
    }

    protected abstract boolean isSource(long var1);

    protected abstract int getComputedLevel(long var1, long var3, int var5);

    protected abstract void checkNeighborsAfterUpdate(long var1, int var3, boolean var4);

    protected abstract int getLevel(long var1);

    protected abstract void setLevel(long var1, int var3);

    protected abstract int computeLevelFromNeighbor(long var1, long var3, int var5);

    private static /* synthetic */ void lambda$removeIf$0(LongPredicate longPredicate, LongList longList, long l) {
        if (longPredicate.test(l)) {
            longList.add(l);
        }
    }

}

