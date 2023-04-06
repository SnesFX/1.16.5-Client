/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.core;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;

public class IdMapper<T>
implements IdMap<T> {
    private int nextId;
    private final IdentityHashMap<T, Integer> tToId;
    private final List<T> idToT;

    public IdMapper() {
        this(512);
    }

    public IdMapper(int n) {
        this.idToT = Lists.newArrayListWithExpectedSize((int)n);
        this.tToId = new IdentityHashMap(n);
    }

    public void addMapping(T t, int n) {
        this.tToId.put(t, n);
        while (this.idToT.size() <= n) {
            this.idToT.add(null);
        }
        this.idToT.set(n, t);
        if (this.nextId <= n) {
            this.nextId = n + 1;
        }
    }

    public void add(T t) {
        this.addMapping(t, this.nextId);
    }

    @Override
    public int getId(T t) {
        Integer n = this.tToId.get(t);
        return n == null ? -1 : n;
    }

    @Nullable
    @Override
    public final T byId(int n) {
        if (n >= 0 && n < this.idToT.size()) {
            return this.idToT.get(n);
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.filter(this.idToT.iterator(), (Predicate)Predicates.notNull());
    }

    public int size() {
        return this.tToId.size();
    }
}

