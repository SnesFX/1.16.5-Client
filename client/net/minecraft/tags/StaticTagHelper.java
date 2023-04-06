/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SetTag;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagContainer;

public class StaticTagHelper<T> {
    private TagCollection<T> source = TagCollection.empty();
    private final List<Wrapper<T>> wrappers = Lists.newArrayList();
    private final Function<TagContainer, TagCollection<T>> collectionGetter;

    public StaticTagHelper(Function<TagContainer, TagCollection<T>> function) {
        this.collectionGetter = function;
    }

    public Tag.Named<T> bind(String string) {
        Wrapper wrapper = new Wrapper(new ResourceLocation(string));
        this.wrappers.add(wrapper);
        return wrapper;
    }

    public void resetToEmpty() {
        this.source = TagCollection.empty();
        SetTag setTag = SetTag.empty();
        this.wrappers.forEach(wrapper -> wrapper.rebind(resourceLocation -> setTag));
    }

    public void reset(TagContainer tagContainer) {
        TagCollection<T> tagCollection = this.collectionGetter.apply(tagContainer);
        this.source = tagCollection;
        this.wrappers.forEach(wrapper -> wrapper.rebind(tagCollection::getTag));
    }

    public TagCollection<T> getAllTags() {
        return this.source;
    }

    public List<? extends Tag.Named<T>> getWrappers() {
        return this.wrappers;
    }

    public Set<ResourceLocation> getMissingTags(TagContainer tagContainer) {
        TagCollection<T> tagCollection = this.collectionGetter.apply(tagContainer);
        Set set = this.wrappers.stream().map(Wrapper::getName).collect(Collectors.toSet());
        ImmutableSet immutableSet = ImmutableSet.copyOf(tagCollection.getAvailableTags());
        return Sets.difference(set, (Set)immutableSet);
    }

    static class Wrapper<T>
    implements Tag.Named<T> {
        @Nullable
        private Tag<T> tag;
        protected final ResourceLocation name;

        private Wrapper(ResourceLocation resourceLocation) {
            this.name = resourceLocation;
        }

        @Override
        public ResourceLocation getName() {
            return this.name;
        }

        private Tag<T> resolve() {
            if (this.tag == null) {
                throw new IllegalStateException("Tag " + this.name + " used before it was bound");
            }
            return this.tag;
        }

        void rebind(Function<ResourceLocation, Tag<T>> function) {
            this.tag = function.apply(this.name);
        }

        @Override
        public boolean contains(T t) {
            return this.resolve().contains(t);
        }

        @Override
        public List<T> getValues() {
            return this.resolve().getValues();
        }
    }

}

