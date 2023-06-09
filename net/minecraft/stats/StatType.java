/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.stats;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatFormatter;

public class StatType<T>
implements Iterable<Stat<T>> {
    private final Registry<T> registry;
    private final Map<T, Stat<T>> map = new IdentityHashMap<T, Stat<T>>();
    @Nullable
    private Component displayName;

    public StatType(Registry<T> registry) {
        this.registry = registry;
    }

    public boolean contains(T t) {
        return this.map.containsKey(t);
    }

    public Stat<T> get(T t, StatFormatter statFormatter) {
        return this.map.computeIfAbsent(t, object -> new Stat<Object>(this, object, statFormatter));
    }

    public Registry<T> getRegistry() {
        return this.registry;
    }

    @Override
    public Iterator<Stat<T>> iterator() {
        return this.map.values().iterator();
    }

    public Stat<T> get(T t) {
        return this.get(t, StatFormatter.DEFAULT);
    }

    public String getTranslationKey() {
        return "stat_type." + Registry.STAT_TYPE.getKey(this).toString().replace(':', '.');
    }

    public Component getDisplayName() {
        if (this.displayName == null) {
            this.displayName = new TranslatableComponent(this.getTranslationKey());
        }
        return this.displayName;
    }
}

