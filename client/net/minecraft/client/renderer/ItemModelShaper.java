/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ItemModelShaper {
    public final Int2ObjectMap<ModelResourceLocation> shapes = new Int2ObjectOpenHashMap(256);
    private final Int2ObjectMap<BakedModel> shapesCache = new Int2ObjectOpenHashMap(256);
    private final ModelManager modelManager;

    public ItemModelShaper(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public TextureAtlasSprite getParticleIcon(ItemLike itemLike) {
        return this.getParticleIcon(new ItemStack(itemLike));
    }

    public TextureAtlasSprite getParticleIcon(ItemStack itemStack) {
        BakedModel bakedModel = this.getItemModel(itemStack);
        if (bakedModel == this.modelManager.getMissingModel() && itemStack.getItem() instanceof BlockItem) {
            return this.modelManager.getBlockModelShaper().getParticleIcon(((BlockItem)itemStack.getItem()).getBlock().defaultBlockState());
        }
        return bakedModel.getParticleIcon();
    }

    public BakedModel getItemModel(ItemStack itemStack) {
        BakedModel bakedModel = this.getItemModel(itemStack.getItem());
        return bakedModel == null ? this.modelManager.getMissingModel() : bakedModel;
    }

    @Nullable
    public BakedModel getItemModel(Item item) {
        return (BakedModel)this.shapesCache.get(ItemModelShaper.getIndex(item));
    }

    private static int getIndex(Item item) {
        return Item.getId(item);
    }

    public void register(Item item, ModelResourceLocation modelResourceLocation) {
        this.shapes.put(ItemModelShaper.getIndex(item), (Object)modelResourceLocation);
    }

    public ModelManager getModelManager() {
        return this.modelManager;
    }

    public void rebuildCache() {
        this.shapesCache.clear();
        for (Map.Entry entry : this.shapes.entrySet()) {
            this.shapesCache.put((Integer)entry.getKey(), (Object)this.modelManager.getModel((ModelResourceLocation)entry.getValue()));
        }
    }
}

