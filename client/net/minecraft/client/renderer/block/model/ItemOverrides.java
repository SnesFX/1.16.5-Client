/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ItemOverrides {
    public static final ItemOverrides EMPTY = new ItemOverrides();
    private final List<ItemOverride> overrides = Lists.newArrayList();
    private final List<BakedModel> overrideModels;

    private ItemOverrides() {
        this.overrideModels = Collections.emptyList();
    }

    public ItemOverrides(ModelBakery modelBakery, BlockModel blockModel, Function<ResourceLocation, UnbakedModel> function, List<ItemOverride> list) {
        this.overrideModels = list.stream().map(itemOverride -> {
            UnbakedModel unbakedModel = (UnbakedModel)function.apply(itemOverride.getModel());
            if (Objects.equals(unbakedModel, blockModel)) {
                return null;
            }
            return modelBakery.bake(itemOverride.getModel(), BlockModelRotation.X0_Y0);
        }).collect(Collectors.toList());
        Collections.reverse(this.overrideModels);
        for (int i = list.size() - 1; i >= 0; --i) {
            this.overrides.add(list.get(i));
        }
    }

    @Nullable
    public BakedModel resolve(BakedModel bakedModel, ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
        if (!this.overrides.isEmpty()) {
            for (int i = 0; i < this.overrides.size(); ++i) {
                ItemOverride itemOverride = this.overrides.get(i);
                if (!itemOverride.test(itemStack, clientLevel, livingEntity)) continue;
                BakedModel bakedModel2 = this.overrideModels.get(i);
                if (bakedModel2 == null) {
                    return bakedModel;
                }
                return bakedModel2;
            }
        }
        return bakedModel;
    }
}

