/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieRenderer
extends AbstractZombieRenderer<Zombie, ZombieModel<Zombie>> {
    public ZombieRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ZombieModel(0.0f, false), new ZombieModel(0.5f, true), new ZombieModel(1.0f, true));
    }
}

