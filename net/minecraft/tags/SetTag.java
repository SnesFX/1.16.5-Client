/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.tags;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import net.minecraft.tags.Tag;

public class SetTag<T>
implements Tag<T> {
    private final ImmutableList<T> valuesList;
    private final Set<T> values;
    @VisibleForTesting
    protected final Class<?> closestCommonSuperType;

    protected SetTag(Set<T> set, Class<?> class_) {
        this.closestCommonSuperType = class_;
        this.values = set;
        this.valuesList = ImmutableList.copyOf(set);
    }

    public static <T> SetTag<T> empty() {
        return new SetTag<T>((Set<T>)ImmutableSet.of(), Void.class);
    }

    public static <T> SetTag<T> create(Set<T> set) {
        return new SetTag<T>(set, SetTag.findCommonSuperClass(set));
    }

    @Override
    public boolean contains(T t) {
        return this.closestCommonSuperType.isInstance(t) && this.values.contains(t);
    }

    @Override
    public List<T> getValues() {
        return this.valuesList;
    }

    private static <T> Class<?> findCommonSuperClass(Set<T> set) {
        if (set.isEmpty()) {
            return Void.class;
        }
        Class<?> class_ = null;
        for (T t : set) {
            if (class_ == null) {
                class_ = t.getClass();
                continue;
            }
            class_ = SetTag.findClosestAncestor(class_, t.getClass());
        }
        return class_;
    }

    private static Class<?> findClosestAncestor(Class<?> class_, Class<?> class_2) {
        while (!class_.isAssignableFrom(class_2)) {
            class_ = class_.getSuperclass();
        }
        return class_;
    }
}

