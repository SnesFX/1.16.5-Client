/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.client.searchtree;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;

public class SearchRegistry
implements ResourceManagerReloadListener {
    public static final Key<ItemStack> CREATIVE_NAMES = new Key();
    public static final Key<ItemStack> CREATIVE_TAGS = new Key();
    public static final Key<RecipeCollection> RECIPE_COLLECTIONS = new Key();
    private final Map<Key<?>, MutableSearchTree<?>> searchTrees = Maps.newHashMap();

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        for (MutableSearchTree<?> mutableSearchTree : this.searchTrees.values()) {
            mutableSearchTree.refresh();
        }
    }

    public <T> void register(Key<T> key, MutableSearchTree<T> mutableSearchTree) {
        this.searchTrees.put(key, mutableSearchTree);
    }

    public <T> MutableSearchTree<T> getTree(Key<T> key) {
        return this.searchTrees.get(key);
    }

    public static class Key<T> {
    }

}

