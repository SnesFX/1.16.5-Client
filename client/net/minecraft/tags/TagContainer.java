/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.tags;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.StaticTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;

public interface TagContainer {
    public static final TagContainer EMPTY = TagContainer.of(TagCollection.empty(), TagCollection.empty(), TagCollection.empty(), TagCollection.empty());

    public TagCollection<Block> getBlocks();

    public TagCollection<Item> getItems();

    public TagCollection<Fluid> getFluids();

    public TagCollection<EntityType<?>> getEntityTypes();

    default public void bindToGlobal() {
        StaticTags.resetAll(this);
        Blocks.rebuildCache();
    }

    default public void serializeToNetwork(FriendlyByteBuf friendlyByteBuf) {
        this.getBlocks().serializeToNetwork(friendlyByteBuf, Registry.BLOCK);
        this.getItems().serializeToNetwork(friendlyByteBuf, Registry.ITEM);
        this.getFluids().serializeToNetwork(friendlyByteBuf, Registry.FLUID);
        this.getEntityTypes().serializeToNetwork(friendlyByteBuf, Registry.ENTITY_TYPE);
    }

    public static TagContainer deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        TagCollection<Block> tagCollection = TagCollection.loadFromNetwork(friendlyByteBuf, Registry.BLOCK);
        TagCollection<Item> tagCollection2 = TagCollection.loadFromNetwork(friendlyByteBuf, Registry.ITEM);
        TagCollection<Fluid> tagCollection3 = TagCollection.loadFromNetwork(friendlyByteBuf, Registry.FLUID);
        TagCollection<EntityType<?>> tagCollection4 = TagCollection.loadFromNetwork(friendlyByteBuf, Registry.ENTITY_TYPE);
        return TagContainer.of(tagCollection, tagCollection2, tagCollection3, tagCollection4);
    }

    public static TagContainer of(final TagCollection<Block> tagCollection, final TagCollection<Item> tagCollection2, final TagCollection<Fluid> tagCollection3, final TagCollection<EntityType<?>> tagCollection4) {
        return new TagContainer(){

            @Override
            public TagCollection<Block> getBlocks() {
                return tagCollection;
            }

            @Override
            public TagCollection<Item> getItems() {
                return tagCollection2;
            }

            @Override
            public TagCollection<Fluid> getFluids() {
                return tagCollection3;
            }

            @Override
            public TagCollection<EntityType<?>> getEntityTypes() {
                return tagCollection4;
            }
        };
    }

}

