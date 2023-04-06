/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.PeekingIterator
 */
package net.minecraft.client.searchtree;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.searchtree.ReloadableIdSearchTree;
import net.minecraft.client.searchtree.SuffixArray;
import net.minecraft.resources.ResourceLocation;

public class ReloadableSearchTree<T>
extends ReloadableIdSearchTree<T> {
    protected SuffixArray<T> tree = new SuffixArray();
    private final Function<T, Stream<String>> filler;

    public ReloadableSearchTree(Function<T, Stream<String>> function, Function<T, Stream<ResourceLocation>> function2) {
        super(function2);
        this.filler = function;
    }

    @Override
    public void refresh() {
        this.tree = new SuffixArray();
        super.refresh();
        this.tree.generate();
    }

    @Override
    protected void index(T t) {
        super.index(t);
        this.filler.apply(t).forEach(string -> this.tree.add(t, string.toLowerCase(Locale.ROOT)));
    }

    @Override
    public List<T> search(String string) {
        int n = string.indexOf(58);
        if (n < 0) {
            return this.tree.search(string);
        }
        List list = this.namespaceTree.search(string.substring(0, n).trim());
        String string2 = string.substring(n + 1).trim();
        List list2 = this.pathTree.search(string2);
        List<T> list3 = this.tree.search(string2);
        return Lists.newArrayList(new ReloadableIdSearchTree.IntersectionIterator(list.iterator(), new MergingUniqueIterator(list2.iterator(), list3.iterator(), (arg_0, arg_1) -> this.comparePosition(arg_0, arg_1)), (arg_0, arg_1) -> this.comparePosition(arg_0, arg_1)));
    }

    static class MergingUniqueIterator<T>
    extends AbstractIterator<T> {
        private final PeekingIterator<T> firstIterator;
        private final PeekingIterator<T> secondIterator;
        private final Comparator<T> orderT;

        public MergingUniqueIterator(Iterator<T> iterator, Iterator<T> iterator2, Comparator<T> comparator) {
            this.firstIterator = Iterators.peekingIterator(iterator);
            this.secondIterator = Iterators.peekingIterator(iterator2);
            this.orderT = comparator;
        }

        protected T computeNext() {
            boolean bl;
            boolean bl2 = !this.firstIterator.hasNext();
            boolean bl3 = bl = !this.secondIterator.hasNext();
            if (bl2 && bl) {
                return (T)this.endOfData();
            }
            if (bl2) {
                return (T)this.secondIterator.next();
            }
            if (bl) {
                return (T)this.firstIterator.next();
            }
            int n = this.orderT.compare(this.firstIterator.peek(), this.secondIterator.peek());
            if (n == 0) {
                this.secondIterator.next();
            }
            return (T)(n <= 0 ? this.firstIterator.next() : this.secondIterator.next());
        }
    }

}

