/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Functions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.server.packs.repository;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

public class PackRepository
implements AutoCloseable {
    private final Set<RepositorySource> sources;
    private Map<String, Pack> available = ImmutableMap.of();
    private List<Pack> selected = ImmutableList.of();
    private final Pack.PackConstructor constructor;

    public PackRepository(Pack.PackConstructor packConstructor, RepositorySource ... arrrepositorySource) {
        this.constructor = packConstructor;
        this.sources = ImmutableSet.copyOf((Object[])arrrepositorySource);
    }

    public PackRepository(RepositorySource ... arrrepositorySource) {
        this((arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6) -> Pack.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6), arrrepositorySource);
    }

    public void reload() {
        List list = (List)this.selected.stream().map(Pack::getId).collect(ImmutableList.toImmutableList());
        this.close();
        this.available = this.discoverAvailable();
        this.selected = this.rebuildSelected(list);
    }

    private Map<String, Pack> discoverAvailable() {
        TreeMap treeMap = Maps.newTreeMap();
        for (RepositorySource repositorySource : this.sources) {
            repositorySource.loadPacks(pack -> treeMap.put(pack.getId(), pack), this.constructor);
        }
        return ImmutableMap.copyOf((Map)treeMap);
    }

    public void setSelected(Collection<String> collection) {
        this.selected = this.rebuildSelected(collection);
    }

    private List<Pack> rebuildSelected(Collection<String> collection) {
        List list = this.getAvailablePacks(collection).collect(Collectors.toList());
        for (Pack pack : this.available.values()) {
            if (!pack.isRequired() || list.contains(pack)) continue;
            pack.getDefaultPosition().insert(list, pack, Functions.identity(), false);
        }
        return ImmutableList.copyOf(list);
    }

    private Stream<Pack> getAvailablePacks(Collection<String> collection) {
        return collection.stream().map(this.available::get).filter(Objects::nonNull);
    }

    public Collection<String> getAvailableIds() {
        return this.available.keySet();
    }

    public Collection<Pack> getAvailablePacks() {
        return this.available.values();
    }

    public Collection<String> getSelectedIds() {
        return (Collection)this.selected.stream().map(Pack::getId).collect(ImmutableSet.toImmutableSet());
    }

    public Collection<Pack> getSelectedPacks() {
        return this.selected;
    }

    @Nullable
    public Pack getPack(String string) {
        return this.available.get(string);
    }

    @Override
    public void close() {
        this.available.values().forEach(Pack::close);
    }

    public boolean isAvailable(String string) {
        return this.available.containsKey(string);
    }

    public List<PackResources> openAllSelected() {
        return (List)this.selected.stream().map(Pack::open).collect(ImmutableList.toImmutableList());
    }
}

