/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.tags;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagContainer;

public class SerializationTags {
    private static volatile TagContainer instance = TagContainer.of(TagCollection.of(BlockTags.getWrappers().stream().collect(Collectors.toMap(Tag.Named::getName, named -> named))), TagCollection.of(ItemTags.getWrappers().stream().collect(Collectors.toMap(Tag.Named::getName, named -> named))), TagCollection.of(FluidTags.getWrappers().stream().collect(Collectors.toMap(Tag.Named::getName, named -> named))), TagCollection.of(EntityTypeTags.getWrappers().stream().collect(Collectors.toMap(Tag.Named::getName, named -> named))));

    public static TagContainer getInstance() {
        return instance;
    }

    public static void bind(TagContainer tagContainer) {
        instance = tagContainer;
    }
}

