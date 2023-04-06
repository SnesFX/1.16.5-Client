/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.client.renderer.entity.layers.HorseMarkingLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;

public final class HorseRenderer
extends AbstractHorseRenderer<Horse, HorseModel<Horse>> {
    private static final Map<Variant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(Variant.class), enumMap -> {
        enumMap.put(Variant.WHITE, new ResourceLocation("textures/entity/horse/horse_white.png"));
        enumMap.put(Variant.CREAMY, new ResourceLocation("textures/entity/horse/horse_creamy.png"));
        enumMap.put(Variant.CHESTNUT, new ResourceLocation("textures/entity/horse/horse_chestnut.png"));
        enumMap.put(Variant.BROWN, new ResourceLocation("textures/entity/horse/horse_brown.png"));
        enumMap.put(Variant.BLACK, new ResourceLocation("textures/entity/horse/horse_black.png"));
        enumMap.put(Variant.GRAY, new ResourceLocation("textures/entity/horse/horse_gray.png"));
        enumMap.put(Variant.DARKBROWN, new ResourceLocation("textures/entity/horse/horse_darkbrown.png"));
    });

    public HorseRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new HorseModel(0.0f), 1.1f);
        this.addLayer(new HorseMarkingLayer(this));
        this.addLayer(new HorseArmorLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Horse horse) {
        return LOCATION_BY_VARIANT.get((Object)horse.getVariant());
    }
}

