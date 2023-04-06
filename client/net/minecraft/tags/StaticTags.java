/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 */
package net.minecraft.tags;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.StaticTagHelper;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class StaticTags {
    private static final Map<ResourceLocation, StaticTagHelper<?>> HELPERS = Maps.newHashMap();

    public static <T> StaticTagHelper<T> create(ResourceLocation resourceLocation, Function<TagContainer, TagCollection<T>> function) {
        StaticTagHelper<T> staticTagHelper = new StaticTagHelper<T>(function);
        StaticTagHelper<T> staticTagHelper2 = HELPERS.putIfAbsent(resourceLocation, staticTagHelper);
        if (staticTagHelper2 != null) {
            throw new IllegalStateException("Duplicate entry for static tag collection: " + resourceLocation);
        }
        return staticTagHelper;
    }

    public static void resetAll(TagContainer tagContainer) {
        HELPERS.values().forEach(staticTagHelper -> staticTagHelper.reset(tagContainer));
    }

    public static void resetAllToEmpty() {
        HELPERS.values().forEach(StaticTagHelper::resetToEmpty);
    }

    public static Multimap<ResourceLocation, ResourceLocation> getAllMissingTags(TagContainer tagContainer) {
        HashMultimap hashMultimap = HashMultimap.create();
        HELPERS.forEach((arg_0, arg_1) -> StaticTags.lambda$getAllMissingTags$1((Multimap)hashMultimap, tagContainer, arg_0, arg_1));
        return hashMultimap;
    }

    public static void bootStrap() {
        StaticTagHelper[] arrstaticTagHelper = new StaticTagHelper[]{BlockTags.HELPER, ItemTags.HELPER, FluidTags.HELPER, EntityTypeTags.HELPER};
        boolean bl = Stream.of(arrstaticTagHelper).anyMatch(staticTagHelper -> !HELPERS.containsValue(staticTagHelper));
        if (bl) {
            throw new IllegalStateException("Missing helper registrations");
        }
    }

    private static /* synthetic */ void lambda$getAllMissingTags$1(Multimap multimap, TagContainer tagContainer, ResourceLocation resourceLocation, StaticTagHelper staticTagHelper) {
        multimap.putAll((Object)resourceLocation, staticTagHelper.getMissingTags(tagContainer));
    }
}

