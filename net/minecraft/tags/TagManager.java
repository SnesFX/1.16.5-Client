/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 */
package net.minecraft.tags;

import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.StaticTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagContainer;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class TagManager
implements PreparableReloadListener {
    private final TagLoader<Block> blocks = new TagLoader(Registry.BLOCK::getOptional, "tags/blocks", "block");
    private final TagLoader<Item> items = new TagLoader(Registry.ITEM::getOptional, "tags/items", "item");
    private final TagLoader<Fluid> fluids = new TagLoader(Registry.FLUID::getOptional, "tags/fluids", "fluid");
    private final TagLoader<EntityType<?>> entityTypes = new TagLoader(Registry.ENTITY_TYPE::getOptional, "tags/entity_types", "entity_type");
    private TagContainer tags = TagContainer.EMPTY;

    public TagContainer getTags() {
        return this.tags;
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
        CompletableFuture<Map<ResourceLocation, Tag.Builder>> completableFuture = this.blocks.prepare(resourceManager, executor);
        CompletableFuture<Map<ResourceLocation, Tag.Builder>> completableFuture2 = this.items.prepare(resourceManager, executor);
        CompletableFuture<Map<ResourceLocation, Tag.Builder>> completableFuture3 = this.fluids.prepare(resourceManager, executor);
        CompletableFuture<Map<ResourceLocation, Tag.Builder>> completableFuture4 = this.entityTypes.prepare(resourceManager, executor);
        return ((CompletableFuture)CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3, completableFuture4).thenCompose(preparationBarrier::wait)).thenAcceptAsync(void_ -> {
            TagCollection<Fluid> tagCollection;
            TagCollection<Item> tagCollection2;
            TagCollection<EntityType<?>> tagCollection3;
            TagCollection<Block> tagCollection4 = this.blocks.load((Map)completableFuture.join());
            TagContainer tagContainer = TagContainer.of(tagCollection4, tagCollection2 = this.items.load((Map)completableFuture2.join()), tagCollection = this.fluids.load((Map)completableFuture3.join()), tagCollection3 = this.entityTypes.load((Map)completableFuture4.join()));
            Multimap<ResourceLocation, ResourceLocation> multimap = StaticTags.getAllMissingTags(tagContainer);
            if (!multimap.isEmpty()) {
                throw new IllegalStateException("Missing required tags: " + multimap.entries().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).sorted().collect(Collectors.joining(",")));
            }
            SerializationTags.bind(tagContainer);
            this.tags = tagContainer;
        }, executor2);
    }
}

