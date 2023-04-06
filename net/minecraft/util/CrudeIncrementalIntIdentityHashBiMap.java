/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterators
 *  javax.annotation.Nullable
 */
package net.minecraft.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;
import net.minecraft.util.Mth;

public class CrudeIncrementalIntIdentityHashBiMap<K>
implements IdMap<K> {
    private static final Object EMPTY_SLOT = null;
    private K[] keys;
    private int[] values;
    private K[] byId;
    private int nextId;
    private int size;

    public CrudeIncrementalIntIdentityHashBiMap(int n) {
        n = (int)((float)n / 0.8f);
        this.keys = new Object[n];
        this.values = new int[n];
        this.byId = new Object[n];
    }

    @Override
    public int getId(@Nullable K k) {
        return this.getValue(this.indexOf(k, this.hash(k)));
    }

    @Nullable
    @Override
    public K byId(int n) {
        if (n < 0 || n >= this.byId.length) {
            return null;
        }
        return this.byId[n];
    }

    private int getValue(int n) {
        if (n == -1) {
            return -1;
        }
        return this.values[n];
    }

    public int add(K k) {
        int n = this.nextId();
        this.addMapping(k, n);
        return n;
    }

    private int nextId() {
        while (this.nextId < this.byId.length && this.byId[this.nextId] != null) {
            ++this.nextId;
        }
        return this.nextId;
    }

    private void grow(int n) {
        K[] arrK = this.keys;
        int[] arrn = this.values;
        this.keys = new Object[n];
        this.values = new int[n];
        this.byId = new Object[n];
        this.nextId = 0;
        this.size = 0;
        for (int i = 0; i < arrK.length; ++i) {
            if (arrK[i] == null) continue;
            this.addMapping(arrK[i], arrn[i]);
        }
    }

    public void addMapping(K k, int n) {
        int n2;
        int n3 = Math.max(n, this.size + 1);
        if ((float)n3 >= (float)this.keys.length * 0.8f) {
            for (n2 = this.keys.length << 1; n2 < n; n2 <<= 1) {
            }
            this.grow(n2);
        }
        n2 = this.findEmpty(this.hash(k));
        this.keys[n2] = k;
        this.values[n2] = n;
        this.byId[n] = k;
        ++this.size;
        if (n == this.nextId) {
            ++this.nextId;
        }
    }

    private int hash(@Nullable K k) {
        return (Mth.murmurHash3Mixer(System.identityHashCode(k)) & Integer.MAX_VALUE) % this.keys.length;
    }

    private int indexOf(@Nullable K k, int n) {
        int n2;
        for (n2 = n; n2 < this.keys.length; ++n2) {
            if (this.keys[n2] == k) {
                return n2;
            }
            if (this.keys[n2] != EMPTY_SLOT) continue;
            return -1;
        }
        for (n2 = 0; n2 < n; ++n2) {
            if (this.keys[n2] == k) {
                return n2;
            }
            if (this.keys[n2] != EMPTY_SLOT) continue;
            return -1;
        }
        return -1;
    }

    private int findEmpty(int n) {
        int n2;
        for (n2 = n; n2 < this.keys.length; ++n2) {
            if (this.keys[n2] != EMPTY_SLOT) continue;
            return n2;
        }
        for (n2 = 0; n2 < n; ++n2) {
            if (this.keys[n2] != EMPTY_SLOT) continue;
            return n2;
        }
        throw new RuntimeException("Overflowed :(");
    }

    @Override
    public Iterator<K> iterator() {
        return Iterators.filter((Iterator)Iterators.forArray((Object[])this.byId), (Predicate)Predicates.notNull());
    }

    public void clear() {
        Arrays.fill(this.keys, null);
        Arrays.fill(this.byId, null);
        this.nextId = 0;
        this.size = 0;
    }

    public int size() {
        return this.size;
    }
}

