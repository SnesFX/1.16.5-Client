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
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;

public class UndeadHorseRenderer
extends AbstractHorseRenderer<AbstractHorse, HorseModel<AbstractHorse>> {
    private static final Map<EntityType<?>, ResourceLocation> MAP = Maps.newHashMap((Map)ImmutableMap.of(EntityType.ZOMBIE_HORSE, (Object)new ResourceLocation("textures/entity/horse/horse_zombie.png"), EntityType.SKELETON_HORSE, (Object)new ResourceLocation("textures/entity/horse/horse_skeleton.png")));

    public UndeadHorseRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new HorseModel(0.0f), 1.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractHorse abstractHorse) {
        return MAP.get(abstractHorse.getType());
    }
}

