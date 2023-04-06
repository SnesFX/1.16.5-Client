/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources.model;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

public interface UnbakedModel {
    public Collection<ResourceLocation> getDependencies();

    public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> var1, Set<Pair<String, String>> var2);

    @Nullable
    public BakedModel bake(ModelBakery var1, Function<Material, TextureAtlasSprite> var2, ModelState var3, ResourceLocation var4);
}

