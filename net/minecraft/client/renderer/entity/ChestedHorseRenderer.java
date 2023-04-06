/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ChestedHorseModel;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Mule;

public class ChestedHorseRenderer<T extends AbstractChestedHorse>
extends AbstractHorseRenderer<T, ChestedHorseModel<T>> {
    private static final Map<EntityType<?>, ResourceLocation> MAP = Maps.newHashMap((Map)ImmutableMap.of(EntityType.DONKEY, (Object)new ResourceLocation("textures/entity/horse/donkey.png"), EntityType.MULE, (Object)new ResourceLocation("textures/entity/horse/mule.png")));

    public ChestedHorseRenderer(EntityRenderDispatcher entityRenderDispatcher, float f) {
        super(entityRenderDispatcher, new ChestedHorseModel(0.0f), f);
    }

    @Override
    public ResourceLocation getTextureLocation(T t) {
        return MAP.get(((Entity)t).getType());
    }
}

