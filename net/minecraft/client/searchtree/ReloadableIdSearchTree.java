/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.PeekingIterator
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.client.searchtree;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.SuffixArray;
import net.minecraft.resources.ResourceLocation;

public class ReloadableIdSearchTree<T>
implements MutableSearchTree<T> {
    protected SuffixArray<T> namespaceTree = new SuffixArray();
    protected SuffixArray<T> pathTree = new SuffixArray();
    private final Function<T, Stream<ResourceLocation>> idGetter;
    private final List<T> contents = Lists.newArrayList();
    private final Object2IntMap<T> orderT = new Object2IntOpenHashMap();

    public ReloadableIdSearchTree(Function<T, Stream<ResourceLocation>> function) {
        this.idGetter = function;
    }

    @Override
    public void refresh() {
        this.namespaceTree = new SuffixArray();
        this.pathTree = new SuffixArray();
        for (T t : this.contents) {
            this.index(t);
        }
        this.namespaceTree.generate();
        this.pathTree.generate();
    }

    @Override
    public void add(T t) {
        this.orderT.put(t, this.contents.size());
        this.contents.add(t);
        this.index(t);
    }

    @Override
    public void clear() {
        this.contents.clear();
        this.orderT.clear();
    }

    protected void index(T t) {
        this.idGetter.apply(t).forEach(resourceLocation -> {
            this.namespaceTree.add(t, resourceLocation.getNamespace().toLowerCase(Locale.ROOT));
            this.pathTree.add(t, resourceLocation.getPath().toLowerCase(Locale.ROOT));
        });
    }

    protected int comparePosition(T t, T t2) {
        return Integer.compare(this.orderT.getInt(t), this.orderT.getInt(t2));
    }

    @Override
    public List<T> search(String string) {
        int n = string.indexOf(58);
        if (n == -1) {
            return this.pathTree.search(string);
        }
        List<T> list = this.namespaceTree.search(string.substring(0, n).trim());
        String string2 = string.substring(n + 1).trim();
        List<T> list2 = this.pathTree.search(string2);
        return Lists.newArrayList(new IntersectionIterator<T>(list.iterator(), list2.iterator(), (arg_0, arg_1) -> this.comparePosition(arg_0, arg_1)));
    }

    public static class IntersectionIterator<T>
    extends AbstractIterator<T> {
        private final PeekingIterator<T> firstIterator;
        private final PeekingIterator<T> secondIterator;
        private final Comparator<T> orderT;

        public IntersectionIterator(Iterator<T> iterator, Iterator<T> iterator2, Comparator<T> comparator) {
            this.firstIterator = Iterators.peekingIterator(iterator);
            this.secondIterator = Iterators.peekingIterator(iterator2);
            this.orderT = comparator;
        }

        protected T computeNext() {
            while (this.firstIterator.hasNext() && this.secondIterator.hasNext()) {
                int n = this.orderT.compare(this.firstIterator.peek(), this.secondIterator.peek());
                if (n == 0) {
                    this.secondIterator.next();
                    return (T)this.firstIterator.next();
                }
                if (n < 0) {
                    this.firstIterator.next();
                    continue;
                }
                this.secondIterator.next();
            }
            return (T)this.endOfData();
        }
    }

}

