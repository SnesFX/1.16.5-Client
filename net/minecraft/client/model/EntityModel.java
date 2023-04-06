/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

import java.util.function.Function;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class EntityModel<T extends Entity>
extends Model {
    public float attackTime;
    public boolean riding;
    public boolean young = true;

    protected EntityModel() {
        this(RenderType::entityCutoutNoCull);
    }

    protected EntityModel(Function<ResourceLocation, RenderType> function) {
        super(function);
    }

    public abstract void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6);

    public void prepareMobModel(T t, float f, float f2, float f3) {
    }

    public void copyPropertiesTo(EntityModel<T> entityModel) {
        entityModel.attackTime = this.attackTime;
        entityModel.riding = this.riding;
        entityModel.young = this.young;
    }
}

